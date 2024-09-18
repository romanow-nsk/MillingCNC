/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epos.slm3d.settingsView;

import epos.slm3d.utils.Events;
import epos.slm3d.settings.Settings;
import epos.slm3d.settings.WorkSpace;
import epos.slm3d.slicer.SliceData;
import epos.slm3d.stl.MyAngle;
import epos.slm3d.utils.I_Notify;
import epos.slm3d.utils.Values;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 *
 * @author romanow
 */
public class ModelSettingsPanel extends javax.swing.JPanel  implements I_SettingsPanel{

    /**
     * Creates new form M3SSettings
     */
    private I_Notify notify;
    public ModelSettingsPanel(I_Notify notify0) {
        initComponents();
        notify = notify0;
        this.setBounds(0,0, 800, 650);
        loadSettings();
        }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        MarkingFieldWidth = new javax.swing.JTextField();
        Z = new javax.swing.JTextField();
        PageServoOffsetsLeft = new javax.swing.JTextField();
        PageServoOffsetsTop = new javax.swing.JTextField();
        ScaleFactor = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        RotateButton = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        MarkingFieldHight = new javax.swing.JTextField();
        ANGLE = new javax.swing.JTextField();
        Zstart = new javax.swing.JTextField();
        Z0_2 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator5 = new javax.swing.JSeparator();
        Zfinish = new javax.swing.JTextField();
        Z0_1 = new javax.swing.JLabel();
        XYZ = new javax.swing.JComboBox<>();
        jLabel25 = new javax.swing.JLabel();
        PrintTime = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        LineCount = new javax.swing.JTextField();
        LineLength = new javax.swing.JTextField();
        MoveProc = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        SliceTime = new javax.swing.JTextField();
        ShiftButton = new javax.swing.JButton();
        SHIFT = new javax.swing.JTextField();
        XYZShift = new javax.swing.JComboBox<>();

        setLayout(null);
        add(jLabel1);
        jLabel1.setBounds(30, 20, 34, 0);

        MarkingFieldWidth.setEditable(false);
        MarkingFieldWidth.setBackground(new java.awt.Color(200, 200, 200));
        add(MarkingFieldWidth);
        MarkingFieldWidth.setBounds(160, 30, 80, 25);

        Z.setEditable(false);
        Z.setBackground(new java.awt.Color(200, 200, 200));
        add(Z);
        Z.setBounds(160, 150, 80, 25);

        PageServoOffsetsLeft.setEditable(false);
        PageServoOffsetsLeft.setBackground(new java.awt.Color(200, 200, 200));
        PageServoOffsetsLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PageServoOffsetsLeftActionPerformed(evt);
            }
        });
        add(PageServoOffsetsLeft);
        PageServoOffsetsLeft.setBounds(160, 90, 80, 25);

        PageServoOffsetsTop.setEditable(false);
        PageServoOffsetsTop.setBackground(new java.awt.Color(200, 200, 200));
        add(PageServoOffsetsTop);
        PageServoOffsetsTop.setBounds(160, 120, 80, 25);

        ScaleFactor.setEditable(false);
        ScaleFactor.setBackground(new java.awt.Color(200, 200, 200));
        add(ScaleFactor);
        ScaleFactor.setBounds(160, 180, 80, 25);

        jLabel8.setText("Масштаб модели");
        add(jLabel8);
        jLabel8.setBounds(10, 190, 110, 14);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Статистика");
        add(jLabel10);
        jLabel10.setBounds(270, 10, 90, 20);

        jLabel11.setText("Размер по X (мм)");
        add(jLabel11);
        jLabel11.setBounds(10, 40, 110, 20);

        jLabel12.setText("Размер по Z (мм)");
        add(jLabel12);
        jLabel12.setBounds(10, 160, 110, 14);

        jLabel14.setText("Смещение X влево (мм)");
        add(jLabel14);
        jLabel14.setBounds(10, 100, 140, 14);

        jLabel15.setText("Смещение  Y вверх (мм)");
        add(jLabel15);
        jLabel15.setBounds(10, 130, 140, 14);

        jLabel16.setText("Время печати");
        add(jLabel16);
        jLabel16.setBounds(270, 160, 100, 14);

        RotateButton.setText("Поворот");
        RotateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RotateButtonActionPerformed(evt);
            }
        });
        add(RotateButton);
        RotateButton.setBounds(380, 210, 100, 23);

        jLabel22.setText("Размер по Y (мм)");
        add(jLabel22);
        jLabel22.setBounds(10, 70, 110, 14);

        MarkingFieldHight.setEditable(false);
        MarkingFieldHight.setBackground(new java.awt.Color(200, 200, 200));
        add(MarkingFieldHight);
        MarkingFieldHight.setBounds(160, 60, 80, 25);

        ANGLE.setText("90");
        ANGLE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ANGLEActionPerformed(evt);
            }
        });
        add(ANGLE);
        ANGLE.setBounds(330, 210, 40, 25);

        Zstart.setText("0");
        add(Zstart);
        Zstart.setBounds(160, 210, 80, 25);

        Z0_2.setText("Z конечное (мм) ");
        add(Z0_2);
        Z0_2.setBounds(10, 250, 120, 14);

        jLabel46.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel46.setText("Модель");
        add(jLabel46);
        jLabel46.setBounds(10, 10, 80, 20);
        add(jSeparator2);
        jSeparator2.setBounds(580, 152, 180, 0);
        add(jSeparator5);
        jSeparator5.setBounds(770, 330, 0, 2);

        Zfinish.setText("0");
        Zfinish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ZfinishActionPerformed(evt);
            }
        });
        add(Zfinish);
        Zfinish.setBounds(160, 240, 80, 25);

        Z0_1.setText("Z начальное (мм) ");
        add(Z0_1);
        Z0_1.setBounds(10, 220, 120, 14);

        XYZ.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "X", "Y", "Z" }));
        add(XYZ);
        XYZ.setBounds(270, 210, 50, 25);

        jLabel25.setText("Линий");
        add(jLabel25);
        jLabel25.setBounds(270, 40, 80, 14);

        PrintTime.setEditable(false);
        PrintTime.setBackground(new java.awt.Color(200, 200, 200));
        add(PrintTime);
        PrintTime.setBounds(380, 150, 80, 25);

        jLabel26.setText("Длина  (м)");
        add(jLabel26);
        jLabel26.setBounds(270, 70, 80, 14);

        LineCount.setEditable(false);
        LineCount.setBackground(new java.awt.Color(200, 200, 200));
        add(LineCount);
        LineCount.setBounds(380, 30, 80, 25);

        LineLength.setEditable(false);
        LineLength.setBackground(new java.awt.Color(200, 200, 200));
        add(LineLength);
        LineLength.setBounds(380, 60, 80, 25);

        MoveProc.setEditable(false);
        MoveProc.setBackground(new java.awt.Color(200, 200, 200));
        add(MoveProc);
        MoveProc.setBounds(380, 90, 80, 25);

        jLabel27.setText("Холостой ход (%)");
        add(jLabel27);
        jLabel27.setBounds(270, 100, 100, 14);

        jLabel28.setText("Время слайсинга");
        add(jLabel28);
        jLabel28.setBounds(270, 130, 100, 14);

        SliceTime.setEditable(false);
        SliceTime.setBackground(new java.awt.Color(200, 200, 200));
        add(SliceTime);
        SliceTime.setBounds(380, 120, 80, 25);

        ShiftButton.setText("Сдвиг");
        ShiftButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShiftButtonActionPerformed(evt);
            }
        });
        add(ShiftButton);
        ShiftButton.setBounds(380, 240, 100, 23);

        SHIFT.setText("10.0");
        SHIFT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SHIFTActionPerformed(evt);
            }
        });
        add(SHIFT);
        SHIFT.setBounds(330, 240, 40, 25);

        XYZShift.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "X", "Y", "Z" }));
        add(XYZShift);
        XYZShift.setBounds(270, 240, 50, 25);
    }// </editor-fold>//GEN-END:initComponents
    public boolean loadSettings(){
        try {
            Settings set = WorkSpace.ws().local();
            set.setNotNull();
            DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
            DecimalFormat df = new DecimalFormat("0.000", dfs);
            DecimalFormat df2 = new DecimalFormat("0.00", dfs);
            DecimalFormat df3 = new DecimalFormat("000.0", dfs);
            MarkingFieldHight.setText(df2.format(set.local.MarkingFieldHight.getVal()));
            MarkingFieldWidth.setText(df2.format(set.local.MarkingFieldWidth.getVal()));
            Z.setText(df2.format(set.local.Z.getVal()));
            Zstart.setText(df2.format(set.local.ZStart.getVal()));
            Zfinish.setText(df2.format(set.local.ZFinish.getVal())); 
            PageServoOffsetsLeft.setText(df2.format(set.local.PageServoOffsetsLeft.getVal()));
            PageServoOffsetsTop.setText(df2.format(set.local.PageServoOffsetsTop.getVal()));
            ScaleFactor.setText(df.format(set.global.ScaleFactor.getVal()));
            LineCount.setText(""+set.statistic.LineCount.getVal());
            LineLength.setText(set.statistic.printLength());
            MoveProc.setText(""+set.statistic.moveProc());
            PrintTime.setText(set.statistic.printTime());
            SliceTime.setText(set.statistic.sliceTime());
            } catch (Exception ee){
                notify.notify(Values.error,ee.toString());
                return false; 
                }
        return true;
        }

    public boolean saveSettings(){
        try {
            Settings set = WorkSpace.ws().local();
            set.local.ZStart.setVal(Float.parseFloat(Zstart.getText()));
            set.local.ZFinish.setVal(Float.parseFloat(Zfinish.getText()));
            } catch (Exception ee){
                notify.notify(Values.error,ee.toString());
                return false; 
                }
        return true;
        }
    
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    }//GEN-LAST:event_formWindowClosing

    private void ZfinishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ZfinishActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ZfinishActionPerformed

    private void PageServoOffsetsLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PageServoOffsetsLeftActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PageServoOffsetsLeftActionPerformed

    private void ANGLEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ANGLEActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ANGLEActionPerformed

    private void RotateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RotateButtonActionPerformed
        double angle;
        try {
            angle = Double.parseDouble(ANGLE.getText())*Math.PI/180;
        } catch(Exception ee){
            notify.notify(Values.error,"Недопустимое значение угла");
            return;
        }
        WorkSpace ws = WorkSpace.ws();
        ws.model().rotate(XYZ.getSelectedIndex(),new MyAngle(angle),notify);
        ws.model().shiftToCenter();
        ws.data(new SliceData());
        Settings set = WorkSpace.ws().local();
        ws.model().saveModelDimensions();
        set.setZStartFinish();
        loadSettings();
        notify.log(String.format("Поворот %2s %s",(String)XYZ.getSelectedItem(),ANGLE.getText()));
        WorkSpace.ws().sendEvent(Events.Rotate);
    }//GEN-LAST:event_RotateButtonActionPerformed

    private void ShiftButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShiftButtonActionPerformed
        double shift;
        try {
            shift = Double.parseDouble(SHIFT.getText());
        } catch(Exception ee){
            notify.notify(Values.error,"Недопустимое значение сдвига");
            return;
            }
        shift/=(Values.PrinterFieldSize/2);
        WorkSpace ws = WorkSpace.ws();
        ws.model().shift(XYZShift.getSelectedIndex(),shift);
        ws.data(new SliceData());
        Settings set = WorkSpace.ws().local();
        ws.model().saveModelDimensions();
        set.setZStartFinish();
        loadSettings();
        notify.log(String.format("Сдвиг %2s %s",(String)XYZShift.getSelectedItem(),SHIFT.getText()));
        WorkSpace.ws().sendEvent(Events.Rotate);

    }//GEN-LAST:event_ShiftButtonActionPerformed

    private void SHIFTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SHIFTActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SHIFTActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField ANGLE;
    private javax.swing.JTextField LineCount;
    private javax.swing.JTextField LineLength;
    private javax.swing.JTextField MarkingFieldHight;
    private javax.swing.JTextField MarkingFieldWidth;
    private javax.swing.JTextField MoveProc;
    private javax.swing.JTextField PageServoOffsetsLeft;
    private javax.swing.JTextField PageServoOffsetsTop;
    private javax.swing.JTextField PrintTime;
    private javax.swing.JButton RotateButton;
    private javax.swing.JTextField SHIFT;
    private javax.swing.JTextField ScaleFactor;
    private javax.swing.JButton ShiftButton;
    private javax.swing.JTextField SliceTime;
    private javax.swing.JComboBox<String> XYZ;
    private javax.swing.JComboBox<String> XYZShift;
    private javax.swing.JTextField Z;
    private javax.swing.JLabel Z0_1;
    private javax.swing.JLabel Z0_2;
    private javax.swing.JTextField Zfinish;
    private javax.swing.JTextField Zstart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator5;
    // End of variables declaration//GEN-END:variables
}
