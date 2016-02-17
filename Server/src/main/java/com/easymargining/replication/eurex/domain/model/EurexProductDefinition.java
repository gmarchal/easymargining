package com.easymargining.replication.eurex.domain.model;

import com.univocity.parsers.annotations.Parsed;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * Created by gmarchal on 17/02/2016.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EurexProductDefinition implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
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
    private Double tickSize;
    @Parsed(field = "Trad Unit")
    private Double tradUnit;
    @Parsed(field = "Tick Value")
    private Double tickValue;
    @Parsed(field = "Min Block Size")
    private Integer minBlockSize;
    @Parsed(field = "Settlement Type")
    private String settlementType;
}
