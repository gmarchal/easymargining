package com.easymargining.replication.ccg.market;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.util.Assert;

import java.net.URL;
import java.time.LocalDate;

/**
 * Created by Gilles Marchal on 08/12/2015.
 */
@Slf4j
public class MarketDataFileResolverTest {

    private static final LocalDate valuationDate = LocalDate.of(2015, 11, 26);
    private static final MarketDataFileResolver marketDataFileResolver;

    static {
        marketDataFileResolver = new MarketDataFileResolver("marketData", valuationDate);
    }

    @Test
    public void testArrayFile() throws Exception {
        Assert.notNull(marketDataFileResolver);
        URL arrayFileURL = marketDataFileResolver.arrayFile();
        Assert.notNull(arrayFileURL);
        log.info("URL RiskArray = " + arrayFileURL);
    }

    @Test
    public void testClassFile() throws Exception {
        Assert.notNull(marketDataFileResolver);
        URL arrayFileURL = marketDataFileResolver.classFile();
        Assert.notNull(arrayFileURL);
        log.info("URL ClassFile = " + arrayFileURL);
    }
}