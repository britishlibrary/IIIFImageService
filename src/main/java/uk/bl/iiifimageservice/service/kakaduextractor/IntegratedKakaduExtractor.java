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
package uk.bl.iiifimageservice.service.kakaduextractor;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import uk.bl.iiifimageservice.domain.ImageFormat;
import uk.bl.iiifimageservice.domain.ImageMetadata;
import uk.bl.iiifimageservice.domain.RequestData;
import uk.bl.iiifimageservice.service.AbstractImageService;

/**
 * If the image requested is a jpg then extract using Kakadu and pipe bmp to cjpeg to convert to jpg
 * 
 * @author pblake
 * 
 */
@Service(value = "IntegratedKakaduExtractor")
public class IntegratedKakaduExtractor extends AbstractImageService {

    private static final Logger log = LoggerFactory.getLogger(IntegratedKakaduExtractor.class);

    @Value("${integrated.binary.path}")
    protected String integratedBinaryPath;

    @Value("${jpg.delete}")
    protected boolean jpgDelete;

    @Override
    public byte[] extractImage(RequestData requestData) throws InterruptedException, IOException {

        if (!requestData.isFormatJpg()) {
            return super.extractImage(requestData);
        }

        log.debug("Integrated extraction of image using parameters [" + requestData + "]");

        ImageMetadata jp2ImageMetadata = extractImageMetadata(requestData.getIdentifier());

        Path jpgFile = fileSystemReader.getOutputFilename(".jpg");
        // create output .jpg file
        callShellCommand(buildExtractImageCommandString(integratedBinaryPath, requestData, jp2ImageMetadata, jpgFile));

        BufferedImage jpgInputImage = ImageIO.read(jpgFile.toFile());
        if (jpgDelete) {
            Files.delete(jpgFile);
        }

        BufferedImage manipulatedImage = imageManipulator.changeImage(jpgInputImage, requestData, jp2ImageMetadata);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(manipulatedImage, ImageFormat.JPG.name(), byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }

    }

}
