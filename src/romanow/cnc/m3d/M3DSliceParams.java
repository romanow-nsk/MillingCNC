/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.m3d;

import romanow.cnc.settings.Settings;
import romanow.cnc.settingsView.SliceSettingsPanel;
import romanow.cnc.settingsView.StatisticPanel;
import romanow.cnc.slicer.SliceRezult;
import romanow.cnc.utils.I_Notify;

/**
 *
 * @author romanow
 */
public class M3DSliceParams extends javax.swing.JFrame implements I_SettingsChanged{
    private I_Notify notify;
    private SliceSettingsPanel panel;
    private StatisticPanel panel2;
    /**
     * Creates new form M3DReSlice
     */
    public M3DSliceParams(SliceRezult res, Settings set, I_Notify notify0) {
        initComponents();
        notify = notify0;
        setTitle("Параметры слайсинга");
        setBounds(150,150,600,450);
        panel = new SliceSettingsPanel(this,set,notify);
        panel.setBounds(0,0,300,450);
        add(panel);
        panel2 = new StatisticPanel();
        panel2.setValues(res);
        panel2.setBounds(300,0,250,450);  
        add(panel2);
        setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void onChange() {

    }

    @Override
    public void onCancel() {

    }

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}