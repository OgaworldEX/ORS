package settings;

import java.io.Serializable;
import java.util.List;

public class PatternAndGrepPattern implements Cloneable,Serializable{
    private static final long serialVersionUID = 1825261219920405104L;

    private List<String> payloadsPattern;
    private List<String> grepPattern;
    private boolean isRegex = false;

    public boolean isRegex() {
        return isRegex;
    }

    public void setRegex(boolean isRegex) {
        this.isRegex = isRegex;
    }

    public PatternAndGrepPattern(List<String> payloadsPattern,
                                 List<String> grepPattern){
        this.payloadsPattern = payloadsPattern;
        this.grepPattern = grepPattern;
    }

    //clone
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    //setter getter
    public List<String> getPayloadsPattern() {
        return payloadsPattern;
    }
    public void setPayloadsPattern(List<String> payloadsPattern) {
        this.payloadsPattern = payloadsPattern;
    }
    public List<String> getGrepPattern() {
        return grepPattern;
    }
    public void setGrepPattern(List<String> grepPattern) {
        this.grepPattern = grepPattern;
    }

}
