package settings;

import static constants.Constants.*;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class Positions implements Cloneable,Serializable{
    private static final long serialVersionUID = 1600039427484690417L;

    private String baseRequest;
    private String attackRequest;
    private boolean isReplace = false;

    public boolean isReplace() {
        return isReplace;
    }

    public void setIsReplace(boolean isReplace) {
        this.isReplace = isReplace;
    }

    public Positions(String request){
        this.baseRequest = 	StringUtils.remove(request,DELIMITER);
        this.attackRequest = request;
    }

    //clone
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    //getter setter
    public String getBaseRequest() {
        return baseRequest;
    }
    public void setBaseRequest(String baseRequest) {
        this.baseRequest = baseRequest;
    }
    public String getAttackRequest() {
        return attackRequest;
    }
    public void setAttackRequest(String attackRequest) {
        this.attackRequest = attackRequest;
    }

}
