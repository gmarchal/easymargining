package com.easymargining.replication.ccg.trade;

import com.easymargining.replication.ccg.common.ClassTypeEnum;
import com.easymargining.replication.ccg.common.OptionTypeEnum;
import com.univocity.parsers.annotations.EnumOptions;
import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.conversions.EnumSelector;
import lombok.*;

import java.io.Serializable;

/**
 * Created by Gilles Marchal on 07/12/2015.
 */

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CcgMarsMarginTradeItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Parsed(field = "AccountID")
    private String accountid;
    @Parsed(field = "ClassType")
    @EnumOptions(customElement = "_classType", selectors = { EnumSelector.CUSTOM_FIELD })
    private ClassTypeEnum classType;
    @Parsed(field = "Symbol")
    private String symbol;
    @Parsed(field = "ExpirationYear")
    private Integer expYear;
    @Parsed(field = "ExpirationMonth")
    private Integer expMonth;
    @Parsed(field = "StrikePrice")
    private String strikePrice;
    @Parsed(field = "PutOrCall")
    @EnumOptions(customElement = "_optionType", selectors = { EnumSelector.CUSTOM_FIELD })
    private OptionTypeEnum optionType;
    @Parsed(field = "LongPosition")
    private Double longPosition;
    @Parsed(field = "ShortPosition")
    private Double shortPosition;
    @Parsed(field = "NetPosition")
    private Double netPosition;
    @Parsed(field = "DVPDate")
    private String dvpDate;
    @Parsed(field = "DVPAmount")
    private Double dvpAmount;

}
