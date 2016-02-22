package com.easymargining.replication.eurex.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Created by Gilles Marchal on 16/02/2016.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Product implements Serializable  {

    private static final long serialVersionUID = 1L;

    @Id
    private String _id;

    private LocalDate effectiveDate;

    // Trade fields
    private String productId;

    private Integer contractYear;

    private Integer contractMonth;

    private Integer versionNumber;

    private String settlementType;

    private String optionType;          //Call or Put

    private Double exercisePrice;

    private String exerciseStyleFlag;

    private String instrumentType;

}
