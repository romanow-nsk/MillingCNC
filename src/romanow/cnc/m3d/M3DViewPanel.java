/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.m3d;

import javax.swing.JPanel;

/**
 *
 * @author EPOS
 */
public class M3DViewPanel extends javax.swing.JFrame {
    private ViewAdapter viewCommon=null;
    /**
     * Creates new form M3DViewPanel
     */
    public M3DViewPanel() {
        initComponents();
        setBounds(100,100,550,600);
        setTitle("Просмотр");
    }
    public JPanel fld(){ return FLD; }
    public void setAdapter(ViewAdapter viewCommon0){ 
        viewCommon = viewCommon0; 
        }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        FLD = new javax.swing.JPanel();
        Scale = new javax.swing.JSlider();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        FLD.setBackground(new java.awt.Color(255, 255, 255));
        FLD.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout FLDLayout = new javax.swing.GroupLayout(FLD);
        FLD.setLayout(FLDLayout);
        FLDLayout.setHorizontalGroup(
            FLDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 498, Short.MAX_VALUE)
        );
        FLDLayout.setVerticalGroup(
            FLDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 508, Short.MAX_VALUE)
        );

        getContentPane().add(FLD);
        FLD.setBounds(10, 10, 500, 510);

        Scale.setMinimum(-50);
        Scale.setValue(0);
        Scale.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ScaleStateChanged(evt);
            }
        });
        getContentPane().add(Scale);
        Scale.setBounds(10, 520, 200, 23);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ScaleStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ScaleStateChanged
        double vv= Scale.getValue();
        if (vv>0)
        viewCommon.mas = viewCommon.sz() * vv/10;
        else
        if (vv<0)
        viewCommon.mas = viewCommon.sz() / (-vv/10);
        else
        viewCommon.mas = viewCommon.sz();
    }//GEN-LAST:event_ScaleStateChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        setVisible(false);
    }//GEN-LAST:event_formWindowClosing



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel FLD;
    private javax.swing.JSlider Scale;
    // End of variables declaration//GEN-END:variables
}
