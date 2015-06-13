package http;

import java.io.Serializable;

public class HttpResponseLine implements Serializable{
    private static final long serialVersionUID = 247627707439253216L;

    private String version;
    private String status;
    private String message;

    public HttpResponseLine(String firstHeader){
        String [] firstHeaderArray = firstHeader.split(" ");
        this.version = firstHeaderArray[0];
        this.status = firstHeaderArray[1];
        this.message =firstHeaderArray[2];
    }

    public String getVersion() {
        return version;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
