package execPlan;

import settings.Settings;

public class ExecPlanManager {

    private static ExecPlanManager instance = new ExecPlanManager();
    private ExecPlanManager(){}
    public static ExecPlanManager getInstance(){
        return instance;
    }

    public void startSend(Settings setting){
        ExecPlanWorker st = new ExecPlanWorker(setting);
        Thread thread = new Thread(st);
        thread.start();
    }
}
