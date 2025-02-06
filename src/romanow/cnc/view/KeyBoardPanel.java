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
public class KeyBoardPanel extends BasePopupDialog {

    /**
     * Creates new form DigitPanel
     */
    private final static int maxDigits=80;
    private char cc[]=new char[maxDigits];
    private int nn=0;
    private int pointIdx=-1;
    private boolean pass=false;
    private I_RealValue back=null;
    private boolean shift=false;
    private JTextField field;
    //public KeyBoardPanel(boolean pass0, JTextField field0, I_RealValue back0) {
    //    this(new Dimension(),"!!!!!!!!!!!!!!!!!!",pass0,field0,back0);
    //    }
    public KeyBoardPanel(Dimension dim, String title, JTextField field0 , boolean pass0, I_RealValue back0) {
        super(dim,670,250);
        initComponents();
        TITLE.setText(title);
        field = field0;
        char zz[] = field.getText().toCharArray();
        nn=zz.length;
        for(int i=0;i<nn;i++)
            cc[i]=zz[i];
        showString();
        pass = pass0;
        back = back0;
        Shift.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/down.png"))); // NOI18N
        Value.setText(field.getText());
        BasePanel.setComponentsScale(this,dim);
        positionOn(field,dim, -200,100, true);         // Уже пересчитан масштаб !!!!!!!!!!!!!!!
        retryLongDelay();
        revalidate();
        setVisible(true);
        retryLongDelay();
        }
    private void showString(){
        if (pass){
            char zz[] = new char[nn*2];
            for(int i=0;i<nn;i+=2){
                zz[i]='.';
                zz[i+1]=' ';
                }
            String cc= new String(zz);
            Value.setText(cc);
            if (field!=null)
                field.setText(cc);
            }
        else{
            String bb = new String(cc,0,nn);
            Value.setText(bb);
            if (field!=null)
                field.setText(bb);
            }
        retryLongDelay();
        }
    private void procDigit(char digit){
        if (nn==maxDigits)
            return;
        if (!shift && digit>='A' && digit<='Z')
            digit = (char) (digit -'A'+'a');
        cc[nn++]=digit;
        showString();
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
        Shift = new javax.swing.JButton();
        Point = new javax.swing.JButton();
        OK = new javax.swing.JButton();
        B10 = new javax.swing.JButton();
        Canсel = new javax.swing.JButton();
        B11 = new javax.swing.JButton();
        B12 = new javax.swing.JButton();
        B13 = new javax.swing.JButton();
        B14 = new javax.swing.JButton();
        B15 = new javax.swing.JButton();
        B16 = new javax.swing.JButton();
        B17 = new javax.swing.JButton();
        B18 = new javax.swing.JButton();
        B19 = new javax.swing.JButton();
        B20 = new javax.swing.JButton();
        B22 = new javax.swing.JButton();
        B23 = new javax.swing.JButton();
        B24 = new javax.swing.JButton();
        B25 = new javax.swing.JButton();
        B26 = new javax.swing.JButton();
        B27 = new javax.swing.JButton();
        B28 = new javax.swing.JButton();
        B29 = new javax.swing.JButton();
        B30 = new javax.swing.JButton();
        B31 = new javax.swing.JButton();
        B32 = new javax.swing.JButton();
        B33 = new javax.swing.JButton();
        B34 = new javax.swing.JButton();
        B35 = new javax.swing.JButton();
        B36 = new javax.swing.JButton();
        B37 = new javax.swing.JButton();
        B39 = new javax.swing.JButton();
        TITLE = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        Value.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        getContentPane().add(Value);
        Value.setBounds(260, 10, 400, 30);

        B9.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B9.setText("9");
        B9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B9ActionPerformed(evt);
            }
        });
        getContentPane().add(B9);
        B9.setBounds(490, 50, 50, 40);

        Back.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        Back.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/left.PNG"))); // NOI18N
        Back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BackActionPerformed(evt);
            }
        });
        getContentPane().add(Back);
        Back.setBounds(610, 110, 50, 50);

        B8.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B8.setText("8");
        B8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B8ActionPerformed(evt);
            }
        });
        getContentPane().add(B8);
        B8.setBounds(430, 50, 50, 40);

        B7.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B7.setText("7");
        B7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B7ActionPerformed(evt);
            }
        });
        getContentPane().add(B7);
        B7.setBounds(370, 50, 50, 40);

        B4.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B4.setText("4");
        B4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B4ActionPerformed(evt);
            }
        });
        getContentPane().add(B4);
        B4.setBounds(190, 50, 50, 40);

        B5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B5.setText("5");
        B5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B5ActionPerformed(evt);
            }
        });
        getContentPane().add(B5);
        B5.setBounds(250, 50, 50, 40);

        B6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B6.setText("6");
        B6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B6ActionPerformed(evt);
            }
        });
        getContentPane().add(B6);
        B6.setBounds(310, 50, 50, 40);

        B1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B1.setText("1");
        B1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B1ActionPerformed(evt);
            }
        });
        getContentPane().add(B1);
        B1.setBounds(10, 50, 50, 40);

        B2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B2.setText("2");
        B2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B2ActionPerformed(evt);
            }
        });
        getContentPane().add(B2);
        B2.setBounds(70, 50, 50, 40);

        B3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B3.setText("0");
        B3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B3ActionPerformed(evt);
            }
        });
        getContentPane().add(B3);
        B3.setBounds(550, 50, 50, 40);

        Shift.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        Shift.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShiftActionPerformed(evt);
            }
        });
        getContentPane().add(Shift);
        Shift.setBounds(10, 200, 50, 40);

        Point.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        Point.setText(".");
        Point.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PointActionPerformed(evt);
            }
        });
        getContentPane().add(Point);
        Point.setBounds(550, 200, 50, 40);

        OK.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        OK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/refresh.png"))); // NOI18N
        OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKActionPerformed(evt);
            }
        });
        getContentPane().add(OK);
        OK.setBounds(610, 50, 50, 50);

        B10.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B10.setText("3");
        B10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B10ActionPerformed(evt);
            }
        });
        getContentPane().add(B10);
        B10.setBounds(130, 50, 50, 40);

        Canсel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/remove.png"))); // NOI18N
        Canсel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CanсelActionPerformed(evt);
            }
        });
        getContentPane().add(Canсel);
        Canсel.setBounds(610, 190, 50, 50);

        B11.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B11.setText("P");
        B11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B11ActionPerformed(evt);
            }
        });
        getContentPane().add(B11);
        B11.setBounds(550, 100, 50, 40);

        B12.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B12.setText("Q");
        B12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B12ActionPerformed(evt);
            }
        });
        getContentPane().add(B12);
        B12.setBounds(10, 100, 50, 40);

        B13.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B13.setText("W");
        B13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B13ActionPerformed(evt);
            }
        });
        getContentPane().add(B13);
        B13.setBounds(70, 100, 50, 40);

        B14.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B14.setText("E");
        B14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B14ActionPerformed(evt);
            }
        });
        getContentPane().add(B14);
        B14.setBounds(130, 100, 50, 40);

        B15.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B15.setText("R");
        B15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B15ActionPerformed(evt);
            }
        });
        getContentPane().add(B15);
        B15.setBounds(190, 100, 50, 40);

        B16.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B16.setText("T");
        B16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B16ActionPerformed(evt);
            }
        });
        getContentPane().add(B16);
        B16.setBounds(250, 100, 50, 40);

        B17.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B17.setText("Y");
        B17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B17ActionPerformed(evt);
            }
        });
        getContentPane().add(B17);
        B17.setBounds(310, 100, 50, 40);

        B18.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B18.setText("U");
        B18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B18ActionPerformed(evt);
            }
        });
        getContentPane().add(B18);
        B18.setBounds(370, 100, 50, 40);

        B19.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B19.setText("I");
        B19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B19ActionPerformed(evt);
            }
        });
        getContentPane().add(B19);
        B19.setBounds(430, 100, 50, 40);

        B20.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B20.setText("O");
        B20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B20ActionPerformed(evt);
            }
        });
        getContentPane().add(B20);
        B20.setBounds(490, 100, 50, 40);

        B22.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B22.setText("A");
        B22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B22ActionPerformed(evt);
            }
        });
        getContentPane().add(B22);
        B22.setBounds(40, 150, 50, 40);

        B23.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B23.setText("S");
        B23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B23ActionPerformed(evt);
            }
        });
        getContentPane().add(B23);
        B23.setBounds(100, 150, 50, 40);

        B24.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B24.setText("D");
        B24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B24ActionPerformed(evt);
            }
        });
        getContentPane().add(B24);
        B24.setBounds(160, 150, 50, 40);

        B25.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B25.setText("F");
        B25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B25ActionPerformed(evt);
            }
        });
        getContentPane().add(B25);
        B25.setBounds(220, 150, 50, 40);

        B26.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B26.setText("G");
        B26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B26ActionPerformed(evt);
            }
        });
        getContentPane().add(B26);
        B26.setBounds(280, 150, 50, 40);

        B27.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B27.setText("H");
        B27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B27ActionPerformed(evt);
            }
        });
        getContentPane().add(B27);
        B27.setBounds(340, 150, 50, 40);

        B28.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B28.setText("J");
        B28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B28ActionPerformed(evt);
            }
        });
        getContentPane().add(B28);
        B28.setBounds(400, 150, 50, 40);

        B29.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B29.setText("K");
        B29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B29ActionPerformed(evt);
            }
        });
        getContentPane().add(B29);
        B29.setBounds(460, 150, 50, 40);

        B30.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B30.setText("L");
        B30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B30ActionPerformed(evt);
            }
        });
        getContentPane().add(B30);
        B30.setBounds(520, 150, 50, 40);

        B31.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B31.setText("Z");
        B31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B31ActionPerformed(evt);
            }
        });
        getContentPane().add(B31);
        B31.setBounds(70, 200, 50, 40);

        B32.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B32.setText("X");
        B32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B32ActionPerformed(evt);
            }
        });
        getContentPane().add(B32);
        B32.setBounds(130, 200, 50, 40);

        B33.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B33.setText("C");
        B33.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B33ActionPerformed(evt);
            }
        });
        getContentPane().add(B33);
        B33.setBounds(190, 200, 50, 40);

        B34.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B34.setText("V");
        B34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B34ActionPerformed(evt);
            }
        });
        getContentPane().add(B34);
        B34.setBounds(250, 200, 50, 40);

        B35.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        B35.setText("B");
        B35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B35ActionPerformed(evt);
            }
        });
        getContentPane().add(B35);
        B35.setBounds(310, 200, 50, 40);

        B36.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B36.setText("N");
        B36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B36ActionPerformed(evt);
            }
        });
        getContentPane().add(B36);
        B36.setBounds(370, 200, 50, 40);

        B37.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B37.setText("M");
        B37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B37ActionPerformed(evt);
            }
        });
        getContentPane().add(B37);
        B37.setBounds(430, 200, 50, 40);

        B39.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        B39.setText(",");
        B39.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B39ActionPerformed(evt);
            }
        });
        getContentPane().add(B39);
        B39.setBounds(490, 200, 50, 40);

        TITLE.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        getContentPane().add(TITLE);
        TITLE.setBounds(20, 10, 230, 30);

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

    private void ShiftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShiftActionPerformed
        shift =!shift;
        Shift.setIcon(new javax.swing.ImageIcon(getClass().getResource(shift ? "/drawable-mdpi/up.png" : "/drawable-mdpi/down.png"))); // NOI18N
    }//GEN-LAST:event_ShiftActionPerformed

    private void PointActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PointActionPerformed
        procDigit('.');
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

    private void B11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B11ActionPerformed
        procDigit('P');
    }//GEN-LAST:event_B11ActionPerformed

    private void B12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B12ActionPerformed
        procDigit('Q');
    }//GEN-LAST:event_B12ActionPerformed

    private void B13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B13ActionPerformed
        procDigit('W');
    }//GEN-LAST:event_B13ActionPerformed

    private void B14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B14ActionPerformed
        procDigit('E');
    }//GEN-LAST:event_B14ActionPerformed

    private void B15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B15ActionPerformed
        procDigit('R');
    }//GEN-LAST:event_B15ActionPerformed

    private void B16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B16ActionPerformed
        procDigit('T');
    }//GEN-LAST:event_B16ActionPerformed

    private void B17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B17ActionPerformed
        procDigit('Y');
    }//GEN-LAST:event_B17ActionPerformed

    private void B18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B18ActionPerformed
        procDigit('U');
    }//GEN-LAST:event_B18ActionPerformed

    private void B19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B19ActionPerformed
        procDigit('I');
    }//GEN-LAST:event_B19ActionPerformed

    private void B20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B20ActionPerformed
        procDigit('O');
    }//GEN-LAST:event_B20ActionPerformed

    private void B22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B22ActionPerformed
        procDigit('A');
    }//GEN-LAST:event_B22ActionPerformed

    private void B23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B23ActionPerformed
        procDigit('S');
    }//GEN-LAST:event_B23ActionPerformed

    private void B24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B24ActionPerformed
        procDigit('D');
    }//GEN-LAST:event_B24ActionPerformed

    private void B25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B25ActionPerformed
        procDigit('F');
    }//GEN-LAST:event_B25ActionPerformed

    private void B26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B26ActionPerformed
        procDigit('G');
    }//GEN-LAST:event_B26ActionPerformed

    private void B27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B27ActionPerformed
        procDigit('H');
    }//GEN-LAST:event_B27ActionPerformed

    private void B28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B28ActionPerformed
        procDigit('J');
    }//GEN-LAST:event_B28ActionPerformed

    private void B29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B29ActionPerformed
        procDigit('K');
    }//GEN-LAST:event_B29ActionPerformed

    private void B30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B30ActionPerformed
        procDigit('L');
    }//GEN-LAST:event_B30ActionPerformed

    private void B31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B31ActionPerformed
        procDigit('Z');
    }//GEN-LAST:event_B31ActionPerformed

    private void B32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B32ActionPerformed
        procDigit('X');
    }//GEN-LAST:event_B32ActionPerformed

    private void B33ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B33ActionPerformed
        procDigit('C');
    }//GEN-LAST:event_B33ActionPerformed

    private void B34ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B34ActionPerformed
        procDigit('V');
    }//GEN-LAST:event_B34ActionPerformed

    private void B35ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B35ActionPerformed
        procDigit('B');
    }//GEN-LAST:event_B35ActionPerformed

    private void B36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B36ActionPerformed
        procDigit('N');
    }//GEN-LAST:event_B36ActionPerformed

    private void B37ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B37ActionPerformed
        procDigit('M');
    }//GEN-LAST:event_B37ActionPerformed

    private void B39ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B39ActionPerformed
        procDigit(',');
    }//GEN-LAST:event_B39ActionPerformed

    /**
     * @param args the command line arguments
     */
    /*
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(KeyBoardPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(KeyBoardPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(KeyBoardPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(KeyBoardPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new KeyBoardPanel(false,null,"aaaaaa",new I_RealValue() {
                    @Override
                    public void onEvent(String value) {
                        System.out.println(value);
                        }
                    }).setVisible(true);
                }
            });
        }
        */


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton B1;
    private javax.swing.JButton B10;
    private javax.swing.JButton B11;
    private javax.swing.JButton B12;
    private javax.swing.JButton B13;
    private javax.swing.JButton B14;
    private javax.swing.JButton B15;
    private javax.swing.JButton B16;
    private javax.swing.JButton B17;
    private javax.swing.JButton B18;
    private javax.swing.JButton B19;
    private javax.swing.JButton B2;
    private javax.swing.JButton B20;
    private javax.swing.JButton B22;
    private javax.swing.JButton B23;
    private javax.swing.JButton B24;
    private javax.swing.JButton B25;
    private javax.swing.JButton B26;
    private javax.swing.JButton B27;
    private javax.swing.JButton B28;
    private javax.swing.JButton B29;
    private javax.swing.JButton B3;
    private javax.swing.JButton B30;
    private javax.swing.JButton B31;
    private javax.swing.JButton B32;
    private javax.swing.JButton B33;
    private javax.swing.JButton B34;
    private javax.swing.JButton B35;
    private javax.swing.JButton B36;
    private javax.swing.JButton B37;
    private javax.swing.JButton B39;
    private javax.swing.JButton B4;
    private javax.swing.JButton B5;
    private javax.swing.JButton B6;
    private javax.swing.JButton B7;
    private javax.swing.JButton B8;
    private javax.swing.JButton B9;
    private javax.swing.JButton Back;
    private javax.swing.JButton Canсel;
    private javax.swing.JButton OK;
    private javax.swing.JButton Point;
    private javax.swing.JButton Shift;
    private javax.swing.JLabel TITLE;
    private javax.swing.JTextField Value;
    // End of variables declaration//GEN-END:variables
}
