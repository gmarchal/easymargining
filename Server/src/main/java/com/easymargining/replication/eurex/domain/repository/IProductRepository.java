package com.easymargining.replication.eurex.domain.repository;

import com.easymargining.replication.eurex.domain.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Gilles Marchal on 16/02/2016.
 */
public interface IProductRepository  extends MongoRepository<Product, String> {
}
