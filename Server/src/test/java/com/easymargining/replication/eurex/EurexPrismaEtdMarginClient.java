/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.easymargining.replication.eurex;

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
import com.opengamma.margining.eurex.prisma.replication.request.EurexPrismaReplicationRequest;
import com.opengamma.margining.eurex.prisma.replication.request.EurexPrismaReplicationRequests;
import com.opengamma.sesame.trade.TradeWrapper;
import com.opengamma.util.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.LocalDate;

import java.io.IOException;
import java.net.URL;

/**
 * Basic integration example for Eurex Prisma calculations on an ETD portfolio.
 */
public class EurexPrismaEtdMarginClient {

  private static final Logger s_logger = LoggerFactory.getLogger(EurexPrismaEtdMarginClient.class);
  
  private static final LocalDate s_valuationDate = LocalDate.of(2015, 11, 26);

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
    URL tradeFile = FileResources.byPath("trade/etdTrades.csv");
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
