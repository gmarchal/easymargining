package com.easymargining.replication.ccg.calc;

import com.easymargining.replication.ccg.trade.CcgMarsMarginTradeItem;
import com.easymargining.replication.ccg.trade.parsers.CcgMarsTradesLoader;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by Gilles Marchal on 08/12/2015.
 */
public class MarsReplicationCalculatorTest {

    private static final LocalDate valuationDate = LocalDate.of(2015, 11, 26);

    @Test
    public void testComputeFuturesSpreadMargin() throws Exception {

        URL url = new ClassPathResource("ccg/trade/ccgTrades.csv").getURL();
        Assert.notNull(url);

        CcgMarsTradesLoader ccgMarsTradesLoader = new CcgMarsTradesLoader();
        List<CcgMarsMarginTradeItem> tradeItems = ccgMarsTradesLoader.readTradeFile(url);
        Assert.notEmpty(tradeItems);

        CcgMarsReplicationCalculator calculator = new CcgMarsReplicationCalculator();
        calculator.computeFuturesSpreadMargin(valuationDate, tradeItems);

    }
}