package com.easymargining.replication.ccg.calc;

import com.easymargining.replication.ccg.market.ClassFileItem;
import com.easymargining.replication.ccg.market.RiskArrayItem;
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

class MarginPositionItem {

    private CcgMarsMarginTradeItem tradeItem;

    private ClassFileItem classFileItem;

    private RiskArrayItem riskArrayItem;

    public String toShortDescription() {
        return "MarginPositionItem{" +
                "AccountId=" + tradeItem.getAccountid() +
                ", ProductGroup=" + classFileItem.getProductGroup() +
                ", ClassGroup=" + classFileItem.getClassGroup() +
                ", ClassType=" + classFileItem.getClassType() +
                '}';
    }
}
