/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epos.slm3d.settingsView;

import epos.slm3d.m3d.I_SettingsChanged;
import epos.slm3d.utils.Events;
import epos.slm3d.settings.Settings;
import epos.slm3d.settings.WorkSpace;
import epos.slm3d.utils.I_Notify;
import epos.slm3d.utils.Values;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.JPanel;

/**
 *
 * @author romanow
 */
public class GlobalSettingsPanel extends javax.swing.JPanel implements I_SettingsPanel{
    private I_SettingsChanged changed;
    /**
     * Creates new form M3SSettings
     */
    private Settings set;
    private I_Notify notify;
    public GlobalSettingsPanel(I_SettingsChanged changed0, Settings set0, I_Notify notify0) {
        initComponents();
        changed = changed0;
        notify = notify0;
        set = set0;
        this.setBounds(0,0, 800, 650);
        loadSettings();        
        }
    public JPanel getContentPane(){ return this; }
    public void setDefaultCloseOperation(int xx){}
    public void pack(){}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        ScaleFactor = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        AutoCenter = new javax.swing.JCheckBox();
        AutoScale = new javax.swing.JCheckBox();
        CenterOffsetX = new javax.swing.JTextField();
        CenterXY = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator5 = new javax.swing.JSeparator();
        LineBlock = new javax.swing.JCheckBox();
        CurrentLine = new javax.swing.JTextField();
        LayerCount = new javax.swing.JTextField();
        LCurrentLine = new javax.swing.JLabel();
        LCurrentLayer = new javax.swing.JLabel();
        IP = new javax.swing.JTextField();
        IPLabel = new javax.swing.JLabel();
        CenterOffsetY = new javax.swing.JTextField();
        CenterXY1 = new javax.swing.JLabel();
        ThreadLabel = new javax.swing.JLabel();
        ThreadNum = new javax.swing.JTextField();
        Motor4Label = new javax.swing.JLabel();
        Motor1Label = new javax.swing.JLabel();
        Motor3Label = new javax.swing.JLabel();
        M4L = new javax.swing.JTextField();
        M3L = new javax.swing.JTextField();
        M1L = new javax.swing.JTextField();
        M4C = new javax.swing.JTextField();
        M3C = new javax.swing.JTextField();
        M1C = new javax.swing.JTextField();
        Motor4Label1 = new javax.swing.JLabel();
        M4H = new javax.swing.JTextField();
        M3H = new javax.swing.JTextField();
        M1H = new javax.swing.JTextField();
        jLabel47 = new javax.swing.JLabel();
        LCurrentLayer1 = new javax.swing.JLabel();
        CurrentLayer = new javax.swing.JTextField();
        PrintingState = new javax.swing.JTextField();
        LCurrentLine1 = new javax.swing.JLabel();
        COMPort = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        WorkFieldSize = new javax.swing.JTextField();
        LCurrentLine2 = new javax.swing.JLabel();

        setLayout(null);
        add(jLabel1);
        jLabel1.setBounds(30, 20, 34, 0);
        add(ScaleFactor);
        ScaleFactor.setBounds(160, 30, 80, 25);

        jLabel8.setText("Масштаб модели");
        add(jLabel8);
        jLabel8.setBounds(30, 40, 110, 16);

        AutoCenter.setSelected(true);
        AutoCenter.setText("АвтоЦентр");
        add(AutoCenter);
        AutoCenter.setBounds(30, 90, 100, 20);

        AutoScale.setSelected(true);
        AutoScale.setText("АвтоМасштаб");
        AutoScale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AutoScaleActionPerformed(evt);
            }
        });
        add(AutoScale);
        AutoScale.setBounds(30, 60, 110, 20);

        CenterOffsetX.setText("0");
        add(CenterOffsetX);
        CenterOffsetX.setBounds(180, 140, 60, 25);

        CenterXY.setText("Смещение центра Y (мм)");
        add(CenterXY);
        CenterXY.setBounds(30, 170, 160, 20);

        jLabel36.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel36.setText("Настройки ПО");
        add(jLabel36);
        jLabel36.setBounds(30, 120, 90, 14);

        jLabel46.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel46.setText("Станок");
        add(jLabel46);
        jLabel46.setBounds(270, 10, 80, 20);
        add(jSeparator2);
        jSeparator2.setBounds(580, 152, 180, 0);
        add(jSeparator5);
        jSeparator5.setBounds(770, 330, 0, 3);

        LineBlock.setText("Блочный вывод");
        add(LineBlock);
        LineBlock.setBounds(30, 200, 120, 20);

        CurrentLine.setEditable(false);
        CurrentLine.setBackground(new java.awt.Color(220, 220, 220));
        add(CurrentLine);
        CurrentLine.setBounds(390, 90, 80, 25);

        LayerCount.setEditable(false);
        LayerCount.setBackground(new java.awt.Color(220, 220, 220));
        add(LayerCount);
        LayerCount.setBounds(390, 30, 80, 25);

        LCurrentLine.setText("Рабочее поле (мм)");
        add(LCurrentLine);
        LCurrentLine.setBounds(270, 130, 120, 16);

        LCurrentLayer.setText("Снято слоев");
        add(LCurrentLayer);
        LCurrentLayer.setBounds(270, 40, 100, 16);

        IP.setText("localhost");
        add(IP);
        IP.setBounds(120, 230, 120, 25);

        IPLabel.setText("IP контроллера");
        add(IPLabel);
        IPLabel.setBounds(30, 230, 90, 20);

        CenterOffsetY.setText("0");
        add(CenterOffsetY);
        CenterOffsetY.setBounds(180, 170, 60, 25);

        CenterXY1.setText("Смещение центра X (мм)");
        add(CenterXY1);
        CenterXY1.setBounds(30, 140, 160, 20);

        ThreadLabel.setText("Потоков");
        add(ThreadLabel);
        ThreadLabel.setBounds(30, 265, 50, 16);

        ThreadNum.setText("5");
        add(ThreadNum);
        ThreadNum.setBounds(120, 260, 40, 25);

        Motor4Label.setText("Начальное     Текущее      Конечное");
        add(Motor4Label);
        Motor4Label.setBounds(540, 120, 200, 16);

        Motor1Label.setText("Мотор 1");
        add(Motor1Label);
        Motor1Label.setBounds(490, 40, 60, 16);

        Motor3Label.setText("Мотор 3");
        add(Motor3Label);
        Motor3Label.setBounds(490, 70, 60, 16);

        M4L.setEditable(false);
        M4L.setBackground(new java.awt.Color(255, 255, 255));
        add(M4L);
        M4L.setBounds(550, 90, 50, 25);

        M3L.setEditable(false);
        M3L.setBackground(new java.awt.Color(255, 255, 255));
        add(M3L);
        M3L.setBounds(550, 60, 50, 25);

        M1L.setEditable(false);
        M1L.setBackground(new java.awt.Color(255, 255, 255));
        add(M1L);
        M1L.setBounds(550, 30, 50, 25);

        M4C.setEditable(false);
        M4C.setBackground(new java.awt.Color(220, 220, 220));
        add(M4C);
        M4C.setBounds(610, 90, 60, 25);

        M3C.setEditable(false);
        M3C.setBackground(new java.awt.Color(220, 220, 220));
        add(M3C);
        M3C.setBounds(610, 60, 60, 25);

        M1C.setEditable(false);
        M1C.setBackground(new java.awt.Color(220, 220, 220));
        add(M1C);
        M1C.setBounds(610, 30, 60, 25);

        Motor4Label1.setText("Мотор 4 ");
        add(Motor4Label1);
        Motor4Label1.setBounds(490, 100, 60, 16);

        M4H.setEditable(false);
        M4H.setBackground(new java.awt.Color(255, 255, 255));
        add(M4H);
        M4H.setBounds(680, 90, 60, 25);

        M3H.setEditable(false);
        M3H.setBackground(new java.awt.Color(255, 255, 255));
        add(M3H);
        M3H.setBounds(680, 60, 60, 25);

        M1H.setEditable(false);
        M1H.setBackground(new java.awt.Color(255, 255, 255));
        add(M1H);
        M1H.setBounds(680, 30, 60, 25);

        jLabel47.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel47.setText("Модель");
        add(jLabel47);
        jLabel47.setBounds(30, 10, 80, 20);

        LCurrentLayer1.setText("Текущий слой");
        add(LCurrentLayer1);
        LCurrentLayer1.setBounds(270, 70, 90, 16);

        CurrentLayer.setEditable(false);
        CurrentLayer.setBackground(new java.awt.Color(220, 220, 220));
        add(CurrentLayer);
        CurrentLayer.setBounds(390, 60, 80, 25);

        PrintingState.setEditable(false);
        PrintingState.setBackground(new java.awt.Color(220, 220, 220));
        add(PrintingState);
        PrintingState.setBounds(390, 150, 120, 25);

        LCurrentLine1.setText("Текущая линия");
        add(LCurrentLine1);
        LCurrentLine1.setBounds(270, 100, 90, 16);

        COMPort.setText("COM3");
        add(COMPort);
        COMPort.setBounds(250, 260, 60, 25);

        jLabel2.setText("COM порт");
        add(jLabel2);
        jLabel2.setBounds(170, 265, 60, 16);

        WorkFieldSize.setEditable(false);
        WorkFieldSize.setBackground(new java.awt.Color(255, 255, 255));
        add(WorkFieldSize);
        WorkFieldSize.setBounds(390, 120, 80, 25);

        LCurrentLine2.setText("Процесс печати");
        add(LCurrentLine2);
        LCurrentLine2.setBounds(270, 160, 100, 16);
    }// </editor-fold>//GEN-END:initComponents
    public boolean loadSettings(){
        try {
            set.setNotNull();
            DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
            DecimalFormat df = new DecimalFormat("0.000", dfs);
            DecimalFormat df2 = new DecimalFormat("0.00", dfs);
            ScaleFactor.setText(df.format(set.global.ScaleFactor.getVal()));
            CenterOffsetX.setText(df2.format(set.global.CenterOffsetX.getVal()));
            CenterOffsetY.setText(df2.format(set.global.CenterOffsetY.getVal()));
            AutoCenter.setSelected(set.global.AutoCenter.getVal());
            AutoScale.setSelected(set.global.AutoScale.getVal());
            LineBlock.setSelected(set.global.LineBlock.getVal());
            LayerCount.setText(""+set.global.LayerCount.getVal());
            CurrentLayer.setText(""+set.global.CurrentLayer.getVal());
            CurrentLine.setText(""+set.global.CurrentLine.getVal());
            IP.setText(set.global.ControllerIP.getVal());
            ThreadNum.setText(""+set.global.SliceThreadNum.getVal());
            M1L.setText(""+set.global.M1LowPos.getVal());
            M3L.setText(""+set.global.M3LowPos.getVal());
            M4L.setText(""+set.global.M4LowPos.getVal());
            M1C.setText(""+set.global.M1CurrentPos.getVal());
            M3C.setText(""+set.global.M3CurrentPos.getVal());
            M4C.setText(""+set.global.M4CurrentPos.getVal());
            M1H.setText(""+set.global.M1HighPos.getVal());
            M3H.setText(""+set.global.M3HighPos.getVal());
            M4H.setText(""+set.global.M4HighPos.getVal());
            WorkFieldSize.setText(""+set.global.WorkFieldSize.getVal());
            LayerCount.setText(""+set.global.LayerCount.getVal());  
            PrintingState.setText(Events.PStates[set.global.PrintingState.getVal()]);
            COMPort.setText(set.global.COMPort.getVal());
            } catch (Exception ee){
                notify.notify(Values.error,ee.toString());
                return false; 
                }
        return true;
        }

    public boolean saveSettings(){
        try {
            /** потеря точности при перезаписи ???? */
            set.global.ScaleFactor.setVal(Float.parseFloat(ScaleFactor.getText()));
            set.global.CenterOffsetX.setVal(Float.parseFloat(CenterOffsetX.getText()));
            set.global.CenterOffsetY.setVal(Float.parseFloat(CenterOffsetY.getText()));
            set.global.WorkFieldSize.setVal(Float.parseFloat(WorkFieldSize.getText()));
            set.global.AutoCenter.setVal(AutoCenter.isSelected());
            set.global.AutoScale.setVal(AutoScale.isSelected());
            set.global.LineBlock.setVal(LineBlock.isSelected());
            set.global.ControllerIP.setVal(IP.getText());
            set.global.SliceThreadNum.setVal(Integer.parseInt(ThreadNum.getText()));
            set.global.COMPort.setVal(COMPort.getText());
            //set.global.M1LowPos.setVal(Integer.parseInt(M1L.getText()));
            //set.global.M3LowPos.setVal(Integer.parseInt(M3L.getText()));
            //set.global.M4LowPos.setVal(Integer.parseInt(M4L.getText()));
            //set.global.M1HighPos.setVal(Integer.parseInt(M1H.getText()));
            //set.global.M3HighPos.setVal(Integer.parseInt(M3H.getText()));
            //set.global.M4HighPos.setVal(Integer.parseInt(M4H.getText()));
            //set.global.LayerCount.setVal(Integer.parseInt(LayerCount.getText()));
            } catch (Exception ee){
                notify.notify(Values.error,ee.toString());
                return false; 
                }
        return true;
        }
    
    private void AutoScaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AutoScaleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AutoScaleActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox AutoCenter;
    private javax.swing.JCheckBox AutoScale;
    private javax.swing.JTextField COMPort;
    private javax.swing.JTextField CenterOffsetX;
    private javax.swing.JTextField CenterOffsetY;
    private javax.swing.JLabel CenterXY;
    private javax.swing.JLabel CenterXY1;
    private javax.swing.JTextField CurrentLayer;
    private javax.swing.JTextField CurrentLine;
    private javax.swing.JTextField IP;
    private javax.swing.JLabel IPLabel;
    private javax.swing.JLabel LCurrentLayer;
    private javax.swing.JLabel LCurrentLayer1;
    private javax.swing.JLabel LCurrentLine;
    private javax.swing.JLabel LCurrentLine1;
    private javax.swing.JLabel LCurrentLine2;
    private javax.swing.JTextField LayerCount;
    private javax.swing.JCheckBox LineBlock;
    private javax.swing.JTextField M1C;
    private javax.swing.JTextField M1H;
    private javax.swing.JTextField M1L;
    private javax.swing.JTextField M3C;
    private javax.swing.JTextField M3H;
    private javax.swing.JTextField M3L;
    private javax.swing.JTextField M4C;
    private javax.swing.JTextField M4H;
    private javax.swing.JTextField M4L;
    private javax.swing.JLabel Motor1Label;
    private javax.swing.JLabel Motor3Label;
    private javax.swing.JLabel Motor4Label;
    private javax.swing.JLabel Motor4Label1;
    private javax.swing.JTextField PrintingState;
    private javax.swing.JTextField ScaleFactor;
    private javax.swing.JLabel ThreadLabel;
    private javax.swing.JTextField ThreadNum;
    private javax.swing.JTextField WorkFieldSize;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator5;
    // End of variables declaration//GEN-END:variables
}
