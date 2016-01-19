package com.easymargining.replication.eurex.domain.repository;

import com.easymargining.replication.eurex.domain.model.Portfolio;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Gilles Marchal on 12/01/2016.
 */
public interface IPortfolioRepository extends MongoRepository<Portfolio, String> {

}
