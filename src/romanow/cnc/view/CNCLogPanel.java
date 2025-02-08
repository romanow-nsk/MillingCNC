/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package romanow.cnc.view;

import romanow.cnc.io.COMPortGDriver;
import romanow.cnc.io.I_COMPortGReceiver;
import romanow.cnc.settings.MashineSettings;
import romanow.cnc.slicer.SliceData;
import romanow.cnc.slicer.SliceDataGenerator;
import romanow.cnc.stl.GCodeLayer;
import romanow.cnc.stl.I_STLPoint2D;
import romanow.cnc.stl.STLLine;
import romanow.cnc.stl.STLPoint2D;
import romanow.cnc.utils.Events;
import romanow.cnc.utils.Pair;
import romanow.cnc.utils.UNIException;
import romanow.cnc.utils.Utils;
import romanow.cnc.Values;
import romanow.cnc.settings.WorkSpace;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import romanow.cnc.m3d.ViewAdapter;
import romanow.cnc.m3d.ViewNotifyer;

import static romanow.cnc.Values.*;
import static romanow.cnc.utils.Utils.viewUpdate;

import romanow.cnc.m3d.Slice2DViewer;

/**
 *
 * @author Admin
 */
public class CNCLogPanel extends BasePanel {
    private ViewNotifyer notify;
    private BufferedWriter logFile = null;
    private ViewAdapter viewCommon = new ViewAdapter(null);
    private boolean stopOnWarning=false;
    private WorkSpace ws=null;
    private final COMPortGDriver driver = new COMPortGDriver();
    /**
     * Creates new form CNCViewerPanel
     */
    public CNCLogPanel(CNCViewer baseFrame) {
        super(baseFrame);
        initComponents();
        ws = WorkSpace.ws();
        setComponentsScale();
        ViewAdapter viewCommon = new ViewAdapter(){       // Объект-адаптер для визуальных методов
            @Override
            public boolean onStepLine() {
                if (BYSTEP.isSelected()){
                    pause(true);
                    PAUSE.setText("продолжить");
                }
                return super.onStepLine();
            }
            @Override
            public boolean onStepLayer() {
                if (BYSTEP.isSelected()){
                    pause(true);
                    PAUSE.setText("продолжить");
                    }
                return super.onStepLayer();
                }
            };
        notify = new ViewNotifyer(LOG,null){
            @Override
            public void notify(final int level0, final String mes) {
                super.notify(level0, mes);
                java.awt.EventQueue.invokeLater(
                        ()->{
                            if (level0>= Values.important && logFile!=null){
                                try {
                                    logFile.write(Utils.currentTime()+ " "+mes);
                                    logFile.newLine();
                                } catch (IOException ex) { closeLogFile(ex);}
                            }
                            if (level0>=Values.error){
                                viewCommon.finish();
                                PAUSE.setText("...");
                            }
                            if (level0 >Values.warning || stopOnWarning && level0==Values.warning){           // выше warning  - приостановить
                                if (viewCommon.isRunning()) {
                                    viewCommon.pause(true);
                                    PAUSE.setText("продолжить");
                                }
                            }
                        });
                }
            };
        ws.setNotify(notify,viewCommon);
        }
    private void toLog(String ss){
        notify.notify(common,ss);
        }
    public void openLogFile(){
        if (logFile!=null)
            return;
        try {
            Date xx = new Date();
            if (ws.modelName().length()==0){
                notify.log("Лог-файл только с моделью");
                LogToFile.setSelected(false);
                return;
            }
            String fname = ws.defaultDir()+ ws.modelName()+"_log "+Utils.currentLogName()+".txt";
            ws.testDefaultDir();
            logFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fname),"Windows-1251"));
            LogToFile.setSelected(true);
            } catch (Exception ex) {
                notify.notify(Values.error, ex.getMessage());
                }
        }
    private void closeLogFile(Exception ex){
        if (logFile==null)
            return;
        try { logFile.flush(); logFile.close(); } catch (IOException ex1) {}
        logFile=null;
        LogToFile.setSelected(false);
        if (ex!=null)
            notify.notify(Values.error, ex.getMessage());
        }
    @Override
    public String getName() {
        return "События";
    }

    @Override
    public int modeMask() {
        return PanelMain;
        }
    @Override
    public boolean modeEnabled() {
        return true;
        }

    @Override
    public void onActivate() {
        }

    @Override
    public void onDeactivate() {
        }


    @Override
    public void onClose() {
        }
    @Override

    public void onEvent(int code, int par1, long par2, String par3, Object oo) {
        switch (code){
            case Events.Log:
                notify.notify(par1,par3);
                break;
            case Events.LogFileClose:
                closeLogFile(null);
                break;
            case Events.LogFileOpen:
                openLogFile();
                break;
            case Events.OnWarning:
                stopOnWarning = par1!=0;
                break;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        LOG = new java.awt.TextArea();
        LEVEL = new javax.swing.JComboBox<>();
        LogToFile = new javax.swing.JCheckBox();
        STOP = new javax.swing.JButton();
        PAUSE = new javax.swing.JButton();
        LogStop = new javax.swing.JCheckBox();
        BYSTEP = new javax.swing.JCheckBox();

        setLayout(null);

        LOG.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        add(LOG);
        LOG.setBounds(10, 10, 1060, 680);

        LEVEL.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        LEVEL.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "информ.", "важное", "предупр.", "сбой" }));
        LEVEL.setPreferredSize(new java.awt.Dimension(72, 25));
        LEVEL.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                LEVELItemStateChanged(evt);
            }
        });
        add(LEVEL);
        LEVEL.setBounds(230, 700, 140, 30);

        LogToFile.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        LogToFile.setText("Лог в файле");
        LogToFile.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                LogToFileItemStateChanged(evt);
            }
        });
        add(LogToFile);
        LogToFile.setBounds(650, 700, 120, 20);

        STOP.setText("...");
        STOP.setPreferredSize(new java.awt.Dimension(81, 25));
        STOP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                STOPActionPerformed(evt);
            }
        });
        add(STOP);
        STOP.setBounds(120, 700, 100, 30);

        PAUSE.setText("...");
        PAUSE.setPreferredSize(new java.awt.Dimension(91, 25));
        PAUSE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PAUSEActionPerformed(evt);
            }
        });
        add(PAUSE);
        PAUSE.setBounds(10, 700, 100, 30);

        LogStop.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        LogStop.setText("Остановить лог");
        LogStop.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                LogStopItemStateChanged(evt);
            }
        });
        add(LogStop);
        LogStop.setBounds(490, 700, 150, 24);

        BYSTEP.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        BYSTEP.setText("По шагам");
        add(BYSTEP);
        BYSTEP.setBounds(380, 700, 130, 24);
    }// </editor-fold>//GEN-END:initComponents

    private void LEVELItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_LEVELItemStateChanged
        notify.setLevel(LEVEL.getSelectedIndex());
    }//GEN-LAST:event_LEVELItemStateChanged

    private void LogToFileItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_LogToFileItemStateChanged
        if (LogToFile.isSelected())
        openLogFile();
        else
        closeLogFile(null);
    }//GEN-LAST:event_LogToFileItemStateChanged

    private void STOPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_STOPActionPerformed
        viewCommon.finish();
        PAUSE.setText("...");
        STOP.setText("...");
        sendEvent(Events.OperateFinish,0,0,null,null);
    }//GEN-LAST:event_STOPActionPerformed

    private void PAUSEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PAUSEActionPerformed
        if (!viewCommon.isRunning())
        return;
        if (viewCommon.changePause()){
            PAUSE.setText("продолжить");
        }
        else{
            PAUSE.setText("остановить");
        }
    }//GEN-LAST:event_PAUSEActionPerformed

    private void LogStopItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_LogStopItemStateChanged
        notify.logSuspendState(LogStop.isSelected());
    }//GEN-LAST:event_LogStopItemStateChanged
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public void refresh() {
        }

    @Override
    public void shutDown() {
        }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox BYSTEP;
    private javax.swing.JComboBox<String> LEVEL;
    private java.awt.TextArea LOG;
    private javax.swing.JCheckBox LogStop;
    private javax.swing.JCheckBox LogToFile;
    private javax.swing.JButton PAUSE;
    private javax.swing.JButton STOP;
    // End of variables declaration//GEN-END:variables
}
