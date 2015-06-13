package ui;

import java.util.ArrayList;
import java.util.List;

import execPlan.ExecPlan;

public class AttackResultFrameTabelRawData {

    private String number;
    private String payload;
    private String status;
    private String length;
    private String comment;
    private List<String> grepResult;

    public AttackResultFrameTabelRawData(){}

    public AttackResultFrameTabelRawData(ExecPlan execPlan){
        this.number = execPlan.getRequestNumberString();
        this.payload = execPlan.getAttackPattern();
        if(execPlan.getHttpMessage().getHttpResponse() != null){
            this.status = execPlan.getHttpMessage().getHttpResponse().getHttpResponseStatus();
            this.length = Integer.toString(execPlan.getHttpMessage().getHttpResponse().getLength());
            this.comment = "";
        }else{
            this.status = "";
            this.length = "";
            this.comment = "No Response";
        }
        grepResult = execPlan.getResult();
    }

    public Object [] getArrayValue(){
        ArrayList<String> retArray = new ArrayList<String>();
        retArray.add(number);
        retArray.add(payload);
        retArray.add(status);
        retArray.add(length);
        retArray.add(comment);

        for(String i:grepResult){
            retArray.add(i);
        }

        return retArray.toArray();
    }

    //setter getter
    public List<String> getGrepPettern() {
        return grepResult;
    }
    public void setGrepPettern(List<String> grepPettern) {
        this.grepResult = grepPettern;
    }
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public String getPayload() {
        return payload;
    }
    public void setPayload(String payload) {
        this.payload = payload;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public int getLength() {
        return Integer.valueOf(length);
    }
    public void setLength(int length) {
        this.length = Integer.toString(length);
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public List<String> getGrepResult() {
        return grepResult;
    }
    public void setGrepResult(List<String> grepResult) {
        this.grepResult = grepResult;
    }
}
