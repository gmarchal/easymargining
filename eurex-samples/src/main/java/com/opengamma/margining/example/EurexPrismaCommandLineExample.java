/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 * <p/>
 * Please see distribution for license.
 */
package com.opengamma.margining.example;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Table;
import com.opengamma.margining.core.MarginEnvironment;
import com.opengamma.margining.core.MarginEnvironmentFactory;
import com.opengamma.margining.core.request.MarginCalculator;
import com.opengamma.margining.core.request.PortfolioMeasure;
import com.opengamma.margining.core.request.TradeMeasure;
import com.opengamma.margining.core.result.MarginResults;
import com.opengamma.margining.core.trade.MarginPortfolio;
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
import com.opengamma.margining.eurex.prisma.replication.data.EurexOtcMarketDataLoadRequest;
import com.opengamma.margining.eurex.prisma.replication.request.EurexPrismaReplicationRequest;
import com.opengamma.margining.eurex.prisma.replication.request.EurexPrismaReplicationRequests;
import com.opengamma.sesame.trade.TradeWrapper;
import com.opengamma.util.money.MultipleCurrencyAmount;
import com.opengamma.util.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.LocalDate;

import java.net.URL;

/**
 * Implements a command-line example for calculating Eurex Prisma initial margin, where all input data is provided
 * through command-line arguments.
 * <p/>
 * This loads a portfolio from any supported format, and a set of market data from the standard directory
 * structure.
 */
public class EurexPrismaCommandLineExample {

  private static final Logger s_logger = LoggerFactory.getLogger(EurexPrismaCommandLineExample.class);

  private static final String VALUATION_DATE_OPTION = "d";
  private static final String MARKET_DATA_DIR_OPTION = "m";
  private static final String PORTFOLIO_FILE_OPTION = "p";

  /**
   * Main entry point.
   *
   * @param args  command-line arguments
   */
  public static void main(String[] args) throws ParseException {
    Options commandLineOptions = getCommandLineOptions();
    PosixParser commandLineParser = new PosixParser();
    CommandLine commandLine = commandLineParser.parse(commandLineOptions, args);

    LocalDate valuationDate = LocalDate.parse(commandLine.getOptionValue(VALUATION_DATE_OPTION));
    String marketDataDirectory = commandLine.getOptionValue(MARKET_DATA_DIR_OPTION);
    String portfolioFile = commandLine.getOptionValue(PORTFOLIO_FILE_OPTION);

    // Initialize environment with data
    MarginEnvironment environment = MarginEnvironmentFactory.buildBasicEnvironment(new EurexPrismaReplication());

    // Use file resolver utility to discover data from standard Eurex directory structure
    MarketDataFileResolver fileResolver = new MarketDataFileResolver("file:" + marketDataDirectory, valuationDate);

    // Create fixings and holidays load request, and load
    MarketDataLoaders.loadGeneralData(environment.getMarginData(), fileResolver);

    // Create data load request and load
    s_logger.info("Loading market data");
    EurexOtcMarketDataLoadRequest otcDataLoadRequest = MarketDataLoaders.otcRequest(fileResolver);
    EurexEtdMarketDataLoadRequest etdDataLoadRequest = MarketDataLoaders.etdRequest(fileResolver);
    EurexMarketDataLoadRequest loadRequest = EurexMarketDataLoadRequest.marketDataRequest(valuationDate, otcDataLoadRequest, etdDataLoadRequest);
    environment.getMarginData().loadData(loadRequest);

    // Load portfolio
    s_logger.info("Loading portfolio");
    URL portfolioFileUrl = FileResources.byPath("file:" + portfolioFile);
    OgmLinkResolver linkResolver = environment.getInjector().getInstance(OgmLinkResolver.class);
    MarginPortfolio portfolio = PortfolioLoader.load(ImmutableList.of(portfolioFileUrl), linkResolver);

    // Build calculation request
    EurexPrismaReplicationRequest calcRequest = getCalculationRequest(valuationDate);

    // Get calculator
    MarginCalculator calculator = environment.getMarginCalculator();

    // Run request
    s_logger.info("Running calculation request");
    MarginResults calcResults = calculator.calculate(portfolio, calcRequest);

    // Print trade-level results
    Table<TradeWrapper<?>, TradeMeasure, Result<?>> tradeResults = calcResults.getTradeResults().getResults();
    String tradeResultsTable = TradeMeasureResultFormatter.formatter()
        .truncateAfter(200)
        .format(tradeResults);
    System.out.println("Trade results:\n" + tradeResultsTable);

    // Print portfolio-level results
    String portfolioResultsTable = PortfolioMeasureResultFormatter.formatter()
        .format(calcResults.getPortfolioResults());
    System.out.println("Portfolio results:\n" + portfolioResultsTable);

    Result<MultipleCurrencyAmount> imResult = calcResults.getPortfolioResults().getValues().get("Total", EurexPrismaReplicationRequests.portfolioMeasures().im());
    if (imResult.isSuccess()) {
      System.out.println("IM result: " + imResult.getValue());
    } else {
      System.err.println("IM calculation failed: " + imResult.getFailureMessage());
    }
  }

  /**
   * Obtains the calculation request. This asks for trade-level PV, and portfolio-level IM.
   *
   * @return the calculation request
   */
  private static EurexPrismaReplicationRequest getCalculationRequest(LocalDate valuationDate) {

    PortfolioMeasure im = EurexPrismaReplicationRequests.portfolioMeasures().im();
    TradeMeasure pv = EurexPrismaReplicationRequests.tradeMeasures().pv().build();

    return EurexPrismaReplicationRequests.request(valuationDate)
        .portfolioMeasures(im)
        .tradeMeasures(pv)
        .build();
  }

  /**
   * Obtains the command-line options.
   *
   * @return the options
   */
  private static Options getCommandLineOptions() {
    Options options = new Options();

    Option valuationDateOption = new Option(VALUATION_DATE_OPTION, true, "Valuation date, yyyy-MM-dd");
    valuationDateOption.setRequired(true);
    options.addOption(valuationDateOption);

    Option marketDataDirOption = new Option(MARKET_DATA_DIR_OPTION, true, "Market data directory");
    marketDataDirOption.setRequired(true);
    options.addOption(marketDataDirOption);

    Option portfolioOption = new Option(PORTFOLIO_FILE_OPTION, true, "Portfolio file");
    portfolioOption.setRequired(true);
    options.addOption(portfolioOption);

    return options;
  }

}
