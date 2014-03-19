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

import java.io.StringWriter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import uk.bl.iiifimageservice.domain.ImageError;
import uk.bl.iiifimageservice.domain.ImageError.ParameterName;
import uk.bl.iiifimageservice.service.ImageService;
import uk.bl.iiifimageservice.util.ImageServiceException;

@Component
public class ControllerHelper {

    @Resource
    private MessageSource messageSource;

    @Resource
    private Jaxb2Marshaller marshaller;

    @Resource(name = "kakaduExtractorStrategyName")
    protected ImageService imageService;

    private static final Logger log = LoggerFactory.getLogger(ControllerHelper.class);

    protected ImageError extractErrorFrom(BindException bindException) {

        ParameterName parameterName = ParameterName.valueOf(bindException.getFieldError()
                                                                         .getField()
                                                                         .toUpperCase());
        String errorMessage = messageSource.getMessage(bindException.getFieldError()
                                                                    .getCode(), null, null);
        return new ImageError(parameterName, errorMessage);

    }

    protected ImageError extractErrorFrom(ImageServiceException imageServiceException) {

        return new ImageError(imageServiceException.getParameterName(), imageServiceException.getMessage());

    }

    protected ImageError extractErrorFrom(Exception exception) {

        String message = exception.getMessage();
        if (StringUtils.isEmpty(message)) {
            message = exception.getClass()
                               .getName();
        }
        return new ImageError(ParameterName.UNKNOWN, message);

    }

    protected String convertImageErrorToXml(ImageError imageError) {

        if (StringUtils.isEmpty(imageError.getMessage())) {
            imageError.setMessage("unknown error");
        }

        // ugly - see Spring Jira SPR-9878
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        marshaller.marshal(imageError, result);

        return writer.toString();
    }

    protected HttpHeaders createExceptionHeaders() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        headers.add("Link", getLinkHeaderValue());

        return headers;

    }

    protected void addLinkHeader(HttpServletResponse response) {

        response.addHeader("Link", getLinkHeaderValue());

    }

    protected String getLinkHeaderValue() {
        return "<" + imageService.getComplianceLevelUrl() + ">;rel=\"profile\"";
    }

    protected void validateRequestUri(String requestUri) {

        log.debug("request uri [" + requestUri + "]");
        if (requestUri.length() > 1024) {
            throw new ImageServiceException("maximum length of URI is 1024", 414, ParameterName.UNKNOWN);
        }
    }

}
