/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epos.slm3d.console;

import epos.slm3d.m3d.BaseFrame;
import epos.slm3d.utils.Events;
import epos.slm3d.utils.Values;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.JCheckBox;

/**
 *
 * @author romanow
 */
public class LaserConsole extends BaseFrame {
    private COMPortDriver laser=null;                   // Драйвер порта
    private JCheckBox flags[];                          // Чекбоксы для разрядов слова состояния
    private I_COMPortReceiver back=null;                // Обработчик событий (ответов) драйвера порта
    DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
    DecimalFormat df = new DecimalFormat("00.0", dfs);
    public LaserConsole() {
        if (!tryToStart()) return;
        initComponents();
        setBounds(50,110,600,480);
        setTitle("Консоль лазера");
        flags = new JCheckBox[]{NullBox,Overheat,EmissionOn,HighBacReflectionLevel, //0..3
            ExternalControlModeEnabled,NullBox,ModuleDisconnected,NullBox,          //4..7
            AimingBeamON,PulseTooShort,PulsedMode,PowerSupplyOff,                   //8..11
            ModulationON,NullBox,NullBox,Emission3second,                           //12..15
            GateModeEnabled,HighPulseEnergy,HardwareEmissionControlEnabled,PowerSupplyFailure, //16..19
            FrontPanelDisplayLocked,KeyswitchREMposition,NullBox,HighDutyCycle,     //20..23
            LowTemperature,PowerSupplyFailure2,NullBox,HardwareAimingBeamControlEnabled, //24..27
            NullBox,CriticalError,OpticalInterlockActive,AveragePowerTooHigh        //28..31
            };
        NullBox.setVisible(false);
        setChecksVisible();
        setLaserOnOff(true); 
        laser.getStaticData();
        }
    private void log(String ss){
        ws().notify(Values.important, ss);
        }
    public void setLaserOnOff(boolean on){
        if (on){
            laser = ws().comPort();
            back = new I_COMPortReceiver() {
                @Override
                public void onVolume(double val) {
                    log("Лазер: установлена мощность "+df.format(val)+"%");
                    Power.setText(df.format(val));
                    }
                @Override
                public void onOther(String mes) {
                    log("Ошибка лазера: "+mes);
                    MES.setText(mes);
                    }
                @Override
                public void onTemperature(double val) {
                    Temperature.setText(df.format(val));
                    }
                @Override
                public void onState(int state) {
                    for(int i=0;i<flags.length;i++)
                        flags[i].setSelected((state & (1<<i))!=0); 
                    setChecksVisible();
                    }
                @Override
                public void onErrorCode(int state){
                    ErrorCode.setText(""+state);
                    }
                @Override
                public void onHZ(double state) {
                    HZ.setText(df.format(state));                    }
                @Override
                public void onMS(double state) {
                    MS.setText(df.format(state));                    
                    }
                @Override
                public void onPower(double state) {
                    ROP.setText(df.format(state));
                    }
                };
            laser.connect(back);
            }
        if (!on){
            laser.disconnect(back);
            }
        }
    private synchronized void execAndAskState(String cmd){
        execAndAskState(cmd,null);
        }
    private synchronized void execAndAskState(String cmd,String cmd2){
        if (cmd!=null)
            laser.write(cmd);
        if (cmd2!=null)
            laser.write(cmd2);
        laser.getStateWord();           
        }        
    @Override
    public void onEvent(int code,boolean on, int value, String name) {
        super.onEvent(code,on,value,name);
        if (code == Events.Clock && laser!=null){        
            execAndAskState("RCT");       
        }
    }
    private void setChecksVisible(){
        AimingBeamON.setVisible(HardwareAimingBeamControlEnabled.isSelected());
        ExternalControlModeON.setVisible(ExternalControlModeEnabled.isSelected());
        EmissionOn.setVisible(HardwareEmissionControlEnabled.isSelected());
        ModulationON.setVisible(GateModeEnabled.isSelected());
        }  
    private void checkBoxCmd(JCheckBox box, String off, String on){
        MES.setText("");
        if (!box.isSelected())
            execAndAskState(on);
        else
            execAndAskState(off);
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
        jCheckBox2 = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        Overheat = new javax.swing.JCheckBox();
        EmissionOn = new javax.swing.JCheckBox();
        HighBacReflectionLevel = new javax.swing.JCheckBox();
        ExternalControlModeEnabled = new javax.swing.JCheckBox();
        ModuleDisconnected = new javax.swing.JCheckBox();
        NullBox = new javax.swing.JCheckBox();
        AimingBeamON = new javax.swing.JCheckBox();
        PulseTooShort = new javax.swing.JCheckBox();
        PulsedMode = new javax.swing.JCheckBox();
        PowerSupplyOff = new javax.swing.JCheckBox();
        ModulationON = new javax.swing.JCheckBox();
        Emission3second = new javax.swing.JCheckBox();
        GateModeEnabled = new javax.swing.JCheckBox();
        HighPulseEnergy = new javax.swing.JCheckBox();
        HardwareEmissionControlEnabled = new javax.swing.JCheckBox();
        PowerSupplyFailure = new javax.swing.JCheckBox();
        FrontPanelDisplayLocked = new javax.swing.JCheckBox();
        KeyswitchREMposition = new javax.swing.JCheckBox();
        HighDutyCycle = new javax.swing.JCheckBox();
        LowTemperature = new javax.swing.JCheckBox();
        PowerSupplyFailure2 = new javax.swing.JCheckBox();
        HardwareAimingBeamControlEnabled = new javax.swing.JCheckBox();
        CriticalError = new javax.swing.JCheckBox();
        OpticalInterlockActive = new javax.swing.JCheckBox();
        AveragePowerTooHigh = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        ExternalControlModeON = new javax.swing.JCheckBox();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jButton1 = new javax.swing.JButton();
        ErrorCode = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        Temperature = new javax.swing.JTextField();
        MS = new javax.swing.JTextField();
        Power = new javax.swing.JTextField();
        HZ = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        MES = new javax.swing.JTextField();
        ROP = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();

        jCheckBox1.setText("jCheckBox1");

        jCheckBox2.setText("jCheckBox2");

        jLabel2.setText("jLabel2");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Состояние лазера");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(30, 10, 120, 14);

        Overheat.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        Overheat.setText("Перегрев");
        Overheat.setEnabled(false);
        getContentPane().add(Overheat);
        Overheat.setBounds(20, 30, 100, 23);

        EmissionOn.setText("Эмиссия");
        EmissionOn.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                EmissionOnItemStateChanged(evt);
            }
        });
        EmissionOn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EmissionOnActionPerformed(evt);
            }
        });
        getContentPane().add(EmissionOn);
        EmissionOn.setBounds(20, 280, 140, 23);

        HighBacReflectionLevel.setText("Сильное обратное отражение");
        HighBacReflectionLevel.setEnabled(false);
        getContentPane().add(HighBacReflectionLevel);
        HighBacReflectionLevel.setBounds(280, 70, 200, 23);

        ExternalControlModeEnabled.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ExternalControlModeEnabled.setText("Разрешение аналогового управления");
        ExternalControlModeEnabled.setEnabled(false);
        getContentPane().add(ExternalControlModeEnabled);
        ExternalControlModeEnabled.setBounds(280, 220, 290, 23);

        ModuleDisconnected.setText("Модуль не подсоединен");
        ModuleDisconnected.setEnabled(false);
        getContentPane().add(ModuleDisconnected);
        ModuleDisconnected.setBounds(20, 90, 170, 23);

        NullBox.setText("...");
        getContentPane().add(NullBox);
        NullBox.setBounds(160, 10, 37, 23);

        AimingBeamON.setText("Пилотный лазер");
        AimingBeamON.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                AimingBeamONItemStateChanged(evt);
            }
        });
        AimingBeamON.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AimingBeamONActionPerformed(evt);
            }
        });
        getContentPane().add(AimingBeamON);
        AimingBeamON.setBounds(20, 240, 130, 23);

        PulseTooShort.setText("Длительность импульса мала ");
        PulseTooShort.setEnabled(false);
        getContentPane().add(PulseTooShort);
        PulseTooShort.setBounds(280, 50, 200, 23);

        PulsedMode.setText("Импульсный режим");
        PulsedMode.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                PulsedModeItemStateChanged(evt);
            }
        });
        PulsedMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PulsedModeActionPerformed(evt);
            }
        });
        getContentPane().add(PulsedMode);
        PulsedMode.setBounds(20, 300, 190, 23);

        PowerSupplyOff.setText("Источник питания выключен");
        PowerSupplyOff.setEnabled(false);
        getContentPane().add(PowerSupplyOff);
        PowerSupplyOff.setBounds(280, 180, 180, 23);

        ModulationON.setText("Модуляция излучения");
        ModulationON.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ModulationONItemStateChanged(evt);
            }
        });
        ModulationON.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModulationONActionPerformed(evt);
            }
        });
        getContentPane().add(ModulationON);
        ModulationON.setBounds(280, 280, 170, 23);

        Emission3second.setText("Излучение - 3 сек. после запуска");
        Emission3second.setEnabled(false);
        getContentPane().add(Emission3second);
        Emission3second.setBounds(280, 110, 220, 23);

        GateModeEnabled.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        GateModeEnabled.setText("Разрешение модуляции излучения");
        GateModeEnabled.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                GateModeEnabledItemStateChanged(evt);
            }
        });
        GateModeEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GateModeEnabledActionPerformed(evt);
            }
        });
        getContentPane().add(GateModeEnabled);
        GateModeEnabled.setBounds(280, 260, 260, 23);

        HighPulseEnergy.setText("Большая энергия в импульсе");
        HighPulseEnergy.setEnabled(false);
        getContentPane().add(HighPulseEnergy);
        HighPulseEnergy.setBounds(280, 30, 190, 23);

        HardwareEmissionControlEnabled.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        HardwareEmissionControlEnabled.setText("Разрешение  эмиссии");
        HardwareEmissionControlEnabled.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                HardwareEmissionControlEnabledItemStateChanged(evt);
            }
        });
        HardwareEmissionControlEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HardwareEmissionControlEnabledActionPerformed(evt);
            }
        });
        getContentPane().add(HardwareEmissionControlEnabled);
        HardwareEmissionControlEnabled.setBounds(20, 260, 250, 23);

        PowerSupplyFailure.setText("Неисправность источника питания");
        PowerSupplyFailure.setEnabled(false);
        PowerSupplyFailure.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PowerSupplyFailureActionPerformed(evt);
            }
        });
        getContentPane().add(PowerSupplyFailure);
        PowerSupplyFailure.setBounds(20, 50, 240, 23);

        FrontPanelDisplayLocked.setText("Сенсорный экран блокирован");
        FrontPanelDisplayLocked.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                FrontPanelDisplayLockedItemStateChanged(evt);
            }
        });
        getContentPane().add(FrontPanelDisplayLocked);
        FrontPanelDisplayLocked.setBounds(20, 320, 220, 23);

        KeyswitchREMposition.setText("Ключ в положении REM");
        KeyswitchREMposition.setEnabled(false);
        getContentPane().add(KeyswitchREMposition);
        KeyswitchREMposition.setBounds(280, 160, 180, 23);

        HighDutyCycle.setText("Высокая скважность импульсов");
        HighDutyCycle.setEnabled(false);
        getContentPane().add(HighDutyCycle);
        HighDutyCycle.setBounds(280, 90, 220, 23);

        LowTemperature.setText("Низкая температура");
        LowTemperature.setEnabled(false);
        getContentPane().add(LowTemperature);
        LowTemperature.setBounds(20, 110, 170, 23);

        PowerSupplyFailure2.setText("Неисправность источника питания 2 ");
        PowerSupplyFailure2.setEnabled(false);
        getContentPane().add(PowerSupplyFailure2);
        PowerSupplyFailure2.setBounds(20, 70, 230, 23);

        HardwareAimingBeamControlEnabled.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        HardwareAimingBeamControlEnabled.setText("Разрешение вкл. пилотного лазера");
        HardwareAimingBeamControlEnabled.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                HardwareAimingBeamControlEnabledItemStateChanged(evt);
            }
        });
        HardwareAimingBeamControlEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HardwareAimingBeamControlEnabledActionPerformed(evt);
            }
        });
        getContentPane().add(HardwareAimingBeamControlEnabled);
        HardwareAimingBeamControlEnabled.setBounds(20, 220, 250, 23);

        CriticalError.setText("Критическая ошибка");
        CriticalError.setEnabled(false);
        getContentPane().add(CriticalError);
        CriticalError.setBounds(20, 150, 160, 23);

        OpticalInterlockActive.setText("Оптическая блокировка активна");
        OpticalInterlockActive.setEnabled(false);
        getContentPane().add(OpticalInterlockActive);
        OpticalInterlockActive.setBounds(280, 130, 230, 23);

        AveragePowerTooHigh.setText("Слишком большая средняя мощность");
        AveragePowerTooHigh.setEnabled(false);
        getContentPane().add(AveragePowerTooHigh);
        AveragePowerTooHigh.setBounds(20, 130, 230, 23);
        getContentPane().add(jSeparator1);
        jSeparator1.setBounds(20, 210, 490, 10);

        ExternalControlModeON.setText("Аналоговое управление");
        ExternalControlModeON.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ExternalControlModeONItemStateChanged(evt);
            }
        });
        ExternalControlModeON.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExternalControlModeONActionPerformed(evt);
            }
        });
        getContentPane().add(ExternalControlModeON);
        ExternalControlModeON.setBounds(280, 240, 190, 23);
        getContentPane().add(jSeparator2);
        jSeparator2.setBounds(260, 158, 250, 2);

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        getContentPane().add(jSeparator3);
        jSeparator3.setBounds(260, 30, 20, 180);

        jButton1.setText("Сброс ошибки");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(20, 180, 110, 23);

        ErrorCode.setEditable(false);
        ErrorCode.setBackground(new java.awt.Color(200, 200, 200));
        getContentPane().add(ErrorCode);
        ErrorCode.setBounds(140, 180, 60, 25);

        jLabel3.setText("Ток накачки диодов %");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(300, 315, 140, 20);

        jLabel4.setText("Частота импульсов (гц)");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(300, 350, 140, 14);

        jLabel5.setText("Длительность импульсов (мс)");
        getContentPane().add(jLabel5);
        jLabel5.setBounds(270, 380, 180, 14);

        jLabel6.setText("Температура");
        getContentPane().add(jLabel6);
        jLabel6.setBounds(20, 355, 80, 14);

        Temperature.setEditable(false);
        Temperature.setBackground(new java.awt.Color(200, 200, 200));
        Temperature.setText("...");
        getContentPane().add(Temperature);
        Temperature.setBounds(120, 350, 80, 25);

        MS.setBackground(new java.awt.Color(240, 240, 240));
        MS.setText("...");
        getContentPane().add(MS);
        MS.setBounds(440, 370, 80, 25);

        Power.setBackground(new java.awt.Color(240, 240, 240));
        Power.setText("...");
        getContentPane().add(Power);
        Power.setBounds(440, 310, 80, 25);

        HZ.setBackground(new java.awt.Color(240, 240, 240));
        HZ.setText("...");
        getContentPane().add(HZ);
        HZ.setBounds(440, 340, 80, 25);

        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2);
        jButton2.setBounds(530, 370, 30, 30);

        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3);
        jButton3.setBounds(530, 310, 30, 30);

        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton4);
        jButton4.setBounds(530, 340, 30, 30);

        MES.setEditable(false);
        getContentPane().add(MES);
        MES.setBounds(20, 420, 540, 25);

        ROP.setEditable(false);
        ROP.setBackground(new java.awt.Color(200, 200, 200));
        ROP.setText("0");
        getContentPane().add(ROP);
        ROP.setBounds(120, 380, 80, 25);

        jLabel7.setText("Мощность (вт)");
        getContentPane().add(jLabel7);
        jLabel7.setBounds(20, 385, 80, 14);

        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton5);
        jButton5.setBounds(210, 380, 25, 25);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        setLaserOnOff(false); 
        onClose();       
    }//GEN-LAST:event_formWindowClosing

    private void PowerSupplyFailureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PowerSupplyFailureActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PowerSupplyFailureActionPerformed

    private void HardwareAimingBeamControlEnabledItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_HardwareAimingBeamControlEnabledItemStateChanged
 
    }//GEN-LAST:event_HardwareAimingBeamControlEnabledItemStateChanged

    private void AimingBeamONItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_AimingBeamONItemStateChanged

    }//GEN-LAST:event_AimingBeamONItemStateChanged

    private void ExternalControlModeONItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ExternalControlModeONItemStateChanged

    }//GEN-LAST:event_ExternalControlModeONItemStateChanged

    private void HardwareEmissionControlEnabledItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_HardwareEmissionControlEnabledItemStateChanged

    }//GEN-LAST:event_HardwareEmissionControlEnabledItemStateChanged

    private void EmissionOnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_EmissionOnItemStateChanged
        checkBoxCmd(EmissionOn,"EMON","EMOFF");
    }//GEN-LAST:event_EmissionOnItemStateChanged

    private void FrontPanelDisplayLockedItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_FrontPanelDisplayLockedItemStateChanged
        checkBoxCmd(FrontPanelDisplayLocked,"LFP","UFP");
    }//GEN-LAST:event_FrontPanelDisplayLockedItemStateChanged

    private void PulsedModeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_PulsedModeItemStateChanged

    }//GEN-LAST:event_PulsedModeItemStateChanged

    private void ModulationONItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ModulationONItemStateChanged

    }//GEN-LAST:event_ModulationONItemStateChanged

    private void GateModeEnabledItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_GateModeEnabledItemStateChanged

    }//GEN-LAST:event_GateModeEnabledItemStateChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        execAndAskState("RMEC","RERR");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try{
            laser.setVolume(Double.parseDouble(Power.getText()));
            Power.setText("");
            MES.setText("");
           } catch(Exception ee){ log("Установка мощности - формат вещественного числа"); }           
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try{
            laser.write("SPRR "+Double.parseDouble(HZ.getText()));
            HZ.setText("");
            MES.setText("");
            } catch(Exception ee){ log("Установка частоты - формат целого числа"); }           
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try{
            laser.write("SPW "+Double.parseDouble(MS.getText()));
            MS.setText("");
            MES.setText("");
            } catch(Exception ee){ log("Установка длительности импульса - формат вещественного числа"); }           
    }//GEN-LAST:event_jButton2ActionPerformed

    private void HardwareAimingBeamControlEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HardwareAimingBeamControlEnabledActionPerformed
        checkBoxCmd(HardwareAimingBeamControlEnabled,"EEABC","DEABC");    
    }//GEN-LAST:event_HardwareAimingBeamControlEnabledActionPerformed

    private void AimingBeamONActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AimingBeamONActionPerformed
        checkBoxCmd(AimingBeamON,"ABN","ABF");  
    }//GEN-LAST:event_AimingBeamONActionPerformed

    private void HardwareEmissionControlEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HardwareEmissionControlEnabledActionPerformed
        checkBoxCmd(HardwareEmissionControlEnabled,"ELE","DLE"); 
    }//GEN-LAST:event_HardwareEmissionControlEnabledActionPerformed

    private void EmissionOnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EmissionOnActionPerformed
        checkBoxCmd(ModulationON,"EMOD","DMOD");
    }//GEN-LAST:event_EmissionOnActionPerformed

    private void ExternalControlModeONActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExternalControlModeONActionPerformed
        checkBoxCmd(ExternalControlModeON,"EEC","DEC"); 
    }//GEN-LAST:event_ExternalControlModeONActionPerformed

    private void GateModeEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GateModeEnabledActionPerformed
        checkBoxCmd(GateModeEnabled,"EGM","DGM");
    }//GEN-LAST:event_GateModeEnabledActionPerformed

    private void ModulationONActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModulationONActionPerformed
        checkBoxCmd(ModulationON,"EMOD","DMOD");
    }//GEN-LAST:event_ModulationONActionPerformed

    private void PulsedModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PulsedModeActionPerformed
        checkBoxCmd(PulsedMode,"EPM","DPM");
    }//GEN-LAST:event_PulsedModeActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        laser.write("ROP");
    }//GEN-LAST:event_jButton5ActionPerformed

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
            java.util.logging.Logger.getLogger(LaserConsole.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LaserConsole.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LaserConsole.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LaserConsole.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LaserConsole().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox AimingBeamON;
    private javax.swing.JCheckBox AveragePowerTooHigh;
    private javax.swing.JCheckBox CriticalError;
    private javax.swing.JCheckBox Emission3second;
    private javax.swing.JCheckBox EmissionOn;
    private javax.swing.JTextField ErrorCode;
    private javax.swing.JCheckBox ExternalControlModeEnabled;
    private javax.swing.JCheckBox ExternalControlModeON;
    private javax.swing.JCheckBox FrontPanelDisplayLocked;
    private javax.swing.JCheckBox GateModeEnabled;
    private javax.swing.JTextField HZ;
    private javax.swing.JCheckBox HardwareAimingBeamControlEnabled;
    private javax.swing.JCheckBox HardwareEmissionControlEnabled;
    private javax.swing.JCheckBox HighBacReflectionLevel;
    private javax.swing.JCheckBox HighDutyCycle;
    private javax.swing.JCheckBox HighPulseEnergy;
    private javax.swing.JCheckBox KeyswitchREMposition;
    private javax.swing.JCheckBox LowTemperature;
    private javax.swing.JTextField MES;
    private javax.swing.JTextField MS;
    private javax.swing.JCheckBox ModulationON;
    private javax.swing.JCheckBox ModuleDisconnected;
    private javax.swing.JCheckBox NullBox;
    private javax.swing.JCheckBox OpticalInterlockActive;
    private javax.swing.JCheckBox Overheat;
    private javax.swing.JTextField Power;
    private javax.swing.JCheckBox PowerSupplyFailure;
    private javax.swing.JCheckBox PowerSupplyFailure2;
    private javax.swing.JCheckBox PowerSupplyOff;
    private javax.swing.JCheckBox PulseTooShort;
    private javax.swing.JCheckBox PulsedMode;
    private javax.swing.JTextField ROP;
    private javax.swing.JTextField Temperature;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    // End of variables declaration//GEN-END:variables
}
