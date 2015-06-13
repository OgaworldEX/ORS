package http;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpRequest extends HttpBase{
    private static final long serialVersionUID = 2950552847217478218L;

    private final static  Logger logger = LogManager.getLogger(HttpRequest.class);

    private HttpRequestLine httprequestline;

    public HttpRequest(byte [] requestMessage){
        super(requestMessage);
        String httpMessage = new String(super.rawMessage);
        String requestLine = StringUtils.substringBefore(httpMessage,"\r\n");
        this.httprequestline =  new HttpRequestLine(requestLine);
    }

    public HttpRequestLine getRequestLine(){
        return this.httprequestline;
    }

    public String getRequestLineString(){
        return httprequestline.getRequestLineStringNoCRLF();
    }

    public String getBodyParameter(){
        String method = httprequestline.getMethod();

        String returnString = null;
        if("POST".equals(method)){
            returnString = StringUtils.substringAfterLast(new String(super.rawMessage), "\r\n\r\n");
        }else{
            returnString = null;
        }
        return returnString;
    }

    public void setRequestLine(String requestLine){
        this.httprequestline =  new HttpRequestLine(requestLine);
    }

    public void updateContentLength(){
        String requestMessage = new String(super.rawMessage);
        String requestBody = StringUtils.substringAfter(requestMessage, "\r\n\r\n");

        if(requestBody.length() < 1){
            return;
        }

        String charsetString = this.getCharset();
        int countentLength = -1;
        if(charsetString != null){
            try {
                countentLength = requestBody.getBytes(charsetString).length;
            } catch (UnsupportedEncodingException e) {
                countentLength = requestBody.getBytes().length;
                logger.debug(e);
            }
        }else{
            countentLength = requestBody.getBytes().length;
        }

        Pattern p = Pattern.compile("Content-Length: [0-9]*");
        Matcher m = p.matcher(requestMessage);
        String newMessage = m.replaceFirst("Content-Length: " + Integer.toString(countentLength));
        super.rawMessage = newMessage.getBytes();
    }

    public String getCharset(){
        String [] hedders = this.getHeadders();
        String targetString = "";
        for(int i=0;i<hedders.length;i++){
            targetString = hedders[i];
            if(StringUtils.containsIgnoreCase(targetString,"Content-Type: ")==true){
                break;
            }
        }

        String retString = null;
        Pattern p = Pattern.compile("charset=.*");
        Matcher m = p.matcher(targetString);
        if(m.find()){
            String matchstr = m.group();
            if(StringUtils.containsIgnoreCase(matchstr,"SHIFT_JIS")){
                retString = "Shift_JIS";
            }else if(StringUtils.containsIgnoreCase(matchstr,"UTF-8")){
                retString = "UTF-8";
            }else if(StringUtils.containsIgnoreCase(matchstr,"EUC-JP")){
                retString = "EUC-JP";
            }else{
                //
            }
        }
        return retString;
    }

    public String[] getHeadders(){
        String requestAllHeader = StringUtils.substringBefore(new String(super.rawMessage),"\r\n\r\n");
        String requestHeader = StringUtils.substringAfter(requestAllHeader,"\r\n");
        String [] headders = requestHeader.split("\r\n");
        return headders;
    }

    public String []getRedirectHeadders(){
        String [] headders = this.getHeadders();
        ArrayList<String> retArray = new ArrayList<>();
        for(int i=0;i<headders.length;i++){
            if(headders[i].startsWith("Content-Length")){
            }else{
                retArray.add(headders[i]);
            }
        }
        String [] ret = new String[retArray.size()];
        retArray.toArray(ret);
        return ret;
    }

    @Override
    public String toString() {
        return new String(super.rawMessage);
    }

    //setter.getter
    public String getHttpRequestString(){
        return new String(super.rawMessage);
    }
    public String getHttpRequestMethod(){
        return httprequestline.getMethod();
    }
    public String getHttpRequestPath(){
        return httprequestline.getPath();
    }
    public String getHttpRequestVersion(){
        return httprequestline.getVersion();
    }
}
