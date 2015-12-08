<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" >
	<xsl:output method="text" omit-xml-declaration="yes" indent="no"/>
	<xsl:template match="/">Date,Class-Type,Symbol,Year,Month,Strike-Price,P-C,Isin-Code,Mark-Price,Downside5,Downside4,Downside3,Downside2,Downside1,Upside1,Upside2,Upside3,Upside4,Upside5,Short-Adjustment,Volatility,Open-Interest,Cleared-Volume,Market-Id,Market-Id
		<xsl:for-each select="//Data">
			<xsl:value-of select="concat(Date,',',Class-Type,',',Symbol,',',Year,',',Month,',',Strike-Price,',',P-C,',',Isin-Code,',',Mark-Price,',',Downside5,',',Downside4,',',Downside3,',',Downside2,',',Downside1,',',Upside1,',',Upside2,',',Upside3,',',Upside4,',',Upside5,',',Short-Adjustment,',',Volatility,',',Open-Interest,',',Cleared-Volume,',',Market-Id,',',Currency,'&#xA;')"/>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>