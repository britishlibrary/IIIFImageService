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
