/**
* Copyright (c) 2014, The British Library Board
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
* Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
*   in the documentation and/or other materials provided with the distribution.
* Neither the name of The British Library nor the names of its contributors may be used to endorse or promote products
*   derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
*   INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
*   IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
*   OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
*   OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, 
*   OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
*   EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
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

        Rectangle coords = regionSizeCalculator.getRegionCoordinates(requestData, imageMetadata);

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
