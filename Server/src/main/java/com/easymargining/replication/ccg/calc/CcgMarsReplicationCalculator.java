package com.easymargining.replication.ccg.calc;

import com.easymargining.replication.ccg.common.ClassTypeEnum;
import com.easymargining.replication.ccg.common.ClassTypeEnumComparator;
import com.easymargining.replication.ccg.market.ClassFileItem;
import com.easymargining.replication.ccg.market.MarketDataEnvironment;
import com.easymargining.replication.ccg.market.MarketDataFileResolver;
import com.easymargining.replication.ccg.market.RiskArrayItem;
import com.easymargining.replication.ccg.trade.CcgMarsMarginTradeItem;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Gilles Marchal on 07/12/2015.
 */
@Slf4j
/*
 * The daily margin requirement for a class group or a product group has four
 * components: “futures spread margin” which covers futures positions which have
 * been spread, “Mark to Market margin” which covers, for futures, any stock futures
 * positions which have been expired and have not yet been settled and, for
 * securities, the cost to liquidate securities positions at current market value,
 * “premium margin” which covers the cost to liquidate option positions at current
 * market prices and “additional margin” (risk margin) which covers the additional
 * projected cost of liquidating all securities positions, option positions and non-spread
 * futures positions in the event of an assumed worst case change in the price of the
 * underlying. In addition, if the risk margin component of a particular product group is
 * less than a calculated minimum margin for the product group, then the minimum
 * margin will be taken as the risk margin.
 */
public class CcgMarsReplicationCalculator {


    public void computeFuturesSpreadMargin(LocalDate s_valuationDate, List<CcgMarsMarginTradeItem> marginTradeItems){

        // Use file resolver utility to discover data from CCG directory structure
        MarketDataFileResolver fileResolver = new MarketDataFileResolver("marketData", s_valuationDate);
        MarketDataEnvironment marketDataEnv = new MarketDataEnvironment();
        marketDataEnv.loadMarketData(fileResolver);

        // 1- Update position "List<CcgMarsMarginTradeItem>" records with underlying information

        // class file must be used to acquire the Class Group and Product group for each
        // position record by matching on the “Symbol” field
        List<ClassFileItem> classFileItems = marketDataEnv.getClassFileItems();

        Map<String, ClassFileItem> classFileMap = classFileItems
                .stream()
                .collect(Collectors.toMap(
                        p -> p.getSymbol() + "#" + p.getClassType().getShortName(), // Unique Key = Symbol + Class Type
                        p -> p,
                        (oldValue, newValue)  -> oldValue)
                );


        List<RiskArrayItem> riskArrayItems = marketDataEnv.getRiskArrayItems();
        Map<String, RiskArrayItem> riskArrayMap = riskArrayItems
                .stream()
                .collect(Collectors.toMap(
                        p -> p.getSymbol() + "#" + p.getClassType(), // Unique Key = Symbol + Class Type
                        p -> p,
                        (oldValue, newValue)  -> oldValue)
                );

        // Build MarginPosition from marginTradeItem and
        List<MarginPositionItem> marginPositions = new ArrayList<MarginPositionItem>();
        marginTradeItems.forEach( marginTradeItem -> {

            MarginPositionItem marginPosition = new MarginPositionItem();
            marginPosition.setTradeItem(marginTradeItem);
            // Link ClassFileItem to TradeIem
            ClassFileItem classFileItem = classFileMap.get(
                    marginTradeItem.getSymbol() + "#" +
                            marginTradeItem.getClassType().getShortName());
            marginPosition.setClassFileItem(classFileItem);
            // Link RiskArrayItem to TradeItem
            RiskArrayItem riskArrayItem = riskArrayMap.get(
                    marginTradeItem.getSymbol() + "#" +
                            marginTradeItem.getClassType().getShortName());
            marginPosition.setRiskArrayItem(riskArrayItem);
            marginPositions.add(marginPosition);
        });

        // Positions must be sorted in the following sequence for further processing:
        // account ID, product group, class group, futures, options, securities.
        Comparator<MarginPositionItem> byAccountId = (e1, e2) -> e1
                .getTradeItem().getAccountid().compareTo(e2.getTradeItem().getAccountid());

        Comparator<MarginPositionItem> byProductGroup = (e1, e2) -> e1.getClassFileItem().getProductGroup()
                .compareTo(e2.getClassFileItem().getProductGroup());

        Comparator<MarginPositionItem> byClassGroup = (e1, e2) -> e1.getClassFileItem().getClassGroup()
                .compareTo(e2.getClassFileItem().getClassGroup());

        // Futures 'F', Options 'O', Securities 'C'
        Comparator<MarginPositionItem> byClassType = (e1, e2) -> new ClassTypeEnumComparator()
                .compare(e1.getTradeItem().getClassType(), e2.getTradeItem().getClassType());

        List<MarginPositionItem> sortedMarginPositions = marginPositions.stream().sorted(
                byAccountId.thenComparing(byProductGroup).
                thenComparing(byClassGroup).thenComparing(byClassType)).collect(Collectors.toList());

        // End of Build Position

        // Select only Futures products and build an aggregation
        Map<MarginPositionAggregationKey, List<MarginPositionItem>> futuresMarginPositionsMap = sortedMarginPositions.stream()
                .filter(b -> b.getTradeItem().getClassType() == ClassTypeEnum.CONVERTIBLE_BONDS.FUTURES)
                .collect(Collectors.groupingBy(p -> new MarginPositionAggregationKey(
                                p.getTradeItem().getAccountid(),
                                p.getClassFileItem().getClassGroup(),
                                p.getClassFileItem().getClassType(),
                                p.getTradeItem().getExpYear(),
                                p.getTradeItem().getExpMonth(),
                                p.getTradeItem().getStrikePrice(),
                                p.getTradeItem().getOptionType())
                        ));


        futuresMarginPositionsMap.forEach(
                (marginPositionAggregationKey, marginPositionItems) -> {
                    log.info(marginPositionItems.toString());
                    //test
                } );


    }

    public void computeMarktoMarketMargin() {

    }

    public void premiumMargin() {

    }

    public void computeAdditionalMargin () {

    }

}
