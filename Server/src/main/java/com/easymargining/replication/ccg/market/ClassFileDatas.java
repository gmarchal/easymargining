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
	<xsl:template match="/">Symbol,Description,Class-Group,Product-Group,Class-Type,Product-Type,Offset,Spot-Spread-Rate,Non-Spot-Spread-Rate,Delivery-Rate,Multiplier,Product-Style,Underlying-Code,CMV,Underlying-Isin-Code,Margin-Inteval,Currency,Exchange-Rate,Currency-Haircut,Minimum-Margin,Interest-Rate,Days-To-Settle,Expiry-Time,Market-Id,SubType,VM-Multiplier
	<xsl:for-each select="//Data">
	<xsl:value-of select="concat(Symbol,',',Description,',',Class-Group,',',Product-Group,',',Class-Type,',',Product-Type,',',Offset,',',Spot-Spread-Rate,',',Non-Spot-Spread-Rate,',',Delivery-Rate,',',Multiplier,',',Product-Style,',',Underlying-Code,',',CMV,',',Underlying-Isin-Code,',',Margin-Inteval,',',Currency,',',Exchange-Rate,',',Currency-Haircut,',',Minimum-Margin,',',Interest-Rate,',',Days-To-Settle,',',Expiry-Time,',',Market-Id,',',SubType,',',VM-Multiplier,'&#xA;')"/>
	</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
 */
@XmlRootElement(name="Flow")
@XmlAccessorType(XmlAccessType.NONE)
public class ClassFileDatas implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name="Data")
    private List<ClassFileItem> classFileDatas = new ArrayList<ClassFileItem>();

}
