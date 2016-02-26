package com.easymargining.replication.eurex.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by gmarchal on 26/02/2016.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LiquidationGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String liquidationGroupName;

    private Set<String> liquidationGroupSplits;

}
