package uk.bl.iiifimageservice.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Adds extra data to the image metadata such as server name, scheme etc.
 * 
 * @author pblake
 * 
 */
@XmlRootElement(name = "info")
@XmlType(propOrder = { "identifier", "width", "height", "scaleFactors", "tileWidth", "tileHeight", "formats",
        "qualities", "profile", "scheme" })
public class ImageRequestMetadata extends ImageMetadata {

    public ImageRequestMetadata() {
    }

    public ImageRequestMetadata(ImageMetadata imageMetadata) {
        setFormats(imageMetadata.getFormats());
        setHeight(imageMetadata.getHeight());
        setIdentifier(imageMetadata.getIdentifier());
        setProfile(imageMetadata.getProfile());
        setQualities(imageMetadata.getQualities());
        setScaleFactors(imageMetadata.getScaleFactors());
        setTileHeight(imageMetadata.getTileHeight());
        setTileWidth(imageMetadata.getTileWidth());
        setWidth(imageMetadata.getWidth());
    }

    private String scheme;

    @XmlElement
    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

}
