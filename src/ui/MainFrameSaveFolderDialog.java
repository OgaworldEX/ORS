package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

public class MainFrameSaveFolderDialog extends JDialog {
    private static final long serialVersionUID = 4746250585906770141L;

    private final JPanel contentPanel = new JPanel();
    private JTextField targetFolderTextField;
    private Boolean isInputGO;

    public MainFrameSaveFolderDialog() {
        setBounds(100, 100, 411, 119);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        {
            targetFolderTextField = new JTextField();
            targetFolderTextField.setColumns(10);
        }

        JLabel lblInputOutputFoldername = new JLabel("Input Output FolderName");
        GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
        gl_contentPanel.setHorizontalGroup(
            gl_contentPanel.createParallelGroup(Alignment.LEADING)
                .addComponent(targetFolderTextField, GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
                .addGroup(gl_contentPanel.createSequentialGroup()
                    .addComponent(lblInputOutputFoldername)
                    .addContainerGap())
        );
        gl_contentPanel.setVerticalGroup(
            gl_contentPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPanel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblInputOutputFoldername)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(targetFolderTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(81, Short.MAX_VALUE))
        );

        contentPanel.setLayout(gl_contentPanel);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("Go");
                okButton.addActionListener( e -> {
                    isInputGO = true;
                    this.setVisible(false);
                });
                okButton.setActionCommand("Go");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.setActionCommand("Cancel");
                cancelButton.addActionListener( e -> {
                    isInputGO = false;
                    this.setVisible(false);
                });
                buttonPane.add(cancelButton);
            }
        }
    }

    public Boolean isInputGO(){
        return this.isInputGO;
    }

    public String getInputText(){
        return this.targetFolderTextField.getText();
    }
}
