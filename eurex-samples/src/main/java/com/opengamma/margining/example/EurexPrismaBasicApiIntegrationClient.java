/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 * <p/>
 * Please see distribution for license.
 */
package com.opengamma.margining.example;


import static com.opengamma.financial.convention.frequency.SimpleFrequency.SEMI_ANNUAL;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.LocalDate;
import org.threeten.bp.OffsetTime;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.opengamma.core.id.ExternalSchemes;
import com.opengamma.core.position.Counterparty;
import com.opengamma.core.position.impl.SimpleCounterparty;
import com.opengamma.core.position.impl.SimpleTrade;
import com.opengamma.core.security.impl.SimpleSecurityLink;
import com.opengamma.financial.convention.FloatingIndex;
import com.opengamma.financial.convention.businessday.BusinessDayConventions;
import com.opengamma.financial.convention.daycount.DayCounts;
import com.opengamma.financial.convention.frequency.SimpleFrequency;
import com.opengamma.financial.security.fra.ForwardRateAgreementSecurity;
import com.opengamma.id.ExternalId;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.id.UniqueId;
import com.opengamma.margining.core.MarginEnvironment;
import com.opengamma.margining.core.MarginEnvironmentFactory;
import com.opengamma.margining.core.data.CoreLoadRequests;
import com.opengamma.margining.core.data.MarginData;
import com.opengamma.margining.core.data.fixing.MarginFixingsLoadRequest;
import com.opengamma.margining.core.data.holidays.MarginHolidaysLoadRequest;
import com.opengamma.margining.core.request.MarginCalculator;
import com.opengamma.margining.core.request.PortfolioMeasure;
import com.opengamma.margining.core.request.TradeMeasure;
import com.opengamma.margining.core.result.MarginResults;
import com.opengamma.margining.core.trade.ClearedEtdTrade;
import com.opengamma.margining.core.trade.ImmutableMarginPortfolio;
import com.opengamma.margining.core.trade.MarginPortfolio;
import com.opengamma.margining.core.util.CheckResults;
import com.opengamma.margining.core.util.PortfolioMeasureResultFormatter;
import com.opengamma.margining.core.util.TradeMeasureResultFormatter;
import com.opengamma.margining.eurex.prisma.data.MarketDataFileResolver;
import com.opengamma.margining.eurex.prisma.loader.MarketDataLoaders;
import com.opengamma.margining.eurex.prisma.replication.EurexPrismaReplication;
import com.opengamma.margining.eurex.prisma.replication.cross.EurexCrossMarginDataLoadRequest;
import com.opengamma.margining.eurex.prisma.replication.data.EurexEtdMarketDataLoadRequest;
import com.opengamma.margining.eurex.prisma.replication.data.EurexMarketDataLoadRequest;
import com.opengamma.margining.eurex.prisma.replication.data.EurexOtcMarketDataLoadRequest;
import com.opengamma.margining.eurex.prisma.replication.market.EurexIdUtils;
import com.opengamma.margining.eurex.prisma.replication.request.EurexPrismaReplicationRequest;
import com.opengamma.margining.eurex.prisma.replication.request.EurexPrismaReplicationRequests;
import com.opengamma.margining.financial.security.ClearedEtdSecurity;
import com.opengamma.sesame.trade.ForwardRateAgreementTrade;
import com.opengamma.sesame.trade.TradeWrapper;
import com.opengamma.timeseries.date.localdate.ImmutableLocalDateDoubleTimeSeries;
import com.opengamma.timeseries.date.localdate.LocalDateDoubleTimeSeries;
import com.opengamma.util.money.Currency;
import com.opengamma.util.result.Result;

/**
 * Example illustrating using direct object creation (rather than loading from files) for essential business
 * objects such as:
 * fixings
 * holidays
 * trades
 */
public class EurexPrismaBasicApiIntegrationClient {

  private static final Logger s_logger = LoggerFactory.getLogger(EurexPrismaBasicApiIntegrationClient.class);

  private static final LocalDate s_valuationDate = LocalDate.of(2015, 6, 3);

  public static void main(String[] args) {

    // Initialize environment
    MarginEnvironment environment = MarginEnvironmentFactory.buildBasicEnvironment(new EurexPrismaReplication());
    MarginData marginData = environment.getMarginData();

    // build fixings
    Map<String, LocalDateDoubleTimeSeries> fixings = buildFixings();
    MarginFixingsLoadRequest fixingsLoadRequest = CoreLoadRequests.fixings(fixings);
    marginData.loadData(fixingsLoadRequest);

    // build holidays
    Multimap<String, LocalDate> holidays = buildHolidays();
    MarginHolidaysLoadRequest holidaysLoadRequest = CoreLoadRequests.holidays(holidays);
    marginData.loadData(holidaysLoadRequest);

    // build portfolio
    MarginPortfolio portfolio = buildPortfolio();

    // Use file resolver utility to discover data from standard Eurex directory structure
    MarketDataFileResolver fileResolver = new MarketDataFileResolver("marketData", s_valuationDate);

    // Create OTC data load request and ETD data load request, pointing to classpath, and load
    EurexOtcMarketDataLoadRequest otcDataLoadRequest = MarketDataLoaders.otcRequest(fileResolver);
    EurexEtdMarketDataLoadRequest etdDataLoadRequest = MarketDataLoaders.etdRequest(fileResolver);
    EurexCrossMarginDataLoadRequest crossDataLoadRequest = MarketDataLoaders.crossRequest(fileResolver);
    EurexMarketDataLoadRequest loadRequest = EurexMarketDataLoadRequest.of(s_valuationDate,
        otcDataLoadRequest, etdDataLoadRequest, crossDataLoadRequest);
    environment.getMarginData().loadData(loadRequest);

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

  private static Map<String, LocalDateDoubleTimeSeries> buildFixings() {
    return ImmutableMap.of(
        "CHF-LIBOR-BBA-1Y",
        ImmutableLocalDateDoubleTimeSeries.builder()
            .put(LocalDate.of(2015, 6, 1), -0.005863)
            .build(),
        "EUR-EURIBOR-Reuters-6M",
        ImmutableLocalDateDoubleTimeSeries.builder()
            .put(LocalDate.of(2015, 6, 1), -0.005863)
            .build());
  }

  private static Multimap<String, LocalDate> buildHolidays() {
    return ImmutableMultimap.of(
        "EUTA",
        LocalDate.of(2020, 1, 1)
    );
  }

  private static MarginPortfolio buildPortfolio() {
    TradeWrapper<?> otc = buildOtcTrade();
    TradeWrapper<?> etd = buildEtdTrade();
    List<TradeWrapper<?>> trades = ImmutableList.<TradeWrapper<?>>of(otc, etd);
    return ImmutableMarginPortfolio.ofTrades(trades);
  }

  private static TradeWrapper<?> buildOtcTrade() {
    ExternalId tradeId = ExternalId.of(EurexIdUtils.EUREX_EXTERNAL_SCHEME, "267177");
    ExternalId securityId = ExternalId.of(EurexIdUtils.EUREX_EXTERNAL_SCHEME, "example fra");

    ForwardRateAgreementSecurity fra = new ForwardRateAgreementSecurity(
        Currency.EUR,
        FloatingIndex.EUR_EURIBOR_REUTERS.toFrequencySpecificExternalId(SimpleFrequency.SEMI_ANNUAL),
        SimpleFrequency.SEMI_ANNUAL,
        LocalDate.of(2015, 6, 15),
        LocalDate.of(2015, 12, 15),
        Double.parseDouble("0.65") / 100d,
        105000000d,
        DayCounts.ACT_360,
        BusinessDayConventions.FOLLOWING,
        ImmutableSet.of(ExternalId.of(ExternalSchemes.ISDA_HOLIDAY, "EUTA")),
        ImmutableSet.of(ExternalId.of(ExternalSchemes.ISDA_HOLIDAY, "EUTA")),
        Integer.valueOf(-2));
    fra.setExternalIdBundle(ExternalIdBundle.of(securityId));
    SimpleTrade trade = new SimpleTrade(SimpleSecurityLink.of(fra), BigDecimal.ONE,
        new SimpleCounterparty(ExternalId.of("Cpty", "Empty")), LocalDate.MIN, OffsetTime.MIN);
    trade.setUniqueId(UniqueId.of(UniqueId.EXTERNAL_SCHEME.getName(), tradeId.getValue()));
    return new ForwardRateAgreementTrade(trade);
  }

  private static TradeWrapper<?> buildEtdTrade() {
    ExternalId tradeId = ExternalId.of(EurexIdUtils.EUREX_EXTERNAL_SCHEME, "1525562");
    ExternalId securityId = ExternalId.of(EurexIdUtils.EUREX_EXTERNAL_SCHEME, "ORDX2015070P1200");
    ClearedEtdSecurity security = ClearedEtdSecurity.of(securityId, Currency.EUR, 10, true);
    security.setExternalIdBundle(ExternalIdBundle.of(securityId));

    double quantity = 25; // long 25

    SimpleTrade trade = new SimpleTrade(
        security,
        BigDecimal.valueOf(quantity),
        new SimpleCounterparty(ExternalId.of(Counterparty.DEFAULT_SCHEME, "CPARTY")),
        LocalDate.now(),
        OffsetTime.now());
    trade.setUniqueId(UniqueId.of(UniqueId.EXTERNAL_SCHEME.getName(), tradeId.getValue()));

    return new ClearedEtdTrade(trade);
  }

}
