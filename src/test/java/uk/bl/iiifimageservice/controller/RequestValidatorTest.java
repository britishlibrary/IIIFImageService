package uk.bl.iiifimageservice.controller;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import uk.bl.iiifimageservice.domain.RequestData;
import uk.bl.iiifimageservice.service.RegionSizeCalculator;
import uk.bl.iiifimageservice.util.ImageMetadataGenerator;
import uk.bl.iiifimageservice.util.RequestDataGenerator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-test.xml")
public class RequestValidatorTest {

    @Autowired
    private RequestValidator validator;

    @Autowired
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
