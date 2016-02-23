package com.easymargining.replication.eurex.converter;

import com.beust.jcommander.internal.Lists;
import com.opengamma.margining.eurex.prisma.data.FileResources;
import com.easymargining.replication.eurex.Application;
import com.easymargining.replication.eurex.domain.model.EurexTradeEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.Assert;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * Created by Mederic on 23/11/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)   // 1
@SpringApplicationConfiguration(Application.class)   // 2
@WebAppConfiguration   // 3
@IntegrationTest("server.port:0")
public class TradeFileHandlerTest {

    @Autowired
    private TradeFileHandler tradeFileHandler;

    @Test
    public void testReadWriteTradeFile() throws Exception {
        Assert.notNull(tradeFileHandler);

        URL url = FileResources.byPath("resources/eurex/trade/etdTrades.csv");

        Assert.notNull(url);

        List<EurexTradeEntity> tradeEntities = tradeFileHandler.readTradeFile(url);
        Assert.notEmpty(tradeEntities);

        //create a temp file
        File temp = File.createTempFile("temp-etdTrades", ".csv");

        tradeFileHandler.writeTradeFile(tradeEntities, temp.toURI().toURL());
        List<EurexTradeEntity> readTradeEntities = tradeFileHandler.readTradeFile(temp.toURI().toURL());
        Assert.notEmpty(readTradeEntities);
        Assert.isTrue(readTradeEntities.size() == tradeEntities.size());
        for (EurexTradeEntity eurexTradeEntity : tradeEntities) {
            Assert.isTrue(readTradeEntities.contains(eurexTradeEntity));
        }
    }

    @Test
    public void testWriteTradeFile() throws Exception {
        Assert.notNull(tradeFileHandler);

        URL url = FileResources.byPath("resources/eurex/trade/etdTrades.csv");

        Assert.notNull(url);

        List<EurexTradeEntity> eurexTradeEntityList = Lists.newArrayList();
        EurexTradeEntity trade = new EurexTradeEntity();
        trade.setProductId("ORDX");
        eurexTradeEntityList.add(trade);
        tradeFileHandler.writeTradeFile(eurexTradeEntityList, url);
        System.out.println("Done");
    }
}