package uk.bl.iiifimageservice.util;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import uk.bl.iiifimageservice.domain.ImageFormat;

public class TextDisplayHelperTest {

    TextDisplayHelper textDisplayHelper = new TextDisplayHelper();

    @Test
    public void getValuesTest() {

        List<String> values = textDisplayHelper.getDisplayValuesFrom(ImageFormat.values());
        textDisplayHelper.getDisplayValuesFrom(ImageFormat.values());

        assertTrue("error with image formats", Arrays.asList("jpg", "png", "gif", "jp2").equals(values));

    }

}
