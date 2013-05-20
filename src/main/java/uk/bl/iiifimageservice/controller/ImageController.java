package uk.bl.iiifimageservice.controller;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.bl.iiifimageservice.domain.ImageMetadata;
import uk.bl.iiifimageservice.domain.RequestData;
import uk.bl.iiifimageservice.service.ImageService;

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
    private ImageService imageService;

    @RequestMapping(value = "/{identifier}/info", method = RequestMethod.GET)
    public @ResponseBody
    Callable<ImageMetadata> getImageMetadata(@PathVariable final String identifier) {

        log.debug("Extracting metadata for image file with identifier [" + identifier + "]");

        return new Callable<ImageMetadata>() {
            @Override
            public ImageMetadata call() throws Exception {
                return imageService.extractImageMetadata(identifier);
            }
        };
    }

    @RequestMapping(value = "/{identifier}/{region}/{size}/{rotation}/{quality}.{format}", method = RequestMethod.GET)
    public @ResponseBody
    Callable<byte[]> getImage(final RequestData requestData) {

        log.debug("requesting image for [" + requestData.toString() + "]");

        return new Callable<byte[]>() {
            @Override
            public byte[] call() throws Exception {
                return imageService.extractImage(requestData);
            }
        };
    }

}
