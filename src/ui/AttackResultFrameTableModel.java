package ui;

import javax.swing.table.DefaultTableModel;

public class AttackResultFrameTableModel extends DefaultTableModel {
    private static final long serialVersionUID = 4218011460091642564L;

    public AttackResultFrameTableModel(Object[] columnNames, int rowCount){
        super(columnNames,rowCount);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if(column == 4){
            return true;
        }else{
            return false;
        }
    }
}
