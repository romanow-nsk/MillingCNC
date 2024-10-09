/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epos.slm3d.m3d;

import epos.slm3d.settingsView.PrintSettingsPanel;
import epos.slm3d.settingsView.GlobalSettingsPanel;
import epos.slm3d.settingsView.SliceSettingsPanel;
import epos.slm3d.settingsView.ModelSettingsPanel;
import epos.slm3d.settingsView.I_SettingsPanel;
import epos.slm3d.settings.WorkSpace;
import epos.slm3d.utils.Events;
import epos.slm3d.utils.I_Notify;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author romanow
 */
public class M3DSettings_2 extends BaseFrame {

    /**
     * Creates new form M3DSettings_2
     */
    private ArrayList<I_SettingsPanel> panels = new ArrayList();
    private boolean localValid = WorkSpace.ws().modelPresent();
    private GlobalSettingsPanel glob=null;
    private ModelSettingsPanel model=null;
    public M3DSettings_2(I_Notify mainNotify) {
        if (!tryToStart()) return;
        initComponents();
        setTitle("Настройки");
        this.setBounds(100,100, 800, 450);
        glob =  new GlobalSettingsPanel(mainNotify);
        panels.add((I_SettingsPanel)glob);
        SettingsList.add("Принтер",glob);
        JPanel pn =  new PrintSettingsPanel(WorkSpace.ws().global(),mainNotify);
        panels.add((I_SettingsPanel)pn);
        SettingsList.add("Уставки (общие)",pn);
        pn =  new SliceSettingsPanel(WorkSpace.ws().global(),mainNotify);
        panels.add((I_SettingsPanel)pn);
        SettingsList.add("Слайсинг (общие)",pn);
        if (localValid){
            model =  new ModelSettingsPanel(mainNotify);
            panels.add((I_SettingsPanel)model);
            SettingsList.add("Модель",model);
            pn =  new SliceSettingsPanel(WorkSpace.ws().local(),mainNotify);
            panels.add((I_SettingsPanel)pn);
            SettingsList.add("Слайсинг (модель)",pn);
            pn =  new PrintSettingsPanel(WorkSpace.ws().local(),mainNotify);
            panels.add((I_SettingsPanel)pn);
            SettingsList.add("Уставки (модель)",pn);
            }
        }
    
    private I_Notify notify = new I_Notify() {
        @Override
        public void notify(int level, String mes) { LOG.setText(mes); }
        @Override
        public void setProgress(int proc) {}

        @Override
        public void info(String mes) { LOG.setText(mes); }
        @Override
        public void log(String mes) { LOG.setText(mes); }
        };
    
    @Override
    public void onEvent(int code,boolean on, int value, String name) {
        super.onEvent(code,on,value,name);
        if (code== Events.Settings){
            glob.loadSettings();
            if (model!=null)
                model.loadSettings();
            }
        if (code == Events.Close){
            onClose();
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

        SettingsList = new javax.swing.JTabbedPane();
        Save = new javax.swing.JButton();
        LOG = new javax.swing.JTextField();
        jSeparator8 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);
        getContentPane().add(SettingsList);
        SettingsList.setBounds(10, 22, 740, 320);

        Save.setText("Сохранить");
        Save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveActionPerformed(evt);
            }
        });
        getContentPane().add(Save);
        Save.setBounds(10, 360, 100, 30);

        LOG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LOGActionPerformed(evt);
            }
        });
        getContentPane().add(LOG);
        LOG.setBounds(120, 360, 630, 30);
        getContentPane().add(jSeparator8);
        jSeparator8.setBounds(10, 350, 760, 20);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void SaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveActionPerformed
        for (I_SettingsPanel pn : panels)
            pn.saveSettings();
        WorkSpace.ws().saveSettings();
        onClose();
        dispose();
    }//GEN-LAST:event_SaveActionPerformed

    private void LOGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LOGActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_LOGActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        onClose();
    }//GEN-LAST:event_formWindowClosing



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField LOG;
    private javax.swing.JButton Save;
    private javax.swing.JTabbedPane SettingsList;
    private javax.swing.JSeparator jSeparator8;
    // End of variables declaration//GEN-END:variables
}
