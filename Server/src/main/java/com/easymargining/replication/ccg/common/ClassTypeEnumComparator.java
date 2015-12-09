package com.easymargining.replication.ccg.common;

import java.util.Comparator;

/**
 * Created by Gilles Marchal on 09/12/2015.
 */
public class ClassTypeEnumComparator implements Comparator<ClassTypeEnum>
{

    // Order : Futures 'F', Options 'O', Securities 'C'
    public int compare(ClassTypeEnum o1, ClassTypeEnum o2)
    {
        return getValue(o1) - getValue(o2);
    }

    private int getValue(ClassTypeEnum o1) {
        int value;
        switch (o1) {
            case FUTURES:
                value = 1;
                break;
            case OPTIONS:
                value = 2;
                break;
            case EQUITIES:
                value = 3;
                break;
            case CONVERTIBLE_BONDS:
                value = 4;
                break;
            case WARRANTS:
                value = 5;
                break;
            default:
                value = 6;
        }
        return value;
    }
}

