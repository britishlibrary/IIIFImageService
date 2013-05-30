package uk.bl.iiifimageservice.service.kakaduextractor;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
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
@Service(value = "PipedKakaduExtractor")
public class PipedKakaduExtractor extends AbstractImageService {

    private static final Logger log = LoggerFactory.getLogger(PipedKakaduExtractor.class);

    @Value("${piped.shell.path}")
    protected String pipedShellPath;

    @Override
    public byte[] extractImage(RequestData requestData) throws InterruptedException, IOException {

        if (!requestData.isFormatJpg()) {
            return super.extractImage(requestData);
        }

        log.debug("Piped extraction of image using parameters [" + requestData + "]");

        ImageMetadata jp2ImageMetadata = extractImageMetadata(requestData.getIdentifier());

        Path jpgFile = fileSystemReader.getOutputFilename(requestData.getIdentifier(), ".jpg");
        // create output .jpg file
        callShellCommand(buildPipedExtractImageCommandString(requestData, jp2ImageMetadata, jpgFile));

        BufferedImage jpgInputImage = ImageIO.read(jpgFile.toFile());
        Files.delete(jpgFile);

        BufferedImage manipulatedImage = imageManipulator.resizeImage(jpgInputImage, requestData, jp2ImageMetadata);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(manipulatedImage, ImageFormat.JPG.name(), byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }

    }

    private String[] buildPipedExtractImageCommandString(RequestData requestData, ImageMetadata imageMetadata,
            Path jpgPath) {

        int reduce = kakaduParameterCalculator.calculateReduceParameter(imageMetadata, requestData);

        String jp2ImageFilename = fileSystemReader.getImagePathFromIdentifier(requestData.getIdentifier()).toString();

        String[] command = new String[] { pipedShellPath, jp2ImageFilename, jpgPath.toString(), "-resilient", "-quiet",
                "-reduce", String.valueOf(reduce) };

        command = addRegionCommand(imageMetadata, requestData, command);

        log.debug("Extract image and pipe to cjpeg shell command [" + StringUtils.join(command, " ") + "]");

        return command;

    }

}
