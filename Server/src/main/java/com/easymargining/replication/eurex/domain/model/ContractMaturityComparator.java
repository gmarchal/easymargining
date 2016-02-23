package com.easymargining.replication.eurex.domain.model;

import java.util.Comparator;

/**
 * Created by gmarchal on 23/02/2016.
 */
public class ContractMaturityComparator implements Comparator<ContractMaturity> {

    @Override
    public int compare(ContractMaturity o1, ContractMaturity o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }
        if (o1.getContractYear() != null) {
            if (o1.getContractYear().equals(o2.getContractYear())) {
                return o1.getContractMonth().compareTo(o2.getContractMonth());
            } else {
                return o1.getContractYear().compareTo(o2.getContractYear());
            }
        } else if (o1.getContractMonth() != null) {
            return o1.getContractMonth().compareTo(o2.getContractMonth());
        } else if ( o1.getContractYear() == null && o1.getContractMonth() == null ){
            return 0;
        } else {
            return 1;
        }
    }
}

