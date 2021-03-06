package com.easymargining.replication.eurex.domain.services;

import com.opengamma.core.link.SecurityLink;
import com.opengamma.core.position.Counterparty;
import com.opengamma.core.position.impl.SimpleCounterparty;
import com.opengamma.core.position.impl.SimpleTrade;
import com.opengamma.id.ExternalId;
import com.opengamma.id.UniqueId;
import com.opengamma.margining.core.trade.ClearedEtdTrade;
import com.opengamma.margining.core.util.OgmLinkResolver;
import com.opengamma.margining.eurex.prisma.replication.market.EurexIdUtils;
import com.opengamma.margining.financial.security.ClearedEtdSecurity;
import com.opengamma.sesame.trade.TradeWrapper;
import lombok.extern.slf4j.Slf4j;
import org.threeten.bp.LocalDate;
import org.threeten.bp.OffsetTime;

import java.math.BigDecimal;

/**
 * Created by Gilles on 23/11/2015.
 */
@Slf4j
public class EurexETDTradeBuilder {

    public TradeWrapper<?> buildFlexOptionTrade(OgmLinkResolver linkResolver,
                                         String productId,
                                         int expiryYear,
                                         int expiryMonth,
                                         int expiryDay,
                                         int versionNumber,
                                         String productSettlementType,
                                         String callPut,
                                         Double exercisePrice,
                                         String exerciseStyle,
                                         double longBalance,
                                         double shortBalance,
                                         String openGammaSpecifiedId ) {

        SecurityLink link = SecurityLink.resolvable(EurexIdUtils.buildClearedEtdFlexOptionSecurityId(productId, expiryYear, expiryMonth, expiryDay, versionNumber, productSettlementType, callPut, exercisePrice.doubleValue(), exerciseStyle));
        ClearedEtdSecurity security = (ClearedEtdSecurity)linkResolver.resolve(link);
        double quantity = longBalance - shortBalance;
        SimpleTrade trade = new SimpleTrade(security, BigDecimal.valueOf(quantity), new SimpleCounterparty(ExternalId.of(Counterparty.DEFAULT_SCHEME, "CPARTY")), LocalDate.now(), OffsetTime.now());

        if(openGammaSpecifiedId != null) {
            if(openGammaSpecifiedId.contains("~")) {
                trade.setUniqueId(UniqueId.parse(openGammaSpecifiedId));
            } else {
                trade.setUniqueId(UniqueId.of("OpenGammaId", openGammaSpecifiedId));
            }
        } else {
            trade.setUniqueId(EurexIdUtils.nextTradeId());
        }

        return new ClearedEtdTrade(trade);
    }


    public TradeWrapper<?> buildOptionTrade(OgmLinkResolver linkResolver,
                                     String productId,
                                     int expiryYear,
                                     int expiryMonth,
                                     int versionNumber,
                                     String callPut,
                                     Double exercisePrice,
                                     double longBalance,
                                     double shortBalance,
                                     LocalDate tradedate,
                                     String openGammaSpecifiedId ) {

        log.info ("Create new Option Trade with parameter : { productId : " + productId + ", expiryYear : " + expiryYear
                + ", expiryMonth : " + expiryMonth + ", versionNumber : " + versionNumber + ",callPut : " + callPut
                + ", exercisePrice : " + exercisePrice + " } ");

        SecurityLink link = SecurityLink.resolvable(
                                EurexIdUtils.buildClearedEtdOptionSecurityId(
                                            productId,
                                            expiryYear,
                                            expiryMonth,
                                            versionNumber,
                                            callPut,
                                            exercisePrice));

        ClearedEtdSecurity security = (ClearedEtdSecurity)linkResolver.resolve(link);

        double quantity = longBalance - shortBalance;

        SimpleTrade trade = new SimpleTrade(security,
                                            BigDecimal.valueOf(quantity),
                                            new SimpleCounterparty(ExternalId.of(Counterparty.DEFAULT_SCHEME, "CPARTY")),
                                            tradedate,
                                            OffsetTime.now());

        if(openGammaSpecifiedId != null) {
            if(openGammaSpecifiedId.contains("~")) {
                trade.setUniqueId(UniqueId.parse(openGammaSpecifiedId));
            } else {
                trade.setUniqueId(UniqueId.of("OpenGammaId", openGammaSpecifiedId));
            }
        } else {
            trade.setUniqueId(EurexIdUtils.nextTradeId());
        }

        return new ClearedEtdTrade(trade);
    }


    public TradeWrapper<?> buildFlexFutureTrade(OgmLinkResolver linkResolver,
                                         String productId,
                                         int expiryYear,
                                         int expiryMonth,
                                         int expiryDay,
                                         String productSettlementType,
                                         double longBalance,
                                         double shortBalance,
                                         LocalDate tradedate,
                                         String openGammaSpecifiedId ) {

        log.info ("Create new Flex Future Trade with parameter : { productId : " + productId + ", expiryYear : " + expiryYear
                + ", expiryMonth : " + expiryMonth + ", expiryDay : " + expiryDay + ",productSettlementType : " + productSettlementType
                + ", quantity : " + (longBalance - shortBalance) + " } ");

        SecurityLink link = SecurityLink.resolvable(
                                EurexIdUtils.buildClearedEtdFlexFutureSecurityId(
                                                productId,
                                                expiryYear,
                                                expiryMonth,
                                                expiryDay,
                                                productSettlementType));

        ClearedEtdSecurity security = (ClearedEtdSecurity)linkResolver.resolve(link);

        double quantity = longBalance - shortBalance;

        SimpleTrade trade = new SimpleTrade(
                                    security,
                                    BigDecimal.valueOf(quantity),
                                    new SimpleCounterparty(ExternalId.of(Counterparty.DEFAULT_SCHEME, "CPARTY")),
                                    tradedate,
                                    OffsetTime.now());

        if(openGammaSpecifiedId != null) {
            if(openGammaSpecifiedId.contains("~")) {
                trade.setUniqueId(UniqueId.parse(openGammaSpecifiedId));
            } else {
                trade.setUniqueId(UniqueId.of("OpenGammaId", openGammaSpecifiedId));
            }
        } else {
            trade.setUniqueId(EurexIdUtils.nextTradeId());
        }

        return new ClearedEtdTrade(trade);
    }


    public TradeWrapper<?> buildFutureTrade(OgmLinkResolver linkResolver,
                                     String productId,
                                     int expiryYear,
                                     int expiryMonth,
                                     double longBalance,
                                     double shortBalance,
                                     String openGammaSpecifiedId ) {

        log.info ("Create new Future Trade with parameter : { productId : " + productId + ", expiryYear : " + expiryYear
                + ", expiryMonth : " + expiryMonth + ", quantity : " + (longBalance - shortBalance) + " } ");

        SecurityLink link = SecurityLink.resolvable(
                EurexIdUtils.buildClearedEtdFutureSecurityId(
                        productId,
                        expiryYear,
                        expiryMonth));

        ClearedEtdSecurity security = (ClearedEtdSecurity)linkResolver.resolve(link);
        double quantity = longBalance - shortBalance;
        SimpleTrade trade = new SimpleTrade(security, BigDecimal.valueOf(quantity), new SimpleCounterparty(ExternalId.of(Counterparty.DEFAULT_SCHEME, "CPARTY")), LocalDate.now(), OffsetTime.now());

        if(openGammaSpecifiedId != null) {
            if(openGammaSpecifiedId.contains("~")) {
                trade.setUniqueId(UniqueId.parse(openGammaSpecifiedId));
            } else {
                trade.setUniqueId(UniqueId.of("OpenGammaId", openGammaSpecifiedId));
            }
        } else {
            trade.setUniqueId(EurexIdUtils.nextTradeId());
        }

        return new ClearedEtdTrade(trade);
    }

}
