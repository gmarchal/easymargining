package com.easymargining.replication.eurex.domain.repository;

import com.easymargining.replication.eurex.domain.model.EurexProductDefinition;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by Gilles Marchal on 20/02/2016.
 */
public interface IProductDefinitionRepository extends MongoRepository<EurexProductDefinition, String> {

    // ProductType : Future or Option
    List<EurexProductDefinition> findByTypeAndProductNameLikeOrEurexCodeLike(String productType, String like, String like2);

}