package execPlan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import settings.Settings;
import http.HttpMessage;
import http.HttpRequest;

public class ExecPlan implements Serializable{
    private static final long serialVersionUID = 2510750990401871255L;

    private HttpMessage httpMessage;
    private Settings settings;
    private String requestNumber;
    private String attackPattern;

    public ExecPlan(String requestNumber,HttpRequest request,Settings setting) {
        this.requestNumber = requestNumber;
        this.httpMessage = new HttpMessage(request);
        this.settings = setting;
    }

    public ExecPlan(String requestNumber,HttpMessage message,Settings setting) {
        this.requestNumber = requestNumber;
        this.httpMessage = message;
        this.settings = setting;
    }

    public List<String> getResult(){
        ArrayList<String> ret = new ArrayList<>();
        List<String> grepPattern = settings.getPayAndGrep().getGrepPattern();

        grepPattern.stream().forEach(pattern ->{
            String response = "";
            if(httpMessage.getHttpResponse() != null){
                response = httpMessage.getHttpResponse().getHttpResponseString();
            }else{
                response = "";
            }

            int retcount = 0;
            if(settings.getPayAndGrep().isRegex() == true){
                Pattern p = Pattern.compile(pattern);
                Matcher m = p.matcher(response);
                while(m.find()){
                    retcount++;
                 }
            }else{
                retcount = StringUtils.countMatches(response,pattern);
            }
            if(retcount == 0){
                ret.add("-");
            }else{
                ret.add(Integer.toString(retcount));
            }
        });
        return ret;
    }

    //setter getter
    public List<String> getGrepPattern(){
        return settings.getPayAndGrep().getGrepPattern();
    }
    public int getRequestNumber() {
        return Integer.valueOf(requestNumber).intValue();
    }
    public void setRequestNumber(String requestNumber) {
        this.requestNumber = requestNumber;
    }
    public String getRequestNumberString() {
        return requestNumber;
    }
    public HttpMessage getHttpMessage() {
        return this.httpMessage;
    }
    public String getAttackPattern() {
        return attackPattern;
    }
    public void setAttackPattern(String attackPattern) {
        this.attackPattern = attackPattern;
    }
}

