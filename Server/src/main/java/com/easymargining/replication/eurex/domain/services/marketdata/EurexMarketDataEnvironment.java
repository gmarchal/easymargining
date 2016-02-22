package com.easymargining.replication.eurex.domain.services.marketdata;

import com.opengamma.margining.core.MarginEnvironment;
import com.opengamma.margining.core.MarginEnvironmentFactory;
import com.opengamma.margining.eurex.prisma.data.MarketDataFileResolver;
import com.opengamma.margining.eurex.prisma.loader.MarketDataLoaders;
import com.opengamma.margining.eurex.prisma.replication.EurexPrismaReplication;
import com.opengamma.margining.eurex.prisma.replication.data.EurexEtdMarketDataLoadRequest;
import com.opengamma.margining.eurex.prisma.replication.data.EurexMarketDataLoadRequest;
import com.opengamma.margining.eurex.prisma.replication.data.EurexOtcMarketDataLoadRequest;
import lombok.extern.slf4j.Slf4j;
import org.threeten.bp.LocalDate;

/**
 * Created by gmarchal on 22/02/2016.
 */
@Slf4j
public class EurexMarketDataEnvironment {

    private static EurexMarketDataEnvironment INSTANCE = null;

    private MarginEnvironment marginEnvironment = null;

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
    public static void init(LocalDate s_valuationDate) {
        EurexMarketDataEnvironment environment =
                EurexMarketDataEnvironment.getInstance() ;

        // Initialize environment with data
        MarginEnvironment marginEnvironment = MarginEnvironmentFactory.buildBasicEnvironment(new EurexPrismaReplication());

        // Use file resolver utility to discover data from standard Eurex directory structure
        MarketDataFileResolver fileResolver = new MarketDataFileResolver("marketData", s_valuationDate);

        // Create ETD data load request, pointing to classpath, and load
        EurexEtdMarketDataLoadRequest etdDataLoadRequest = MarketDataLoaders.etdRequest(fileResolver);
        EurexMarketDataLoadRequest loadRequest = EurexMarketDataLoadRequest.etdMarketDataRequest(s_valuationDate, etdDataLoadRequest);
        marginEnvironment.getMarginData().loadData(loadRequest);

        environment.setMarginEnvironment(marginEnvironment);
    }

    public MarginEnvironment getMarginEnvironment() {
        return marginEnvironment;
    }

    public void setMarginEnvironment(MarginEnvironment marginEnvironment) {
        this.marginEnvironment = marginEnvironment;
    }
}
