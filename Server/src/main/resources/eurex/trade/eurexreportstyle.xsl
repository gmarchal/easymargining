<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="text"
                omit-xml-declaration="yes"
                encoding="iso-8859-1"
                indent="no"/>

    <xsl:template match="/">
        <xsl:text>Effective Date,Account Type Group,Product Id,Expiry Year,Expiry Month,Expiry Day,Version Number,Product Settlement Type, Call Put Flag,Exercise Price,Exercise Style Flag,Instrument Type,Assigned/Notified Balance,Exercise/Allocated Balance,Long Balance,Short Balance&#xA;</xsl:text>
        <xsl:variable name="effectivedate" select="//rptHdr/rptPrntEffDat" />

        <xsl:for-each select="//cb010Grp/cb010Grp1/cb010Grp2/cb010Grp3/cb010Grp4/cb010Grp5">

            <xsl:variable name="product_Id" select="ancestor::node()/cb010KeyGrp4/prodId" />
            <xsl:variable name="lngQty" select="//sumTrnLngQty" />
            <xsl:variable name="shtQty" select="//sumTrnShtQty" />

            <xsl:value-of select="concat(ancestor::node()/rptHdr/rptPrntEffDat,',',
                                         ancestor::node()/cb010KeyGrp3/acctTypGrp,',',
                                        ancestor::node()/cb010KeyGrp4/prodId,',',
                                        ancestor::node()/cb010Grp5/cb010KeyGrp5/cntrDtlClassGrp/cntrDtlGrp/cntrExpYrDat,',',
                                        ancestor::node()/cb010Grp5/cb010KeyGrp5/cntrDtlClassGrp/cntrDtlGrp/cntrExpMthDat,',,,,',
                                        ancestor::node()/cb010Grp5/cb010KeyGrp5/cntrDtlClassGrp/cntrClasCod,',',
                                        ancestor::node()/cb010Grp5/cb010KeyGrp5/cntrDtlClassGrp/cntrDtlGrp/cntrExerPrc)" />

            <xsl:choose>
                <xsl:when test="starts-with(ancestor::node()/cb010KeyGrp4/prodTypId,'O')">
                    <xsl:text>,,Option,</xsl:text>
                </xsl:when>
                <xsl:when test="starts-with(ancestor::node()/cb010KeyGrp4/prodTypId,'F')">
                    <xsl:text>,,Future,</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text></xsl:text>
                </xsl:otherwise>
            </xsl:choose>

            <xsl:value-of select="concat(',,',sumTrnLngQty,',',sumTrnShtQty,'&#xA;')"/>

        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>