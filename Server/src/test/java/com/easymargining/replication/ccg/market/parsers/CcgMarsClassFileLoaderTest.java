package com.easymargining.replication.ccg.market.parsers;

import com.easymargining.replication.ccg.market.ClassFileItem;
import com.opengamma.margining.eurex.prisma.data.FileResources;
import com.easymargining.replication.eurex.Application;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class CcgMarsClassFileLoaderTest {

    private static final String symbol = "AAA13";
    private static final String description = "ETF LYXOR AAA MACRO-WEIGH GOV";
    private static final String classGroup = "AAA13";
    private static final String ProductGroup = "AA3";
    private static final String ClassType = "C";
    private static final String ProductType = "E";
    private static final String Offset = "100";
    private static final String SpotSpreadRate = "0.0";
    private static final String NonSpotSpreadRate = "0.0";
    private static final String DeliveryRate = "0.0";
    private static final String Multiplier = "1.0";
    private static final String ProductStyle = "";
    private static final String UnderlyingCode = "AAA13";
    private static final double CMV = 103.50;
    private static final String UnderlyingIsinCode = "FR0011146315";
    private static final String MarginInteval = "5.0";
    private static final String Currency = "EU";
    private static final String ExchangeRate = "1.0";
    private static final String CurrencyHaircut = "97";
    private static final String MinimumMargin = "0.0001";
    private static final String InterestRate = "1.0E-4";
    private static final String DaysToSettle = "2";
    private static final String ExpiryTime = "1730";
    private static final String MarketId = "3";
    private static final String SubType = "N";
    private static final String VMMultiplier = "1.0";


    @Autowired
    private CcgMarsClassFileLoader ccgMarsClassFileLoader;

    @Test
    public void testReadCCGClassFileFile() throws Exception {
        Assert.notNull(ccgMarsClassFileLoader);

        URL url = FileResources.byPath("ccg/market/classfile.xml");
        Assert.notNull(url);

        List<ClassFileItem> items = ccgMarsClassFileLoader.readCCGClassFileFile(url);
        Assert.notEmpty(items);

        int index = 0;
        ClassFileItem item = items.get(index);
        Assert.notNull(item);
        Assert.isTrue(item.getClassGroup().equals(classGroup));
        Assert.isTrue(item.getClassType().equals(ClassType));
        Assert.isTrue(item.getCmv() == CMV);

        log.info("First row of ClassFile file : " + item.toString());

        //for (ClassFileItem item : items) {
        //    System.out.println(item);
        //}
    }
}