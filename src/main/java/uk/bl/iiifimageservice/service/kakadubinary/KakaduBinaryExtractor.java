package uk.bl.iiifimageservice.service.kakadubinary;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import uk.bl.iiifimageservice.domain.ImageMetadata;
import uk.bl.iiifimageservice.domain.RequestData;
import uk.bl.iiifimageservice.service.FileSystemReader;
import uk.bl.iiifimageservice.service.ImageManipulator;
import uk.bl.iiifimageservice.service.ImageService;
import uk.bl.iiifimageservice.service.LogFileExtractor;

/**
 * Implementation of the ImageService interface using the kdu_expand binary.
 * 
 * @author pblake
 * 
 */
@Service
public class KakaduBinaryExtractor implements ImageService {

    private static final Logger log = LoggerFactory.getLogger(KakaduBinaryExtractor.class);

    @Value("${kakadu.binary.path}")
    private String kakaduBinaryPath;

    @Autowired
    private LogFileExtractor logFileExtractor;

    @Autowired
    private FileSystemReader fileSystemReader;

    @Autowired
    private KakaduParameterCalculator kakaduParameterCalculator;

    @Autowired
    private ImageManipulator imageManipulator;

    @Override
    public byte[] extractImage(RequestData requestData) throws InterruptedException, IOException {

        ImageMetadata jp2ImageMetadata = extractImageMetadata(requestData.getIdentifier());

        log.debug("Calling kdu_expand to extract image using [" + requestData + "]");

        Path bmpFile = fileSystemReader.getOutputFilename(requestData.getIdentifier());

        ProcessBuilder processBuilder = new ProcessBuilder(buildExtractImageCommandString(requestData,
                jp2ImageMetadata, bmpFile));
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        process.waitFor();

        // read in bmp
        log.debug("Reading in extracted file from [" + bmpFile.toString() + "]");
        BufferedImage bmpInputImage = ImageIO.read(bmpFile.toFile());

        Files.delete(bmpFile);

        BufferedImage manipulatedImage = imageManipulator.resizeImage(bmpInputImage, requestData, jp2ImageMetadata);

        // now write bmp in-memory image to output stream in requested format
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(manipulatedImage, requestData.getFormat(), byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public ImageMetadata extractImageMetadata(String identifier) {

        log.debug("Extracting metadata in different thread for jp2 file with identifier [" + identifier + "]");

        if (StringUtils.isEmpty(identifier)) {
            return null;
        }

        if (!fileSystemReader.imageFileExists(identifier)) {
            return null;
        }

        if (!fileSystemReader.logFileExists(identifier)) {
            generateKduLogFile(identifier);
        }

        return loadAndParseLogFile(identifier);

    }

    private void generateKduLogFile(String identifier) {

        log.debug("Calling kdu_expand.exe to extract metadata for identifier [" + identifier + "]");

        String imageFilename = fileSystemReader.getImagePathFromIdentifier(identifier).toString();
        String logFilename = fileSystemReader.getLogFileFromIdentifier(identifier).toString();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(kakaduBinaryPath, "-i", imageFilename, "-record",
                    logFilename);
            log.debug("Calling shell command [" + StringUtils.join(processBuilder.command(), " ") + "]");
            Process process = processBuilder.start();
            process.waitFor();
        } catch (InterruptedException | IOException e) {
            // TODO add error handling
            e.printStackTrace();
        }

    }

    private ImageMetadata loadAndParseLogFile(String identifier) {

        String logFile = fileSystemReader.readFile(fileSystemReader.getLogFileFromIdentifier(identifier));

        log.debug("identifier [" + identifier + "] produces log file [" + logFile + "]");

        return logFileExtractor.extractImageMetadata(identifier, logFile);
    }

    private String[] buildExtractImageCommandString(RequestData requestData, ImageMetadata imageMetadata, Path bmpPath) {

        int reduce = kakaduParameterCalculator.calculateReduceParameter(imageMetadata, requestData);
        String regionCommandValue = kakaduParameterCalculator.calculateRegionExtractionParameter(imageMetadata, requestData);

        String jp2ImageFilename = fileSystemReader.getImagePathFromIdentifier(requestData.getIdentifier()).toString();

        String[] commandParameters = new String[] { kakaduBinaryPath, "-resilient", "-quiet", "-reduce",
                String.valueOf(reduce), "-i", jp2ImageFilename, "-o", bmpPath.toString() };

        if (!StringUtils.isEmpty(regionCommandValue)) {
            commandParameters = Arrays.copyOf(commandParameters, commandParameters.length + 2);
            commandParameters[commandParameters.length - 2] = "-region";
            commandParameters[commandParameters.length - 1] = regionCommandValue;
        }

        log.debug("Extract image shell command [" + StringUtils.join(commandParameters, " ") + "]");

        return commandParameters;

    }
}
