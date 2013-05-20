package uk.bl.iiifimageservice.util;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import uk.bl.iiifimageservice.domain.ImageFormat;
import uk.bl.iiifimageservice.util.TextDisplayHelper;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration
public class TextDisplayHelperTest {

//    @Autowired
    TextDisplayHelper textDisplayHelper =  new TextDisplayHelper();

    @Test
    public void getValuesTest() {

        List<String> values = textDisplayHelper.getDisplayValuesFrom(ImageFormat.values());
        textDisplayHelper.getDisplayValuesFrom(ImageFormat.values());

        assertTrue("", Arrays.asList("jpg", "png", "gif").equals(values));

    }

}
