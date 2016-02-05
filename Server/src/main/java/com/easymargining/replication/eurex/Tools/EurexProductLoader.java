package com.easymargining.replication.eurex.Tools;

import com.opengamma.margining.eurex.prisma.replication.market.parsers.EurexSettlementPriceDefinition;
import com.opengamma.margining.eurex.prisma.replication.market.parsers.EurexSettlementPricesParser;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Gilles Marchal on 05/02/2016.
 */
@Slf4j
public class EurexProductLoader {


    public static void main(String[] args) {

        EurexSettlementPricesParser parser = new EurexSettlementPricesParser();

        try {
            log.info("Load Settlement Price definition ...");

            List<EurexSettlementPriceDefinition> definitions =
                    parser.parse(new File("Server/src/main/resources/marketData/20150603//ETD/00stlpricepubli20150603eodx.zip").toURI().toURL());

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

            log.info("End of Loading of Settlement Price definition");

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
