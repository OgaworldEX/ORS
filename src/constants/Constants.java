package constants;

public class Constants {

    private Constants(){}
    public static final String APPNAME = "ORS";
    public static final String VERSION = " v1.0";
    public static final String DELIMITER = "♪";
    public static final String CRLF = "\r\n";

    public static final String defaultLogPath = "./orslog/";

    //Config value
    public enum  BeforeSendBaseRequestEnum{once,every}
    public enum  AfterSendBaseRequestEnum{once,every}

}
