package http;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
public class HttpResponse extends HttpBase{
    private static final long serialVersionUID = 2278728484481592734L;

    private HttpResponseLine httpResponseline;

    public HttpResponse(byte [] requestMessage){
        super(requestMessage);
        String httpMessage = new String(super.rawMessage);
        String requestLine = StringUtils.substringBefore(httpMessage,"\r\n");
        this.httpResponseline =  new HttpResponseLine(requestLine);
    }

    public int getLength(){
        int ret = -1;
        String response = new String(super.rawMessage);
        ret = response.length();
        return ret;
    }

    private String[] getHedders(){
        String responseAllHeader = StringUtils.substringBefore(new String(super.rawMessage),"\r\n\r\n");
        String responseHeader = StringUtils.substringAfter(responseAllHeader,"\r\n");
        String [] headders = responseHeader.split("\r\n");
        return headders;
    }

    public String getLocationHedderValue(){
        String httpResponse = new String(super.rawMessage);
        String responseHeadder = StringUtils.substringBefore(httpResponse,"\r\n\r\n");
        String [] hedders = responseHeadder.split("\r\n");
        String locationValue ="";
        for(int i=0;i<hedders.length;i++){
            if(hedders[i].startsWith("Location: ")){
                locationValue = StringUtils.substringAfter(hedders[i],"Location: ");
                break;
            }
        }
        return locationValue;
    }

    public List<String> getSetCookieList(){
        List<String> retList = new ArrayList<>();

        String [] headders = this.getHedders();
        for(int i=0;i<headders.length;i++){
            if(headders[i].startsWith("Set-Cookie: ")){
                String [] splitCookieHeader = headders[i].split(": ");
                String [] splitCookieValue =  splitCookieHeader[1].split("; ");
                retList.add(splitCookieValue[1]);
            }
        }
        return retList;
    }

    //setter getter
    public String getHttpResponseString(){
        return new String(super.rawMessage);
    }
    public String getHttpResponseVersion(){
        return httpResponseline.getVersion();
    }
    public String getHttpResponseStatus(){
        return httpResponseline.getStatus();
    }
    public String getHttpResponseMessage(){
        return httpResponseline.getMessage();
    }
}
