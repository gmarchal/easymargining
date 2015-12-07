package com.easymargining.replication.ccg.trade;

/**
 * Created by Gilles Marchal on 07/12/2015.
 */
public enum OptionType {

    PUT("P"),
    CALL("C");

    private final String _optionType;

    private OptionType(String optionType) {
        this._optionType = optionType;
    }

    static OptionType of(String optionType) {
        try {
            return valueOf(optionType);
        } catch (IllegalArgumentException var2) {
            throw new IllegalArgumentException("Invalid value for Option Type \'" + optionType + "\'");
        }
    }

    public String getShortName() {
        return this._optionType;
    }
}
