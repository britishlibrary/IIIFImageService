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

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonIgnore; // added SM 140312

/**
 * The JP2 image metadata
 * 
 * @author pblake
 * 
 */
@XmlTransient
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
    @JsonIgnore // added SM 140312
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
    @JsonProperty("tile_width")
    public int getTileWidth() {
        return tileWidth;
    }

    public void setTileWidth(int tileWidth) {
        this.tileWidth = tileWidth;
    }

    @XmlElement(name = "tile_height")
    @JsonProperty("tile_height")
    public int getTileHeight() {
        return tileHeight;
    }

    public void setTileHeight(int tileHeight) {
        this.tileHeight = tileHeight;
    }

    @XmlElementWrapper(name = "scale_factors")
    @XmlElement(name = "scale_factor")
    @JsonProperty("scale_factors")
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
