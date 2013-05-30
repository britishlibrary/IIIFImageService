package uk.bl.iiifimageservice.service.kakaduextractor;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import uk.bl.iiifimageservice.domain.RequestData;
import uk.bl.iiifimageservice.service.AbstractImageService;

/**
 * Implementation of the ImageService interface using the kdu_expand binary.
 * 
 * @author pblake
 * 
 */
@Service(value = "SimpleKakaduExtractor")
public class SimpleKakaduExtractor extends AbstractImageService {

    private static final Logger log = LoggerFactory.getLogger(SimpleKakaduExtractor.class);

    @Override
    public byte[] extractImage(RequestData requestData) throws InterruptedException, IOException {

        log.debug("using SimpleKakaduExtractor");

        return super.extractImage(requestData);
    }

}
