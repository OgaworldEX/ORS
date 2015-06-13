package http;

import java.io.Serializable;

public class HttpBase implements Serializable{
    private static final long serialVersionUID = -5031671699939959853L;

    protected byte [] rawMessage;

    public HttpBase(byte[] rawMessage){
        this.rawMessage = rawMessage;
    }
}
