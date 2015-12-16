package com.easymargining.replication.ccg.common;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Gilles Marchal on 09/12/2015.
 */
@Slf4j
public class ClassTypeEnumComparatorTest {

    @Test
    public void testCompare() throws Exception {

        List<ClassTypeEnum> listOfClassType = new ArrayList<>(10);
        listOfClassType.add(0, ClassTypeEnum.EQUITIES);
        listOfClassType.add(1, ClassTypeEnum.OPTIONS);
        listOfClassType.add(2, ClassTypeEnum.FUTURES);
        listOfClassType.add(3, ClassTypeEnum.FUTURES);
        listOfClassType.add(4, ClassTypeEnum.CONVERTIBLE_BONDS);
        listOfClassType.add(5, ClassTypeEnum.WARRANTS);
        listOfClassType.add(6, ClassTypeEnum.OPTIONS);
        listOfClassType.add(7, ClassTypeEnum.WARRANTS);
        listOfClassType.add(8, ClassTypeEnum.EQUITIES);
        listOfClassType.add(9, ClassTypeEnum.CONVERTIBLE_BONDS);
        log.debug("List unordered of ClassType = " + listOfClassType);
        Collections.sort(listOfClassType, new ClassTypeEnumComparator());
        log.debug("List ordered of ClassType = " + listOfClassType);

        List<ClassTypeEnum> listOfOrderedClassType = new ArrayList<>(10);
        listOfOrderedClassType.add(0, ClassTypeEnum.FUTURES);
        listOfOrderedClassType.add(1, ClassTypeEnum.FUTURES);
        listOfOrderedClassType.add(2, ClassTypeEnum.OPTIONS);
        listOfOrderedClassType.add(3, ClassTypeEnum.OPTIONS);
        listOfOrderedClassType.add(4, ClassTypeEnum.EQUITIES);
        listOfOrderedClassType.add(5, ClassTypeEnum.EQUITIES);
        listOfOrderedClassType.add(6, ClassTypeEnum.CONVERTIBLE_BONDS);
        listOfOrderedClassType.add(7, ClassTypeEnum.CONVERTIBLE_BONDS);
        listOfOrderedClassType.add(8, ClassTypeEnum.WARRANTS);
        listOfOrderedClassType.add(9, ClassTypeEnum.WARRANTS);

        log.info("List of ClassType after ordering = " + listOfClassType);
        log.info("List ordered of ClassType = " + listOfOrderedClassType);

        Assert.isTrue(listOfClassType.toString().equals(listOfOrderedClassType.toString()));
    }
}