package com.socgen.finit.easymargin.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Trade entity for REST service
 * Created by Mederic on 21/11/2015.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class TradeEntity implements Serializable {

    private static final long serialVersionUID = 1879636427086123116L;

    private String productId;
    private String expiryDate;
    private String versionNumber;
    private String productSettlementType;
    private String callPutFlag;
    private Double exercisePrice;
    private String exerciseStyleFlag;
    private String instrumentType;
    private String assignedNotifiedBalance;
    private String exercisedAllocatedBalance;
    private String longBalance;
    private String shortBalance;

}
