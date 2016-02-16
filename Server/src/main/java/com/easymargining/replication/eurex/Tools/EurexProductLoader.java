package com.easymargining.replication.eurex.Tools;

import com.opengamma.margining.eurex.prisma.data.MarketDataFileResolver;
import com.opengamma.margining.eurex.prisma.replication.market.parsers.EurexProductDefinition;
import com.opengamma.margining.eurex.prisma.replication.market.parsers.EurexScenarioPricesParser;
import com.opengamma.margining.eurex.prisma.replication.market.parsers.EurexSettlementPriceDefinition;
import com.opengamma.margining.eurex.prisma.replication.market.parsers.EurexSettlementPricesParser;
import lombok.extern.slf4j.Slf4j;
import org.threeten.bp.LocalDate;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by Gilles Marchal on 05/02/2016.
 */
@Slf4j
public class EurexProductLoader {


    public static void main(String[] args) {


        EurexSettlementPricesParser parser = new EurexSettlementPricesParser();




        try {
            log.info("Load Eurex product definition ...");

            LocalDate s_valuationDate = LocalDate.of(2015, 6, 3);
            // Use file resolver utility to discover data from standard Eurex directory structure
            MarketDataFileResolver fileResolver = new MarketDataFileResolver("marketData", s_valuationDate);

            // Small File "Server/src/main/resources/marketData/20150603//ETD/00stlpricepubli20150603eodx.zip"
            //
            List<EurexProductDefinition> productDefinitions =
                    EurexScenarioPricesParser.parse(
                            new File("D:/gmarchal/eurex-data/marketdata/20151127/ETD/00THEOINSTPUBLI20151127NISERIESEODX0001_0005.TXT.ZIP").toURI().toURL());

            // Big File "D:/gmarchal/eurex-data/marketdata/20151127/ETD/00THEOINSTPUBLI20151127NISERIESEODX0001_0005.TXT.ZIP"
            //List<EurexSettlementPriceDefinition> definitions =
            //        parser.parse(new File("D:/gmarchal/eurex-data/marketdata/20151127/ETD/00THEOINSTPUBLI20151127NISERIESEODX0001_0005.TXT.ZIP").toURI().toURL());

            log.info("End of Loading Eurex Product definition : " + productDefinitions.size() + "products");

            productDefinitions.forEach(
                    (eurexProductDefinition) -> {
                        log.info(" --- " +
                                eurexProductDefinition.getProductId()+                              // Product Id
                                " - " +
                                eurexProductDefinition.getContractYear() +                          // Expiry Year
                                " - " +
                                eurexProductDefinition.getContractMonth() +                         // Expiry Month
                                " - " +
                                eurexProductDefinition.getSeriesDefinition().getSettlementType() +  // Settlement Type
                                " - " +
                                eurexProductDefinition.getSeriesDefinition().getCallPutFlag() +     // Call Put Flag
                                " - " +
                                eurexProductDefinition.getSeriesDefinition().getExercisePrice() +   // Exercice Price
                                " - " +
                                eurexProductDefinition.getSeriesDefinition().getExerciseFlag() +    // Exercice Style Flag
                                " - " +
                                eurexProductDefinition.getSeriesDefinition().getExerciseFlag() +    // Exercice Style Flag
                                " - " +
                                eurexProductDefinition.getSeriesDefinition().getExerciseFlag()      // Exercice Style Flag
                        );
                    }
            );

            /*
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

            log.info("End of printing : Eurex Product definition: " + productDefinitions.size() + " products");

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
