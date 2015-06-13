package httpClient;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import settings.Settings;

public class SSLHttpClient extends HttpClient{

    private final static  Logger logger = LogManager.getLogger(SSLHttpClient.class);

    public SSLHttpClient(Settings settings) {
        super(settings);
        socketFactory = getDummySSLContext().getSocketFactory();
    }

    /**
     * Self-signed Dummy SSL
     * @return
     */
    protected static SSLContext getDummySSLContext() {
        SSLContext sslcontext = null;
        try {
            sslcontext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            logger.debug(e);
        }

        X509TrustManager[] trustManager = {
            new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) {}
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) {}
                public X509Certificate [] getAcceptedIssuers() {return null;}
            }
        };

        try {
            sslcontext.init(null, trustManager, null);
        } catch (KeyManagementException e) {
            logger.debug(e);
        }
        return sslcontext;
    }


}
