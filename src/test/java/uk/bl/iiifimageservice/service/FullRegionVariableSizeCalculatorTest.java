package uk.bl.iiifimageservice.service;

import static org.junit.Assert.assertEquals;

import java.awt.Dimension;

import org.junit.Test;

import uk.bl.iiifimageservice.domain.ImageMetadata;
import uk.bl.iiifimageservice.domain.RequestData;
import uk.bl.iiifimageservice.util.ImageMetadataGenerator;
import uk.bl.iiifimageservice.util.RequestDataGenerator;

public class FullRegionVariableSizeCalculatorTest {

    private RegionSizeCalculator regionSizeCalculator = new RegionSizeCalculator();
    private ImageMetadata imageMetadata = ImageMetadataGenerator.getTestImageMetadata();

    @Test
    public void testFullSizeCoordinates() {

        RequestData requestData = RequestDataGenerator.getTestRequestDataFullRegionFullSize();

        Dimension size = regionSizeCalculator.getSize(imageMetadata, requestData);
        Dimension expected = new Dimension(4700, 6500);

        assertEquals(expected, size);

    }

    @Test
    public void testSizeSetByWidth() {

        RequestData requestData = RequestDataGenerator.getTestRequestDataFullRegionSizeSetByWidth();

        Dimension size = regionSizeCalculator.getSize(imageMetadata, requestData);
        Dimension expected = new Dimension(600, 829);

        assertEquals(expected, size);

    }

    @Test
    public void testSizeSetByHeight() {

        RequestData requestData = RequestDataGenerator.getTestRequestDataFullRegionSizeSetByHeight();

        Dimension size = regionSizeCalculator.getSize(imageMetadata, requestData);
        Dimension expected = new Dimension(723, 1000);

        assertEquals(expected, size);

    }

    @Test
    public void testSizeSetByPixels() {

        RequestData requestData = RequestDataGenerator.getTestRequestDataFullRegionSizeSetByPixels();

        Dimension size = regionSizeCalculator.getSize(imageMetadata, requestData);
        Dimension expected = new Dimension(600, 1000);

        assertEquals(expected, size);

    }

    @Test
    public void testSizeSetByPercent() {

        RequestData requestData = RequestDataGenerator.getTestRequestDataFullRegionSizeSetByPercent();

        Dimension size = regionSizeCalculator.getSize(imageMetadata, requestData);
        Dimension expected = new Dimension(940, 1300);

        assertEquals(expected, size);

    }

}
