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


    public List<FuturesSpreadMargin> computeFuturesSpreadMargin(LocalDate s_valuationDate, List<CcgMarsMarginTradeItem> marginTradeItems){

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


        List<MarginPositionItem> futureMarginPositionsNetted = new ArrayList<>();
        futuresMarginPositionsMap.forEach(
                (marginPositionAggregationKey, marginPositionItems) -> {
                    log.info(marginPositionItems.toString());
                    if (marginPositionItems.size() > 1 ) {
                        // Net Futures positions

                        // Find Future position with greater multiplier
                        MarginPositionItem futurePositionWithGreaterMultiplier =
                                marginPositionItems.stream()
                                        .max((p1, p2) -> Double.compare(
                                                p1.getClassFileItem().getMultiplier(),
                                                p2.getClassFileItem().getMultiplier()))
                                        .get();
                        log.info(" Future Position With Greater Multiplier = " + futurePositionWithGreaterMultiplier.toString());

                        // Find Future position with smaller multiplier
                        MarginPositionItem futurePositionWithSmallerMultiplier =
                                marginPositionItems.stream()
                                        .min((p1, p2) -> Double.compare(
                                                p1.getClassFileItem().getMultiplier(),
                                                p2.getClassFileItem().getMultiplier()))
                                        .get();

                        log.info(" Future Position With Smaller Multiplier = " + futurePositionWithSmallerMultiplier.toString());

                        // Calculate Conversion Factor
                        double conversionFactor = futurePositionWithGreaterMultiplier.getClassFileItem().getMultiplier() /
                                futurePositionWithSmallerMultiplier.getClassFileItem().getMultiplier();

                        log.info(" Conversion Factor = " + conversionFactor);

                        // Generate converted position from position with the smaller multiplier
                        futurePositionWithGreaterMultiplier.getTradeItem().setLongPosition(
                                futurePositionWithGreaterMultiplier.getTradeItem().getLongPosition() * conversionFactor
                        );
                        futurePositionWithGreaterMultiplier.getTradeItem().setShortPosition(
                                futurePositionWithGreaterMultiplier.getTradeItem().getShortPosition() * conversionFactor
                        );

                        // Net positions
                        double nettedLongPosition = 0;
                        double nettedShortPosition = 0;

                        log.info(" Compute Netted Long/Short position for this list of MarginPositionItem = " +
                                marginPositionItems);

                        for (MarginPositionItem item : marginPositionItems) {
                            nettedLongPosition += item.getTradeItem().getLongPosition();
                            nettedShortPosition += item.getTradeItem().getShortPosition();
                        }
                        double nettedNetPosition = nettedShortPosition - nettedLongPosition;

                        log.info(" NettedLongPosition = " + nettedLongPosition + ", NettedShortPosition = " +
                                nettedShortPosition + ", NettedNetPosition = " + nettedNetPosition);

                        futurePositionWithSmallerMultiplier.getTradeItem().setLongPosition(nettedLongPosition);
                        futurePositionWithSmallerMultiplier.getTradeItem().setShortPosition(nettedShortPosition);
                        futurePositionWithSmallerMultiplier.getTradeItem().setNetPosition(nettedNetPosition);

                        futureMarginPositionsNetted.add(futurePositionWithSmallerMultiplier);

                    } else {
                        //No Netting
                        log.info("No netting needed, only one position items = " + marginPositionItems.toString());
                        futureMarginPositionsNetted.addAll(marginPositionItems);
                    }
                } );

        log.info(" Future Margin Positions netted are : " );
        futureMarginPositionsNetted.forEach(
                (marginPositionItem) -> {
                    log.info(" --- " + marginPositionItem.getTradeItem().toString());
                }
        );

        // *************************************************************************************************************
        // Calculate Future Spread margin
        // *************************************************************************************************************
        // Sum the total of all net long futures positions in the class and the total
        // of all net short futures positions in the class.

        Map<MarginPositionAggregationKey, List<MarginPositionItem>> futuresMarginPositionsGroupByClassMap = futureMarginPositionsNetted.stream()
        .collect(Collectors.groupingBy(p -> new MarginPositionAggregationKey(
                p.getTradeItem().getAccountid(),
                p.getClassFileItem().getClassGroup(),
                p.getClassFileItem().getClassType(),
                null,
                null,
                null,
                null)));

        List<FuturesSpreadMargin> futuresSpreadMargins = new ArrayList<>();
        futuresMarginPositionsGroupByClassMap.forEach(
                (marginPositionAggregationKey, marginPositionItems) -> {
                    log.info(marginPositionItems.toString());

                    // Calculate Total Spread Quantity (Long & Short) for the class
                    double totalClassSpreadLongQuantity = 0;
                    double totalClassSpreadShortQuantity = 0;
                    for (MarginPositionItem item : marginPositionItems) {
                        totalClassSpreadLongQuantity += item.getTradeItem().getLongPosition();
                        totalClassSpreadShortQuantity += item.getTradeItem().getShortPosition();
                    }
                    double totalClassSpreadQuantity = totalClassSpreadShortQuantity - totalClassSpreadLongQuantity;

                    //Calculate the total non spread contrat quantity for the class
                    double totalClassNonSpreadContractQuantity = totalClassSpreadShortQuantity -
                            totalClassSpreadLongQuantity;

                    // Looking for the spot month (nearest future contact)
                    MarginPositionItem spotMonthFuturesPosition =
                            marginPositionItems.stream()
                                    .min((p1, p2) -> Double.compare(
                                            p1.getTradeItem().getExpYear() * 100 + p1.getTradeItem().getExpMonth(),
                                            p2.getTradeItem().getExpYear() * 100 + p2.getTradeItem().getExpMonth()))
                                    .get();

                    // Calculate SpotMontSpreadQuantity
                    double spotMonthSpreadQuantity = 0;
                    if (spotMonthFuturesPosition.getTradeItem().getNetPosition() < 0) { // Long Position
                        if (spotMonthFuturesPosition.getTradeItem().getLongPosition() <= totalClassSpreadLongQuantity) {
                            spotMonthSpreadQuantity = spotMonthFuturesPosition.getTradeItem().getLongPosition();
                        } else {
                            spotMonthSpreadQuantity = totalClassSpreadLongQuantity;
                        }
                    } else { // short Position
                        if (spotMonthFuturesPosition.getTradeItem().getShortPosition() <= totalClassSpreadShortQuantity) {
                            spotMonthSpreadQuantity = spotMonthFuturesPosition.getTradeItem().getShortPosition();
                        } else {
                            spotMonthSpreadQuantity = totalClassSpreadShortQuantity;
                        }
                    }

                    double nonSpotSpreadContractQuantity = totalClassSpreadQuantity - spotMonthSpreadQuantity;

                    double classSpotMonthSpreadMargin = spotMonthSpreadQuantity *
                            spotMonthFuturesPosition.getClassFileItem().getNonSpotSpreadRate();
                    double classNonSpotMonthSpreadMargin = nonSpotSpreadContractQuantity *
                            spotMonthFuturesPosition.getClassFileItem().getSpotSpreadRate();

                    // Spread Margin Requirement for the Futures - Maturity
                    double totalClassSpreadMarginRequirement = classSpotMonthSpreadMargin +
                            classNonSpotMonthSpreadMargin;

                    futuresSpreadMargins.add(new FuturesSpreadMargin(
                            marginPositionItems.get(0).getClassFileItem().getProductGroup(),
                            marginPositionItems.get(0).getClassFileItem().getClassGroup(),
                            totalClassSpreadMarginRequirement,      // Spread Margin requirements
                            totalClassNonSpreadContractQuantity)     // Non Spread contract quantity
                    );
                }
        );

        log.info(" Future Spread Margin are : " );
        futuresSpreadMargins.forEach(
                (futuresSpreadMargin) -> {
                    log.info(" --- " + futuresSpreadMargin.toString());
                }
        );

        return futuresSpreadMargins;
    }

    /*
     * Mark-To-Market Margin: calculated for securities positions and for stock futures
     * positions which have been expired and have not yet been settled. It has the
     * purpose of revaluating the theoretical liquidation gains/losses to current market
     * prices (Mark-To-Market). For equity positions it represents a theoretical credit for
     * the clearing member that has bought/sold shares below/above current market
     * prices, assumed equal to the reference price. On the other side, it represents a
     * theoretical debit for the clearing member that has bought (sold) shares above
     * (below) current market prices. For stock Future positions it represents a theoretical
     * credit (debit) for the member who bought the futures if the settlement price is lower
     * (higher) than the current market value, set equal to the reference price of the of the
     * underlying, and vice versa for the member that sold the futures.
     */
    public void computeMarktoMarketMargin() {

    }

    /*
     * Premium Margins: calculated only for stock-style options. It has the purpose of
     * revaluating the theoretical liquidation costs/revenues at the current market values
     * (market-to-market) and therefore represents a theoretical credit for the options
     * buyer and a theoretical debt for the seller. It is equal to the current market value of
     * the option itself, assumed equal to the closing price that is calculated daily.
     */
    public void premiumMargin() {

    }

    /*
     * Additional Margin: calculated for all securities positions, option positions and nonspread
     * futures positions. It has the purpose of evaluating the maximum loss
     * reasonably possible in the hypothesis of market price fluctuations of the underlying
     * asset. If the risk margin component of a particular product group is less than a
     * calculated minimum margin for the product group, then the minimum margin will be
     * taken as the additional margin.
     */
    public void computeAdditionalMargin () {

    }

}
