package com.easymargining.replication.eurex.domain.model.results;

import lombok.*;

import java.io.Serializable;

/**
 * Created by gmarchal on 22/02/2016.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MarketRiskIMResult implements Serializable {
    // Market Risk IM for a Liquidation Group Split
    private Double marketRiskIM;
    // Stress VaR for a Liquidation Group Split
    private Double stressVaR;
    // Filtered Historical Sub VaR for a Liquidation Group Split
    private Double filteredHistoricalVar;
}
