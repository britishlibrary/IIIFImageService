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
