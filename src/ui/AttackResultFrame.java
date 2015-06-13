package ui;

import httpClient.HttpClient;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTable;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JProgressBar;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLayeredPane;
import javax.swing.JTextField;
import javax.swing.JSeparator;
import javax.swing.ScrollPaneConstants;

import java.awt.Toolkit;

import javax.swing.ImageIcon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import execPlan.ExecPlan;
import execPlan.ExecPlanGroup;
import execPlan.ExecPlanWorker;
import settings.Settings;
import util.*;

public class AttackResultFrame extends JFrame {
    private static final long serialVersionUID = 2084815454100278880L;

    private final static  Logger logger = LogManager.getLogger(HttpClient.class);

    private JPanel contentPane;
    private DefaultTableModel tableModel;
    private List<String> defaultColumnNames ;
    private JTextArea textAreaRequest;
    private JTextArea textAreaResponse;
    private LinkedHashMap<String,ExecPlan> sendPlanMap = new LinkedHashMap<String,ExecPlan>();

    private JTable table;
    private JLayeredPane requestLayeredPane;
    private JLayeredPane responseLayeredPane;

    private JProgressBar progressBar;
    private int progresCount = 1;

    private JTextField reqSerchTextField;
    private JTextField resSerchTextField;

    private String baseExexName = "";
    private ExecPlanWorker parentThread;

    private Settings settings;

    private int countAllSendPlan = 0;

    private Boolean isReadOnlyMode = false;

    /**
     * Create the frame.
     */
    public AttackResultFrame() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(AttackResultFrame.class.getResource("/images/img_result_128x128.png")));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setBounds(100, 100, 706, 531);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnSave = new JMenu("File");
        mnSave.setIcon(new ImageIcon(AttackResultFrame.class.getResource("/images/img_file_30x30.png")));
        menuBar.add(mnSave);

        JMenuItem mntmLoad = new JMenuItem("Load");
        mntmLoad.setIcon(new ImageIcon(AttackResultFrame.class.getResource("/images/img_loadresultfile_24x24.png")));
        mntmLoad.addActionListener(e -> loadResult());
        mnSave.add(mntmLoad);

        JSeparator separator = new JSeparator();
        mnSave.add(separator);

        JMenuItem mntmClose = new JMenuItem("Close");
        mntmClose.setIcon(new ImageIcon(AttackResultFrame.class.getResource("/images/img_close_24x24.png")));
        mntmClose.addActionListener(e -> close());

        mnSave.add(mntmClose);

        JMenu mnNewMenu = new JMenu("Stop");
        mnNewMenu.setIcon(new ImageIcon(AttackResultFrame.class.getResource("/images/img_stop1_24x24.png")));
        menuBar.add(mnNewMenu);

        JMenuItem mntmStop = new JMenuItem("Stop!");
        mntmStop.setIcon(new ImageIcon(AttackResultFrame.class.getResource("/images/img_stop2_24x24.png")));
        mnNewMenu.add(mntmStop);
        mntmStop.addActionListener( e -> {
            if(parentThread !=null){
                parentThread.setStop();
            }
            //saveResult();
        });
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setContinuousLayout(true);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(0.5);

        progressBar = new JProgressBar();

        splitPane.setPreferredSize(new Dimension(200, 100));
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
                    .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                        .addComponent(progressBar, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 690, Short.MAX_VALUE)
                        .addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 690, Short.MAX_VALUE))
                    .addGap(0))
        );
        gl_contentPane.setVerticalGroup(
            gl_contentPane.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );

        JPanel panel = new JPanel();
        splitPane.setLeftComponent(panel);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        table = new JTable();
        table.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {}
        ));

        table.setDefaultRenderer(Object.class, new AttackResultFrameTableCellRenderer());
        table.setColumnSelectionAllowed(true);
        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                dispRequestResponse();
            }
        });

        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER){
                    e.consume();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                dispRequestResponse();
                if (e.getKeyChar() == KeyEvent.VK_ENTER){
                    int selectedColum = table.getSelectedColumn();
                    if(5 > selectedColum){
                        searchWord(textAreaRequest,reqSerchTextField.getText());
                        reqSerchTextField.requestFocusInWindow();
                    }else{
                        searchWordByRegex(textAreaResponse,resSerchTextField.getText());
                        resSerchTextField.requestFocusInWindow();
                    }
                    e.consume();
                }
            }
        });
        scrollPane.setViewportView(table);
        GroupLayout gl_panel = new GroupLayout(panel);
        gl_panel.setHorizontalGroup(
            gl_panel.createParallelGroup(Alignment.LEADING)
                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE)
        );
        gl_panel.setVerticalGroup(
            gl_panel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel.createSequentialGroup()
                    .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                    .addContainerGap())
        );
        panel.setLayout(gl_panel);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
           public void mouseClicked(MouseEvent e) {dispRequestResponse();}
        });

        JPanel panel_1 = new JPanel();
        splitPane.setRightComponent(panel_1);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        requestLayeredPane = new JLayeredPane();
        tabbedPane.addTab("Request", null, requestLayeredPane, null);

        JScrollPane scrollPaneRequest = new JScrollPane();

        textAreaRequest = new JTextArea();
        textAreaRequest.setEditable(false);
        textAreaRequest.setLineWrap(true);
        scrollPaneRequest.setViewportView(textAreaRequest);

        reqSerchTextField = new JTextField();
        reqSerchTextField.setToolTipText("");
        reqSerchTextField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                searchWord(textAreaRequest,reqSerchTextField.getText());
            }

        });
        reqSerchTextField.setColumns(1);
        GroupLayout gl_requestLayeredPane = new GroupLayout(requestLayeredPane);
        gl_requestLayeredPane.setHorizontalGroup(
            gl_requestLayeredPane.createParallelGroup(Alignment.LEADING)
                .addComponent(reqSerchTextField, GroupLayout.DEFAULT_SIZE, 673, Short.MAX_VALUE)
                .addComponent(scrollPaneRequest, GroupLayout.DEFAULT_SIZE, 673, Short.MAX_VALUE)
        );
        gl_requestLayeredPane.setVerticalGroup(
            gl_requestLayeredPane.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_requestLayeredPane.createSequentialGroup()
                    .addComponent(scrollPaneRequest, GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(reqSerchTextField, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
        );
        requestLayeredPane.setLayout(gl_requestLayeredPane);

        responseLayeredPane = new JLayeredPane();
        tabbedPane.addTab("Response", null, responseLayeredPane, null);

        JScrollPane scrollPaneResponse = new JScrollPane();

        textAreaResponse = new JTextArea();
        textAreaResponse.setEditable(false);
        textAreaResponse.setLineWrap(true);
        scrollPaneResponse.setViewportView(textAreaResponse);

        resSerchTextField = new JTextField();
        resSerchTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchWordByRegex(textAreaResponse,resSerchTextField.getText());
            }
        });
        resSerchTextField.setColumns(10);
        GroupLayout gl_responselayeredPane = new GroupLayout(responseLayeredPane);
        gl_responselayeredPane.setHorizontalGroup(
            gl_responselayeredPane.createParallelGroup(Alignment.LEADING)
                .addComponent(scrollPaneResponse)
                .addComponent(resSerchTextField, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 673, Short.MAX_VALUE)
        );
        gl_responselayeredPane.setVerticalGroup(
            gl_responselayeredPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_responselayeredPane.createSequentialGroup()
                    .addComponent(scrollPaneResponse, GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(resSerchTextField, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                    .addGap(0))
        );
        responseLayeredPane.setLayout(gl_responselayeredPane);
        GroupLayout gl_panel_1 = new GroupLayout(panel_1);
        gl_panel_1.setHorizontalGroup(
            gl_panel_1.createParallelGroup(Alignment.TRAILING)
                .addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE)
        );
        gl_panel_1.setVerticalGroup(
            gl_panel_1.createParallelGroup(Alignment.TRAILING)
                .addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
        );
        panel_1.setLayout(gl_panel_1);
        contentPane.setLayout(gl_contentPane);
        splitPane.setDividerLocation(0.5);
        this.setTitle("Initialization...");
    }

    public void initTabelData(Settings settings){
        this.settings = settings;
        defaultColumnNames = new ArrayList<String>();
        defaultColumnNames.add("No");
        defaultColumnNames.add("Payload");
        defaultColumnNames.add("Status");
        defaultColumnNames.add("length");
        defaultColumnNames.add("comment");

        settings.getPayAndGrep().getGrepPattern().stream().forEach(pattern -> defaultColumnNames.add(pattern));
        tableModel = new AttackResultFrameTableModel(defaultColumnNames.toArray(), 1);
        table.setModel(tableModel);
        tableModel.removeRow(0);
    }

    public void addAttackRequestAndResultToTableData(ExecPlanGroup spg){

        for(ExecPlan sa: spg.getExecPlanList()){
            try{
                int maxLength = String.valueOf(countAllSendPlan).length();
                String newRequestNumber = String.format("%0"+ String.valueOf(maxLength)+"d",sa.getRequestNumber());
                sa.setRequestNumber(newRequestNumber);
                sendPlanMap.put(newRequestNumber, sa);
            }catch (NumberFormatException e ){
                sendPlanMap.put(sa.getRequestNumberString(), sa);
            }
            sendPlanMap.put(sa.getRequestNumberString(), sa);
            AttackResultFrameTabelRawData data = new AttackResultFrameTabelRawData(sa);
            tableModel.addRow(data.getArrayValue());
        }

        progressBar.setValue(progresCount++);
        if(progressBar.getValue() == progressBar.getMaximum()){
            saveResult();
        }

        table.repaint();
        dispPercentage();
    }

    public void setCountAllSendPlan(int countAllSendPlan) {
        this.countAllSendPlan = countAllSendPlan;
        progressBar.setMaximum(countAllSendPlan);
        progressBar.setMinimum(0);
    }

    public void setExexName(String name){
        this.baseExexName = name;
    }

    private void dispPercentage(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.baseExexName);
        sb.append(" (");
        sb.append(progressBar.getValue());
        sb.append("/");
        sb.append(progressBar.getMaximum());
        sb.append(")");
        this.setTitle(new String(sb));
    }

    private int movePos = 0;
    private synchronized void searchWord(JTextArea textArea,String serchpattern){
        Document doc = textArea.getDocument();

        String targetString = null;
        try {
            targetString = doc.getText(0, doc.getLength());
        } catch (BadLocationException e1) {
            logger.debug(e1);
        }
        int pos = targetString.indexOf(serchpattern, movePos);

        if(pos > -1){
            Highlighter highlighter = textArea.getHighlighter();
            try {
                highlighter.addHighlight(pos, pos + serchpattern.length(), DefaultHighlighter.DefaultPainter);
                textArea.setCaretPosition(pos);
                movePos = pos + serchpattern.length();
            } catch (BadLocationException e) {
                logger.debug(e);
            }
        }else{
            movePos = 0;
        }
    }

    private int moveRegexPos = 0;

    private synchronized void searchWordByRegex(JTextArea testArea,String serchpattern){
        testArea.getHighlighter().removeAllHighlights();
        if(serchpattern.equals("") ||
           3 > serchpattern.length() ||
           serchpattern.startsWith("|")	||
           serchpattern.startsWith("\\")){
            return;
        }

        try{
            Highlighter highlighter = testArea.getHighlighter();
            Document doc = testArea.getDocument();
            String text = doc.getText(0, doc.getLength());
            Matcher matcher = Pattern.compile(serchpattern).matcher(text);

            int pos = 0;
            while(matcher.find(pos)) {
                pos = matcher.end();
                highlighter.addHighlight(matcher.start(), pos, DefaultHighlighter.DefaultPainter);
            }

            if (matcher.find(moveRegexPos)){
                moveRegexPos = matcher.end();
                testArea.setCaretPosition(moveRegexPos);
            }else{
                moveRegexPos = 0;
            }
        }catch(java.lang.IndexOutOfBoundsException e){
            logger.debug(e);
        }catch(BadLocationException e1) {
            logger.debug(e1);
        }
    }

    private void dispRequestResponse(){
        textAreaRequest.setText("");
        textAreaResponse.setText("");

        int row = table.getSelectedRow();
        int col = 0;
        int selectedresultCol = table.getSelectedColumn() - 5;
        String key = null;
        try{
            key = (String)table.getModel().getValueAt(row, col);
        }catch(Exception e){
            // java.lang.ArrayIndexOutOfBoundsException
        }

        if(key != null){
            ExecPlan sa =  sendPlanMap.get(key);
            textAreaRequest.setText(sa.getHttpMessage().getHttpRequest().getHttpRequestString());
            textAreaRequest.setCaretPosition(0);
            if(sa.getHttpMessage().getHttpResponse() != null){
                textAreaResponse.setText(sa.getHttpMessage().getHttpResponse().getHttpResponseString());
                textAreaResponse.setCaretPosition(0);
            }else{
                textAreaResponse.setText("");
            }

            reqSerchTextField.setText(sa.getAttackPattern());
            List<String> grepPattern = sa.getGrepPattern();
            if(selectedresultCol > -1){
                String grepString = grepPattern.get(selectedresultCol);
                if(grepString !=null){
                    resSerchTextField.setText(grepPattern.get(selectedresultCol));
                }
            }
        }
    }

    public void close(){
        if(this.parentThread !=null){
            this.parentThread.setStop();
        }

        if(!this.isReadOnlyMode){
            saveResult();
        }
        setVisible(false);
        dispose();
    }

    private void saveResult(){
        if(isReadOnlyMode){
            return;
        }
        new ResultWriter().saveAttackResult(this.baseExexName, this.settings, this.sendPlanMap);
    }

    public boolean loadResult(){
        return loadResult(null);
    }

    public boolean loadResult(File file){
        if(file == null){
            JFileChooser filechooser = new JFileChooser();

            int selected = filechooser.showOpenDialog(this);
            if (selected == JFileChooser.APPROVE_OPTION){
                file = filechooser.getSelectedFile();
            }else{
                return false;
            }
        }

        this.setTitle("Load file.." + file.getName());
        ResultLoader resultloader = new ResultLoader();

        AttackResultFrameSaveData loadObj = resultloader.loadResult(file);
        this.sendPlanMap = loadObj.getSendPlanMap();
        this.settings = loadObj.getSettings();

        if(loadObj.isLoadOK()){
            initTabelData(settings);
            for(String key : sendPlanMap.keySet()) {
                ExecPlan sa = sendPlanMap.get(key);
                AttackResultFrameTabelRawData data = new AttackResultFrameTabelRawData(sa);
                tableModel.addRow(data.getArrayValue());
            }
        }
        return loadObj.isLoadOK();
    }

    //getter setter
    public void setParentThread(ExecPlanWorker parentThread) {
        this.parentThread = parentThread;
    }

    public void setReadOnly(){
        this.isReadOnlyMode = true;
    }
}

