package com.easymargining.replication.ccg.calc;

import com.easymargining.replication.ccg.common.ClassTypeEnum;
import com.easymargining.replication.ccg.common.OptionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by Gilles Marchal on 09/12/2015.
 */
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
public class MarginPositionAggregationKey {

    private String AccountId;
    private String ClassGroup;
    private ClassTypeEnum classType;
    private String expYear;
    private String expMonth;
    private String strikePrice;
    private OptionTypeEnum optionType;

}
