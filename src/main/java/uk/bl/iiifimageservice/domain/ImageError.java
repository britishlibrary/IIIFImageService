package uk.bl.iiifimageservice.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "error")
@XmlType(propOrder = { "parameterName", "message" })
public class ImageError {

    public ImageError() {
    }

    public ImageError(ParameterName parameterName, String message) {
        this.parameterName = parameterName;
        this.message = message;
    }

    public enum ParameterName {
        // @formatter:off
        @XmlEnumValue("unknown") UNKNOWN, 
        @XmlEnumValue("identifier") IDENTIFIER, 
        @XmlEnumValue("region") REGION, 
        @XmlEnumValue("size") SIZE, 
        @XmlEnumValue("rotation") ROTATION, 
        @XmlEnumValue("quality") QUALITY, 
        @XmlEnumValue("format") FORMAT
        // @formatter:on
    }

    private ParameterName parameterName;

    private String message;

    @XmlElement(name = "parameter")
    public ParameterName getParameterName() {
        return parameterName;
    }

    public void setParameterName(ParameterName parameterName) {
        this.parameterName = parameterName;
    }

    @XmlElement(name = "text")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Error [parameterName=" + parameterName + ", message=" + message + "]";
    }

}