package uk.bl.iiifimageservice.service;

import java.io.IOException;

import uk.bl.iiifimageservice.domain.ImageMetadata;
import uk.bl.iiifimageservice.domain.RequestData;

public interface ImageService {

    public ImageMetadata extractImageMetadata(String identifier);

    public byte[] extractImage(RequestData requestData) throws InterruptedException, IOException;

}
