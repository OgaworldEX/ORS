package execPlan;

import httpClient.HttpClientFactory;
import httpClient.AbstractHttpClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import settings.Settings;
import ui.AttackResultFrame;

public class ExecPlanWorker implements Runnable{

    private final static  Logger logger = LogManager.getLogger(ExecPlanWorker.class);
    private final static SimpleDateFormat logdataformat = new SimpleDateFormat("yyyyMMddmmssSSS");

    private Settings settings;
    private Boolean isAttackStop = false;

    public ExecPlanWorker(Settings setting){
        this.settings = setting;
    }

    public void run() {
        AttackResultFrame resultFrame = new AttackResultFrame();
        resultFrame.setVisible(true);
        resultFrame.setParentThread(this);
        resultFrame.initTabelData(settings);
        resultFrame.invalidate();

        ExecPlanMaker spm = new ExecPlanMaker(settings);
        List<ExecPlanGroup> splist = spm.createSendPlanGroup();

        resultFrame.setCountAllSendPlan(splist.size());
        StringBuilder execNameSb = new StringBuilder();
        execNameSb.append(settings.getSendConfig().getResultOutFolder());
        execNameSb.append(logdataformat.format(new Date()));
        execNameSb.append(settings.getPositions().isReplace()?"_replace.ors":"_add.ors");
        resultFrame.setExexName(execNameSb.toString());

        logger.fatal(execNameSb.toString()); //(´Д` ;)

        HttpClientFactory httpclientfactory = new HttpClientFactory();
        for(ExecPlanGroup spg : splist){
            //end flag
            if(isAttackStop == true){
                logger.debug("push stop!");
                break;
            }

            //wait time
            try {
                Thread.sleep(settings.getSendConfig().getWaitTime());
            } catch (InterruptedException e) {
                logger.debug(e);
            }

            AbstractHttpClient abstHttpClient =	httpclientfactory.create(settings);
            try {
                abstHttpClient.sendMessage(spg);
            } catch (Exception e) {
                logger.debug(e);
            }
            resultFrame.addAttackRequestAndResultToTableData(spg);

        }
        logger.debug("push stop!");
    }

    public void setStop(){
        isAttackStop = true;
    }
}
