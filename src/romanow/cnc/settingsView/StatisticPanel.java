/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.settingsView;

import romanow.cnc.slicer.SliceRezult;

/**
 *
 * @author romanow
 */
public class StatisticPanel extends javax.swing.JPanel {

    /**
     * Creates new form StatisticPanel
     */
    public void setValues(SliceRezult statistic){
        LineCount.setText(""+statistic.lineCount());
        LineLength.setText(""+statistic.printLength());
        MoveProc.setText(""+statistic.moveProc());
        PrintTime.setText(""+statistic.printTime());
        SliceTime.setText(""+statistic.sliceTime());        
        }
    public StatisticPanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel10 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        PrintTime = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        LineCount = new javax.swing.JTextField();
        LineLength = new javax.swing.JTextField();
        MoveProc = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        SliceTime = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();

        setLayout(null);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel10.setText("Статистика");
        add(jLabel10);
        jLabel10.setBounds(10, 0, 140, 20);

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel16.setText("фрезер.");
        add(jLabel16);
        jLabel16.setBounds(10, 210, 100, 20);

        jLabel25.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel25.setText("Линий");
        add(jLabel25);
        jLabel25.setBounds(10, 40, 80, 20);

        PrintTime.setEditable(false);
        PrintTime.setBackground(new java.awt.Color(200, 200, 200));
        PrintTime.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        add(PrintTime);
        PrintTime.setBounds(100, 190, 90, 35);

        jLabel26.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel26.setText("Длина  (м)");
        add(jLabel26);
        jLabel26.setBounds(10, 75, 90, 25);

        LineCount.setEditable(false);
        LineCount.setBackground(new java.awt.Color(200, 200, 200));
        LineCount.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        add(LineCount);
        LineCount.setBounds(100, 30, 90, 35);

        LineLength.setEditable(false);
        LineLength.setBackground(new java.awt.Color(200, 200, 200));
        LineLength.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        add(LineLength);
        LineLength.setBounds(100, 70, 90, 35);

        MoveProc.setEditable(false);
        MoveProc.setBackground(new java.awt.Color(200, 200, 200));
        MoveProc.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        add(MoveProc);
        MoveProc.setBounds(100, 110, 90, 35);

        jLabel27.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel27.setText("ход (%)");
        add(jLabel27);
        jLabel27.setBounds(10, 130, 80, 20);

        jLabel28.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel28.setText("слайсинга");
        add(jLabel28);
        jLabel28.setBounds(10, 170, 90, 20);

        SliceTime.setEditable(false);
        SliceTime.setBackground(new java.awt.Color(200, 200, 200));
        SliceTime.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        add(SliceTime);
        SliceTime.setBounds(100, 150, 90, 35);

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel17.setText("Время");
        add(jLabel17);
        jLabel17.setBounds(10, 190, 100, 20);

        jLabel29.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel29.setText("Время ");
        add(jLabel29);
        jLabel29.setBounds(10, 150, 90, 20);

        jLabel30.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel30.setText("Холостой");
        add(jLabel30);
        jLabel30.setBounds(10, 110, 90, 20);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField LineCount;
    private javax.swing.JTextField LineLength;
    private javax.swing.JTextField MoveProc;
    private javax.swing.JTextField PrintTime;
    private javax.swing.JTextField SliceTime;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    // End of variables declaration//GEN-END:variables
}
