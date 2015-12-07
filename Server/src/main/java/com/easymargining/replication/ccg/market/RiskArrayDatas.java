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
import java.util.ArrayList;
import java.util.List;

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
	<xsl:template match="/">Date,Class-Type,Symbol,Year,Month,Strike-Price,P-C,Isin-Code,Mark-Price,Downside5,Downside4,Downside3,Downside2,Downside1,Upside1,Upside2,Upside3,Upside4,Upside5,Short-Adjustment,Volatility,Open-Interest,Cleared-Volume,Market-Id,Market-Id
		<xsl:for-each select="//Data">
			<xsl:value-of select="concat(Date,',',Class-Type,',',Symbol,',',Year,',',Month,',',Strike-Price,',',P-C,',',Isin-Code,',',Mark-Price,',',Downside5,',',Downside4,',',Downside3,',',Downside2,',',Downside1,',',Upside1,',',Upside2,',',Upside3,',',Upside4,',',Upside5,',',Short-Adjustment,',',Volatility,',',Open-Interest,',',Cleared-Volume,',',Market-Id,',',Currency,'&#xA;')"/>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
 */
@XmlRootElement(name="Flow")
@XmlAccessorType(XmlAccessType.NONE)
public class RiskArrayDatas implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name="Data")
    private List<RiskArrayItem> riskArrayDatas = new ArrayList<RiskArrayItem>();
}
