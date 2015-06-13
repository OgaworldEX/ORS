package ui;

import java.io.Serializable;
import java.util.LinkedHashMap;

import execPlan.ExecPlan;
import settings.Settings;

public class AttackResultFrameSaveData implements Serializable{
    private static final long serialVersionUID = 7016901351147776582L;

    private Settings settings;
    private LinkedHashMap<String,ExecPlan> sendPlanMap;
    private boolean isLoadOK = false;
    private boolean isSaveOK = false;

    public AttackResultFrameSaveData(Settings settings,LinkedHashMap<String,ExecPlan> sendPlanMap){
        this.settings = settings;
        this.sendPlanMap = sendPlanMap;
    }

    //getter setter
    public Settings getSettings() {
        return settings;
    }
    public LinkedHashMap<String, ExecPlan> getSendPlanMap() {
        return sendPlanMap;
    }
    public boolean isLoadOK() {
        return isLoadOK;
    }
    public void setLoadOK(boolean isLoadOK) {
        this.isLoadOK = isLoadOK;
    }
    public boolean isSaveOK() {
        return isSaveOK;
    }
    public void setSaveOK(boolean isSaveOK) {
        this.isSaveOK = isSaveOK;
    }
    public void setSettings(Settings settings) {
        this.settings = settings;
    }
    public void setSendPlanMap(LinkedHashMap<String, ExecPlan> sendPlanMap) {
        this.sendPlanMap = sendPlanMap;
    }
}
