package com.easymargining.replication.eurex.controller;

import com.easymargining.replication.eurex.domain.services.ProductReferentialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Gilles Marchal on 16/02/2016.
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/product")
public class ProductReferentialController {

    @Autowired
    ProductReferentialService productReferentialService;


}
