package uk.bl.iiifimageservice.controller;

import java.io.StringWriter;
import java.util.concurrent.Callable;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.bl.iiifimageservice.domain.ImageError;
import uk.bl.iiifimageservice.domain.ImageError.ParameterName;
import uk.bl.iiifimageservice.domain.ImageMetadata;
import uk.bl.iiifimageservice.domain.RequestData;
import uk.bl.iiifimageservice.service.ImageService;
import uk.bl.iiifimageservice.util.ImageServiceException;

/**
 * Implements the International Image Interoperability Framework Image API 1.0 This version makes asynchronous calls to
 * the kdu_expand.exe to extract browser-friendly images from jp2 files. It also extracts image metadata as xml and
 * json.
 * 
 * 
 * @see http://library.stanford.edu/iiif/image-api
 * 
 * @author pblake
 * 
 */
@Controller
public class ImageController {

    private static final Logger log = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    @Qualifier("kakaduExtractorStrategyName")
    protected ImageService imageService;

    @Autowired
    private RequestValidator requestValidator;

    @Autowired
    private Jaxb2Marshaller marshaller;

    @Autowired
    private MessageSource messageSource;

    @RequestMapping(value = "/{identifier}/info", method = RequestMethod.GET)
    public @ResponseBody
    Callable<ImageMetadata> getImageMetadata(final @PathVariable String identifier, HttpServletRequest request,
            HttpServletResponse response) {

        validateRequestUri(request.getRequestURI());
        log.info("Extracting metadata for image file with identifier [" + identifier + "]");
        addLinkHeader(response);

        return new Callable<ImageMetadata>() {
            @Override
            public ImageMetadata call() throws Exception {
                return imageService.extractImageMetadata(identifier);
            }
        };
    }

    @RequestMapping(value = "/{identifier}/{region}/{size}/{rotation}/{quality}.{format}", method = RequestMethod.GET)
    public @ResponseBody
    Callable<byte[]> getImage(final @Valid RequestData requestData, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        validateRequestUri(request.getRequestURI());
        log.info("requesting image for [" + requestData.toString() + "]");
        addLinkHeader(response);

        return new Callable<byte[]>() {
            @Override
            public byte[] call() throws Exception {
                return imageService.extractImage(requestData);
            }
        };
    }

    @PostConstruct
    public void logStartupInfo() {
        // is jp2 configured?
        log.info("Available write formats from ImageIO api [" + StringUtils.join(ImageIO.getWriterFormatNames(), ", ")
                + "]");
        log.info("user.home for properties file location [" + System.getProperty("user.home") + "]");
        log.info("java.io.tmpdir temporary file location [" + System.getProperty("java.io.tmpdir") + "]");

    }

    /**
     * If there are any request parameter bind exceptions then extract the first and send the xml response.
     * 
     * Note - the xml response is manually marshalled. Usually this should not be required. Check this Spring Framework
     * Jira for why it's a simple workaround - <a href="https://jira.springsource.org/browse/SPR-9878">SPR-9878</a>
     * 
     * @param bindException
     * @param response
     * @return
     */
    @ExceptionHandler
    @ResponseBody
    public ResponseEntity<String> handleException(BindException bindException, HttpServletResponse response) {

        log.info("bindException occured [" + bindException.getMessage() + "]", bindException);
        ImageError imageError = extractErrorFrom(bindException);
        String errorAsXml = convertImageErrorToXml(imageError);

        return new ResponseEntity<String>(errorAsXml, createExceptionHeaders(), HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler
    @ResponseBody
    public ResponseEntity<String> handleException(ImageServiceException imageServiceException,
            HttpServletResponse response) {

        log.error("imageServiceException occured [" + imageServiceException.getMessage() + "]", imageServiceException);
        ImageError imageError = extractErrorFrom(imageServiceException);
        String errorAsXml = convertImageErrorToXml(imageError);

        return new ResponseEntity<String>(errorAsXml, createExceptionHeaders(),
                HttpStatus.valueOf(imageServiceException.getStatusCode()));

    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<String> handleException(Exception exception, HttpServletResponse response) {

        if (!(exception.getCause() instanceof java.net.SocketException)) {
            // skip client aborts
            log.error("exception occured [" + exception.getMessage() + "]", exception);
        }

        ImageError imageError = extractErrorFrom(exception);
        String errorAsXml = convertImageErrorToXml(imageError);

        return new ResponseEntity<String>(errorAsXml, createExceptionHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(requestValidator);
    }

    private ImageError extractErrorFrom(BindException bindException) {

        ParameterName parameterName = ParameterName.valueOf(bindException.getFieldError().getField().toUpperCase());
        String errorMessage = messageSource.getMessage(bindException.getFieldError().getCode(), null, null);
        return new ImageError(parameterName, errorMessage);

    }

    private ImageError extractErrorFrom(ImageServiceException imageServiceException) {

        return new ImageError(imageServiceException.getParameterName(), imageServiceException.getMessage());

    }

    private ImageError extractErrorFrom(Exception exception) {

        String message = exception.getMessage();
        if (StringUtils.isEmpty(message)) {
            message = exception.getClass().getName();
        }
        return new ImageError(ParameterName.UNKNOWN, message);

    }

    private String convertImageErrorToXml(ImageError imageError) {

        if (StringUtils.isEmpty(imageError.getMessage())) {
            imageError.setMessage("unknown error");
        }

        // ugly - see Spring Jira SPR-9878
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        marshaller.marshal(imageError, result);

        return writer.toString();
    }

    private HttpHeaders createExceptionHeaders() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        headers.add("Link", getLinkHeaderValue());

        return headers;

    }

    private void addLinkHeader(HttpServletResponse response) {

        response.addHeader("Link", getLinkHeaderValue());

    }

    private String getLinkHeaderValue() {
        return "<" + imageService.getComplianceLevelUrl() + ">;rel=\"profile\"";
    }

    private void validateRequestUri(String requestUri) {

        log.debug("request uri [" + requestUri + "]");
        if (requestUri.length() > 1024) {
            throw new ImageServiceException("maximum length of URI is 1024", 414, ParameterName.UNKNOWN);
        }
    }
}
