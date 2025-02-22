/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package romanow.cnc.view;

import romanow.cnc.io.COMPortGDriver;
import romanow.cnc.io.I_COMPortGReceiver;
import romanow.cnc.settings.MashineSettings;
import romanow.cnc.slicer.SliceData;
import romanow.cnc.slicer.SliceDataGenerator;
import romanow.cnc.stl.GCodeLayer;
import romanow.cnc.stl.I_STLPoint2D;
import romanow.cnc.stl.STLLine;
import romanow.cnc.stl.STLPoint2D;
import romanow.cnc.utils.Events;
import romanow.cnc.utils.Pair;
import romanow.cnc.utils.UNIException;
import romanow.cnc.utils.Utils;
import romanow.cnc.Values;
import romanow.cnc.settings.WorkSpace;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import romanow.cnc.m3d.ViewAdapter;
import romanow.cnc.m3d.ViewNotifyer;
import romanow.cnc.view.design.JCheckBoxButton;

import static romanow.cnc.Values.*;
import static romanow.cnc.utils.Utils.viewUpdate;

/**
 *
 * @author Admin
 */
public class CNCViewerPanel extends BasePanel {
    private WorkSpace ws=null;
    private int comPortState= ComPortStateOff;
    private final COMPortGDriver driver = new COMPortGDriver();
    private final static int SavedMaxSize=50;
    private final static int KeyCodeUp=38;
    private final static int KeyCodeEnter=10;
    private ArrayList<String> savedGCodes = new ArrayList<>();
    private int lastSavedCount=0;
    private JCheckBoxButton relative;
    private JCheckBoxButton pauseButton;
    private ViewNotifyer notify;
    private BufferedWriter logFile = null;
    private ViewAdapter viewCommon = new ViewAdapter(null);
    private boolean stopOnWarning=false;
    /**
     * Creates new form CNCViewerPanel
     */
    public CNCViewerPanel(CNCViewer baseFrame) {
        super(baseFrame);
        initComponents();
        relative = new JCheckBoxButton(Relative);
        relative.setSelected(false);
        pauseButton = new JCheckBoxButton(Pause,"pause-48","play-48");
        pauseButton.setSelected(false);
        pauseButton.setEnabled(false);
        CNCReset.setEnabled(false);
        Stop.setEnabled(false);
        ws = WorkSpace.ws();
        Dimension dim = ws.getDim();
        if (dim.width!=0)
            setComponentsScale();
        SliceMode.removeAll();
        setComPortState(ComPortStateOff);
        for(String ss : SliceModes)
            SliceMode.addItem(ss);
        setMenuVisible();
        ViewAdapter viewCommon = new ViewAdapter(){       // Объект-адаптер для визуальных методов
            @Override
            public boolean onStepLine() {
                if (BYSTEP.isSelected()){
                    pause(true);
                    pauseButton.setSelected(true);
                    }
                return super.onStepLine();
            }
            @Override
            public boolean onStepLayer() {
                if (BYSTEP.isSelected()){
                    pause(true);
                    pauseButton.setSelected(true);
                    }
                return super.onStepLayer();
            }
        };
        notify = new ViewNotifyer(LOG,null){
            @Override
            public void notify(final int level0, final String mes) {
                super.notify(level0, mes);
                java.awt.EventQueue.invokeLater(
                        ()->{
                            if (level0>= Values.important && logFile!=null){
                                try {
                                    logFile.write(Utils.currentTime()+ " "+mes);
                                    logFile.newLine();
                                } catch (IOException ex) { closeLogFile(ex);}
                            }
                            if (level0>=Values.error){
                                viewCommon.finish();
                                }
                            if (level0 >Values.warning || stopOnWarning && level0==Values.warning){           // выше warning  - приостановить
                                if (viewCommon.isRunning()) {
                                    viewCommon.pause(true);
                                    }
                            }
                        });
            }
        };
        ws.setNotify(notify,viewCommon);
        }

    private void toLog(String ss){
        WorkSpace.ws().getNotify().notify(common,ss);
        }

    @Override
    public String getName() {
        return "Главная";
    }

    @Override
    public int modeMask() {
        return PanelMain;
        }
    @Override
    public boolean modeEnabled() {
        return true;
        }

    @Override
    public void onActivate() {
        }

    @Override
    public void onDeactivate() {
        }


    @Override
    public void onClose() {
        }


    public void setComPortState(int state){
        comPortState = state;
        COMPortOnOff.setIcon(new javax.swing.ImageIcon(getClass().getResource(ComPortStates[state])));
        resizeIcon(COMPortOnOff);
        }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        LOG = new java.awt.TextArea();
        STLLoad = new javax.swing.JButton();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        SLICE = new javax.swing.JButton();
        SliceMode = new javax.swing.JComboBox<>();
        jLabel41 = new javax.swing.JLabel();
        GCODEMilling = new javax.swing.JButton();
        GCODESave = new javax.swing.JButton();
        jLabel42 = new javax.swing.JLabel();
        GCODEView = new javax.swing.JButton();
        MLNLoad = new javax.swing.JButton();
        GGODESend = new javax.swing.JTextField();
        COMPortOnOff = new javax.swing.JButton();
        Stop = new javax.swing.JButton();
        LEVEL = new javax.swing.JComboBox<>();
        LogToFile = new javax.swing.JCheckBox();
        LogStop = new javax.swing.JCheckBox();
        BYSTEP = new javax.swing.JCheckBox();
        CNCReset = new javax.swing.JButton();
        Relative = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        Pause = new javax.swing.JButton();

        setLayout(null);

        LOG.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        add(LOG);
        LOG.setBounds(10, 10, 550, 710);

        STLLoad.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        STLLoad.setText("Загрузить STL");
        STLLoad.setBorder(new javax.swing.border.MatteBorder(null));
        STLLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                STLLoadActionPerformed(evt);
            }
        });
        add(STLLoad);
        STLLoad.setBounds(580, 50, 200, 40);

        jLabel39.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel39.setText("G-код");
        add(jLabel39);
        jLabel39.setBounds(580, 150, 140, 29);

        jLabel40.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel40.setText("Модель");
        add(jLabel40);
        jLabel40.setBounds(580, 10, 140, 30);

        SLICE.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        SLICE.setText("Слайсинг");
        SLICE.setBorder(new javax.swing.border.MatteBorder(null));
        SLICE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SLICEActionPerformed(evt);
            }
        });
        add(SLICE);
        SLICE.setBounds(790, 100, 240, 40);

        SliceMode.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        add(SliceMode);
        SliceMode.setBounds(790, 50, 240, 40);

        jLabel41.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel41.setText("Слайсинг");
        add(jLabel41);
        jLabel41.setBounds(790, 10, 150, 29);

        GCODEMilling.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        GCODEMilling.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/icon2/icons8-drill-48.png"))); // NOI18N
        GCODEMilling.setBorder(new javax.swing.border.MatteBorder(null));
        GCODEMilling.setBorderPainted(false);
        GCODEMilling.setContentAreaFilled(false);
        GCODEMilling.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GCODEMillingActionPerformed(evt);
            }
        });
        add(GCODEMilling);
        GCODEMilling.setBounds(580, 300, 50, 50);

        GCODESave.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        GCODESave.setText("G-код (экспорт)");
        GCODESave.setBorder(new javax.swing.border.MatteBorder(null));
        GCODESave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GCODESaveActionPerformed(evt);
            }
        });
        add(GCODESave);
        GCODESave.setBounds(580, 190, 200, 40);

        jLabel42.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel42.setText("Фрезерование");
        add(jLabel42);
        jLabel42.setBounds(580, 250, 220, 30);

        GCODEView.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        GCODEView.setText("G-код (просмотр)");
        GCODEView.setBorder(new javax.swing.border.MatteBorder(null));
        GCODEView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GCODEViewActionPerformed(evt);
            }
        });
        add(GCODEView);
        GCODEView.setBounds(790, 190, 240, 40);

        MLNLoad.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        MLNLoad.setText("Загрузить MLN");
        MLNLoad.setBorder(new javax.swing.border.MatteBorder(null));
        MLNLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MLNLoadActionPerformed(evt);
            }
        });
        add(MLNLoad);
        MLNLoad.setBounds(580, 100, 200, 40);

        GGODESend.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        GGODESend.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                GGODESendKeyPressed(evt);
            }
        });
        add(GGODESend);
        GGODESend.setBounds(580, 410, 450, 40);

        COMPortOnOff.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/icon2/icons8-turn-gray-48.png"))); // NOI18N
        COMPortOnOff.setBorderPainted(false);
        COMPortOnOff.setContentAreaFilled(false);
        COMPortOnOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                COMPortOnOffActionPerformed(evt);
            }
        });
        add(COMPortOnOff);
        COMPortOnOff.setBounds(870, 300, 50, 50);

        Stop.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        Stop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/icon2/icons8-stop-48.png"))); // NOI18N
        Stop.setBorder(new javax.swing.border.MatteBorder(null));
        Stop.setBorderPainted(false);
        Stop.setContentAreaFilled(false);
        Stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StopActionPerformed(evt);
            }
        });
        add(Stop);
        Stop.setBounds(740, 300, 50, 50);

        LEVEL.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        LEVEL.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "информ.", "важное", "предупр.", "сбой" }));
        LEVEL.setPreferredSize(new java.awt.Dimension(72, 25));
        LEVEL.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                LEVELItemStateChanged(evt);
            }
        });
        add(LEVEL);
        LEVEL.setBounds(570, 650, 170, 40);

        LogToFile.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        LogToFile.setText("Лог в файле");
        LogToFile.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                LogToFileItemStateChanged(evt);
            }
        });
        add(LogToFile);
        LogToFile.setBounds(830, 700, 120, 20);

        LogStop.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        LogStop.setText("Остановить лог");
        LogStop.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                LogStopItemStateChanged(evt);
            }
        });
        add(LogStop);
        LogStop.setBounds(680, 700, 150, 24);

        BYSTEP.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        BYSTEP.setText("По шагам");
        add(BYSTEP);
        BYSTEP.setBounds(570, 700, 130, 24);

        CNCReset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/icon2/icons8-refresh-48.png"))); // NOI18N
        CNCReset.setBorderPainted(false);
        CNCReset.setContentAreaFilled(false);
        CNCReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CNCResetActionPerformed(evt);
            }
        });
        add(CNCReset);
        CNCReset.setBounds(810, 300, 50, 50);

        Relative.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                RelativeMouseClicked(evt);
            }
        });
        add(Relative);
        Relative.setBounds(580, 360, 60, 40);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setText("Относительная СК");
        add(jLabel1);
        jLabel1.setBounds(660, 360, 220, 32);

        Pause.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PauseMouseClicked(evt);
            }
        });
        add(Pause);
        Pause.setBounds(650, 300, 50, 50);
    }// </editor-fold>//GEN-END:initComponents

    private void openModel(){
        try {
            if (getBaseFrame().test1()) return;
            final String fname = getBaseFrame().getInputFileName("Файл STL","stl",false);
            if (fname==null) return;
            ws.loadModel(fname, WorkSpace.ws().getNotify());
            setMenuVisible();
            if (ws.model().loaded()){
                getBaseFrame().setViewPanelEnable(PanelSTL3D);
                }
            getBaseFrame().refreshPanels();
            } catch (UNIException ee){ toLog(ee.toString());}
        }

    private void STLLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_STLLoadActionPerformed
        ws.removeAll();
        setMenuVisible();
        openModel();
        /*
        setMenuVisible();if (test1()) return;
        final String fname = getBaseFrame().getInputFileName("Файл модели","stl",true);
        if (fname==null) return;
        startView(0,0);
        new Thread(
                ()->{
                    try {
                        ws.removeAll();
                        setMenuVisible();
                        ws.load(new DataInputStream(new FileInputStream(fname)));
                        ws.lastName(fname);
                        ws.fileStateChanged();
                    } catch (IOException e) { ws.getNotify().notify(Values.error,e.toString()); }
                    finishOperation();
                }).start();
         */
    }//GEN-LAST:event_STLLoadActionPerformed

    private Color savedColor;
    private void SLICEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SLICEActionPerformed
        switch (SliceMode.getSelectedIndex()){
            case SliceModeSequent:
                sliceTo3D();
                break;
            case SliceModeParellel:
                sliceConcurent();
                break;
            case SliceModeIntoFile:
                sliceConcurentToFile(true);
                break;
            case SliceModeIntoFileAs:
                sliceConcurentToFile(false);
                break;
        }
    }//GEN-LAST:event_SLICEActionPerformed

    private I_COMPortGReceiver gCodeBack = new I_COMPortGReceiver() {
        @Override
        public void onError(UNIException ee) {
            WorkSpace.ws().getNotify().notify(error,"GCODE - ошибка: "+ee.toString());
            }
        @Override
        public void onReceive(String ss) {
            WorkSpace.ws().getNotify().notify(info,"GCODE - асинхронный ответ: "+ss);
            }
        @Override
        public void onClose() {
            WorkSpace.ws().getNotify().notify(info,"GCODE - отключение");
            }
        @Override
        public void setOKTimeOut(int delyInMS) {}
    };

    private I_COMPortGReceiver gCodeManualBack = new I_COMPortGReceiver() {
        @Override
        public void setOKTimeOut(final int delayInMS){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(delayInMS==0 ? ManualOKTimeOut : delayInMS);
                        } catch (InterruptedException e) {}
                    java.awt.EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            ws.getNotify().notify(warning,"GCODE: тайм-аут \'ok\'");
                            setComPortState(ComPortStateOn);
                        }
                    });
                    }
                }).start();
            }
        @Override
        public void onError(UNIException ee) {
            ws.getNotify().notify(error,"GCODE - ошибка: "+ee.toString());
            setComPortState(ComPortStateFail);
            }
        @Override
        public void onReceive(String ss) {
            if (!ss.equals("ok")){
                ws.getNotify().notify(info, "GCODE: " + ss);
                setComPortState(ComPortStateBusy);
                }
            else{
                try {
                    Thread.sleep(ManualOKTimeOut);
                    } catch (InterruptedException e) {}
                setComPortState(ComPortStateOn);
                }
            }
        @Override
        public void onClose() {
            ws.getNotify().notify(info,"GCODE - отключение");
            }
        };



    private void gCodeSend(BufferedReader in,COMPortGDriver driver,int timeOut) {
        int count = 0;
        viewCommon.start();
        pauseButton.setEnabled(true);
        Stop.setEnabled(true);
        try {
            String gCode = null;
            while (viewCommon.isRunning() && (gCode = in.readLine()) != null) {
                if (viewCommon.isPause()){
                    try {
                        Thread.sleep(1000);
                        } catch (Exception ee){}
                    continue;
                    }
                count++;
                ws.getNotify().notify(info, "GCODE: " + gCode);
                final String gcode1 = gCode;
                java.awt.EventQueue.invokeLater(new Runnable(){
                    @Override
                    public void run() {
                        //getBaseFrame().sendEvent(Events.GCode,1,0,gcode1,null);
                        }
                    });
                Pair<String, String> res = driver.write(gCode,timeOut);
                if (res.o1 != null) {
                    ws.getNotify().notify(error, "GCODE - ошибка: " + res.o1);
                    viewCommon.finish();
                    }
                else
                    {
                    if (!res.o2.equals("ok")){
                        ws.getNotify().notify(info, "GCODE - ответ " + res.o2);
                        if (res.o2.startsWith("error"))
                            viewCommon.finish();
                        }
                    }
                }
            in.close();
            //driver.close();
            ws.getNotify().notify(info, "GCODE: " + count + " команд");
            } catch (Exception ee) {
                ws.getNotify().notify(error,"GCODE: " +Utils.createFatalMessage(ee,10));
                //driver.close();
                if (in != null) {
                    try { in.close(); } catch (IOException e) {}
                    }
                }
            viewCommon.finish();
            pauseButton.setEnabled(false);
            Stop.setEnabled(false);
        }
    private void GCODEMillingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GCODEMillingActionPerformed
        final String fname = getBaseFrame().getInputFileName("Файл GCODE","gcode",false);
        if (fname==null) return;
        BufferedReader in=null;
        try {
            MashineSettings ms = ws.global().mashine;
            if (comPortState!= ComPortStateOff){
                driver.close();
                setComPortState(ComPortStateOff);
                }
            in = new BufferedReader(new InputStreamReader(new FileInputStream(fname),"UTF8"));
            String ss = driver.open(ms.DeviceName.getVal()+ms.DeviceNum.getVal(),ms.BaudRate.getVal(),ms.DeviceTimeOut.getVal(),gCodeBack);
            if (ss!=null){
                setComPortState(ComPortStateFail);
                ws.getNotify().notify(error,ss);
                if (in!=null)
                    in.close();
                return;
                }
            setComPortState(ComPortStateOn);
            if (relative.isSelected())
                in = gCodeConvertIntoRelative(in);
            if (in==null)
                return;
            final BufferedReader in2 = in;
            //--------------------- сброс последовательности -----------------------------------------------------------
            //getBaseFrame().sendEvent(Events.GCode,0,0,"",null);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    gCodeSend(in2,driver,ws.global().mashine.DeviceTimeOut.getVal());
                    }
                }).start();
            } catch (Exception ee){
                ws.getNotify().notify(error,"GCODE: " +Utils.createFatalMessage(ee,10));
                closeComPort(in);
                }
    }//GEN-LAST:event_GCODEMillingActionPerformed

    private void closeComPort(BufferedReader in){
        setComPortState(ComPortStateOff);
        driver.close();
        CNCReset.setEnabled(false);
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {}
        }
    }


    private void GCODESaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GCODESaveActionPerformed
        exportGCode();
    }//GEN-LAST:event_GCODESaveActionPerformed


    //------------------------------------------------------------------------------------------------------------------
    private static String itoa(int val, int ndig){
        String out="";
        while(ndig--!=0){
            out = ""+val%10+out;
            val/=10;
            }
        return out;
        }
    private BufferedReader gCodeConvertIntoRelative(BufferedReader in){
        boolean firstAbsolute=true;
        boolean relativeMode=true;
        BufferedWriter out = null;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            out = new BufferedWriter(new OutputStreamWriter(bout, "UTF8"));
            }catch (Exception ee){
                ws.notifySync(error, "GCODE: " + Utils.createFatalMessage(ee,10));
                return null;
                }
        String gCode = null;
        int count=0;
        Double oldX = new Double(0);
        Double oldY = new Double(0);
        Double oldZ = new Double(0);
        try {
            out.write("G91");
            out.newLine();
            relativeMode = true;
            while (true) {
                gCode = in.readLine();
                if (gCode == null) {
                    try {
                        in.close();
                        } catch (IOException ex) {
                    }
                    break;
                }
                count++;
                if (!gCode.startsWith("G")){
                    out.write(gCode);
                    out.newLine();
                    continue;
                    }
                StringTokenizer tokenizer = new StringTokenizer(gCode);
                ArrayList<String> tokens = new ArrayList<>();
                while (tokenizer.hasMoreTokens())
                    tokens.add(tokenizer.nextToken(" "));
                HashMap<Character, Double> pars = new HashMap<>();
                for (String token : tokens) {
                    char cc = token.charAt(0);
                    double dd = Double.parseDouble(token.substring(1));
                    if (pars.get(cc) != null) {
                        ws.notifySync(Values.warning, "Повторный тэг " + gCode);
                        continue;
                        }
                    pars.put(cc, dd);
                    }
                Character xx = new Character('X');
                Character yy = new Character('Y');
                Character zz = new Character('Z');
                Character ff = new Character('F');
                Double dd = pars.get(new Character('G'));
                if (dd == null) {
                    ws.notifySync(Values.warning, "GCode: " + count + " Не найден тег G: " + gCode);
                    out.write(gCode);
                    out.newLine();
                    continue;
                    }
                if (dd.intValue()==90){
                    relativeMode = false;
                    continue;
                    }
                else
                if (dd.intValue()==91){
                    relativeMode=true;
                    continue;
                    }
                if (relativeMode){
                    out.write(gCode);
                    out.newLine();
                    continue;
                    }
                String ss = "G"+itoa(dd.intValue(),2);
                Double pp = pars.get(xx);
                if (pp != null) {
                    double vv = firstAbsolute ? 0 : pp.doubleValue() - oldX.doubleValue();
                    ss += String.format(" X%-6.2f", vv);
                    oldX = pp;
                    }
                pp = pars.get(yy);
                if (pp != null) {
                    double vv = firstAbsolute ? 0 : pp.doubleValue() - oldY.doubleValue();
                    ss += String.format(" Y%-6.2f", vv);
                    oldY = pp;
                    }
                pp = pars.get(zz);
                if (pp != null) {
                    double vv = firstAbsolute ? 0 : pp.doubleValue() - oldZ.doubleValue();
                    ss += String.format(" Z%-6.2f", vv);
                    oldZ = pp;
                    }
                firstAbsolute = false;
                pp = pars.get(ff);
                if (pp != null) {
                    ss += String.format(" F%d", pp.intValue());
                    }
                ss = ss.replace(",",".");
                out.write(ss);
                out.newLine();
                }
            out.flush();
            BufferedReader two = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bout.toByteArray()), "UTF8"));
            try { in.close(); } catch (IOException ex) {}
            return two;
            } catch (IOException e) {
                ws.notifySync(error, "GCODE: " + Utils.createFatalMessage(e,10));
                try { in.close(); } catch (IOException ex) {}
                return null;
                }
        }
    //------------------------------------------------------------------------------------------------------------------
    private double layerZ = 0;
    private Graphics gr=null;
    int step=0;                             // Обработка последовательности смены слоя
    private ArrayList<GCodeLayer> gCodeParse(BufferedReader in){
        I_STLPoint2D prevPoint = new STLPoint2D(0,0);
        boolean absolute=false;
        ArrayList<GCodeLayer> layers = new ArrayList<>();
        GCodeLayer current=null;
        ArrayList<STLLine> lines = new ArrayList<>();
        String gCode = null;
        int count=0;
        double x0 = WorkSpace.ws().global().mashine.WorkFrameX.getVal()/2;
        double y0 = WorkSpace.ws().global().mashine.WorkFrameY.getVal()/2;
        double zUp = ws.global().model.ZUp.getVal();
        I_STLPoint2D last = new STLPoint2D(0,0);
        double lastZ = 0;
        boolean up = true;              // Фреза поднята
        while (true) {
            try {
                gCode = in.readLine();
                } catch (IOException e) {
                    ws.notifySync(error, "GCODE: " + Utils.createFatalMessage(e,10));
                    try { in.close(); } catch (IOException ex) {}
                    return null;
                    }
                if (gCode==null){
                    try { in.close(); } catch (IOException ex) {}
                    break;
                    }
                count++;
                //ws.notifySync(info, "GCODE: " + gCode);
                if (!gCode.startsWith("G"))
                    continue;
                StringTokenizer tokenizer = new StringTokenizer(gCode);
                ArrayList<String> tokens = new ArrayList<>();
                while (tokenizer.hasMoreTokens())
                    tokens.add(tokenizer.nextToken(" "));
                HashMap<Character,Double> pars = new HashMap<>();
                for(String token : tokens){
                    char cc = token.charAt(0);
                    double dd = Double.parseDouble(token.substring(1));
                    if (pars.get(cc)!=null){
                        ws.notifySync(Values.warning,"Повторный тэг "+gCode);
                        continue;
                        }
                    pars.put(cc,dd);
                    }
                Character xx = new Character('X');
                Character yy = new Character('Y');
                Character zz = new Character('Z');
                Double dd = pars.get(new Character('G'));
                double dz = 0;
                if (dd==null){
                    ws.notifySync(Values.warning,"GCode: "+count+" Не найден тег G: "+gCode);
                    try { in.close(); } catch (IOException ex) {}
                    return null;
                    }
                switch ((int)dd.doubleValue()){
                    case 90:
                        absolute = true;
                        break;
                    case 91:
                        absolute = false;
                        if (current!=null)
                            layers.add(current);
                        current = new GCodeLayer();
                        break;
                    case 0:
                        if (pars.get(zz)!=null){
                            dz = pars.get(zz).doubleValue();
                            if (dz > 0 ){                       // перемещение вверх
                                up = true;
                                if (current!=null)
                                    current.groups.add(lines);  // Добавить накопленную группу линий
                                lines = new ArrayList<>();
                                }
                            else{                               // Перемещение вниз
                                lastZ = -dz;
                                }
                            }
                        else{                                   // Холостое перемещение над повехностью к новой точке
                            prevPoint = new STLPoint2D(prevPoint.x()+pars.get(xx),prevPoint.y()+pars.get(yy));
                            }
                        break;
                    case 1:
                        if (pars.get(zz)!=null){
                            lastZ = lastZ - dz - zUp;
                            if (current!=null)
                                current.setLayerZ(lastZ);
                            }
                        else{       // Фрезерование внутри группы
                            I_STLPoint2D two = new STLPoint2D(prevPoint.x()+pars.get(xx),prevPoint.y()+pars.get(yy));
                            STLLine line = new STLLine(prevPoint,two);
                            prevPoint = two;
                            lines.add(line);
                            }
                        break;
                    case 2:         // Кривая - как линия
                        I_STLPoint2D two = new STLPoint2D(pars.get(xx)-x0,pars.get(yy)-y0);
                        STLLine line = new STLLine(prevPoint,two);
                        prevPoint = two;
                        lines.add(line);
                        break;
                        }
                    //------------------------------------------------------------------------------
                    }
            if (lines.size()!=0){
                current.groups.add(lines);
                lines = new ArrayList<>();
                }
            if (current!=null){}
                layers.add(current);
            layers.remove(0);                   // Пока убрать пустой лишний слой
            return layers;
            }

    private void GCODEViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GCODEViewActionPerformed
        if (!WorkSpace.ws().modelPresent()){
            ws.getNotify().notify(error,"Не загружена модель: необходимы размерности");
            return;
            }
        final String fname = getBaseFrame().getInputFileName("Файл GCODE","gcode",false);
        if (fname==null) return;
        BufferedReader in=null;
        try {
            MashineSettings ms = ws.global().mashine;
            in = new BufferedReader(new InputStreamReader(new FileInputStream(fname),"UTF8"));
            //--------------------- сброс последовательности -----------------------------------------------------------
            final BufferedReader in2 = in;
            ArrayList<GCodeLayer> res = gCodeParse(in2);
            if (res!=null) {
                getBaseFrame().setViewPanelEnable(PanelSTL3DLoops);
                getBaseFrame().refreshPanels();
                getBaseFrame().toFront(PanelSTL3DLoops);
                getBaseFrame().sendEvent(Events.GCode, 0, 0, "", res);
                }
            } catch (Exception ee){
                WorkSpace.ws().getNotify().notify(error,"GCODE: " +Utils.createFatalMessage(ee,10));
                if (in != null) {
                    try {
                        in.close();
                        } catch (IOException e) {}
                }
            }

    }//GEN-LAST:event_GCODEViewActionPerformed


    private void MLNLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MLNLoadActionPerformed
        if (getBaseFrame().test1()) return;
        final String fname = getBaseFrame().getInputFileName("Файл слайсинга",Values.FileType,true);
        if (fname==null) return;
        getBaseFrame().startView(0,0);
        new Thread(
                ()->{
                    try {
                        ws.removeAll();
                        ws.load(new DataInputStream(new FileInputStream(fname)));
                        ws.lastName(fname);
                        ws.fileStateChanged();
                        ws.dataState(Sliced);
                        setMenuVisible();
                        String ss= "Загружен файл "+fname;
                        ws.getNotify().notify(Values.info,ss);
                    } catch (IOException e) {
                            String ss= "Ошибка загрузки файла "+fname+": "+e.toString();
                            ws.getNotify().notify(Values.error,ss);
                            ws.popup(ss);
                            }
                    Utils.delayInGUI(0, new Runnable() {
                        @Override
                        public void run() {
                            getBaseFrame().finishOperation();
                            getBaseFrame().refreshPanels();
                            }
                        });
                }).start();
    }//GEN-LAST:event_MLNLoadActionPerformed

    private void GGODESendKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_GGODESendKeyPressed
        int keyCode = evt.getKeyCode();
        if (keyCode==KeyCodeUp){
            if (savedGCodes.size()==0 || lastSavedCount>=savedGCodes.size())
                return;
            GGODESend.setText(savedGCodes.get(savedGCodes.size()-1-lastSavedCount));
            lastSavedCount++;
            return;
            }
        if(evt.getKeyCode()!=KeyCodeEnter)
            return;
        if (comPortState!=ComPortStateOn){
            viewUpdate(evt,false);
            WorkSpace.ws().getNotify().notify(error,"GCODE: устройство не готово");
            return;
            }
        setComPortState(ComPortStateBusy);
        String gCode = GGODESend.getText();
        savedGCodes.add(gCode);
        GGODESend.setText("");
        lastSavedCount=0;
        if (savedGCodes.size()>=SavedMaxSize)
            savedGCodes.remove(0);
        Pair<String,String> ans = driver.write(gCode,ws.global().mashine.DeviceTimeOut.getVal());
        if (ans.o1!=null){
            WorkSpace.ws().getNotify().notify(error,"GCODE: "+ans.o1);
            setComPortState(ComPortStateFail);
            }
        else{
            if(!ans.o2.equals("ok"))
                WorkSpace.ws().getNotify().notify(info,"GCODE: "+ans.o2);
            setComPortState(ComPortStateOn);
            }
        /*
        driver.writeNoWait(GGODESend.getText());
        gCodeManualBack.setOKTimeOut(ws.global().mashine.DeviceTimeOut.getVal()*1000);
         */

    }//GEN-LAST:event_GGODESendKeyPressed

    private void COMPortOnOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_COMPortOnOffActionPerformed
        switch (comPortState){
            case ComPortStateBusy:
                closeComPort(null);
                break;
            case ComPortStateOn:
                closeComPort(null);
                break;
            case ComPortStateFail:
                closeComPort(null);
                break;
            case ComPortStateOff:
                MashineSettings ms = ws.global().mashine;
                String ss = driver.open(ms.DeviceName.getVal()+ms.DeviceNum.getVal(),ms.BaudRate.getVal(),ms.DeviceTimeOut.getVal(),gCodeManualBack);
                if (ss!=null){
                    setComPortState(ComPortStateFail);
                    ws.getNotify().notify(error,ss);
                    return;
                    }
                setComPortState(ComPortStateOn);
                CNCReset.setEnabled(true);
                break;
            }
    }//GEN-LAST:event_COMPortOnOffActionPerformed

    private void StopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StopActionPerformed
        viewCommon.finish();
        Stop.setEnabled(false);
        Pause.setEnabled(false);
        sendEvent(Events.OperateFinish,0,0,null,null);
    }//GEN-LAST:event_StopActionPerformed

    private void LEVELItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_LEVELItemStateChanged
        notify.setLevel(LEVEL.getSelectedIndex());
    }//GEN-LAST:event_LEVELItemStateChanged

    private void LogToFileItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_LogToFileItemStateChanged
        if (LogToFile.isSelected())
        openLogFile();
        else
        closeLogFile(null);
    }//GEN-LAST:event_LogToFileItemStateChanged

    private void LogStopItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_LogStopItemStateChanged
        notify.logSuspendState(LogStop.isSelected());
    }//GEN-LAST:event_LogStopItemStateChanged


    public void sendOneCommmand(String cmd){
        Pair<String,String> ans = driver.write(cmd,ws.global().mashine.DeviceTimeOut.getVal());
        if (ans.o1!=null){
            WorkSpace.ws().getNotify().notify(error,"Reset: "+ans.o1);
            setComPortState(ComPortStateFail);
            }
        else{
            if(!ans.o2.equals("ok"))
                WorkSpace.ws().getNotify().notify(info,"Reset: "+ans.o2);
            else{
                viewCommon.finish();
                Stop.setText("...");
                sendEvent(Events.OperateFinish,0,0,null,null);
                }
            setComPortState(ComPortStateOn);
            }
        }

    private void CNCResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CNCResetActionPerformed
        char ctrlX[] = {(char)0x18};
        sendOneCommmand(new String(ctrlX));
        sendOneCommmand("$X");
    }//GEN-LAST:event_CNCResetActionPerformed

    private void RelativeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_RelativeMouseClicked
        relative.itemStateChanged();
    }//GEN-LAST:event_RelativeMouseClicked

    private void PauseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PauseMouseClicked
        if (!viewCommon.isRunning())
            return;
        viewCommon.changePause();
        pauseButton.itemStateChanged();
    }//GEN-LAST:event_PauseMouseClicked

    private void exportGCode(){
        if (getBaseFrame().test3()) return;
        String dir = ws.defaultFileName();
        dir = Utils.changeFileExt(dir, "gcode");
        final String outname = getBaseFrame().getOutputFileName("Файл gcode","gcode",dir);
        if (outname==null)
            return;
        WorkSpace.ws().getNotify().log("Экспорт в файл "+outname);
        getBaseFrame().startView(0,0);
        new Thread(
                ()->{
                    try {
                        ws.operate().exportToGCode(ws.viewCommon(),new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outname))));
                    } catch (IOException e) { WorkSpace.ws().getNotify().notify(Values.error,e.toString()); }
                    getBaseFrame().finishOperation();
                }).start();
        }

    private void dataChanged(){
        ws.dataChanged();
        ws.fileStateChanged();
        }
    private void sliceTo3D() {
        if (getBaseFrame().test2()) return;
        getBaseFrame().startView(0,0);
        new Thread(
                ()->{
                    SliceData data = new SliceData();
                    ws.data(data);
                    ws.operate().sliceTo(new SliceDataGenerator(data,getBaseFrame().getViewCommon()),getBaseFrame().getViewCommon());
                    if (!data.isSliceStop())
                        dataChanged();
                    getBaseFrame().finishOperation();
                }).start();
        }
    private void sliceConcurent() {
        if (getBaseFrame().test2()) return;
        getBaseFrame().startView(0,0);
        new Thread(
                ()->{
                    SliceData data = ws.operate().sliceConcurent(getBaseFrame().getViewCommon());
                    ws.data(data);
                    ws.lastName("");
                    if (!data.isSliceStop()){
                        dataChanged();
                        }
                    getBaseFrame().finishOperation();
                }).start();
        }
    private void sliceConcurentToFile(boolean defName) {
        if (getBaseFrame().test2()) return;
        String dir = ws.defaultFileName();
        final String outname = defName ? dir : getBaseFrame().getOutputFileName("Файл слайсинга",Values.FileType,dir);
        if (outname == null) return;
        getBaseFrame().startView(0,0);
        new Thread(
                ()->{
                    try {
                        WorkSpace.ws().getNotify().log("Слайсинг в файл "+outname);
                        SliceData data = ws.operate().sliceConcurent(getBaseFrame().getViewCommon(),new DataOutputStream(new FileOutputStream(outname)));
                        ws.lastName(defName ? "" : outname);
                        //------------- Состояние dataState не меняется ----------------------------------------
                        if (!data.isSliceStop()){
                            dataChanged();
                            }
                    } catch (IOException e) { WorkSpace.ws().getNotify().notify(Values.error,e.toString()); }
                    getBaseFrame().finishOperation();
                }).start();
        }

    private void setMenuVisible(){
        boolean loaded = ws.modelPresent();
        boolean sliced = ws.slicePresent();
        boolean merge = sliced && !ws.data().isMerged();
        int userType = ws.currentUser().accessMode;
        boolean isAdmin = userType==Values.userAdmin;
        boolean canSave = userType==Values.userAdmin || userType==Values.userConstructor;
        SLICE.setEnabled(loaded || sliced);
        SliceMode.setEditable(loaded || sliced);
        GCODESave.setEnabled(sliced);
        /*
        mBar.getMenu(mSlice).setEnabled(loaded || sliced);
        mBar.getMenu(mSet).setEnabled(userType!=Values.userGuest);
        mBar.getMenu(mPrint).setEnabled(userType!=Values.userGuest);
        mBar.getMenu(mOther).setEnabled(isAdmin);
        //--------------------------------------------------------------------
        mBar.getMenu(mFile).getItem(2).setEnabled(sliced && canSave);
        mBar.getMenu(mFile).getItem(3).setEnabled(sliced && canSave);
        mBar.getMenu(mFile).getItem(4).setEnabled(sliced && canSave);
        mBar.getMenu(mView).getItem(0).setEnabled(loaded);
        mBar.getMenu(mView).getItem(1).setEnabled(sliced);
        mBar.getMenu(mView).getItem(2).setEnabled(loaded);
        mBar.getMenu(mSet).getItem(1).setEnabled(isAdmin);
        mBar.getMenu(mSlice).getItem(2).setEnabled((loaded || sliced)&&userType!=Values.userGuest);
        mBar.getMenu(mSlice).getItem(3).setEnabled((loaded || sliced)&&userType!=Values.userGuest);
         */
    }


    @Override
    public void refresh() {
        }

    public void openLogFile(){
        if (logFile!=null)
            return;
        try {
            Date xx = new Date();
            if (ws.modelName().length()==0){
                notify.log("Лог-файл только с моделью");
                LogToFile.setSelected(false);
                return;
            }
            String fname = ws.defaultDir()+ ws.modelName()+"_log "+Utils.currentLogName()+".txt";
            ws.testDefaultDir();
            logFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fname),"Windows-1251"));
            LogToFile.setSelected(true);
            } catch (Exception ex) {
                notify.notify(Values.error, ex.getMessage());
                }
        }
    private void closeLogFile(Exception ex){
        if (logFile==null)
            return;
        try { logFile.flush(); logFile.close(); } catch (IOException ex1) {}
        logFile=null;
        LogToFile.setSelected(false);
        if (ex!=null)
               notify.notify(Values.error, ex.getMessage());
        }

    public void onEvent(int code, int par1, long par2, String par3, Object oo) {
        switch (code){
            case Events.Log:
                notify.notify(par1,par3);
                break;
            case Events.LogFileClose:
                closeLogFile(null);
                break;
            case Events.LogFileOpen:
                openLogFile();
                break;
            case Events.OnWarning:
                stopOnWarning = par1!=0;
                break;
        }
    }

    @Override
    public void shutDown() {
        }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox BYSTEP;
    private javax.swing.JButton CNCReset;
    private javax.swing.JButton COMPortOnOff;
    private javax.swing.JButton GCODEMilling;
    private javax.swing.JButton GCODESave;
    private javax.swing.JButton GCODEView;
    private javax.swing.JTextField GGODESend;
    private javax.swing.JComboBox<String> LEVEL;
    private java.awt.TextArea LOG;
    private javax.swing.JCheckBox LogStop;
    private javax.swing.JCheckBox LogToFile;
    private javax.swing.JButton MLNLoad;
    private javax.swing.JButton Pause;
    private javax.swing.JButton Relative;
    private javax.swing.JButton SLICE;
    private javax.swing.JButton STLLoad;
    private javax.swing.JComboBox<String> SliceMode;
    private javax.swing.JButton Stop;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    // End of variables declaration//GEN-END:variables
}
