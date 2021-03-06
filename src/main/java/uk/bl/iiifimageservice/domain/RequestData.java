/**
* Copyright (c) 2014, The British Library Board
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
* Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
*   in the documentation and/or other materials provided with the distribution.
* Neither the name of The British Library nor the names of its contributors may be used to endorse or promote products
*   derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
*   INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
*   IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
*   OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
*   OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, 
*   OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
*   EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package uk.bl.iiifimageservice.domain;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

public class RequestData {

    public static final String FULL_LITERAL = "full";
    public static final String PERCENTAGE_LITERAL = "pct:";
    public static final String REQUEST_DELIMITER = ",";

    @NotNull
    private String identifier;

    @NotNull
    private String region;

    @NotNull
    private String size;

    @NotNull
    private Integer rotation;

    @NotNull
    private String quality;

    private String format;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getRotation() {
        return rotation;
    }

    public void setRotation(Integer rotation) {
        this.rotation = rotation;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isRegionFull() {
        return region.equals(FULL_LITERAL);
    }

    public boolean isRegionPercentage() {
        return region.startsWith(PERCENTAGE_LITERAL);
    }

    public boolean isRegionAbsolute() {
        return Character.isDigit(region.charAt(0));
    }

    /**
     * A size value of 'full' is specified. No resizing occurs once the image region has been extracted.
     * 
     * @return
     */
    public boolean isSizeFull() {
        return size.equals(FULL_LITERAL);
    }

    /**
     * Only the width of the size is specified in the request
     * 
     * @return
     */
    public boolean isSizeHeightDeterminedByWidth() {
        return size.endsWith(REQUEST_DELIMITER);
    }

    /**
     * Only the height of the size is specified in the request
     * 
     * @return
     */
    public boolean isSizeWidthDeterminedByHeight() {
        return size.startsWith(REQUEST_DELIMITER);
    }

    /**
     * The size scale is specified by a percentage value in the request
     * 
     * @return
     */
    public boolean isSizePercentage() {
        return size.startsWith(PERCENTAGE_LITERAL);
    }

    public BigDecimal getSizePercentage() {
        return new BigDecimal(removePercentageLiteral(size));
    }

    public BigDecimal getSizePercentageAsDecimal() {
        return new BigDecimal(removePercentageLiteral(size)).movePointLeft(2);
    }

    public boolean isSizeBestFit() {
        return size.startsWith("!");
    }

    /**
     * Both the width and height are explicitly set in the request
     * 
     * @return
     */
    public boolean isSizeAbsolute() {
        return !(isSizeFull() || isSizeWidthDeterminedByHeight() || isSizeHeightDeterminedByWidth()
                || isSizePercentage() || isSizeBestFit());
    }

    private String removePercentageLiteral(String value) {
        return value.substring(PERCENTAGE_LITERAL.length());
    }

    public boolean isSizeDeterminedByWidthHeight() {
        return isSizeWidthDeterminedByHeight() || isSizeHeightDeterminedByWidth() || isSizeAbsolute();
    }

    public boolean isARotation() {
        return rotation != 0;
    }

    public boolean isSizeChangeForRotation() {
        return rotation == 90 || rotation == 270;
    }

    public boolean isFormatJpg() {
        return format.equalsIgnoreCase("jpg");
    }

    /**
     * Remove any OS-unfriendly characters from filename
     * 
     * @return identifier comprising only of letters a-z, A-Z, digits, full stop and hyphen
     */
    public String getCleanIdentifier() {
        return identifier.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    @Override
    public String toString() {
        return "RequestData [identifier=" + identifier + ", region(x,y,w,h)=" + region + ", size(w,h)=" + size
                + ", rotation=" + rotation + ", quality=" + quality + ", format=" + format + "]";
    }

}
