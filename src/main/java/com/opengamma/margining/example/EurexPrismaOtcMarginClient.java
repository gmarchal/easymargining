/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 * <p/>
 * Please see distribution for license.
 */
package com.opengamma.margining.example;


import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.LocalDate;

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
import com.opengamma.margining.eurex.prisma.replication.data.EurexMarketDataLoadRequest;
import com.opengamma.margining.eurex.prisma.replication.data.EurexOtcMarketDataLoadRequest;
import com.opengamma.margining.eurex.prisma.replication.request.EurexPrismaReplicationRequest;
import com.opengamma.margining.eurex.prisma.replication.request.EurexPrismaReplicationRequests;
import com.opengamma.sesame.trade.TradeWrapper;
import com.opengamma.util.result.Result;

/**
 * Basic integration example for Eurex Prisma calculations on OTC trades.
 */
public class EurexPrismaOtcMarginClient {

  private static final Logger s_logger = LoggerFactory.getLogger(EurexPrismaOtcMarginClient.class);

  private static final LocalDate s_valuationDate = LocalDate.of(2015, 6, 3);

  public static void main(String[] args) throws IOException {

    // Initialize environment with data
    MarginEnvironment environment = MarginEnvironmentFactory.buildBasicEnvironment(new EurexPrismaReplication());

    // Use file resolver utility to discover data from standard Eurex directory structure
    MarketDataFileResolver fileResolver = new MarketDataFileResolver("marketData", s_valuationDate);

    // Create fixings and holidays load request, and load
    MarketDataLoaders.loadGeneralData(environment.getMarginData(), fileResolver);

    // Create OTC data load request, pointing to classpath, and load
    EurexOtcMarketDataLoadRequest otcDataLoadRequest = MarketDataLoaders.otcRequest(fileResolver);
    EurexMarketDataLoadRequest loadRequest = EurexMarketDataLoadRequest.otcMarketDataRequest(s_valuationDate, otcDataLoadRequest);
    environment.getMarginData().loadData(loadRequest);

    // Obtain portfolio, loaded from a trade file on the classpath
    URL tradeFile = FileResources.byPath("trade/swapTrade.csv");
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

    // Print results
    String imResultTable = PortfolioMeasureResultFormatter.formatter()
        .format(imResults.getPortfolioResults());
    System.out.println("Portfolio results:\n" + imResultTable);

    System.out.println("IM result: " + imResults.getPortfolioResults().getValues().get("Total", EurexPrismaReplicationRequests.portfolioMeasures().im()));

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
