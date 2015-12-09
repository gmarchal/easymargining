package com.easymargining.replication.ccg.common;

/**
 * Created by Gilles Marchal on 07/12/2015.
 */
public enum OptionTypeEnum {

    PUT("P"),
    CALL("C");

    private final String _optionType;

    private OptionTypeEnum(String optionType) {
        this._optionType = optionType;
    }

    static OptionTypeEnum of(String optionType) {
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
