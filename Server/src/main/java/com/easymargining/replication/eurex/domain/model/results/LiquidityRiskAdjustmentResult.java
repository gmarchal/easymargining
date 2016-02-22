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
public class LiquidityRiskAdjustmentResult implements Serializable {
    //Liquidity Risk Adjustment for a Liquidation Group Split
    private Double totalLiquidityRiskAdjustmentAddOn;
    //Liquidity Risk Adjustment for the OTC part of a Liquidation Group Split
    private Double otcliquidityRiskAdjustmentAddOnPart;
    //Liquidity Risk Adjustment for the ETD part of a Liquidation Group Split
    private Double etdliquidityRiskAdjustmentAddOnPart;
    // Long Option Credit for a Liquidation Group Split
    private Double longOptionCreditAddOn;
}
