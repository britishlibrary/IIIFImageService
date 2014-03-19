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
package uk.bl.iiifimageservice.controller;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import uk.bl.iiifimageservice.domain.RequestData;
import uk.bl.iiifimageservice.service.RegionSizeCalculator;
import uk.bl.iiifimageservice.util.ImageMetadataGenerator;
import uk.bl.iiifimageservice.util.RequestDataGenerator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:image-servlet-test.xml")
public class RequestValidatorTest {

    @Resource
    private RequestValidator validator;

    @Resource
    private RegionSizeCalculator regionSizeCalculator;

    @Test
    public void validateRegionTest() {

        RequestData requestData = RequestDataGenerator.getTestRequestDataFullRegionFullSize();
        requestData.setRegion("broken");
        Errors errors = new BindException(requestData, "rd");

        validator.validate(requestData, errors);
        assertTrue(errors.hasErrors());

    }

    @Test
    public void validateSingleCharacterRegionTest() {

        RequestData requestData = RequestDataGenerator.getTestRequestDataFullRegionFullSize();
        requestData.setRegion(",");
        Errors errors = new BindException(requestData, "rd");

        validator.validate(requestData, errors);
        assertTrue(errors.hasErrors());

    }

    @Test
    public void validateXYWHRegionTest() {

        RequestData requestData = RequestDataGenerator.getTestRequestDataFullRegionFullSize();
        requestData.setRegion("1000,1100,1200,1300");
        Errors errors = new BindException(requestData, "rd");

        validator.validate(requestData, errors);
        assertTrue(!errors.hasErrors());

    }

    @Test
    public void validateXYSizeTest() {

        RequestData requestData = RequestDataGenerator.getTestRequestDataFullRegionFullSize();
        requestData.setSize("1000,1100");
        Errors errors = new BindException(requestData, "rd");

        validator.validate(requestData, errors);
        assertTrue(!errors.hasErrors());

    }

    @Test
    public void validateSingleCharacterSizeTest() {

        RequestData requestData = RequestDataGenerator.getTestRequestDataFullRegionFullSize();
        requestData.setSize(",");
        Errors errors = new BindException(requestData, "rd");

        validator.validate(requestData, errors);
        assertTrue(errors.hasErrors());

    }

    @Test
    public void validateBrokenSizeTest() {

        RequestData requestData = RequestDataGenerator.getTestRequestDataFullRegionFullSize();
        requestData.setSize(",broken");

        try {
            regionSizeCalculator.getSizeForExtraction(ImageMetadataGenerator.getTestImageMetadata(), requestData);
        } catch (Exception e) {
            return;
        }

        fail("unable to parse size");
    }

}
