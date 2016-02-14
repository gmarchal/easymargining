package com.easymargining.replication.eurex.controller;

import com.easymargining.replication.eurex.domain.model.Portfolio;
import com.easymargining.replication.eurex.domain.model.exception.PortfolioConflictException;
import com.easymargining.replication.eurex.domain.repository.IPortfolioRepository;
import com.google.common.net.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by Gilles Marchal on 12/01/2016.
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/portfolio")
public class PortfolioController {

    @Autowired
    private IPortfolioRepository portfolioRepository;

    @RequestMapping(value = "/list/{ownerId}", method= RequestMethod.GET)
    public List<Portfolio> getPortfolios(@PathVariable("ownerId") String ownerId) {
        log.info("PortfolioController::findByOwnerId( " + ownerId + " )");
        return portfolioRepository.findByOwnerId(ownerId);
    }

    @RequestMapping(value = "/add", method= RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public Portfolio addPortfolio(@RequestBody Portfolio portfolio, HttpServletResponse response) {
        log.info("PortfolioController::addPortfolio( " + portfolio + " )");

        // Validate first that the portfolio doesn't exist.
        Portfolio existingPortfolio = portfolioRepository.findByOwnerIdAndName(portfolio.getOwnerId(), portfolio.getName());

        if (existingPortfolio != null) {
            throw new PortfolioConflictException(portfolio.getOwnerId(), portfolio.getName());
        }

        Portfolio savedPortfolio = portfolioRepository.save(portfolio);
        response.setHeader(HttpHeaders.LOCATION, "/portfolio/" + savedPortfolio.getName());

        return portfolio;
    }

    @RequestMapping(value = "/remove")
    public Portfolio removePortfolio(Portfolio portfolio) {
        log.info("PortfolioController::removePortfolio( " + portfolio + " )");
        portfolioRepository.delete(portfolio);
        return portfolio;
    }

    @RequestMapping(value = "/removes")
    public boolean removePortfolios(List<Portfolio> portfolios) {
        portfolioRepository.delete(portfolios);
        return true;
    }
}
