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

    <xsl:template match="/">
        <xsl:text>@startuml</xsl:text>
        <xsl:apply-templates/>
        <xsl:text>&#xa;@enduml</xsl:text>
    </xsl:template>

    <xsl:template match="a:nodes">
        <xsl:apply-templates/>
        <xsl:apply-templates mode="uses" select="//a:uses"/>
    </xsl:template>

    <xsl:template match="a:node|a:folder">
        <xsl:text>&#xA;</xsl:text>
        <xsl:value-of select="local-name()"/>
        <xsl:text> </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:if test="@href">
            <xsl:value-of select="concat(' [[', @href, ']]')"/>
        </xsl:if>
        <xsl:if test="a:node|a:folder">
            <xsl:text> {</xsl:text>
            <xsl:apply-templates/>
            <xsl:text>&#xA;}</xsl:text>
        </xsl:if>
    </xsl:template>

    <xsl:template match="a:architecture">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="a:uses" mode="uses">
        <xsl:text>&#xA;</xsl:text>
        <xsl:value-of select="../@name"/>
        <xsl:text> </xsl:text>
        <xsl:choose>
            <xsl:when test="@as='consumer'">..(0</xsl:when>
            <xsl:when test="@as='user'">..^</xsl:when>
            <xsl:otherwise>..</xsl:otherwise>
        </xsl:choose>
        <xsl:text> </xsl:text>
        <xsl:value-of select="@ref"/>

    </xsl:template>

    <xsl:template match="node()"/>

</xsl:transform>