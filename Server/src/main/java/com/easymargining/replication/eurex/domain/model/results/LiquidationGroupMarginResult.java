package com.easymargining.replication.eurex.domain.model.results;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gmarchal on 22/02/2016.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LiquidationGroupMarginResult implements Serializable {
    // Liquidation Group Name
    private String liquidationGroup;
    // IM for a Liquidation Group
    private Double imGroupResult;
    // IM for a Liquidation Group Split
    private List<LiquidationGroupSplitMarginResult> liquidationGroupSplitMarginResult;
}
