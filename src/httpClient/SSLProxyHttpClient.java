package httpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import settings.Settings;

public class SSLProxyHttpClient extends SSLHttpClient {

    private final static  Logger logger = LogManager.getLogger(SSLProxyHttpClient.class);

    public SSLProxyHttpClient(Settings settings) {
        super(settings);
        //connect method socket
        socketFactory = SocketFactory.getDefault();
    }

    /**
     * Https for proxy
     */
    @Override
    public void preparaSocket() {

        String targetProxyHost = settings.getSendConfig().getTargetProxyInfo().getHost();
        int targetProxyPort = settings.getSendConfig().getTargetProxyInfo().getPort();

        try {
            socket = socketFactory.createSocket(targetProxyHost,targetProxyPort);
        } catch (IOException e) {
            logger.debug(e);
        }

        //send connect method
        String targetHostname = settings.getSendConfig().getTargetInfo().getHost();
        int targetPort = settings.getSendConfig().getTargetInfo().getPort();
        SSLSocket sslsocket = sendConnectMethodForProxy(socket,targetHostname,targetPort);

        try {
            sslsocket.startHandshake();
        } catch (IOException e) {
            logger.debug(e);
        }
        socket = sslsocket;
    }

   private SSLSocket sendConnectMethodForProxy(Socket socket,String targetHost,int targetPort){
       StringBuilder sb = new StringBuilder();
       sb.append("CONNECT ");
       sb.append(targetHost);
       sb.append(":");
       sb.append(targetPort);
       sb.append(" HTTP/1.1");
       sb.append("\r\n");
       sb.append("Host: ");
       sb.append(targetHost);
       sb.append("\r\n\r\n");

       Writer out = null;
       SSLSocket retSocket = null;
       try {
           out = new OutputStreamWriter(socket.getOutputStream());

           String request = new String(sb);
           logger.trace(request);

           out.write(request);
           out.flush();

           InputStream in = socket.getInputStream();
           ByteArrayOutputStream bout = new ByteArrayOutputStream();
           byte [] buffer = new byte[1024];
           while (true) {
               int len = in.read(buffer);
               bout.write(buffer, 0, len);
               String response = new String(bout.toByteArray());

               if(StringUtils.containsIgnoreCase(response, "Connection established")){
                   break;
               }
           }
           retSocket = (SSLSocket) getDummySSLContext().getSocketFactory().createSocket(socket, targetHost, targetPort, false);
       } catch (IOException e) {
           logger.debug(e);
       }

       return retSocket;
   }
}
