package http;

import java.io.Serializable;

public class HttpMessage implements Serializable{
    private static final long serialVersionUID = -1114764212343485987L;

    private HttpRequest httpRequest;
    private HttpResponse httpResponse;

    public HttpMessage(HttpRequest request){
        this.httpRequest = request;
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }
    public void setHttpRequest(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }
    public HttpResponse getHttpResponse() {
        return httpResponse;
    }
    public void setHttpResponse(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }
}
