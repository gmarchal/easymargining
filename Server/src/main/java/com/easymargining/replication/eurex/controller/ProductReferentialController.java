package com.easymargining.replication.eurex.controller;

import com.easymargining.replication.eurex.domain.model.Product;
import com.easymargining.replication.eurex.domain.services.ProductReferentialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.threeten.bp.LocalDate;

import java.util.List;

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
    }}
