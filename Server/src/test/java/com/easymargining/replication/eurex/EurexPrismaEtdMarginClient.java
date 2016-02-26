/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.easymargining.replication.eurex;

import com.easymargining.replication.eurex.domain.model.results.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Table;
import com.opengamma.margining.core.MarginEnvironment;
import com.opengamma.margining.core.MarginEnvironmentFactory;
import com.opengamma.margining.core.request.MarginCalculator;
import com.opengamma.margining.core.request.PortfolioMeasure;
import com.opengamma.margining.core.request.TradeMeasure;
import com.opengamma.margining.core.result.MarginResults;
import com.opengamma.margining.core.trade.MarginPortfolio;
import com.opengamma.margining.core.util.CheckResults;
import com.opengamma.margining.core.util.OgmLinkResolver;
import com.opengamma.margining.core.util.PortfolioMeasureResultFormatter;
import com.opengamma.margining.core.util.TradeMeasureResultFormatter;
import com.opengamma.margining.eurex.prisma.data.FileResources;
import com.opengamma.margining.eurex.prisma.data.MarketDataFileResolver;
import com.opengamma.margining.eurex.prisma.loader.MarketDataLoaders;
import com.opengamma.margining.eurex.prisma.loader.PortfolioLoader;
import com.opengamma.margining.eurex.prisma.replication.EurexPrismaReplication;
import com.opengamma.margining.eurex.prisma.replication.data.EurexEtdMarketDataLoadRequest;
import com.opengamma.margining.eurex.prisma.replication.data.EurexMarketDataLoadRequest;
import com.opengamma.margining.eurex.prisma.replication.market.parsers.EurexRiskMeasureConfigParser;
import com.opengamma.margining.eurex.prisma.replication.request.EurexPrismaReplicationRequest;
import com.opengamma.margining.eurex.prisma.replication.request.EurexPrismaReplicationRequests;
import com.opengamma.sesame.trade.TradeWrapper;
import com.opengamma.util.money.CurrencyAmount;
import com.opengamma.util.money.MultipleCurrencyAmount;
import com.opengamma.util.result.Result;
import com.opengamma.util.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.LocalDate;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Basic integration example for Eurex Prisma calculations on an ETD portfolio.
 */
public class EurexPrismaEtdMarginClient {

  private static final Logger s_logger = LoggerFactory.getLogger(EurexPrismaEtdMarginClient.class);
  
  private static final LocalDate s_valuationDate = LocalDate.of(2015, 06, 03);

  public static void main(String[] args) throws IOException {

    // Initialize environment with data
    MarginEnvironment environment = MarginEnvironmentFactory.buildBasicEnvironment(new EurexPrismaReplication());

    // Use file resolver utility to discover data from standard Eurex directory structure
    MarketDataFileResolver fileResolver = new MarketDataFileResolver("eurex/marketData", s_valuationDate);

    // Create ETD data load request, pointing to classpath, and load
    EurexEtdMarketDataLoadRequest etdDataLoadRequest = MarketDataLoaders.etdRequest(fileResolver);
    EurexMarketDataLoadRequest loadRequest = EurexMarketDataLoadRequest.etdMarketDataRequest(s_valuationDate, etdDataLoadRequest);
    environment.getMarginData().loadData(loadRequest);

    // Obtain portfolio, loaded from a trade file on the classpath
    URL tradeFile = FileResources.byPath("eurex/trade/etdTrades.csv");
    OgmLinkResolver linkResolver = environment.getInjector().getInstance(OgmLinkResolver.class);
    MarginPortfolio portfolio = PortfolioLoader.load(ImmutableList.of(tradeFile), linkResolver);

    // Build PV calculation request
    EurexPrismaReplicationRequest pvRequest = getPvCalculationRequest();

    // Build IM calculation request
    EurexPrismaReplicationRequest imRequest = getImCalculationRequest();

    // Grab calculator
    MarginCalculator calculator = environment.getMarginCalculator();

    // Run PV request
    s_logger.info("Running PV request");
    MarginResults pvResults = calculator.calculate(portfolio, pvRequest);

    // Print results
    Table<TradeWrapper<?>, TradeMeasure, Result<?>> tradePvResults = pvResults.getTradeResults().getResults();
    String stringPvTable = TradeMeasureResultFormatter.formatter()
        .truncateAfter(200)
        .format(tradePvResults);
    System.out.println("PV results:\n" + stringPvTable);

    // Run IM request
    s_logger.info("Running IM request");
    MarginResults imResults = calculator.calculate(portfolio, imRequest);


    // Load Eurex Risk Measure
    Set<Triple<String, String, String>> liquidationGroupDef = null;
    Map<String, Set<String>> liquidationGroupDefinitions = new HashMap<>();
    try {
      liquidationGroupDef = (new EurexRiskMeasureConfigParser().parse(etdDataLoadRequest.getRiskMeasureConfig())).keySet();
      Iterator<Triple<String, String, String>> liquidityGroupDefIter = liquidationGroupDef.iterator();
      while (liquidityGroupDefIter.hasNext()) {
        Triple<String, String, String> stringStringStringTriple = liquidityGroupDefIter.next();
        Set<String> currentLiquidationGroupSplit = liquidationGroupDefinitions.get(stringStringStringTriple.getFirst());
        if (currentLiquidationGroupSplit == null) {
          currentLiquidationGroupSplit = new ConcurrentSkipListSet<>();
          currentLiquidationGroupSplit.add(stringStringStringTriple.getSecond());
          liquidationGroupDefinitions.put(stringStringStringTriple.getFirst(), currentLiquidationGroupSplit);
        }
        currentLiquidationGroupSplit.add(stringStringStringTriple.getSecond());
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    //-------------------------------




    // Build Output Result.
    MarginResult result = new MarginResult();
    result.setPortfolioMarginResults(new ArrayList());
    List<LiquidationGroupMarginResult> liquidationGroupMarginResults = null;
    CurrencyAmount[] currencyAmounts =
            imResults.getPortfolioResults().getValues().get(
                    "Total",
                    EurexPrismaReplicationRequests.portfolioMeasures().im()).getValue().getCurrencyAmounts();

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
    // ---



    // Print results
    String imResultTable = PortfolioMeasureResultFormatter.formatter()
        .format(imResults.getPortfolioResults());
    System.out.println("Portfolio results:\n" + imResultTable);

    System.out.println("IM result: " + imResults.getPortfolioResults().getValues()
        .get("Total", EurexPrismaReplicationRequests.portfolioMeasures().im()));
    System.out.println("Historical VAR result: " + imResults.getPortfolioResults().getValues()
        .get("Total", EurexPrismaReplicationRequests.portfolioMeasures().var("PFI01_HP2_T0-99999~FILTERED_HISTORICAL_VAR_2")));

    CheckResults.checkMarginResults(imResults);

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
        .build();
  }
  
}
