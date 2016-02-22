package com.easymargining.replication.eurex.domain.model.results;

import lombok.*;

import java.io.Serializable;
import java.util.List;

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

    // List of IMResult by currency.
    private List<PortfolioMarginResult> portfolioMarginResults;

}
