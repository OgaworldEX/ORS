package httpClient;

import java.io.IOException;
import settings.Settings;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProxyHttpClient extends HttpClient {

    private final static Logger logger = LogManager.getLogger(ProxyHttpClient.class);

    public ProxyHttpClient(Settings settings) {
        super(settings);
    }

    @Override
    public void preparaSocket() {
        try {
            String targetProxyHost = settings.getSendConfig().getTargetProxyInfo().getHost();
             int targetProxyPort = settings.getSendConfig().getTargetProxyInfo().getPort();
            socket = socketFactory.createSocket(targetProxyHost,targetProxyPort);
        } catch (IOException e) {
           logger.debug(e);
        }
    }

}
