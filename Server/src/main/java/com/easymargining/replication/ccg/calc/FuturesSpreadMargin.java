package com.easymargining.replication.ccg.calc;

import lombok.*;

/**
 * Created by Gilles Marchal on 11/12/2015.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class FuturesSpreadMargin {

    public String productGroup;
    public String classGroup;
    public double spreadMarginRequirement;
    public double nonSpreadContractQuantity;

}
