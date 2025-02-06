/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.view;

import romanow.cnc.Values;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author romanow
 */
public class DigitPanel extends BasePopupDialog {

    /**
     * Creates new form DigitPanel
     */
    private final static int maxDigits=20;
    private char cc[]=new char[maxDigits];
    private int nn=0;
    private int pointIdx=-1;
    private int digCount=0;
    private boolean real=false;
    private boolean onlyPlus=false;
    private int power=0;
    private I_RealValue back=null;
    public DigitPanel(I_RealValue back0) {
        super(280,300);
        initComponents();
        back = back0;
        positionOn(200,200);
        retryLongDelay();
        }
    public DigitPanel(Dimension dim, String title, double val,I_RealValue back0) {
        super(dim, 250,280);
        initComponents();
        back = back0;
        TITLE.setText(title);
        setValue(val);
        BasePanel.setComponentsScale(this,dim);
        revalidate();
        positionOn(200,200);
        retryLongDelay();
        }
    public DigitPanel(Dimension dim, String title, String val,I_RealValue back0) {
        super(dim, 250,280);
        initComponents();
        back = back0;
        TITLE.setText(title);
        setValue(val);
        BasePanel.setComponentsScale(this,dim);
        revalidate();
        positionOn(200,200);
        retryLongDelay();
        }
    public DigitPanel(Dimension dim, String title, JTextField field, I_RealValue back0) {
        this(dim,title,field,false,back0);
        }
    public DigitPanel(Dimension dim, String title, JTextField field, boolean intMode, I_RealValue back0) {
        super(dim, 250,280);
        initComponents();
        back = back0;
        TITLE.setText(title);
        setValue(field.getText());
        BasePanel.setComponentsScale(this,dim);
        positionOn(field,dim, 100,100, true);         // Уже пересчитан масштаб !!!!!!!!!!!!!!!
        retryLongDelay();
        if (intMode)
            Point.setVisible(false);
        revalidate();
        setVisible(true);
        }
    private void showString(){
        Value.setText(new String(cc,0,nn));
        retryLongDelay();
        }
    private void procDigit(char digit){
        if (nn==maxDigits)
            return;
        if (pointIdx!=-1)
            digCount++;
        cc[nn++]=digit;
        showString();
        }
    public void setValue(double value){
        char ss[] = (""+value).toCharArray();
        nn=ss.length;
        for(int i=0;i<nn;i++)
            cc[i] = ss[i];
        showString();
        }
    public void setValue(String value){
        char ss[] = value.toCharArray();
        nn=ss.length;
        for(int i=0;i<nn;i++)
            cc[i] = ss[i];
        showString();
    }
    public void setNoFloat(){
        Point.setVisible(false);
        }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Value = new javax.swing.JTextField();
        B9 = new javax.swing.JButton();
        Back = new javax.swing.JButton();
        B8 = new javax.swing.JButton();
        B7 = new javax.swing.JButton();
        B4 = new javax.swing.JButton();
        B5 = new javax.swing.JButton();
        B6 = new javax.swing.JButton();
        B1 = new javax.swing.JButton();
        B2 = new javax.swing.JButton();
        B3 = new javax.swing.JButton();
        Minus = new javax.swing.JButton();
        Point = new javax.swing.JButton();
        OK = new javax.swing.JButton();
        B10 = new javax.swing.JButton();
        Canсel = new javax.swing.JButton();
        TITLE = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        Value.setFont(new java.awt.Font("Segoe UI Symbol", 1, 18)); // NOI18N
        getContentPane().add(Value);
        Value.setBounds(10, 40, 230, 30);

        B9.setFont(new java.awt.Font("Segoe UI Symbol", 1, 24)); // NOI18N
        B9.setText("9");
        B9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B9ActionPerformed(evt);
            }
        });
        getContentPane().add(B9);
        B9.setBounds(130, 80, 50, 40);

        Back.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        Back.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/left.PNG"))); // NOI18N
        Back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BackActionPerformed(evt);
            }
        });
        getContentPane().add(Back);
        Back.setBounds(190, 150, 50, 50);

        B8.setFont(new java.awt.Font("Segoe UI Symbol", 1, 24)); // NOI18N
        B8.setText("8");
        B8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B8ActionPerformed(evt);
            }
        });
        getContentPane().add(B8);
        B8.setBounds(70, 80, 50, 40);

        B7.setFont(new java.awt.Font("Segoe UI Symbol", 1, 24)); // NOI18N
        B7.setText("7");
        B7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B7ActionPerformed(evt);
            }
        });
        getContentPane().add(B7);
        B7.setBounds(10, 80, 50, 40);

        B4.setFont(new java.awt.Font("Segoe UI Symbol", 1, 24)); // NOI18N
        B4.setText("4");
        B4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B4ActionPerformed(evt);
            }
        });
        getContentPane().add(B4);
        B4.setBounds(10, 130, 50, 40);

        B5.setFont(new java.awt.Font("Segoe UI Symbol", 1, 24)); // NOI18N
        B5.setText("5");
        B5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B5ActionPerformed(evt);
            }
        });
        getContentPane().add(B5);
        B5.setBounds(70, 130, 50, 40);

        B6.setFont(new java.awt.Font("Segoe UI Symbol", 1, 24)); // NOI18N
        B6.setText("6");
        B6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B6ActionPerformed(evt);
            }
        });
        getContentPane().add(B6);
        B6.setBounds(130, 130, 50, 40);

        B1.setFont(new java.awt.Font("Segoe UI Symbol", 1, 24)); // NOI18N
        B1.setText("1");
        B1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B1ActionPerformed(evt);
            }
        });
        getContentPane().add(B1);
        B1.setBounds(10, 180, 50, 40);

        B2.setFont(new java.awt.Font("Segoe UI Symbol", 1, 24)); // NOI18N
        B2.setText("2");
        B2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B2ActionPerformed(evt);
            }
        });
        getContentPane().add(B2);
        B2.setBounds(70, 180, 50, 40);

        B3.setFont(new java.awt.Font("Segoe UI Symbol", 1, 24)); // NOI18N
        B3.setText("0");
        B3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B3ActionPerformed(evt);
            }
        });
        getContentPane().add(B3);
        B3.setBounds(130, 230, 50, 40);

        Minus.setFont(new java.awt.Font("Segoe UI Symbol", 1, 24)); // NOI18N
        Minus.setText("-");
        Minus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MinusActionPerformed(evt);
            }
        });
        getContentPane().add(Minus);
        Minus.setBounds(10, 230, 50, 40);

        Point.setFont(new java.awt.Font("Segoe UI Symbol", 1, 24)); // NOI18N
        Point.setText(".");
        Point.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PointActionPerformed(evt);
            }
        });
        getContentPane().add(Point);
        Point.setBounds(70, 230, 50, 40);

        OK.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        OK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/refresh.png"))); // NOI18N
        OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKActionPerformed(evt);
            }
        });
        getContentPane().add(OK);
        OK.setBounds(190, 80, 50, 50);

        B10.setFont(new java.awt.Font("Segoe UI Symbol", 1, 24)); // NOI18N
        B10.setText("3");
        B10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B10ActionPerformed(evt);
            }
        });
        getContentPane().add(B10);
        B10.setBounds(130, 180, 50, 40);

        Canсel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/remove.png"))); // NOI18N
        Canсel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CanсelActionPerformed(evt);
            }
        });
        getContentPane().add(Canсel);
        Canсel.setBounds(190, 220, 50, 50);

        TITLE.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        getContentPane().add(TITLE);
        TITLE.setBounds(10, 6, 230, 30);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void B9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B9ActionPerformed
        procDigit('9');
    }//GEN-LAST:event_B9ActionPerformed

    private void BackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BackActionPerformed
        if (nn==0)
            return;
        nn--;
        if (cc[nn]=='.'){
            pointIdx=-1;
            }
        showString();
    }//GEN-LAST:event_BackActionPerformed

    private void B8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B8ActionPerformed
        procDigit('8');
    }//GEN-LAST:event_B8ActionPerformed

    private void B7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B7ActionPerformed
        procDigit('7');
    }//GEN-LAST:event_B7ActionPerformed

    private void B4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B4ActionPerformed
        procDigit('4');
    }//GEN-LAST:event_B4ActionPerformed

    private void B5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B5ActionPerformed
        procDigit('5');
    }//GEN-LAST:event_B5ActionPerformed

    private void B6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B6ActionPerformed
        procDigit('6');
    }//GEN-LAST:event_B6ActionPerformed

    private void B1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B1ActionPerformed
        procDigit('1');
    }//GEN-LAST:event_B1ActionPerformed

    private void B2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B2ActionPerformed
        procDigit('2');
    }//GEN-LAST:event_B2ActionPerformed

    private void B3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B3ActionPerformed
        procDigit('0');
    }//GEN-LAST:event_B3ActionPerformed

    private void MinusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MinusActionPerformed
        if (nn!=0)
            return;
        cc[nn++]='-';
        showString();
    }//GEN-LAST:event_MinusActionPerformed

    private void PointActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PointActionPerformed
        if (pointIdx!=-1)
            return;
        pointIdx=nn;
        cc[nn++]='.';
        digCount=0;
        showString();
    }//GEN-LAST:event_PointActionPerformed

    private void OKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKActionPerformed
        if (nn!=0){
            String ss = new String(cc,0,nn);
            try {
                if (back!=null)
                    back.onEvent(ss);
                } catch (Exception ee){}
            }
        closeView();
    }//GEN-LAST:event_OKActionPerformed

    private void B10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B10ActionPerformed
        procDigit('3');
    }//GEN-LAST:event_B10ActionPerformed

    private void CanсelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CanсelActionPerformed
        closeView();
    }//GEN-LAST:event_CanсelActionPerformed

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
            java.util.logging.Logger.getLogger(DigitPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DigitPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DigitPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DigitPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DigitPanel(new I_RealValue() {
                    @Override
                    public void onEvent(String value) {
                        System.out.println(value);
                    }
                }).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton B1;
    private javax.swing.JButton B10;
    private javax.swing.JButton B2;
    private javax.swing.JButton B3;
    private javax.swing.JButton B4;
    private javax.swing.JButton B5;
    private javax.swing.JButton B6;
    private javax.swing.JButton B7;
    private javax.swing.JButton B8;
    private javax.swing.JButton B9;
    private javax.swing.JButton Back;
    private javax.swing.JButton Canсel;
    private javax.swing.JButton Minus;
    private javax.swing.JButton OK;
    private javax.swing.JButton Point;
    private javax.swing.JLabel TITLE;
    private javax.swing.JTextField Value;
    // End of variables declaration//GEN-END:variables
}
