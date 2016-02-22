package com.easymargining.replication.eurex.domain.services;

import com.easymargining.replication.eurex.domain.model.Product;
import com.easymargining.replication.eurex.domain.repository.IProductDefinitionRepository;
import com.easymargining.replication.eurex.domain.repository.IProductRepository;
import com.easymargining.tools.eurex.EurexProductDefinitionParser;
import com.opengamma.margining.eurex.prisma.replication.market.parsers.EurexProductDefinition;
import com.opengamma.margining.eurex.prisma.replication.market.parsers.EurexScenarioPricesParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gilles Marchal on 16/02/2016.
 */
@Slf4j
@Service
public class ProductReferentialService {

    private final int BULK_INSERT_SIZE = 1000;

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private IProductDefinitionRepository productDefRepository;

    /*
    @Autowired
    public ProductReferentialService(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }*/

    public void storeProducts(List<Product> products) {
        log.info("ProductReferentialService::storeProducts( " + products.size() + " )");
        productRepository.save(products);
    }

    public void loadEurexProductDefinition(List<URL> files, LocalDate effectiveDate) {
        log.info("ProductReferentialService::loadEurexProductDefinition( " + files.toString() + ", " + effectiveDate + " )");
        files.forEach( (file) -> {

            List<com.easymargining.replication.eurex.domain.model.EurexProductDefinition> productDefinitions = null;
            try {
                productDefinitions = EurexProductDefinitionParser.parse(file);
                productDefRepository.save(productDefinitions);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    public void loadProducts(List<URL> files, LocalDate effectiveDate) throws IOException {
        log.info("ProductReferentialService::loadProducts( " + files.toString() + ", " + effectiveDate + " )");
        List<EurexProductDefinition> productDefinitions = EurexScenarioPricesParser.parse(files);
        log.info("End of Loading Eurex Product definition : " + productDefinitions.size() + " products");
        List<Product> eurexProducts = new ArrayList<>();
        productDefinitions.forEach(
                (eurexProductDefinition) -> {
                    // Bulk insert
                    eurexProducts.add(new Product (
                            null,
                            effectiveDate,
                            eurexProductDefinition.getProductId(),
                            eurexProductDefinition.getContractYear(),
                            eurexProductDefinition.getContractMonth(),
                            eurexProductDefinition.getSeriesDefinition().getVersionNumber(),
                            eurexProductDefinition.getSeriesDefinition().getSettlementType().toString(),
                            eurexProductDefinition.getSeriesDefinition().getCallPutFlag().isPresent() ? eurexProductDefinition.getSeriesDefinition().getCallPutFlag().get().name() : "",
                            eurexProductDefinition.getSeriesDefinition().getExercisePrice(),
                            eurexProductDefinition.getSeriesDefinition().getExerciseFlag().isPresent() ? eurexProductDefinition.getSeriesDefinition().getExerciseFlag().get().name() : "" ,
                            eurexProductDefinition.getSeriesDefinition().getCallPutFlag().isPresent() ? "Option" : "Future"));

                    if (eurexProducts.size() >= BULK_INSERT_SIZE ) {
                        // bulk insert
                        this.storeProducts(eurexProducts);
                        eurexProducts.clear();
                    }
                }
        );
        // Insert Product
        if (eurexProducts.size() > 0) {
            // bulk insert
            this.storeProducts(eurexProducts);
            eurexProducts.clear();
        }
    }

    public List<Product> findDistinctProductByEffectiveDate(LocalDate effectiveDate) {
        return productRepository.findDistinctProductByEffectiveDate(effectiveDate);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findByEffectiveDate(LocalDate effectiveDate) {
        return productRepository.findByEffectiveDate(effectiveDate);
    }

    public List<com.easymargining.replication.eurex.domain.model.EurexProductDefinition> findAllProductDefs() {
        return productDefRepository.findAll();
    }

    public List<com.easymargining.replication.eurex.domain.model.EurexProductDefinition> findProductWithCriteria(String productType, String like) {
        return productDefRepository.findByTypeAndProductNameLikeOrEurexCodeLike(productType, like, like);
    }
}