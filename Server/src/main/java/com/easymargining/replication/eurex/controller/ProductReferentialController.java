package com.easymargining.replication.eurex.controller;

import com.easymargining.replication.eurex.domain.model.ContractMaturity;
import com.easymargining.replication.eurex.domain.model.EurexProductDefinition;
import com.easymargining.replication.eurex.domain.model.Product;
import com.easymargining.replication.eurex.domain.services.ProductReferentialService;
import com.easymargining.replication.eurex.domain.services.marketdata.EurexMarketDataEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Gilles Marchal on 16/02/2016.
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/product")
public class ProductReferentialController {

    @Autowired
    ProductReferentialService productReferentialService;

    @RequestMapping(value = "/list/{effectiveDate}", method= RequestMethod.GET)
    public List<Product> getProducts(@PathVariable("effectiveDate") LocalDate effectiveDate) {
        log.info("ProductReferentialController::getProducts( " + effectiveDate + " )");
        return productReferentialService.findByEffectiveDate(effectiveDate);
    }

    @RequestMapping(value = "/all", method= RequestMethod.GET)
    public List<Product> getProducts() {
        log.info("ProductReferentialController::getAllProducts( )");
        return productReferentialService.findAll();
    }

    @RequestMapping(value = "/alldef", method= RequestMethod.GET)
    public List<EurexProductDefinition> getProductDefs() {
        log.info("ProductReferentialController::getProductDefs( )");
        return productReferentialService.findAllProductDefs();
    }

    @RequestMapping(value = "/findByCriteria", method= RequestMethod.GET)
    public List<EurexProductDefinition> findProductWithCriteria(String productType, String like) {
        log.info("ProductReferentialController::findProductWithCriteria( " + productType + ", " + like +" )");
        return productReferentialService.findProductWithCriteria(productType, like);
    }

    @RequestMapping(value = "/distinctProductId", method= RequestMethod.GET)
    public List<Product> getDistinctProductId() {
        log.info("ProductReferentialController::getDistinctProductId( )");
        LocalDate s_valuationDate = LocalDate.of(2015, 6, 3);
        return productReferentialService.findDistinctProductByEffectiveDate(s_valuationDate);
    }

    @RequestMapping(value = "/loadProducts", method= RequestMethod.POST)
    public Boolean loadProducts() {
        log.info("ProductReferentialController::loadProducts()");

        List<URL> list = EurexMarketDataEnvironment.getInstance().getTheoreticalPricesAndInstrumentConfiguration();
        LocalDate valuationDate = EurexMarketDataEnvironment.getInstance().getValuationDate();
        // Initialize Product Referential
        try {
            productReferentialService.loadProducts(list, valuationDate);
        } catch( IOException ex) {
            ex.printStackTrace();
        }
        return Boolean.TRUE;
    }

    @RequestMapping(value = "/loadProductDefs", method= RequestMethod.POST)
    public Boolean loadProductDefinition() {
        log.info("ProductReferentialController::loadProductDefs()");

        // Initialize Product Definition Referential
        try {
            String userDir = System.getProperties().getProperty("user.dir");
            LocalDate s_valuationDate = LocalDate.of(2015, 6, 3);
            List<URL> list = new ArrayList<URL>();
            list.add(new File(userDir + "/Server/src/main/resources/eurex/EurexProductDefinitions.csv").toURI().toURL());
            productReferentialService.loadEurexProductDefinition(list, s_valuationDate);
        } catch( IOException ex) {
            ex.printStackTrace();
        }
        return Boolean.TRUE;
    }

    @RequestMapping(value = "/getProducts", method= RequestMethod.GET)
    public List<Product> getProducts(String productId) {
        log.info("ProductReferentialController::getMaturities( " + productId + " )");
        return productReferentialService.getProducts(productId);
    }

    @RequestMapping(value = "/getMaturities", method= RequestMethod.GET)
    public Set<ContractMaturity> getMaturities(String productId) {
        log.info("ProductReferentialController::getMaturities( " + productId + " )");
        return productReferentialService.getMaturities(productId);
    }

    @RequestMapping(value = "/getStrikes", method= RequestMethod.GET)
    public Set<Double> getStrikes(String productId, String maturity) {
        log.info("ProductReferentialController::getStrikes( " + productId + ", " + maturity + " )");
        return productReferentialService.getStrikes(productId, maturity);
    }
}
