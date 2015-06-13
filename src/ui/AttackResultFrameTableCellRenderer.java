package ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class AttackResultFrameTableCellRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 3224621307675179222L;

    private static final Color HEDDERCOLOR = new Color(204, 255, 250);
    private static final Color HITCOLOR = new Color(255, 110, 181);
    private static final Color SELECTEDCOLOR = new Color(0, 0, 129);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if(isSelected) {
            this.setBackground(SELECTEDCOLOR);
        }else if((-1 < column ) && (column < 5)) {
            this.setBackground(HEDDERCOLOR);
        }else if((4 < column) && (!value.equals("-"))){
            this.setBackground(HITCOLOR);
        }else{
            this.setBackground(table.getBackground());
        }

        return this;
    }

}
