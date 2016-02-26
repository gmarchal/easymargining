package com.easymargining.replication.eurex.controller;

import com.easymargining.replication.eurex.domain.model.Trade;
import com.easymargining.replication.eurex.domain.repository.ITradeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Gilles Marchal on 19/01/2016.
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/positions")
public class PositionController {

    @Autowired
    private ITradeRepository tradeRepository;

    @RequestMapping(value = "/list")
    public List<Trade> getPositions() {
        return tradeRepository.findAll();
    }

    @RequestMapping(value = "/{portfolioId}", method = RequestMethod.GET)
    public List<Trade> getPositionsByPortfolio(@PathVariable("portfolioId") String portfolioId) {
        return tradeRepository.findByPortfolioId(portfolioId);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Trade addPosition(@RequestBody Trade trade) {
        log.info("Save position asked for position : " + trade );


        tradeRepository.save(trade);
        return trade;
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public Trade removePosition(@RequestBody Trade trade) {
        log.info("Remove position asked for position : " + trade );
        tradeRepository.delete(trade);
        return trade;
    }

    @RequestMapping(value = "/removes", method = RequestMethod.POST)
    public boolean removePositions(@RequestBody List<Trade> trades) {
        tradeRepository.delete(trades);
        return true;
    }
}
