package com.easymargining.replication.ccg.trade;

/**
 * Created by Gilles Marchal on 07/12/2015.
 */
public enum  ClassType {

    FUTURES("F"),
    OPTIONS("O"),
    EQUITIES("C"),
    CONVERTIBLE_BONDS("V"),
    WARRANTS("W");

    private final String _classType;

    private ClassType(String classType) {
        this._classType = classType;
    }

    static ClassType of(String classType) {
        try {
            return valueOf(classType);
        } catch (IllegalArgumentException var2) {
            throw new IllegalArgumentException("Invalid value for Class Type \'" + classType + "\'");
        }
    }

    public String getShortName() {
        return this._classType;
    }

}
