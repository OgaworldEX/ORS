package httpClient;

import http.HttpMessage;
import http.HttpRequest;
import http.HttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.List;

import javax.net.SocketFactory;

import org.apache.commons.lang3.StringUtils;

import execPlan.ExecPlan;
import execPlan.ExecPlanGroup;
import settings.HostPort;
import settings.Settings;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class HttpClient extends AbstractHttpClient{

    private final static  Logger logger = LogManager.getLogger(HttpClient.class);

    protected Settings settings;
    protected Socket socket;
    protected SocketFactory socketFactory;
    protected HttpMessage message;

    protected int redirectMaxCount = 20;

    public HttpClient(Settings settings){
        this.settings = settings;
        socketFactory = SocketFactory.getDefault();
    }

    @Override
    public void sendMessage(ExecPlanGroup spg) throws IOException{
        ExecPlan firstPlan = spg.getFirstSendPlan();
        message = firstPlan.getHttpMessage();

        preparaSocket();
        sendRequest();

        //redirect
        HttpMessage targetHttpMessage = this.message;
        for(int i=0;i<redirectMaxCount;i++){
            if(targetHttpMessage.getHttpResponse() == null){
                break;
            }
            String status = targetHttpMessage.getHttpResponse().getHttpResponseStatus();
            if(status.startsWith("30")){
                Settings redirectSettings = (Settings) settings.clone();
                HttpMessage newMessage = createRedirectRequest(targetHttpMessage,redirectSettings);

                HttpClientFactory httpclientfactory = new HttpClientFactory();
                AbstractHttpClient abstHttpClient =	httpclientfactory.create(redirectSettings);

                abstHttpClient.sendRedirectMessage(newMessage);
                spg.addExecPlanSendedResult(newMessage, redirectSettings);
                targetHttpMessage = newMessage;
            }else{
                break;
            }
        }
    }

    @Override
    public void sendRedirectMessage(HttpMessage message) throws IOException{
        //Redirect
        this.message = message;
        preparaSocket();
        sendRequest();
    }

    @Override
    protected void preparaSocket() {
        try {
            String targetHost = settings.getSendConfig().getTargetInfo().getHost();
            int targetPort = settings.getSendConfig().getTargetInfo().getPort();
            socket = socketFactory.createSocket(targetHost,targetPort);
        } catch (IOException e) {
            logger.debug(e);
        }
    }

    protected void sendRequest() throws IOException{
        try {
            //String only
            String requestString = message.getHttpRequest().getHttpRequestString();
            Writer out = new OutputStreamWriter(socket.getOutputStream());
            out.write(requestString);
            out.flush();

            logger.trace(requestString);

            InputStream in = socket.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();

            byte [] buffer = new byte[1024];
            while (true) {
                int len = in.read(buffer);
                if(len<0){
                    break;
                }
                bout.write(buffer, 0, len);
            }
            byte [] response = bout.toByteArray();

            logger.trace(new String(response));

            HttpResponse resp = new HttpResponse(response);
            message.setHttpResponse(resp);

            bout.close();
            in.close();
        } catch (IOException e){
            logger.debug(e);
            throw e;
        } finally {
            try {
                if(socket != null){
                    socket.close();
                }
            } catch (IOException e) {
                logger.debug(e);
            }
        }
    }

    private HttpMessage createRedirectRequest(HttpMessage httpMessage,Settings setting){
        String locationValue = httpMessage.getHttpResponse().getLocationHedderValue();
        URL url = null;
        try {
            //relative path redirect
            if(!(locationValue.startsWith("http://") || locationValue.startsWith("https://"))){
                StringBuilder sb = new StringBuilder();
                sb.append(setting.getSendConfig().isUseHTTPS()?"https://":"http://");
                sb.append(setting.getSendConfig().getTargetInfo().getHost());
                if(!locationValue.startsWith("/")){
                    sb.append("/");
                }
                int targetPort = setting.getSendConfig().getTargetInfo().getPort();
                if(!(targetPort == 80 ||targetPort == 443)){
                    sb.append(":");
                    sb.append(Integer.toString(targetPort));
                }
                locationValue = sb.append(locationValue).toString();
            }
            url = new URL(locationValue);
        } catch (MalformedURLException e) {
            logger.debug(e);
        }

        //make redirectRequest
        String httpversion = httpMessage.getHttpRequest().getHttpRequestVersion();

        int tmpPort = -1;
        if(url.getProtocol().equals("https")){
            setting.getSendConfig().setUseHTTPS();
            tmpPort = 443;
        }else{
            setting.getSendConfig().setNotUseHTTPS();
            tmpPort = 80;
        }

        HostPort hostPort = null;
        if(0 < url.getPort()){
            hostPort = new HostPort(url.getHost(),url.getPort());
        }else{
            hostPort = new HostPort(url.getHost(),tmpPort);
        }

        setting.getSendConfig().setTargetInfo(hostPort);

        StringBuilder targetPath = new StringBuilder();
        targetPath.append(url.getPath());
        if (StringUtils.isNotEmpty(url.getQuery())){
            targetPath.append("?");
            targetPath.append(url.getQuery());
        }

        //make new requestline
        StringBuilder newRequestString = new StringBuilder();
        StringBuilder requestLine = new StringBuilder();
        requestLine.append("GET ");

        //proxy connection
        if(setting.getSendConfig().isUseProxy()){
            if(setting.getSendConfig().isUseHTTPS()){
                requestLine.append("https://");
            }else{
                requestLine.append("http://");
            }

            requestLine.append(setting.getSendConfig().getTargetInfo().getHost());
            int targetPort = setting.getSendConfig().getTargetInfo().getPort();
            if(!(targetPort == 80 || targetPort == 443)){
                requestLine.append(":");
                requestLine.append(Integer.toString(targetPort));
            }
        }

        requestLine.append(targetPath);
        requestLine.append(" ");
        requestLine.append(httpversion);
        requestLine.append("\r\n");
        newRequestString.append(requestLine.toString());

        //cookie re-set
        List<String> cookieList =  httpMessage.getHttpResponse().getSetCookieList();

        StringBuilder addCookieSb = new StringBuilder();
        for(String cookie:cookieList){
            addCookieSb.append("; ");
            addCookieSb.append(cookie);
        }

        String [] headders = httpMessage.getHttpRequest().getRedirectHeadders();
        for(int i=0;i<headders.length;i++){
            if(headders[i].startsWith("Cookie: ")){
                headders[i] = headders[i] + addCookieSb.toString();
            }
        }

        for(int i=0;i<headders.length;i++){
            newRequestString.append(headders[i]);
            newRequestString.append("\r\n");
        }
        newRequestString.append("\r\n");

        HttpRequest newRequest = new HttpRequest(newRequestString.toString().getBytes());
        HttpMessage retMessage = new HttpMessage(newRequest);
        return retMessage;
    }

    //setter getter
    public HttpMessage getMessage() {
        return message;
    }
    public void setMessage(HttpMessage message) {
        this.message = message;
    }
}