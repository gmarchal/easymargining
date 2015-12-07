package com.easymargining.replication.ccg.trade;

import com.univocity.parsers.annotations.EnumOptions;
import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.conversions.EnumSelector;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by Gilles Marchal on 07/12/2015.
 */

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class CcgMarsMarginTradeItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Parsed(field = "AccountID")
    private String accountid;
    @Parsed(field = "ClassType")
    @EnumOptions(customElement = "_classType", selectors = { EnumSelector.CUSTOM_FIELD })
    private ClassType classType;
    @Parsed(field = "Symbol")
    private String symbol;
    @Parsed(field = "ExpirationYear")
    private String expYear;
    @Parsed(field = "ExpirationMonth")
    private String expMonth;
    @Parsed(field = "StrikePrice")
    private String strikePrice;
    @Parsed(field = "PutOrCall")
    @EnumOptions(customElement = "_optionType", selectors = { EnumSelector.CUSTOM_FIELD })
    private OptionType optionType;
    @Parsed(field = "LongPosition")
    private String longPosition;
    @Parsed(field = "ShortPosition")
    private String shortPosition;
    @Parsed(field = "DVPDate")
    private String dvpDate;
    @Parsed(field = "DVPAmount")
    private String dvpAmount;

}