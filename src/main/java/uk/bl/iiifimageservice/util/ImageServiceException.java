package uk.bl.iiifimageservice.util;

import uk.bl.iiifimageservice.domain.ImageError.ParameterName;

public class ImageServiceException extends RuntimeException {

    private static final long serialVersionUID = 7887180486907358323L;
    private int statusCode;
    private ParameterName parameterName;

    public ImageServiceException(int statusCode, ParameterName parameterName) {
        super();
        this.statusCode = statusCode;
        this.parameterName = parameterName;
    }

    public ImageServiceException(String message, int statusCode, ParameterName parameterName) {
        super(message);
        this.statusCode = statusCode;
        this.parameterName = parameterName;
    }

    public ImageServiceException(String message, int statusCode, ParameterName parameterName, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.parameterName = parameterName;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public ParameterName getParameterName() {
        return this.parameterName;
    }

}
