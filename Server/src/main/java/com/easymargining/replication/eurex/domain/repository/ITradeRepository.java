package com.easymargining.replication.eurex.domain.repository;

import com.easymargining.replication.eurex.domain.model.TradeEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by gmarchal on 12/01/2016.
 */
public interface ITradeRepository extends MongoRepository<TradeEntity, String> {

}
