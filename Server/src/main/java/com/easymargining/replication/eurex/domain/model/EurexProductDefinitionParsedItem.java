package com.easymargining.replication.eurex.domain.model;

import com.univocity.parsers.annotations.Parsed;
import lombok.*;

import java.io.Serializable;

/**
 * Created by Gilles Marchal on 20/02/2016.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EurexProductDefinitionParsedItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Parsed(field = "Eurex Code")
    private String eurexCode;
    @Parsed(field = "Type")
    private String type;
    @Parsed(field = "Product Name")
    private String productName;
    @Parsed(field = "Code & Link")
    private String bbgCode;
    @Parsed(field = "CUR")
    private String currency;
    @Parsed(field = "Product ISIN")
    private String isinCode;
    @Parsed(field = "Tick Size")
    private String tickSize;
    @Parsed(field = "Trad Unit")
    private String tradUnit;
    @Parsed(field = "Tick Value")
    private String tickValue;
    @Parsed(field = "Min Block Size")
    private String minBlockSize;
    @Parsed(field = "Settlement Type")
    private String settlementType;
}
