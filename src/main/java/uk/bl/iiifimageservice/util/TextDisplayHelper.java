package uk.bl.iiifimageservice.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class TextDisplayHelper {

    public <E extends Enum<E>> List<String> getDisplayValuesFrom(E[] enumValues) {

        List<String> values = new ArrayList<>();
        for (E enumItem : enumValues) {
            values.add(enumItem.name().toLowerCase());
        }

        return values;

    }

}
