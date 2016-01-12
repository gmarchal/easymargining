package com.easymargining.replication.eurex.repository;

import com.easymargining.replication.eurex.model.TradeEntity;

/**
 * Created by gmarchal on 12/01/2016.
 */
public interface ITradeRepository extends MongoRepository<TradeEntity, String> {

}
