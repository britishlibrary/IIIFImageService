package uk.bl.iiifimageservice.domain;

import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.math.BigDecimal;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.bl.iiifimageservice.service.RegionSizeCalculator;
import uk.bl.iiifimageservice.util.ImageMetadataGenerator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:image-servlet-test.xml")
public class RequestDataTest {

    @Resource
    private RegionSizeCalculator regionSizeCalculator;

    private ImageMetadata imageMetadata = ImageMetadataGenerator.getTestImageMetadata();

    @Test
    public void testIsRegionFull() {

        RequestData requestData = new RequestData();
        requestData.setRegion(RequestData.FULL_LITERAL);

        assertTrue(requestData.isRegionFull());
    }

    @Test
    public void testIsRegionPercentage() {
        RequestData requestData = new RequestData();
        requestData.setRegion(RequestData.PERCENTAGE_LITERAL);

        assertTrue(requestData.isRegionPercentage());
    }

    @Test
    public void testGetRegionCoordinates() {

        RequestData requestData = new RequestData();
        requestData.setRegion("80,15,60,75");

        Rectangle coords = regionSizeCalculator.getRegionCoordinates(imageMetadata, requestData);

        assertTrue("X coord wrong, expected 80, got " + coords.getX(), coords.getX() == 80d);
        assertTrue("Y coord wrong", coords.getY() == 15d);
        assertTrue("width coord wrong", coords.getWidth() == 60d);
        assertTrue("height coord wrong", coords.getHeight() == 75d);

    }

    @Test
    public void testGetSizeScalingForWidthAspectRatio() {

        RequestData requestData = new RequestData();
        requestData.setSize("100,");
        requestData.setRegion("full");

        Dimension scaling = regionSizeCalculator.getSizeForImageManipulation(imageMetadata, requestData);

        assertTrue("Width wrong, expected 100, got " + scaling.getWidth(), scaling.getWidth() == 100d);

    }

    @Test
    public void testGetSizeScalingForHeightAspectRatio() {

        RequestData requestData = new RequestData();
        requestData.setSize(",100");
        requestData.setRegion("full");

        Dimension scaling = regionSizeCalculator.getSizeForImageManipulation(imageMetadata, requestData);

        assertTrue("Height wrong, expected 100, got " + scaling.getHeight(), scaling.getHeight() == 100d);

    }

    @Test
    public void testGetSizeScalingForPercentage() {

        RequestData requestData = new RequestData();
        requestData.setSize("pct:50");

        BigDecimal sizePercentage = requestData.getSizePercentageAsDecimal();

        assertTrue("Size percentage wrong, expected 0.5, got " + sizePercentage,
                sizePercentage.compareTo(new BigDecimal("0.5")) == 0);

    }

    @Test
    public void testGetBestFitSize() {

        RequestData requestData = new RequestData();
        requestData.setSize("!150,75");
        requestData.setRegion("full");

        assertTrue(requestData.isSizeBestFit());
        assertTrue(regionSizeCalculator.getSizeForImageManipulation(imageMetadata, requestData)
                                       .getWidth() == 54d);
        assertTrue(regionSizeCalculator.getSizeForImageManipulation(imageMetadata, requestData)
                                       .getHeight() == 75d);

    }

}
