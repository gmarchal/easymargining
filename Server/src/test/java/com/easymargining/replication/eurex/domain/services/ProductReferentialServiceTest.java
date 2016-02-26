package com.easymargining.replication.eurex.domain.services;

import com.easymargining.replication.eurex.Application;
import com.easymargining.replication.eurex.domain.model.ContractMaturity;
import com.easymargining.replication.eurex.domain.model.Product;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;

/**
 * Created by gmarchal on 23/02/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)             // 1
@SpringApplicationConfiguration(Application.class)  // 2
@WebAppConfiguration                                // 3
@IntegrationTest("server.port:0")
@Component
public class ProductReferentialServiceTest {

    @Autowired
    ProductReferentialService productReferentialService;

    @Test
    public void testGetMaturities() throws Exception {
        Set<ContractMaturity> maturities = productReferentialService.getMaturities("ORDX");

    }

    @Test
    public void testGetStrikes() throws Exception {
        Set<Double> strikes = productReferentialService.getStrikes("ORDX", new ContractMaturity(2022, 12));

    }

    @Test
    public void testGetProducts() throws Exception {
        List<Product> products = productReferentialService.getProducts("ORDX");
    }


    public static void main(String[] args) throws Exception {

        ApplicationContext context = SpringApplication.run(Application.class, args);
        ProductReferentialServiceTest p = context.getBean(ProductReferentialServiceTest.class);
        p.testGetStrikes();

    }
}