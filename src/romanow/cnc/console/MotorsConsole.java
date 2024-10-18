/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.console;

import romanow.cnc.commands.Command;
import romanow.cnc.commands.CommandIntList;
import romanow.cnc.controller.USBBack;
import romanow.cnc.controller.USBCodes;
import romanow.cnc.controller.USBProtocol;
import romanow.cnc.view.BaseFrame;
import romanow.cnc.utils.Events;
import romanow.cnc.m3d.OK;
import romanow.cnc.settings.GlobalSettings;
import romanow.cnc.settings.WorkSpace;
import romanow.cnc.utils.I_Notify;
import romanow.cnc.utils.Utils;
import romanow.cnc.Values;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author romanow
 */
public class MotorsConsole extends BaseFrame {

    /**
     * Creates new form MotorsConsole
     */
    private USBProtocol usb;                        // Драйвер протокола
    private USBBack back_p;                         // Родительский обработчик событий протокола
    private USBBack back;                           // Собственный обработчик событий протокола
    private I_Notify notify;
    private int mdata[][]={null,null,null,null};    // Массивы ответных данных моторов
    private int High0[]={18800,0,11255,11463};      // Верхние границы
    private GlobalSettings set = WorkSpace.ws().global().global;
    private static int motorWait=2;
    private TextField FHigh[]=new TextField[4];     // Массив ссылок на текстовые поля
    private TextField FLow[]=new TextField[4];      // с параметрами моторов
    private TextField FPos[]=new TextField[4];
    private JProgressBar Prg[]=new JProgressBar[4]; // Массив прогресс-баров моторов
    private JSlider Sld[]=new JSlider[4];           // Массив слайдеров моторов
    private Panel SensH[]=new Panel[4];             // Массив индикаторов-концевиков
    private Panel SensL[]=new Panel[4];
    private int askMotorCount=0;
    private int askMotorValue=0;

    private void runWhenState(final Runnable code){
        usb.runWhenState(USBCodes.STATE_STANDBY, USBCodes.STATE_TEST,30,notify,code);
        }

    private void waitForMotors(final int num){
        runWhenState(()->{ askMotorState(num); });
        }
    private void delayForMotorsRetry(int delay,final int num){
        Utils.runAfterDelay(delay,()->{ askMotorState(num); });
        }
    private void delayForMotors(int delay,final int num){
        Utils.runAfterDelay(delay,()->{ askMotorState(5,num); });
    }
    private void askMotorState(int num){
        oneCommand(new CommandIntList(USBCodes.GetMotorStatus,num));
        }
    private void askMotorState(int count, int num){
        askMotorCount=count;
        if (mdata[num-1]==null)
            askMotorValue=0;
        else
            askMotorValue=mdata[num-1][4];
        askMotorState(num);
        }
    private void oneCommand(Command cmd){
        int xx[] = cmd.toIntArray();
        usb.oneCommand(xx,back);
        if (cmd.code()==USBCodes.SetMotorParam)
            notify.log("++Команда установки параметров мотора "+xx[0]+"["+xx[1]+","+xx[2]+","+xx[3]+"]");
        }
    private void askMotorsState(){
        askMotorState(1);
        askMotorState(3);
        askMotorState(4);
        }
    @Override
    public void onEvent(int code,boolean on, int value, String name) {
        super.onEvent(code,on,value,name);
        if (code== Events.Motors)
            askMotorsState();
        }    
    
    public MotorsConsole(USBProtocol usb0,USBBack back0,I_Notify notify0) {
        if (!tryToStart()) return;
        initComponents();
        setTitle("Моторы");
        setBounds(600,200,370,450);
        usb = usb0;
        notify = notify0;
        back_p = back0;
        M1Sld.setMinimum(0);
        M1Sld.setMaximum(High0[0]);
        M1Prg.setMinimum(0);
        M1Prg.setMaximum(High0[0]);
        M3Sld.setMinimum(0);
        M3Sld.setMaximum(High0[2]);
        M3Prg.setMinimum(0);
        M3Prg.setMaximum(High0[2]);
        M4Sld.setMinimum(0);
        M4Sld.setMaximum(High0[3]);
        M4Prg.setMinimum(0);
        M4Prg.setMaximum(High0[3]);
        //-------------------------------------------------------------------------------------
        FLow[0]=M1Left;
        FLow[2]=M3Low;
        FLow[3]=M4Low;
        FHigh[0]=M1Right;
        FHigh[2]=M3High;
        FHigh[3]=M4High;
        FPos[0]=M1Cur;
        FPos[2]=M3Pos;
        FPos[3]=M4Pos;
        Prg[0]=M1Prg;
        Prg[2]=M3Prg;
        Prg[3]=M4Prg;
        Sld[0]=M1Sld;
        Sld[2]=M3Sld;
        Sld[3]=M4Sld;
        SensL[0]=M1SensL;
        SensL[2]=M3SensL;
        SensL[3]=M4SensL;
        SensH[0]=M1SensH;
        SensH[2]=M3SensH;
        SensH[3]=M4SensH;
        //------------------------------------------------------------------------------------
        M3Prg.setOrientation(JProgressBar.VERTICAL);
        M4Prg.setOrientation(JProgressBar.VERTICAL);
        back = new USBBack() {
            @Override
            public void onSuccess(int code, int[] data) {
                if (code== USBCodes.GetMotorStatus){
                    int idx = data[1]-1;
                    mdata[idx]=data;
                    FLow[idx].setText(""+data[2]);
                    FHigh[idx].setText(""+data[3]);
                    FPos[idx].setText(""+data[4]);
                    Prg[idx].setValue(data[4]);
                    System.out.format("Мотор = %x", data[6]);
                    SensH[idx].setBackground((data[6] & 1)==0 ? Color.gray : Color.red);
                    SensL[idx].setBackground((data[6] & 2)==0 ? Color.gray : Color.red);
                    usb.saveMotorPosition(data,idx);
                    notify.log("Мотор "+data[1]+" позиция "+data[4]);
                    if (data[6]<0){                 // Мотор движется [31]==1
                        askMotorCount--;
                        askMotorValue = data[4];
                        delayForMotorsRetry(motorWait,data[1]);
                        }
                    else
                        Sld[idx].setValue(data[4]);
                    }
                else
                    back_p.onSuccess(code,data);
                }
            @Override
            public void onError(int cmd, int[] data) {
                back_p.onError(cmd,data);
                }
            @Override
            public void onFatal(int errorCode, String message) {
                back_p.onFatal(errorCode,message);
                }
            };
        }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        M4Low = new java.awt.TextField();
        M3High = new java.awt.TextField();
        M4Calibrate = new javax.swing.JButton();
        M3Pos = new java.awt.TextField();
        M1Prg = new javax.swing.JProgressBar();
        M3Low = new java.awt.TextField();
        M4Prg = new javax.swing.JProgressBar();
        M3Prg = new javax.swing.JProgressBar();
        M1Calibrate = new javax.swing.JButton();
        M1Sld = new javax.swing.JSlider();
        M3Calibrate = new javax.swing.JButton();
        label6 = new java.awt.Label();
        label7 = new java.awt.Label();
        label8 = new java.awt.Label();
        label9 = new java.awt.Label();
        label10 = new java.awt.Label();
        label11 = new java.awt.Label();
        M1Left = new java.awt.TextField();
        M1Cur = new java.awt.TextField();
        M1Right = new java.awt.TextField();
        M4Add = new java.awt.TextField();
        M3Add = new java.awt.TextField();
        M3Move = new javax.swing.JButton();
        M1SensH = new java.awt.Panel();
        M4Move = new javax.swing.JButton();
        M1SensL = new java.awt.Panel();
        M4Sld = new javax.swing.JSlider();
        M3Sld = new javax.swing.JSlider();
        M4SensH = new java.awt.Panel();
        M3SensH = new java.awt.Panel();
        M4SensL = new java.awt.Panel();
        M3SensL = new java.awt.Panel();
        M4High = new java.awt.TextField();
        M4Pos = new java.awt.TextField();
        jLabel1 = new javax.swing.JLabel();
        M1Move = new javax.swing.JButton();
        M1Add = new java.awt.TextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });
        getContentPane().setLayout(null);

        M4Low.setMinimumSize(new java.awt.Dimension(80, 20));
        M4Low.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                M4LowKeyPressed(evt);
            }
        });
        getContentPane().add(M4Low);
        M4Low.setBounds(20, 260, 80, 20);

        M3High.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                M3HighKeyPressed(evt);
            }
        });
        getContentPane().add(M3High);
        M3High.setBounds(200, 160, 80, 20);

        M4Calibrate.setText(">0<");
        M4Calibrate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                M4CalibrateActionPerformed(evt);
            }
        });
        getContentPane().add(M4Calibrate);
        M4Calibrate.setBounds(120, 320, 55, 23);

        M3Pos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                M3PosActionPerformed(evt);
            }
        });
        getContentPane().add(M3Pos);
        M3Pos.setBounds(200, 210, 76, 20);
        getContentPane().add(M1Prg);
        M1Prg.setBounds(100, 40, 230, 14);

        M3Low.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                M3LowKeyPressed(evt);
            }
        });
        getContentPane().add(M3Low);
        M3Low.setBounds(200, 260, 76, 20);

        M4Prg.setMinimumSize(new java.awt.Dimension(200, 14));
        M4Prg.setName(""); // NOI18N
        M4Prg.setPreferredSize(new java.awt.Dimension(200, 14));
        M4Prg.setRequestFocusEnabled(false);
        getContentPane().add(M4Prg);
        M4Prg.setBounds(140, 140, 13, 170);

        M3Prg.setMinimumSize(new java.awt.Dimension(200, 14));
        M3Prg.setName(""); // NOI18N
        M3Prg.setPreferredSize(new java.awt.Dimension(200, 14));
        getContentPane().add(M3Prg);
        M3Prg.setBounds(310, 140, 14, 170);

        M1Calibrate.setText(">0<");
        M1Calibrate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                M1CalibrateActionPerformed(evt);
            }
        });
        getContentPane().add(M1Calibrate);
        M1Calibrate.setBounds(30, 20, 55, 23);

        M1Sld.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                M1SldMouseReleased(evt);
            }
        });
        getContentPane().add(M1Sld);
        M1Sld.setBounds(100, 60, 240, 23);

        M3Calibrate.setText(">0<");
        M3Calibrate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                M3CalibrateActionPerformed(evt);
            }
        });
        getContentPane().add(M3Calibrate);
        M3Calibrate.setBounds(290, 320, 55, 23);

        label6.setText("Верх.предел");
        getContentPane().add(label6);
        label6.setBounds(20, 140, 80, 20);

        label7.setText("Позиция");
        getContentPane().add(label7);
        label7.setBounds(20, 190, 60, 20);

        label8.setText("Ниж.предел");
        getContentPane().add(label8);
        label8.setBounds(20, 240, 76, 20);

        label9.setText("Верх.предел");
        getContentPane().add(label9);
        label9.setBounds(200, 140, 76, 20);

        label10.setText("Ниж.предел");
        getContentPane().add(label10);
        label10.setBounds(200, 240, 74, 20);

        label11.setText("Позиция");
        getContentPane().add(label11);
        label11.setBounds(200, 190, 76, 20);
        getContentPane().add(M1Left);
        M1Left.setBounds(100, 90, 70, 20);

        M1Cur.addTextListener(new java.awt.event.TextListener() {
            public void textValueChanged(java.awt.event.TextEvent evt) {
                M1CurTextValueChanged(evt);
            }
        });
        getContentPane().add(M1Cur);
        M1Cur.setBounds(180, 90, 70, 20);
        getContentPane().add(M1Right);
        M1Right.setBounds(260, 90, 70, 20);

        M4Add.setMaximumSize(new java.awt.Dimension(80, 20));
        M4Add.setMinimumSize(new java.awt.Dimension(80, 20));
        M4Add.setName(""); // NOI18N
        M4Add.setText("0");
        M4Add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                M4AddActionPerformed(evt);
            }
        });
        getContentPane().add(M4Add);
        M4Add.setBounds(20, 290, 80, 20);

        M3Add.setMaximumSize(new java.awt.Dimension(70, 20));
        M3Add.setMinimumSize(new java.awt.Dimension(70, 20));
        M3Add.setName(""); // NOI18N
        M3Add.setText("0");
        getContentPane().add(M3Add);
        M3Add.setBounds(200, 290, 73, 20);

        M3Move.setText("+/-");
        M3Move.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                M3MoveActionPerformed(evt);
            }
        });
        getContentPane().add(M3Move);
        M3Move.setBounds(210, 320, 50, 23);

        M1SensH.setBackground(new java.awt.Color(51, 153, 0));
        M1SensH.setName("RightSens"); // NOI18N

        javax.swing.GroupLayout M1SensHLayout = new javax.swing.GroupLayout(M1SensH);
        M1SensH.setLayout(M1SensHLayout);
        M1SensHLayout.setHorizontalGroup(
            M1SensHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );
        M1SensHLayout.setVerticalGroup(
            M1SensHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
        );

        getContentPane().add(M1SensH);
        M1SensH.setBounds(310, 20, 15, 14);

        M4Move.setText("+/-");
        M4Move.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                M4MoveActionPerformed(evt);
            }
        });
        getContentPane().add(M4Move);
        M4Move.setBounds(30, 320, 50, 23);

        M1SensL.setBackground(new java.awt.Color(51, 153, 0));
        M1SensL.setName("LeftSens"); // NOI18N

        javax.swing.GroupLayout M1SensLLayout = new javax.swing.GroupLayout(M1SensL);
        M1SensL.setLayout(M1SensLLayout);
        M1SensLLayout.setHorizontalGroup(
            M1SensLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );
        M1SensLLayout.setVerticalGroup(
            M1SensLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
        );

        getContentPane().add(M1SensL);
        M1SensL.setBounds(100, 20, 15, 14);

        M4Sld.setOrientation(javax.swing.JSlider.VERTICAL);
        M4Sld.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                M4SldMouseReleased(evt);
            }
        });
        getContentPane().add(M4Sld);
        M4Sld.setBounds(110, 140, 30, 180);

        M3Sld.setOrientation(javax.swing.JSlider.VERTICAL);
        M3Sld.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                M3SldMouseReleased(evt);
            }
        });
        getContentPane().add(M3Sld);
        M3Sld.setBounds(290, 140, 10, 180);

        M4SensH.setBackground(new java.awt.Color(51, 153, 0));
        M4SensH.setName("RightSens"); // NOI18N

        javax.swing.GroupLayout M4SensHLayout = new javax.swing.GroupLayout(M4SensH);
        M4SensH.setLayout(M4SensHLayout);
        M4SensHLayout.setHorizontalGroup(
            M4SensHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );
        M4SensHLayout.setVerticalGroup(
            M4SensHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
        );

        getContentPane().add(M4SensH);
        M4SensH.setBounds(160, 140, 15, 14);

        M3SensH.setBackground(new java.awt.Color(0, 153, 0));
        M3SensH.setName("RightSens"); // NOI18N

        javax.swing.GroupLayout M3SensHLayout = new javax.swing.GroupLayout(M3SensH);
        M3SensH.setLayout(M3SensHLayout);
        M3SensHLayout.setHorizontalGroup(
            M3SensHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );
        M3SensHLayout.setVerticalGroup(
            M3SensHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
        );

        getContentPane().add(M3SensH);
        M3SensH.setBounds(330, 140, 15, 14);

        M4SensL.setBackground(new java.awt.Color(0, 153, 0));
        M4SensL.setName("RightSens"); // NOI18N

        javax.swing.GroupLayout M4SensLLayout = new javax.swing.GroupLayout(M4SensL);
        M4SensL.setLayout(M4SensLLayout);
        M4SensLLayout.setHorizontalGroup(
            M4SensLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );
        M4SensLLayout.setVerticalGroup(
            M4SensLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
        );

        getContentPane().add(M4SensL);
        M4SensL.setBounds(160, 290, 15, 14);

        M3SensL.setBackground(new java.awt.Color(51, 153, 0));
        M3SensL.setName("RightSens"); // NOI18N

        javax.swing.GroupLayout M3SensLLayout = new javax.swing.GroupLayout(M3SensL);
        M3SensL.setLayout(M3SensLLayout);
        M3SensLLayout.setHorizontalGroup(
            M3SensLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );
        M3SensLLayout.setVerticalGroup(
            M3SensLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
        );

        getContentPane().add(M3SensL);
        M3SensL.setBounds(330, 290, 15, 14);

        M4High.setMinimumSize(new java.awt.Dimension(80, 20));
        M4High.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                M4HighKeyPressed(evt);
            }
        });
        getContentPane().add(M4High);
        M4High.setBounds(20, 160, 80, 20);

        M4Pos.setMinimumSize(new java.awt.Dimension(80, 20));
        getContentPane().add(M4Pos);
        M4Pos.setBounds(20, 210, 76, 20);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Бункер");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(200, 120, 50, 10);

        M1Move.setText("+/-");
        M1Move.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                M1MoveActionPerformed(evt);
            }
        });
        getContentPane().add(M1Move);
        M1Move.setBounds(30, 60, 50, 23);

        M1Add.setMaximumSize(new java.awt.Dimension(60, 20));
        M1Add.setMinimumSize(new java.awt.Dimension(60, 20));
        M1Add.setName(""); // NOI18N
        M1Add.setText("0");
        getContentPane().add(M1Add);
        M1Add.setBounds(20, 90, 70, 20);

        jButton2.setText("Обновить");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2);
        jButton2.setBounds(210, 350, 130, 30);

        jButton3.setBackground(new java.awt.Color(255, 51, 0));
        jButton3.setText("Восстановить из настроек");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3);
        jButton3.setBounds(20, 350, 180, 30);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("Моторы");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(140, 13, 60, 17);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Ракель");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(220, 20, 50, 10);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Деталь");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(20, 120, 50, 10);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void M3HighKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_M3HighKeyPressed
        if(evt.getKeyCode()==10){//Enter key
            try {
                int hh = Integer.parseInt(M3High.getText());
                set.M3HighPos.setVal(hh);
                ws().saveSettings();
                M3High.setBackground(Color.green);
                oneCommand(new CommandIntList(USBCodes.SetMotorParam,3,set.M3LowPos.getVal(),
                    set.M3HighPos.getVal(),set.M3CurrentPos.getVal()));
                }catch(Exception ee){ notify.notify(Values.error,"Формат целого числа ????");}
        }
    }//GEN-LAST:event_M3HighKeyPressed

    private void M4CalibrateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_M4CalibrateActionPerformed
        set.M4LowPos.setVal(0);
        set.M4CurrentPos.setVal(0);
        set.M4HighPos.setVal(High0[3]);
        WorkSpace.ws().saveSettings();
        oneCommand(new CommandIntList(USBCodes.StartTest,4,0,0));
        oneCommand(new CommandIntList(USBCodes.SetMotorParam,4,set.M4LowPos.getVal(),
            set.M4HighPos.getVal(),set.M4CurrentPos.getVal()));
        delayForMotors(motorWait,4);
        //SetMotorParam(4,0,4000);
        //calibrateMotor(4);
        //saveMotorPosition(4,0);
        //showMotorsPosition();
        //waitForMotors();
    }//GEN-LAST:event_M4CalibrateActionPerformed

    private void M3PosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_M3PosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_M3PosActionPerformed

    private void M1CalibrateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_M1CalibrateActionPerformed
        set.M1LowPos.setVal(0);
        set.M1CurrentPos.setVal(0);
        set.M1HighPos.setVal(High0[0]);
        WorkSpace.ws().saveSettings();
        oneCommand(new CommandIntList(USBCodes.StartTest,1,0,0));        
        oneCommand(new CommandIntList(USBCodes.SetMotorParam,1,set.M1LowPos.getVal(),
            set.M1HighPos.getVal(),set.M1CurrentPos.getVal()));
        delayForMotors(motorWait,1);
        //calibrateMotor(1);
        //saveMotorPosition(1,0);
        //showMotorsPosition();
        //waitForMotors();
    }//GEN-LAST:event_M1CalibrateActionPerformed

    private void M1SldMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_M1SldMouseReleased
        try{
            /* Вычисляем направление перемещения */
            int oldPos=Integer.parseInt(M1Cur.getText());
            int steps=M1Sld.getValue()-oldPos;      
            //Integer.parseInt(positionM1.getText());
            oneCommand(new CommandIntList(USBCodes.GoMotorPos,1,steps));
            delayForMotors(motorWait,1);
            }   catch(Exception ee){ notify.notify(Values.error,"Формат целого числа ????"); }
    }//GEN-LAST:event_M1SldMouseReleased

    private void M3CalibrateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_M3CalibrateActionPerformed
        set.M3LowPos.setVal(0);
        set.M3CurrentPos.setVal(0);
        set.M3HighPos.setVal(High0[2]);
        WorkSpace.ws().saveSettings();
        oneCommand(new CommandIntList(USBCodes.StartTest,3,0,0));        
        oneCommand(new CommandIntList(USBCodes.SetMotorParam,3,set.M3LowPos.getVal(),
            set.M3HighPos.getVal(),set.M3CurrentPos.getVal()));
        delayForMotors(motorWait,3);        
        //calibrateMotor(3);
        //saveMotorPosition(3,0);
        //showMotorsPosition();
        //waitForMotors();
    }//GEN-LAST:event_M3CalibrateActionPerformed

    private void M1CurTextValueChanged(java.awt.event.TextEvent evt) {//GEN-FIRST:event_M1CurTextValueChanged
        try{
            if(Integer.parseInt(M1Cur.getText())>6000){
                M1Cur.setBackground(Color.red);
            }else{
                M1Cur.setBackground(Color.white);
            }
        }catch(Exception ee){ System.out.println ("Формат целого числа ????");}
    }//GEN-LAST:event_M1CurTextValueChanged

    private void M4AddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_M4AddActionPerformed
        /*
        final JProgressBar pb = new JProgressBar();
        pb.setMinimum(0);
        pb.setMaximum(MAX);
        pb.setStringPainted(true);
        pb.setOrientation(JProgressBar.VERTICAL);
        */
    }//GEN-LAST:event_M4AddActionPerformed

    private void M3MoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_M3MoveActionPerformed
        int oldPos=Integer.parseInt(M3Pos.getText());
        int steps=Integer.parseInt(M3Add.getText());
        if (oldPos+steps < set.M3LowPos.getVal()){
            notify.notify(Values.error, "Установлено на границу");
            steps = set.M3LowPos.getVal()-oldPos;
            }
        if (oldPos+steps > set.M3HighPos.getVal()){
            notify.notify(Values.error, "Установлено на границу");
            steps = set.M3HighPos.getVal()-oldPos;
            }
        oneCommand(new CommandIntList(USBCodes.GoMotorPos,3,steps));
        delayForMotors(motorWait,3);
    }//GEN-LAST:event_M3MoveActionPerformed

    private void M4MoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_M4MoveActionPerformed
        int oldPos=Integer.parseInt(M4Pos.getText());
        int steps=Integer.parseInt(M4Add.getText());
        if (oldPos+steps < set.M4LowPos.getVal()){
            notify.notify(Values.error, "Установлено на границу");
            steps = set.M4LowPos.getVal()-oldPos;
            }
        if (oldPos+steps > set.M4HighPos.getVal()){
            notify.notify(Values.error, "Установлено на границу");
            steps = set.M4HighPos.getVal()-oldPos;
            }
        oneCommand(new CommandIntList(USBCodes.GoMotorPos,4,steps));
        delayForMotors(motorWait,4);
    }//GEN-LAST:event_M4MoveActionPerformed

    private void M4SldMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_M4SldMouseReleased
        try{
            int oldPos=Integer.parseInt(M4Pos.getText());
            int newPos = M4Sld.getValue();
            if (newPos < set.M4LowPos.getVal()){
                notify.notify(Values.error, "Установлено на границу");
                newPos = set.M4LowPos.getVal();
                }
            if (newPos > set.M4HighPos.getVal()){
                notify.notify(Values.error, "Установлено на границу");
                newPos = set.M4HighPos.getVal();
                }
            int steps=newPos-oldPos;//Integer.parseInt(positionM1.getText());
            oneCommand(new CommandIntList(USBCodes.GoMotorPos,4,steps));
            delayForMotors(motorWait,4);
        }catch(Exception ee){ notify.notify(Values.error,"Формат целого числа ????");}
    }//GEN-LAST:event_M4SldMouseReleased

    private void M3SldMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_M3SldMouseReleased
        try{
            int oldPos=Integer.parseInt(M3Pos.getText());
            int newPos = M3Sld.getValue();
            if (newPos < set.M3LowPos.getVal()){
                notify.notify(Values.error, "Установлено на границу");
                newPos = set.M3LowPos.getVal();
                }
            if (newPos > set.M3HighPos.getVal()){
                notify.notify(Values.error, "Установлено на границу");
                newPos = set.M3HighPos.getVal();
                }
            int steps=newPos-oldPos;//Integer.parseInt(positionM1.getText());
            oneCommand(new CommandIntList(USBCodes.GoMotorPos,3,steps));
            delayForMotors(motorWait,3);
        }   catch(Exception ee){ notify.notify(Values.error,"Формат целого числа ????"); }
    }//GEN-LAST:event_M3SldMouseReleased

    private void M4HighKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_M4HighKeyPressed
        if(evt.getKeyCode()==10){//Enter key
            try {
                int hh = Integer.parseInt(M4High.getText());
                set.M4HighPos.setVal(hh);
                ws().saveSettings();
                M4High.setBackground(Color.green);
                oneCommand(new CommandIntList(USBCodes.SetMotorParam,4,set.M4LowPos.getVal(),
                    set.M4HighPos.getVal(),set.M4CurrentPos.getVal()));
                }catch(Exception ee){ notify.notify(Values.error,"Формат целого числа ????");}
        }
    }//GEN-LAST:event_M4HighKeyPressed

    private void M1MoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_M1MoveActionPerformed
        int steps=Integer.parseInt(M1Add.getText());
        oneCommand(new CommandIntList(USBCodes.GoMotorPos,1,steps));
        delayForMotors(motorWait,1);

    }//GEN-LAST:event_M1MoveActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        onClose();
    }//GEN-LAST:event_formWindowClosed

    private void restore(){
        notify.log("Восстановление позиций моторов");
        int v1=0,v2=0,v3=0;
        v1 = set.M1LowPos.getVal();
        v2 = set.M1HighPos.getVal();
        v3 = set.M1CurrentPos.getVal();
        notify.log( String.format("Команда: параметры мотора 1 =%4d,%4d,%4d",v1,v2,v3));
        oneCommand(new CommandIntList(USBCodes.SetMotorParam,1,v1,v2,v3));
        v1 = set.M3LowPos.getVal();
        v2 = set.M3HighPos.getVal();
        v3 = set.M3CurrentPos.getVal();
        notify.log( String.format("Команда: параметры мотора 3 =%4d,%4d,%4d",v1,v2,v3));
        oneCommand(new CommandIntList(USBCodes.SetMotorParam,3,v1,v2,v3));
        v1 = set.M4LowPos.getVal();
        v2 = set.M4HighPos.getVal();
        v3 = set.M4CurrentPos.getVal();
        notify.log( String.format("Команда: параметры мотора 4 =%4d,%4d,%4d",v1,v2,v3));
        oneCommand(new CommandIntList(USBCodes.SetMotorParam,4,v1,v2,v3));
        Sld[0].setValue(set.M1CurrentPos.getVal());
        Sld[2].setValue(set.M3CurrentPos.getVal());
        Sld[3].setValue(set.M4CurrentPos.getVal());
        FLow[0].setText(""+set.M1LowPos.getVal());
        FLow[2].setText(""+set.M3LowPos.getVal());
        FLow[3].setText(""+set.M4LowPos.getVal());
        FHigh[0].setText(""+set.M1HighPos.getVal());
        FHigh[2].setText(""+set.M3HighPos.getVal());
        FHigh[3].setText(""+set.M4HighPos.getVal());
        FPos[0].setText(""+set.M1CurrentPos.getVal());
        FPos[2].setText(""+set.M3CurrentPos.getVal());
        FPos[3].setText(""+set.M4CurrentPos.getVal());
        Prg[0].setValue(set.M1CurrentPos.getVal());
        Prg[2].setValue(set.M3CurrentPos.getVal());
        Prg[3].setValue(set.M4CurrentPos.getVal());
        //askMotorsState();     // Только в одном направлении
        }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        askMotorsState();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        new OK(getBounds(),"Восстановить",()->{restore(); } );
    }//GEN-LAST:event_jButton3ActionPerformed

    private void M4LowKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_M4LowKeyPressed
        if(evt.getKeyCode()==10){//Enter key
            try {
                int hh = Integer.parseInt(M4Low.getText());
                set.M4LowPos.setVal(hh);
                ws().saveSettings();
                M4Low.setBackground(Color.green);
                oneCommand(new CommandIntList(USBCodes.SetMotorParam,4,set.M4LowPos.getVal(),
                    set.M4HighPos.getVal(),set.M4CurrentPos.getVal()));
                }catch(Exception ee){ notify.notify(Values.error,"Формат целого числа ????");}
        }
    }//GEN-LAST:event_M4LowKeyPressed

    private void M3LowKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_M3LowKeyPressed
        if(evt.getKeyCode()==10){//Enter key
            try {
                int hh = Integer.parseInt(M3Low.getText());
                set.M3LowPos.setVal(hh);
                ws().saveSettings();
                M3Low.setBackground(Color.green);
                oneCommand(new CommandIntList(USBCodes.SetMotorParam,3,set.M3LowPos.getVal(),
                    set.M3HighPos.getVal(),set.M3CurrentPos.getVal()));
                }catch(Exception ee){ notify.notify(Values.error,"Формат целого числа ????");}
        }
    }//GEN-LAST:event_M3LowKeyPressed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.TextField M1Add;
    private javax.swing.JButton M1Calibrate;
    private java.awt.TextField M1Cur;
    private java.awt.TextField M1Left;
    private javax.swing.JButton M1Move;
    private javax.swing.JProgressBar M1Prg;
    private java.awt.TextField M1Right;
    private java.awt.Panel M1SensH;
    private java.awt.Panel M1SensL;
    private javax.swing.JSlider M1Sld;
    private java.awt.TextField M3Add;
    private javax.swing.JButton M3Calibrate;
    private java.awt.TextField M3High;
    private java.awt.TextField M3Low;
    private javax.swing.JButton M3Move;
    private java.awt.TextField M3Pos;
    private javax.swing.JProgressBar M3Prg;
    private java.awt.Panel M3SensH;
    private java.awt.Panel M3SensL;
    private javax.swing.JSlider M3Sld;
    private java.awt.TextField M4Add;
    private javax.swing.JButton M4Calibrate;
    private java.awt.TextField M4High;
    private java.awt.TextField M4Low;
    private javax.swing.JButton M4Move;
    private java.awt.TextField M4Pos;
    private javax.swing.JProgressBar M4Prg;
    private java.awt.Panel M4SensH;
    private java.awt.Panel M4SensL;
    private javax.swing.JSlider M4Sld;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private java.awt.Label label10;
    private java.awt.Label label11;
    private java.awt.Label label6;
    private java.awt.Label label7;
    private java.awt.Label label8;
    private java.awt.Label label9;
    // End of variables declaration//GEN-END:variables

}
