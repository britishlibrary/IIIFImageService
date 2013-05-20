package uk.bl.iiifimageservice.service;

import static org.junit.Assert.assertEquals;

import java.awt.Dimension;
import java.awt.Rectangle;

import org.junit.Test;

import uk.bl.iiifimageservice.domain.ImageMetadata;
import uk.bl.iiifimageservice.domain.RequestData;
import uk.bl.iiifimageservice.util.ImageMetadataGenerator;
import uk.bl.iiifimageservice.util.RequestDataGenerator;

public class PixelRegionVariableSizeCalculator {

    private RegionSizeCalculator regionSizeCalculator = new RegionSizeCalculator();
    private ImageMetadata imageMetadata = ImageMetadataGenerator.getTestImageMetadata();

    @Test
    public void testFullSizeCoordinates() {

        RequestData requestData = RequestDataGenerator.getTestRequestDataPixelRegionFullSize();

        Dimension size = regionSizeCalculator.getSize(imageMetadata, requestData);
        Dimension expected = new Dimension(1200, 1300);

        assertEquals(expected, size);

    }

    @Test
    public void testSizeSetByWidth() {

        RequestData requestData = RequestDataGenerator.getTestRequestDataPixelRegionSizeSetByWidth();

        Dimension size = regionSizeCalculator.getSize(imageMetadata, requestData);
        Dimension expected = new Dimension(600, 650);

        assertEquals(expected, size);

    }

    @Test
    public void testSizeSetByHeight() {

        RequestData requestData = RequestDataGenerator.getTestRequestDataPixelRegionSizeSetByHeight();

        Dimension size = regionSizeCalculator.getSize(imageMetadata, requestData);
        Dimension expected = new Dimension(553, 600);

        assertEquals(expected, size);

    }

    @Test
    public void testSizeSetByPixels() {

        RequestData requestData = RequestDataGenerator.getTestRequestDataPixelRegionSizeSetByPixels();

        Dimension size = regionSizeCalculator.getSize(imageMetadata, requestData);
        Dimension expected = new Dimension(600, 1300);

        assertEquals(expected, size);

        Rectangle region = regionSizeCalculator.getRegionCoordinates(imageMetadata, requestData);
        Rectangle expectedRegion = new Rectangle(1000, 1100, 1200, 1300);
        assertEquals(expectedRegion, region);

    }

}
