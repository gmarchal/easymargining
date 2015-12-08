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
import java.util.Date;

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
    private Date date;

    @XmlElement(name="Class-Type")
    private String classType;

    @XmlElement(name="Symbol")
    private String symbol;

    @XmlElement(name="Year")
    private int year;

    @XmlElement(name="Month")
    private int month;

    @XmlElement(name="Strike-Price")
    private double strikePrice;

    @XmlElement(name="P-C")
    private String optionType;

    @XmlElement(name="Isin-Code")
    private String isinCode;

    @XmlElement(name="Mark-Price")
    private double markPrice;

    @XmlElement(name="Downside5")
    private double downside5;

    @XmlElement(name="Downside4")
    private double downside4;

    @XmlElement(name="Downside3")
    private double downside3;

    @XmlElement(name="Downside2")
    private double downside2;

    @XmlElement(name="Downside1")
    private double downside1;

    @XmlElement(name="Upside1")
    private double Upside1;

    @XmlElement(name="Upside2")
    private double Upside2;

    @XmlElement(name="Upside3")
    private double Upside3;

    @XmlElement(name="Upside4")
    private double Upside4;

    @XmlElement(name="Upside5")
    private double Upside5;

    @XmlElement(name="Short-Adjustment")
    private double shortAdjustment;

    @XmlElement(name="Volatility")
    private double volatility;

    @XmlElement(name="Open-Interest")
    private double openInterest;

    @XmlElement(name="Cleared-Volume")
    private double clearedVolume;

    @XmlElement(name="Market-Id")
    private int marketId;

    @XmlElement(name="Currency")
    private String currency;

}
