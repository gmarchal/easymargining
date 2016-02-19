package com.easymargining.replication.eurex.domain.services;

import com.easymargining.replication.eurex.domain.model.Product;
import com.easymargining.replication.eurex.domain.repository.IProductRepository;
import com.opengamma.margining.eurex.prisma.replication.market.parsers.EurexProductDefinition;
import com.opengamma.margining.eurex.prisma.replication.market.parsers.EurexScenarioPricesParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.threeten.bp.LocalDate;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gilles Marchal on 16/02/2016.
 */
@Slf4j
@Service
//@EnableMongoRepositories("com.easymargining.replication.eurex.domain.repository")
public class ProductReferentialService {

    private final int BULK_INSERT_SIZE = 1000;

    @Autowired
    private IProductRepository productRepository;

    /*
    @Autowired
    public ProductReferentialService(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }*/

    public void storeProducts(List<Product> products) {
        log.info("ProductReferentialService::storeProducts( " + products.size() + " )");
        productRepository.save(products);
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

    public List<Product> findByEffectiveDate(LocalDate effectiveDate) {
        return productRepository.findByEffectiveDate(effectiveDate);
    }
}