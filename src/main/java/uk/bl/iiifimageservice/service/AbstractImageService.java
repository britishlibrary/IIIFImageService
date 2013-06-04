package uk.bl.iiifimageservice.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import uk.bl.iiifimageservice.domain.ImageError.ParameterName;
import uk.bl.iiifimageservice.domain.ImageFormat;
import uk.bl.iiifimageservice.domain.ImageMetadata;
import uk.bl.iiifimageservice.domain.RequestData;
import uk.bl.iiifimageservice.service.kakaduextractor.KakaduParameterCalculator;
import uk.bl.iiifimageservice.util.ImageServiceException;

public abstract class AbstractImageService implements ImageService {

    private static final Logger log = LoggerFactory.getLogger(AbstractImageService.class);

    private final static String COMPLIANCE_LEVEL_URL = "http://library.stanford.edu/iiif/image-api/compliance.html#level";
    protected final static String MISSING_IMAGE_MESSAGE = "image does not exist";

    @Value("${kakadu.binary.path}")
    protected String kakaduBinaryPath;

    @Autowired
    protected FileSystemReader fileSystemReader;

    @Autowired
    protected LogFileExtractor logFileExtractor;

    @Autowired
    protected KakaduParameterCalculator kakaduParameterCalculator;

    @Autowired
    protected ImageManipulator imageManipulator;

    @Override
    public final ImageMetadata extractImageMetadata(String identifier) throws IOException, InterruptedException {

        log.debug("Extracting metadata for jp2 file with identifier [" + identifier + "]");

        if (StringUtils.isEmpty(identifier)) {
            return null;
        }

        if (!fileSystemReader.imageFileExists(identifier)) {
            throw new ImageServiceException(MISSING_IMAGE_MESSAGE, 404, ParameterName.IDENTIFIER);
        }

        if (!fileSystemReader.logFileExists(identifier)) {
            generateKduLogFile(identifier);
        }

        ImageMetadata imageMetadata = loadAndParseLogFile(identifier);
        if (null == imageMetadata) {
            throw new ImageServiceException(MISSING_IMAGE_MESSAGE, 404, ParameterName.IDENTIFIER);
        }

        return imageMetadata;

    }

    @Override
    public byte[] extractImage(RequestData requestData) throws InterruptedException, IOException {

        log.debug("Simple extraction of image using parameters [" + requestData + "]");

        ImageMetadata jp2ImageMetadata = extractImageMetadata(requestData.getIdentifier());

        Path bmpFile = fileSystemReader.getOutputFilename();

        // create .bmp file
        callShellCommand(buildExtractImageCommandString(kakaduBinaryPath, requestData, jp2ImageMetadata, bmpFile));

        log.debug("Reading in extracted file from [" + bmpFile.toString() + "]");
        BufferedImage bmpInputImage = null;

        bmpInputImage = ImageIO.read(bmpFile.toFile());
        Files.delete(bmpFile);

        BufferedImage manipulatedImage = imageManipulator.changeImage(bmpInputImage, requestData, jp2ImageMetadata);

        String outputFormat = requestData.getFormat();
        if (outputFormat.toUpperCase().equals(ImageFormat.JP2.name())) {
            outputFormat = "JPEG2000";
        }

        // now write bmp in-memory image to output stream in requested format
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(manipulatedImage, outputFormat, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }

    }

    @Override
    public final String getComplianceLevelUrl() {
        return COMPLIANCE_LEVEL_URL + 2;
    }

    protected String[] buildExtractImageCommandString(String binaryPath, RequestData requestData,
            ImageMetadata imageMetadata, Path outputPath) {

        int reduce = kakaduParameterCalculator.calculateReduceParameter(imageMetadata, requestData);
        String jp2ImageFilename = fileSystemReader.getImagePathFromIdentifier(requestData.getIdentifier()).toString();

        String[] command = new String[] { binaryPath, "-resilient", "-quiet", "-reduce", String.valueOf(reduce), "-i",
                jp2ImageFilename, "-o", outputPath.toString() };

        command = addRegionCommand(imageMetadata, requestData, command);

        log.debug("Extract image shell command [" + StringUtils.join(command, " ") + "]");

        return command;

    }

    protected void callShellCommand(String... command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        process.waitFor();

    }

    protected String[] addRegionCommand(ImageMetadata imageMetadata, RequestData requestData, String[] command) {

        String regionCommandValue = kakaduParameterCalculator.calculateRegionExtractionParameter(imageMetadata,
                requestData);

        if (!StringUtils.isEmpty(regionCommandValue)) {
            command = Arrays.copyOf(command, command.length + 2);
            command[command.length - 2] = "-region";
            command[command.length - 1] = regionCommandValue;
        }

        return command;

    }

    private void generateKduLogFile(String identifier) throws IOException, InterruptedException {

        log.debug("Calling kdu_expand binary to extract metadata for identifier [" + identifier + "]");

        String imageFilename = fileSystemReader.getImagePathFromIdentifier(identifier).toString();
        String logFilename = fileSystemReader.getLogFileFromIdentifier(identifier).toString();

        callShellCommand(kakaduBinaryPath, "-i", imageFilename, "-record", logFilename);

    }

    private ImageMetadata loadAndParseLogFile(String identifier) {

        String logFile = fileSystemReader.readFile(fileSystemReader.getLogFileFromIdentifier(identifier));

        log.trace("identifier [" + identifier + "] produces log file [" + logFile + "]");

        return logFileExtractor.extractImageMetadata(identifier, logFile, getComplianceLevelUrl());
    }

}
