package com.easymargining.replication.ccg.market.parsers;

import com.easymargining.replication.ccg.market.RiskArrayItem;
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

public class CcgMarsRiskArrayLoaderTest {

    @Autowired
    private CcgMarsRiskArrayLoader ccgMarsRiskArrayLoader;

    @Test
    public void testReadCCGRiskArrayFile() throws Exception {
        Assert.notNull(ccgMarsRiskArrayLoader);

        URL url = FileResources.byPath("ccg/market/riskarray.xml");
        Assert.notNull(url);

        List<RiskArrayItem> items = ccgMarsRiskArrayLoader.readCCGRiskArrayFile(url);
        Assert.notEmpty(items);
    }
}