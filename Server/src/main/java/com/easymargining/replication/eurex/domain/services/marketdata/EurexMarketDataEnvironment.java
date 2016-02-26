package com.easymargining.replication.eurex.domain.services.marketdata;

import com.opengamma.margining.core.MarginEnvironment;
import com.opengamma.margining.core.MarginEnvironmentFactory;
import com.opengamma.margining.eurex.prisma.data.MarketDataFileResolver;
import com.opengamma.margining.eurex.prisma.loader.MarketDataLoaders;
import com.opengamma.margining.eurex.prisma.replication.EurexPrismaReplication;
import com.opengamma.margining.eurex.prisma.replication.data.EurexEtdMarketDataLoadRequest;
import com.opengamma.margining.eurex.prisma.replication.data.EurexMarketDataLoadRequest;
import com.opengamma.margining.eurex.prisma.replication.market.parsers.EurexRiskMeasureConfigParser;
import com.opengamma.util.tuple.Triple;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by gmarchal on 22/02/2016.
 */
@Slf4j
public class EurexMarketDataEnvironment {

    private static EurexMarketDataEnvironment INSTANCE = null;

    /* Liquidation Group
        Listed Equity (Index) Derivatives Liquidation Group (PEQ01)
        Listed Fixed Income Liquidation Group (PFI01)
        Asian cooperations KOSPI/TAIFEX Liquidation Group (PAC01)
        Commodity (Index) Derivatives Liquidation Group (PCM01)
        Precious Metal Derivatives Liquidation Group (PPM01)
        Property Futures Liquidation Group (PPR01)
        FX Derivatives Liquidation Group (PFX01)
        GMEX IRS Constant Maturity Futures Liquidation Group (PGE01)
    */
    private Map<String, Set<String>> liquidationGroupSplit = null;

    private MarginEnvironment marginEnvironment = null;

    private List<URL> theoreticalPricesAndInstrumentConfiguration = null;

    private LocalDate valuationDate = null;

    private EurexMarketDataEnvironment() {
    }

    /** Holder */
    private static class EurexMarketDataEnvironmentHolder
    {
        private final static EurexMarketDataEnvironment instance =
                new EurexMarketDataEnvironment();
    }

    public static EurexMarketDataEnvironment getInstance()
    {
        return EurexMarketDataEnvironmentHolder.instance;
    }

    // Initialize Eurex MarketData Environment
    public static void init(String marketDataDirectory, LocalDate valuationDate) {
        log.info("Initialize Eurex Market Data Environment for valuation date : " + valuationDate.toString());
        EurexMarketDataEnvironment environment =
                EurexMarketDataEnvironment.getInstance() ;

        // Convert LocalDate
        org.threeten.bp.LocalDate s_valuationDate = org.threeten.bp.LocalDate.parse(valuationDate.toString());

        // Initialize environment with data
        MarginEnvironment marginEnvironment = MarginEnvironmentFactory.buildBasicEnvironment(new EurexPrismaReplication());

        // Use file resolver utility to discover data from standard Eurex directory structure
        String directoryFilePattern = new String("file:").concat(marketDataDirectory);
        MarketDataFileResolver fileResolver = new MarketDataFileResolver(directoryFilePattern, s_valuationDate);

        // Create ETD data load request, pointing to classpath, and load
        EurexEtdMarketDataLoadRequest etdDataLoadRequest = MarketDataLoaders.etdRequest(fileResolver);

        EurexMarketDataLoadRequest loadRequest = EurexMarketDataLoadRequest.etdMarketDataRequest(s_valuationDate, etdDataLoadRequest);
        marginEnvironment.getMarginData().loadData(loadRequest);

        environment.setMarginEnvironment(marginEnvironment);
        environment.setTheoreticalPricesAndInstrumentConfiguration(etdDataLoadRequest.getTheoreticalPricesAndInstrumentConfiguration());
        environment.setValuationDate(valuationDate);

        // Load Eurex Risk Measure
        Set<Triple<String, String, String>> liquidationGroupDef = null;
        Map<String, Set<String>> liquidationGroupSplit = new HashMap<>();
        try {
            liquidationGroupDef = (new EurexRiskMeasureConfigParser().parse(etdDataLoadRequest.getRiskMeasureConfig())).keySet();
            Iterator<Triple<String, String, String>> liquidityGroupDefIter = liquidationGroupDef.iterator();
            while (liquidityGroupDefIter.hasNext()) {
                Triple<String, String, String> stringStringStringTriple = liquidityGroupDefIter.next();
                Set<String> currentLiquidationGroupSplit = liquidationGroupSplit.get(stringStringStringTriple.getFirst());
                if (currentLiquidationGroupSplit == null) {
                    currentLiquidationGroupSplit = new ConcurrentSkipListSet<>();
                    currentLiquidationGroupSplit.add(stringStringStringTriple.getSecond());
                    liquidationGroupSplit.put(stringStringStringTriple.getFirst(), currentLiquidationGroupSplit);
                }
                currentLiquidationGroupSplit.add(stringStringStringTriple.getSecond());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        environment.setLiquidationGroupSplit(liquidationGroupSplit);

        log.info("Eurex Market Data Environment for valuation date : " + s_valuationDate.toString() + " is initialized ");
    }

    public MarginEnvironment getMarginEnvironment() {
        return marginEnvironment;
    }

    public void setMarginEnvironment(MarginEnvironment marginEnvironment) {
        this.marginEnvironment = marginEnvironment;
    }

    public List<URL> getTheoreticalPricesAndInstrumentConfiguration() {
        return theoreticalPricesAndInstrumentConfiguration;
    }

    public void setTheoreticalPricesAndInstrumentConfiguration(List<URL> theoreticalPricesAndInstrumentConfiguration) {
        this.theoreticalPricesAndInstrumentConfiguration = theoreticalPricesAndInstrumentConfiguration;
    }

    public LocalDate getValuationDate() {
        return valuationDate;
    }

    public void setValuationDate(LocalDate valuationDate) {
        this.valuationDate = valuationDate;
    }

    // Map<Liquidation Group Name, <Liquidation Group Name Split>>
    public Map<String, Set<String>> getLiquidationGroupSplit() {
        return liquidationGroupSplit;
    }

    public void setLiquidationGroupSplit(Map<String, Set<String>> liquidationGroupSplit) {
        this.liquidationGroupSplit = liquidationGroupSplit;
    }
}
