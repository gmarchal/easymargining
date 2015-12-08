package com.easymargining.replication.ccg.calc;

import com.easymargining.replication.ccg.trade.CcgMarsMarginTradeItem;
import lombok.*;

/**
 * Created by Gilles Marchal on 08/12/2015.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor

class MarginPositions {

    private String classGroup;
    private String productGroup;

    private CcgMarsMarginTradeItem tradeItem;

}
