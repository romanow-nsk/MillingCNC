/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.console;

import romanow.cnc.Values;
import romanow.cnc.view.BaseFrame;
import romanow.cnc.commands.*;
import romanow.cnc.m3d.*;
import romanow.cnc.settings.Settings;
import romanow.cnc.settings.WorkSpace;
import romanow.cnc.slicer.SliceLayer;
import romanow.cnc.stl.STLLine;
import romanow.cnc.stl.STLPoint2D;
import romanow.cnc.controller.*;
import romanow.cnc.utils.*;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;

import static romanow.cnc.utils.Events.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 *
 * @author romanow
 */
public class PrintConsole extends BaseFrame {
    private I_Notify notify;
    private volatile boolean isRun=false;
    private USBCommandGenerator generator;      // Генератор команд
    private USBProtocol usb;                    // Драйвер протокола
    private USBFace controller;                 // Интерфейс линейного драйвера (USB,UDP)
    private USBCommandFactory factoryFull = new USBCommandFactory();
    private USBNotify back;                     // Адаптер ответов на команды принтера
    private USBNotify backMotors;               // Адаптер ответов на команды управления моторами
    private volatile boolean closing=false;     // Процесс закрытия формы
    //private Thread stateLoop;
    private boolean udp = false;                // Индикатор работы с UDP (TCP/IP)
    private Thread printerThread=null;          // Поток печати
    private synchronized void setRun(boolean xx){ isRun = xx; }
    private synchronized boolean isRun(){ return isRun; }
    private COMPortDriver power=null;           // Драйвер управления лазером
    private M3DOperations operate=null;         // Модуль операций для копирования данных слайсинга
    private Thread printThread=null;            // Поток печати для исполения отдельных команд и тестов
    private int layerCount=0;                   // Номер слоя
    private int lineCount=0;                    // Номер линии
    private int printerState=0;                 // Состояние принтера, полученное от контроллера
    private boolean  saveLineCount=false;
    private ViewAdapter synch = new ViewAdapter(null);
    private boolean laserOn=false;              // Лазер включен
    private SliceLayer data=null;               // Данные млайсинга
    private Settings printSettings=null;        // Текущие установки печати слоя
    private boolean oxygen=false;               // Включение датчика кислорода
    private volatile boolean fatalDisconnect=false; // Повторный коннект при фатальной ошибке
    private I_COMPortReceiver comBack;          // Адаптер ответов драйвера лазера
    private boolean laserEnergyCalc = false;    // Режим подсчета энергии лазера в драйвере лазера
    private DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
    private DecimalFormat df = new DecimalFormat("00.0", dfs);

    /**
     * Creates new form PrintConsole
     */
    
    private void setPowerVisible(boolean on){
        laserOn = on;
        L4.setVisible(on);
        L7.setVisible(on);
        Power.setVisible(on);
        Temperature.setVisible(on);
        setPrintParamsVisible();        
        }
    public void setPowerOnOff(boolean on){
        if (on){
            power = ws().comPort();
            comBack = new I_COMPortReceiver() {
                @Override
                public void onVolume(double val) {
                    String ss = df.format(val);
                    Power.setText(ss);
                    notify.log("Лазер: установлена мощность "+ss+"%");
                    }
                @Override
                public void onOther(String mes) {
                    notify.notify(Values.error,mes);
                    }
                @Override
                public void onTemperature(double val) {
                    Temperature.setText(df.format(val));
                    }
                @Override
                public void onState(int state) {}
                @Override
                public void onErrorCode(int state){}
                @Override
                public void onHZ(double state) {}
                @Override
                public void onMS(double state) {}
                @Override
                public void onPower(double state) {
                    Energy.setText(String.format("%6.2f/%6.2f",state,power.laserPowerData().middle()));
                    }
                };
            power.connect(comBack);
            }
        if (!on){
            power.disconnect(comBack);
            }
        }
    private void setBottonsVisible(){
        setBottonsVisible(ws().printing());
        }
    private void setBottonsVisible(int state){
        CancelPrint.setText(state == PStateWorking ? "Прервать" : "Сбросить");
        PrintingState.setText(Events.PStates[state]);
        if (data==null){
            Suspend.setVisible(false);
            ChangeLayer.setVisible(false);
            Print.setVisible(false);
            return;
            }
        Print.setVisible(state == PStateStandBy || state == PStateFullLayer);
        Suspend.setVisible(state == PStateWorking || state == PStateSuspend);
        Suspend.setText(state != PStateWorking ? "Продолжить" : "Приостановить");
        CancelPrint.setText(state == PStateWorking ? "Прервать" : "Сбросить");
        ChangeLayer.setVisible(state == PStateStandBy || state == PStateFullLayer);
        Correct.setVisible(state != PStateWorking);
        MotorsControl.setVisible(state != PStateWorking);
        LineStep.setVisible(state != PStateWorking);
        sourceOn.enable(state != PStateWorking);
        emissionOn.enable(state != PStateWorking);
        guideOn.enable(state != PStateWorking);
        modulationOn.enable(state != PStateWorking);
        laserErr.enable(state != PStateWorking);
        }
    private void closePrint(){
        try {
            generator.end(null);
            } catch (UNIException e) { notify.notify(Values.error,e.toString()); }
        }
    private void sendCommand(Command cmd){
        usb.oneCommand(cmd.toIntArray(),back);
        }
    private void sendLine(STLLine line) throws UNIException { 
        generator.line(line); 
        }
    private void noLayerMode(){
        sendCommand(new CommandIntList(USBCodes.ChangeLayerMotorsEnable,NoLayer.isSelected() ? 0 : 1));
        }
    public void log(String ss){
        notify.log(ss);
        }
    
    public PrintConsole(I_Notify notify0, boolean udp0) {
        if (!tryToStart()) return;
        udp = udp0;
        controller = udp ? new USBUDPEmulator() : new USBLineController();
        usb = new USBProtocol(controller);
        notify = notify0;
        operate = new M3DOperations(notify);
        initComponents();
        setBounds(50,110,670,550);
        setTitle("Печать (файл, тесты)");
        setLayerData();
        ArrayList<String> ss = factoryFull.commandList();
        for(int i=0;i<ss.size();i++)
            CmdList.addItem(ss.get(i));
        setManualCommandVisible();
        viewPrintParams();
        setPrintParamsVisible();
        setPowerVisible(false);
        setBottonsVisible();
        restoreLayerCount(); 
        setPowerOnOff(true);
        back = new USBNotify(factoryFull,notify) {
            @Override
            public void onSuccess(int code, int[] data) {
                switch(code){
                    case USBCodes.IsReady:
                        printerState=data[1];
                        String ss = USBCodes.USBStateNames[printerState];
                        PrinterState.setText(ss);
                        break;
                    case USBCodes.GetAvailMemory:
                        FreeBuf.setText(""+data[1]);
                        usb.setAvailMemory(data[1]);
                        break;
                    case USBCodes.SetBurnLine:
                        FreeBuf.setText(""+data[1]);
                        usb.setAvailMemory(data[1]);
                        break;
                    case USBCodes.GetLineCount:
                        Printed.setText(""+(data[1])+"/"+data[2]);
                        if (saveLineCount){
                            notify.log("Напечатано "+(data[1])+"/"+data[2]);
                            ws().global().global.CurrentLayer.setVal(data[1]); 
                            ws().global().global.CurrentLine.setVal(data[2]); 
                            ws().saveSettings();                            
                            saveLineCount=false;
                            }
                        break;
                    case USBCodes.GetBeamStatus:
                        LaserVisualisation(data[1]);
                        break;
                    case USBCodes.ReadMessages:
                    case USBCodes.ReadLog:
                        int count = data[1];
                        notify.log("Прочитано "+count+" строки");
                        String zz[] = new String[0];
                        try {
                            zz = Utils.IntArrayToStrings(data);
                            } catch (UNIException e) {
                            notify.notify(Values.fatal,e.toString());
                            return;
                            }
                        for(String vv : zz){
                            notify.log(vv);
                        }
                        break;
                    case USBCodes.GetMotorStatus:
                        notify.log("Мотор "+data[1]+" ["+data[2]+"..."+data[3]+"] "+data[4]);
                        break;
                    case USBCodes.OxygenSensorGetData:
                        if (data[2]!=1)
                            OxygenData.setText("Ошибка:"+data[2]);
                        else{
                            OxygenData.setText(""+((data[3]>>16)&0x0FF)+"."+(data[3]&0x0FF)+"% "+((data[4]>>16)&0x0FF)+"."+(data[4]&0x0FF)+"C ");
                            }
                        break;
                        
                }
            }
        };
        backMotors = new USBNotify(factoryFull,notify) {
            @Override
            public void onSuccess(int code, int[] data) {
                switch(code){
                    case USBCodes.GetMotorStatus:
                        switch (data[1]){
                            case 1: ws().global().global.M1CurrentPos.setVal(data[4]); 
                                    ws().saveSettings();
                                    break;
                            case 3: ws().global().global.M3CurrentPos.setVal(data[4]); 
                                    ws().saveSettings();
                                    break;
                            case 4: ws().global().global.M4CurrentPos.setVal(data[4]); 
                                    ws().saveSettings();
                                    break;
                                }                                
                        notify.log("Мотор после слоя "+data[1]+" ["+data[2]+"..."+data[3]+"] "+data[4]);
                        break;                        
                }
            }
        };
        try {
            synchronized (this) { usb.init(); }
            } catch (UNIException e) { notify.notify(Values.error,e.toString()); }
        /*---------------------------------------------------------------------
        stateLoop = new Thread(()->{
            while (!closing){
                if (fatalDisconnect)
                    try {
                        Thread.sleep(Values.PrinterReconnectDelay*1000);
                        } catch (InterruptedException e) {}
                askState();
                try {
                    Thread.sleep(Values.PrinterStateLoopDelay*1000);
                    } catch (InterruptedException e) {}
                }
            });
        stateLoop.start();
        ---------------------------------------------------------------------*/
        }
    private void askState(){
        sendCommand(new Command(USBCodes.IsReady));
        sendCommand(new Command(USBCodes.GetBeamStatus));
        sendCommand(new Command(USBCodes.GetAvailMemory));
        sendCommand(new Command(USBCodes.GetLineCount));
        if (oxygen)
            usb.OxygenGetData(back);
        if (power!=null)
            power.getTemperature();
        }
    private void setManualCommandVisible(){
        String cc = (String)CmdList.getSelectedItem();
        try {
            Command xx = factoryFull.getCommand(cc);
            if (xx==null) {
                notify.log( "Не найдена команда:" + CmdList.getSelectedItem());
                return;
            }
            int sign = xx.signature();
            X2.setVisible(sign==Command.SIGN_LINE);
            Y2.setVisible(sign==Command.SIGN_LINE);
            LX2.setVisible(sign==Command.SIGN_LINE);
            LY2.setVisible(sign==Command.SIGN_LINE);
            XY.setVisible(sign==Command.SIGN_LINE || sign==Command.SIGN_POINT);
            X1.setVisible(sign==Command.SIGN_LINE || sign==Command.SIGN_POINT);
            Y1.setVisible(sign==Command.SIGN_LINE || sign==Command.SIGN_POINT);
            LX1.setVisible(sign==Command.SIGN_LINE || sign==Command.SIGN_POINT);
            LY1.setVisible(sign==Command.SIGN_LINE || sign==Command.SIGN_POINT);
            PAR.setVisible(sign == Command.SIGN_INTLIST && xx.dataSize() >=1 );
            P1.setVisible(sign == Command.SIGN_INTLIST && xx.dataSize() >=1 || sign == Command.SIGN_INT || sign == Command.SIGN_FLOAT);
            P2.setVisible(sign == Command.SIGN_INTLIST && xx.dataSize() >=2 );
            P3.setVisible(sign == Command.SIGN_INTLIST && xx.dataSize() >=3 );
            P4.setVisible(sign == Command.SIGN_INTLIST && xx.dataSize() >=4 );
            Settings set = WorkSpace.ws().global();
            switch (xx.code()){
                case USBCodes.MarkingMicroStepsMarkInt: P1.setText(""+set.marking.MicroStepsMark.getVal()); break;
                case USBCodes.ControlNextLayerMovingM4Step: P1.setText(""+set.control.NextLayerMovingM4Step.getVal()); break;
                case USBCodes.ControlNextLayerMovingM3Step: P1.setText(""+set.control.NextLayerMovingM3Step.getVal()); break;                }
            if (xx instanceof CommandParam){
                switch(((CommandParam)xx).idx()){
                    case USBCodes._DelaysLaserOn: P1.setText(""+set.delays.LaserOn.getVal()); break;
                    case USBCodes._DelaysLaserOff: P1.setText(""+set.delays.LaserOff.getVal()); break;
                    case USBCodes._DelaysMovingPenJumpDelay: P1.setText(""+set.delays.MovingPenJumpDelay.getVal()); break;
                    case USBCodes._DelaysMovingPenMarkDelay: P1.setText(""+set.delays.MovingPenMarkDelay.getVal()); break;
                    case USBCodes._DelaysMovingPenStrokeDelay: P1.setText(""+set.delays.MovingPenStrokeDelay.getVal()); break;
                    case USBCodes._MarkingMicroStepsMarkInt: P1.setText(""+set.marking.MicroStepsMark.getVal()); break;
                    case USBCodes._MarkingMicroStepsJumpInt: P1.setText(""+set.marking.MicroStepsJump.getVal()); break;
                    case USBCodes._MarkingMarkTailsInputInt: P1.setText(""+set.marking.MarkTailsInput.getVal()); break;
                    case USBCodes._MarkingMarkTailsOutputInt: P1.setText(""+set.marking.MarkTailsOutput.getVal()); break;
                    case USBCodes._ControlNextLayerMovingM4Step: P1.setText(""+set.control.NextLayerMovingM4Step.getVal()); break;
                    case USBCodes._ControlNextLayerMovingM3Step: P1.setText(""+set.control.NextLayerMovingM3Step.getVal()); break;
                    case USBCodes._PulseLaserFrequence: P1.setText(""+set.pulses.LaserFrequence.getVal()); break;
                    case USBCodes._PulseLaserPumpPower: P1.setText(""+set.pulses.LaserPumpPower.getVal()); break;
                    case USBCodes._PulseLaserPulseType: P1.setText(""+set.pulses.LaserPulseType.getVal()); break;
                    case USBCodes._PulseLaserPulseSuppress: P1.setText(""+set.pulses.LaserPulseSuppress.getVal()); break;
                    case USBCodes._PulseDACFrequence: P1.setText(""+set.pulses.DACFrequence.getVal());break;

                    }
                }
            } catch (UNIException e) { notify.log("Ошибка генерации команды: "+cc+e.toString()+e.toString()); }
        }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        TestList = new javax.swing.JComboBox<>();
        Start = new javax.swing.JButton();
        CmdList = new java.awt.Choice();
        jButton1 = new javax.swing.JButton();
        LY1 = new javax.swing.JLabel();
        X1 = new javax.swing.JTextField();
        Y1 = new javax.swing.JTextField();
        LX1 = new javax.swing.JLabel();
        LY2 = new javax.swing.JLabel();
        X2 = new javax.swing.JTextField();
        Y2 = new javax.swing.JTextField();
        LX2 = new javax.swing.JLabel();
        P1 = new javax.swing.JTextField();
        P2 = new javax.swing.JTextField();
        P3 = new javax.swing.JTextField();
        P4 = new javax.swing.JTextField();
        PAR = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        XY = new javax.swing.JLabel();
        sourceOn = new java.awt.Checkbox();
        jSeparator1 = new javax.swing.JSeparator();
        guideOn = new java.awt.Checkbox();
        emissionOn = new java.awt.Checkbox();
        modulationOn = new java.awt.Checkbox();
        laserErr = new java.awt.Checkbox();
        jLabel4 = new javax.swing.JLabel();
        PrinterState = new javax.swing.JTextField();
        NoLayer = new javax.swing.JCheckBox();
        Step = new javax.swing.JTextField();
        Quadrant = new javax.swing.JTextField();
        NLines = new javax.swing.JTextField();
        L2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        RunTest = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        M4Step = new javax.swing.JTextField();
        MicroStep = new javax.swing.JTextField();
        M3Step = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        L1 = new javax.swing.JLabel();
        L3 = new javax.swing.JLabel();
        XX1 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        YY1 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        XX2 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        YY2 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        Printed = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        L4 = new javax.swing.JLabel();
        FreeBuf = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        ResetPrint = new javax.swing.JCheckBox();
        WorkTime = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        Print = new javax.swing.JButton();
        Power = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        Suspend = new javax.swing.JButton();
        CancelPrint = new javax.swing.JButton();
        MotorsControl = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JSeparator();
        jSeparator6 = new javax.swing.JSeparator();
        jSeparator7 = new javax.swing.JSeparator();
        jLabel20 = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();
        jSeparator9 = new javax.swing.JSeparator();
        OneLayerMode = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        Current = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        PrintingState = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator10 = new javax.swing.JSeparator();
        SetManual = new javax.swing.JCheckBox();
        jSeparator11 = new javax.swing.JSeparator();
        SetParams = new javax.swing.JButton();
        SetPower = new javax.swing.JButton();
        jSeparator13 = new javax.swing.JSeparator();
        jSeparator14 = new javax.swing.JSeparator();
        Correct = new javax.swing.JButton();
        Oxygen = new javax.swing.JCheckBox();
        OxygenData = new javax.swing.JTextField();
        LineStep = new javax.swing.JButton();
        ChangeLayer = new javax.swing.JButton();
        L5 = new javax.swing.JLabel();
        L6 = new javax.swing.JLabel();
        LaserOffDelay = new javax.swing.JTextField();
        LaserOnDelay = new javax.swing.JTextField();
        L7 = new javax.swing.JLabel();
        Temperature = new javax.swing.JTextField();
        PrintEvent = new javax.swing.JCheckBox();
        LaserEnergy = new javax.swing.JCheckBox();
        Energy = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        TestList.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Рабочая зона", "Рандом", "Линия", "Квадрат", "Квадрат2" }));
        getContentPane().add(TestList);
        TestList.setBounds(340, 40, 150, 25);

        Start.setText("Старт");
        Start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StartActionPerformed(evt);
            }
        });
        getContentPane().add(Start);
        Start.setBounds(340, 180, 80, 23);

        CmdList.setBackground(new java.awt.Color(240, 240, 240));
        CmdList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                CmdListItemStateChanged(evt);
            }
        });
        CmdList.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                CmdListCaretPositionChanged(evt);
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
            }
        });
        getContentPane().add(CmdList);
        CmdList.setBounds(10, 10, 300, 20);

        jButton1.setText("Выполнить");
        jButton1.setMaximumSize(new java.awt.Dimension(89, 30));
        jButton1.setMinimumSize(new java.awt.Dimension(89, 30));
        jButton1.setPreferredSize(new java.awt.Dimension(89, 25));
        jButton1.setRequestFocusEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(200, 40, 110, 25);

        LY1.setText("Y1");
        getContentPane().add(LY1);
        LY1.setBounds(160, 130, 30, 14);

        X1.setText("-40");
        getContentPane().add(X1);
        X1.setBounds(180, 90, 50, 25);

        Y1.setText("40");
        getContentPane().add(Y1);
        Y1.setBounds(180, 120, 50, 25);

        LX1.setText("X1");
        getContentPane().add(LX1);
        LX1.setBounds(160, 100, 30, 14);

        LY2.setText("Y2");
        getContentPane().add(LY2);
        LY2.setBounds(160, 190, 30, 14);

        X2.setText("-40");
        getContentPane().add(X2);
        X2.setBounds(180, 150, 50, 25);

        Y2.setText("40");
        getContentPane().add(Y2);
        Y2.setBounds(180, 180, 50, 25);

        LX2.setText("X2");
        getContentPane().add(LX2);
        LX2.setBounds(160, 160, 30, 14);

        P1.setText("0");
        getContentPane().add(P1);
        P1.setBounds(250, 90, 50, 25);

        P2.setText("0");
        getContentPane().add(P2);
        P2.setBounds(250, 120, 50, 25);

        P3.setText("0");
        getContentPane().add(P3);
        P3.setBounds(250, 150, 50, 25);

        P4.setText("0");
        getContentPane().add(P4);
        P4.setBounds(250, 180, 50, 25);

        PAR.setText("Параметры");
        getContentPane().add(PAR);
        PAR.setBounds(250, 70, 70, 14);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Печать");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(360, 300, 80, 14);

        XY.setText("Координаты");
        getContentPane().add(XY);
        XY.setBounds(170, 70, 80, 14);

        sourceOn.setLabel("Вкл.сеть");
        sourceOn.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                sourceOnItemStateChanged(evt);
            }
        });
        getContentPane().add(sourceOn);
        sourceOn.setBounds(10, 250, 75, 20);
        getContentPane().add(jSeparator1);
        jSeparator1.setBounds(20, 282, 350, 0);

        guideOn.setLabel("Указатель");
        guideOn.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                guideOnItemStateChanged(evt);
            }
        });
        getContentPane().add(guideOn);
        guideOn.setBounds(10, 280, 86, 20);

        emissionOn.setLabel("Эмиссия");
        emissionOn.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                emissionOnItemStateChanged(evt);
            }
        });
        getContentPane().add(emissionOn);
        emissionOn.setBounds(10, 310, 77, 20);

        modulationOn.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        modulationOn.setForeground(new java.awt.Color(255, 0, 0));
        modulationOn.setLabel("Модуляция");
        modulationOn.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                modulationOnItemStateChanged(evt);
            }
        });
        getContentPane().add(modulationOn);
        modulationOn.setBounds(10, 340, 93, 20);

        laserErr.setLabel("Ошибка");
        laserErr.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                laserErrItemStateChanged(evt);
            }
        });
        getContentPane().add(laserErr);
        laserErr.setBounds(10, 370, 71, 20);

        jLabel4.setText("Шаг");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(510, 160, 70, 14);

        PrinterState.setEditable(false);
        getContentPane().add(PrinterState);
        PrinterState.setBounds(430, 240, 200, 25);

        NoLayer.setSelected(true);
        NoLayer.setText("Пропускать засыпку");
        NoLayer.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                NoLayerItemStateChanged(evt);
            }
        });
        getContentPane().add(NoLayer);
        NoLayer.setBounds(360, 380, 140, 23);

        Step.setText("0.05");
        getContentPane().add(Step);
        Step.setBounds(590, 150, 40, 25);

        Quadrant.setText("1");
        getContentPane().add(Quadrant);
        Quadrant.setBounds(590, 90, 40, 25);

        NLines.setText("5");
        NLines.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NLinesActionPerformed(evt);
            }
        });
        getContentPane().add(NLines);
        NLines.setBounds(590, 120, 40, 25);

        L2.setText("Шаг  - деталь (0.01 мм)");
        getContentPane().add(L2);
        L2.setBounds(120, 285, 130, 14);

        jLabel6.setText("Номер клетки");
        getContentPane().add(jLabel6);
        jLabel6.setBounds(510, 100, 80, 14);

        jLabel7.setText("Повторений");
        getContentPane().add(jLabel7);
        jLabel7.setBounds(340, 100, 90, 14);

        RunTest.setText("Выполнить");
        RunTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RunTestActionPerformed(evt);
            }
        });
        getContentPane().add(RunTest);
        RunTest.setBounds(510, 42, 120, 23);
        getContentPane().add(jSeparator2);
        jSeparator2.setBounds(20, 282, 350, 0);
        getContentPane().add(jSeparator4);
        jSeparator4.setBounds(10, 222, 630, 0);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Тест");
        getContentPane().add(jLabel9);
        jLabel9.setBounds(340, 20, 50, 14);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Лазер");
        getContentPane().add(jLabel10);
        jLabel10.setBounds(10, 230, 100, 14);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("Тест линий 5x5");
        getContentPane().add(jLabel11);
        jLabel11.setBounds(510, 20, 120, 14);

        M4Step.setText("5");
        getContentPane().add(M4Step);
        M4Step.setBounds(260, 280, 40, 25);

        MicroStep.setText("20");
        getContentPane().add(MicroStep);
        MicroStep.setBounds(260, 250, 40, 25);

        M3Step.setText("5");
        getContentPane().add(M3Step);
        M3Step.setBounds(260, 310, 40, 25);

        jLabel12.setText("Процесс");
        getContentPane().add(jLabel12);
        jLabel12.setBounds(460, 305, 50, 14);

        L1.setText("Скорость прожига (мм/с)");
        getContentPane().add(L1);
        L1.setBounds(120, 255, 140, 14);

        L3.setText("Лазер  выкл.  (мкс)");
        getContentPane().add(L3);
        L3.setBounds(120, 375, 120, 14);

        XX1.setText("-20");
        XX1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                XX1ActionPerformed(evt);
            }
        });
        getContentPane().add(XX1);
        XX1.setBounds(360, 120, 50, 25);

        jLabel15.setText("X1");
        getContentPane().add(jLabel15);
        jLabel15.setBounds(340, 130, 20, 14);

        YY1.setText("-20");
        YY1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                YY1ActionPerformed(evt);
            }
        });
        getContentPane().add(YY1);
        YY1.setBounds(440, 120, 50, 25);

        jLabel16.setText("Y1");
        getContentPane().add(jLabel16);
        jLabel16.setBounds(420, 130, 20, 14);

        XX2.setText("20");
        XX2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                XX2ActionPerformed(evt);
            }
        });
        getContentPane().add(XX2);
        XX2.setBounds(360, 150, 50, 25);

        jLabel17.setText("X2");
        getContentPane().add(jLabel17);
        jLabel17.setBounds(340, 160, 20, 14);

        YY2.setText("20");
        getContentPane().add(YY2);
        YY2.setBounds(440, 150, 50, 25);

        jLabel18.setText("Y2");
        getContentPane().add(jLabel18);
        jLabel18.setBounds(420, 160, 20, 14);

        Printed.setEditable(false);
        getContentPane().add(Printed);
        Printed.setBounds(560, 270, 70, 25);

        jLabel19.setText("Состояние");
        getContentPane().add(jLabel19);
        jLabel19.setBounds(360, 250, 70, 14);

        L4.setText("Мощность  лазера  (%)");
        getContentPane().add(L4);
        L4.setBounds(120, 400, 130, 20);

        FreeBuf.setEditable(false);
        getContentPane().add(FreeBuf);
        FreeBuf.setBounds(430, 270, 50, 25);

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel21.setText("Прямое исполнение команд");
        getContentPane().add(jLabel21);
        jLabel21.setBounds(10, 40, 200, 14);

        ResetPrint.setSelected(true);
        ResetPrint.setText("Отменить печать");
        getContentPane().add(ResetPrint);
        ResetPrint.setBounds(340, 70, 120, 20);

        WorkTime.setText("0");
        getContentPane().add(WorkTime);
        WorkTime.setBounds(440, 90, 50, 25);

        jLabel22.setText("Линий");
        getContentPane().add(jLabel22);
        jLabel22.setBounds(510, 130, 70, 14);

        Print.setText("Печать");
        Print.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PrintActionPerformed(evt);
            }
        });
        getContentPane().add(Print);
        Print.setBounds(360, 320, 90, 23);

        Power.setText("10");
        getContentPane().add(Power);
        Power.setBounds(260, 400, 40, 25);

        jLabel23.setText("Буфер");
        getContentPane().add(jLabel23);
        jLabel23.setBounds(360, 275, 70, 14);

        Suspend.setText("Приостановить");
        Suspend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SuspendActionPerformed(evt);
            }
        });
        getContentPane().add(Suspend);
        Suspend.setBounds(360, 410, 130, 23);

        CancelPrint.setBackground(new java.awt.Color(204, 51, 0));
        CancelPrint.setText("Прервать");
        CancelPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelPrintActionPerformed(evt);
            }
        });
        getContentPane().add(CancelPrint);
        CancelPrint.setBounds(520, 410, 110, 23);

        MotorsControl.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        MotorsControl.setText("Моторы");
        MotorsControl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MotorsControlActionPerformed(evt);
            }
        });
        getContentPane().add(MotorsControl);
        MotorsControl.setBounds(10, 80, 90, 23);
        getContentPane().add(jSeparator5);
        jSeparator5.setBounds(330, 22, 0, 200);

        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);
        getContentPane().add(jSeparator6);
        jSeparator6.setBounds(330, 12, 2, 210);

        jSeparator7.setOrientation(javax.swing.SwingConstants.VERTICAL);
        getContentPane().add(jSeparator7);
        jSeparator7.setBounds(498, 12, 2, 210);

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel20.setText("Параметры печати");
        getContentPane().add(jLabel20);
        jLabel20.setBounds(120, 230, 130, 14);

        jSeparator8.setOrientation(javax.swing.SwingConstants.VERTICAL);
        getContentPane().add(jSeparator8);
        jSeparator8.setBounds(110, 220, 10, 240);

        jSeparator9.setOrientation(javax.swing.SwingConstants.VERTICAL);
        getContentPane().add(jSeparator9);
        jSeparator9.setBounds(350, 220, 10, 240);

        OneLayerMode.setText("Пауза после слоя");
        getContentPane().add(OneLayerMode);
        OneLayerMode.setBounds(360, 350, 150, 30);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Принтер");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(360, 230, 110, 14);

        Current.setEditable(false);
        getContentPane().add(Current);
        Current.setBounds(520, 330, 110, 25);

        jLabel24.setText("Напечатано");
        getContentPane().add(jLabel24);
        jLabel24.setBounds(490, 275, 70, 14);

        jLabel25.setText("Слой");
        getContentPane().add(jLabel25);
        jLabel25.setBounds(470, 335, 40, 14);

        PrintingState.setEditable(false);
        getContentPane().add(PrintingState);
        PrintingState.setBounds(520, 300, 110, 25);
        getContentPane().add(jSeparator3);
        jSeparator3.setBounds(440, 422, 200, 0);
        getContentPane().add(jSeparator10);
        jSeparator10.setBounds(10, 220, 620, 10);

        SetManual.setText("Ручная установка параметров");
        SetManual.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                SetManualItemStateChanged(evt);
            }
        });
        getContentPane().add(SetManual);
        SetManual.setBounds(120, 430, 210, 23);
        getContentPane().add(jSeparator11);
        jSeparator11.setBounds(0, 460, 640, 10);

        SetParams.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SetParamsActionPerformed(evt);
            }
        });
        getContentPane().add(SetParams);
        SetParams.setBounds(310, 250, 30, 140);

        SetPower.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SetPowerActionPerformed(evt);
            }
        });
        getContentPane().add(SetPower);
        SetPower.setBounds(310, 395, 30, 30);

        jSeparator13.setOrientation(javax.swing.SwingConstants.VERTICAL);
        getContentPane().add(jSeparator13);
        jSeparator13.setBounds(330, 12, 2, 210);

        jSeparator14.setOrientation(javax.swing.SwingConstants.VERTICAL);
        getContentPane().add(jSeparator14);
        jSeparator14.setBounds(150, 62, 30, 160);

        Correct.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        Correct.setText("Коррекция геом.");
        Correct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CorrectActionPerformed(evt);
            }
        });
        getContentPane().add(Correct);
        Correct.setBounds(10, 110, 130, 23);

        Oxygen.setText("Кислород");
        Oxygen.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                OxygenItemStateChanged(evt);
            }
        });
        getContentPane().add(Oxygen);
        Oxygen.setBounds(360, 470, 90, 23);

        OxygenData.setText("---");
        getContentPane().add(OxygenData);
        OxygenData.setBounds(230, 470, 120, 25);

        LineStep.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        LineStep.setText("Край линии");
        LineStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LineStepActionPerformed(evt);
            }
        });
        getContentPane().add(LineStep);
        LineStep.setBounds(10, 140, 130, 23);

        ChangeLayer.setText("Сменить слой");
        ChangeLayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChangeLayerActionPerformed(evt);
            }
        });
        getContentPane().add(ChangeLayer);
        ChangeLayer.setBounds(520, 370, 110, 23);

        L5.setText("Шаг - бункер  (0.01 мм)");
        getContentPane().add(L5);
        L5.setBounds(120, 315, 130, 14);

        L6.setText("Лазер  вкл.  (мкс)");
        getContentPane().add(L6);
        L6.setBounds(120, 345, 120, 14);

        LaserOffDelay.setText("10");
        getContentPane().add(LaserOffDelay);
        LaserOffDelay.setBounds(260, 370, 40, 25);

        LaserOnDelay.setText("10");
        getContentPane().add(LaserOnDelay);
        LaserOnDelay.setBounds(259, 340, 40, 25);

        L7.setText("Температура ");
        getContentPane().add(L7);
        L7.setBounds(10, 400, 80, 20);
        getContentPane().add(Temperature);
        Temperature.setBounds(10, 425, 50, 25);

        PrintEvent.setText("Отображать печать");
        PrintEvent.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                PrintEventItemStateChanged(evt);
            }
        });
        getContentPane().add(PrintEvent);
        PrintEvent.setBounds(510, 470, 140, 23);

        LaserEnergy.setText("Энергия (дж)");
        getContentPane().add(LaserEnergy);
        LaserEnergy.setBounds(120, 470, 100, 23);
        getContentPane().add(Energy);
        Energy.setBounds(10, 470, 100, 25);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TestQuadrat() throws UNIException{
        int nLines=10;
        double dd=1./nLines;
        int i=0;
        for(double y=-0.9; y<=0.9 && isRun(); y+=dd){
            if (i%2==0)
                sendLine(new STLLine(new STLPoint2D(-0.9,y),new STLPoint2D(0.9,y)));
            else
                sendLine(new STLLine(new STLPoint2D(0.9,y),new STLPoint2D(-0.9,y)));
            i++;
            }
        }
    private void TestQuadrat2() throws UNIException{
        int nLines=10;
        double dd=1./nLines;
        int i=0;
        for(double y=-0.9; y<=0.9 && isRun(); y+=dd){
            if (i%2==0)
                sendLine(new STLLine(new STLPoint2D(-0.9,y),new STLPoint2D(0.9,y)));
            else
                sendLine(new STLLine(new STLPoint2D(0.9,y),new STLPoint2D(-0.9,y)));
            i++;
            }
        }
  
    private void TestWorkZone() throws UNIException{
        double sz = generator.getWorkSize();
        sendLine(new STLLine(new STLPoint2D(-sz,-sz),new STLPoint2D(-sz,sz)));
        sendLine(new STLLine(new STLPoint2D(-sz,sz),new STLPoint2D(sz,sz)));
        sendLine(new STLLine(new STLPoint2D(sz,sz),new STLPoint2D(sz,-sz)));
        sendLine(new STLLine(new STLPoint2D(sz,-sz),new STLPoint2D(-sz,-sz)));
        sendLine(new STLLine(new STLPoint2D(-sz,-sz),new STLPoint2D(sz,sz)));
        sendLine(new STLLine(new STLPoint2D(-sz,sz),new STLPoint2D(sz,-sz)));
        sendLine(new STLLine(new STLPoint2D(-sz,-sz),new STLPoint2D(sz,sz)));
        sendLine(new STLLine(new STLPoint2D(-sz,sz),new STLPoint2D(sz,-sz)));
        }
    private double rand(){
        return Math.random()*2-1;
        }

    private void LaserVisualisation(int data){
        /* Значения битов */
        /* 7 = 1 снята блокировка 1 */
        /* 6 = 1 снята блокировка 2 */
        /* 5 = 1 подается мощность  */
        /* 4 = 1 система стартовала  */
        /* 3 = 1 модуляция включена аппаратно (включается на короткое время во время прожига) */
        /* 2 = 1 эмиссия включена   */
        /* 1 = 1 указатель включен */
        /* 0 = 1 есть ошибки */
        if((data & 0x20)!=0){ //лазер включен
         setPowerVisible(true);   
         sourceOn.setState(true);
         emissionOn.setEnabled(true);
         modulationOn.setEnabled(true); 
         guideOn.setEnabled(true);
         laserErr.setEnabled(true);
        }else{
         setPowerVisible(false);   
         sourceOn.setState(false);
         emissionOn.setEnabled(false);
         modulationOn.setEnabled(false); 
         guideOn.setEnabled(false);
         laserErr.setEnabled(false);         
        }
        if((data & 0x02)!=0){  //эмиссия включена
             emissionOn.setState(true);        
        }else{
             emissionOn.setState(false);        
        }
        if((data & 0x04)!=0){  //эмиссия включена
             guideOn.setState(true);        
        }else{
             guideOn.setState(false);        
        }
        if((data & 0x01)!=0){  //эмиссия включена
             laserErr.setState(true);        
        }else{
             laserErr.setState(false);        
            }        
 
        }
    
    private void StartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StartActionPerformed
        int code = TestList.getSelectedIndex();
        if (isRun()){
            Start.setText("Старт");
            printerThread.interrupt();
            setRun(false);
            }
        else{
            generator = new USBCommandGenerator(usb,notify);    // Получает уже инициализирвоанный
            try {
                generator.start();
                } catch (UNIException e) {
                    notify.notify(Values.error,e.toString());
                    closePrint();
                    return;
                    }
            Start.setText("Стоп");
            setRun(true);
            printerThread = new Thread(()->{
                long startTime = new Date().getTime();
                int delay = Integer.parseInt(WorkTime.getText());
                //--------------------------------------------------------------
                double x1 = Double.parseDouble(XX1.getText());
                double y1 = Double.parseDouble(YY1.getText());
                double x2 = Double.parseDouble(XX2.getText());
                double y2 = Double.parseDouble(YY2.getText());
                STLLine line = new STLLine(new STLPoint2D(x1,y1),new STLPoint2D(x2,y2));
                STLPoint2D p0 = new STLPoint2D(rand(),rand());
                STLPoint2D p1;
                //--------------------------------------------------------------
                while (isRun()){
                    try {
                        try {
                            Thread.sleep(1);
                            } catch (InterruptedException e) {
                                setRun(false);
                                }
                        switch(code){
                            case 0: TestWorkZone();break;
                            case 3: TestQuadrat();break;
                            case 4: TestQuadrat2();break;
                            case 1: p1 = new STLPoint2D(rand(),rand());
                                    sendLine(new STLLine(p0,p1));
                                    p0=p1;
                                    break;
                            case 2: sendLine(line); break;
                            }
                        } catch (UNIException e) {
                            setRun(false);
                            notify.notify(Values.error,e.toString());
                            }
                        if (delay > 0){
                            delay--;
                            if (delay==0) setRun(false);
                            }    
                      }
                    notify.log("Время теста"+(new Date().getTime()-startTime)/1000);
                    java.awt.EventQueue.invokeLater(()->{ 
                        if (ResetPrint.isSelected()){
                            usb.StopPrint(back);
                            }
                        closePrint();
                        Start.setText("Старт");
                        });
                    });
            printerThread.start();
            }
    }//GEN-LAST:event_StartActionPerformed


    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       String cc = (String)CmdList.getSelectedItem();
       try {
            Command xx = factoryFull.getCommand(cc);
            if (xx==null) {
                notify.log( "Не найдена команда:" + CmdList.getSelectedItem());
                return;
                }
            notify.log( "Ручное исполнение команды:" + CmdList.getSelectedItem());            
            int sign = xx.signature();
            double x1,x2,y1,y2;
            if (xx.code()==USBCodes.MarkingMicroStepsMarkInt){
                int msf = Integer.parseInt(P1.getText());
                float ff = (float)(msf*0.000001/4.);
                xx.value(ff);
                }
            else{
                switch (sign){
                case Command.SIGN_INT: xx.value(Integer.parseInt(P1.getText())); break;
                case Command.SIGN_FLOAT: xx.value(Float.parseFloat(P1.getText())); break;
                case Command.SIGN_POINT:
                    x1 = Double.parseDouble(X1.getText());
                    y1 = Double.parseDouble(Y1.getText());
                    xx.value(new STLPoint2D(x1,y1)); 
                    break;
                case Command.SIGN_LINE:
                    x1 = Double.parseDouble(X1.getText());
                    y1 = Double.parseDouble(Y1.getText());
                    x2 = Double.parseDouble(X2.getText());
                    y2 = Double.parseDouble(Y2.getText());
                    xx.value(new STLLine(new STLPoint2D(x1,y1),new STLPoint2D(x2,y2))); 
                    break;
                case Command.SIGN_INTLIST:
                    CommandIntList cmd = (CommandIntList)xx;
                    if (xx.dataSize()>=1) cmd.valueIdx(Integer.parseInt(P1.getText()),0);
                    if (xx.dataSize()>=2) cmd.valueIdx(Integer.parseInt(P2.getText()),1);
                    if (xx.dataSize()>=3) cmd.valueIdx(Integer.parseInt(P3.getText()),2);
                    if (xx.dataSize()>=4) cmd.valueIdx(Integer.parseInt(P4.getText()),3);
                    break;
                    }}
                int out[] = xx.toIntArray();
                new Thread(()->{                // В потоке 
                    usb.oneCommand(out,back);
                    askState();
                    }).start();
        } catch (Exception e) { notify.log("Ошибка генерации команды: "+cc+e.toString()); }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void CmdListCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_CmdListCaretPositionChanged
        setManualCommandVisible();
    }//GEN-LAST:event_CmdListCaretPositionChanged

    private void closeWindow(){
        try {
            closing=true;
            onClose();
            synchronized (this){
                //stateLoop.interrupt();          // Дернуть опрос состояния
                if (printerThread!=null)
                    printerThread.interrupt();
                usb.close();
                }
            } catch (UNIException ex) { notify.notify(Values.error,ex.toString()); }        
        }
    
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
            int state = ws().printing();
            if (state == PStateWorking){
                log("Перед выходом остановите печать");
                return;
                }
            setPowerOnOff(false);
            setLaserEnergyOff();
            closeWindow();
    }//GEN-LAST:event_formWindowClosing

    private void CmdListItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_CmdListItemStateChanged
        setManualCommandVisible();
    }//GEN-LAST:event_CmdListItemStateChanged
    
    private void sourceOnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sourceOnItemStateChanged
        if (disabledOnPrint()) return;
        boolean on = (evt.getStateChange()!=2);
        sendCommand(new CommandIntList(USBCodes.BeamPowerOn,on ? 1 : 0));
        guideOn.setEnabled(on);
        emissionOn.setEnabled(on);
        modulationOn.setEnabled(on);
        laserErr.setEnabled(on);
        setPowerVisible(on);
        askState();
    }//GEN-LAST:event_sourceOnItemStateChanged
    
    boolean disabledOnPrint(){
        boolean dis = ws().printing()==PStateWorking;
        if (dis)
            notify.log("Запрещено вкл/выкл.при печати");
        return dis;
        }
    
    private void guideOnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_guideOnItemStateChanged
        if (disabledOnPrint()) return;
        boolean on = (evt.getStateChange()!=2);
        sendCommand(new CommandIntList(USBCodes.BeamSetGuide,on ? 1 : 0));
        askState();

    }//GEN-LAST:event_guideOnItemStateChanged

    private void emissionOnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_emissionOnItemStateChanged
        if (disabledOnPrint()) return;
        boolean on = (evt.getStateChange()!=2);
        sendCommand(new CommandIntList(USBCodes.BeamEmisOn,on ? 1 : 0));
        askState();

    }//GEN-LAST:event_emissionOnItemStateChanged

    private void modulationOnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_modulationOnItemStateChanged
        if (disabledOnPrint()) return;
        boolean on = (evt.getStateChange()!=2);
        sendCommand(new CommandIntList(USBCodes.BeamModOn,on ? 1 : 0));
        askState();
    }//GEN-LAST:event_modulationOnItemStateChanged

    private void laserErrItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_laserErrItemStateChanged
        if (disabledOnPrint()) return;
        boolean on = (evt.getStateChange()!=2);
        sendCommand(new CommandIntList(USBCodes.BeamResetErr,on ? 1 : 0));
        askState();
    }//GEN-LAST:event_laserErrItemStateChanged

    private void NoLayerItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_NoLayerItemStateChanged
        noLayerMode();
    }//GEN-LAST:event_NoLayerItemStateChanged

    private void NLinesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NLinesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NLinesActionPerformed

    private void RunTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RunTestActionPerformed
        if (isRun()){
            notify.info("Тест уже запущен");
            return;
            }
        int NK=5;
        generator = new USBCommandGenerator(usb,notify);        // Получает уже инициализированный
        //try {
        //    synchronized (this) {
        //        generator.init();
        //        }
        //    } catch (UNIException ex) {
        //        notify.notify(Values.error,ex.toString());
        //        return;
        //        }
        int idx = Integer.parseInt(Quadrant.getText());
        int nlines = Integer.parseInt(NLines.getText());
        double dd = 2./NK;
        int xidx = (idx-1)%NK;
        int yidx = (idx-1)/NK;
        double x0 = -1 + dd*xidx+dd/10;
        double y0 = -1 + dd*yidx+dd/10;
        double step = Double.parseDouble(Step.getText());
        setRun(true);
        new Thread(()->{
             try {
                 synchronized (this){
                    generator.start();
                    }
                 for (int i=0;i<nlines;i++){
                     double x1 = x0+dd*0.8;
                     double yy = y0+i*step;
                     sendLine(new STLLine(new STLPoint2D(x0,yy),new STLPoint2D(x1,yy)));
                     askState();
                     }
                 closePrint();
                 askState();
                 setRun(false);
                 Quadrant.setText(""+(idx+1));
                 notify.info("Тест завершен");
                 } catch (UNIException e) {
                     notify.notify(Values.error,e.toString());
                     setRun(false);
                 }
            }).start();
    }//GEN-LAST:event_RunTestActionPerformed
   
    private void XX2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_XX2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_XX2ActionPerformed

    private void YY1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_YY1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_YY1ActionPerformed

    private void cancelPrint(){
        try {
            operate.cancelPrint();
            } catch (UNIException ex) { notify.notify(Values.error,ex.toString()); }            
        notify.log("отмена печати");
        printThread=null;
        ws().printing(PStateCancel);
        }
    private boolean printDisabled(){
            if (WorkSpace.ws().data()==null){
                log("файл slm3d не открыт");
                return true;
                }
            if (printThread!=null){
                log("повторный запуск печати");
                return true;
                } 
            if (layerCount >=WorkSpace.ws().data().size()){
                log("недопустимый номер слоя "+layerCount);
                return  true;
                }
            return false;
            }
    private void setLaserEnergyOn(){
        if (LaserEnergy.isSelected()){
            power.laserPoverCycleOnOff(true);
            laserEnergyCalc=true;            
            }
        }
    private void setLaserEnergyOff(){
        if (laserEnergyCalc){
            power.laserPoverCycleOnOff(false);
            laserEnergyCalc=false;            
            }
        }
    private long startTime;
    private void printLayer(final boolean resume){
        if (printDisabled())
            return;
        try{
            if (printerState == USBCodes.STATE_STANDBY){        // Первый слой после простоя
                ws().layerCount(layerCount);
                operate.startUSBPrint(usb);
                }
            setLayerData();           
            setPrintParams();
            setLaserPower();
            setLaserEnergyOn();
            ws().printing(PStateWorking);
            startTime = new Date().getTime();
            printThread = new Thread(()->{
                try{
                   if (!operate.copyLayerToUSB(synch,usb,data, resume ? lineCount : -1)){
                        printThread=null;
                        return;
                        }
                   } catch (UNIException ee){ 
                        notify.notify(Values.error,ee.toString());
                        cancelPrint();
                        return;
                        }
                //------------- ЖДАТЬ ПЕРЕХОДА В СОТОЯНИЕ "ГОТОВ" и выполнить в GUI
                usb.runWhenState(USBCodes.STATE_WAITFORDATA, USBCodes.STATE_PRINT,30,notify,()->{
                    saveLineCount=true;
                    usb.getLineCount(back);
                    usb.oneCommand(new CommandIntList(USBCodes.GetMotorStatus,3).toIntArray(),backMotors);
                    usb.oneCommand(new CommandIntList(USBCodes.GetMotorStatus,4).toIntArray(),backMotors);
                    ws().sendEvent(Motors);
                    log("Напечатан слой "+data.label()+": "+Utils.toTimeString((new Date().getTime()-startTime)/1000)+" ("+data.rezult().printTime()+")");
                    layerCount++;
                    printThread=null;
                    log(power.laserPowerData().toString());
                    setLaserEnergyOff();
                    if (layerCount==WorkSpace.ws().data().size()){
                        log("Печать завершена");
                        layerCount=0;
                        setLayerData();
                        ws().printing(PStateStandBy);
                        try {
                            operate.finishUSBPrint();
                            } catch (UNIException ee){ notify.notify(Values.error,ee.toString()); }                        
                        return;
                        }
                    ws().layerCount(layerCount);
                    ws().global().global.LayerCount.setVal(layerCount);
                    ws().saveSettings();
                    setLayerData();   
                    if (!SetManual.isSelected())
                        viewPrintParams();                
                    if (!OneLayerMode.isSelected() && layerCount<WorkSpace.ws().data().size())
                        printLayer(false);
                    else
                        ws().printing(PStateFullLayer);
                    });
                });
            printThread.start();        
            } catch (UNIException ee){ notify.notify(Values.error,ee.toString()); }        
        }
    
    public void restoreLayerCount(){
        if (ws().printing()==PStateStandBy){
            layerCount=0;
            }
        if (ws().printing()==PStateFullLayer){
            restoreFullLayer();
            }
        if (ws().printing()==PStateSuspend){
            restoreResumed();
            }
        }
    
    private void PrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PrintActionPerformed
        if (ws().printing()==PStateStandBy){
            layerCount=0;
            }
        if (ws().printing()==PStateFullLayer){
            restoreFullLayer();
            }
        if (!SetManual.isSelected())
            viewPrintParams();
        printLayer(false);
    }//GEN-LAST:event_PrintActionPerformed

    private void restoreFullLayer(){
        layerCount = ws().global().global.LayerCount.getVal();
        ws().layerCount(layerCount);
        lineCount=0;
        log("Восстановление номера слоя "+(layerCount+1));
        setLayerData();        
        }
    private void restoreResumed(){
        layerCount = ws().global().global.CurrentLayer.getVal();
        lineCount =  ws().global().global.CurrentLine.getVal();
        usb.setLineCount(layerCount,lineCount);
        ws().layerCount(layerCount);
        log("Восстановление номера слоя/линии "+(layerCount+1) +"/"+lineCount);
        setLayerData();        
        }
    private void setLayerData(){
        data = null;
        printSettings = null;
        Current.setText("");
        if (ws().data() == null || layerCount>=ws().data().size()){
            log("Нет данных слайсинга или недопустимое количество слоев "+layerCount);
            return;
            }
        data = ws().data().get(layerCount); 
        printSettings = ws().local();
        if (data!=null && data.printSettings()!=null)
            printSettings = data.printSettings();
        Current.setText(data.label());        
        }
    
    private void setLaserPower(){
        try {
            boolean manual = SetManual.isSelected();
            double value = manual  ? Double.parseDouble(Power.getText()) : printSettings.pulses.LaserPumpPower.getVal();
            power.setVolume(value);
            } catch (Exception ee){ 
                notify.notify(Values.error, "Мощность лазера: формат вещественного числа");
                }
        }
    private void SuspendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SuspendActionPerformed
        if (ws().printing()==PStateWorking){
            usb.SuspendPrint(back);
            saveLineCount=true;
            usb.getLineCount(back);
            notify.log("Печать приостановлена");
            synch.pause(true);
            ws().printing(PStateSuspend);
            try {
                usb.saveLayerLineCounts();
                } catch (UNIException ex) { 
                    notify.notify(Values.error, "Ошибка сохранения данных останова: "+ex.toString());
                    }
            }
        else
        if (ws().printing()==PStateSuspend){
            if (printerState == USBCodes.STATE_STANDBY){
                restoreResumed();
                printLayer(true);
                }
            else{
                synch.pause(false);
                usb.ResumePrint(back);
                }
            ws().printing(PStateWorking);
            notify.log("Печать возобновлена");
            }
    }//GEN-LAST:event_SuspendActionPerformed
    
    private void CancelPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelPrintActionPerformed
        final boolean xx = ws().printing() == PStateWorking || ws().printing() == PStateSuspend;
        new OK(getBounds(),xx ? "Прервать печать" : "Сбросить печать",()->{
            if (xx){ 
                synch.finish();
                usb.cancelWait();       // Обрывает цикл ожидания состояния в протоколе
                setLayerData();        
                ws().printing(PStateCancel);
                }
            Utils.runAfterDelay(2, ()->{
                ws().global().global.LayerCount.setVal(0);
                ws().global().global.CurrentLayer.setVal(-1);
                ws().global().global.CurrentLine.setVal(0);
                layerCount=0;
                lineCount=0;
                setLayerData();        
                usb.CancelPrint(back);
                ws().printing(PStateStandBy);
                askState();
                if (xx){
                    Utils.runAfterDelay(2, ()->{            
                        notify.log("Прерывание печати, перезапуск контроллера\n ЗАКРЫТЬ/ОТКРЫТЬ ФОРМУ ПЕЧАТИ");
                        usb.HardReset(back);
                        });
                    }
                });        
        });
    }//GEN-LAST:event_CancelPrintActionPerformed

    private void XX1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_XX1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_XX1ActionPerformed
    
    private void MotorsControlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MotorsControlActionPerformed
        new MotorsConsole(usb,back,notify);
    }//GEN-LAST:event_MotorsControlActionPerformed
    private void setPrintParamsVisible(){
        boolean print = ws().printing() == PStateWorking;
        SetManual.setVisible(!print);
        boolean manual = SetManual.isSelected();
        SetParams.setVisible(!print && manual);
        SetPower.setVisible(!print && manual && laserOn);
        }
    private void viewPrintParams(){
        if (printSettings==null) return;
        MicroStep.setText(""+printSettings.marking.MicroStepsMark.getVal());
        M3Step.setText(""+printSettings.control.NextLayerMovingM3Step.getVal());
        M4Step.setText(""+printSettings.control.NextLayerMovingM4Step.getVal());
        Power.setText(""+printSettings.pulses.LaserPumpPower.getVal());
        LaserOnDelay.setText(""+printSettings.delays.MovingPenJumpDelay.getVal());
        LaserOffDelay.setText(""+printSettings.delays.MovingPenMarkDelay.getVal());
        }
    
    private void setPrintParams(){
        if (ws().printing() == PStateWorking) return;
        setLayerData();
        sendCommand(new CommandIntList(USBCodes.ChangeLayerMotorsEnable,NoLayer.isSelected() ? 0 : 1));                
        boolean manual = SetManual.isSelected();
        if (!manual && printSettings==null) return;
        try{
            int msf = manual ? Integer.parseInt(MicroStep.getText()) : printSettings.marking.MicroStepsMark.getVal();
            float ff = (float)(msf*0.000001/4.);
            notify.log( String.format("Команда: установка микрошага %3d [%8.6f]",msf,ff));
            sendCommand(new CommandInt(USBCodes.MarkingMicroStepsMarkInt,Float.floatToIntBits(ff)));
            }catch(Exception ee){ 
                notify.notify(Values.error,"Скорость прожига: формат целого числа ????");
                } 
        try{
            int v3=manual ? Integer.parseInt(M3Step.getText()) : printSettings.control.NextLayerMovingM3Step.getVal();
            notify.log( "Команда:установка шага бункера "+v3);
            sendCommand(new CommandInt(USBCodes.ControlNextLayerMovingM3Step,v3));
            }catch(Exception ee){ 
                notify.notify(Values.error,"Установка шага бункера: формат целого числа ????");
                }  
        try{
            int v4=manual ? Integer.parseInt(M4Step.getText()) : printSettings.control.NextLayerMovingM4Step.getVal();
            notify.log( "Команда:установка шага детали "+v4);
            sendCommand(new CommandInt(USBCodes.ControlNextLayerMovingM4Step,v4));
            }catch(Exception ee){ 
                notify.notify(Values.error,"Установка шага детали: формат целого числа ????");
                }  
        try{
            int v4=manual ? Integer.parseInt(LaserOnDelay.getText()) : printSettings.delays.MovingPenJumpDelay.getVal();
            notify.log( "Команда:задержка включения лазера "+v4);
            sendCommand(new CommandInt(USBCodes.DelaysMovingPenJumpDelay,v4));
            }catch(Exception ee){ 
                notify.notify(Values.error,"Задержка включения лазера: формат целого числа ????");
                }     
        try{
            int v4=manual ? Integer.parseInt(LaserOffDelay.getText()) : printSettings.delays.MovingPenMarkDelay.getVal();
            notify.log( "Команда:задержка выключения лазера "+v4);
            sendCommand(new CommandInt(USBCodes.DelaysMovingPenMarkDelay,v4));
            }catch(Exception ee){ 
                notify.notify(Values.error,"Задержка выключения лазера: формат целого числа ????");
                }     
    }
    
    
    private void SetParamsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SetParamsActionPerformed
        setPrintParams();
    }//GEN-LAST:event_SetParamsActionPerformed

    private void SetPowerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SetPowerActionPerformed
        setLaserPower();
    }//GEN-LAST:event_SetPowerActionPerformed

    private void SetManualItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_SetManualItemStateChanged
        setPrintParamsVisible();
    }//GEN-LAST:event_SetManualItemStateChanged

    private void CorrectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CorrectActionPerformed
        if (ws().printing()!=PStateStandBy){
            notify.log("Коррекция геометрии только в состоянии приостановлен");
            return;
            }
        String ss = getInputFileName("Коррекция геометрии","bin",false);
        if (ss==null) return;
        try{
            FileInputStream out = new FileInputStream(ss);
            Distortion dd = new Distortion();
            dd.load(out);
            out.close();
            new Thread(()->{
                    dd.setDistortionBlock(usb,back,true);
                    }).start();
            }catch(Exception ee){ ws().notify(Values.error, ee.getMessage());}
    }//GEN-LAST:event_CorrectActionPerformed

    private void OxygenItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_OxygenItemStateChanged
        if (Oxygen.isSelected()){
            usb.OxygenOn(back);
            }
        else{
            OxygenData.setText("---");
            usb.OxygenOff(back);
            }
        oxygen = Oxygen.isSelected();
    }//GEN-LAST:event_OxygenItemStateChanged

    private void LineStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LineStepActionPerformed
        usb.SetDropParameter(ws().local().delays.LineSpeedPoints.getVal(), ws().local().delays.LineSpeedDelta.getVal(), back);
    }//GEN-LAST:event_LineStepActionPerformed

    private void ChangeLayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChangeLayerActionPerformed
        new M3DSelectLayer(ws().data(),layerCount,(value)->{
                layerCount = value;
                ws().layerCount(layerCount);
                ws().global().global.LayerCount.setVal(layerCount);
                ws().saveSettings();
                setLayerData();   
                });
    }//GEN-LAST:event_ChangeLayerActionPerformed

    private void PrintEventItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_PrintEventItemStateChanged
        operate.printEvent(PrintEvent.isSelected());
    }//GEN-LAST:event_PrintEventItemStateChanged

    @Override
    public void onEvent(int code,boolean on, int value, String name) {
        if (code == FileState && on){
            setLayerData();        
            setBottonsVisible();          
            }
        if (code == Events.Print){
            setBottonsVisible(value);          
            }
        if (code == Close){
            System.out.println("Закрыто");
            closeWindow();
            }
        if (code == Settings){
            if (!SetManual.isSelected())
                viewPrintParams();
            }
        if (code == USBFatal) {
            if (fatalDisconnect) return;
            try {
                notify.log("Повторное подключение через "+Values.PrinterReconnectDelay+" сек");
                usb.close();
                fatalDisconnect=true;
                Utils.runAfterDelay(Values.PrinterReconnectDelay,()->{
                    fatalDisconnect=false;
                    controller = udp ? new USBUDPEmulator() : new USBLineController();
                    usb = new USBProtocol(controller);
                    });
                } catch (UNIException e) {
                    notify.notify(Values.error, e.toString());
                    }
            }
        if (code==Clock && !closing){
            if (!fatalDisconnect)
                askState();
            //showEnergy();
            }
        }
    private void showEnergy(){
        if (laserEnergyCalc){
            LaserPowerData dd = power.laserPowerData();
            System.out.println(dd.toString());
            log(dd.toString());
            }
        }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CancelPrint;
    private javax.swing.JButton ChangeLayer;
    private java.awt.Choice CmdList;
    private javax.swing.JButton Correct;
    private javax.swing.JTextField Current;
    private javax.swing.JTextField Energy;
    private javax.swing.JTextField FreeBuf;
    private javax.swing.JLabel L1;
    private javax.swing.JLabel L2;
    private javax.swing.JLabel L3;
    private javax.swing.JLabel L4;
    private javax.swing.JLabel L5;
    private javax.swing.JLabel L6;
    private javax.swing.JLabel L7;
    private javax.swing.JLabel LX1;
    private javax.swing.JLabel LX2;
    private javax.swing.JLabel LY1;
    private javax.swing.JLabel LY2;
    private javax.swing.JCheckBox LaserEnergy;
    private javax.swing.JTextField LaserOffDelay;
    private javax.swing.JTextField LaserOnDelay;
    private javax.swing.JButton LineStep;
    private javax.swing.JTextField M3Step;
    private javax.swing.JTextField M4Step;
    private javax.swing.JTextField MicroStep;
    private javax.swing.JButton MotorsControl;
    private javax.swing.JTextField NLines;
    private javax.swing.JCheckBox NoLayer;
    private javax.swing.JCheckBox OneLayerMode;
    private javax.swing.JCheckBox Oxygen;
    private javax.swing.JTextField OxygenData;
    private javax.swing.JTextField P1;
    private javax.swing.JTextField P2;
    private javax.swing.JTextField P3;
    private javax.swing.JTextField P4;
    private javax.swing.JLabel PAR;
    private javax.swing.JTextField Power;
    private javax.swing.JButton Print;
    private javax.swing.JCheckBox PrintEvent;
    private javax.swing.JTextField Printed;
    private javax.swing.JTextField PrinterState;
    private javax.swing.JTextField PrintingState;
    private javax.swing.JTextField Quadrant;
    private javax.swing.JCheckBox ResetPrint;
    private javax.swing.JButton RunTest;
    private javax.swing.JCheckBox SetManual;
    private javax.swing.JButton SetParams;
    private javax.swing.JButton SetPower;
    private javax.swing.JButton Start;
    private javax.swing.JTextField Step;
    private javax.swing.JButton Suspend;
    private javax.swing.JTextField Temperature;
    private javax.swing.JComboBox<String> TestList;
    private javax.swing.JTextField WorkTime;
    private javax.swing.JTextField X1;
    private javax.swing.JTextField X2;
    private javax.swing.JTextField XX1;
    private javax.swing.JTextField XX2;
    private javax.swing.JLabel XY;
    private javax.swing.JTextField Y1;
    private javax.swing.JTextField Y2;
    private javax.swing.JTextField YY1;
    private javax.swing.JTextField YY2;
    private java.awt.Checkbox emissionOn;
    private java.awt.Checkbox guideOn;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private java.awt.Checkbox laserErr;
    private java.awt.Checkbox modulationOn;
    private java.awt.Checkbox sourceOn;

    @Override
    public void refresh() {

    }

    @Override
    public void shutDown() {

    }
    // End of variables declaration//GEN-END:variables
}
