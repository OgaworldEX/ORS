package httpClient;

import settings.Settings;

public class HttpClientFactory {

    public AbstractHttpClient create(Settings setting){

        AbstractHttpClient retClient = null;

        if(setting.getSendConfig().isUseHTTPS() && setting.getSendConfig().isUseProxy()){
            //Https and proxy
            retClient = new SSLProxyHttpClient(setting);
        }else if((setting.getSendConfig().isUseHTTPS() && (setting.getSendConfig().isUseProxy()==false))){
            //Https and Noproxy
            retClient = new SSLHttpClient(setting);
        }else if(((setting.getSendConfig().isUseHTTPS() == false)  && setting.getSendConfig().isUseProxy())){
            //Http and proxy
            retClient = new ProxyHttpClient(setting);
        }else{
            //Http and Noproxy
            retClient = new HttpClient(setting);
        }
        return retClient;
    }
}
