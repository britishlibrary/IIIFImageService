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
package uk.bl.iiifimageservice.service;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import uk.bl.iiifimageservice.domain.ImageError.ParameterName;
import uk.bl.iiifimageservice.domain.ImageMetadata;
import uk.bl.iiifimageservice.domain.ImageQuality;
import uk.bl.iiifimageservice.domain.RequestData;
import uk.bl.iiifimageservice.service.kakaduextractor.KakaduParameterCalculator;
import uk.bl.iiifimageservice.util.ImageServiceException;

/**
 * Once the requested region has been extracted from Kakadu the image is resized, rotated etc here.
 * 
 * @author pblake
 * 
 */
@Service
public class ImageManipulator {

    private static final Logger log = LoggerFactory.getLogger(ImageManipulator.class);

    @Resource
    private RegionSizeCalculator regionSizeCalculator;

    @Resource
    protected KakaduParameterCalculator kakaduParameterCalculator;

    public BufferedImage changeImage(BufferedImage extractedImage, RequestData requestData, ImageMetadata imageMetadata) {

        Dimension requestedSize = new Dimension();
        if (isSizeExact(requestData, imageMetadata)) {
            requestedSize.width = extractedImage.getWidth();
            requestedSize.height = extractedImage.getHeight();
        } else {
            requestedSize = regionSizeCalculator.getSizeForImageManipulation(imageMetadata, requestData);
        }

        zeroSizeCheck(requestedSize);
        log.debug("result image size [" + requestedSize.toString() + "]");

        if (requestData.isARotation()) {
            requestedSize = regionSizeCalculator.resizeForRotation(requestData, requestedSize);
            zeroSizeCheck(requestedSize);
            log.debug("result image size for rotation [" + requestedSize.toString() + "]");
            extractedImage = rotate(extractedImage, requestData);
        }

        BufferedImage resizedImage = new BufferedImage(requestedSize.width, requestedSize.height, getImageType(
                requestData, extractedImage));
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(extractedImage, 0, 0, requestedSize.width, requestedSize.height, null);
        g.dispose();

        return resizedImage;

    }

    /**
     * It's possible that the Kakadu generated image matches the exact size requested. Where the scale factor returned
     * by the log file matches the scaling requested then no resizing is performed by this image service.
     * 
     * @param requestData
     * @param imageMetadata
     * @return
     */
    private boolean isSizeExact(RequestData requestData, ImageMetadata imageMetadata) {

        boolean isSizeExact = false;
        BigDecimal scale = kakaduParameterCalculator.calculateScale(imageMetadata, requestData);

        int possibleScaleFactor = 0;
        try {
            possibleScaleFactor = BigDecimal.ONE.divide(scale, new MathContext(10, RoundingMode.HALF_EVEN))
                                                .intValueExact();
        } catch (ArithmeticException ae) {
            isSizeExact = false;
        }

        if (imageMetadata.getScaleFactors()
                         .contains(possibleScaleFactor)) {
            log.debug("requested resize matches scale factor [" + possibleScaleFactor + "] exactly");
            isSizeExact = true;
        }

        return isSizeExact;
    }

    private void zeroSizeCheck(Dimension requestedSize) {

        if (requestedSize.width == 0 || requestedSize.height == 0) {
            throw new ImageServiceException("image width or height is zero", 400, ParameterName.SIZE);
        }

    }

    private BufferedImage rotate(BufferedImage image, RequestData requestData) {

        int width = image.getWidth();
        int height = image.getHeight();

        int newWidth = image.getWidth();
        int newHeight = image.getHeight();

        if (requestData.getRotation() == 90 || requestData.getRotation() == 270) {
            newWidth = image.getHeight();
            newHeight = image.getWidth();
        }

        BufferedImage result = new BufferedImage(newWidth, newHeight, image.getType());

        Graphics2D g = result.createGraphics();

        BigDecimal two = new BigDecimal(2);
        g.translate(new BigDecimal(newWidth - width).divide(two, RoundingMode.UP)
                                                    .intValue(),
                new BigDecimal(newHeight - height).divide(two, RoundingMode.UP)
                                                  .intValue());
        g.rotate(Math.toRadians(requestData.getRotation()), new BigDecimal(width).divide(two, RoundingMode.UP)
                                                                                 .intValue(),
                new BigDecimal(height).divide(two, RoundingMode.UP)
                                      .intValue());
        g.drawRenderedImage(image, null);
        g.dispose();

        return result;
    }

    private int getImageType(RequestData requestData, BufferedImage extractedImage) {

        ImageQuality imageQuality = ImageQuality.valueOf(requestData.getQuality()
                                                                    .toUpperCase());
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
