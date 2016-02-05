package com.easymargining.replication.eurex.domain.repository;

import com.easymargining.replication.eurex.domain.model.Trade;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by gmarchal on 12/01/2016.
 */
public interface ITradeRepository extends MongoRepository<Trade, String> {

    List<Trade> findByPortfolioId(String portfolioId);
}
