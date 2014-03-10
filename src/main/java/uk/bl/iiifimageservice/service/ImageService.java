package uk.bl.iiifimageservice.service;

import java.io.IOException;

import uk.bl.iiifimageservice.domain.ImageMetadata;
import uk.bl.iiifimageservice.domain.ImageRequestMetadata;
import uk.bl.iiifimageservice.domain.RequestData;
import uk.bl.iiifimageservice.domain.ServerRequestData;

public interface ImageService {

    public ImageMetadata extractImageMetadata(String identifier) throws IOException, InterruptedException;

    public ImageRequestMetadata extractImageMetadata(ServerRequestData serverRequestData) throws IOException,
            InterruptedException;

    public byte[] extractImage(RequestData requestData) throws InterruptedException, IOException;

    public String getComplianceLevelUrl();

}
