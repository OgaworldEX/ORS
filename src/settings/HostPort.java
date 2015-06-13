package settings;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HostPort implements Cloneable,Serializable{
    private static final long serialVersionUID = 5709807045675669592L;

    private final static Logger logger = LogManager.getLogger(HostPort.class);

    private String host = "localhost";
    private int port = 8080;

    public HostPort(String host,int port){
        this.host = host;
        this.port = port;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            logger.debug(e);
            return null;
        }
    }

    //setter getter
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
