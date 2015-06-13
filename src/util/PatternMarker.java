package util;

import static constants.Constants.DELIMITER;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import http.HttpRequest;
import http.HttpRequestLine;

public class PatternMarker {

    public static String addAutoDelimiter(String targetString){
        //Add:RequestType..　json multipart
        return addDelimiterAtURLEncodeParameter(targetString);
    }

    private static String addDelimiterAtURLEncodeParameter(String targetString){
        StringBuilder retSb = new StringBuilder();
        HttpRequest request = new HttpRequest(targetString.getBytes());
        //Request Line
        HttpRequestLine targetRequestLine = request.getRequestLine();
        retSb.append(targetRequestLine.getMethod());
        retSb.append(" ");
        retSb.append(addDelimiterAtStandardHttpParameter(targetRequestLine.getPath()));
        retSb.append(" ");
        retSb.append(targetRequestLine.getVersion());
        retSb.append("\r\n");

        //Headders
        String [] headers = request.getHeadders();
        List<String> heddersList = Arrays.asList(headers);
        heddersList.stream().forEach(headder -> {
            retSb.append(headder);
            retSb.append("\r\n");
        });

        //HeaderEnd
        retSb.append("\r\n");

        //PostBody
        if(!(request.getBodyParameter() == null)){
            retSb.append(addDelimiterAtStandardHttpParameter(request.getBodyParameter()));
        }

        return retSb.toString();
    }

    /**
     * arg ex) key1=value1&key2=value2...
     * @param httpParameterLine
     * @return
     */
    private static String addDelimiterAtStandardHttpParameter(String httpParameterLine){

        if(!StringUtils.contains(httpParameterLine, "=")){
            return httpParameterLine;
        }

        StringBuilder retSb = new StringBuilder();
        List<String> spliteAmpList = Arrays.asList(httpParameterLine.split("&"));
        spliteAmpList.stream().forEach(keyValue ->{
            String[] spliteEqualArray = keyValue.split("=");
            if(1 < spliteEqualArray.length){
                retSb.append(spliteEqualArray[0]);
                retSb.append("=");
                retSb.append(DELIMITER);
                retSb.append(spliteEqualArray[1]);
                retSb.append(DELIMITER);
                retSb.append("&");
            }else{
                retSb.append(keyValue);
            }
        });

        return StringUtils.removeEnd(retSb.toString(),"&");
    }

    public static String removeDelimiter(String targetString,int startPos,int endPos){

        String beforeString = StringUtils.substring(targetString, 0, startPos);
        String selectedString = StringUtils.substring(targetString, startPos, endPos);
        String afterString = StringUtils.substring(targetString, endPos, targetString.length());

        String removeString = StringUtils.remove(selectedString, DELIMITER);

        StringBuilder sb = new StringBuilder();
        sb.append(beforeString);
        sb.append(removeString);
        sb.append(afterString);

        return sb.toString();
    }

    public static String removeDelimiter(String targetString){
        return StringUtils.remove(targetString, DELIMITER);
    }
}
