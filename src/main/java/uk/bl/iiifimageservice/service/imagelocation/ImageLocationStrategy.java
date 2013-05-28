package uk.bl.iiifimageservice.service.imagelocation;

import java.nio.file.Path;

/**
 * The conversion of the identifier (embedded in the http request) to the location on disk of the image file may vary
 * across systems. Implementing this interface will enable alternate strategies. To enable a strategy specify the class
 * name as a property called 'file.location.strategy' in the config.properties file.
 * 
 * There are two currently defined - {@link SimpleImageLocationStrategy} and
 * {@link DirectoryFileNoExtensionImageLocationStrategy}
 * 
 * @author pblake
 * 
 */
public interface ImageLocationStrategy {

    public Path getImagePath(String identifier);

    public Path getLogPath(String identifier);

    public Path getExtractedImagePath(String identifier);

}
