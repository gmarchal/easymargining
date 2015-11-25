package com.socgen.finit.easymargin.model;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import org.threeten.bp.LocalDate;

import java.util.List;

/**
 * Created by Gilles Marchal on 24/11/2015.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder(builderMethodName = "aRequest")
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Request implements Comparable<Request>{

    public enum PnlCalculatorMethodology {
        FULL_REVAL,
        DELTA,
        DELTA_GAMMA;
    }

    /**
     * Functional id to identify the request.
     */
    @JsonProperty(value = "id", required = false)
    private long id;

    /**
     * Valuation date
     */
    @JsonProperty(value = "valuationDate", required = true)
    private LocalDate valuationDate;

    @JsonProperty(value = "portfolioName", required = true)
    private String portfolioName;

    @JsonProperty(value = "tradeEntries", required = true)
    private List<TradeEntity> tradeEntries;

    @JsonProperty(value = "currency", required = true)
    private String currency;

    @JsonProperty(value = "otcPnlCalculatorMethodology", required = true)
    private PnlCalculatorMethodology otcPnlCalculatorMethodology;

    @JsonProperty(value = "crossMarginingEnabled", required = false)
    private boolean crossMarginingEnabled;

    @Override
    public int compareTo(final Request aValue) {
        return aValue == null ? -1 : Long.compare(id, aValue.id);
    }
}
