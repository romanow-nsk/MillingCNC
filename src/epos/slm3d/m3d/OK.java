/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epos.slm3d.m3d;

import java.awt.Rectangle;

/**
 *
 * @author romanow
 */
public class OK extends javax.swing.JFrame {
    private I_Button ok;
    /**
     * Creates new form OK
     */
    public OK(Rectangle rr, String title, I_Button ok0) {
        this(20,20,rr,title,ok0);
        }
    public OK(int dx,int dy,Rectangle rr, String title, I_Button ok0) {
        initComponents();
        ok = ok0;
        OK.setText(title);
        setBounds(rr.x+dx,rr.y+dy,200,90);
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

        OK = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        OK.setText("jButton1");
        OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKActionPerformed(evt);
            }
        });
        getContentPane().add(OK);
        OK.setBounds(10, 10, 150, 30);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void OKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKActionPerformed
        ok.onPush();
        dispose();
    }//GEN-LAST:event_OKActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton OK;
    // End of variables declaration//GEN-END:variables
}
