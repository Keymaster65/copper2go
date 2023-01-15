<?xml version="1.0" encoding="UTF-8" ?>
<!--
 * Copyright 2021 Wolf Sluyterman van Langeweyde
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
-->
<xsl:transform
        xmlns:a="http://keymaster65.github.io/architecture"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        version="3.0"
>
    <xsl:output method="text"/>
    <xsl:param name="level">4</xsl:param>
    <xsl:param name="hiddenApplications"></xsl:param>

    <xsl:template match="/">
        <xsl:text>@startuml</xsl:text>
        <xsl:apply-templates select="a:architecture/a:deployment"/>
        <xsl:text>&#xa;@enduml</xsl:text>
    </xsl:template>

    <xsl:template priority="10.0" match="a:nodes">
        <xsl:apply-templates/>
        <xsl:apply-templates mode="uses" select="//a:uses[not(contains($hiddenApplications, ../@applicationRef))]"/>
    </xsl:template>

    <xsl:template priority="10.0" match="a:node[not(descendant-or-self::a:node/@applicationRef) or not(contains($hiddenApplications, descendant-or-self::a:node/@applicationRef))]">
        <xsl:text>&#xA;</xsl:text>
        <xsl:value-of select="local-name()"/>
        <xsl:text> </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:if test="@href">
            <xsl:value-of select="concat(' [[', @href, ']]')"/>
        </xsl:if>
        <xsl:if test="a:node|@applicationRef">
            <xsl:variable name="applicationRef" select="@applicationRef"/>
            <xsl:variable name="application" select="/a:architecture/a:applications/a:application[@name=$applicationRef]"/>
            <xsl:text> {</xsl:text>
            <xsl:apply-templates select="$application"/>
            <xsl:apply-templates select="a:node"/>
            <xsl:text>&#xA;}</xsl:text>
        </xsl:if>
    </xsl:template>



    <xsl:template match="a:processorPool[not(@level) or @level = '' or $level>=@level]|a:process[not(@level) or @level = '' or $level>=@level]">
        <xsl:text>&#xA;node </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:if test="@href">
            <xsl:value-of select="concat(' [[', @href, ']]')"/>
        </xsl:if>
        <xsl:choose>
            <xsl:when test="a:process">
                <xsl:text> {</xsl:text>
                <xsl:apply-templates select="a:process"/>
                <xsl:text>&#xA;}</xsl:text>
                <xsl:apply-templates select="*[local-name() != 'process']"/>
            </xsl:when>
            <xsl:otherwise><xsl:apply-templates/></xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="a:application[not(@level) or @level = '' or $level>=@level]">
        <xsl:text>&#xA;node </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:if test="@href">
            <xsl:value-of select="concat(' [[', @href, ']]')"/>
        </xsl:if>
        <xsl:text> {</xsl:text>
        <xsl:apply-templates/>
        <xsl:text>&#xA;}</xsl:text>
    </xsl:template>

    <xsl:template match="a:architecture">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template priority="10.0" match="a:uses" mode="uses">
        <xsl:text>&#xA;</xsl:text>
        <xsl:value-of select="../@name"/>
        <xsl:text> </xsl:text>
        <xsl:choose>
            <xsl:when test="@as='consumer'">..(0</xsl:when>
            <xsl:when test="@as='user'">..^</xsl:when>
            <xsl:otherwise>..</xsl:otherwise>
        </xsl:choose>
        <xsl:text> </xsl:text>
        <xsl:variable name="ref" select="@ref"/>
        <xsl:value-of select="//*[@name=$ref]/ancestor-or-self::*[@name and (not(@level) or @level = '' or $level>=@level)][1]/@name"/>
    </xsl:template>

    <xsl:template match="a:deployment|a:nodes|a:processorPools|a:packages|a:components|a:uses|a:node" priority="5.0">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="a:*[not(@level) or @level = '' or $level>=@level]" priority="0.0">
        <xsl:text>&#xA;</xsl:text>
        <xsl:value-of select="local-name()"/>
        <xsl:text> </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:if test="@href">
            <xsl:value-of select="concat(' [[', @href, ']]')"/>
        </xsl:if>
        <xsl:if test="a:*">
            <xsl:text> {</xsl:text>
            <xsl:apply-templates/>
            <xsl:text>&#xA;}</xsl:text>
        </xsl:if>
    </xsl:template>

    <xsl:template match="node()"/>

</xsl:transform>