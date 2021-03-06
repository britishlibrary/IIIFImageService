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

        Dimension size = regionSizeCalculator.getSizeForImageManipulation(imageMetadata, requestData);
        Dimension expected = new Dimension(4700, 6500);

        assertEquals(expected, size);

    }

    @Test
    public void testSizeSetByWidth() {

        RequestData requestData = RequestDataGenerator.getTestRequestDataFullRegionSizeSetByWidth();

        Dimension size = regionSizeCalculator.getSizeForImageManipulation(imageMetadata, requestData);
        Dimension expected = new Dimension(600, 830);

        assertEquals(expected, size);

    }

    @Test
    public void testSizeSetByHeight() {

        RequestData requestData = RequestDataGenerator.getTestRequestDataFullRegionSizeSetByHeight();

        Dimension size = regionSizeCalculator.getSizeForImageManipulation(imageMetadata, requestData);
        Dimension expected = new Dimension(723, 1000);

        assertEquals(expected, size);

    }

    @Test
    public void testSizeSetByPixels() {

        RequestData requestData = RequestDataGenerator.getTestRequestDataFullRegionSizeSetByPixels();

        Dimension size = regionSizeCalculator.getSizeForImageManipulation(imageMetadata, requestData);
        Dimension expected = new Dimension(600, 1000);

        assertEquals(expected, size);

    }

    @Test
    public void testSizeSetByPercent() {

        RequestData requestData = RequestDataGenerator.getTestRequestDataFullRegionSizeSetByPercent();

        Dimension size = regionSizeCalculator.getSizeForImageManipulation(imageMetadata, requestData);
        Dimension expected = new Dimension(940, 1300);

        assertEquals(expected, size);

    }

}
