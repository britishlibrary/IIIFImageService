package uk.bl.iiifimageservice.domain;

import java.math.BigDecimal;

public class RequestData {

    public static final String FULL_LITERAL = "full";
    public static final String PERCENTAGE_LITERAL = "pct:";
    public static final String REQUEST_DELIMITER = ",";

    private String identifier;

    private String region;

    private String size;

    private Float rotation;

    private String quality;

    private String format;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        // as identifier is used to write files, clean it.
        this.identifier = identifier.replaceAll("[^a-zA-Z0-9.-]", "_");
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

    public Float getRotation() {
        return rotation;
    }

    public void setRotation(Float rotation) {
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

    public boolean isRegionCoordinates() {
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
    public boolean isAspectRatioDeterminedByWidth() {
        return size.endsWith(REQUEST_DELIMITER);
    }

    /**
     * Only the height of the size is specified in the request
     * 
     * @return
     */
    public boolean isAspectRatioDeterminedByHeight() {
        return size.startsWith(REQUEST_DELIMITER);
    }

    /**
     * The size scale is specified by a percentage value in the request
     * 
     * @return
     */
    public boolean isSizePercentageScaled() {
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
    public boolean isSizePixels() {
        return !(isSizeFull() || isAspectRatioDeterminedByHeight() || isAspectRatioDeterminedByWidth()
                || isSizePercentageScaled() || isSizeBestFit());
    }

    private String removePercentageLiteral(String value) {
        return value.substring(PERCENTAGE_LITERAL.length());
    }

    public boolean resizeImage() {
        return isAspectRatioDeterminedByHeight() || isAspectRatioDeterminedByWidth() || isSizePixels();
    }

    @Override
    public String toString() {
        return "RequestData [identifier=" + identifier + ", region(x,y,w,h)=" + region + ", size(w,h)=" + size
                + ", rotation=" + rotation + ", quality=" + quality + ", format=" + format + "]";
    }

}
