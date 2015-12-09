package com.easymargining.replication.ccg.market;

import com.easymargining.replication.ccg.common.ClassTypeEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by Gilles Marchal on 07/12/2015.
 */

@Getter
@Setter
@EqualsAndHashCode
@ToString
/*
<?xml version="1.0"?>
	<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" >
	<xsl:output method="text" omit-xml-declaration="yes" indent="no"/>
	<xsl:template match="/">Symbol,Description,Class-Group,Product-Group,Class-Type,Product-Type,Offset,Spot-Spread-Rate,Non-Spot-Spread-Rate,Delivery-Rate,Multiplier,Product-Style,Underlying-Code,CMV,Underlying-Isin-Code,Margin-Inteval,Currency,Exchange-Rate,Currency-Haircut,Minimum-Margin,Interest-Rate,Days-To-Settle,Expiry-Time,Market-Id,SubType,VM-Multiplier
	<xsl:for-each select="//Data">
	<xsl:value-of select="concat(Symbol,',',Description,',',Class-Group,',',Product-Group,',',Class-Type,',',Product-Type,',',Offset,',',Spot-Spread-Rate,',',Non-Spot-Spread-Rate,',',Delivery-Rate,',',Multiplier,',',Product-Style,',',Underlying-Code,',',CMV,',',Underlying-Isin-Code,',',Margin-Inteval,',',Currency,',',Exchange-Rate,',',Currency-Haircut,',',Minimum-Margin,',',Interest-Rate,',',Days-To-Settle,',',Expiry-Time,',',Market-Id,',',SubType,',',VM-Multiplier,'&#xA;')"/>
	</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
 */
@XmlRootElement(name="Data")
@XmlAccessorType(XmlAccessType.NONE)
public class ClassFileItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name="Symbol")
    private String symbol;

    @XmlElement(name="Description")
    private String description;

    @XmlElement(name="Class-Group")
    private String classGroup;

    @XmlElement(name="Product-Group")
    private String productGroup;

    @XmlElement(name="Class-Type")
    private String classType;

    @XmlElement(name="Product-Type")
    private String productType;

    @XmlElement(name="offset")
    private int Offset;

    @XmlElement(name="Spot-Spread-Rate")
    private double spotSpreadRate;

    @XmlElement(name="Non-Spot-Spread-Rate")
    private double nonSpotSpreadRate;

    @XmlElement(name="Delivery-Rate")
    private double deliveryRate;

    @XmlElement(name="Multiplier")
    private double Multiplier;

    @XmlElement(name="Product-Style")
    private String productStyle;

    @XmlElement(name="Underlying-Code")
    private String underlyingCode;

    @XmlElement(name="CMV")
    private double cmv;

    @XmlElement(name="Underlying-Isin-Code")
    private String underlyingIsinCode;

    @XmlElement(name="Margin-Inteval")
    private String marginInteval;

    @XmlElement(name="Currency")
    private String currency;

    @XmlElement(name="Exchange-Rate")
    private String exchangeRate;

    @XmlElement(name="Currency-Haircut")
    private int currencyHaircut;

    @XmlElement(name="Minimum-Margin")
    private double minimumMargin;

    @XmlElement(name="Interest-Rate")
    private double interestRate;

    @XmlElement(name="Days-To-Settle")
    private int daysToSettle;

    @XmlElement(name="Expiry-Time")
    private int expiryTime;

    @XmlElement(name="Market-Id")
    private int marketId;

    @XmlElement(name="SubType")
    private String subType;

    @XmlElement(name="VM-Multiplier")
    private double vmMultiplier;

    public ClassTypeEnum getClassType() {
        return ClassTypeEnum.valueOf(ClassTypeEnum.getEnumKey(classType));
    }
}
