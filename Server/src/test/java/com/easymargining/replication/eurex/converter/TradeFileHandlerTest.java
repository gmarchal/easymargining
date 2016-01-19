package com.easymargining.replication.eurex.converter;

import com.beust.jcommander.internal.Lists;
import com.opengamma.margining.eurex.prisma.data.FileResources;
import com.easymargining.replication.eurex.Application;
import com.easymargining.replication.eurex.domain.model.TradeEntity;
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

        URL url = FileResources.byPath("trade/etdTrades.csv");

        Assert.notNull(url);

        List<TradeEntity> tradeEntities = tradeFileHandler.readTradeFile(url);
        Assert.notEmpty(tradeEntities);

        //create a temp file
        File temp = File.createTempFile("temp-etdTrades", ".csv");

        tradeFileHandler.writeTradeFile(tradeEntities, temp.toURI().toURL());
        List<TradeEntity> readTradeEntities = tradeFileHandler.readTradeFile(temp.toURI().toURL());
        Assert.notEmpty(readTradeEntities);
        Assert.isTrue(readTradeEntities.size() == tradeEntities.size());
        for (TradeEntity tradeEntity : tradeEntities) {
            Assert.isTrue(readTradeEntities.contains(tradeEntity));
        }
    }

    @Test
    public void testWriteTradeFile() throws Exception {
        Assert.notNull(tradeFileHandler);

        URL url = FileResources.byPath("trade/etdTrades.csv");

        Assert.notNull(url);

        List<TradeEntity> tradeEntityList = Lists.newArrayList();
        TradeEntity trade = new TradeEntity();
        trade.setProductId("ORDX");
        tradeEntityList.add(trade);
        tradeFileHandler.writeTradeFile(tradeEntityList, url);
        System.out.println("Done");
    }
}