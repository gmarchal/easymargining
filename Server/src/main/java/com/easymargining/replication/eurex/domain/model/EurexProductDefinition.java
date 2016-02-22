package com.easymargining.replication.eurex.domain.model;

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
    private String eurexCode;

    private String type;

    private String productName;

    private String bbgCode;

    private String currency;

    private String isinCode;

    private Double tickSize;

    private Double tradUnit;

    private Double tickValue;

    private Integer minBlockSize;

    private String settlementType;
}
