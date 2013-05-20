package uk.bl.iiifimageservice.util;

import java.util.Arrays;

import uk.bl.iiifimageservice.domain.ImageMetadata;

public class ImageMetadataGenerator {

    public static ImageMetadata getTestImageMetadata() {

        ImageMetadata imageMetadata = new ImageMetadata();

        imageMetadata.setIdentifier("1E34750D-38DB-4825-A38A-B60A345E591C");
        imageMetadata.setWidth(4700);
        imageMetadata.setHeight(6500);
        imageMetadata.setScaleFactors(Arrays.asList(0, 1, 4, 9, 16, 25));
        imageMetadata.setTileWidth(4700);
        imageMetadata.setTileHeight(6500);
        imageMetadata.setFormats(Arrays.asList("jpg", "png", "gif"));
        imageMetadata.setQualities(Arrays.asList("native", "grey", "color", "bitonal"));

        return imageMetadata;

    }

}
