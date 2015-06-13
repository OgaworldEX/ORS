package ui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainFrameDropTargetListener implements DropTargetListener {

    private final static Logger logger = LogManager.getLogger(MainFrameDropTargetListener.class);

    @Override
    public void dragEnter(DropTargetDragEvent arg0) {}

    @Override
    public void dragExit(DropTargetEvent arg0) {}

    @Override
    public void dragOver(DropTargetDragEvent arg0) {}

    @Override
    public void dropActionChanged(DropTargetDragEvent arg0) {}

    @Override
    public void drop(DropTargetDropEvent arg0) {
        Transferable tr = arg0.getTransferable();
        arg0.acceptDrop(DnDConstants.ACTION_REFERENCE);
        if(arg0.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
            try {
                @SuppressWarnings("unchecked")
                List<File> fileList = (List<File>)tr.getTransferData(DataFlavor.javaFileListFlavor);
                for(File file: fileList){
                    AttackResultFrame resultFrame = new AttackResultFrame();
                    if (resultFrame.loadResult(file) == true){
                        resultFrame.setVisible(true);
                        resultFrame.invalidate();
                    }else{
                        resultFrame = null;
                    }
                }
            } catch (UnsupportedFlavorException | IOException e) {
                logger.debug(e);
            }
        }
    }
}
