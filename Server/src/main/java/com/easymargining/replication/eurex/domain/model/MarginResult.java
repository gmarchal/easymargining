package com.easymargining.replication.eurex.domain.model;

import lombok.*;

import java.io.Serializable;

/**
 * Created by Gilles Marchal on 21/01/2016.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MarginResult implements Serializable {

    private String imResult;
}
