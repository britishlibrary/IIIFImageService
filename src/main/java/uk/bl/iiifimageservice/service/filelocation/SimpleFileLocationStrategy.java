package uk.bl.iiifimageservice.service.filelocation;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

/**
 * Files are read based on the identifier in the request plus file extension alone. e.g. For an image.root.path property
 * of "C:\\JP2Path", image.file.extension property of "jp2" and identifier of "AmericanRegister" the image file is read
 * from C:\JP2Cache\AmericanRegister.jp2
 * 
 * @author pblake
 * 
 */
@Service(value = "Simple")
public class SimpleFileLocationStrategy extends AbstractFileLocationStrategy {

    @Override
    public Path getImagePath(String identifier) {
        return Paths.get(imageRootPath, identifier + imageFileExtension);
    }

}
