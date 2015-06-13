package settings;

import static constants.Constants.*;

import java.io.Serializable;

public class SendConfig implements Cloneable,Serializable{
    private static final long serialVersionUID = -8111235648681285659L;

    private HostPort targetInfo = new HostPort("localhost",80);
    private boolean isUseHTTPS = false;

    private HostPort targetProxyInfo = new HostPort("localhost",8080);
    private boolean isUseProxy = false;

    private int waitTime = 500;

    private boolean isSendBeforeSending;
    private BeforeSendBaseRequestEnum BeforeSendaBaseRequestEnum;
    private AfterSendBaseRequestEnum AfterSendaBaseRequestEnum;
    private boolean isSendAfterBeforeSending;

    private String resultOutFolder = defaultLogPath;

    //clone
    public Object clone() {
        try {
            this.targetInfo = (HostPort)this.targetInfo.clone();
            this.targetProxyInfo = (HostPort)this.targetProxyInfo.clone();
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    //setter getter
    public void setUseProxy(){
        this.isUseProxy = true;
    }

    public void setNotUseProxy(){
        this.isUseProxy = false;
    }

    public void setUseHTTPS(){
        this.isUseHTTPS = true;
    }

    public void setNotUseHTTPS(){
        this.isUseHTTPS = false;
    }

    //
    public boolean isUseProxy() {
        return isUseProxy;
    }

    public boolean isUseHTTPS() {
        return isUseHTTPS;
    }

    public void setUseHTTPS(boolean isUseHTTPS) {
        this.isUseHTTPS = isUseHTTPS;
    }

    public HostPort getTargetInfo() {
        return targetInfo;
    }

    public void setTargetInfo(HostPort targetInfo) {
        this.targetInfo = targetInfo;
    }

    public HostPort getTargetProxyInfo() {
        return targetProxyInfo;
    }

    public void setTargetProxyInfo(HostPort targetProxyInfo) {
        this.targetProxyInfo = targetProxyInfo;
    }

    public void setUseProxy(boolean isUseProxy) {
        this.isUseProxy = isUseProxy;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public boolean isSendBeforeSending() {
        return isSendBeforeSending;
    }

    public void setSendBeforeSending(boolean isSendBeforeSending) {
        this.isSendBeforeSending = isSendBeforeSending;
    }

    public BeforeSendBaseRequestEnum getBeforeSendaBaseRequestEnum() {
        return BeforeSendaBaseRequestEnum;
    }

    public void setBeforeSendaBaseRequestEnum(
            BeforeSendBaseRequestEnum beforeSendaBaseRequestEnum) {
        BeforeSendaBaseRequestEnum = beforeSendaBaseRequestEnum;
    }

    public AfterSendBaseRequestEnum getAfterSendaBaseRequestEnum() {
        return AfterSendaBaseRequestEnum;
    }

    public void setAfterSendaBaseRequestEnum(
            AfterSendBaseRequestEnum afterSendaBaseRequestEnum) {
        AfterSendaBaseRequestEnum = afterSendaBaseRequestEnum;
    }

    public boolean isSendAfterBeforeSending() {
        return isSendAfterBeforeSending;
    }

    public void setSendAfterBeforeSending(boolean isSendAfterBeforeSending) {
        this.isSendAfterBeforeSending = isSendAfterBeforeSending;
    }

    public String getResultOutFolder() {
        return resultOutFolder;
    }

    public void setResultOutFolder(String resultOutFolder) {
        StringBuilder tmpSb = new StringBuilder(defaultLogPath);
        tmpSb.append(resultOutFolder);
        if(!"".equals(resultOutFolder)){
            tmpSb.append("/");
        }
        this.resultOutFolder = tmpSb.toString();
    }
}
