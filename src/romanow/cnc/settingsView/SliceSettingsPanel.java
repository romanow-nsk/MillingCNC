/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.settingsView;

import romanow.cnc.m3d.I_SettingsChanged;
import romanow.cnc.settings.Settings;
import romanow.cnc.utils.I_Notify;
import romanow.cnc.utils.Utils;
import romanow.cnc.Values;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 *
 * @author romanow
 */
public class SliceSettingsPanel extends javax.swing.JPanel  implements I_SettingsPanel{
    private I_SettingsChanged changed;
    /**
     * Creates new form M3SSettings
     */
    private Settings set;
    private I_Notify notify;
    private boolean busy=false;
    public SliceSettingsPanel(I_SettingsChanged changed0, Settings set0, I_Notify notify0) {
        initComponents();
        set = set0;
        notify = notify0;
        changed = changed0;
        this.setBounds(0,0, 800, 650);
        busy = true;
        Mode.removeAll();
        Mode.addItem("Растр");
        Mode.addItem("Клетки");
        Mode.addItem("Случайная");
        Mode.addItem("Фреза-1");
        Mode.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (busy) return;
                set.slice.Mode.setVal(Mode.getSelectedIndex());
                Utils.viewUpdate(Mode,true);
            }
        });
        MoveOptimize.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (busy) return;
                set.slice.MoveOptimize.setVal(MoveOptimize.isSelected());
                Utils.viewUpdate(MoveOptimize,true);
            }
        });
        loadSettings();
        busy = false;
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
        jLabel2 = new javax.swing.JLabel();
        VerticalStep = new javax.swing.JTextField();
        FillParametersOffset = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        FillParametersAngle = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        FillParametersFillCell = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        FillingFlatness = new javax.swing.JTextField();
        FillParametersAngleInc = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        Mode = new java.awt.Choice();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator5 = new javax.swing.JSeparator();
        MoveOptimize = new javax.swing.JCheckBox();
        SendLoops = new javax.swing.JCheckBox();
        jLabel39 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        FillParametersRaster = new javax.swing.JTextField();
        FlateCircuitSlice = new javax.swing.JCheckBox();
        Continuous = new javax.swing.JCheckBox();
        RepairLoops = new javax.swing.JCheckBox();
        LoopsWithSomeLineTypes = new javax.swing.JCheckBox();

        setLayout(null);
        add(jLabel1);
        jLabel1.setBounds(30, 20, 34, 0);

        jLabel2.setText("Фактура");
        add(jLabel2);
        jLabel2.setBounds(20, 40, 80, 14);
        add(VerticalStep);
        VerticalStep.setBounds(160, 240, 80, 25);
        add(FillParametersOffset);
        FillParametersOffset.setBounds(160, 90, 80, 25);

        jLabel17.setText("Смещение клетки (мм)");
        add(jLabel17);
        jLabel17.setBounds(20, 100, 140, 14);
        add(FillParametersAngle);
        FillParametersAngle.setBounds(160, 120, 80, 25);

        jLabel18.setText("Угол начальный (град)");
        add(jLabel18);
        jLabel18.setBounds(20, 130, 130, 10);
        add(FillParametersFillCell);
        FillParametersFillCell.setBounds(160, 180, 80, 25);

        jLabel19.setText("Размер клетки (мм)");
        add(jLabel19);
        jLabel19.setBounds(20, 190, 120, 14);
        add(FillingFlatness);
        FillingFlatness.setBounds(160, 210, 80, 25);
        add(FillParametersAngleInc);
        FillParametersAngleInc.setBounds(160, 150, 80, 25);

        jLabel21.setText("Приращение угла (град)");
        add(jLabel21);
        jLabel21.setBounds(20, 160, 150, 14);

        Mode.setBackground(new java.awt.Color(240, 240, 240));
        add(Mode);
        Mode.setBounds(130, 30, 110, 25);

        jLabel23.setText("Шаг по Z (мм)");
        add(jLabel23);
        jLabel23.setBounds(20, 250, 110, 14);

        jLabel24.setText("Сглаживание (мм)");
        add(jLabel24);
        jLabel24.setBounds(20, 220, 130, 14);
        add(jSeparator2);
        jSeparator2.setBounds(580, 152, 180, 0);
        add(jSeparator5);
        jSeparator5.setBounds(770, 330, 0, 2);

        MoveOptimize.setSelected(true);
        MoveOptimize.setText("Оптимизация перемещений");
        MoveOptimize.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                MoveOptimizeItemStateChanged(evt);
            }
        });
        add(MoveOptimize);
        MoveOptimize.setBounds(260, 30, 200, 23);

        SendLoops.setText("Оконтуривание");
        add(SendLoops);
        SendLoops.setBounds(260, 90, 180, 23);

        jLabel39.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel39.setText("Слайсинг");
        add(jLabel39);
        jLabel39.setBounds(20, 10, 80, 20);

        jLabel20.setText("Шаг растра (мм)");
        add(jLabel20);
        jLabel20.setBounds(20, 70, 120, 14);
        add(FillParametersRaster);
        FillParametersRaster.setBounds(160, 60, 80, 25);

        FlateCircuitSlice.setText("Слайсинг плоских контуров");
        add(FlateCircuitSlice);
        FlateCircuitSlice.setBounds(260, 120, 200, 23);

        Continuous.setText("Непрерывная штриховка / Зигзаг");
        add(Continuous);
        Continuous.setBounds(260, 60, 230, 23);

        RepairLoops.setText("Принудительно замыкать контуры");
        add(RepairLoops);
        RepairLoops.setBounds(260, 150, 220, 23);

        LoopsWithSomeLineTypes.setText("Контуры из отрезков одного типа");
        add(LoopsWithSomeLineTypes);
        LoopsWithSomeLineTypes.setBounds(260, 180, 210, 23);
    }// </editor-fold>//GEN-END:initComponents
    private boolean loadSettings(){
        try {
            set.setNotNull();
            DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
            DecimalFormat df = new DecimalFormat("0.000", dfs);
            DecimalFormat df2 = new DecimalFormat("0.00", dfs);
            DecimalFormat df3 = new DecimalFormat("000.0", dfs);
            //VerticalStep.setText(df.format(set.slice.VerticalStep.getVal()));
            Mode.select(set.slice.Mode.getVal());
            FillParametersRaster.setText(df.format(set.slice.FillParametersRaster.getVal()));
            FillParametersOffset.setText(df.format(set.slice.FillParametersOffset.getVal()));
            FillParametersAngle.setText(df.format(set.slice.FillParametersAngle.getVal()));
            FillParametersAngleInc.setText(df.format(set.slice.FillParametersAngleInc.getVal()));
            FillParametersFillCell.setText(df.format(set.slice.FillParametersFillCell.getVal()));
            FillingFlatness.setText(df.format(set.slice.FillingFlatness.getVal()));
            MoveOptimize.setSelected(set.slice.MoveOptimize.getVal());
            SendLoops.setSelected(set.slice.SendLoops.getVal());
            Continuous.setSelected(set.slice.FillContinuous.getVal());
            FlateCircuitSlice.setSelected(set.slice.FlateCircuitSlice.getVal());
            Continuous.setVisible(MoveOptimize.isSelected());
            RepairLoops.setSelected(set.slice.RepairLoops.getVal());
            LoopsWithSomeLineTypes.setSelected(set.slice.LoopsWithSomeLineTypes.getVal());
            } catch (Exception ee){
                notify.notify(Values.error,ee.toString());
                return false; 
                }
        return true;
        }

    public Settings copySettings(){
        Settings set  = new Settings();
        try {
            //set.slice.VerticalStep.setVal(Float.parseFloat(VerticalStep.getText()));
            set.slice.Mode.setVal(Mode.getSelectedIndex());
            set.slice.FillParametersRaster.setVal(Float.parseFloat(FillParametersRaster.getText()));
            set.slice.FillParametersOffset.setVal(Float.parseFloat(FillParametersOffset.getText()));
            set.slice.FillParametersAngle.setVal(Float.parseFloat(FillParametersAngle.getText()));
            set.slice.FillParametersAngleInc.setVal(Float.parseFloat(FillParametersAngleInc.getText()));
            set.slice.FillParametersFillCell.setVal(Float.parseFloat(FillParametersFillCell.getText()));
            set.slice.FillingFlatness.setVal(Float.parseFloat(FillingFlatness.getText()));
            set.slice.MoveOptimize.setVal(MoveOptimize.isSelected());
            set.slice.SendLoops.setVal(SendLoops.isSelected());
            set.slice.FlateCircuitSlice.setVal(FlateCircuitSlice.isSelected());            
            set.slice.FillContinuous.setVal(Continuous.isSelected());            
            set.slice.RepairLoops.setVal(RepairLoops.isSelected());    
            set.slice.LoopsWithSomeLineTypes.setVal(LoopsWithSomeLineTypes.isSelected());
            } catch (Exception ee){
                notify.notify(Values.error,ee.toString());
                return null; 
                }
        return set;
        }
    public boolean saveSettings(){
        try {
            //set.slice.VerticalStep.setVal(Float.parseFloat(VerticalStep.getText()));
            set.slice.Mode.setVal(Mode.getSelectedIndex());
            set.slice.FillParametersRaster.setVal(Float.parseFloat(FillParametersRaster.getText()));
            set.slice.FillParametersOffset.setVal(Float.parseFloat(FillParametersOffset.getText()));
            set.slice.FillParametersAngle.setVal(Float.parseFloat(FillParametersAngle.getText()));
            set.slice.FillParametersAngleInc.setVal(Float.parseFloat(FillParametersAngleInc.getText()));
            set.slice.FillParametersFillCell.setVal(Float.parseFloat(FillParametersFillCell.getText()));
            set.slice.FillingFlatness.setVal(Float.parseFloat(FillingFlatness.getText()));
            set.slice.MoveOptimize.setVal(MoveOptimize.isSelected());
            set.slice.SendLoops.setVal(SendLoops.isSelected());
            set.slice.FlateCircuitSlice.setVal(FlateCircuitSlice.isSelected());
            set.slice.FillContinuous.setVal(Continuous.isSelected());            
            set.slice.RepairLoops.setVal(RepairLoops.isSelected());            
            set.slice.LoopsWithSomeLineTypes.setVal(LoopsWithSomeLineTypes.isSelected());
            } catch (Exception ee){
                notify.notify(Values.error,ee.toString());
                return false; 
                }
        return true;
        }
    
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    }//GEN-LAST:event_formWindowClosing

    private void MoveOptimizeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_MoveOptimizeItemStateChanged
        Continuous.setVisible(MoveOptimize.isSelected());
    }//GEN-LAST:event_MoveOptimizeItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox Continuous;
    private javax.swing.JTextField FillParametersAngle;
    private javax.swing.JTextField FillParametersAngleInc;
    private javax.swing.JTextField FillParametersFillCell;
    private javax.swing.JTextField FillParametersOffset;
    private javax.swing.JTextField FillParametersRaster;
    private javax.swing.JTextField FillingFlatness;
    private javax.swing.JCheckBox FlateCircuitSlice;
    private javax.swing.JCheckBox LoopsWithSomeLineTypes;
    private java.awt.Choice Mode;
    private javax.swing.JCheckBox MoveOptimize;
    private javax.swing.JCheckBox RepairLoops;
    private javax.swing.JCheckBox SendLoops;
    private javax.swing.JTextField VerticalStep;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator5;
    // End of variables declaration//GEN-END:variables
}
