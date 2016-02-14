package com.easymargining.replication.eurex.domain.repository;

import com.easymargining.replication.eurex.domain.model.Portfolio;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by Gilles Marchal on 12/01/2016.
 */
public interface IPortfolioRepository extends MongoRepository<Portfolio, String> {

    Portfolio findByName(String name);

    List<Portfolio> findByOwnerId(String ownerId);

    Portfolio findByOwnerIdAndName(String ownerId, String name);
}