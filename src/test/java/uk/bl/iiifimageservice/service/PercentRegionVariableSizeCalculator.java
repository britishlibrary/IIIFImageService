package uk.bl.iiifimageservice.service;

import static org.junit.Assert.assertEquals;

import java.awt.Dimension;

import org.junit.Ignore;
import org.junit.Test;

import uk.bl.iiifimageservice.domain.ImageMetadata;
import uk.bl.iiifimageservice.domain.RequestData;
import uk.bl.iiifimageservice.util.ImageMetadataGenerator;
import uk.bl.iiifimageservice.util.RequestDataGenerator;

public class PercentRegionVariableSizeCalculator {

    private RegionSizeCalculator regionSizeCalculator = new RegionSizeCalculator();
    private ImageMetadata imageMetadata = ImageMetadataGenerator.getTestImageMetadata();

    @Test
    @Ignore
    public void testFullSizeCoordinates() {

        RequestData requestData = RequestDataGenerator.getTestRequestDataPercentRegionFullSize();

        Dimension size = regionSizeCalculator.getSize(imageMetadata, requestData);
        Dimension expected = new Dimension(1410, 2600);

        assertEquals(expected, size);

    }

}
