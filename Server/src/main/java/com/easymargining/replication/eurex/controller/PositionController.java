package com.easymargining.replication.eurex.controller;

import com.easymargining.replication.eurex.domain.model.Trade;
import com.easymargining.replication.eurex.domain.repository.ITradeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Gilles Marchal on 19/01/2016.
 */
@Slf4j
@RestController
public class PositionController {

    @Autowired
    private ITradeRepository tradeRepository;

    @RequestMapping(value = "/position/all")
    public List<Trade> getPositions() {
        return tradeRepository.findAll();
    }

    @RequestMapping(value = "/position/{portfolioId}", method = RequestMethod.GET)
    public List<Trade> getPositionsByPortfolio(@PathVariable("portfolioId") String portfolioId) {
        return tradeRepository.findByPortfolioId(portfolioId);
    }

    @RequestMapping(value = "/position/add")
    public Trade addPosition(Trade trade) {
        log.info("Add position asked for position : " + trade );
        tradeRepository.save(trade);
        return trade;
    }

    @RequestMapping(value = "/position/save", method = RequestMethod.POST)
    public Trade savePosition(@RequestBody Trade trade) {
        log.info("Save position asked for position : " + trade );
        tradeRepository.save(trade);
        return trade;
    }

    @RequestMapping(value = "/position/remove", method = RequestMethod.POST)
    public Trade removePosition(@RequestBody Trade trade) {
        log.info("Remove position asked for position : " + trade );
        tradeRepository.delete(trade);
        return trade;
    }

    @RequestMapping(value = "/position/removes", method = RequestMethod.POST)
    public boolean removePositions(@RequestBody List<Trade> trades) {
        tradeRepository.delete(trades);
        return true;
    }
}
