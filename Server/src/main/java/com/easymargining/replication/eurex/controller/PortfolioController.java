package com.easymargining.replication.eurex.controller;

import com.easymargining.replication.eurex.domain.repository.IPortfolioRepository;
import com.easymargining.replication.eurex.domain.model.Portfolio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Gilles Marchal on 12/01/2016.
 */
@Slf4j
@RestController
public class PortfolioController {

    @Autowired
    private IPortfolioRepository portfolioRepository;

    @RequestMapping(value = "/portfolio/all")
    public List<Portfolio> getPortfolios() {
        return portfolioRepository.findAll();
    }

    @RequestMapping(value = "/portfolio/add")
    public Portfolio addPortfolio(Portfolio portfolio) {
        portfolioRepository.save(portfolio);
        return portfolio;
    }

    @RequestMapping(value = "/portfolio/remove")
    public Portfolio removePortfolio(Portfolio portfolio) {
        portfolioRepository.delete(portfolio);
        return portfolio;
    }

    @RequestMapping(value = "/portfolio/removes")
    public boolean removePortfolios(List<Portfolio> portfolios) {
        portfolioRepository.delete(portfolios);
        return true;
    }
}
