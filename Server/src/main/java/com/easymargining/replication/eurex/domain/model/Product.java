package com.easymargining.replication.eurex.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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
@Document
@CompoundIndexes({
        @CompoundIndex(name = "product_idx1", def = "{'productId': 1, 'contractYear': 1, 'contractMonth': 1}")
})
public class Product implements Serializable  {

    private static final long serialVersionUID = 1L;

    @Id
    private String _id;

    private LocalDate effectiveDate;

    // Trade fields
    @Indexed
    private String productId;

    private Integer contractYear;

    private Integer contractMonth;

    private Integer versionNumber;

    private String settlementType;

    @Indexed
    private String optionType;          //Call or Put

    private Double exercisePrice;

    private String exerciseStyleFlag;

    private String instrumentType;

}
