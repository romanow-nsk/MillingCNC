/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.console;

import romanow.cnc.view.BaseFrame;
import romanow.cnc.settings.WorkSpace;
import romanow.cnc.utils.I_Notify;
import romanow.cnc.utils.UNIException;
import romanow.cnc.utils.Utils;
import romanow.cnc.Values;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

import romanow.cnc.settings.GlobalSettings;
import romanow.cnc.commands.*;
import romanow.cnc.controller.*;
import romanow.cnc.io.BinOutputStream;

/**
 *
 * @author VT
 */
public class ConsoleOfOperator extends BaseFrame {
    private I_Notify notify;
    private M3DCommandFactory factory = new M3DCommandFactory();
    private USBProtocol usb;
    private USBFace controller;
    private USBBack back;
    private USBCommandFactory factoryFull = new USBCommandFactory();
    boolean stop=false;
    boolean suspend=true;
    Moved mov;
    private GlobalSettings set;
    private int motor1Pos=-1,motor3Pos=-1,motor4Pos=-1;
    
     /**
     * Creates new form ConsoleOfOperator
     */
    private void runAfterDelay(final int delay, final Runnable code){
        new Thread(()->{
            try {
                Thread.sleep(delay*1000);
                java.awt.EventQueue.invokeLater(()->{
                    code.run();
                    });
                } catch (InterruptedException ex) {}
            }).start();
        }
    private void runWhileState(final Runnable code){
        new Thread(()->{
            try {
                try {
                    Thread.sleep(1000);
                    } catch (InterruptedException ex) {}
                usb.waitForState(USBCodes.STATE_STANDBY, USBCodes.STATE_TEST, 30);
                java.awt.EventQueue.invokeLater(()->{
                    code.run();
                    });
                } catch (UNIException ex) { notify.notify(Values.error,ex.toString()); }
            }).start();
        }    
    private void waitForMotors(){
        runWhileState(()->{ askMotors(); });
        }
    private void delayForMotors(int delay){
        runAfterDelay(delay,()->{ askMotors(); });
        }

    @Override
    public void onEvent(int code, boolean on, int value, String name) {
        }
    @Override
    public void refresh() {
        }

    @Override
    public void shutDown() {
        }

    //----------- ВЛОЖЕННЫЙ КЛАСС-ПОТОК ----------------------------------------
    public class Moved extends Thread{

        Color back;
        int i;
        volatile boolean stop=false;
        Moved(){
            back=canvas1.getBackground();
            start();                        // Можно здесь......................
            }
        @Override
        public void run() {
            while(!stop){
            try {
                Thread.sleep(1000);
                synchronized (ConsoleOfOperator.this){
                    if(suspend) ConsoleOfOperator.this.wait();
                    //    System.out.println("Cycle!");
                    isReady();
                    //askMotor(1);
                    //askMotor(3); 
                    //askMotor(4);  

                    }
                } catch (InterruptedException ex) {}
            }

        
        }
    } 
    

private synchronized void message(String ss){
        textField9.setText(ss); 
    }
    
    public ConsoleOfOperator(I_Notify notify0,boolean udp) {
        if (!tryToStart()) return;
        this.setBounds(100,100, 800, 900);
        initComponents();
        initFieldsVal();    
        controller = udp ? new USBUDPEmulator() : new USBLineController();
        usb = new USBProtocol(controller);
        notify = notify0;
        set = WorkSpace.ws().global().global;
        showMotorsPosition();
        waitForMotors();                             
        ArrayList<String> xx = factory.commandList();
        //mov = new Moved();  //запуск потока опроса моторов
        back = new USBNotify(factoryFull,notify) {
            @Override
            public void onSuccess(int code, int[] data) {
                notify.info(String.format("Выполнено code=%4x",code));
                //if (code==USBCodes.GetAvailMemory)
                //    notify.info("Памяти команд "+data[1]+" слов");
                if (code==USBCodes.GetMotorStatus){
                    MotorVisualisation(data);
                    notify.info("Мотор "+data[1]);
                    }
                if (code==USBCodes.GetBeamStatus){
                    LaserVisualisation(data);
                    notify.info("Лазер "+data[1]);
                    }
                if (code==USBCodes.IsReady){
                    textField9.setText("Готовность "+data[1]);   
                    notify.info("Проверка-длительная операция "+data[1]);
                    }
                if (code==USBCodes.SetBurnLine ){
                    notify.info("Задание координат отрезка "+data[1]+" свободно");
                    textField9.setText("Свободно "+data[1]);
                    }
                if (code==USBCodes.GetAvailMemory ) {
                    notify.info( "Свободная память" + data[1] + " отрезков");
                    textField9.setText("Свободно " + data[1]);
                    }
                if (code==USBCodes.ReadMessages || code==USBCodes.ReadLog){
                    int count = data[1];
                    notify.info("Прочитано "+count+" строки");
                    String ss[] = new String[0];
                    try {
                        ss = Utils.IntArrayToStrings(data);
                    } catch (UNIException e) {
                        notify.notify(Values.fatal,e.toString());
                        return;
                    }
                    for(String zz : ss){
                        notify.info(zz);
                        }
                    }
                }
            };
        try {
            usb.init();
            getBeamStatus();
            } catch (UNIException e) { notify.notify(Values.error,e.toString()); }
        

        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        canvas1 = new java.awt.Canvas();
        jSlider1 = new javax.swing.JSlider();
        rightSensM1 = new java.awt.Panel();
        leftSensM1 = new java.awt.Panel();
        jSlider4 = new javax.swing.JSlider();
        jSlider3 = new javax.swing.JSlider();
        hiSensM4 = new java.awt.Panel();
        hiSensM3 = new java.awt.Panel();
        loSensM4 = new java.awt.Panel();
        loSensM3 = new java.awt.Panel();
        upLimM4 = new java.awt.TextField();
        positionM4 = new java.awt.TextField();
        downLimM4 = new java.awt.TextField();
        upLimM3 = new java.awt.TextField();
        positionM3 = new java.awt.TextField();
        downLimM3 = new java.awt.TextField();
        guideX = new java.awt.TextField();
        guideY = new java.awt.TextField();
        label1 = new java.awt.Label();
        label2 = new java.awt.Label();
        label3 = new java.awt.Label();
        textField9 = new java.awt.TextField();
        label6 = new java.awt.Label();
        label7 = new java.awt.Label();
        label8 = new java.awt.Label();
        label9 = new java.awt.Label();
        label10 = new java.awt.Label();
        label11 = new java.awt.Label();
        leftLim = new java.awt.TextField();
        positionM1 = new java.awt.TextField();
        rightLim = new java.awt.TextField();
        label12 = new java.awt.Label();
        testOnOff = new java.awt.Checkbox();
        sourceOn = new java.awt.Checkbox();
        goXY = new javax.swing.JToggleButton();
        powerWait = new java.awt.Checkbox();
        emissionOn = new java.awt.Checkbox();
        guideOn = new java.awt.Checkbox();
        modulationOn = new java.awt.Checkbox();
        setPRM = new javax.swing.JButton();
        setM1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        newMotorPosition4 = new java.awt.TextField();
        newMotorPosition3 = new java.awt.TextField();
        newMotorPosition1 = new java.awt.TextField();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        laserErr = new java.awt.Checkbox();
        stepPowder = new java.awt.TextField();
        stepSample = new java.awt.TextField();
        microstep = new java.awt.TextField();
        label4 = new java.awt.Label();
        label5 = new java.awt.Label();
        label15 = new java.awt.Label();
        softReset = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        Velocity = new javax.swing.JLabel();
        mot4_Set0 = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jProgressBar4 = new javax.swing.JProgressBar();
        jProgressBar3 = new javax.swing.JProgressBar();
        mot1_Set0 = new javax.swing.JButton();
        mot3_Set0 = new javax.swing.JButton();
        saveBTN = new javax.swing.JButton();
        loadBTN = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Консоль оператора принтера SLM-3D");
        setPreferredSize(new java.awt.Dimension(818, 788));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        canvas1.setBackground(new java.awt.Color(51, 51, 51));
        canvas1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                canvas1MouseClicked(evt);
            }
        });

        jSlider1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlider1MouseReleased(evt);
            }
        });

        rightSensM1.setBackground(new java.awt.Color(51, 153, 0));
        rightSensM1.setName("RightSens"); // NOI18N

        javax.swing.GroupLayout rightSensM1Layout = new javax.swing.GroupLayout(rightSensM1);
        rightSensM1.setLayout(rightSensM1Layout);
        rightSensM1Layout.setHorizontalGroup(
            rightSensM1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );
        rightSensM1Layout.setVerticalGroup(
            rightSensM1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
        );

        leftSensM1.setBackground(new java.awt.Color(51, 153, 0));
        leftSensM1.setName("LeftSens"); // NOI18N

        javax.swing.GroupLayout leftSensM1Layout = new javax.swing.GroupLayout(leftSensM1);
        leftSensM1.setLayout(leftSensM1Layout);
        leftSensM1Layout.setHorizontalGroup(
            leftSensM1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );
        leftSensM1Layout.setVerticalGroup(
            leftSensM1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
        );

        jSlider4.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlider4MouseReleased(evt);
            }
        });

        jSlider3.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlider3MouseReleased(evt);
            }
        });

        hiSensM4.setBackground(new java.awt.Color(51, 153, 0));
        hiSensM4.setName("RightSens"); // NOI18N

        javax.swing.GroupLayout hiSensM4Layout = new javax.swing.GroupLayout(hiSensM4);
        hiSensM4.setLayout(hiSensM4Layout);
        hiSensM4Layout.setHorizontalGroup(
            hiSensM4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );
        hiSensM4Layout.setVerticalGroup(
            hiSensM4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
        );

        hiSensM3.setBackground(new java.awt.Color(0, 153, 0));
        hiSensM3.setName("RightSens"); // NOI18N

        javax.swing.GroupLayout hiSensM3Layout = new javax.swing.GroupLayout(hiSensM3);
        hiSensM3.setLayout(hiSensM3Layout);
        hiSensM3Layout.setHorizontalGroup(
            hiSensM3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );
        hiSensM3Layout.setVerticalGroup(
            hiSensM3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
        );

        loSensM4.setBackground(new java.awt.Color(0, 153, 0));
        loSensM4.setName("RightSens"); // NOI18N

        javax.swing.GroupLayout loSensM4Layout = new javax.swing.GroupLayout(loSensM4);
        loSensM4.setLayout(loSensM4Layout);
        loSensM4Layout.setHorizontalGroup(
            loSensM4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );
        loSensM4Layout.setVerticalGroup(
            loSensM4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
        );

        loSensM3.setBackground(new java.awt.Color(51, 153, 0));
        loSensM3.setName("RightSens"); // NOI18N

        javax.swing.GroupLayout loSensM3Layout = new javax.swing.GroupLayout(loSensM3);
        loSensM3.setLayout(loSensM3Layout);
        loSensM3Layout.setHorizontalGroup(
            loSensM3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );
        loSensM3Layout.setVerticalGroup(
            loSensM3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
        );

        upLimM4.setMinimumSize(new java.awt.Dimension(80, 20));
        upLimM4.setText("textField1");
        upLimM4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                upLimM4KeyPressed(evt);
            }
        });

        positionM4.setMinimumSize(new java.awt.Dimension(80, 20));
        positionM4.setText("textField2");

        downLimM4.setMinimumSize(new java.awt.Dimension(80, 20));
        downLimM4.setText("textField3");

        upLimM3.setText("textField4");
        upLimM3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upLimM3ActionPerformed(evt);
            }
        });
        upLimM3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                upLimM3KeyPressed(evt);
            }
        });

        positionM3.setText("textField5");
        positionM3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                positionM3ActionPerformed(evt);
            }
        });

        downLimM3.setText("textField6");

        guideX.setText("textField7");

        guideY.setText("textField8");

        label1.setText("Координаты");

        label2.setText("X");

        label3.setText("Y");

        textField9.setText("textField9");

        label6.setText("Верх.предел");

        label7.setText("Позиция");

        label8.setText("Ниж.предел");

        label9.setText("Верх.предел");

        label10.setText("Ниж.предел");

        label11.setText("Позиция");

        leftLim.setText("textField10");

        positionM1.setText("textField11");
        positionM1.addTextListener(new java.awt.event.TextListener() {
            public void textValueChanged(java.awt.event.TextEvent evt) {
                positionM1TextValueChanged(evt);
            }
        });

        rightLim.setText("textField12");

        label12.setText("Лазер");

        testOnOff.setLabel("Тестирование");
        testOnOff.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                testOnOffItemStateChanged(evt);
            }
        });

        sourceOn.setLabel("Вкл.сеть");
        sourceOn.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                sourceOnItemStateChanged(evt);
            }
        });

        goXY.setText("GO!");
        goXY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goXYActionPerformed(evt);
            }
        });

        powerWait.setLabel("Мощность");
        powerWait.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                powerWaitItemStateChanged(evt);
            }
        });

        emissionOn.setLabel("Эмиссия");
        emissionOn.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                emissionOnItemStateChanged(evt);
            }
        });

        guideOn.setLabel("Указатель");
        guideOn.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                guideOnItemStateChanged(evt);
            }
        });

        modulationOn.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        modulationOn.setForeground(new java.awt.Color(255, 0, 0));
        modulationOn.setLabel("Модуляция");
        modulationOn.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                modulationOnItemStateChanged(evt);
            }
        });

        setPRM.setText("SET");
        setPRM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setPRMActionPerformed(evt);
            }
        });

        setM1.setText("setM1");
        setM1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setM1ActionPerformed(evt);
            }
        });

        jButton2.setText("Refresh");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        newMotorPosition4.setMaximumSize(new java.awt.Dimension(80, 20));
        newMotorPosition4.setMinimumSize(new java.awt.Dimension(80, 20));
        newMotorPosition4.setName(""); // NOI18N
        newMotorPosition4.setText("0");
        newMotorPosition4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMotorPosition4ActionPerformed(evt);
            }
        });

        newMotorPosition3.setMaximumSize(new java.awt.Dimension(70, 20));
        newMotorPosition3.setMinimumSize(new java.awt.Dimension(70, 20));
        newMotorPosition3.setName(""); // NOI18N
        newMotorPosition3.setText("0");

        newMotorPosition1.setMaximumSize(new java.awt.Dimension(60, 20));
        newMotorPosition1.setMinimumSize(new java.awt.Dimension(60, 20));
        newMotorPosition1.setName(""); // NOI18N
        newMotorPosition1.setText("0");

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton3.setText("jButton3");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        laserErr.setLabel("Ошибка");
        laserErr.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                laserErrItemStateChanged(evt);
            }
        });

        stepPowder.setMinimumSize(new java.awt.Dimension(50, 20));
        stepPowder.setName(""); // NOI18N
        stepPowder.setText("5");

        stepSample.setMinimumSize(new java.awt.Dimension(50, 20));
        stepSample.setName(""); // NOI18N
        stepSample.setText("5");

        microstep.setMinimumSize(new java.awt.Dimension(55, 20));
        microstep.setName(""); // NOI18N
        microstep.setText("0.00002");

        label4.setText("Шаг М3");

        label5.setText("Шаг М4");

        label15.setText("Микрошаг");

        softReset.setText("softReset");
        softReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                softResetActionPerformed(evt);
            }
        });

        jLabel1.setText("1 шаг 0.01 мм");

        Velocity.setText("jLabel2");

        mot4_Set0.setText(">0<");
        mot4_Set0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mot4_Set0ActionPerformed(evt);
            }
        });

        jProgressBar4.setMinimumSize(new java.awt.Dimension(200, 14));
        jProgressBar4.setName(""); // NOI18N
        jProgressBar4.setPreferredSize(new java.awt.Dimension(200, 14));
        jProgressBar4.setRequestFocusEnabled(false);

        jProgressBar3.setMinimumSize(new java.awt.Dimension(200, 14));
        jProgressBar3.setName(""); // NOI18N
        jProgressBar3.setPreferredSize(new java.awt.Dimension(200, 14));

        mot1_Set0.setText(">0<");
        mot1_Set0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mot1_Set0ActionPerformed(evt);
            }
        });

        mot3_Set0.setText(">0<");
        mot3_Set0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mot3_Set0ActionPerformed(evt);
            }
        });

        saveBTN.setText("Сохр.");
        saveBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveBTNActionPerformed(evt);
            }
        });

        loadBTN.setText("Загр.");
        loadBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadBTNActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(textField9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGap(261, 261, 261)
                                        .addComponent(label9, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jSlider3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jProgressBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(loSensM3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(hiSensM3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addComponent(mot3_Set0))
                                        .addGap(73, 73, 73))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(421, 421, 421)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(newMotorPosition1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(setM1)))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                            .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addGroup(layout.createSequentialGroup()
                                                                .addGap(10, 10, 10)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                    .addComponent(guideY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                    .addComponent(guideX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                    .addComponent(goXY, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                                    .addComponent(downLimM4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(newMotorPosition4, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jButton3))
                                                .addGap(26, 26, 26)
                                                .addComponent(mot4_Set0)
                                                .addGap(85, 85, 85)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(jButton1)
                                                    .addComponent(newMotorPosition3, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(label12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(sourceOn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(powerWait, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(guideOn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(emissionOn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(modulationOn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(laserErr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Velocity)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(testOnOff, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(stepPowder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(microstep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(stepSample, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(label15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(label5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addComponent(jLabel1)
                                        .addComponent(setPRM)
                                        .addComponent(softReset, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGap(44, 44, 44))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(mot1_Set0)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(upLimM4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(label7, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(positionM4, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label8, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(41, 41, 41)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addComponent(leftSensM1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(rightSensM1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addComponent(leftLim, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(positionM1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(rightLim, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addComponent(canvas1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jSlider4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jProgressBar4, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGap(91, 91, 91)
                                                .addComponent(label11, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(hiSensM4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(loSensM4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGap(66, 66, 66)
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(downLimM3, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(label10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(positionM3, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                    .addComponent(upLimM3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addGap(197, 197, 197)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(saveBTN)
                                            .addComponent(jButton2)
                                            .addComponent(loadBTN))))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(canvas1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(guideX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(guideY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(goXY)))
                                .addGap(23, 23, 23)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(leftLim, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(positionM1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                                        .addComponent(leftSensM1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(rightLim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                                        .addComponent(rightSensM1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(19, 19, 19)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(mot1_Set0)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(newMotorPosition1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(9, 9, 9)))
                        .addGap(9, 9, 9))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(label12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sourceOn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(powerWait, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(guideOn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emissionOn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(modulationOn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(laserErr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(stepPowder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(13, 13, 13)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(label5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(label15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(stepSample, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(microstep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Velocity)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(label9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(upLimM3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(positionM3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(downLimM3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(hiSensM3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(loSensM3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(jProgressBar3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jSlider3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(mot3_Set0)
                                    .addComponent(newMotorPosition3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(label6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(upLimM4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(positionM4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(downLimM4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(newMotorPosition4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(jSlider4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jProgressBar4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGroup(layout.createSequentialGroup()
                                                    .addComponent(hiSensM4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(loSensM4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(mot4_Set0))))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton3)
                            .addComponent(jButton1))
                        .addGap(10, 10, 10))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(setM1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(setPRM)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(testOnOff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(softReset)))
                        .addGap(38, 38, 38)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(loadBTN)
                        .addGap(18, 18, 18)
                        .addComponent(saveBTN)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(textField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jProgressBar4.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void LaserVisualisation(int data[]){
        /* Значения битов */
        /* 7 = 1 снята блокировка 1 */
        /* 6 = 1 снята блокировка 2 */
        /* 5 = 1 подается мощность  */
        /* 4 = 1 система стартовала  */
        /* 3 = 1 модуляция включена аппаратно (включается на короткое время во время прожига) */
        /* 2 = 1 эмиссия включена   */
        /* 1 = 1 указатель включен */
        /* 0 = 1 есть ошибки */
        if((data[1] & 0x20)!=0){ //лазер включен
         sourceOn.setState(true);
         emissionOn.setEnabled(true);
         modulationOn.setEnabled(true); 
         guideOn.setEnabled(true);
         laserErr.setEnabled(true);
        }else{
         sourceOn.setState(false);
         emissionOn.setEnabled(false);
         modulationOn.setEnabled(false); 
         guideOn.setEnabled(false);
         laserErr.setEnabled(false);         
        }
        if((data[1] & 0x02)!=0){  //эмиссия включена
             emissionOn.setState(true);        
        }else{
             emissionOn.setState(false);        
        }
        if((data[1] & 0x04)!=0){  //эмиссия включена
             guideOn.setState(true);        
        }else{
             guideOn.setState(false);        
        }
        if((data[1] & 0x01)!=0){  //эмиссия включена
             laserErr.setState(true);        
        }else{
             laserErr.setState(false);        
        }        
 
    }
    
    
    private void MotorVisualisation(int data[]){
    //    textField9.setText(""+data[6]);
        switch(data[1]){
            case 1:
            {
                motor1Pos=data[4];
                leftLim.setText(""+data[2]);
                rightLim.setText(""+data[3]);
                //positionM1.setText(""+data[4]);
                
                jSlider1.setMinimum(data[2]);
                jSlider1.setMaximum(data[3]);
                /*
                jSlider1.setValue(data[4]);                     
                */
                jProgressBar1.setMinimum(data[2]);                
                jProgressBar1.setMaximum(data[3]);
                jProgressBar1.setValue(data[4]);                   
                if((data[6] & 1)==0){
                   rightSensM1.setBackground(Color.gray);
                }else{
                   rightSensM1.setBackground(Color.red);                    
                }
                if((data[6] & 2)==0){
                   leftSensM1.setBackground(Color.gray);
                }else{
                   leftSensM1.setBackground(Color.red);                    
                }                
                break;
            }
            case 3:
            {
                motor3Pos=data[4];
                downLimM3.setText(""+data[2]);
                upLimM3.setText(""+data[3]);
                //positionM3.setText(""+data[4]);
                
                jSlider3.setMinimum(data[2]);
                jSlider3.setMaximum(data[3]);
                /*
                jSlider3.setValue(data[4]);                    
                */
                jProgressBar3.setMinimum(data[2]);                
                jProgressBar3.setMaximum(data[3]);
                jProgressBar3.setValue(data[4]);    
                if((data[6] & 1)==0){
                   hiSensM3.setBackground(Color.gray);
                }else{
                   hiSensM3.setBackground(Color.red);                    
                }
                if((data[6] & 2)==0){
                   loSensM3.setBackground(Color.gray);
                }else{
                   loSensM3.setBackground(Color.red);                    
                }                  
                break;
            }
            case 4:
            {
                motor4Pos=data[4];
                downLimM4.setText(""+data[2]);
                upLimM4.setText(""+data[3]);
                //positionM4.setText(""+data[4]);
                
                jSlider4.setMinimum(data[2]);
                jSlider4.setMaximum(data[3]);
                /*
                jSlider4.setValue(data[4]);               
                */
                jProgressBar4.setMinimum(data[2]);                
                jProgressBar4.setMaximum(data[3]);
                jProgressBar4.setValue(data[4]);    
                if((data[6] & 1)==0){
                   hiSensM4.setBackground(Color.gray);
                }else{
                   hiSensM4.setBackground(Color.red);                    
                }
                if((data[6] & 2)==0){
                   loSensM4.setBackground(Color.gray);
                }else{
                   loSensM4.setBackground(Color.red);                    
                }   
                break;  
            }
            default:
                break;
        }
    showMotorsPosition();
    }
    //------------------------------------------- Примеры вызова -------------------------------------------------------
    private void sendCmdWithParam(Command cmd){
        new Thread(()->{
            usb.oneCommand(cmd.toIntArray(),back);
            }).start();
        }
    //------------------------------------------------------------------------------------------------------------------
    private void sendCmdWithParam(String cc, int param[]){
         try {
            Command xx = factoryFull.getCommand(cc);
            if (xx==null) {

                notify.log( "Не найдена команда:" + cc);
                return;
            }
            new Thread(()->{
                int _out[] = xx.toIntArray();
                int out[] = new int[6];
                out[0]= _out[0];
                out[1]= 0;
                out[2]= param[0]; //номер мотора 1,3,4
                out[3]= param[1];
                out[4]= param[2];
                out[5]= param[3];
                usb.oneCommand(out,back);
                }).start();

        } catch (UNIException e) { notify.log("Ошибка генерации команды: "+cc); }               
    }
    
    /* Проверка-длительная операция */
    private void isReady(){
        int param[] = new int[4];
        String cc = new String("Проверка-длительная операция"); 
        param[0]=0;
        param[1]=0; //start 
        param[2]=0;
        param[3]=0;         
        sendCmdWithParam(cc,param);       
    }
    /* Задание параметров мотора */
    private void SetMotorParam(int motor, int min, int max){
           int param[] = new int[4];
           String cc = new String("Задание параметров мотора");
           param[0]=motor;
           param[1]=min; //start
           param[2]=max; //end
           sendCmdWithParam(cc,param);
           //sendCmdWithParam(cc,new CommandIntList(USBCodes.SetMotorParam,motor,min,max));
    }
    
    /* Запуск мотора на +-N шагов */
    private void startMotor(int motor, int pos){
           int param[] = new int[4];
           String cc = new String("Запуск мотора на +-N шагов");
           param[0]=motor;
           param[1]=pos; //позиция
           param[2]=0; 
           sendCmdWithParam(cc,param);        
    }
    /* Останов мотора */
    private void stopMotor(int motor){
           int param[] = new int[4];
           String cc = new String("Останов мотора");
           param[0]=motor;
           param[1]=0; 
           param[2]=0; 
           sendCmdWithParam(cc,param);               
    }
    /*   пока нет */
    private void calibrateMotor(int motor){
        sendCmdWithParam(new CommandIntList(USBCodes.StartTest,motor,0,0));
        /*
           int param[] = new int[4];
           String cc = new String("Тест оборудования №");
           param[0]=motor;
           param[1]=0; 
           param[2]=0; 
           sendCmdWithParam(cc,param);            
        */
    }
    
    /*Cтатус дистанционного управления лазером*/
    private void getBeamStatus(){
           int param[] = new int[4];
           String cc = new String("Cтатус дистанционного управления лазером");
           param[0]=0;
           param[1]=0; 
           param[2]=0; 
           sendCmdWithParam(cc,param);           
    }
    /*Отключение блокировки лазера 1*/
    private void lockBeam1(int action){
           int param[] = new int[4];
           String cc = new String("Отключение блокировки лазера 1");
           param[0]=action;
           param[1]=0; 
           param[2]=0; 
           sendCmdWithParam(cc,param);          
    }
    /* "Отключение блокировки лазера 2" */
    private void lockBeam2(int action){
            int param[] = new int[4];
           String cc = new String("Отключение блокировки лазера 2");
           param[0]=action;
           param[1]=0; 
           param[2]=0; 
           sendCmdWithParam(cc,param);              
    }
    /* "Включение сетевого напряжения лазера" */
     private void BeamPowerOn(int action){
           int param[] = new int[4];
           String cc = new String("Включение сетевого напряжения лазера");
           param[0]=action;
           param[1]=0; 
           param[2]=0; 
           sendCmdWithParam(cc,param);            
     }
    /* "Запуск лазера" */
     private void BeamStart(int action){
           int param[] = new int[4];
           String cc = new String("Запуск лазера");
           param[0]= action;
           param[1]=0; 
           param[2]=0; 
           sendCmdWithParam(cc,param);            
     }
    /* "Включение модуляции лазера" */
     private void BeamModOn(int action){
           int param[] = new int[4];
           String cc = new String("Включение модуляции лазера");
           param[0]=action;
           param[1]=0; 
           param[2]=0; 
           sendCmdWithParam(cc,param);            
     }
    /* "Включение эмиссии лазера") */
     private void BeamEmisOn(int action){
           int param[] = new int[4];
           String cc = new String("Включение эмиссии лазера");
           param[0]=action;
           param[1]=0; 
           param[2]=0; 
           sendCmdWithParam(cc,param);            
     }
    /* "Включение указателя местоположения" */
     private void BeamSetGuide(int action){
           int param[] = new int[4];
           String cc = new String("Включение указателя местоположения");
           param[0]=action;
           param[1]=0; 
           param[2]=0; 
           sendCmdWithParam(cc,param);            
     }
    /* "Сброс ошибки лазера" */
     private void BeamResetErr(int action){
           int param[] = new int[4];
           String cc = new String("Сброс ошибки лазера");
           param[0]=action;
           param[1]=0; 
           param[2]=0; 
           sendCmdWithParam(cc,param);            
     }
    // "Задание HEX позиции"
     private void SetPositionHEX(int posX, int posY){    
           int param[] = new int[4];
           String cc = new String("Задание HEX позиции");
           param[0]=posX;
           param[1]=posY; 
           param[2]=0; 
           sendCmdWithParam(cc,param);                   
     }
    // "Задание Q31 позиции"
     private void SetPositionQ31(int posX, int posY){
           int param[] = new int[4];
           String cc = new String("Задание Q31 позиции");
           param[0]=posX;
           param[1]=posY; 
           param[2]=0; 
           sendCmdWithParam(cc,param);                   
     }
    //"Задание координат отрезка" 
     private void SetBurningLine(int startX,int startY, int endX, int endY){
           int param[] = new int[5];
           String cc = new String("Линия");
           param[0]=0x1; //СmdMove=0x1 только перемещение, для прожига СmdFire=0x101
           param[1]=startX;
           param[2]=startY; 
           param[3]=endX;
           param[4]=endY;            
           sendCmdWithParam(cc,param);                      
            }
         //"Свободная память" 
     
     private void GetAvailMemory(int startX,int startY, int endX, int endY){
           int param[] = new int[2];
           String cc = new String("Свободная память");
           param[0]=0x00; 
           param[1]=0x00; 
           sendCmdWithParam(cc,param);                      
     }
     /*"Полный тест оборудования"  */
     private void StartTestOfEquipment(int arg1,int arg2){
           int param[] = new int[4];
           String cc = new String("Полный тест оборудования");
           param[0]=arg1;
           param[1]=arg2;
           sendCmdWithParam(cc,param);                
     }
     
     /** Остановка печати без возобновлния */
     private void StopPrint(int arg1,int arg2){
           int param[] = new int[4];
           String cc = new String("Отменить печать");
           param[0]=arg1;
           param[1]=arg2;
           sendCmdWithParam(cc,param);                
        }
     private void showMotorsPosition(){
         int pos1 = set.M1CurrentPos.getVal();
         int pos3 = set.M3CurrentPos.getVal();
         int pos4 = set.M4CurrentPos.getVal();
         positionM3.setText(""+pos3);
         positionM4.setText(""+pos4);
         positionM1.setText(""+pos1);
         jSlider1.setValue(pos1);
         jSlider3.setValue(pos3);
         jSlider4.setValue(pos4);
        }

    private void askMotor(int motor){
          String cc = new String("Запрос состояния мотора");
         try {
            Command xx = factoryFull.getCommand(cc);
            if (xx==null) {
//                notify.log( "Не найдена команда:" + CmdList.getSelectedItem());
                notify.log( "Не найдена команда:" + cc);
                return;
            }
            new Thread(()->{
//              System.out.println("Addr="+xx.addr +", Code="+xx.code);
                int _out[] = xx.toIntArray();
                int out[] = new int[4];
                out[0]= _out[0];
                out[1]= 0;
                out[2]= motor; //номер мотора 1,3,4
                usb.oneCommand(out,back);
                }).start();

        } catch (UNIException e) { notify.log("Ошибка генерации команды: "+cc); }       
    }

    

     
    private void setPRMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setPRMActionPerformed

 
    try{
       int ms=Float.floatToIntBits(Float.parseFloat(microstep.getText())); //doubleToQ31(Double.parseDouble(guideX.getText()));
       float msf=Float.parseFloat(microstep.getText());
       sendCmdWithParam(new CommandInt(USBCodes.MarkingMicroStepsMarkInt,ms));
       Velocity.setText("V= "+(msf*200*20000)+" мм/с");
    }catch(Exception ee){ System.out.println ("Формат вещественного числа ????");}
    try{
        int v3=Integer.parseInt(stepPowder.getText());
         sendCmdWithParam(new CommandInt(USBCodes.ControlNextLayerMovingM3Step,v3));
        int v2=Integer.parseInt(stepSample.getText()); 
         sendCmdWithParam(new CommandInt(USBCodes.ControlNextLayerMovingM4Step,v2));
    }catch(Exception ee){ System.out.println ("Формат целого числа ????");}



         
    }//GEN-LAST:event_setPRMActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            onClose();
            usb.close();
            stop=true;
            } catch (UNIException e) { notify.notify(Values.error,e.toString()); }  
                  
    }//GEN-LAST:event_formWindowClosing

    private void askMotors(){
        askMotor(1);
        askMotor(3); 
        askMotor(4);
        //compareMotorPositions();
        }
    
    
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        askMotors();
    //       getBeamStatus();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void testOnOffItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_testOnOffItemStateChanged
        if(evt.getStateChange()==2){
                  
       StartTestOfEquipment(0,0); // Смена слоя происходит без движения моторов, временно для отдадки
      
        }else{

       StartTestOfEquipment(1,0); // Смена слоя с движением моторов  
                
        }

    }//GEN-LAST:event_testOnOffItemStateChanged

    private void sourceOnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_sourceOnItemStateChanged
        if(evt.getStateChange()==2){
            BeamPowerOn(0); //отключаем
            guideOn.setEnabled(false);
            emissionOn.setEnabled(false);
            modulationOn.setEnabled(false);
            laserErr.setEnabled(false);        
         System.out.println("Off");         
        }else{
            BeamPowerOn(1); //включаем
            emissionOn.setEnabled(true);
            modulationOn.setEnabled(true); 
            guideOn.setEnabled(true);
            laserErr.setEnabled(true);        
        System.out.println("On");                  
        }
    }//GEN-LAST:event_sourceOnItemStateChanged

    private void guideOnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_guideOnItemStateChanged
        if(evt.getStateChange()==2){
        BeamSetGuide(0); //отключаем
        }else{
        BeamSetGuide(1); //включаем
        }
    }//GEN-LAST:event_guideOnItemStateChanged

    private void powerWaitItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_powerWaitItemStateChanged

    }//GEN-LAST:event_powerWaitItemStateChanged

    private void emissionOnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_emissionOnItemStateChanged
        if(evt.getStateChange()==2){
        BeamEmisOn(0); //отключаем
        }else{
        BeamEmisOn(1); //включаем
        }
    }//GEN-LAST:event_emissionOnItemStateChanged

    private void modulationOnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_modulationOnItemStateChanged
        if(evt.getStateChange()==2){
        BeamModOn(0); //отключаем
        }else{
        BeamModOn(1); //включаем
        }
    }//GEN-LAST:event_modulationOnItemStateChanged

    private void goXYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goXYActionPerformed
    //   int coordX=Integer.parseUnsignedInt(guideX.getText());
    //   int coordY=Integer.parseUnsignedInt(guideY.getText());
    try{
       int coordX= BinOutputStream.doubleToQ31(Double.parseDouble(guideX.getText()));
       int coordY= BinOutputStream.doubleToQ31(Double.parseDouble(guideY.getText()));
       System.out.println("X="+coordX+" Y="+coordY);
       SetPositionQ31(coordX,coordY);        
    }catch(Exception ee){ System.out.println ("Формат вещественного числа ????");}
    
    //   System.out.println("X="+coordX+" Y="+coordY);
    //   SetPositionHEX(coordX,coordY);   
    }//GEN-LAST:event_goXYActionPerformed

    private void setM1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setM1ActionPerformed
          int steps=Integer.parseInt(newMotorPosition1.getText());
          startMotor(1, steps);
          set.M1CurrentPos.incValue(steps);
          saveSettings();
          showMotorsPosition(); 
        delayForMotors(15);                                                                                                                    
    }//GEN-LAST:event_setM1ActionPerformed

    private void canvas1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_canvas1MouseClicked
       int coordX=evt.getX()*327;
       int coordY=65535-(evt.getY()*327);
       SetPositionHEX(coordX,coordY);   
    //   System.out.println("X="+coordX+", Y="+coordY);
        
    }//GEN-LAST:event_canvas1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
          int steps=Integer.parseInt(newMotorPosition3.getText());
  //        System.out.println("Steps ="+steps);          
          startMotor(3, steps);
          set.M3CurrentPos.incValue(steps);
          saveSettings();
          showMotorsPosition();
          delayForMotors(15);                                                                                                                                       
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        int steps=Integer.parseInt(newMotorPosition4.getText());
        startMotor(4, steps);
        set.M4CurrentPos.incValue(steps);
        saveSettings();
        showMotorsPosition();
        delayForMotors(15);                                                                   
    }//GEN-LAST:event_jButton3ActionPerformed

    private void laserErrItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_laserErrItemStateChanged
       if(evt.getStateChange()==2){
        BeamResetErr(0); //отключаем
           }else{
        BeamResetErr(1); //включаем
        }
    }//GEN-LAST:event_laserErrItemStateChanged

    private void softResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_softResetActionPerformed
        // Мягкий сброс системы
        sendCmdWithParam(new CommandIntList(USBCodes.SoftReset));
    }//GEN-LAST:event_softResetActionPerformed

    private void newMotorPosition4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newMotorPosition4ActionPerformed
        /*
        final JProgressBar pb = new JProgressBar();
        pb.setMinimum(0);
        pb.setMaximum(MAX);
        pb.setStringPainted(true);
        pb.setOrientation(JProgressBar.VERTICAL);
        */
    }//GEN-LAST:event_newMotorPosition4ActionPerformed

    private void saveSettings(){
        WorkSpace.ws().saveSettings();
        }
    private void saveMotorPosition(int num, int value){
        if (num==4)
            set.M4CurrentPos.setVal(value);
        if (num==3)
            set.M3CurrentPos.setVal(value);
        if (num==1)
            set.M1CurrentPos.setVal(value);
        saveSettings();
        }
    
    private void mot4_Set0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mot4_Set0ActionPerformed
        //SetMotorParam(4,0,4000);
        calibrateMotor(4);
        saveMotorPosition(4,0);
        showMotorsPosition();
        waitForMotors();                                                                                       
    }//GEN-LAST:event_mot4_Set0ActionPerformed

    private void saveBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBTNActionPerformed
        //--- обработчик события – сохранить в BIN файл
      FileDialog dlg=new FileDialog(this,"Сохранить файл BIN",FileDialog.SAVE);
      dlg.setFile("motors.dat");
      dlg.setVisible(true);
      String path=dlg.getDirectory()+dlg.getFile();
      try	{
            DataOutputStream si=new DataOutputStream(new FileOutputStream(path));
            /* сохранение параметров горизонтального мотора */
            si.writeInt(Integer.parseInt(leftLim.getText()));
            si.writeInt(Integer.parseInt(positionM1.getText()));
            si.writeInt(Integer.parseInt(rightLim.getText()));
            
            /* сохранение параметров мотора с образцом */
            si.writeInt(Integer.parseInt(downLimM4.getText()));
            si.writeInt(Integer.parseInt(positionM4.getText()));
            si.writeInt(Integer.parseInt(upLimM4.getText()));
            
            /* сохранение параметров мотора с порошком*/
            si.writeInt(Integer.parseInt(downLimM3.getText()));
            si.writeInt(Integer.parseInt(positionM3.getText()));
            si.writeInt(Integer.parseInt(upLimM3.getText()));
            si.close();
            }
        catch (IOException ee) { 
            System.out.println(""+ee);
        }

                                           
    }//GEN-LAST:event_saveBTNActionPerformed

    private void loadBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadBTNActionPerformed
        //-------- обработчик события – загрузить из BIN файла

        FileDialog dlg=new FileDialog (this,"Открыть файл BIN",FileDialog.LOAD);
 
        dlg.setFile("*.*");
        dlg.setVisible(true);
        String path=dlg.getDirectory()+dlg.getFile();
        try	{
            DataInputStream si=new DataInputStream(new FileInputStream(path));
             /* загрузка параметров горизонтального мотора  */           
            leftLim.setText(""+si.readInt());
            positionM1.setText(""+si.readInt());
            rightLim.setText(""+si.readInt());
            
            /* загрузка параметров мотора с образцом */
            downLimM4.setText(""+si.readInt());
            positionM4.setText(""+si.readInt());
            upLimM4.setText(""+si.readInt()); 
            
            /* загрузка параметров мотора с порошком*/
            downLimM3.setText(""+si.readInt());
            positionM3.setText(""+si.readInt());
            upLimM3.setText(""+si.readInt());               

            /* обновление позиций */
            jSlider1.setMinimum(Integer.parseInt(leftLim.getText()));
            jSlider1.setMaximum(Integer.parseInt(rightLim.getText()));
           
            jSlider1.setValue(Integer.parseInt(positionM1.getText()));                     
            
            jProgressBar1.setMinimum(Integer.parseInt(leftLim.getText()));                
            jProgressBar1.setMaximum(Integer.parseInt(rightLim.getText()));
            jProgressBar1.setValue(Integer.parseInt(positionM1.getText()));
            
            jSlider3.setMinimum(Integer.parseInt(downLimM3.getText()));
            jSlider3.setMaximum(Integer.parseInt(upLimM3.getText()));
            
            jSlider3.setValue(Integer.parseInt(positionM3.getText()));                    
             
            jProgressBar3.setMinimum(Integer.parseInt(downLimM3.getText()));                
            jProgressBar3.setMaximum(Integer.parseInt(upLimM3.getText()));
            jProgressBar3.setValue(Integer.parseInt(positionM3.getText()));               

            jSlider4.setMinimum(Integer.parseInt(downLimM4.getText()));
            jSlider4.setMaximum(Integer.parseInt(upLimM4.getText()));
            
            jSlider4.setValue(Integer.parseInt(positionM4.getText()));                    
            
            jProgressBar4.setMinimum(Integer.parseInt(downLimM4.getText()));                
            jProgressBar4.setMaximum(Integer.parseInt(upLimM4.getText()));
            jProgressBar4.setValue(Integer.parseInt(positionM4.getText()));  
            
            
            si.close();
            }catch (IOException ee) { 
             System.out.println(""+ee);
            }
        
        
    }//GEN-LAST:event_loadBTNActionPerformed

    private void jSlider1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider1MouseReleased
        try{
            /* Вычисляем направление перемещения */
            int oldPos=Integer.parseInt(positionM1.getText());
            int steps=jSlider1.getValue()-oldPos;//Integer.parseInt(positionM1.getText());
            System.out.println("MouseReleased "+jSlider1.getValue()+" steps="+steps);
            startMotor(1, steps);
            set.M1CurrentPos.incValue(steps);
            saveSettings();
            showMotorsPosition();  
            delayForMotors(15);                                                                                                                              
            //positionM1.setText(""+(oldPos+steps));
        }catch(Exception ee){ System.out.println ("Формат целого числа ????");}
    }//GEN-LAST:event_jSlider1MouseReleased

    private void jSlider4MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider4MouseReleased
        try{    
            int oldPos=Integer.parseInt(positionM4.getText());
            int steps=jSlider4.getValue()-oldPos;//Integer.parseInt(positionM1.getText());
            System.out.println("MouseReleased "+jSlider4.getValue()+" steps="+steps);
            startMotor(4, steps);
            set.M4CurrentPos.incValue(steps);
            saveSettings();
            showMotorsPosition();
            delayForMotors(15);                                                                   
             //positionM4.setText(""+(oldPos+steps));
        }catch(Exception ee){ System.out.println ("Формат целого числа ????");}
    }//GEN-LAST:event_jSlider4MouseReleased

    private void jSlider3MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider3MouseReleased
         try{    
            int oldPos=Integer.parseInt(positionM3.getText());
            int steps=jSlider3.getValue()-oldPos;//Integer.parseInt(positionM1.getText());
            System.out.println("MouseReleased "+jSlider3.getValue()+" steps="+steps);
            startMotor(3, steps);
            set.M3CurrentPos.incValue(steps);
            saveSettings();
            showMotorsPosition();
            delayForMotors(15);                                                                                                                   
            //positionM3.setText(""+(oldPos+steps));
        }catch(Exception ee){ System.out.println ("Формат целого числа ????");}
    }//GEN-LAST:event_jSlider3MouseReleased

    private void mot1_Set0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mot1_Set0ActionPerformed
       calibrateMotor(1);
       saveMotorPosition(1,0);
       showMotorsPosition();
        waitForMotors();                                                                            
    }//GEN-LAST:event_mot1_Set0ActionPerformed

    private void positionM1TextValueChanged(java.awt.event.TextEvent evt) {//GEN-FIRST:event_positionM1TextValueChanged
        try{
            if(Integer.parseInt(positionM1.getText())>6000){
            positionM1.setBackground(Color.red);
        }else{
            positionM1.setBackground(Color.white); 
        }
        }catch(Exception ee){ System.out.println ("Формат целого числа ????");}
    }//GEN-LAST:event_positionM1TextValueChanged

    private void mot3_Set0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mot3_Set0ActionPerformed
        calibrateMotor(3);
        saveMotorPosition(3,0);
        showMotorsPosition();
        waitForMotors();                                                                                             
    }//GEN-LAST:event_mot3_Set0ActionPerformed

    private void upLimM4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_upLimM4KeyPressed
//       System.out.println(""+evt.getKeyCode());
       // задаем новый предел 
       if(evt.getKeyCode()==10){//Enter key
        try{
         SetMotorParam(4,0,Integer.parseInt(upLimM4.getText()));
         upLimM4.setBackground(Color.green); 
        }catch(Exception ee){ System.out.println ("Формат целого числа ????");}   
       }
    }//GEN-LAST:event_upLimM4KeyPressed

    private void upLimM3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_upLimM3KeyPressed
       // задаем новый предел 
       if(evt.getKeyCode()==10){ //Enter key
        try{
         SetMotorParam(3,0,Integer.parseInt(upLimM3.getText()));
         upLimM3.setBackground(Color.green); 
        }catch(Exception ee){ System.out.println ("Формат целого числа ????");}   
       }
    }//GEN-LAST:event_upLimM3KeyPressed

    private void upLimM3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upLimM3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_upLimM3ActionPerformed

    private void positionM3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_positionM3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_positionM3ActionPerformed
    
    private void initFieldsVal(){
        upLimM4.setSize(67, 20);
        downLimM4.setSize(67, 20);
        upLimM3.setSize(67, 20);
        downLimM3.setSize(67, 20);
        positionM3.setSize(67,20);
        positionM4.setSize(67,20);
        positionM1.setSize(67,20);        
        rightLim.setSize(67,20);
        leftLim.setSize(67,20);
        rightSensM1.setBackground(Color.gray);                   
        hiSensM4.setBackground(Color.gray);
        hiSensM3.setBackground(Color.gray);              
        leftSensM1.setBackground(Color.gray);             
        loSensM3.setBackground(Color.gray);                
        loSensM4.setBackground(Color.gray);          
        upLimM4.setText("?");
        positionM4.setText("?");
        downLimM4.setText("?");
        upLimM3.setText("?");
        positionM3.setText("?");
        downLimM3.setText("?");
        guideX.setText("0.0");
        guideY.setText("0.0");
        label1.setText("Координаты");
        label2.setText("X");
        label3.setText("Y");
        textField9.setText("");
        label6.setText("Верх.предел");
        label7.setText("Позиция");
        label8.setText("Ниж.предел");
        label9.setText("Верх.предел");
        label10.setText("Ниж.предел");
        label11.setText("Позиция");
        leftLim.setText("?");
        positionM1.setText("?");
        rightLim.setText("?");
        label12.setText("Лазер");
        testOnOff.setLabel("Тест");
        testOnOff.setEnabled(true);       
        sourceOn.setEnabled(true);
        sourceOn.setLabel("Вкл.сеть");
        goXY.setText("GO!");
        powerWait.setEnabled(false);
        powerWait.setLabel("Мощность");
        emissionOn.setEnabled(false);
        emissionOn.setLabel("Эмиссия");
        guideOn.setEnabled(false);
        guideOn.setLabel("Указатель");
        modulationOn.setEnabled(false);
        modulationOn.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        modulationOn.setForeground(new java.awt.Color(255, 0, 0));
        modulationOn.setLabel("Модуляция");
        setPRM.setText("ЗАДАТЬ");
        setM1.setText("УстМ1");
        jButton1.setText("УстМ3");
        jButton2.setText("Обновить");        
        jButton3.setText("УстМ4");
        Velocity.setText("V=");
        testOnOff.setLabel("ВКЛ мотор смены слоя");
//        jProgressBar2.setBounds(100,100,10,50);
        jProgressBar4.setOrientation(JProgressBar.VERTICAL);
 //       jProgressBar2.setSize(100,10);        
        jProgressBar3.setOrientation(JProgressBar.VERTICAL);        
    }



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Velocity;
    private java.awt.Canvas canvas1;
    private java.awt.TextField downLimM3;
    private java.awt.TextField downLimM4;
    private java.awt.Checkbox emissionOn;
    private javax.swing.JToggleButton goXY;
    private java.awt.Checkbox guideOn;
    private java.awt.TextField guideX;
    private java.awt.TextField guideY;
    private java.awt.Panel hiSensM3;
    private java.awt.Panel hiSensM4;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JProgressBar jProgressBar3;
    private javax.swing.JProgressBar jProgressBar4;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JSlider jSlider3;
    private javax.swing.JSlider jSlider4;
    private java.awt.Label label1;
    private java.awt.Label label10;
    private java.awt.Label label11;
    private java.awt.Label label12;
    private java.awt.Label label15;
    private java.awt.Label label2;
    private java.awt.Label label3;
    private java.awt.Label label4;
    private java.awt.Label label5;
    private java.awt.Label label6;
    private java.awt.Label label7;
    private java.awt.Label label8;
    private java.awt.Label label9;
    private java.awt.Checkbox laserErr;
    private java.awt.TextField leftLim;
    private java.awt.Panel leftSensM1;
    private java.awt.Panel loSensM3;
    private java.awt.Panel loSensM4;
    private javax.swing.JButton loadBTN;
    private java.awt.TextField microstep;
    private java.awt.Checkbox modulationOn;
    private javax.swing.JButton mot1_Set0;
    private javax.swing.JButton mot3_Set0;
    private javax.swing.JButton mot4_Set0;
    private java.awt.TextField newMotorPosition1;
    private java.awt.TextField newMotorPosition3;
    private java.awt.TextField newMotorPosition4;
    private java.awt.TextField positionM1;
    private java.awt.TextField positionM3;
    private java.awt.TextField positionM4;
    private java.awt.Checkbox powerWait;
    private java.awt.TextField rightLim;
    private java.awt.Panel rightSensM1;
    private javax.swing.JButton saveBTN;
    private javax.swing.JButton setM1;
    private javax.swing.JButton setPRM;
    private javax.swing.JButton softReset;
    private java.awt.Checkbox sourceOn;
    private java.awt.TextField stepPowder;
    private java.awt.TextField stepSample;
    private java.awt.Checkbox testOnOff;
    private java.awt.TextField textField9;
    private java.awt.TextField upLimM3;
    private java.awt.TextField upLimM4;
    // End of variables declaration//GEN-END:variables
}
