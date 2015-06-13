package http;

import org.apache.commons.lang3.StringUtils;

import settings.HostPort;

public class HttpHelpers {

    public static String changeRequestLineForProxy(String requestString,HostPort hostport,boolean isHttps){

        HttpRequest tmpRequest = new HttpRequest(requestString.getBytes());

        StringBuilder newRequest = new StringBuilder();
        newRequest.append(tmpRequest.getHttpRequestMethod());
        newRequest.append(" ");

        String requestPath = tmpRequest.getHttpRequestPath();
        if(!(StringUtils.startsWithIgnoreCase(requestPath, "http://") ||
           StringUtils.startsWithIgnoreCase(requestPath, "https://"))){

            newRequest.append(isHttps?"https://":"http://");
            newRequest.append(hostport.getHost());

            if(!(hostport.getPort() == 80 || hostport.getPort() == 443)){
                newRequest.append(":");
                newRequest.append(Integer.toString(hostport.getPort()));
            }
        }

        newRequest.append(tmpRequest.getHttpRequestPath());
        newRequest.append(" ");
        newRequest.append(tmpRequest.getHttpRequestVersion());
        newRequest.append("\r\n");

        String afterString = StringUtils.substringAfter(tmpRequest.toString(),"\r\n");
        newRequest.append(afterString);

        return newRequest.toString();
    }

    public static String changeRequestLineForNotProxy(String requestString,HostPort hostport){
        HttpRequest tmpRequest = new HttpRequest(requestString.getBytes());

        StringBuilder newRequest = new StringBuilder();

        newRequest.append(tmpRequest.getHttpRequestMethod());
        newRequest.append(" ");

        String requestPath = tmpRequest.getHttpRequestPath();
        if(StringUtils.startsWithIgnoreCase(requestPath, "http://") ||
           StringUtils.startsWithIgnoreCase(requestPath, "https://")){

            String newPath =  removeForwardSearchByCount(requestPath,"/",3);
            newRequest.append("/");
            newRequest.append(newPath);
            newRequest.append(" ");
            newRequest.append(tmpRequest.getHttpRequestVersion());
            newRequest.append("\r\n");

            String afterString = StringUtils.substringAfter(tmpRequest.toString(),"\r\n");
            newRequest.append(afterString);

        }else{
            return tmpRequest.toString();
        }
        return newRequest.toString();
    }

    private static String removeForwardSearchByCount(String targetString,String serchChar,int count){
        int index = targetString.indexOf(serchChar);
        String newString = targetString.substring(index + 1);
        count--;
        if(count == 0){return newString;}
        return removeForwardSearchByCount(newString,serchChar,count);
    }

}
