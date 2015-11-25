package com.socgen.finit.easymargin.web;

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
import com.socgen.finit.easymargin.converter.TradeFileHandler;
import com.socgen.finit.easymargin.model.Request;
import com.socgen.finit.easymargin.model.TradeEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.threeten.bp.LocalDate;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static com.socgen.finit.easymargin.web.EurexPrismaController.PATH_API;

@Slf4j
@RestController
@RequestMapping(value = PATH_API, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE} )
public class EurexPrismaController {

    public static final String PATH_API = "/api/PrismaEurex";

    @Autowired
    private TradeFileHandler tradeFileHandler;

    @Autowired
    private SessionFactory sessionFactory;

    @RequestMapping(value = "/ComputeEtdMargin", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> computeEtdMargin(@RequestBody Request request) {
        return new ResponseEntity<>("AddTrade " + request, HttpStatus.OK);
    }






    private static final LocalDate s_valuationDate = LocalDate.of(2015, 6, 3);


    @RequestMapping(value = "/session", method = RequestMethod.PUT)
    @ResponseBody
    public String createSession() {
        SessionInfo session = sessionFactory.createSession();
        if (session != null) {
            return session.getSessionId();
        }
        return null;
    }

    @RequestMapping(value = "/trade/{sessionId}", method = RequestMethod.GET)
    public ResponseEntity<List<TradeEntity>> getTrades(@PathVariable("sessionId") String sessionId) {
        List<TradeEntity> tradeEntityList = null;
        try {
            tradeEntityList = tradeFileHandler.readTradeFile(getEtdTradeUrl(sessionId));
            return new ResponseEntity<>(tradeEntityList, HttpStatus.OK);
        } catch (IOException e) {
            log.error("getTrades error", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/trade", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> addTrade(@RequestBody TradeEntity tradeEntity) {
        return new ResponseEntity<>("AddTrade " + tradeEntity, HttpStatus.OK);
    }

    @RequestMapping(value = "/trade/{sessionId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> postTrades(@PathVariable("sessionId") String sessionId, @RequestBody List<TradeEntity> tradeEntities) {
        try {
            tradeFileHandler.writeTradeFile(tradeEntities, getEtdTradeUrl(sessionId));
        } catch (IOException e) {
            log.error("postTrades error", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    @RequestMapping(value = "/etdMargin/{sessionId}", method = RequestMethod.GET)
    @ResponseBody
    public String etdMargin(@PathVariable("sessionId") String sessionId) {

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
        MarginPortfolio portfolio = PortfolioLoader.load(ImmutableList.of(getEtdTradeUrl(sessionId)), linkResolver);

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
        outResult.append("Historical VAR result: ").append(imResults.getPortfolioResults().getValues()
                .get("Total", EurexPrismaReplicationRequests.portfolioMeasures().var("PFI01_HP2_T0-99999~FILTERED_HISTORICAL_VAR_2")));

        CheckResults.checkMarginResults(imResults);

        return outResult.toString();
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

    private URL getEtdTradeUrl(String sessionId) {
        SessionInfo orCreateSession = sessionFactory.getOrCreateSession(sessionId);
        return orCreateSession != null ? orCreateSession.getEtdTradeUrl() : null;
    }

}