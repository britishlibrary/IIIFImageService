package uk.bl.iiifimageservice.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import uk.bl.iiifimageservice.service.filelocation.FileLocationStrategy;

/**
 * Interacts with file system. Checks for existence of files.
 * 
 * @author pblake
 * 
 */
@Service
public class FileSystemReader {

    @Autowired
    @Qualifier("fileLocationStrategyName")
    private FileLocationStrategy fileLocationStrategy;

    public Path getImagePathFromIdentifier(String identifier) {
        return fileLocationStrategy.getImagePath(identifier);
    }

    public boolean imageFileExists(String identifier) {
        Path path = getImagePathFromIdentifier(identifier);
        return Files.exists(path) && !Files.isDirectory(path);
    }

    public Path getLogFileFromIdentifier(String identifier) {
        return fileLocationStrategy.getLogPath(identifier);
    }

    public boolean logFileExists(String identifier) {
        Path path = getLogFileFromIdentifier(identifier);
        return Files.exists(path) && !Files.isDirectory(path);
    }

    public Path getOutputFilename(String identifier) {
        return fileLocationStrategy.getExtractedImagePath(identifier);
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
