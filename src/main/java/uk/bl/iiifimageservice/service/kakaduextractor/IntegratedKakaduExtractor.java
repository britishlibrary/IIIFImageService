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

    @Override
    public byte[] extractImage(RequestData requestData) throws InterruptedException, IOException {

        if (!requestData.isFormatJpg()) {
            return super.extractImage(requestData);
        }

        log.debug("Integrated extraction of image using parameters [" + requestData + "]");

        ImageMetadata jp2ImageMetadata = extractImageMetadata(requestData.getIdentifier());

        Path jpgFile = fileSystemReader.getOutputFilename(requestData.getIdentifier(), ".jpg");
        // create output .jpg file
        callShellCommand(buildExtractImageCommandString(integratedBinaryPath, requestData, jp2ImageMetadata, jpgFile));

        BufferedImage jpgInputImage = ImageIO.read(jpgFile.toFile());
        Files.delete(jpgFile);

        BufferedImage manipulatedImage = imageManipulator.resizeImage(jpgInputImage, requestData, jp2ImageMetadata);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(manipulatedImage, ImageFormat.JPG.name(), byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }

    }

}
