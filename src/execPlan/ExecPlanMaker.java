package execPlan;

import static constants.Constants.*;
import http.HttpRequest;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import settings.Settings;

public class ExecPlanMaker {

    private final static  Logger logger = LogManager.getLogger(ExecPlanMaker.class);

    private Settings settings;
    private int requestCounter = 0;

    private static ExecPlanMaker instance = new ExecPlanMaker();
    private ExecPlanMaker(){}
    public static ExecPlanMaker getInstance(){
        return instance;
    }

    public ExecPlanMaker(Settings setting){
        this.settings = setting;
    }

    public List<ExecPlanGroup> createSendPlanGroup(){
        String attackrequest = this.settings.getPositions().getAttackRequest();
        List<String> attackPattern = this.settings.getPayAndGrep().getPayloadsPattern();
        List<ExecPlanGroup> retlist = new ArrayList<>();

        int count =  StringUtils.countMatches(attackrequest, DELIMITER);
        if(count % 2 != 0){
            logger.debug("error");
        }

        String requestString = this.settings.getPositions().getBaseRequest();
        HttpRequest baseRequest = new HttpRequest(requestString.getBytes());

        //beforeSnd
        ExecPlanGroup baseSendPlanGroup = new ExecPlanGroup("-b-",baseRequest,this.settings,"");
        if(this.settings.getSendConfig().getBeforeSendaBaseRequestEnum() == BeforeSendBaseRequestEnum.once){
            retlist.add(baseSendPlanGroup);
        }

        int headIndex = -1;
        int tailIndex = -1;

        String nextAttackString = new String(attackrequest);
        String targetAttackString = new String(attackrequest);

        int targetCount = (count /2);
        for(int i=0;i<targetCount;i++){
            targetAttackString = new String(nextAttackString);

            headIndex = targetAttackString.indexOf(DELIMITER);
            tailIndex = targetAttackString.indexOf(DELIMITER,headIndex+1);

            for(String pattern:attackPattern){
                StringBuilder attackStr = new StringBuilder(targetAttackString);

                String tmp = null;
                if(settings.getPositions().isReplace()){
                    tmp = attackStr.replace(headIndex,tailIndex+1, pattern).toString();
                }else{
                    tmp = attackStr.insert(tailIndex+1, pattern).toString();
                }
                attackStr = new StringBuilder(tmp.replaceAll(DELIMITER,""));
                HttpRequest request = new HttpRequest(attackStr.toString().getBytes());
                request.updateContentLength();

                if(this.settings.getSendConfig().getBeforeSendaBaseRequestEnum() == BeforeSendBaseRequestEnum.every){
                    retlist.add(baseSendPlanGroup);
                }
                requestCounter++;
                retlist.add(new ExecPlanGroup(Integer.toString(requestCounter),request,this.settings,pattern));

                if(this.settings.getSendConfig().getAfterSendaBaseRequestEnum() == AfterSendBaseRequestEnum.every){
                    retlist.add(baseSendPlanGroup);
                }

            }

            int startIndex = targetAttackString.indexOf(DELIMITER);
            if(startIndex > -1){
                StringBuilder tmpBuilder = new StringBuilder(targetAttackString);
                tmpBuilder.delete(startIndex, startIndex+1);
                int endIndex = tmpBuilder.indexOf(DELIMITER);
                tmpBuilder.delete(endIndex, endIndex+1);
                nextAttackString = new String(tmpBuilder);
            }
        }

        //after send
        if(this.settings.getSendConfig().getAfterSendaBaseRequestEnum() == AfterSendBaseRequestEnum.once){
            retlist.add(baseSendPlanGroup);
        }

        return retlist;
    }
}
