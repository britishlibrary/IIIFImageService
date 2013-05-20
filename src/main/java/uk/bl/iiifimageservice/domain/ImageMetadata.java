package uk.bl.iiifimageservice.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The JP2 image metadata
 * 
 * @author pblake
 * 
 */
@XmlType(propOrder = { "identifier", "width", "height", "scaleFactors", "tileWidth", "tileHeight", "formats",
        "qualities", "profile" })
@XmlRootElement(name = "info", namespace = "http://library.stanford.edu/iiif/image-api/ns/")
public class ImageMetadata {

    private String identifier;

    private int width;

    private int height;

    private int tileWidth;

    private int tileHeight;

    private List<Integer> scaleFactors;

    private List<String> formats;

    private List<String> qualities;

    private String profile;

    @XmlElement
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @XmlElement
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @XmlElement
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @XmlElement(name = "tile_width")
    public int getTileWidth() {
        return tileWidth;
    }

    public void setTileWidth(int tileWidth) {
        this.tileWidth = tileWidth;
    }

    @XmlElement(name = "tile_height")
    public int getTileHeight() {
        return tileHeight;
    }

    public void setTileHeight(int tileHeight) {
        this.tileHeight = tileHeight;
    }

    @XmlElementWrapper(name = "scale_factors")
    @XmlElement(name = "scale_factor")
    public List<Integer> getScaleFactors() {
        return scaleFactors;
    }

    public void setScaleFactors(List<Integer> scaleFactors) {
        this.scaleFactors = scaleFactors;
    }

    @XmlElementWrapper(name = "formats")
    @XmlElement(name = "format")
    public List<String> getFormats() {
        return formats;
    }

    public void setFormats(List<String> format) {
        this.formats = format;
    }

    @XmlElementWrapper(name = "qualities")
    @XmlElement(name = "quality")
    public List<String> getQualities() {
        return qualities;
    }

    public void setQualities(List<String> qualities) {
        this.qualities = qualities;
    }

    @XmlElement
    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "ImageMetadata [identifier=" + identifier + ", width=" + width + ", height=" + height + ", tileWidth="
                + tileWidth + ", tileHeight=" + tileHeight + ", scaleFactors=" + scaleFactors + ", formats=" + formats
                + ", qualities=" + qualities + ", profile=" + profile + "]";
    }

}
