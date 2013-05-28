package uk.bl.iiifimageservice.util;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import org.springframework.stereotype.Component;

import uk.bl.iiifimageservice.domain.RequestData;

/**
 * Manipulates the values from the RequestData
 * 
 * @author pblake
 * 
 */
@Component
public class RequestParser {

    public String removePercentageLiteral(String value) {
        return value.substring(RequestData.PERCENTAGE_LITERAL.length());
    }

    private Rectangle convertCoordinatesToRectangle(String[] coords) {

        Point p = new Point();
        Dimension d = new Dimension();

        p.x = Integer.parseInt(coords[0]);
        p.y = Integer.parseInt(coords[1]);
        d.width = Integer.parseInt(coords[2]);
        d.height = Integer.parseInt(coords[3]);

        return new Rectangle(p, d);

    }

    public Rectangle getRegionValues(RequestData requestData) {
        String regionToSplit = requestData.getRegion();

        if (requestData.isRegionPercentage()) {
            regionToSplit = removePercentageLiteral(regionToSplit);
        }
        String[] coords = splitParameter(regionToSplit);

        return convertCoordinatesToRectangle(coords);
    }

    public String[] splitParameter(String value) {

        return value.split(RequestData.REQUEST_DELIMITER);
    }

}
