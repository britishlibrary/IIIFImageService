package uk.bl.iiifimageservice.domain;

/**
 * Gathers together the data from the request needed to generate the image metadata response
 * 
 * @author pblake
 * 
 */
public class ServerRequestData {

    private String scheme;
    private String host;
    private String contextPath;
    private String identifier;

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

}
