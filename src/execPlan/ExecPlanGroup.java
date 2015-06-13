package execPlan;

import http.HttpMessage;
import http.HttpRequest;

import java.util.ArrayList;
import java.util.List;

import settings.Settings;

public class ExecPlanGroup {
    private String execPlanNumber;
    private List<ExecPlan> execPlanList = new ArrayList<>();

    public ExecPlanGroup(String number,HttpRequest request,Settings setting,String attackPattern){
        execPlanNumber = new String(number);
        ExecPlan sp = new ExecPlan(number,request,setting);
        sp.setAttackPattern(attackPattern);
        this.execPlanList.add(sp);
    }

    public ExecPlan getFirstSendPlan(){
        return execPlanList.get(0);
    }

    public void addExecPlanSendedResult(HttpMessage message,Settings setting){
        ExecPlan ep = new ExecPlan(execPlanNumber + "-" + execPlanList.size(),message,setting);
        this.execPlanList.add(ep);
    }

    public void addHttpRequest(HttpRequest request,Settings setting){
        ExecPlan ep = new ExecPlan(execPlanNumber + "-" + execPlanList.size(),request,setting);
        this.execPlanList.add(ep);
    }

    public List<ExecPlan> getExecPlanList(){
        return this.execPlanList;
    }

    public List<ExecPlan> getExecPlanSecondSubsequent(){
        List<ExecPlan> retValue = new ArrayList<>(this.execPlanList);
        retValue.remove(0);
        return retValue;
    }
}