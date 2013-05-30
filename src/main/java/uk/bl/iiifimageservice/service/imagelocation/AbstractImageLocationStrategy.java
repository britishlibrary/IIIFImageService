package uk.bl.iiifimageservice.service.imagelocation;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractImageLocationStrategy implements ImageLocationStrategy {

    @Value("${image.root.path}")
    protected String imageRootPath;

    @Value("${image.file.extension}")
    protected String imageFileExtension;

    @Value("${log.file.extension}")
    protected String logFileExtension;

    @Value("${temporary.file.path}")
    protected String temporaryFilePath;

    @Override
    public abstract Path getImagePath(String identifier);

    @Override
    public Path getLogPath(String identifier) {
        return getWritePath(identifier, logFileExtension);
    }

    @Override
    public Path getExtractedImagePath(String identifier, String extension) {
        return getWritePath(identifier, extension);
    }

    protected Path getWritePath(String identifier, String extension) {
        return Paths.get(temporaryFilePath, identifier + extension);
    }

}
