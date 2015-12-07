package com.easymargining.replication.ccg.market;

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

@XmlRootElement(name="Data")
@XmlAccessorType(XmlAccessType.NONE)
public class RiskArrayItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name="Date")
    private String date;

    @XmlElement(name="Class-Type")
    private String classType;

    @XmlElement(name="Symbol")
    private String symbol;

    @XmlElement(name="Year")
    private String year;

    @XmlElement(name="Month")
    private String month;

    @XmlElement(name="Strike-Price")
    private String strikePrice;

    @XmlElement(name="P-C")
    private String optionType;

    @XmlElement(name="Isin-Code")
    private String isinCode;

    @XmlElement(name="Mark-Price")
    private String markPrice;

    @XmlElement(name="Downside5")
    private String downside5;

    @XmlElement(name="Downside4")
    private String downside4;

    @XmlElement(name="Downside3")
    private String downside3;

    @XmlElement(name="Downside2")
    private String downside2;

    @XmlElement(name="Downside1")
    private String downside1;

    @XmlElement(name="Upside1")
    private String Upside1;

    @XmlElement(name="Upside2")
    private String Upside2;

    @XmlElement(name="Upside3")
    private String Upside3;

    @XmlElement(name="Upside4")
    private String Upside4;

    @XmlElement(name="Upside5")
    private String Upside5;

    @XmlElement(name="Short-Adjustment")
    private String shortAdjustment;

    @XmlElement(name="Volatility")
    private String volatility;

    @XmlElement(name="Open-Interest")
    private String openInterest;

    @XmlElement(name="Cleared-Volume")
    private String clearedVolume;

    @XmlElement(name="Market-Id")
    private String marketId;

    @XmlElement(name="Currency")
    private String currency;

}
