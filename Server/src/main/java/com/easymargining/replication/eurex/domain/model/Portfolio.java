package com.easymargining.replication.eurex.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * Created by Gilles Marchal on 12/01/2016.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Portfolio implements Serializable {

    @Id
    private String _id;

    private String name;

    //private String account;

    private String ownerId;

}
