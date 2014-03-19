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
package uk.bl.iiifimageservice.service;

import static org.junit.Assert.assertEquals;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.bl.iiifimageservice.domain.ImageMetadata;
import uk.bl.iiifimageservice.domain.RequestData;
import uk.bl.iiifimageservice.util.ImageMetadataGenerator;
import uk.bl.iiifimageservice.util.RequestDataGenerator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:image-servlet-test.xml")
public class PixelRegionVariableSizeCalculator {

    @Resource
    private RegionSizeCalculator regionSizeCalculator;

    private ImageMetadata imageMetadata = ImageMetadataGenerator.getTestImageMetadata();

    @Test
    public void testFullSizeCoordinates() {

        RequestData requestData = RequestDataGenerator.getTestRequestDataPixelRegionFullSize();

        Dimension size = regionSizeCalculator.getSizeForImageManipulation(imageMetadata, requestData);
        Dimension expected = new Dimension(1200, 1300);

        assertEquals(expected, size);

    }

    @Test
    public void testSizeSetByWidth() {

        RequestData requestData = RequestDataGenerator.getTestRequestDataPixelRegionSizeSetByWidth();

        Dimension size = regionSizeCalculator.getSizeForImageManipulation(imageMetadata, requestData);
        Dimension expected = new Dimension(600, 650);

        assertEquals(expected, size);

    }

    @Test
    public void testSizeSetByHeight() {

        RequestData requestData = RequestDataGenerator.getTestRequestDataPixelRegionSizeSetByHeight();

        Dimension size = regionSizeCalculator.getSizeForImageManipulation(imageMetadata, requestData);
        Dimension expected = new Dimension(554, 600);

        assertEquals(expected, size);

    }

    @Test
    public void testSizeSetByPixels() {

        RequestData requestData = RequestDataGenerator.getTestRequestDataPixelRegionSizeSetByPixels();

        Dimension size = regionSizeCalculator.getSizeForImageManipulation(imageMetadata, requestData);
        Dimension expected = new Dimension(600, 1300);

        assertEquals(expected, size);

        Rectangle region = regionSizeCalculator.getRegionCoordinates(requestData, imageMetadata);
        Rectangle expectedRegion = new Rectangle(1000, 1100, 1200, 1300);
        assertEquals(expectedRegion, region);

    }

}
