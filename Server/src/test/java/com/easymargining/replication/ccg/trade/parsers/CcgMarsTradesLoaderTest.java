package com.easymargining.replication.ccg.trade.parsers;

import com.easymargining.replication.ccg.trade.CcgMarsMarginTradeItem;
import com.opengamma.margining.eurex.prisma.data.FileResources;
import com.easymargining.replication.eurex.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.Assert;

import java.net.URL;
import java.util.List;

/**
 * Created by Gilles Marchal on 07/12/2015.
 */

@RunWith(SpringJUnit4ClassRunner.class)   // 1
@SpringApplicationConfiguration(Application.class)   // 2
@WebAppConfiguration   // 3
@IntegrationTest("server.port:0")

public class CcgMarsTradesLoaderTest {

    @Autowired
    private CcgMarsTradesLoader ccgMarsTradesLoader;

    @Test
    public void testReadTradeFile() throws Exception {
        Assert.notNull(ccgMarsTradesLoader);

        URL url = FileResources.byPath("ccg/trade/ccgTrades.csv");
        Assert.notNull(url);

        List<CcgMarsMarginTradeItem> tradeEntities = ccgMarsTradesLoader.readTradeFile(url);
        Assert.notEmpty(tradeEntities);
        Assert.isTrue(tradeEntities.size()==10);
    }
}