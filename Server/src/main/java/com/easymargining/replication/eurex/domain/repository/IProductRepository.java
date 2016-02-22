package com.easymargining.replication.eurex.domain.repository;

import com.easymargining.replication.eurex.domain.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by Gilles Marchal on 16/02/2016.
 */
public interface IProductRepository extends MongoRepository<Product, String> {

    Product findByProductId(String productId);

    List<Product> findDistinctProductByEffectiveDate(LocalDate effectiveDate);

    List<Product> findByEffectiveDate(LocalDate effectiveDate);

    List<Product> findByProductIdAndEffectiveDate(String productId, LocalDate effectiveDate);
}
