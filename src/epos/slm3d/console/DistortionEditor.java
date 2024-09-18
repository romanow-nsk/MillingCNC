/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epos.slm3d.console;

import epos.slm3d.m3d.BaseFrame;
import epos.slm3d.utils.Values;
import java.awt.Color;
import java.awt.Graphics;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 *
 * @author romanow
 */
public class DistortionEditor extends BaseFrame {
    private DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
    private DecimalFormat df = new DecimalFormat("00.000", dfs);
    /**
     * Creates new form DistortionEditor
     */
    private Distortion data;
    public DistortionEditor() {
        if (!tryToStart()) return;
        initComponents();
        setBounds(200,200,550,350);
        setTitle("Коррекция искажений");
        setVisible(true);
        data = new Distortion();
        }
    private void view(){
        Graphics g = PANE.getGraphics();
        for(int y=0;y<256;y++)
            for(int x=0;x<256;x++){
                Color cc = data.toColor(x, y);
                g.setColor(cc);
                g.drawLine(x, 256-y, x+1, 256-y);
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

        jCheckBox1 = new javax.swing.JCheckBox();
        PANE = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        KY = new javax.swing.JTextField();
        VX = new javax.swing.JTextField();
        KR = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        KX = new javax.swing.JTextField();
        VY = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        X = new javax.swing.JTextField();
        Y = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        Mode = new javax.swing.JCheckBox();

        jCheckBox1.setText("jCheckBox1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        PANE.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        PANE.setPreferredSize(new java.awt.Dimension(256, 256));
        PANE.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PANEMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout PANELayout = new javax.swing.GroupLayout(PANE);
        PANE.setLayout(PANELayout);
        PANELayout.setHorizontalGroup(
            PANELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 254, Short.MAX_VALUE)
        );
        PANELayout.setVerticalGroup(
            PANELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 254, Short.MAX_VALUE)
        );

        getContentPane().add(PANE);
        PANE.setBounds(10, 10, 256, 256);

        jButton1.setText("Радиальная");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(290, 50, 110, 25);

        KY.setText("0.5");
        getContentPane().add(KY);
        KY.setBounds(470, 10, 50, 25);

        VX.setText("0");
        getContentPane().add(VX);
        VX.setBounds(410, 90, 50, 25);

        KR.setText("0.5");
        getContentPane().add(KR);
        KR.setBounds(410, 50, 50, 25);

        jButton2.setText("Однородная");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2);
        jButton2.setBounds(290, 90, 110, 25);

        jButton3.setText("Линейная XY");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3);
        jButton3.setBounds(290, 10, 110, 25);

        KX.setText("0.5");
        getContentPane().add(KX);
        KX.setBounds(410, 10, 50, 25);

        VY.setText("0");
        getContentPane().add(VY);
        VY.setBounds(470, 90, 50, 25);

        jButton4.setText("Точка");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton4);
        jButton4.setBounds(290, 140, 110, 25);

        X.setText("100");
        getContentPane().add(X);
        X.setBounds(410, 140, 50, 25);

        Y.setText("100");
        getContentPane().add(Y);
        Y.setBounds(470, 140, 50, 25);

        jLabel1.setText("Y");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(480, 170, 20, 14);

        jLabel2.setText("VY");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(480, 120, 20, 14);

        jLabel3.setText("X");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(420, 170, 30, 14);

        jLabel4.setText("VX");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(420, 120, 30, 14);

        jButton5.setText("Сохранить");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton5);
        jButton5.setBounds(410, 230, 110, 25);

        jButton6.setText("Загрузить");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton6);
        jButton6.setBounds(290, 230, 110, 25);

        Mode.setText("1 - мм, 0 - индекс (0..255)");
        getContentPane().add(Mode);
        Mode.setBounds(300, 190, 190, 23);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        onClose();
    }//GEN-LAST:event_formWindowClosing

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            data.setRadialXY(Double.parseDouble(KR.getText()));
            view();
            } catch(Exception ee){ ws().notify(Values.error, "Формат вещественного числа");}

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            data.values(Integer.parseInt(VX.getText()), Integer.parseInt(VY.getText()));
            view();
            } catch(Exception ee){ ws().notify(Values.error, "Формат целого числа");}
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try {
            data.setLinearXY(Double.parseDouble(KX.getText()), Double.parseDouble(KY.getText()));
            view();
            } catch(Exception ee){ ws().notify(Values.error, "Формат вещественного числа");}

    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {
            boolean mode = Mode.isSelected();
            int xx = mode ? (int)(128+Double.parseDouble(X.getText())/(Values.PrinterFieldSize/2)*128)
                    : Integer.parseInt(X.getText());
            int yy = mode ? (int)(128+Double.parseDouble(Y.getText())/(Values.PrinterFieldSize/2)*128)
                    : Integer.parseInt(Y.getText());
            data.value(xx,yy,Integer.parseInt(VX.getText()), Integer.parseInt(VY.getText()));
            view();
            } catch(Exception ee){ ws().notify(Values.error, "Формат числа");}
    }//GEN-LAST:event_jButton4ActionPerformed

    private void PANEMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PANEMouseClicked
        int x = evt.getX();
        int y = 256 - evt.getY();
        boolean mode = Mode.isSelected();
        X.setText(""+(mode ? df.format((x-128)*Values.PrinterFieldSize/2/128) : x));
        Y.setText(""+(mode ? df.format((y-128)*Values.PrinterFieldSize/2/128) : y));
        if (evt.getButton()==1){
            VX.setText(""+data.valueX(x, y));
            VY.setText(""+data.valueY(x, y));
            }
        if (evt.getButton()==3){
            data.value(x,y,Integer.parseInt(VX.getText()),Integer.parseInt(VY.getText()));
            view();
            }
    }//GEN-LAST:event_PANEMouseClicked

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        String ss = getOutputFileName("Коррекция геометрии","bin","Distortion.bin");
        if (ss==null) return;
        try{
            FileOutputStream out = new FileOutputStream(ss);
            data.save(out);
            out.close();
            }catch(Exception ee){ ws().notify(Values.error, ee.getMessage());}
    }//GEN-LAST:event_jButton5ActionPerformed

    @Override
    public void paint(Graphics g){
        super.paint(g);
        view();
        }
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        String ss = getInputFileName("Коррекция геометрии","bin",false);
        if (ss==null) return;
        try{
            FileInputStream out = new FileInputStream(ss);
            data.load(out);
            out.close();
            view();
            }catch(Exception ee){ ws().notify(Values.error, ee.getMessage());}

    }//GEN-LAST:event_jButton6ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DistortionEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DistortionEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DistortionEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DistortionEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DistortionEditor().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField KR;
    private javax.swing.JTextField KX;
    private javax.swing.JTextField KY;
    private javax.swing.JCheckBox Mode;
    private javax.swing.JPanel PANE;
    private javax.swing.JTextField VX;
    private javax.swing.JTextField VY;
    private javax.swing.JTextField X;
    private javax.swing.JTextField Y;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    // End of variables declaration//GEN-END:variables
}
