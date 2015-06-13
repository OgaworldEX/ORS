package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SettingsReader {

    private final static  Logger logger = LogManager.getLogger(ResultLoader.class);

    private static final String attackPatternFilePath ="default_attack_pattern.txt";
    private static final String grepFilePath ="default_grep_pattern.txt";
    private static final String regexgrepFilePath ="default_regexgrep_pattern.txt";

    public static List<String> readAttackPatern(){
        return readOneLine(attackPatternFilePath);
    }

    public static List<String> readGrepPatern(){
        return readOneLine(grepFilePath);
    }

    public static List<String> readRegexGrepPatern(){
        return readOneLine(regexgrepFilePath);
    }

    private static List<String>readOneLine(String filePath){
        List<String> retArray = new ArrayList<String>();

        try {
            InputStream i = SettingsReader.class.getResourceAsStream(filePath);
            InputStreamReader isr = new InputStreamReader(i);
            BufferedReader in = new BufferedReader(isr);
            String line = null;
            while ((line = in.readLine()) != null) {
                retArray.add(line);
            }
            in.close();
        } catch (IOException e) {
            logger.debug(e);
        }
        return retArray;
    }
}
