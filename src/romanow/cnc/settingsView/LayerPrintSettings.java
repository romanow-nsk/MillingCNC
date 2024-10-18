/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.settingsView;

import romanow.cnc.view.BaseFrame;
import romanow.cnc.m3d.I_SettingsChanged;
import romanow.cnc.m3d.I_SettingsSelect;
import romanow.cnc.settings.Settings;
import romanow.cnc.utils.I_Notify;

/**
 *
 * @author romanow
 */
public class LayerPrintSettings extends BaseFrame implements I_SettingsChanged {

    /**
     * Creates new form M3DSettings_2
     */
    private PrintSettingsPanel panel;
    private boolean copy;
    private I_SettingsSelect res;
    private Settings src;
    public LayerPrintSettings(Settings src0, boolean copy0,I_Notify notify,I_SettingsSelect res0) {
        if (!tryToStart()) return;
        initComponents();
        copy = copy0;
        res = res0;
        src = src0;
        setTitle("Настройки слоя");
        this.setBounds(100,100, 800, 350);
        panel =  new PrintSettingsPanel(this,src,notify);
        panel.setBounds(0,50, 800, 300);
        add(panel);
        }
     
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Save = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        Save.setText("Сохранить");
        Save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveActionPerformed(evt);
            }
        });
        getContentPane().add(Save);
        Save.setBounds(10, 10, 100, 30);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void SaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveActionPerformed
        if (copy)
            res.onSelect(panel.copySettings());
        else{
            panel.saveSettings();
            res.onSelect(src);
            }
        onClose();
        dispose();
    }//GEN-LAST:event_SaveActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        onClose();
    }//GEN-LAST:event_formWindowClosing



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Save;

    @Override
    public void onChange() {

    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onEvent(int code, boolean on, int value, String name) {

    }

    @Override
    public void refresh() {

    }

    @Override
    public void shutDown() {

    }
    // End of variables declaration//GEN-END:variables
}
