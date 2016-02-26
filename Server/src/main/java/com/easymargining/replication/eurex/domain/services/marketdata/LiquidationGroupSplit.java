package com.easymargining.replication.eurex.domain.services.marketdata;

import lombok.*;

import java.io.Serializable;

/**
 * Created by Gilles Marchal on 25/02/2016.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LiquidationGroupSplit implements Serializable {

    private String name;

}
