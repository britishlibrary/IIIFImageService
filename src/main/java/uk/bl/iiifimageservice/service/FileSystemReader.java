package uk.bl.iiifimageservice.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import uk.bl.iiifimageservice.service.imagelocation.ImageLocationStrategy;

/**
 * Interacts with file system. Checks for existence of and reads files.
 * 
 * @author pblake
 * 
 */
@Service
public class FileSystemReader {

    private static final Logger log = LoggerFactory.getLogger(FileSystemReader.class);

    @Autowired
    @Qualifier("imageLocationStrategyName")
    private ImageLocationStrategy imageLocationStrategy;

    public Path getImagePathFromIdentifier(String identifier) {
        return imageLocationStrategy.getImagePath(identifier);
    }

    public boolean imageFileExists(String identifier) {
        Path path = getImagePathFromIdentifier(identifier);
        return Files.exists(path) && !Files.isDirectory(path);
    }

    public Path getLogFileFromIdentifier(String identifier) {
        return imageLocationStrategy.getLogPath(identifier);
    }

    public boolean logFileExists(String identifier) {
        Path path = getLogFileFromIdentifier(identifier);
        return Files.exists(path) && !Files.isDirectory(path);
    }

    public Path getOutputFilename() {
        Path outputFilename = imageLocationStrategy.getExtractedImagePath(UUID.randomUUID().toString(), ".bmp");
        log.debug("UUID generated path [" + outputFilename.toString() + "]");
        return outputFilename;
    }

    public Path getOutputFilename(String identifier, String extension) {
        return imageLocationStrategy.getExtractedImagePath(identifier, extension);
    }

    public String readFile(Path path) {

        String logFileContents = null;
        try {
            logFileContents = new String(Files.readAllBytes(path));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return logFileContents;
    }

}
