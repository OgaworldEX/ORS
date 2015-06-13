package ui;

import static constants.Constants.*;
import http.HttpHelpers;

import java.awt.Toolkit;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import constants.Constants;
import execPlan.ExecPlanManager;
import settings.HostPort;
import settings.PatternAndGrepPattern;
import settings.Positions;
import settings.Settings;
import settings.SendConfig;
import util.SettingsReader;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

import javax.swing.JRadioButton;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.ImageIcon;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class MainFrame extends JFrame{
    private static final long serialVersionUID = 3657676954343257553L;

    private final static  Logger logger = LogManager.getLogger(MainFrame.class);

    //Settings
    private JTextField textFieldTargetHost;
    private JTextField textFieldTargetPort;
    private JTextField textFieldProxyHost;
    private JTextField textFieldProxyPort;
    private JTextField textFieldWaitms;

    private JCheckBox cbBeforeSend;
    private JCheckBox cbAfterSend;
    private JCheckBox cbIsUseHttps;
    private JCheckBox cbUseProxy;

    //positions
    private JTextPane textAreaPositions;
    private JCheckBox chckbxReplaceCheckBox;

    //Payload and grep
    private JList<String> list_patternlist;
    private JList<String> list_greplist;
    private DefaultListModel<String> patternlistModel;
    private DefaultListModel<String> greplistModel;
    private JCheckBox chckbxRegex;

    public MainFrame() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/images/img_main100x100.png")));
        this.setTitle(APPNAME+VERSION);
        new DropTarget(this,new MainFrameDropTargetListener());
        setBounds(100, 100, 698, 523);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        changeUIWindows();

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setMaximumSize(new Dimension(10, 10));

        JPanel Settings_tab = new JPanel();
        tabbedPane.addTab("Settings", null, Settings_tab, null);

        JLabel lbTargetHost = new JLabel("Host");
        lbTargetHost.setBounds(15, 41, 42, 16);

        JLabel lbTargetPort = new JLabel("Port");
        lbTargetPort.setBounds(15, 69, 42, 16);

        textFieldTargetHost = new JTextField();
        textFieldTargetHost.setBounds(61, 35, 399, 28);
        textFieldTargetHost.setColumns(10);

        textFieldTargetPort = new JTextField();
        textFieldTargetPort.setBounds(61, 63, 105, 28);
        textFieldTargetPort.setColumns(10);

        cbIsUseHttps = new JCheckBox("Use HTTPS");
        cbIsUseHttps.setBounds(166, 65, 99, 23);

        cbIsUseHttps.addActionListener(e -> pushIsHttpsCheckBox());

        Settings_tab.setLayout(null);
        Settings_tab.add(cbIsUseHttps);
        Settings_tab.add(lbTargetHost);
        Settings_tab.add(textFieldTargetHost);
        Settings_tab.add(lbTargetPort);
        Settings_tab.add(textFieldTargetPort);

        JButton btnAutoInput = new JButton("Auto Input");
        btnAutoInput.addActionListener(e -> pushAutoInput());
        btnAutoInput.setBounds(458, 36, 117, 29);
        Settings_tab.add(btnAutoInput);

        cbUseProxy = new JCheckBox("Use Proxy");
        cbUseProxy.setSelected(true);
        cbUseProxy.setBounds(6, 147, 94, 23);
        Settings_tab.add(cbUseProxy);

        JLabel lbProxyHost = new JLabel("Host");
        lbProxyHost.setBounds(19, 182, 42, 16);
        Settings_tab.add(lbProxyHost);

        textFieldProxyHost = new JTextField();
        textFieldProxyHost.setText("localhost");
        textFieldProxyHost.setColumns(10);
        textFieldProxyHost.setBounds(61, 176, 134, 28);
        Settings_tab.add(textFieldProxyHost);

        JLabel lbProxyPort = new JLabel("Port");
        lbProxyPort.setBounds(19, 210, 42, 16);
        Settings_tab.add(lbProxyPort);

        textFieldProxyPort = new JTextField();
        textFieldProxyPort.setText("8080");
        textFieldProxyPort.setColumns(10);
        textFieldProxyPort.setBounds(61, 204, 134, 28);
        Settings_tab.add(textFieldProxyPort);

        JSeparator separator_1 = new JSeparator();
        separator_1.setBounds(6, 100, 608, 12);
        Settings_tab.add(separator_1);

        JLabel lblTarget = new JLabel("Target");
        lblTarget.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblTarget.setBounds(6, 13, 61, 16);
        Settings_tab.add(lblTarget);

        JLabel lbWaitms = new JLabel("Wait ms");
        lbWaitms.setBounds(17, 277, 50, 16);
        Settings_tab.add(lbWaitms);

        textFieldWaitms = new JTextField();
        textFieldWaitms.setText("100");
        textFieldWaitms.setColumns(10);
        textFieldWaitms.setBounds(73, 271, 134, 28);
        Settings_tab.add(textFieldWaitms);

        JLabel lblProxy = new JLabel("Proxy");
        lblProxy.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblProxy.setBounds(6, 119, 61, 16);
        Settings_tab.add(lblProxy);

        JLabel lblInterval = new JLabel("Interval");
        lblInterval.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblInterval.setBounds(6, 249, 61, 16);
        Settings_tab.add(lblInterval);

        JSeparator separator_2 = new JSeparator();
        separator_2.setBounds(6, 238, 608, 12);
        Settings_tab.add(separator_2);

        JSeparator separator_4 = new JSeparator();
        separator_4.setBounds(6, 305, 608, 12);
        Settings_tab.add(separator_4);

        JLabel lblSendbaserequest = new JLabel("BaseRequest");
        lblSendbaserequest.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblSendbaserequest.setBounds(6, 315, 160, 16);
        Settings_tab.add(lblSendbaserequest);

        ButtonGroup BeforeSendOnceGroup = new ButtonGroup();
        JRadioButton rbBeforeSendOnce = new JRadioButton("Once");
        BeforeSendOnceGroup.add(rbBeforeSendOnce);
        rbBeforeSendOnce.setSelected(true);
        rbBeforeSendOnce.setEnabled(true);
        rbBeforeSendOnce.setBounds(131, 343, 64, 23);
        Settings_tab.add(rbBeforeSendOnce);

        JRadioButton rbBeforeSendEvery = new JRadioButton("Every");
        BeforeSendOnceGroup.add(rbBeforeSendEvery);
        rbBeforeSendEvery.setSelected(false);
        rbBeforeSendEvery.setEnabled(true);
        rbBeforeSendEvery.setBounds(200, 343, 65, 23);
        Settings_tab.add(rbBeforeSendEvery);

        cbBeforeSend = new JCheckBox("Before Send");
        cbBeforeSend.addActionListener( e -> {
                if(cbBeforeSend.isSelected()){
                    rbBeforeSendOnce.setEnabled(true);
                    rbBeforeSendEvery.setEnabled(true);
                }else{
                    rbBeforeSendOnce.setEnabled(false);
                    rbBeforeSendEvery.setEnabled(false);
                }
            });

        ButtonGroup afterSendOnceGroup = new ButtonGroup();
        JRadioButton rbAfterSendOnce = new JRadioButton("Once");
        afterSendOnceGroup.add(rbAfterSendOnce);
        rbAfterSendOnce.setSelected(true);
        rbAfterSendOnce.setEnabled(true);
        rbAfterSendOnce.setBounds(131, 373, 64, 23);
        Settings_tab.add(rbAfterSendOnce);

        JRadioButton rbAfterSendEvery = new JRadioButton("Every");
        afterSendOnceGroup.add(rbAfterSendEvery);
        rbAfterSendEvery.setSelected(false);
        rbAfterSendEvery.setEnabled(true);
        rbAfterSendEvery.setBounds(200, 373, 65, 23);
        Settings_tab.add(rbAfterSendEvery);

        cbAfterSend = new JCheckBox("After Send");
        cbAfterSend.setSelected(true);
        cbAfterSend.setBounds(6, 373, 99, 23);
        Settings_tab.add(cbAfterSend);

        cbBeforeSend.setSelected(true);
        cbBeforeSend.setBounds(6, 343, 117, 23);
        Settings_tab.add(cbBeforeSend);

        cbAfterSend.addActionListener(e -> {
                if(cbAfterSend.isSelected()){
                    rbAfterSendOnce.setEnabled(true);
                    rbAfterSendEvery.setEnabled(true);
                }else{
                    rbAfterSendOnce.setEnabled(false);
                    rbAfterSendEvery.setEnabled(false);
                }
            });

        JPanel positions_tab = new JPanel();
        positions_tab.setMaximumSize(new Dimension(10, 10));
        tabbedPane.addTab("Positions", null, positions_tab, null);

        JPanel mainPanel = new JPanel();
        JPanel panel = new JPanel();

        JButton btnAdd = new JButton("Add\u266A");
        btnAdd.setMinimumSize(new Dimension(87, 29));
        btnAdd.setMaximumSize(new Dimension(87, 29));
        btnAdd.setPreferredSize(new Dimension(87, 29));
        btnAdd.addActionListener( e ->  pushAddDelimiter());
        JButton btnClear = new JButton("Clear\u266A");
        btnClear.setMinimumSize(new Dimension(87, 29));
        btnClear.setMaximumSize(new Dimension(87, 29));
        btnClear.setPreferredSize(new Dimension(87, 29));

        btnClear.addActionListener( e -> pushClearDelimiter());
        JButton btnAuto = new JButton("Auto\u266A");
        btnAuto.addActionListener(e-> pushAutoDelimiter());

        JButton btnMarkChangeButton = new JButton("§→♪");
        btnMarkChangeButton.setPreferredSize(new Dimension(87, 29));
        btnMarkChangeButton.setMinimumSize(new Dimension(87, 29));
        btnMarkChangeButton.setMaximumSize(new Dimension(87, 29));

        chckbxReplaceCheckBox = new JCheckBox("Replace");

        JButton btnClear_1 = new JButton("Clear");
        btnClear_1.setMinimumSize(new Dimension(87, 29));
        btnClear_1.setMaximumSize(new Dimension(87, 29));
        btnClear_1.setPreferredSize(new Dimension(87, 29));
        GroupLayout gl_panel = new GroupLayout(panel);
        gl_panel.setHorizontalGroup(
            gl_panel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
                        .addComponent(chckbxReplaceCheckBox)
                        .addComponent(btnMarkChangeButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnClear, 0, 0, Short.MAX_VALUE)
                        .addComponent(btnAuto, 0, 0, Short.MAX_VALUE)
                        .addComponent(btnAdd, GroupLayout.PREFERRED_SIZE, 73, Short.MAX_VALUE)
                        .addComponent(btnClear_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap(19, Short.MAX_VALUE))
        );
        gl_panel.setVerticalGroup(
            gl_panel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(btnAdd, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(btnAuto, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(btnClear, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(btnClear_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(btnMarkChangeButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(18)
                    .addComponent(chckbxReplaceCheckBox)
                    .addGap(276))
        );
        gl_panel.setAutoCreateContainerGaps(true);
        gl_panel.setAutoCreateGaps(true);
        panel.setLayout(gl_panel);
        btnClear_1.addActionListener(e -> textAreaPositions.setText(""));

        btnMarkChangeButton.addActionListener(e -> pushMarkChange());
        positions_tab.setLayout(new GridLayout(0, 1, 0, 0));

        JPanel panel_2 = new JPanel();
        panel_2.setMinimumSize(new Dimension(20, 20));
        panel_2.setPreferredSize(new Dimension(20, 20));
        panel_2.setMaximumSize(new Dimension(30000, 30000));
        GroupLayout gl_mainPanel = new GroupLayout(mainPanel);
        gl_mainPanel.setHorizontalGroup(
            gl_mainPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_mainPanel.createSequentialGroup()
                    .addGap(2)
                    .addComponent(panel, GroupLayout.PREFERRED_SIZE, 112, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
                    .addContainerGap())
        );
        gl_mainPanel.setVerticalGroup(
            gl_mainPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_mainPanel.createSequentialGroup()
                    .addGroup(gl_mainPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
                        .addComponent(panel, GroupLayout.PREFERRED_SIZE, 227, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );
        gl_mainPanel.setAutoCreateContainerGaps(true);
        gl_mainPanel.setAutoCreateGaps(true);

        JScrollPane scrollPane_2 = new JScrollPane();
        scrollPane_2.setMinimumSize(new Dimension(10, 10));
        scrollPane_2.setPreferredSize(new Dimension(10, 10));
        scrollPane_2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane_2.setMaximumSize(new Dimension(30000, 30000));
        GroupLayout gl_panel_2 = new GroupLayout(panel_2);
        gl_panel_2.setHorizontalGroup(
            gl_panel_2.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel_2.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                    .addContainerGap())
        );
        gl_panel_2.setVerticalGroup(
            gl_panel_2.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel_2.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                    .addContainerGap())
        );

        textAreaPositions = new JTextPane();
        textAreaPositions.setMaximumSize(new Dimension(30000, 30000));
        textAreaPositions.setSize(new Dimension(1, 1));
        textAreaPositions.setPreferredSize(new Dimension(1, 1));
        textAreaPositions.setAutoscrolls(false);
        textAreaPositions.setDocument(new DefaultStyledDocument(new StyleContext()));
        textAreaPositions.getDocument().addDocumentListener(new DocumentListner());

        scrollPane_2.setViewportView(textAreaPositions);
        gl_panel_2.setAutoCreateContainerGaps(true);
        gl_panel_2.setAutoCreateGaps(true);
        panel_2.setLayout(gl_panel_2);
        mainPanel.setLayout(gl_mainPanel);
        positions_tab.add(mainPanel);

        list_patternlist = new JList<String>();
        patternlistModel = new DefaultListModel<String>();
        greplistModel = new DefaultListModel<String>();

        JSplitPane patternAndGrep_splitPane_tab = new JSplitPane();
        patternAndGrep_splitPane_tab.setOrientation(JSplitPane.VERTICAL_SPLIT);
        tabbedPane.addTab("Pattern&Grep", null, patternAndGrep_splitPane_tab, null);

        JPanel panel_grep = new JPanel();
        patternAndGrep_splitPane_tab.setRightComponent(panel_grep);

        JScrollPane scrollPane_grep = new JScrollPane();

        JPanel panel_grep_menu = new JPanel();
        GroupLayout gl_panel_grep = new GroupLayout(panel_grep);
        gl_panel_grep.setHorizontalGroup(
            gl_panel_grep.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel_grep.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel_grep_menu, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(scrollPane_grep, GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE)
                    .addContainerGap())
        );
        gl_panel_grep.setVerticalGroup(
            gl_panel_grep.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel_grep.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panel_grep.createParallelGroup(Alignment.LEADING)
                        .addComponent(panel_grep_menu, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
                        .addComponent(scrollPane_grep, GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE))
                    .addContainerGap())
        );

        list_greplist = new JList<>();
        list_greplist.setModel(this.greplistModel);
        scrollPane_grep.setViewportView(list_greplist);
        panel_grep_menu.setLayout(new GridLayout(0, 1, 0, 0));

        JLabel label_3 = new JLabel("Grep");
        label_3.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        label_3.setHorizontalAlignment(SwingConstants.CENTER);
        panel_grep_menu.add(label_3);

        JButton button_1 = new JButton("Paste");
        button_1.addActionListener( e-> pushPasteGrepPattern());
        panel_grep_menu.add(button_1);

        JButton button = new JButton("Load");
        button.addActionListener(e -> grepLoad() );
        panel_grep_menu.add(button);

        JButton button_2 = new JButton("Remove");
        button_2.addActionListener( e -> grepRemove());
        panel_grep_menu.add(button_2);

        JButton button_3 = new JButton("Clear");
        button_3.addActionListener( e -> pushClearGrepPattern());
        panel_grep_menu.add(button_3);

        chckbxRegex = new JCheckBox("Regex");
        chckbxRegex.setSelected(true);
        chckbxRegex.addActionListener(e -> pushRegexCheckBox());

        panel_grep_menu.add(chckbxRegex);
        gl_panel_grep.setAutoCreateGaps(true);
        gl_panel_grep.setAutoCreateContainerGaps(true);
        panel_grep.setLayout(gl_panel_grep);

        JPanel panel_payloads = new JPanel();
        patternAndGrep_splitPane_tab.setLeftComponent(panel_payloads);

        JPanel panel_payloads_menu = new JPanel();

        JScrollPane scrollPane_payloads = new JScrollPane();
        GroupLayout gl_panel_payloads = new GroupLayout(panel_payloads);
        gl_panel_payloads.setHorizontalGroup(
            gl_panel_payloads.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel_payloads.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel_payloads_menu, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(scrollPane_payloads, GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
                    .addContainerGap())
        );
        gl_panel_payloads.setVerticalGroup(
            gl_panel_payloads.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel_payloads.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panel_payloads.createParallelGroup(Alignment.LEADING)
                        .addComponent(panel_payloads_menu, GroupLayout.PREFERRED_SIZE, 144, GroupLayout.PREFERRED_SIZE)
                        .addComponent(scrollPane_payloads, GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
                    .addContainerGap())
        );

        list_patternlist = new JList<>();
        scrollPane_payloads.setViewportView(list_patternlist);
        list_patternlist.setModel(patternlistModel);

        panel_payloads_menu.setLayout(new GridLayout(0, 1, 0, 0));

        JLabel lblPattern = new JLabel("Pattern");
        lblPattern.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        lblPattern.setHorizontalAlignment(SwingConstants.CENTER);
        panel_payloads_menu.add(lblPattern);

        JButton button_4 = new JButton("Paste");
        button_4.addActionListener( e -> pushPastePayLoad());
        panel_payloads_menu.add(button_4);

        JButton button_5 = new JButton("Load");
        button_5.addActionListener(e -> payLoad());
        panel_payloads_menu.add(button_5);

        JButton button_6 = new JButton("Remove");
        button_6.addActionListener(e -> pushRemovePayload());
        panel_payloads_menu.add(button_6);

        JButton button_7 = new JButton("Clear");
        button_7.addActionListener( e -> pushClearPayload());
        panel_payloads_menu.add(button_7);
        gl_panel_payloads.setAutoCreateGaps(true);
        gl_panel_payloads.setAutoCreateContainerGaps(true);
        panel_payloads.setLayout(gl_panel_payloads);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnOgaRequestSender = new JMenu("ORS");
        mnOgaRequestSender.setIcon(new ImageIcon(MainFrame.class.getResource("/images/img_main30x30.png")));
        menuBar.add(mnOgaRequestSender);

        JMenuItem mnAboutORS = new JMenuItem("About ORS");
        mnAboutORS.setIcon(new ImageIcon(MainFrame.class.getResource("/images/img_main24x24.png")));

        mnAboutORS.addActionListener(e ->
                JOptionPane.showMessageDialog(
                this,
                "OgaRequestSender" +
                VERSION +
                "\r\n\r\n"+
                "WebAplication Test Tool"+
                "\r\n\r\n"+
                "zangy..",
                "About " + APPNAME,
                JOptionPane.INFORMATION_MESSAGE,
                new ImageIcon(MainFrame.class.getResource("/images/img_main100x100.png")))
                );

        mnOgaRequestSender.add(mnAboutORS);
        JSeparator separator1 = new JSeparator();
        mnOgaRequestSender.add(separator1);

        JMenu mnSettings = new JMenu("Settings");
        mnSettings.setIcon(new ImageIcon(MainFrame.class.getResource("/images/img_settings_24x24.png")));
        mnOgaRequestSender.add(mnSettings);

        JMenu mnLookAndFeel = new JMenu("Look&Feel");
        mnLookAndFeel.setIcon(new ImageIcon(MainFrame.class.getResource("/images/img_laf_24x24.png")));
        mnSettings.add(mnLookAndFeel);

        //button group
        ButtonGroup menulafgroup = new ButtonGroup();

        JRadioButtonMenuItem rdbtnmntmMetal = new JRadioButtonMenuItem("Metal");
        rdbtnmntmMetal.addActionListener(e -> changeUIMetal());
        mnLookAndFeel.add(rdbtnmntmMetal);
        menulafgroup.add(rdbtnmntmMetal);

        JRadioButtonMenuItem rdbtnmntmMotif = new JRadioButtonMenuItem("Motif");
        rdbtnmntmMotif.addActionListener(e -> this.changeUIMotif());
        mnLookAndFeel.add(rdbtnmntmMotif);
        menulafgroup.add(rdbtnmntmMotif);

        JRadioButtonMenuItem rdbtnmntmWindows = new JRadioButtonMenuItem("Windows");
        rdbtnmntmWindows.addActionListener(e -> changeUIWindows());
        mnLookAndFeel.add(rdbtnmntmWindows);
        menulafgroup.add(rdbtnmntmWindows);

        JRadioButtonMenuItem rdbtnmntmMac = new JRadioButtonMenuItem("Mac");
        rdbtnmntmMac.addActionListener(e -> this.changeUIMac());
        mnLookAndFeel.add(rdbtnmntmMac);
        menulafgroup.add(rdbtnmntmMac);

        JRadioButtonMenuItem rdbtnmntmNimbus = new JRadioButtonMenuItem("Nimbus");
        rdbtnmntmNimbus.addActionListener(e -> this.changeUINimbus());
        mnLookAndFeel.add(rdbtnmntmNimbus);
        menulafgroup.add(rdbtnmntmNimbus);

        JMenu mnClearSettings = new JMenu("Clear");
        mnClearSettings.setIcon(new ImageIcon(MainFrame.class.getResource("/images/img_reset_24x24.png")));
        mnSettings.add(mnClearSettings);

        JMenuItem mntmClearTarget = new JMenuItem("Target");
        mntmClearTarget.addActionListener( e -> pushMenuResetSendSettings());

        JMenuItem mntmAllReset = new JMenuItem("ALL");
        mntmAllReset.addActionListener( e ->  pushResetALL());
        mnClearSettings.add(mntmAllReset);

        JSeparator separator_3 = new JSeparator();
        mnClearSettings.add(separator_3);
        mnClearSettings.add(mntmClearTarget);

        JMenuItem mntmResetPositions = new JMenuItem("Positions");
        mntmResetPositions.addActionListener( e ->  pushResetPositions());
        mnClearSettings.add(mntmResetPositions);

        JMenuItem mntmPayloadsGrep = new JMenuItem("Payloads&Grep");
        mntmPayloadsGrep.addActionListener( e ->  resetPayloadAndGrep());
        mnClearSettings.add(mntmPayloadsGrep);

        JSeparator separator2 = new JSeparator();
        mnOgaRequestSender.add(separator2);

        JMenuItem mnQuit = new JMenuItem("Quit");
        mnQuit.setIcon(new ImageIcon(MainFrame.class.getResource("/images/img_exit_24x24.png")));
        mnQuit.addActionListener(e -> System.exit(0));
        mnOgaRequestSender.add(mnQuit);

        JMenu mnFile = new JMenu("File");
        mnFile.setIcon(new ImageIcon(MainFrame.class.getResource("/images/img_file_30x30.png")));
        menuBar.add(mnFile);

        JMenuItem mnLoadresultfile = new JMenuItem("Load ResultFile");
        mnLoadresultfile.setIcon(new ImageIcon(MainFrame.class.getResource("/images/img_loadresultfile_24x24.png")));
        mnLoadresultfile.addActionListener(e -> loadResultFile());
        mnFile.add(mnLoadresultfile);

        JMenu mnSend = new JMenu("Send");
        mnSend.setIcon(new ImageIcon(MainFrame.class.getResource("/images/img_start_30x30.png")));
        menuBar.add(mnSend);

        JMenuItem mnStart = new JMenuItem("Start");
        mnStart.setIcon(new ImageIcon(MainFrame.class.getResource("/images/img_go_24x24.png")));
        mnStart.addActionListener( e -> startAtack());
        mnSend.add(mnStart);

        JMenuItem mnStartSendtoFolder = new JMenuItem("Start (Send to the Folder)");
        mnStartSendtoFolder.setIcon(new ImageIcon(MainFrame.class.getResource("/images/img_gofolder_24x24.png")));
        mnStartSendtoFolder.addActionListener(e-> startAtackAndSendtoFolder());
        mnSend.add(mnStartSendtoFolder);

        setAttackPattern();
        setRegexGrepPattern();

        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setAutoCreateGaps(true);
        groupLayout.setAutoCreateContainerGaps(true);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 606, Short.MAX_VALUE)
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 417, Short.MAX_VALUE)
        );
        getContentPane().setLayout(groupLayout);

        pushResetALL();
    }

    private void setAttackPattern(){
        List<String> attackPatternList = SettingsReader.readAttackPatern();
        patternlistModel.removeAllElements();
        for(String pattern:attackPatternList){
            int index = patternlistModel.size();
            patternlistModel.add(index, pattern);
        }
    }

    private void setGrepPattern(){
        greplistModel.clear();
        List<String> grepPatternList = SettingsReader.readGrepPatern();
        greplistModel.removeAllElements();
        for(String pattern:grepPatternList){
            int index = greplistModel.size();
            greplistModel.add(index, pattern);
        }
    }

    private void setRegexGrepPattern(){
        greplistModel.clear();
        List<String> grepPatternList = SettingsReader.readRegexGrepPatern();
        for(String pattern:grepPatternList){
            int index = greplistModel.size();
            greplistModel.add(index, pattern);
        }
    }

    private Boolean isOkInputData(){
        Boolean retStatus = false;

        String targetPosText = textAreaPositions.getText();
        int count =  StringUtils.countMatches(targetPosText, "♪");
        if(count == 0){
            retStatus = false;
            JOptionPane.showMessageDialog(
                    this,
                    DELIMITER + "がありません",
                    "実行できません",
                    JOptionPane.INFORMATION_MESSAGE,
                    null);
        }else if(count == 1){
            retStatus = false;
            JOptionPane.showMessageDialog(
                    this,
                    DELIMITER + "が一つしかありません。挟みます",
                    "実行できません",
                    JOptionPane.INFORMATION_MESSAGE,
                    null);
        }else if(count % 2 != 0){
            retStatus = false;
            JOptionPane.showMessageDialog(
                    this,
                    DELIMITER + "の数が奇数です。" + DELIMITER +"は挟んで指定するので偶数になります",
                    "実行できません",
                    JOptionPane.INFORMATION_MESSAGE,
                    null);

        }else{
            retStatus = true;
        }
        return retStatus;
    }

    private void startAtack(){
        startAtack("");
    }

    private void startAtack(String outputFolder){
        if (isOkInputData() == false){
            return;
        }
        Settings sendSettings = createSettings(outputFolder);
        ExecPlanManager sm = ExecPlanManager.getInstance();
        sm.startSend(sendSettings);
    }

    private void startAtackAndSendtoFolder(){
        MainFrameSaveFolderDialog savefolderDialog = new MainFrameSaveFolderDialog();
        savefolderDialog.setModal(true);
        savefolderDialog.setAlwaysOnTop(true);
        savefolderDialog.setAutoRequestFocus(true);
        savefolderDialog.setLocation(getX() + (getWidth()/6),getY() + (getHeight()/6));
        savefolderDialog.setVisible(true);
        savefolderDialog.dispose();

        if(savefolderDialog.isInputGO()){
            startAtack(savefolderDialog.getInputText());
        }
    }

    private Settings createSettings(String outputFolder){
        SendConfig sendconfig = new SendConfig();

        //targetInfo
        HostPort targetHostPort = new HostPort(textFieldTargetHost.getText(),Integer.valueOf(textFieldTargetPort.getText()).intValue());
        sendconfig.setTargetInfo(targetHostPort);

        //ProxyInfo
        HostPort targetProxyInfo = new HostPort(textFieldProxyHost.getText(),Integer.valueOf(textFieldProxyPort.getText()).intValue());
        sendconfig.setTargetProxyInfo(targetProxyInfo);

        if(cbIsUseHttps.isSelected()){
            sendconfig.setUseHTTPS();
        }else{
            sendconfig.setNotUseHTTPS();
        }

        if(cbUseProxy.isSelected()){
            sendconfig.setUseProxy();
        }else{
            sendconfig.setNotUseProxy();
        }

        if(this.cbBeforeSend.isSelected()){
            if(cbBeforeSend.isSelected()){
                sendconfig.setBeforeSendaBaseRequestEnum(BeforeSendBaseRequestEnum.once);
            }else{
                sendconfig.setBeforeSendaBaseRequestEnum(BeforeSendBaseRequestEnum.every);
            }
        }

        if(this.cbAfterSend.isSelected()){
            if(cbAfterSend.isSelected()){
                sendconfig.setAfterSendaBaseRequestEnum(AfterSendBaseRequestEnum.once);
            }else{
                sendconfig.setAfterSendaBaseRequestEnum(AfterSendBaseRequestEnum.every);
            }
        }

        //out put folder
        sendconfig.setResultOutFolder(outputFolder);

        //position
        String areaText = changeLFtoCRLF(textAreaPositions.getText());

        Positions positions = null;
        if(cbUseProxy.isSelected()){
            positions = new Positions(HttpHelpers.changeRequestLineForProxy(areaText, targetHostPort, cbIsUseHttps.isSelected()));
        }else{
            positions = new Positions(HttpHelpers.changeRequestLineForNotProxy(areaText, targetHostPort));
        }
        positions.setIsReplace(chckbxReplaceCheckBox.isSelected());

        //pay pattern
        List<String> payloadsPattern = new ArrayList<>();
        for(int i=0;i<patternlistModel.getSize();i++){
            String value = (String)patternlistModel.getElementAt(i);
            payloadsPattern.add(value);
        }

        //grep pattern
        List<String> grepPattern = new ArrayList<>();
        for(int i=0;i<greplistModel.getSize();i++){
            String value = (String)greplistModel.getElementAt(i);
            grepPattern.add(value);
        }
        PatternAndGrepPattern payAndGrep = new PatternAndGrepPattern(payloadsPattern, grepPattern);
        payAndGrep.setRegex(chckbxRegex.isSelected());

        return new Settings(sendconfig, positions, payAndGrep);
    }

    private void loadResultFile(){
        loadResultFile(null);
    }

    private void loadResultFile(File file){
        AttackResultFrame resultFrame = new AttackResultFrame();
        if (resultFrame.loadResult(null) == true){
            resultFrame.setReadOnly();
            resultFrame.setVisible(true);
            resultFrame.invalidate();
        }else{
            resultFrame.setVisible(false);
            resultFrame = null;
        }
    }

    private String changeCRLFtoLF(String target){
        return target.replaceAll("\r\n","\n");
    }

    private String changeLFtoCRLF(String target){
        String retString = null;
        if(target.indexOf("\r\n") == -1){
            retString = target.replaceAll("\n","\r\n");
        }else{
            retString = target;
        }
        return retString;
    }

    private void pushAddDelimiter(){
        int pos = textAreaPositions.getCaretPosition();
        int posStart = textAreaPositions.getSelectionStart();
        int posEnd = textAreaPositions.getSelectionEnd();
        String areaText = changeCRLFtoLF(textAreaPositions.getText());

        int stringLength = areaText.length();
        String beforeString = "";
        String paramaterString = "";
        String afterString = "";

        StringBuilder strBuilder = new StringBuilder();

        if (pos == posStart && pos == posEnd) {
            beforeString = areaText.substring(0, pos);
            afterString = areaText.substring(pos, stringLength);
            strBuilder.append(beforeString);
            strBuilder.append(Constants.DELIMITER);
            strBuilder.append(afterString);
        }else {
            beforeString = areaText.substring(0, posStart);
            paramaterString= areaText.substring(posStart, posEnd);
            afterString = areaText.substring(posEnd, stringLength);
            strBuilder.append(beforeString);
            strBuilder.append(Constants.DELIMITER);
            strBuilder.append(paramaterString);
            strBuilder.append(Constants.DELIMITER);
            strBuilder.append(afterString);
        }
        textAreaPositions.setSelectionStart(posStart);
        textAreaPositions.setSelectionEnd(posEnd);
        textAreaPositions.setText(strBuilder.toString());
        textAreaPositions.setCaretPosition(pos);
    }

    private void pushAutoInput(){
        String targetText = textAreaPositions.getText();
        Pattern p = Pattern.compile("Host: .*");
        Matcher m = p.matcher(targetText);

        if(m.find()){
            String hostName = m.group();
            textFieldTargetHost.setText(StringUtils.substringAfter(hostName, " "));
        }
    }

    //Postions
    private void pushAutoDelimiter(){
        if(textAreaPositions.getText().length() < 1){return;}
        String newStr = changeLFtoCRLF(textAreaPositions.getText());
        String planeString = util.PatternMarker.removeDelimiter(newStr);
        String replaceAfterString = util.PatternMarker.addAutoDelimiter(planeString);
        textAreaPositions.setText(replaceAfterString);
    }

    private void pushClearDelimiter(){
        int startPos = textAreaPositions.getSelectionStart();
        int endPos = textAreaPositions.getSelectionEnd();
        String targetText = changeCRLFtoLF(textAreaPositions.getText());
        String clearText = util.PatternMarker.removeDelimiter(targetText,startPos,endPos);
        textAreaPositions.setText(clearText);
    }

    private void pushMarkChange(){
        String tmp = textAreaPositions.getText();
        String newString = tmp.replaceAll("§", "♪");
        textAreaPositions.setText(newString);
        //textAreaPositions.setDocument(getDelimiterColorDocument(newString));
    }

    private void pushRegexCheckBox(){
        if(chckbxRegex.isSelected()){
            setRegexGrepPattern();
        }else{
            setGrepPattern();
        }
    }

    //Send Target
    private void pushIsHttpsCheckBox(){
        if(cbIsUseHttps.isSelected()){
            textFieldTargetPort.setText("443");
        }else{
            textFieldTargetPort.setText("80");
        }
    }

    //button event Grep
    private void pushPasteGrepPattern(){
        Toolkit kit = Toolkit.getDefaultToolkit();
        Clipboard clip = kit.getSystemClipboard();
        String text = null;
        try {
            text =  (String) clip.getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            logger.debug(e);
        }
        if (text != null){
            int size = greplistModel.getSize();
            greplistModel.add(size, text);
        }
    }

    private void setDelimiterColorDocumentAtPositons(){
        String targetString = textAreaPositions.getText();
        StyledDocument targetDoc = (StyledDocument) textAreaPositions.getDocument();

        //clear style
        SimpleAttributeSet plane = new SimpleAttributeSet();
        targetDoc.setCharacterAttributes(0, targetDoc.getLength(), plane, true);

        //add style
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setBackground(attr, new Color(245, 172, 0));
        String httpText = changeCRLFtoLF(targetString);

        int begeinIndex = -1;
        int endIndex = -1;
        boolean isSetAttrOK = false;
        String [] charArray = httpText.split("");
        for (int i=0;i<charArray.length;i++){
            if(DELIMITER.equals(charArray[i])){
                if(isSetAttrOK == false){
                    begeinIndex = i;
                    isSetAttrOK = true;
                }else{
                    endIndex = i + 1;
                    targetDoc.setCharacterAttributes(begeinIndex, endIndex - begeinIndex, attr, true);
                    isSetAttrOK = false;
                }
            }
        }
    }

    void invokeSetHighlight() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setDelimiterColorDocumentAtPositons();
            }
        });
    }

    //document listner
    class DocumentListner implements DocumentListener {
        public void insertUpdate(DocumentEvent e) {
            invokeSetHighlight();
        }
        public void removeUpdate(DocumentEvent e) {
            invokeSetHighlight();
        }
        public void changedUpdate(DocumentEvent e) {
            //invokeSetHighlight();
        }
    }

    private void grepLoad(){
        readTextPattern(this.greplistModel);
    }

    private void grepRemove(){
        int selectIndex = list_greplist.getSelectedIndex();
        if(selectIndex > -1){
            greplistModel.remove(selectIndex);
        }
    }

    private void pushClearGrepPattern(){
        greplistModel.removeAllElements();
    }

    //button event Payload
    private void pushPastePayLoad(){
        Toolkit kit = Toolkit.getDefaultToolkit();
        Clipboard clip = kit.getSystemClipboard();
        String text = null;
        try {
            text =  (String)clip.getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            logger.debug(e);
        }

        if (text != null){
            int size = patternlistModel.getSize();
            patternlistModel.add(size, text);
        }
    }

    private void payLoad(){
        readTextPattern(this.patternlistModel);
    }

    //menu button event
    private void pushRemovePayload(){
        //payload
        int selectIndex = list_patternlist.getSelectedIndex();
        if(selectIndex > -1){
            patternlistModel.remove(selectIndex);
        }
    }

    private void pushClearPayload(){
        patternlistModel.removeAllElements();
    }

    private void pushMenuResetSendSettings(){
        textFieldTargetHost.setText("localhost");
        textFieldTargetPort.setText("80");
        cbIsUseHttps.setSelected(false);
    }

    private void pushResetPositions(){
        StringBuilder sb = new StringBuilder();
        sb.append("POST /sample?p1=♪p1val♪&p2=♪p2val♪ HTTP/1.0\n");
        sb.append("Host: localhost\n");
        sb.append("Cookie: name=value\n");
        sb.append("Content-Length: 17\n");
        sb.append("\n");
        sb.append("param1=♪value1♪&param2=♪param2♪");

        textAreaPositions.setText(sb.toString());
        chckbxReplaceCheckBox.setSelected(true);
    }

    private void resetPayloadAndGrep(){
        setAttackPattern();
        setRegexGrepPattern();
        chckbxRegex.setSelected(true);
    }

    private void pushResetALL(){
        pushMenuResetSendSettings();
        pushResetPositions();
        resetPayloadAndGrep();
    }

    private void readTextPattern(DefaultListModel<String> listModel){
        JFileChooser filechooser = new JFileChooser();
        File file = null;
        int selected = filechooser.showOpenDialog(this);
        if (selected == JFileChooser.APPROVE_OPTION){
            file = filechooser.getSelectedFile();
        }else{
            return;
        }

        List<String> newPatternList = new ArrayList<>();
        try(Stream<String> stream = Files.lines(Paths.get(file.getPath()),StandardCharsets.UTF_8)){
            stream.forEach(line -> newPatternList.add(line));
        } catch (IOException e) {
            logger.debug(e);
            return;
        }

        listModel.removeAllElements();
        newPatternList.stream().forEach(pattern -> listModel.add(listModel.size(), pattern));
    }

    //UI Change
    private void changeUIMotif(){
        try{
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        }catch(Exception e){
            logger.trace(e);
        }

    }
    private void changeUIMetal(){
        try{
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        }catch(Exception e){
            logger.trace(e);
        }
    }

    private void changeUIWindows(){
        try{
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        }catch(Exception e){
            logger.trace(e);
        }
    }

    private void changeUIMac(){
        try{
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.mac.MacLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        }catch(Exception e){
            logger.trace(e);
        }
    }

    private void changeUINimbus(){
        try{
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        }catch(Exception e){
            logger.trace(e);
        }
    }
}
