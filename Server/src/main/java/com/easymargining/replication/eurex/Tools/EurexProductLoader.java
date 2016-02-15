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

            // Small File "Server/src/main/resources/marketData/20150603//ETD/00stlpricepubli20150603eodx.zip"
            // Big File "D:/gmarchal/eurex-data/marketdata/20151127/ETD/00THEOINSTPUBLI20151127NISERIESEODX0001_0005.TXT.ZIP"
            List<EurexSettlementPriceDefinition> definitions =
                    parser.parse(new File("D:/gmarchal/eurex-data/marketdata/20151127/ETD/00THEOINSTPUBLI20151127NISERIESEODX0001_0005.TXT.ZIP").toURI().toURL());

            log.info("End of Loading Settlement Price definition ...");

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

            log.info("End of printing : Settlement Price definition");

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
