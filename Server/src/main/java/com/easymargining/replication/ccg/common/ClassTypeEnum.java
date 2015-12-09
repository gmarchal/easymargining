package com.easymargining.replication.ccg.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gilles Marchal on 07/12/2015.
 */
public enum ClassTypeEnum {

    FUTURES("F"),
    OPTIONS("O"),
    EQUITIES("C"),
    CONVERTIBLE_BONDS("V"),
    WARRANTS("W");

    private final String _classType;
    private static Map<String, String> enumMap;

    private ClassTypeEnum(String classType) {
        this._classType = classType;
    }

    static ClassTypeEnum of(String classType) {
        try {
            return valueOf(getEnumKey(classType));
        } catch (IllegalArgumentException var2) {
            throw new IllegalArgumentException("Invalid value for Class Type \'" + classType + "\'");
        }
    }

    public String getShortName() {
        return this._classType;
    }

    public static String getEnumKey(String name) {
        if (enumMap == null) {
            initializeMap();
        }
        return enumMap.get(name);
    }

    private static Map<String, String> initializeMap() {
        enumMap = new HashMap<String, String>();
        for (ClassTypeEnum access : ClassTypeEnum.values()) {
            enumMap.put(access.getShortName(), access.toString());
        }
        return enumMap;
    }
}