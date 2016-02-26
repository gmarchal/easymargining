package com.easymargining.replication.eurex.domain.services.eurex;

import com.easymargining.replication.eurex.domain.model.Trade;
import com.easymargining.replication.eurex.domain.model.results.*;
import com.easymargining.replication.eurex.domain.repository.ITradeRepository;
import com.easymargining.replication.eurex.domain.services.marketdata.EurexMarketDataEnvironment;
import com.google.common.collect.Table;
import com.opengamma.margining.core.MarginEnvironment;
import com.opengamma.margining.core.request.MarginCalculator;
import com.opengamma.margining.core.request.PortfolioMeasure;
import com.opengamma.margining.core.request.TradeMeasure;
import com.opengamma.margining.core.result.MarginResults;
import com.opengamma.margining.core.trade.MarginPortfolio;
import com.opengamma.margining.core.util.CheckResults;
import com.opengamma.margining.core.util.OgmLinkResolver;
import com.opengamma.margining.core.util.PortfolioMeasureResultFormatter;
import com.opengamma.margining.core.util.TradeMeasureResultFormatter;
import com.opengamma.margining.eurex.prisma.replication.request.EurexPrismaReplicationRequest;
import com.opengamma.margining.eurex.prisma.replication.request.EurexPrismaReplicationRequests;
import com.opengamma.sesame.trade.TradeWrapper;
import com.opengamma.util.money.CurrencyAmount;
import com.opengamma.util.money.MultipleCurrencyAmount;
import com.opengamma.util.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Gilles Marchal on 21/01/2016.
 */
@Slf4j
@Service
public class EurexPrimaMarginService {

    private static final LocalDate s_valuationDate = LocalDate.of(2015, 6, 3);

    @Autowired
    private ITradeRepository tradeRepository;

    public MarginResult computeEtdMargin(String portfolioId) {

        // Load trade from portfolio
        log.info ("Load trades from portfolioid = " + portfolioId);
        List<Trade> trades = tradeRepository.findByPortfolioId(portfolioId);
        log.info ("Number of trades loaded  : " + trades.size());

        /*
        // Initialize environment with data
        MarginEnvironment environment = MarginEnvironmentFactory.buildBasicEnvironment(new EurexPrismaReplication());
        // Use file resolver utility to discover data from standard Eurex directory structure
        MarketDataFileResolver fileResolver = new MarketDataFileResolver("marketData", s_valuationDate);
        // Create ETD data load request, pointing to classpath, and load
        EurexEtdMarketDataLoadRequest etdDataLoadRequest = MarketDataLoaders.etdRequest(fileResolver);
        EurexMarketDataLoadRequest loadRequest = EurexMarketDataLoadRequest.etdMarketDataRequest(s_valuationDate, etdDataLoadRequest);
        environment.getMarginData().loadData(loadRequest);
        */

        // Load MarketData
        MarginEnvironment environment = EurexMarketDataEnvironment.getInstance().getMarginEnvironment();


        // Obtain portfolio, loaded from a trade file on the classpath
        OgmLinkResolver linkResolver = environment.getInjector().getInstance(OgmLinkResolver.class);
        EurexTradeTransformer transformer = new EurexTradeTransformer(linkResolver);
        MarginPortfolio portfolio = transformer.buildEurexPortfolioFromTrade(trades, s_valuationDate);

        log.info("Margin portfolio = " + portfolio);

        // Build PV calculation request
        EurexPrismaReplicationRequest pvRequest = getPvCalculationRequest();

        // Build IM calculation request
        EurexPrismaReplicationRequest imRequest = getImCalculationRequest();

        // Grab calculator
        MarginCalculator calculator = environment.getMarginCalculator();

        // Run PV request
        log.info("Running PV request");
        MarginResults pvResults = calculator.calculate(portfolio, pvRequest);

        // Print results
        Table<TradeWrapper<?>, TradeMeasure, Result<?>> tradePvResults = pvResults.getTradeResults().getResults();
        String stringPvTable = TradeMeasureResultFormatter.formatter()
                .truncateAfter(200)
                .format(tradePvResults);

        StringBuilder outResult = new StringBuilder();
        outResult.append("PV results:\n").append(stringPvTable);

        // Run IM request
        log.info("Running IM request");
        MarginResults imResults = calculator.calculate(portfolio, imRequest);


        // Print results
        String imResultTable = PortfolioMeasureResultFormatter.formatter()
                .format(imResults.getPortfolioResults());
        outResult.append("Portfolio results:\n").append(imResultTable);

        outResult.append("IM result: ").append(imResults.getPortfolioResults().getValues()
                .get("Total", EurexPrismaReplicationRequests.portfolioMeasures().im()));

        outResult.append("VM result: ").append(imResults.getPortfolioResults().getValues()
                .get("Total", EurexPrismaReplicationRequests.portfolioMeasures().vm()));

        outResult.append("Historical VAR result: ").append(imResults.getPortfolioResults().getValues()
                .get("Total", EurexPrismaReplicationRequests.portfolioMeasures().varEquity()));

        CheckResults.checkMarginResults(imResults);

        log.info("Result :  " + outResult.toString());


        // Build Output Result.
        MarginResult result = new MarginResult();
        result.setPortfolioMarginResults(new ArrayList());
        List<LiquidationGroupMarginResult> liquidationGroupMarginResults = null;
        CurrencyAmount[] currencyAmounts =
                imResults.getPortfolioResults().getValues().get(
                        "Total",
                        EurexPrismaReplicationRequests.portfolioMeasures().im()).getValue().getCurrencyAmounts();


        // Load Liquidation Groups & Liquidation Group Split
        Map<String, Set<String>> liquidationGroupDefinitions = EurexMarketDataEnvironment.getInstance().getLiquidationGroupSplit();
        List<String> liquidationGroups = new ArrayList(liquidationGroupDefinitions.keySet());


        for (int i=0 ; i< currencyAmounts.length; i++) {
            currencyAmounts[i].getCurrency();
            currencyAmounts[i].getAmount();

            // For Each Liquidation Group
            for (int j=0 ; j< liquidationGroups.size(); j++) {

                String liquidationGroupName = liquidationGroups.get(j);

                liquidationGroupMarginResults = new ArrayList();
                result.getPortfolioMarginResults().add(
                        new PortfolioMarginResult(liquidationGroupName,
                                currencyAmounts[i].getCurrency().getCode(),
                                currencyAmounts[i].getAmount(),
                                liquidationGroupMarginResults));

                // Identify IM for Liquidation Group
                MultipleCurrencyAmount imgroup = null;

                Result<MultipleCurrencyAmount> imgroupMarginResult =
                        imResults.getPortfolioResults().getValues()
                                .get("Total",
                                        EurexPrismaReplicationRequests.portfolioMeasures()
                                                .groupIm(liquidationGroupName));

                if (imgroupMarginResult!= null) {
                    imgroup = imgroupMarginResult.getValue();
                }

                if (imgroup != null) {
                    List<LiquidationGroupSplitMarginResult> liquidationGroupSplitMarginResults = new ArrayList();
                    double value = imgroup.getAmount(currencyAmounts[i].getCurrency());
                    liquidationGroupMarginResults.add(new LiquidationGroupMarginResult(liquidationGroupName, value, liquidationGroupSplitMarginResults));

                    // For all Liquidation Group Split
                    List<String> liquidationGroupSplits = new ArrayList(liquidationGroupDefinitions.get(liquidationGroupName));
                    for (int k=0 ; k< liquidationGroupSplits.size(); k++) {
                        String liquidationGroupSplitName = liquidationGroupSplits.get(k);

                        MultipleCurrencyAmount imLGS = null;
                        MultipleCurrencyAmount liquidityAddon = null;
                        MultipleCurrencyAmount liquidityAddonEtd = null;
                        MultipleCurrencyAmount liquidityAddonOtc = null;
                        MultipleCurrencyAmount longOptionCredit = null;
                        MultipleCurrencyAmount var = null;

                        //Identify Market Risk IM for liquidation group
                        Result<MultipleCurrencyAmount> imLGSMarginResult = imResults.getPortfolioResults().getValues()
                                .get("Total",
                                        EurexPrismaReplicationRequests.portfolioMeasures()
                                                .groupIm(liquidationGroupSplitName));
                        if (imLGSMarginResult!= null) {
                            imLGS = imLGSMarginResult.getValue();
                        }

                        Result<MultipleCurrencyAmount> liquidityAddonMarginResult =
                                imResults.getPortfolioResults().getValues()
                                        .get("Total",
                                                EurexPrismaReplicationRequests.portfolioMeasures()
                                                        .liquidityAddon(liquidationGroupSplitName));

                        if (liquidityAddonMarginResult!= null) {
                            liquidityAddon = liquidityAddonMarginResult.getValue();
                        }

                        Result<MultipleCurrencyAmount> liquidityAddonEtdMarginResult =
                                imResults.getPortfolioResults().getValues()
                                        .get("Total",
                                                EurexPrismaReplicationRequests.portfolioMeasures()
                                                        .liquidityAddonEtd(liquidationGroupSplitName));
                        if (liquidityAddonEtdMarginResult!= null) {
                            liquidityAddonEtd = liquidityAddonEtdMarginResult.getValue();
                        }

                        Result<MultipleCurrencyAmount> liquidityAddonOtcMarginResult =
                                imResults.getPortfolioResults().getValues()
                                        .get("Total",
                                                EurexPrismaReplicationRequests.portfolioMeasures()
                                                        .liquidityAddonOtc(liquidationGroupSplitName));

                        if (liquidityAddonOtcMarginResult!= null) {
                            liquidityAddonOtc = liquidityAddonOtcMarginResult.getValue();
                        }

                        Result<MultipleCurrencyAmount> longOptionCreditMarginResult =
                                imResults.getPortfolioResults().getValues()
                                        .get("Total",
                                                EurexPrismaReplicationRequests.portfolioMeasures()
                                                        .longOptionCredit(liquidationGroupSplitName));

                        if (longOptionCreditMarginResult!= null) {
                            longOptionCredit = longOptionCreditMarginResult.getValue();
                        }

                        Result<MultipleCurrencyAmount> varMarginResult =
                                imResults.getPortfolioResults().getValues()
                                        .get("Total",
                                                EurexPrismaReplicationRequests.portfolioMeasures()
                                                        .var(liquidationGroupSplitName));

                        if (varMarginResult!= null) {
                            var = varMarginResult.getValue();
                        }

                        LiquidityRiskAdjustmentResult liquidityRiskAdjustmentResult = new LiquidityRiskAdjustmentResult();
                        MarketRiskIMResult marketRiskIMResult = new MarketRiskIMResult();

                        if (liquidityAddon != null) {
                            liquidityRiskAdjustmentResult.setTotalLiquidityRiskAdjustmentAddOn(liquidityAddon.getAmount(currencyAmounts[i].getCurrency()));
                        }
                        if (liquidityAddonEtd != null) {
                            liquidityRiskAdjustmentResult.setEtdliquidityRiskAdjustmentAddOnPart(liquidityAddonEtd.getAmount(currencyAmounts[i].getCurrency()));
                        }
                        if (liquidityAddonOtc != null) {
                            liquidityRiskAdjustmentResult.setOtcliquidityRiskAdjustmentAddOnPart(liquidityAddonOtc.getAmount(currencyAmounts[i].getCurrency()));
                        }
                        if (longOptionCredit != null) {
                            liquidityRiskAdjustmentResult.setLongOptionCreditAddOn(longOptionCredit.getAmount(currencyAmounts[i].getCurrency()));
                        }

                        if (imLGS != null) {
                            Double valueImLGS = imLGS.getAmount(currencyAmounts[i].getCurrency());

                            liquidationGroupSplitMarginResults.add(new LiquidationGroupSplitMarginResult(liquidationGroupSplitName, valueImLGS, liquidityRiskAdjustmentResult, marketRiskIMResult ));
                        }

                    }
                }
            }
        }

        return result;
    }

    /**
     * Gets a trade PV calculation request.
     *
     * @return the calculation request
     */
    private static EurexPrismaReplicationRequest getPvCalculationRequest() {

        TradeMeasure pv = EurexPrismaReplicationRequests.tradeMeasures()
                .pv()
                .build();

        return EurexPrismaReplicationRequests.request(s_valuationDate)
                .tradeMeasures(pv)
                .build();
    }

    /**
     * Gets a portfolio IM calculation request.
     *
     * @return the calculation request
     */
    private static EurexPrismaReplicationRequest getImCalculationRequest() {

        PortfolioMeasure im = EurexPrismaReplicationRequests.portfolioMeasures().im();

        return EurexPrismaReplicationRequests.request(s_valuationDate)
                .portfolioMeasures(im)
                .crossMarginingEnabled(true)
                .build();

    }

}
