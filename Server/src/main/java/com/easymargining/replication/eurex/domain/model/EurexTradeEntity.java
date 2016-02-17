package com.easymargining.replication.eurex.domain.model;

import com.opengamma.margining.eurex.prisma.replication.trade.parsers.EurexEtdTradeFileReader;
import com.univocity.parsers.annotations.EnumOptions;
import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.conversions.EnumSelector;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * Trade entity for REST service
 * Created by Mederic on 21/11/2015.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class EurexTradeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Parsed(field = "Product ID")
    private String productId;
    @Parsed(field = "Expiry Year")
    private Integer expiryYear;
    @Parsed(field = "Expiry Month")
    private Integer expiryMonth;
    @Parsed(field = "Expiry Day")
    private Integer expiryDay;
    @Parsed(field = "Version Number")
    private String versionNumber;
    @Parsed(field = "Product Settlement Type")
    private String productSettlementType;
    @Parsed(field = "Call Put Flag")
    private String callPutFlag;
    @Parsed(field = "Exercise Price")
    private Double exercisePrice;
    @Parsed(field = "Exercise Style Flag")
    @EnumOptions(selectors = { EnumSelector.STRING })
    private EurexEtdTradeFileReader.ExerciseStyle exerciseStyleFlag;
    @Parsed(field = "Instrument Type")
    @EnumOptions(customElement = "_name", selectors = { EnumSelector.CUSTOM_FIELD })
    private EurexEtdTradeFileReader.InstrumentType instrumentType;
    @Parsed(field = "Assigned/Notified Balance")
    private String assignedNotifiedBalance;
    @Parsed(field = "Exercised/Allocated Balance")
    private String exercisedAllocatedBalance;
    @Parsed(field = "Long Balance")
    private String longBalance;
    @Parsed(field = "Short Balance")
    private String shortBalance;
}
