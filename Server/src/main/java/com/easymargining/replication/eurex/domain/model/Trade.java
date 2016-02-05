package com.easymargining.replication.eurex.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Gilles Marchal on 19/01/2015.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Trade implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String _id;

    private String portfolioId;

    // Trade fields
    private String productId;
    private Date expiryDate;
    private String versionNumber;
    private String productSettlementType;
    private String optionType;  //Call or Put
    private Double exercisePrice;
    private String exerciseStyleFlag;
    private String instrumentType;
    private Double quantity;

}
