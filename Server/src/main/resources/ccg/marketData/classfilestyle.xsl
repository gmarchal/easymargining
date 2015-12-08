<?xml version="1.0"?>
	<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" >
	<xsl:output method="text" omit-xml-declaration="yes" indent="no"/>
	<xsl:template match="/">Symbol,Description,Class-Group,Product-Group,Class-Type,Product-Type,Offset,Spot-Spread-Rate,Non-Spot-Spread-Rate,Delivery-Rate,Multiplier,Product-Style,Underlying-Code,CMV,Underlying-Isin-Code,Margin-Inteval,Currency,Exchange-Rate,Currency-Haircut,Minimum-Margin,Interest-Rate,Days-To-Settle,Expiry-Time,Market-Id,SubType,VM-Multiplier
	<xsl:for-each select="//Data">
	<xsl:value-of select="concat(Symbol,',',Description,',',Class-Group,',',Product-Group,',',Class-Type,',',Product-Type,',',Offset,',',Spot-Spread-Rate,',',Non-Spot-Spread-Rate,',',Delivery-Rate,',',Multiplier,',',Product-Style,',',Underlying-Code,',',CMV,',',Underlying-Isin-Code,',',Margin-Inteval,',',Currency,',',Exchange-Rate,',',Currency-Haircut,',',Minimum-Margin,',',Interest-Rate,',',Days-To-Settle,',',Expiry-Time,',',Market-Id,',',SubType,',',VM-Multiplier,'&#xA;')"/>
	</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>