package uk.bl.iiifimageservice.service.kakaduextractor;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import uk.bl.iiifimageservice.domain.ImageMetadata;
import uk.bl.iiifimageservice.domain.RequestData;
import uk.bl.iiifimageservice.service.RegionSizeCalculator;

/**
 * Logic to calculate the reduce and region extraction parameters for Kakadu binary
 * 
 * @author pblake
 * 
 */
@Service
public class KakaduParameterCalculator {

    private static final Logger log = LoggerFactory.getLogger(KakaduParameterCalculator.class);

    @Resource
    private RegionSizeCalculator regionSizeCalculator;

    public BigDecimal calculateScale(ImageMetadata imageMetadata, RequestData requestData) {

        MathContext precisonTen = new MathContext(10, RoundingMode.HALF_EVEN);
        Rectangle requestedRegion = regionSizeCalculator.getRegionCoordinates(requestData, imageMetadata);
        Dimension requestedSize = regionSizeCalculator.getSizeForExtraction(imageMetadata, requestData);
        BigDecimal requestedSizeWidth = new BigDecimal(requestedSize.width);
        BigDecimal requestedSizeHeight = new BigDecimal(requestedSize.height);

        BigDecimal scale = BigDecimal.ONE;
        BigDecimal scaleX = BigDecimal.ONE;
        BigDecimal scaleY = BigDecimal.ONE;
        BigDecimal tileWidth = BigDecimal.ONE;
        BigDecimal tileHeight = BigDecimal.ONE;

        BigDecimal widthScale = new BigDecimal(String.valueOf((double) requestedSize.width / imageMetadata.getWidth()),
                precisonTen);
        BigDecimal heightScale = new BigDecimal(String.valueOf((double) requestedSize.height
                / imageMetadata.getHeight()), precisonTen);

        if (requestData.isRegionPercentage()) {
        	log.debug("isRegionPercentage - passed");
            tileWidth = new BigDecimal(requestedRegion.width);
            tileHeight = new BigDecimal(requestedRegion.height);
        }

        if (requestData.isRegionAbsolute()) {
        	log.debug("isRegionAbsolute - passed");
            tileWidth = new BigDecimal(requestedRegion.width);
            tileHeight = new BigDecimal(requestedRegion.height);
        }

        // a resize
        if (requestData.isSizeDeterminedByWidthHeight()) {
        	log.debug("isSizeDeterminedByWidthHeight - passed");
            if (requestData.isRegionFull()) {
            	log.debug("isRegionFull - passed");
                scaleX = widthScale;
                scaleY = heightScale;

                if (scaleX.compareTo(scaleY) > 0) {
                    scale = scaleX;
                } else {
                    scale = scaleY;
                }
                tileWidth = new BigDecimal(String.valueOf(imageMetadata.getWidth())).multiply(scale, precisonTen);
                tileHeight = new BigDecimal(String.valueOf(imageMetadata.getHeight())).multiply(scale, precisonTen);

            } else if (requestedSize.width != 0 && requestedSize.height != 0) {
            	log.debug("requestedSize.width != 0 && requestedSize.height - passed");
                if (tileWidth.compareTo(BigDecimal.ZERO) > 0) {
                    scaleX = requestedSizeWidth.divide(tileWidth, precisonTen);
                } else {
                    scaleX = widthScale;
                }

                if (tileHeight.compareTo(BigDecimal.ZERO) > 0) {
                    scaleY = requestedSizeHeight.divide(tileHeight, precisonTen);
                } else {
                    scaleY = heightScale;
                }

                if (requestedSize.width == requestedSize.height) {
                    scale = scaleX; // both scales should be the same so shouldn't matter
                } else if (requestedSizeWidth.compareTo(requestedSizeHeight) > 0) {
                    scale = scaleX;
                } else {
                    scale = scaleY;
                }

            } else if (requestedSize.width > requestedSize.height) {
            	log.debug("requestedSize.width > requestedSize.height - passed");
                
                if (tileWidth.compareTo(BigDecimal.ZERO) > 0) {
                    scale = (new BigDecimal(requestedSize.width).divide(tileWidth, precisonTen));
                } else {
                    scale = widthScale;
                }

                scaleY = scaleX = scale;
            } else {
            	log.debug("final else - passed");
                if (tileHeight.compareTo(BigDecimal.ZERO) > 0) {
                    scale = (new BigDecimal(requestedSize.height).divide(tileHeight, precisonTen));
                } else {
                    scale = heightScale;
                }
            }

            if (!requestData.isSizeDeterminedByWidthHeight() && requestData.isRegionFull()) {
                if (requestedSize.getWidth() != 0)
                    tileWidth = requestedSizeWidth;

                if (requestedSize.getWidth() != 0)
                    tileHeight = requestedSizeHeight;
            }
            log.debug("resizeImage tileWidth [" + tileWidth + "]");
            log.debug("resizeImage requestedSizeWidth [" + requestedSizeWidth + "]");
            log.debug("resizeImage widthScale [" + widthScale + "]");
            log.debug("resizeImage scaleX [" + scaleX + "]");
            log.debug("resizeImage tileHeight [" + tileHeight + "]");
            log.debug("resizeImage requestedSizeHeight [" + requestedSizeHeight + "]");
            log.debug("resizeImage heightScale [" + heightScale + "]");
            log.debug("resizeImage scaleY [" + scaleY + "]");
            log.debug("resizeImage scale [" + scale + "]");
        } // end resizeImage

        // size best fit
        if (requestData.isSizeBestFit()) {
        	log.debug("isSizeBestFit - passed");
        	/* SM added two lines */
        	tileWidth = new BigDecimal(String.valueOf(imageMetadata.getWidth())).multiply(scale, precisonTen);
            tileHeight = new BigDecimal(String.valueOf(imageMetadata.getHeight())).multiply(scale, precisonTen);

        	
            if (tileWidth.compareTo(BigDecimal.ZERO) > 0) {
                scaleX = requestedSizeWidth.divide(tileWidth, precisonTen);
            } else {
                scaleX = widthScale;
            }

            if (tileHeight.compareTo(BigDecimal.ZERO) > 0) {
                scaleY = requestedSizeHeight.divide(tileHeight, precisonTen);
            } else {
                scaleY = heightScale;
            }

            if (scaleX.compareTo(scaleY) < 0) {
                scale = scaleX;
            } else {
                scale = scaleY;
            }

            // get all
            if (requestData.isRegionFull()) {
            	log.debug("isRegionFull - passed");
                tileWidth = new BigDecimal(imageMetadata.getWidth()).multiply(scale, precisonTen);
                tileHeight = new BigDecimal(imageMetadata.getHeight()).multiply(scale, precisonTen);
            }
            log.debug("best fit tileWidth [" + tileWidth + "]");
            log.debug("best fit requestedSizeWidth [" + requestedSizeWidth + "]");
            log.debug("best fit widthScale [" + widthScale + "]");
            log.debug("best fit scaleX [" + scaleX + "]");
            log.debug("best fit tileHeight [" + tileHeight + "]");
            log.debug("best fit requestedSizeHeight [" + requestedSizeHeight + "]");
            log.debug("best fit heightScale [" + heightScale + "]");
            log.debug("best fit scaleY [" + scaleY + "]");
            log.debug("best fit scale [" + scale + "]");
        } // end size best fit

        // size %
        if (requestData.isSizePercentage()) {
            scale = requestData.getSizePercentageAsDecimal();

            if (requestData.isRegionFull()) {
                tileWidth = requestedSizeWidth;
                tileHeight = requestedSizeHeight;
            }

        } // end size %

        if (requestData.isSizeFull()) {
            scale = BigDecimal.ONE;
            if (requestData.isRegionFull()) {
                tileWidth = requestedSizeWidth;
                tileHeight = requestedSizeHeight;
            }
        }

        return scale;

    }

    public int calculateReduceParameter(ImageMetadata imageMetadata, RequestData requestData) {

        BigDecimal scale = calculateScale(imageMetadata, requestData);
        
        log.debug("scale [" + scale + "]");

        int reduce = BigDecimal.ONE.divide(scale, new MathContext(10, RoundingMode.HALF_EVEN))
                                   .setScale(0, RoundingMode.HALF_EVEN)
                                   .intValue();
        if (reduce > 0) {
            reduce = (int) Math.floor(logOfBase(2, reduce));
        }

        log.debug("reduce [" + reduce + "]");

        return reduce;

    }

    public double logOfBase(double base, int num) {
        return Math.log(num) / Math.log(base);
    }

    public String calculateRegionExtractionParameter(ImageMetadata imageMetadata, RequestData requestData) {

        Rectangle requestedRegion = regionSizeCalculator.getRegionCoordinates(requestData, imageMetadata);

        BigDecimal startX = null;
        BigDecimal startY = null;
        BigDecimal tileWidth = null;
        BigDecimal tileHeight = null;
        String regionExtractionParameter = "";

        if (!requestData.isRegionFull()) {
            if (requestData.isRegionPercentage()) {
                requestedRegion = regionSizeCalculator.getRegion(requestData, imageMetadata);
                startX = new BigDecimal(requestedRegion.x).movePointLeft(2);
                startY = new BigDecimal(requestedRegion.y).movePointLeft(2);
                tileWidth = new BigDecimal(requestedRegion.width).movePointLeft(2);
                tileHeight = new BigDecimal(requestedRegion.height).movePointLeft(2);

            }

            if (requestData.isRegionAbsolute()) {
                startX = new BigDecimal(String.valueOf((double) requestedRegion.x / imageMetadata.getWidth()));
                startY = new BigDecimal(String.valueOf((double) requestedRegion.y / imageMetadata.getHeight()));
                tileWidth = new BigDecimal(String.valueOf((double) requestedRegion.width / imageMetadata.getWidth()));
                tileHeight = new BigDecimal(String.valueOf((double) requestedRegion.height / imageMetadata.getHeight()));
            }

            regionExtractionParameter = "{" + startY.toPlainString() + "," + startX.toPlainString() + "},{"
                    + tileHeight.toPlainString() + "," + tileWidth.toPlainString() + "}";

        }

        log.debug("regionExtractionParameter [" + regionExtractionParameter + "]");

        return regionExtractionParameter;

    }

}
