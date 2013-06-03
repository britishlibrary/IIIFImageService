package uk.bl.iiifimageservice.service;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.bl.iiifimageservice.domain.ImageMetadata;
import uk.bl.iiifimageservice.domain.ImageQuality;
import uk.bl.iiifimageservice.domain.RequestData;
import uk.bl.iiifimageservice.service.kakaduextractor.SimpleKakaduExtractor;

/**
 * Once the requested region has been extracted from Kakadu the image is resized, rotated etc here.
 * 
 * @author pblake
 * 
 */
@Service
public class ImageManipulator {

    private static final Logger log = LoggerFactory.getLogger(SimpleKakaduExtractor.class);

    @Autowired
    private RegionSizeCalculator regionSizeCalculator;

    public BufferedImage resizeImage(BufferedImage extractedImage, RequestData requestData,
            ImageMetadata jp2ImageMetadata) {

        Dimension requestedSize = regionSizeCalculator.getSizeForImageManipulation(jp2ImageMetadata, requestData);
        requestedSize = regionSizeCalculator.resizeForRotation(requestData, requestedSize);
        log.debug("image size (possibly calculated) [" + requestedSize.toString() + "]");

        BufferedImage resizedImage = new BufferedImage(requestedSize.width, requestedSize.height, getImageType(
                requestData, extractedImage));
        Graphics2D g = resizedImage.createGraphics();

        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (requestData.isARotation()) {
            rotateImage(extractedImage, g, requestData, requestedSize);
            return resizedImage;
        }
        g.drawImage(extractedImage, 0, 0, requestedSize.width, requestedSize.height, null);

        g.dispose();

        return resizedImage;

    }

    private void rotateImage(BufferedImage extractedImage, Graphics2D g, RequestData requestData,
            Dimension requestedSize) {

        log.debug("rotating image by [" + requestData.getRotation() + "] degrees");

        double theta = Math.toRadians(requestData.getRotation());
        AffineTransform transform = new AffineTransform();

        if (requestData.getRotation() == 180) {
            g.rotate(theta, 0.5 * requestedSize.getWidth(), 0.5 * requestedSize.getHeight());
        } else {
            transform.translate(0.5 * requestedSize.getWidth(), 0.5 * requestedSize.getHeight());
            transform.rotate(theta);
            transform.translate(-0.5 * requestedSize.getHeight(), -0.5 * requestedSize.getWidth());
        }
        g.drawImage(extractedImage, transform, null);
        g.dispose();

    }

    private int getImageType(RequestData requestData, BufferedImage extractedImage) {

        ImageQuality imageQuality = ImageQuality.valueOf(requestData.getQuality().toUpperCase());
        switch (imageQuality) {

        case COLOR:
            return BufferedImage.TYPE_INT_RGB;

        case GREY:
            return BufferedImage.TYPE_BYTE_GRAY;

        case BITONAL:
            return BufferedImage.TYPE_BYTE_BINARY;

        default:
            return extractedImage.getType();
        }

    }
}
