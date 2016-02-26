package com.easymargining.replication.eurex.domain.services.eurex;

import com.easymargining.replication.eurex.domain.model.Trade;
import com.easymargining.replication.eurex.domain.services.EurexETDTradeBuilder;
import com.opengamma.margining.core.trade.ImmutableMarginPortfolio;
import com.opengamma.margining.core.trade.MarginPortfolio;
import com.opengamma.margining.core.util.OgmLinkResolver;
import com.opengamma.sesame.trade.TradeWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gilles Marchal on 21/01/2016.
 */
@Slf4j
@AllArgsConstructor
public class EurexTradeTransformer {

    private OgmLinkResolver linkResolver;

    public MarginPortfolio buildEurexPortfolioFromTrade(List<Trade> trades, LocalDate valuationDate) {

        log.info(" --- Transform Trade to Eurex ETD trade - nb of trades : " + trades.size());

        EurexETDTradeBuilder eurexETDTradeBuilder =  new EurexETDTradeBuilder();
        List<TradeWrapper<?>> etdTrades = new ArrayList();

        trades.forEach(
                (trade) -> {
                    log.info(" --- " + trade.toString());

                    //LocalDate localDate = Instant.ofEpochMilli(trade.getExpiryDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                    //int year = localDate.getYear();
                    //int month = localDate.getMonthValue();

                    int year = trade.getExpiryDate().getContractYear().intValue();
                    int month = trade.getExpiryDate().getContractMonth().intValue();

                    double longBalance = 0;
                    double shortBalance = 0;
                    if ( trade.getQuantity() >  0 )
                        longBalance = trade.getQuantity();
                    else
                        shortBalance = trade.getQuantity();

                    // Transform Generic Trade to Eurex Trade
                    if ( trade.getInstrumentType().equals("Option") ) {
                        log.info( "Build Eurex Option Trade from : " + trade);
                        etdTrades.add(
                                eurexETDTradeBuilder.
                                        buildOptionTrade(
                                                        linkResolver,
                                                        trade.getProductId(),
                                                        year,
                                                        month,
                                                        0,
                                                        trade.getOptionType(),
                                                        trade.getExercisePrice(),
                                                        longBalance,
                                                        shortBalance,
                                                        valuationDate,
                                                        null));

                    } else if ( trade.getInstrumentType().equals("Future") ) {
                        log.info( "Build Eurex Future Trade from : " + trade);
                        etdTrades.add(
                                eurexETDTradeBuilder.
                                        buildFutureTrade(
                                                linkResolver,
                                                trade.getProductId(),
                                                year,
                                                month,
                                                longBalance,
                                                shortBalance,
                                                null));
                    } else {
                        log.info( "Trade not transformed : " + trade);
                    }
                }
        );

        return ImmutableMarginPortfolio.ofTrades(etdTrades);
    }
}
