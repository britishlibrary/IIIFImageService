package uk.bl.iiifimageservice.service;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import uk.bl.iiifimageservice.domain.ImageMetadata;
import uk.bl.iiifimageservice.domain.RequestData;

/**
 * Helper methods for region and size
 * 
 * @author pblake
 * 
 */
@Service
public class RegionSizeCalculator {

    public Rectangle getRegionCoordinates(ImageMetadata imageMetadata, RequestData requestData) {

        Point p = new Point();
        Dimension d = new Dimension();

        if (requestData.isRegionFull()) {
            return new Rectangle(p, new Dimension(imageMetadata.getWidth(), imageMetadata.getHeight()));
        }

        String regionToSplit = requestData.getRegion();

        if (requestData.isRegionPercentage()) {
            regionToSplit = removePercentageLiteral(regionToSplit);
        }

        String[] coords = regionToSplit.split(RequestData.REQUEST_DELIMITER);
        p.x = Integer.parseInt(coords[0]);
        p.y = Integer.parseInt(coords[1]);
        d.width = Integer.parseInt(coords[2]);
        d.height = Integer.parseInt(coords[3]);

        return new Rectangle(p, d);
    }

    public Dimension getSize(ImageMetadata imageMetadata, RequestData requestData) {

        Dimension d = new Dimension();

        Dimension regionSize = null;
        regionSize = getRegionCoordinates(imageMetadata, requestData).getSize();

        if (requestData.isSizeFull()) {
            return regionSize;
        }

        String sizeToSplit = requestData.getSize();

        if (requestData.isSizeBestFit()) {
            sizeToSplit = sizeToSplit.substring(1);
        }

        String[] coords = sizeToSplit.split(RequestData.REQUEST_DELIMITER);

        if (requestData.isAspectRatioDeterminedByWidth()) {
            d.width = Integer.parseInt(coords[0]);
            d.height = (d.width * regionSize.height) / regionSize.width;
            return d;
        }

        if (requestData.isAspectRatioDeterminedByHeight()) {
            d.height = Integer.parseInt(coords[1]);
            d.width = (d.height * regionSize.width) / regionSize.height;
            return d;
        }

        if (requestData.isSizePercentageScaled()) {
            BigDecimal sizePercent = requestData.getSizePercentageAsDecimal();
            d.width = sizePercent.multiply(new BigDecimal(regionSize.width).setScale(0, RoundingMode.HALF_EVEN))
                    .intValue();
            d.height = sizePercent.multiply(new BigDecimal(regionSize.height).setScale(0, RoundingMode.HALF_EVEN))
                    .intValue();
            return d;
        }

        d.width = Integer.parseInt(coords[0]);
        d.height = Integer.parseInt(coords[1]);

        return d;
    }

    public Dimension getSizeForExtraction(ImageMetadata imageMetadata, RequestData requestData) {

        Dimension d = new Dimension();

        Dimension regionSize = null;
        regionSize = getRegionCoordinates(imageMetadata, requestData).getSize();

        if (requestData.isSizeFull()) {
            return regionSize;
        }

        String sizeToSplit = requestData.getSize();

        if (requestData.isSizeBestFit()) {
            sizeToSplit = sizeToSplit.substring(1);
        }

        String[] coords = sizeToSplit.split(RequestData.REQUEST_DELIMITER);

        if (requestData.isAspectRatioDeterminedByWidth()) {
            d.width = Integer.parseInt(coords[0]);
            return d;
        }

        if (requestData.isAspectRatioDeterminedByHeight()) {
            d.height = Integer.parseInt(coords[1]);
            return d;
        }

        if (requestData.isSizePercentageScaled()) {
            BigDecimal sizePercent = requestData.getSizePercentageAsDecimal();
            d.width = sizePercent.multiply(new BigDecimal(regionSize.width).setScale(0, RoundingMode.HALF_EVEN))
                    .intValue();
            d.height = sizePercent.multiply(new BigDecimal(regionSize.height).setScale(0, RoundingMode.HALF_EVEN))
                    .intValue();
            return d;
        }

        d.width = Integer.parseInt(coords[0]);
        d.height = Integer.parseInt(coords[1]);

        return d;
    }

    private String removePercentageLiteral(String value) {
        return value.substring(RequestData.PERCENTAGE_LITERAL.length());
    }

}
