package com.easymargining.replication.eurex.domain.services.eurex;

import com.easymargining.replication.eurex.domain.model.MarginResult;
import com.easymargining.replication.eurex.domain.model.Trade;
import com.easymargining.replication.eurex.domain.repository.ITradeRepository;
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
import com.opengamma.margining.eurex.prisma.data.MarketDataFileResolver;
import com.opengamma.margining.eurex.prisma.loader.MarketDataLoaders;
import com.opengamma.margining.eurex.prisma.replication.EurexPrismaReplication;
import com.opengamma.margining.eurex.prisma.replication.data.EurexEtdMarketDataLoadRequest;
import com.opengamma.margining.eurex.prisma.replication.data.EurexMarketDataLoadRequest;
import com.opengamma.margining.eurex.prisma.replication.request.EurexPrismaReplicationRequest;
import com.opengamma.margining.eurex.prisma.replication.request.EurexPrismaReplicationRequests;
import com.opengamma.sesame.trade.TradeWrapper;
import com.opengamma.util.money.Currency;
import com.opengamma.util.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.threeten.bp.LocalDate;

import java.util.List;

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

        StringBuilder outResult = new StringBuilder();

        // Initialize environment with data
        MarginEnvironment environment = MarginEnvironmentFactory.buildBasicEnvironment(new EurexPrismaReplication());

        // Use file resolver utility to discover data from standard Eurex directory structure
        MarketDataFileResolver fileResolver = new MarketDataFileResolver("marketData", s_valuationDate);

        // Create ETD data load request, pointing to classpath, and load
        EurexEtdMarketDataLoadRequest etdDataLoadRequest = MarketDataLoaders.etdRequest(fileResolver);
        EurexMarketDataLoadRequest loadRequest = EurexMarketDataLoadRequest.etdMarketDataRequest(s_valuationDate, etdDataLoadRequest);
        environment.getMarginData().loadData(loadRequest);

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
                .get("Total", EurexPrismaReplicationRequests.portfolioMeasures().var("PFI01_HP2_T0-99999~FILTERED_HISTORICAL_VAR_2")));

        CheckResults.checkMarginResults(imResults);

        log.info("Result :  " + outResult.toString());

        MarginResult result = new MarginResult();

        result.setImResult(imResults.getPortfolioResults().getValues().get("Total", EurexPrismaReplicationRequests.portfolioMeasures().im()).getValue().getAmount(Currency.EUR));
        result.setHistoVarResult(imResults.getPortfolioResults().getValues().get("Total", EurexPrismaReplicationRequests.portfolioMeasures().var("PFI01_HP2_T0-99999~FILTERED_HISTORICAL_VAR_2")).getValue().getAmount(Currency.EUR));

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
