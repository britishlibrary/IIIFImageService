package uk.bl.iiifimageservice.controller;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import uk.bl.iiifimageservice.domain.ImageError.ParameterName;
import uk.bl.iiifimageservice.domain.ImageFormat;
import uk.bl.iiifimageservice.domain.RequestData;
import uk.bl.iiifimageservice.util.ImageServiceException;
import uk.bl.iiifimageservice.util.RequestParser;

@Component
public class RequestValidator implements Validator {

    private static final Logger log = LoggerFactory.getLogger(RequestValidator.class);

    @Autowired
    private RequestParser requestParser;

    @Override
    public boolean supports(Class<?> clazz) {
        return RequestData.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        RequestData requestData = (RequestData) target;
        validateRegion(requestData, errors);
        validateSize(requestData, errors);
        validateFormat(requestData);

    }

    private void validateSize(RequestData requestData, Errors errors) {

        String sizeValue = requestData.getSize();
        if (!sizeValue.startsWith(RequestData.PERCENTAGE_LITERAL) && !sizeValue.startsWith(RequestData.FULL_LITERAL)
                && !sizeValue.substring(0, 1).matches("\\d") && !sizeValue.startsWith(",")
                && !sizeValue.startsWith("!")) {
            errors.rejectValue("size", "size.invalid.start");
        }

    }

    private void validateRegion(RequestData requestData, Errors errors) {

        String regionValue = requestData.getRegion();

        if (!regionValue.startsWith(RequestData.PERCENTAGE_LITERAL) && !regionValue.substring(0, 1).matches("\\d")
                && !regionValue.startsWith(RequestData.FULL_LITERAL)) {
            errors.rejectValue("region", "region.invalid.start");
        }

        if (!requestData.isRegionFull()) {
            if (requestData.isRegionPercentage()) {
                regionValue = requestParser.removePercentageLiteral(regionValue);
            }

            String[] coords = requestParser.splitParameter(regionValue);
            validateCoordinates(coords, errors);
        }

    }

    private void validateCoordinates(String[] coords, Errors errors) {

        if (coords.length != 4) {
            errors.rejectValue("region", "region.missing");
        }

        if (StringUtils.isEmpty(coords[0])) {
            errors.rejectValue("region", "x.missing");
        }

        if (StringUtils.isEmpty(coords[1])) {
            errors.rejectValue("region", "y.missing");
        }

        if (StringUtils.isEmpty(coords[2])) {
            errors.rejectValue("region", "width.missing");
        }

        if (!StringUtils.isEmpty(coords[2]) && Integer.valueOf(coords[2]) == 0) {
            errors.rejectValue("region", "width.zero");
        }

        if (StringUtils.isEmpty(coords[3])) {
            errors.rejectValue("region", "height.mising");

        }

        if (!StringUtils.isEmpty(coords[3]) && Integer.valueOf(coords[3]) == 0) {
            errors.rejectValue("region", "height.zero");

        }

    }

    private void validateFormat(RequestData requestData) {

        try {
            ImageFormat.valueOf(requestData.getFormat().toUpperCase());
        } catch (IllegalArgumentException iae) {
            throw new ImageServiceException("unknown format", 415, ParameterName.FORMAT);
        }

        // check for jp2 support
        log.debug("Available write formats from ImageIO " + StringUtils.join(ImageIO.getWriterFormatNames(), ", "));

        if (requestData.getFormat().equalsIgnoreCase(ImageFormat.JP2.name())
                && !StringUtils.join(ImageIO.getWriterFormatNames()).contains("JPEG2000")) {
            throw new ImageServiceException("jp2 not supported", 415, ParameterName.FORMAT);
        }

    }

}
