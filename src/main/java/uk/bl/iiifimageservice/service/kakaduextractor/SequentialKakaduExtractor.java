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

import uk.bl.iiifimageservice.domain.ImageError.ParameterName;
import uk.bl.iiifimageservice.domain.ImageFormat;
import uk.bl.iiifimageservice.domain.ImageMetadata;
import uk.bl.iiifimageservice.domain.RequestData;
import uk.bl.iiifimageservice.service.AbstractImageService;
import uk.bl.iiifimageservice.util.ImageServiceException;

/**
 * If the image requested is a jpg then extract using Kakadu and then convert bmp to jpg using cjpeg
 * 
 * @author pblake
 * 
 */
@Service(value = "SequentialKakaduExtractor")
public class SequentialKakaduExtractor extends AbstractImageService {

    private static final Logger log = LoggerFactory.getLogger(SequentialKakaduExtractor.class);

    @Value("${cjpeg.binary.path}")
    protected String cjpegBinaryPath;

    @Value("${cjpeg.quality}")
    protected String cjpegQuality;

    @Override
    public byte[] extractImage(RequestData requestData) throws InterruptedException, IOException {

        if (!requestData.isFormatJpg()) {
            return super.extractImage(requestData);
        }

        log.debug("Sequential extraction of image using parameters [" + requestData + "]");

        ImageMetadata jp2ImageMetadata = extractImageMetadata(requestData.getIdentifier());

        Path bmpFile = fileSystemReader.getOutputFilename();
        // create .bmp file
        callShellCommand(buildExtractImageCommandString(kakaduBinaryPath, requestData, jp2ImageMetadata, bmpFile));

        Path jpgFile = fileSystemReader.getOutputFilename(requestData.getIdentifier(), ".jpg");
        // create .jpg file
        callShellCommand(buildConvertBmpCommandString(requestData, jp2ImageMetadata, bmpFile, jpgFile));

        Files.delete(bmpFile);
        BufferedImage jpgInputImage = ImageIO.read(jpgFile.toFile());
        Files.delete(jpgFile);

        BufferedImage manipulatedImage = imageManipulator.changeImage(jpgInputImage, requestData, jp2ImageMetadata);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(manipulatedImage, ImageFormat.JPG.name(), byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }

    }

    private String[] buildConvertBmpCommandString(RequestData requestData, ImageMetadata imageMetadata, Path inputPath,
            Path outputPath) {

        String cjpegQualitySwitch = "-quality";

        try {
            Integer.valueOf(cjpegQuality);
        } catch (NumberFormatException nfe) {
            throw new ImageServiceException("cjpeg quality value must be an integer", 500, ParameterName.UNKNOWN);
        }

        String[] command = new String[] { cjpegBinaryPath, cjpegQualitySwitch, cjpegQuality, "-outfile",
                outputPath.toString(), inputPath.toString() };

        log.debug("Convert bmp to jpg using cjpeg shell command [" + StringUtils.join(command, " ") + "]");

        return command;

    }

}
