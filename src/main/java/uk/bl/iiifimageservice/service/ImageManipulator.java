package uk.bl.iiifimageservice.service;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.bl.iiifimageservice.domain.ImageMetadata;
import uk.bl.iiifimageservice.domain.ImageQuality;
import uk.bl.iiifimageservice.domain.RequestData;

@Service
public class ImageManipulator {

    @Autowired
    private RegionSizeCalculator regionSizeCalculator;

    public BufferedImage resizeImage(BufferedImage extractedImage, RequestData requestData,
            ImageMetadata jp2ImageMetadata) {

        Dimension requestedSize = regionSizeCalculator.getSizeForImageManipulation(jp2ImageMetadata, requestData);

        if (requestData.isSizeBestFit()) {
            // TODO best fit resizing
            return extractedImage;
        }

        BufferedImage resizedImage = new BufferedImage(requestedSize.width, requestedSize.height, getImageType(
                requestData, extractedImage));
        Graphics2D g = resizedImage.createGraphics();

        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(extractedImage, 0, 0, requestedSize.width, requestedSize.height, null);

        if (requestData.getRotation() != 0) {
            // TODO rotation
            // g.rotate(Math.toRadians(requestData.getRotation()));
        }

        g.dispose();

        return resizedImage;

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
