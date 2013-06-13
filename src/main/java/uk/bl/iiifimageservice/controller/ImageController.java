package uk.bl.iiifimageservice.controller;

import java.util.concurrent.Callable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import uk.bl.iiifimageservice.domain.ImageMetadata;
import uk.bl.iiifimageservice.domain.RequestData;
import uk.bl.iiifimageservice.service.ImageService;
import uk.bl.iiifimageservice.util.ImageServiceException;

/**
 * Implements the International Image Interoperability Framework Image API 1.0 This version makes asynchronous calls to
 * the kdu_expand to extract browser-friendly images from jp2 files. It also extracts image metadata as xml and json.
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

    @Resource(name = "kakaduExtractorStrategyName")
    protected ImageService imageService;

    @Resource
    private RequestValidator requestValidator;

    @Resource
    private ControllerHelper controllerHelper;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(requestValidator);
    }

    @RequestMapping(value = "/{identifier}/info", method = RequestMethod.GET)
    public @ResponseBody
    Callable<ImageMetadata> getImageMetadata(final @PathVariable String identifier, HttpServletRequest request,
            HttpServletResponse response) {

        controllerHelper.validateRequestUri(request.getRequestURI());
        log.info("Extracting metadata for image file with identifier [" + identifier + "]");
        controllerHelper.addLinkHeader(response);

        return new Callable<ImageMetadata>() {
            @Override
            public ImageMetadata call() throws Exception {
                return imageService.extractImageMetadata(identifier);
            }
        };
    }

    @RequestMapping(value = { "/{identifier}/{region}/{size}/{rotation}/{quality}.{format}",
            "/{identifier}/{region}/{size}/{rotation}/{quality}**" }, method = RequestMethod.GET)
    public @ResponseBody
    Callable<byte[]> getImage(final @Valid RequestData requestData, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        controllerHelper.validateRequestUri(request.getRequestURI());
        log.info("requesting image for [" + requestData.toString() + "]");
        controllerHelper.addLinkHeader(response);

        return new Callable<byte[]>() {
            @Override
            public byte[] call() throws Exception {
                return imageService.extractImage(requestData);
            }
        };
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

        log.info("bindException occured [" + bindException.getMessage() + "]");
        ImageError imageError = controllerHelper.extractErrorFrom(bindException);
        String errorAsXml = controllerHelper.convertImageErrorToXml(imageError);

        return new ResponseEntity<String>(errorAsXml, controllerHelper.createExceptionHeaders(), HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler
    @ResponseBody
    public ResponseEntity<String> handleException(ImageServiceException imageServiceException,
            HttpServletResponse response) {

        log.info("imageServiceException occured [" + imageServiceException.getMessage() + "]");
        ImageError imageError = controllerHelper.extractErrorFrom(imageServiceException);
        String errorAsXml = controllerHelper.convertImageErrorToXml(imageError);

        return new ResponseEntity<String>(errorAsXml, controllerHelper.createExceptionHeaders(),
                HttpStatus.valueOf(imageServiceException.getStatusCode()));

    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<String> handleException(Exception exception, HttpServletResponse response) {

        if (!(exception.getCause() instanceof java.net.SocketException)) {
            // skip client aborts
            log.error("exception occured [" + exception.getMessage() + "]", exception);
        }

        ImageError imageError = controllerHelper.extractErrorFrom(exception);
        String errorAsXml = controllerHelper.convertImageErrorToXml(imageError);

        return new ResponseEntity<String>(errorAsXml, controllerHelper.createExceptionHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @PostConstruct
    public void logStartupInfo() {
        // is jp2 configured?
        log.info("Available write formats from ImageIO api [" + StringUtils.join(ImageIO.getWriterFormatNames(), ", ")
                + "]");
        // other useful info
        log.info("user.home for properties file location [" + System.getProperty("user.home") + "]");
        log.info("java.io.tmpdir temporary file location [" + System.getProperty("java.io.tmpdir") + "]");

    }

}
