package uk.bl.iiifimageservice.service.filelocation;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

/**
 * Files are read using the identifier in the request as both a directory name followed by a filename with no extension.
 * e.g. For an image.root.path property of "C:\\JP2Path", an empty or unspecified image.file.extension property and an
 * identifier of "AmericanRegister" the image file is read from C:\JP2Cache\AmericanRegister\AmericanRegister
 * 
 * @author pblake
 * 
 */
@Service(value = "DirectoryFileNoExtensionFileLocationStrategy")
public class DirectoryFileNoExtensionFileLocationStrategy extends AbstractFileLocationStrategy {

    @Override
    public Path getImagePath(String identifier) {
        return Paths.get(imageRootPath, identifier, identifier);
    }

}
