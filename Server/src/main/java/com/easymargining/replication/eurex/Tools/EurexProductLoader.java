package com.easymargining.replication.eurex.Tools;

import com.easymargining.replication.eurex.domain.model.Product;
import com.easymargining.replication.eurex.domain.services.ProductReferentialService;
import com.opengamma.margining.eurex.prisma.replication.market.parsers.EurexProductDefinition;
import com.opengamma.margining.eurex.prisma.replication.market.parsers.EurexScenarioPricesParser;
import com.opengamma.margining.eurex.prisma.replication.market.parsers.EurexSettlementPricesParser;
import lombok.extern.slf4j.Slf4j;
import org.threeten.bp.LocalDate;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gilles Marchal on 05/02/2016.
 */
@Slf4j
public class EurexProductLoader {


    public static void main(String[] args) {

        EurexSettlementPricesParser parser = new EurexSettlementPricesParser();

        try {
            LocalDate s_valuationDate = LocalDate.of(2015, 6, 3);

            log.info("Load Eurex product definition ...");

            // Small File "Server/src/main/resources/marketData/20150603//ETD/00stlpricepubli20150603eodx.zip"
            List<URL> list = new ArrayList<URL>();
            //list.add(new File("D:/gmarchal/eurex-data/marketdata/20151127/ETD/00THEOINSTPUBLI20151127NISERIESEODX0001_0005.TXT.ZIP").toURI().toURL());
            //list.add(new File("D:/gmarchal/eurex-data/marketdata/20151127/ETD/00THEOINSTPUBLI20151127NISERIESEODX0002_0005.TXT.ZIP").toURI().toURL());
            //list.add(new File("D:/gmarchal/eurex-data/marketdata/20151127/ETD/00THEOINSTPUBLI20151127NISERIESEODX0003_0005.TXT.ZIP").toURI().toURL());
            //list.add(new File("D:/gmarchal/eurex-data/marketdata/20151127/ETD/00THEOINSTPUBLI20151127NISERIESEODX0004_0005.TXT.ZIP").toURI().toURL());
            //list.add(new File("D:/gmarchal/eurex-data/marketdata/20151127/ETD/00THEOINSTPUBLI20151127NISERIESEODX0005_0005.TXT.ZIP").toURI().toURL());
            //list.add(new File("D:/gmarchal/eurex-data/marketdata/20151127/ETD/00THEOINSTPUBLI20151127OISERIESEODX0001_0001.TXT.ZIP").toURI().toURL());

            // Test Small File
            list.add(new File("C:/Homeware/workspace-eurex/easymargining/Server/src/main/resources/marketData/20150603/ETD/00theoinstpubli20150603aaa.txt").toURI().toURL());

            List<EurexProductDefinition> productDefinitions = EurexScenarioPricesParser.parse(list);

            log.info("End of Loading Eurex Product definition : " + productDefinitions.size() + " products");

            ProductReferentialService productReferentialService = new ProductReferentialService();
            List<Product> eurexProducts = new ArrayList<>();
            productDefinitions.forEach(
                    (eurexProductDefinition) -> {
                        // Bulk insert
                        eurexProducts.add(new Product (
                                s_valuationDate,
                                eurexProductDefinition.getProductId(),
                                eurexProductDefinition.getContractYear(),
                                eurexProductDefinition.getContractMonth(),
                                eurexProductDefinition.getSeriesDefinition().getVersionNumber(),
                                eurexProductDefinition.getSeriesDefinition().getSettlementType().toString(),
                                eurexProductDefinition.getSeriesDefinition().getCallPutFlag().isPresent() ? eurexProductDefinition.getSeriesDefinition().getCallPutFlag().get().name() : "",
                                eurexProductDefinition.getSeriesDefinition().getExercisePrice(),
                                eurexProductDefinition.getSeriesDefinition().getExerciseFlag().isPresent() ? eurexProductDefinition.getSeriesDefinition().getExerciseFlag().get().name() : "" ,
                                eurexProductDefinition.getSeriesDefinition().getCallPutFlag().isPresent() ? "Option" : "Future"));

                        if (eurexProducts.size() > 100 ) {
                            // bulk insert
                            productReferentialService.storeProducts(eurexProducts);
                            eurexProducts.clear();
                        }
                    }
            );
            // Insert Product
            if (eurexProducts.size() > 0) {
                // bulk insert
                productReferentialService.storeProducts(eurexProducts);
                eurexProducts.clear();
            }

            /*
            productDefinitions.forEach(
                    (eurexProductDefinition) -> {
                        log.info(" --- " +
                                eurexProductDefinition.getProductId()+                              // Product Id
                                " - " +
                                eurexProductDefinition.getContractYear() +                          // Expiry Year
                                " - " +
                                eurexProductDefinition.getContractMonth() +                         // Expiry Month
                                " - " +
                                eurexProductDefinition.getSeriesDefinition().getVersionNumber() +   // Version Number
                                " - " +
                                eurexProductDefinition.getSeriesDefinition().getSettlementType() +  // Settlement Type
                                " - " +
                                eurexProductDefinition.getSeriesDefinition().getCallPutFlag() +     // Call Put Flag
                                " - " +
                                eurexProductDefinition.getSeriesDefinition().getExercisePrice() +   // Exercice Price
                                " - " +
                                eurexProductDefinition.getSeriesDefinition().getExerciseFlag()      // Exercice Style Flag
                        );
                    }
            );
            */

            log.info("End of printing : Eurex Product definition: " + productDefinitions.size() + " products");

            /*

            List<EurexSettlementPriceDefinition> definitions =
                    parser.parse(new File("D:/gmarchal/eurex-data/marketdata/20151127/ETD/00STLPRICEPUBLI20151127EODX.TXT.ZIP").toURI().toURL());

            definitions.forEach(
                    (eurexSettlementPriceDefinition) -> {
                        log.info(" --- " +
                                eurexSettlementPriceDefinition.getProduct().getProductId().toString() +
                                " - " +
                                eurexSettlementPriceDefinition.getProduct().getSeriesDefinition().getCallPutFlag() +
                                " - " +
                                eurexSettlementPriceDefinition.getProduct().getExpirationDate() +
                                " - " +
                                eurexSettlementPriceDefinition.getProduct().getSeriesDefinition().getExercisePrice() +
                                " - " +
                                eurexSettlementPriceDefinition.getProduct().getSeriesDefinition().getExerciseFlag()
                        );
                    }
            );
            */



        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
