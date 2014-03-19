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
package uk.bl.iiifimageservice.util;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import uk.bl.iiifimageservice.domain.ImageMetadata;
import uk.bl.iiifimageservice.domain.RequestData;

/**
 * Manipulates the values from the RequestData
 * 
 * @author pblake
 * 
 */
@Component
public class RequestParser {

    private static final Logger log = LoggerFactory.getLogger(RequestParser.class);

    public String removePercentageLiteral(String value) {
        return value.substring(RequestData.PERCENTAGE_LITERAL.length());
    }

    /**
     * Region coordinates are converted to a more manageable Rectangle. If the requested width/height are outside the
     * image then they are set to the image width/height.
     * 
     * @param coords
     * @param imageMetadata
     * @return
     */
    private Rectangle convertCoordinatesToRectangle(String[] coords, ImageMetadata imageMetadata) {

        Point p = new Point();
        Dimension d = new Dimension();

        p.x = Integer.parseInt(coords[0]);
        p.y = Integer.parseInt(coords[1]);

        try {
            d.width = Integer.parseInt(coords[2]);
        } catch (NumberFormatException nfe) {
            d.width = imageMetadata.getWidth();
            log.debug("requested region width [" + coords[2] + "] too large so replacing with image width ["
                    + imageMetadata.getWidth() + "]");
        }
        if (d.width > imageMetadata.getWidth()) {
            d.width = imageMetadata.getWidth();
            log.debug("requested region width [" + coords[2] + "] too large so replacing with image width ["
                    + imageMetadata.getWidth() + "]");
        }

        try {
            d.height = Integer.parseInt(coords[3]);
        } catch (NumberFormatException nfe) {
            d.height = imageMetadata.getHeight();
            log.debug("requested region height [" + coords[3] + "] too large so replacing with image height ["
                    + imageMetadata.getHeight() + "]");
        }
        if (d.height > imageMetadata.getHeight()) {
            d.height = imageMetadata.getHeight();
            log.debug("requested region height [" + coords[3] + "] too large so replacing with image height ["
                    + imageMetadata.getHeight() + "]");
        }

        return new Rectangle(p, d);

    }

    public Rectangle getRegionValues(RequestData requestData, ImageMetadata imageMetadata) {
        String regionToSplit = requestData.getRegion();

        if (requestData.isRegionPercentage()) {
            regionToSplit = removePercentageLiteral(regionToSplit);
        }
        String[] coords = splitParameter(regionToSplit);

        return convertCoordinatesToRectangle(coords, imageMetadata);
    }

    public String[] splitParameter(String value) {

        return value.split(RequestData.REQUEST_DELIMITER);
    }

}
