package main;

import java.awt.EventQueue;

import ui.MainFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OgaRequestSender {

    private final static Logger logger = LogManager.getLogger(OgaRequestSender.class);

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainFrame mainframe = new MainFrame();
                    mainframe.setVisible(true);
                } catch (Exception e) {
                    logger.debug(e);
                }
            }
        });
    }

}
