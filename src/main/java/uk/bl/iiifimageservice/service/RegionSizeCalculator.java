package uk.bl.iiifimageservice.service;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.bl.iiifimageservice.domain.ImageMetadata;
import uk.bl.iiifimageservice.domain.RequestData;
import uk.bl.iiifimageservice.util.RequestParser;

/**
 * Helper methods for region and size
 * 
 * @author pblake
 * 
 */
@Service
public class RegionSizeCalculator {

    @Autowired
    private RequestParser requestParser;

    /**
     * Returns the requested region as pixel values. The three region types are catered for i.e. full, percentage and
     * absolute.
     * 
     * @param imageMetadata
     * @param requestData
     * @return
     */
    public Rectangle getRegionCoordinates(ImageMetadata imageMetadata, RequestData requestData) {

        if (requestData.isRegionFull()) {
            return new Rectangle(new Point(), new Dimension(imageMetadata.getWidth(), imageMetadata.getHeight()));
        }

        Rectangle regionValues = requestParser.getRegionValues(requestData);
        if (requestData.isRegionPercentage()) {
            regionValues = getRegionCoordinatesFromPercent(imageMetadata, regionValues);
        }
        return regionValues;

    }

    /**
     * When manipulating the extracted image region the size is calculated as follows -
     * <ul>
     * <li>For full size the original image size is returned.
     * <li>For a size with width defined the height is a value that maintains the aspect ratio.
     * <li>For a size with height defined the width is a value that maintains the aspect ratio.
     * <li>For a size defined by percent the width and height is scaled by the percentage value.
     * <li>For a size that has width and height explicitly defined return those values.
     * <li>TODO For a best fit size
     * 
     * 
     * @param imageMetadata
     * @param requestData
     * @return
     */
    public Dimension getSizeForImageManipulation(ImageMetadata imageMetadata, RequestData requestData) {

        Dimension d = new Dimension();

        Dimension regionSize = getRegionCoordinates(imageMetadata, requestData).getSize();

        if (requestData.isSizeFull()) {
            return regionSize;
        }

        String sizeToSplit = requestData.getSize();

        if (requestData.isSizeBestFit()) {
            sizeToSplit = sizeToSplit.substring(1);
        }

        String[] coords = sizeToSplit.split(RequestData.REQUEST_DELIMITER);

        if (requestData.isSizeHeightDeterminedByWidth()) {
            d.width = Integer.parseInt(coords[0]);
            d.height = (d.width * regionSize.height) / regionSize.width;
            return d;
        }

        if (requestData.isSizeWidthDeterminedByHeight()) {
            d.height = Integer.parseInt(coords[1]);
            d.width = (d.height * regionSize.width) / regionSize.height;
            return d;
        }

        if (requestData.isSizePercentage()) {
            BigDecimal sizePercent = requestData.getSizePercentageAsDecimal();
            d.width = sizePercent.multiply(new BigDecimal(regionSize.width).setScale(0, RoundingMode.HALF_EVEN))
                    .intValue();
            d.height = sizePercent.multiply(new BigDecimal(regionSize.height).setScale(0, RoundingMode.HALF_EVEN))
                    .intValue();
            return d;
        }

        if (requestData.isSizeBestFit()) {
            if (!requestData.isRegionFull()) {
                regionSize = requestParser.getRegionValues(requestData).getSize();
            }
            BigDecimal scaleX = new BigDecimal(String.valueOf((double) Integer.parseInt(coords[0]) / regionSize.width));
            BigDecimal scaleY = new BigDecimal(String.valueOf((double) Integer.parseInt(coords[1]) / regionSize.height));

            if (scaleX.compareTo(scaleY) < 0) {
                d.width = (new BigDecimal(regionSize.width)).multiply(scaleX).setScale(0, RoundingMode.HALF_EVEN)
                        .intValue();
                d.height = (new BigDecimal(regionSize.height)).multiply(scaleX).setScale(0, RoundingMode.HALF_EVEN)
                        .intValue();
            } else {
                d.width = (new BigDecimal(regionSize.width)).multiply(scaleY).setScale(0, RoundingMode.HALF_EVEN)
                        .intValue();
                d.height = (new BigDecimal(regionSize.height)).multiply(scaleY).setScale(0, RoundingMode.HALF_EVEN)
                        .intValue();

            }
            return d;
        }

        d.width = Integer.parseInt(coords[0]);
        d.height = Integer.parseInt(coords[1]);

        return d;
    }

    public Dimension resizeForRotation(RequestData requestData, Dimension size) {

        if (requestData.isARotation() && requestData.isSizeChangeForRotation()) {
            // switch
            size.width = size.width ^ size.height;
            size.height = size.width ^ size.height;
            size.width = size.width ^ size.height;
        }

        return size;
    }

    /**
     * The calculation of the reduction parameter depends upon the size being in a particular format.
     * <ul>
     * <li>For full size return a zero width and height.
     * <li>For a size determined by width or height return the requested width or height.
     * <li>For a size determined by percent return a zero width and height.
     * <li>For a size that has width and height explicitly defined return those values.
     * <li>TODO For a best fit size
     * </ul>
     * 
     * @param imageMetadata
     * @param requestData
     * @return
     */
    public Dimension getSizeForExtraction(ImageMetadata imageMetadata, RequestData requestData) {

        Dimension d = new Dimension();

        if (requestData.isSizeFull()) {
            return d;
        }

        String sizeToSplit = requestData.getSize();

        if (requestData.isSizeBestFit()) {
            sizeToSplit = sizeToSplit.substring(1);
        }

        String[] coords = sizeToSplit.split(RequestData.REQUEST_DELIMITER);

        if (requestData.isSizeHeightDeterminedByWidth()) {
            d.width = Integer.parseInt(coords[0]);
            return d;
        }

        if (requestData.isSizeWidthDeterminedByHeight()) {
            d.height = Integer.parseInt(coords[1]);
            return d;
        }

        if (requestData.isSizePercentage()) {
            return d;
        }

        d.width = Integer.parseInt(coords[0]);
        d.height = Integer.parseInt(coords[1]);

        return d;
    }

    public Rectangle getRegion(RequestData requestData) {

        return requestParser.getRegionValues(requestData);

    }

    private Rectangle getRegionCoordinatesFromPercent(ImageMetadata imageMetadata, Rectangle requestedRegionAsPercent) {

        Point p = new Point();
        Dimension d = new Dimension();

        p.x = requestedRegionAsPercent.x * imageMetadata.getHeight() / 100;
        p.y = requestedRegionAsPercent.y * imageMetadata.getWidth() / 100;

        d.width = requestedRegionAsPercent.width * imageMetadata.getWidth() / 100;
        d.height = requestedRegionAsPercent.height * imageMetadata.getHeight() / 100;

        return new Rectangle(p, d);
    }

}
