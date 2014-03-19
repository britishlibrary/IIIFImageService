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

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import uk.bl.iiifimageservice.domain.ImageError.ParameterName;
import uk.bl.iiifimageservice.domain.ImageFormat;
import uk.bl.iiifimageservice.domain.ImageQuality;
import uk.bl.iiifimageservice.domain.RequestData;
import uk.bl.iiifimageservice.util.ImageServiceException;
import uk.bl.iiifimageservice.util.RequestParser;

@Component
public class RequestValidator implements Validator {

    @Resource
    private RequestParser requestParser;

    @Override
    public boolean supports(Class<?> type) {
        return RequestData.class.isAssignableFrom(type);
    }

    @Override
    public void validate(Object target, Errors errors) {

        RequestData requestData = (RequestData) target;
        validateRegion(requestData, errors);
        validateSize(requestData, errors);
        validateFormat(requestData);
        validateRotation(requestData, errors);
        validateQuality(requestData, errors);

    }

    private void validateQuality(RequestData requestData, Errors errors) {

        try {
            ImageQuality.valueOf(requestData.getQuality()
                                            .toUpperCase());
        } catch (Exception e) {
            errors.rejectValue("quality", "quality.invalid");
        }

    }

    private void validateRotation(RequestData requestData, Errors errors) {

        Integer rotation = requestData.getRotation();
        if (rotation == null || (rotation != 0 && rotation != 90 && rotation != 180 && rotation != 270)) {
            errors.rejectValue("rotation", "rotation.invalid");
        }

    }

    private void validateSize(RequestData requestData, Errors errors) {

        String sizeValue = requestData.getSize();
        if (StringUtils.isEmpty(sizeValue) || sizeValue.length() == 1) {
            errors.rejectValue("size", "size.invalid.length");
            return;
        }

        if (!sizeValue.startsWith(RequestData.PERCENTAGE_LITERAL) && !sizeValue.startsWith(RequestData.FULL_LITERAL)
                && !sizeValue.substring(0, 1)
                             .matches("\\d") && !sizeValue.startsWith(",") && !sizeValue.startsWith("!")) {
            errors.rejectValue("size", "size.invalid.start");
        }

    }

    private void validateRegion(RequestData requestData, Errors errors) {

        String regionValue = requestData.getRegion();

        if (!regionValue.startsWith(RequestData.PERCENTAGE_LITERAL) && !regionValue.substring(0, 1)
                                                                                   .matches("\\d")
                && !regionValue.startsWith(RequestData.FULL_LITERAL)) {
            errors.rejectValue("region", "region.invalid.start");
        }

        if (!requestData.isRegionFull()) {
            if (requestData.isRegionPercentage()) {
                regionValue = requestParser.removePercentageLiteral(regionValue);
            }

            String[] coords = requestParser.splitParameter(regionValue);
            validateCoordinates(requestData, coords, errors);
        }

    }

    private void validateCoordinates(RequestData requestData, String[] coords, Errors errors) {

        if (coords.length != 4) {
            errors.rejectValue("region", "region.missing");
            return;
        }

        for (int i = 0; i < coords.length; i++) {
            if (StringUtils.isEmpty(coords[i])) {
                errors.rejectValue("region", "region.missing");
            }
            if (!coords[i].matches("\\d+\\.?\\d*")) {
                errors.rejectValue("region", "region.nan");
            }
        }

        if (coords[2].equals("0") || coords[3].equals("0")) {
            throw new ImageServiceException("region width or height must not be zero", 400, ParameterName.REGION);
        }

    }

    private void validateFormat(RequestData requestData) {

        if (StringUtils.isEmpty(requestData.getFormat())) {
            requestData.setFormat(ImageFormat.JPG.toString()
                                                 .toLowerCase());
        }

        try {
            ImageFormat.valueOf(requestData.getFormat()
                                           .toUpperCase());
        } catch (IllegalArgumentException iae) {
            throw new ImageServiceException("unknown format [" + requestData.getFormat() + "]", 415,
                    ParameterName.FORMAT);
        }

        if (requestData.getFormat()
                       .equalsIgnoreCase(ImageFormat.JP2.name()) && !StringUtils.join(ImageIO.getWriterFormatNames())
                                                                                .contains("JPEG2000")) {
            throw new ImageServiceException("jp2 not supported", 415, ParameterName.FORMAT);
        }

    }

}
