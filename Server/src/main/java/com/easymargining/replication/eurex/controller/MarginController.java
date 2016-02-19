package com.easymargining.replication.eurex.controller;

import com.easymargining.replication.eurex.domain.model.MarginResult;
import com.easymargining.replication.eurex.domain.services.eurex.EurexPrimaMarginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Gilles Marchal on 21/01/2016.
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/margin")
public class MarginController {

    @Autowired
    EurexPrimaMarginService marginService;

    @RequestMapping(value = "/computeEtd/{portfolioId}", method = RequestMethod.GET)
    public MarginResult computeEtdMargin(@PathVariable("portfolioId") String portfolioId) {
        return marginService.computeEtdMargin(portfolioId);
    }


}
