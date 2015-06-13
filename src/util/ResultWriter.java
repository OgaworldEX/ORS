package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import execPlan.ExecPlan;
import settings.Settings;
import ui.AttackResultFrameSaveData;

public class ResultWriter {

    private final static  Logger logger = LogManager.getLogger(ResultWriter.class);

    public void saveAttackResult(String fileName,Settings settings,LinkedHashMap<String,ExecPlan> result){
        AttackResultFrameSaveData rsSaveObject = new AttackResultFrameSaveData(settings,result);
        FileOutputStream outFile = null;
        try {

            //directory check
            File targetfolder = new File(settings.getSendConfig().getResultOutFolder());
            if(!targetfolder.exists()){
                targetfolder.mkdir();
            }
            StringBuilder saveFileName = new StringBuilder();
            saveFileName.append(fileName);
            outFile = new FileOutputStream(saveFileName.toString());
            ObjectOutputStream outObject = new ObjectOutputStream(outFile);
            outObject.writeObject(rsSaveObject);
            outObject.close();
            outFile.close();
        } catch (IOException e) {
            logger.debug(e);
        }
    }

}
