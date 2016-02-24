package com.easymargining.replication.eurex.domain.repository;

import com.easymargining.replication.eurex.domain.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by Gilles Marchal on 16/02/2016.
 */
public interface IProductRepository extends MongoRepository<Product, String> {

    List<Product> findByProductId(String productId);

    List<Product> findDistinctProductByEffectiveDate(LocalDate effectiveDate);

    List<Product> findByEffectiveDate(LocalDate effectiveDate);

    List<Product> findByProductIdAndEffectiveDate(String productId, LocalDate effectiveDate);

    List<Product> findByOptionTypeIn(List<String> optionType);

    List<Product> findByOptionTypeNotIn(List<String> optionType);

    @Query(value="{ 'productId' : ?0 }", fields="{ 'contractYear' : 1, 'contractMonth' : 1}")
    List<Product> findMaturitiesByProductId(String productId);

    @Query(value="{ 'productId' : ?0, 'contractYear' : ?1, 'contractMonth' : ?2 }", fields="{ 'exercisePrice' : 1}")
    List<Product> findStrikesByProductIdAndContractMaturity(String productId, Integer contractYear, Integer contractMonth);
}
