/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.m3d;

import romanow.cnc.view.BaseFrame;
import romanow.cnc.slicer.SliceData;

/**
 *
 * @author romanow
 */
public class M3DSelectLayer extends BaseFrame {

    /**
     * Creates new form M3DSelectLayer
     */
    private I_ValueSelect back;
    public M3DSelectLayer(SliceData data, int cLayer,I_ValueSelect back0) {
        if (!tryToStart()) return;
        initComponents();
        setBounds(300,300,300,150);
        back = back0;
        for(int i=0;i<data.size();i++)
            Layers.add(data.get(i).label());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Layers = new java.awt.Choice();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);
        getContentPane().add(Layers);
        Layers.setBounds(21, 21, 153, 20);

        jButton1.setText("Отказаться");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(130, 50, 110, 23);

        jButton2.setText("Выбрать");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2);
        jButton2.setBounds(20, 50, 100, 23);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        onClose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        back.onSelect(Layers.getSelectedIndex());
        onClose();
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Choice Layers;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;

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
