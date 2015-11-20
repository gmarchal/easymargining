/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.margining.example;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.joda.beans.ser.JodaBeanSer;
import org.springframework.core.io.ClassPathResource;
import org.threeten.bp.LocalDate;

import com.google.common.collect.ImmutableList;
import com.opengamma.financial.analytics.model.fixedincome.SwapLegCashFlows;
import com.opengamma.financial.convention.daycount.DayCounts;
import com.opengamma.margining.core.MarginEnvironment;
import com.opengamma.margining.core.MarginEnvironmentFactory;
import com.opengamma.margining.core.cashflow.CashFlowExtension;
import com.opengamma.margining.core.cashflow.CashFlowFormatter;
import com.opengamma.margining.core.cashflow.CashFlowGenerator;
import com.opengamma.margining.core.trade.MarginPortfolio;
import com.opengamma.margining.core.util.OgmLinkResolver;
import com.opengamma.margining.eurex.prisma.data.FileResources;
import com.opengamma.margining.eurex.prisma.data.MarketDataFileResolver;
import com.opengamma.margining.eurex.prisma.loader.MarketDataLoaders;
import com.opengamma.margining.eurex.prisma.loader.PortfolioLoader;
import com.opengamma.margining.eurex.prisma.replication.EurexPrismaReplication;
import com.opengamma.margining.eurex.prisma.replication.EurexPrismaReplicationDefaults;
import com.opengamma.margining.eurex.prisma.replication.data.EurexMarketDataLoadRequest;
import com.opengamma.margining.eurex.prisma.replication.data.EurexOtcMarketDataLoadRequest;
import com.opengamma.margining.eurex.prisma.replication.request.EurexPrismaReplicationRequests;
import com.opengamma.margining.eurex.prisma.replication.request.EurexPrismaViewConfig;
import com.opengamma.sesame.trade.InterestRateSwapTrade;

/**
 * Example of using the cashflow extension API to extract cashflows on a swap.
 */
public class EurexPrismaCashFlowClient {

  private static final LocalDate s_valuationDate = LocalDate.of(2015, 6, 3);

  public static void main(String[] args) throws IOException {
    new EurexPrismaCashFlowClient().run();
  }

  public void run() throws IOException {

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
    
    CashFlowGenerator cashFlowGenerator = environment.initExtension(new CashFlowExtension(DayCounts.ACT_360));
    
    EurexPrismaViewConfig eurexConfig = environment.getInjector().getInstance(EurexPrismaViewConfig.class);

    // Obtain the first trade from the portfolio
    InterestRateSwapTrade trade = (InterestRateSwapTrade) portfolio.getTrades().get(0);

    List<SwapLegCashFlows> cashFlows =
        cashFlowGenerator.generateSwapCashFlows(trade, 
                                                eurexConfig.createConfig(EurexPrismaReplicationDefaults.IM_MULTICURVE_NAME,
                                                                         EurexPrismaReplicationDefaults.IM_INFLATION_MULTICURVE_NAME),
                                                s_valuationDate,
                                                EurexPrismaReplicationRequests.IM_BASE_SCENARIO);
    
    assertEquals(cashFlows.size(), 2);
    
    System.out.println(CashFlowFormatter.asciiFormatter().formatSwapCashflows(cashFlows));

  }

  /**
   * Loads a trade from an xml encoded joda bean file.
   *
   * @return a list of a single trades
   * @throws IOException if an issue occurs reading a file
   */
  public static InterestRateSwapTrade loadTrade(String fileName) throws IOException {
    URL file = new ClassPathResource(fileName).getURL();
    InterestRateSwapTrade trade = (InterestRateSwapTrade) JodaBeanSer.PRETTY.xmlReader().read(file.openStream());
    return trade;
  }
  
}
