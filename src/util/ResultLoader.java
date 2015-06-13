package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import ui.AttackResultFrameSaveData;

public class ResultLoader {

    private final static  Logger logger = LogManager.getLogger(ResultLoader.class);

    public AttackResultFrameSaveData loadResult(File file){
        AttackResultFrameSaveData loadobject = null;
        try{
            FileInputStream inFile = new FileInputStream(file);
            ObjectInputStream inObject = new ObjectInputStream(inFile);

            loadobject = (AttackResultFrameSaveData)inObject.readObject();

            inFile.close();
            inObject.close();
            loadobject.setLoadOK(true);

        } catch (IOException | ClassNotFoundException e) {
            logger.debug(e);
            loadobject.setLoadOK(false);
        }

        return loadobject;
    }
}
