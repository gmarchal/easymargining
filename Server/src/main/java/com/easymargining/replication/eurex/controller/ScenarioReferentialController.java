package com.easymargining.replication.eurex.controller;

import com.easymargining.replication.eurex.domain.model.Product;
import com.easymargining.replication.eurex.domain.services.ProductReferentialService;
import com.easymargining.replication.eurex.domain.services.ScenarioReferentialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by gmarchal on 26/02/2016.
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/scenario")
public class ScenarioReferentialController {

    @Autowired
    ScenarioReferentialService scenarioReferentialService;

    @RequestMapping(value = "/get", method= RequestMethod.GET)
    public List<Product> getScenarios() {
        log.info("ScenarioReferentialController::getScenarios( )");
        return null; // TODO : scenarioReferentialService.getScenarios();
    }
}
