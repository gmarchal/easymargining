package com.easymargining.replication.eurex.domain.services;

import com.easymargining.replication.eurex.domain.model.ContractMaturity;
import com.easymargining.replication.eurex.domain.model.ContractMaturityComparator;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

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


    public void storeProducts(List<Product> products) {
        log.info("ProductReferentialService::storeProducts( " + products.size() + " )");
        productRepository.save(products);
    }

    public void deleteAllProducts() {
        log.info("ProductReferentialService::deleteAllProducts( )");
        productRepository.deleteAll();
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
        // Clear Existing products in DB
        this.deleteAllProducts();

        // Add Products in DB
        List<Product> eurexProducts = new ArrayList<>();
        productDefinitions.forEach(
                (eurexProductDefinition) -> {

                    Integer contractYear = eurexProductDefinition.getContractYear();
                    if (contractYear != null) {
                        contractYear = 2000 + contractYear;
                    }

                    // Bulk insert
                    eurexProducts.add(new Product (
                            null,
                            effectiveDate,
                            eurexProductDefinition.getProductId(),
                            contractYear,
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

    public Set<ContractMaturity> getMaturities(String productId) {
        List<Product> products = productRepository.findMaturitiesByProductId(productId);
        Set<ContractMaturity> maturitiesSet= new ConcurrentSkipListSet<>(new ContractMaturityComparator());
        products.parallelStream().forEach(product -> {
            maturitiesSet.add(new ContractMaturity(product.getContractYear(), product.getContractMonth()));
        });
        return maturitiesSet;
    }

    // Maturity contract Ex : {2022-01}
    public Set<Double> getStrikes(String productId, String maturity) {
        LocalDate maturityDate = LocalDate.parse(maturity.concat("-01"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        log.info("ProductReferentialService::getStrikes( " + productId + ", " + maturityDate.getYear() + maturityDate.getMonthValue() + " )");
        List<Product> products = productRepository.findStrikesByProductIdAndContractMaturity(productId, maturityDate.getYear(), maturityDate.getMonthValue());
        log.info("ProductReferentialService::getStrikes( " + productId + ", " + maturityDate.getYear() + ", " + maturityDate.getMonthValue() + " ) return " +products.size() + " products.");
        Set<Double> strikes = new ConcurrentSkipListSet<>();
        products.parallelStream().forEach(product -> {
            strikes.add(product.getExercisePrice());
        });
        return strikes;
    }

    public List<Product> getProducts(String productId) {
        return productRepository.findByProductId(productId);
    }

    //Product Type = Future or Option
    public List<Product> getProductsByType(String productType) {

        List<String> parameter = new ArrayList<>();
        parameter.add("C");
        parameter.add("P");

        if (productType.equals("Option")) {
            return productRepository.findByOptionTypeIn(parameter);
        } else if (productType.equals("Future")) {
            return productRepository.findByOptionTypeNotIn(parameter);
        }

        return new ArrayList<>();

    }
}