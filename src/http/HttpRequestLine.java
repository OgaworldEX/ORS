package http;

import java.io.Serializable;

public class HttpRequestLine implements Serializable{
    private static final long serialVersionUID = 8652534022007618851L;

    private String method;
    private String path;
    private String version;

    public HttpRequestLine(String firstHeader){
        String [] firstHeaderArray = firstHeader.split(" ");
        this.method = firstHeaderArray[0];
        this.path = firstHeaderArray[1];
        this.version =firstHeaderArray[2];
    }

    public String getRequestLineStringNoCRLF(){
        return method + " " + path + " " + version;
    }

    //setter getter
    public String getMethod() {
        return method;
    }
    public String getPath() {
        return path;
    }
    public String getVersion() {
        return version;
    }
}
