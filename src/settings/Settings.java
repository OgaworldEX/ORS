package settings;

import java.io.Serializable;

public class Settings implements Cloneable,Serializable{
    private static final long serialVersionUID = 2405706451183814138L;

    private SendConfig sendConfig;
    private Positions positions;
    private PatternAndGrepPattern payAndGrep;

    public Settings(SendConfig sendConfig,
                    Positions positions,
                    PatternAndGrepPattern payAndGrep){

        this.sendConfig = sendConfig;
        this.positions = positions;
        this.payAndGrep = payAndGrep;
    }

    //clone
    public Object clone() {
        try {
            Settings newSetting = (Settings)super.clone();
            newSetting.sendConfig = (SendConfig) this.sendConfig.clone();
            newSetting.positions = (Positions)this.positions.clone();
            newSetting.payAndGrep = (PatternAndGrepPattern)this.payAndGrep.clone();
            return newSetting;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    //setter getter
    public SendConfig getSendConfig() {
        return sendConfig;
    }
    public void setSendConfig(SendConfig sendconfig) {
        this.sendConfig = sendconfig;
    }
    public Positions getPositions() {
        return positions;
    }
    public void setPositions(Positions positions) {
        this.positions = positions;
    }
    public PatternAndGrepPattern getPayAndGrep() {
        return payAndGrep;
    }
    public void setPayAndGrep(PatternAndGrepPattern payAndGrep) {
        this.payAndGrep = payAndGrep;
    }
}
