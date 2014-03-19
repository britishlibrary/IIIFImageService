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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Adds extra data to the image metadata such as server name, scheme etc.
 * 
 * @author pblake
 * 
 */
@XmlRootElement(name = "info")
@XmlType(propOrder = { "identifier", "width", "height", "scaleFactors", "tileWidth", "tileHeight", "formats",
        "qualities", "profile"})
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
    private String host;
    private String portString;
    private String contextPath;
    private String context ="http://library.stanford.edu/iiif/image-api/1.1/context.json";
    

    @XmlTransient //added
    @JsonProperty("@id")
    public String getScheme() {
        return scheme +"://" +host +portString +contextPath +"/" +getIdentifier();
    }
    
    @XmlTransient
    @JsonProperty("@context")
    public String getContext() {
        return context;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }


	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		if(port == 80) portString = "";
		else portString = ":" + port;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
}
