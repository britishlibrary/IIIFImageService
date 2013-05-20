package uk.bl.iiifimageservice.service;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.bl.iiifimageservice.domain.ImageMetadata;
import uk.bl.iiifimageservice.domain.RequestData;

@Service
public class KakaduCommandBuilder {

    private static final Logger log = LoggerFactory.getLogger(KakaduCommandBuilder.class);

    @Autowired
    private RegionSizeCalculator regionSizeCalculator;

    public Results getExtractorValues(ImageMetadata imageMetadata, RequestData requestData) {

        MathContext precisonTwo = new MathContext(10, RoundingMode.HALF_EVEN);
        Rectangle requestedRegion = regionSizeCalculator.getRegionCoordinates(imageMetadata, requestData);

        Dimension requestedSize = regionSizeCalculator.getSizeForExtraction(imageMetadata, requestData);
        BigDecimal requestedSizeWidth = new BigDecimal(requestedSize.width);
        BigDecimal requestedSizeHeight = new BigDecimal(requestedSize.height);

        BigDecimal scale = BigDecimal.ONE;
        BigDecimal scaleX = BigDecimal.ONE;
        BigDecimal scaleY = BigDecimal.ONE;

        BigDecimal startX = BigDecimal.ZERO; // full region
        BigDecimal startY = BigDecimal.ZERO; // full region
        BigDecimal tileHeight = BigDecimal.ONE;
        BigDecimal tileWidth = BigDecimal.ONE;
        BigDecimal widthScale = new BigDecimal(String.valueOf((double) requestedSize.width / imageMetadata.getWidth()),
                precisonTwo);
        BigDecimal heightScale = new BigDecimal(String.valueOf((double) requestedSize.height
                / imageMetadata.getHeight()), precisonTwo);

        if (requestData.isRegionPercentage()) {
            requestedRegion = switchCoordsForRegionExtraction(requestedRegion);
            // requestedRegion = regionSizeCalculator.getRegionCoordinatesFromPercent(imageMetadata, requestedRegion);

            startX = new BigDecimal(requestedRegion.x);
            startY = new BigDecimal(requestedRegion.y);
            // double.Parse(regions[2]) * imageSize.Height;
            tileHeight = new BigDecimal(requestedRegion.height);
            // double.Parse(regions[3]) * imageSize.Width;
            tileWidth = new BigDecimal(requestedRegion.width);
        }

        if (requestData.isRegionPixels()) {
            requestedRegion = switchCoordsForRegionExtraction(requestedRegion);
            startX = new BigDecimal(requestedRegion.x);
            startY = new BigDecimal(requestedRegion.y);
            // double.Parse(regions[2]) * imageSize.Height;
            tileHeight = new BigDecimal(requestedRegion.height);
            // double.Parse(regions[3]) * imageSize.Width;
            tileWidth = new BigDecimal(requestedRegion.width);
        }

        startX = BigDecimal.ZERO;
        startY = BigDecimal.ZERO;

        // a resize
        if (requestData.resizeImage()) {
            if (requestData.isRegionFull()) {
                scaleX = widthScale;
                scaleY = heightScale;

                if (scaleX.compareTo(scaleY) > 0) { // scalex < scaley
                    scale = scaleX;
                } else {
                    scale = scaleY;
                }
                tileWidth = new BigDecimal(String.valueOf(imageMetadata.getWidth())).multiply(scale, precisonTwo);
                tileHeight = new BigDecimal(String.valueOf(imageMetadata.getHeight())).multiply(scale, precisonTwo);

            } else if (requestedSize.width != 0 && requestedSize.height != 0) {
                if (tileWidth.compareTo(BigDecimal.ZERO) > 0) { // tilewidth > 0
                    scaleX = requestedSizeWidth.divide(tileWidth, precisonTwo);
                } else {
                    scaleX = widthScale;
                }

                if (tileHeight.compareTo(BigDecimal.ZERO) > 0) { // tileheight > 0
                    scaleY = requestedSizeHeight.divide(tileHeight, precisonTwo);
                } else {
                    scaleY = heightScale;
                }

                if (requestedSize.width == requestedSize.height) {
                    scale = scaleX; // both scales should be the same so shouldn't matter
                } else if (requestedSizeWidth.compareTo(requestedSizeHeight) > 0) { // p_state.Width > p_state.Height
                    scale = scaleX;
                } else {
                    scale = scaleY;
                }

            } else if (requestedSize.width > requestedSize.height) {
                if (tileWidth.compareTo(BigDecimal.ZERO) > 0) {
                    scale = (new BigDecimal(requestedSize.width).divide(tileWidth, precisonTwo));
                } else {
                    scale = widthScale;
                }

                scaleY = scaleX = scale;
            } else {
                if (tileHeight.compareTo(BigDecimal.ZERO) > 0) {
                    scale = (new BigDecimal(requestedSize.height).divide(tileHeight, precisonTwo));
                } else {
                    scale = heightScale;
                }
            }

            if (!requestData.resizeImage() && requestData.isRegionFull()) {
                if (requestedSize.getWidth() != 0)
                    tileWidth = requestedSizeWidth;

                if (requestedSize.getWidth() != 0)
                    tileHeight = requestedSizeHeight;
            }

        } // end resizeImage

        // size best fit
        if (requestData.isSizeBestFit()) {
            if (tileWidth.compareTo(BigDecimal.ZERO) > 0) {
                scaleX = requestedSizeWidth.divide(tileWidth, precisonTwo); // p_state.Width / tilewidth;
            } else {
                scaleX = widthScale;
            }

            if (tileHeight.compareTo(BigDecimal.ZERO) > 0) {
                scaleY = requestedSizeHeight.divide(tileHeight, precisonTwo); // p_state.Height / tileheight;
            } else {
                scaleY = heightScale;
            }

            if (scaleX.compareTo(scaleY) < 0) { // scalex < scaley
                scale = scaleX;
            } else {
                scale = scaleY;
            }

            // get all
            if (requestData.isRegionFull()) { // (p_state.Region.Equals("all"))
                // int.Parse(Math.Round(imageSize.Height * scale).ToString());

                tileHeight = new BigDecimal(imageMetadata.getHeight()).multiply(scale, precisonTwo);
                // int.Parse(Math.Round(imageSize.Width * scale).ToString());
                tileWidth = new BigDecimal(imageMetadata.getWidth()).multiply(scale, precisonTwo);
            }

        } // end size best fit

        // size %
        if (requestData.isSizePercentageScaled()) {
            scale = requestData.getSizePercentageAsDecimal(); // p_state.Size;

            // get all
            if (requestData.isRegionFull()) // (p_state.Region.Equals("all"))
            {
                // int.Parse(Math.Round(imageSize.Height * scale).ToString());
                // two values below calculated elsewhere. See RegionSizeCalculator
                tileHeight = requestedSizeHeight;
                // int.Parse(Math.Round(imageSize.Width * scale).ToString());
                tileWidth = requestedSizeWidth;
            }

        } // end size %

        if (!requestData.isSizePercentageScaled() && !requestData.isSizeBestFit() && !requestData.resizeImage()) {
            if (widthScale.compareTo(heightScale) < 0) { // (wScale < hScale)
                scale = widthScale;
            } else {
                scale = heightScale;
            }

            scaleX = scaleY = scale;

        }

        String regionValue = "";
        if (!requestData.isRegionFull()) {
            if (requestData.isRegionPercentage()) {
                requestedRegion = switchCoordsForRegionExtraction(requestedRegion);
                requestedRegion = regionSizeCalculator.splitRegion(requestData);
                startX = new BigDecimal(requestedRegion.x).movePointLeft(2);
                startY = new BigDecimal(requestedRegion.y).movePointLeft(2);
                tileWidth = new BigDecimal(requestedRegion.width).movePointLeft(2);
                tileHeight = new BigDecimal(requestedRegion.height).movePointLeft(2);

            }

            if (requestData.isRegionPixels()) {
                requestedRegion = switchCoordsForRegionExtraction(requestedRegion);
                startX = new BigDecimal(String.valueOf((double) requestedRegion.x / imageMetadata.getWidth()));
                startY = new BigDecimal(String.valueOf((double) requestedRegion.y / imageMetadata.getHeight()));
                tileWidth = new BigDecimal(String.valueOf((double) requestedRegion.width / imageMetadata.getWidth()));
                tileHeight = new BigDecimal(String.valueOf((double) requestedRegion.height / imageMetadata.getHeight()));
            }

            regionValue = "{" + startY.toPlainString() + "," + startX.toPlainString() + "},{"
                    + tileHeight.toPlainString() + "," + tileWidth.toPlainString() + "}";

        }
        // Convert.ToInt32(1 / scale);
        int reduce = BigDecimal.ONE.divide(scale, precisonTwo).setScale(0, RoundingMode.HALF_EVEN).intValue();
        if (reduce > 0) {
            reduce = (int) Math.floor(logOfBase(2.5, reduce)); // Convert.ToInt32(Math.Floor(Math.Log(reduce, 2.5)));
        }

        log.debug("regionValue [" + regionValue + "]");
        log.debug("reduce " + reduce + "]");

        return new Results(regionValue, reduce);
    }

    public double logOfBase(double base, int num) {
        return Math.log(num) / Math.log(base);
    }

    private Rectangle switchCoordsForRegionExtraction(Rectangle correct) {

        return new Rectangle(correct.y, correct.x, correct.height, correct.width);
    }
}
