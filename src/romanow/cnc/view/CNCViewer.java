/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.view;

import lombok.Getter;
import romanow.cnc.Values;
import romanow.cnc.console.DistortionEditor;
import romanow.cnc.console.LaserConsole;
import romanow.cnc.console.TestConsole;
import romanow.cnc.io.BinOutputStream;
import romanow.cnc.m3d.*;
import romanow.cnc.settings.WorkSpace;
import romanow.cnc.settingsView.UserListEditor;
import romanow.cnc.slicer.*;
import romanow.cnc.stl.I_ModelGenerator;
import romanow.cnc.stl.STLGeneratorQube;
import romanow.cnc.stl.STLGeneratorWalls;
import romanow.cnc.stl.STLModel3D;
import romanow.cnc.utils.*;
import romanow.cnc.viewer3d.Loop3DViewer;
import romanow.cnc.viewer3d.STLViewer;
import romanow.cnc.controller.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.*;

import static romanow.cnc.Values.*;

/**
 *
 * @author romanow
 */
public class CNCViewer extends BaseFrame {
    private ViewNotifyer notify;
    private M3DTesing testing;
    private JProgressBar progress;
    private MenuBar mBar;
    private Thread.UncaughtExceptionHandler defaultHandler=null;
    private static int childCount=3;
    private WorkSpace ws=null;
    private BufferedWriter logFile = null;
    @Getter private ViewAdapter viewCommon = new ViewAdapter(null);
    private boolean stopOnWarning=false;

    public javax.swing.JTabbedPane getPanelList(){
        return PanelList;
        }
    @Override
    public JProgressBar getProgress(){
        return progress;
        }
    /**
     * Creates new form Viewer
     */
    @Override
    public void toFront(int mask){
        int idx=0;
        for(BasePanel panel : getPanels()){
            if (!panel.isSelected())
                continue;
            if (panel.modeMask()==mask) {
                PanelList.setSelectedIndex(idx);
                break;
                }
            idx++;
            }
        }
    @Override
    public void refreshPanels() {
        PanelList.removeAll();
        for(BasePanel panel : getPanels()) {
            if (panel.isSelected())
                panel.onDeactivate();
            }
        int idx=0;
        Common.removeAll();
        for(final BasePanel panel : getPanels()){
            boolean bb = panel.isSelectedMode();
            if (bb){
                PanelList.add(panel.getName(),panel);
                final int idx2= idx;
                JButton menuButton = new JButton();
                menuButton.setBounds(MenuButtonX0,MenuButtonY0+idx*MenuButtonStep,MenuButtonXSize,MenuButtonYSize);
                menuButton.setText(panel.getName());
                int style = menuButton.getFont().getStyle();
                menuButton.setFont(new java.awt.Font("Segoe UI", style, 24));
                menuButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        PanelList.setSelectedIndex(idx2);
                        }
                    });
                Common.add(menuButton);
                idx++;
                }
            panel.setSelected(bb);
            if (bb)
                panel.onActivate();
            }
        JButton menuButton = new JButton();
        menuButton.setBounds(MenuButtonX0,MenuButtonY0+idx*MenuButtonStep,MenuButtonXSize,MenuButtonYSize);
        menuButton.setText("Выход");
        int style = menuButton.getFont().getStyle();
        menuButton.setFont(new java.awt.Font("Segoe UI", style, 24));
        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WorkSpace.ws().popup("Завершение работы");
                Utils.delayInGUI(3,new Runnable(){
                    @Override
                    public void run() {
                        shutDown();
                    }
                });
                }
            });
        Common.add(menuButton);
        idx++;

        JProgressBar progress = new JProgressBar();
        progress.setMaximum(0);
        progress.setMaximum(100);
        progress.setValue(0);
        Common.add(progress);
        ws.getNotify().setProgressView(progress);
        if (ws.global().fullScreen){
            BasePanel.setComponentsScale(Common);
            }
        revalidate();
        Common.repaint(0,0,Common.getX(),Common.getY());
        Common.revalidate();
        }

    public void setProgress(int proc){
        progress.setValue(proc);
        }
    private void addPanel(BasePanel panel){
        getPanels().add(panel);
        }
    @Override
    public void createPanels(){
        WorkSpace ws = WorkSpace.ws();
        double scaleY = ws.getScaleY();
        double scaleX = ws.getScaleX();
        int xx = (int)(scaleX*Values.FrameMenuRightOffet);
        //--------------------------------------------------------------------------------------------------------------
        Dimension dim = ws.getDim();
        if (dim.width==0)
            PanelList.setBounds(0, 0,Values.FrameWidth-Values.FrameMenuRightOffet,Values.FrameHeight);
        else
            PanelList.setBounds(0, 0, dim.width-xx, dim.height);
        if (dim.width==0)
            Common.setBounds(Values.FrameWidth-Values.FrameMenuRightOffet, 0,Values.FrameMenuRightOffet,Values.FrameHeight);
        else
            Common.setBounds(dim.width-xx, 0, xx, dim.height);
        ArrayList<BasePanel> panels = getPanels();
        panels.clear();
        PanelList.removeAll();
        //-------------------------------------------------------------------------------
        addPanel(new CNCViewerPanel(this));
        //addPanel(new CNCLogPanel(this));
        addPanel(new GlobalSettingsPanel(this));
        addPanel(new ModelSettingsPanel(this));
        //addPanel(new CommonViewPanel(this));                // Пока нельзя убирать.... ws.preview
        addPanel(new Loop3DPanel171(this));
        addPanel(new MLNViewPanel(this));
        //addPanel(new STL3DViewPanel171(this));
        addPanel(new LoginPanel(this));
        for(BasePanel panel : getPanels())
            if (dim.width!=0)
                panel.setBounds(0,0,dim.width-xx,dim.height);
            else
                panel.setBounds(0,0,Values.FrameWidth-Values.FrameMenuRightOffet,Values.FrameHeight);
        //---------------------------------------------------------------------------------
        }

    public CNCViewer() {
        super();
        setUndecorated(true);
        if (!tryToStart()) return;
        ws = WorkSpace.ws();
        initComponents();
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        ws().init();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        System.out.printf("Разрешение экрана: %dx%d\n", screenSize.width, screenSize.height);
        setTitle(Values.getVersion()+" "+ws().currentFileTitle());
        String xx = null;
        try {
            ws().loadGlobalSettings();
            } catch (UNIException e) {
                xx = "Настойки не прочитаны - умолчание";
                ws().saveSettings();
                }
        if (ws().global().fullScreen){
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setBounds(0,0, screenSize.width,screenSize.height);
            //PanelList.setBounds(10,0,screenSize.width-10,screenSize.height);
            //setUndecorated(true);
            //setBounds(0,0,screenSize.width,screenSize.height);
            }
        else
            setBounds(FrameX0,FrameY0, FrameWidth,FrameHeight);
        ws().setDimension();
        createPanels();
        notify = (ViewNotifyer) ws().getNotify();
        if (xx!=null)
            notify.notify(warning,xx);
        setViewPanel(PanelLogin);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                String message="Непредусмотренное исключение - перешлите скан разработчикам\n";
                String ss = e.getMessage()+"/"+e.getLocalizedMessage();
                if (ss!=null) message += ss+"\n";
                message+=e.toString()+"\n";
                message+="Поток: "+t.getName()+"\n";
                StackTraceElement dd[]=e.getStackTrace();
                for (int i = 0; dd != null && i < dd.length && i < Values.StackTraceDeepth; i++) {
                    message += dd[i].getClassName() + "." + dd[i].getMethodName() + ":" + dd[i].getLineNumber()+"\n";
                    }
                notify.notify(Values.fatal,message);
                }
            });
        mBar = menuBar1;
        //---------------------------------------------------------------------------------------------------------------
        refreshPanels();
        toBack();
        }

    private void setWidth(boolean full){
        if (ws.preview()!=null)
            ws.preview().setVisible(full);
        }


    @Override
    public void onEvent(int code,boolean on, int value, String name) {
        System.out.println(getClass().getSimpleName()+" "+code+" "+on+" "+value+" "+name);
        if (code == Events.Print){
            if (value == Events.PStateWorking)
                sendEvent(Events.LogFileOpen,0,0,null,null);
            }
        if (code == Events.Notify){
            notify.notify(value, name);
            }
        if (code == Events.FileState){
            setTitle("SLM 3D Printer: "+ws().currentFileTitle());
            setMenuVisible();
            }
        }

    
    public void finishOperation(){
        //PAUSE.setText("...");
        //STOP.setText("...");
        ws().ws().viewCommon().finish();
        notify.log("Операция завершена "+ Utils.toTimeString(ws().viewCommon().timeInMs()/1000)+" сек");
        notify.setProgress(0);
        setMenuVisible();
        setWidth(false);
        }
    public void breakOperation(){
        ws().viewCommon().finish();
        finishOperation();
        }
    final int mFile=0;
    final int mView=1;
    final int mSlice=2;
    final int mSet=3;
    final int mPrint=4;
    final int mOther=5;
    private void setMenuVisible(){
        boolean loaded = ws().modelPresent();
        boolean sliced = ws().slicePresent();
        boolean merge = sliced && !ws().data().isMerged();
        int userType = ws().currentUser().accessMode;
        boolean isAdmin = userType==Values.userAdmin;
        boolean canSave = userType==Values.userAdmin || userType==Values.userConstructor;
        //mBar.getMenu(mSlice).setEnabled(loaded || sliced);
        //mBar.getMenu(mSet).setEnabled(userType!=Values.userGuest);
        //mBar.getMenu(mPrint).setEnabled(userType!=Values.userGuest);
        //mBar.getMenu(mOther).setEnabled(isAdmin);
        //--------------------------------------------------------------------
        //mBar.getMenu(mFile).getItem(2).setEnabled(sliced && canSave);
        //mBar.getMenu(mFile).getItem(3).setEnabled(sliced && canSave);
        //mBar.getMenu(mFile).getItem(4).setEnabled(sliced && canSave);
        //mBar.getMenu(mView).getItem(0).setEnabled(loaded);
        //mBar.getMenu(mView).getItem(1).setEnabled(sliced);
        //mBar.getMenu(mView).getItem(2).setEnabled(loaded);
        //mBar.getMenu(mSet).getItem(1).setEnabled(isAdmin);
        //mBar.getMenu(mSlice).getItem(2).setEnabled((loaded || sliced)&&userType!=Values.userGuest);
        //mBar.getMenu(mSlice).getItem(3).setEnabled((loaded || sliced)&&userType!=Values.userGuest);
        }
        /**
         * This method is called from within the constructor to initialize the form.
         * WARNING: Do NOT modify this code. The content of this method is always
         * regenerated by the Form Editor.
         */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuBar1 = new java.awt.MenuBar();
        file = new java.awt.Menu();
        openSTL = new java.awt.MenuItem();
        loadSLM3D = new java.awt.MenuItem();
        saveSLM3D = new java.awt.MenuItem();
        saveSLM3Das = new java.awt.MenuItem();
        ExportGCode = new java.awt.MenuItem();
        distortionTable = new java.awt.MenuItem();
        TestConsole = new java.awt.MenuItem();
        createQube = new java.awt.MenuItem();
        createWalls = new java.awt.MenuItem();
        view = new java.awt.Menu();
        view3D = new java.awt.MenuItem();
        viewSLM3D = new java.awt.MenuItem();
        viewLoops3D = new java.awt.MenuItem();
        slice = new java.awt.Menu();
        STL_3D_DATA = new java.awt.MenuItem();
        STL_Concurent = new java.awt.MenuItem();
        STL_ConcurentSave = new java.awt.MenuItem();
        STL_ConcurentSaveAs = new java.awt.MenuItem();
        settings = new java.awt.Menu();
        settings2 = new java.awt.MenuItem();
        UserList = new java.awt.MenuItem();
        printer = new java.awt.Menu();
        LaserConsole = new java.awt.MenuItem();
        other = new java.awt.Menu();
        SliceTo = new java.awt.Menu();
        STL_USB = new java.awt.MenuItem();
        STL_UDP = new java.awt.MenuItem();
        STL_SHOW = new java.awt.MenuItem();
        STL_CIRCUIT_SHOW = new java.awt.MenuItem();
        Console = new java.awt.Menu();
        USBSequence = new java.awt.MenuItem();
        UDPSequence = new java.awt.MenuItem();
        operationConsole = new java.awt.MenuItem();
        operationConsoleUDP = new java.awt.MenuItem();
        Settings = new java.awt.Menu();
        fileSettings = new java.awt.MenuItem();
        allSettings = new java.awt.MenuItem();
        Tests = new java.awt.Menu();
        testUsbMouse = new java.awt.MenuItem();
        testMS = new java.awt.MenuItem();
        MarkOut = new java.awt.Menu();
        directToUsb = new java.awt.MenuItem();
        viewDirectToUsb = new java.awt.MenuItem();
        dumpout = new java.awt.MenuItem();
        markcopy = new java.awt.MenuItem();
        markout = new java.awt.MenuItem();
        usb = new java.awt.MenuItem();
        STL_MARK = new java.awt.MenuItem();
        Copy = new java.awt.Menu();
        SLM3D_USB = new java.awt.MenuItem();
        SLM3D_UDP = new java.awt.MenuItem();
        PanelList = new javax.swing.JTabbedPane();
        Common = new javax.swing.JPanel();

        file.setLabel("Модель");
        file.setName("File");

        openSTL.setLabel("Открыть STL");
        openSTL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openSTLActionPerformed(evt);
            }
        });
        file.add(openSTL);

        loadSLM3D.setLabel("Открыть SLM3D");
        loadSLM3D.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadSLM3DActionPerformed(evt);
            }
        });
        file.add(loadSLM3D);

        saveSLM3D.setLabel("Сохранить SLM3D");
        saveSLM3D.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSLM3DActionPerformed(evt);
            }
        });
        file.add(saveSLM3D);

        saveSLM3Das.setLabel("Сохранить SLM3D как...");
        saveSLM3Das.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSLM3DasActionPerformed(evt);
            }
        });
        file.add(saveSLM3Das);

        ExportGCode.setLabel("Экспорт GCode");
        ExportGCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportGCodeActionPerformed(evt);
            }
        });
        file.add(ExportGCode);

        distortionTable.setLabel("Коррекция геометрии");
        distortionTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                distortionTableActionPerformed(evt);
            }
        });
        file.add(distortionTable);
        distortionTable.getAccessibleContext().setAccessibleName("Коррекция искажений");

        TestConsole.setLabel("Редактор тестов");
        TestConsole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TestConsoleActionPerformed(evt);
            }
        });
        file.add(TestConsole);

        createQube.setLabel("Модель - кубик");
        createQube.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createQubeActionPerformed(evt);
            }
        });
        file.add(createQube);

        createWalls.setLabel("Модель - решетка");
        createWalls.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createWallsActionPerformed(evt);
            }
        });
        file.add(createWalls);

        menuBar1.add(file);

        view.setLabel("Просмотр");

        view3D.setLabel("Просмотр STL 3D");
        view3D.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                view3DActionPerformed(evt);
            }
        });
        view.add(view3D);

        viewSLM3D.setLabel("Просмотр SLM3D по слоям");
        viewSLM3D.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewSLM3DActionPerformed(evt);
            }
        });
        view.add(viewSLM3D);

        viewLoops3D.setLabel("Просмотр контуров 3D");
        viewLoops3D.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewLoops3DActionPerformed(evt);
            }
        });
        view.add(viewLoops3D);

        menuBar1.add(view);

        slice.setLabel("Слайсинг");
        slice.setName("");

        STL_3D_DATA.setLabel("Последовательный");
        STL_3D_DATA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                STL_3D_DATAActionPerformed(evt);
            }
        });
        slice.add(STL_3D_DATA);

        STL_Concurent.setLabel("Параллельный слайсинг");
        STL_Concurent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                STL_ConcurentActionPerformed(evt);
            }
        });
        slice.add(STL_Concurent);

        STL_ConcurentSave.setLabel("Параллельный слайсинг в файл");
        STL_ConcurentSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                STL_ConcurentSaveActionPerformed(evt);
            }
        });
        slice.add(STL_ConcurentSave);

        STL_ConcurentSaveAs.setLabel("Параллельный слайсинг в файл как...");
        STL_ConcurentSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                STL_ConcurentSaveAsActionPerformed(evt);
            }
        });
        slice.add(STL_ConcurentSaveAs);

        menuBar1.add(slice);
        slice.getAccessibleContext().setAccessibleName("Слайсинг в...");

        settings.setLabel("Настройки");

        settings2.setLabel("Технологические");
        settings2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settings2ActionPerformed(evt);
            }
        });
        settings.add(settings2);

        UserList.setLabel("Профили");
        UserList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UserListActionPerformed(evt);
            }
        });
        settings.add(UserList);

        menuBar1.add(settings);

        printer.setLabel("Принтер");

        LaserConsole.setLabel("Консоль лазера");
        LaserConsole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LaserConsoleActionPerformed(evt);
            }
        });
        printer.add(LaserConsole);

        menuBar1.add(printer);

        other.setLabel("Прочее");

        SliceTo.setLabel("Прямой слайсинг");

        STL_USB.setLabel("Слайсинг в USB");
        STL_USB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                STL_USBActionPerformed(evt);
            }
        });
        SliceTo.add(STL_USB);

        STL_UDP.setLabel("Слайсинг в TCP/IP");
        STL_UDP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                STL_UDPActionPerformed(evt);
            }
        });
        SliceTo.add(STL_UDP);

        STL_SHOW.setLabel("Просмотр");
        STL_SHOW.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                STL_SHOWActionPerformed(evt);
            }
        });
        SliceTo.add(STL_SHOW);

        STL_CIRCUIT_SHOW.setLabel("Слайсинг концентрический");
        STL_CIRCUIT_SHOW.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                STL_CIRCUIT_SHOWActionPerformed(evt);
            }
        });
        SliceTo.add(STL_CIRCUIT_SHOW);

        other.add(SliceTo);

        Console.setLabel("Принтер");

        USBSequence.setLabel("Секвенсор  USB");
        USBSequence.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                USBSequenceActionPerformed(evt);
            }
        });
        Console.add(USBSequence);

        UDPSequence.setLabel("Секвенсор  TCP/IP");
        UDPSequence.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UDPSequenceActionPerformed(evt);
            }
        });
        Console.add(UDPSequence);

        operationConsole.setLabel("Консоль оператора (USB)");
        operationConsole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                operationConsoleActionPerformed(evt);
            }
        });
        Console.add(operationConsole);

        operationConsoleUDP.setActionCommand("Консоль оператора (TCP/IP)");
        operationConsoleUDP.setLabel("Консоль оператора (UDP)");
        operationConsoleUDP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                operationConsoleUDPActionPerformed(evt);
            }
        });
        Console.add(operationConsoleUDP);

        other.add(Console);

        Settings.setLabel("Настройки");

        fileSettings.setActionCommand("Настройки локальные");
        fileSettings.setLabel("Локальные");
        fileSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileSettingsActionPerformed(evt);
            }
        });
        Settings.add(fileSettings);

        allSettings.setLabel("Настройки глобальные");
        allSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allSettingsActionPerformed(evt);
            }
        });
        Settings.add(allSettings);

        other.add(Settings);

        Tests.setLabel("Тесты");

        testUsbMouse.setLabel("LibUsbK мышка");
        testUsbMouse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testUsbMouseActionPerformed(evt);
            }
        });
        Tests.add(testUsbMouse);

        testMS.setLabel("LibUsbK флешка");
        testMS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testMSActionPerformed(evt);
            }
        });
        Tests.add(testMS);

        other.add(Tests);

        MarkOut.setLabel("Прототип");

        directToUsb.setLabel("mark.out - USB");
        directToUsb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                directToUsbActionPerformed(evt);
            }
        });
        MarkOut.add(directToUsb);

        viewDirectToUsb.setLabel("mark.out - USB данные");
        viewDirectToUsb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewDirectToUsbActionPerformed(evt);
            }
        });
        MarkOut.add(viewDirectToUsb);

        dumpout.setLabel("Дамп mark.out");
        dumpout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dumpoutActionPerformed(evt);
            }
        });
        MarkOut.add(dumpout);

        markcopy.setLabel("Препарировать в mark.out");
        markcopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                markcopyActionPerformed(evt);
            }
        });
        MarkOut.add(markcopy);

        markout.setLabel("Смотреть mark.out");
        markout.setName("");
        markout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                markoutActionPerformed(evt);
            }
        });
        MarkOut.add(markout);

        usb.setLabel("Конвертировать mark.out в USB");
        usb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usbActionPerformed(evt);
            }
        });
        MarkOut.add(usb);

        STL_MARK.setLabel("Слайсинг в mark.out");
        STL_MARK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                STL_MARKActionPerformed(evt);
            }
        });
        MarkOut.add(STL_MARK);

        other.add(MarkOut);

        Copy.setLabel("Прямая печать");

        SLM3D_USB.setLabel("Растр (USB)");
        SLM3D_USB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SLM3D_USBActionPerformed(evt);
            }
        });
        Copy.add(SLM3D_USB);

        SLM3D_UDP.setLabel("Растр (TCP/IP)");
        SLM3D_UDP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SLM3D_UDPActionPerformed(evt);
            }
        });
        Copy.add(SLM3D_UDP);
        SLM3D_UDP.getAccessibleContext().setAccessibleName("Прямая печать (TCP/IP)");

        other.add(Copy);

        menuBar1.add(other);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeWindow(evt);
            }
        });
        getContentPane().setLayout(null);

        PanelList.setBorder(new javax.swing.border.MatteBorder(null));
        getContentPane().add(PanelList);
        PanelList.setBounds(0, 0, 680, 720);

        Common.setBorder(new javax.swing.border.MatteBorder(null));
        Common.setLayout(null);
        getContentPane().add(Common);
        Common.setBounds(690, 0, 180, 720);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void markoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_markoutActionPerformed
        showMarkOut();
    }//GEN-LAST:event_markoutActionPerformed

    private void markcopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_markcopyActionPerformed
        copyMarkOut();
    }//GEN-LAST:event_markcopyActionPerformed

    private void usbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usbActionPerformed
        sendToUsb();
    }//GEN-LAST:event_usbActionPerformed

    private void testUsbMouseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testUsbMouseActionPerformed
        testMouse();
    }//GEN-LAST:event_testUsbMouseActionPerformed

    private void STL_MARKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_STL_MARKActionPerformed
        sliceToMarkOut();
    }//GEN-LAST:event_STL_MARKActionPerformed

    private void STL_SHOWActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_STL_SHOWActionPerformed
        showSlice(false);
    }//GEN-LAST:event_STL_SHOWActionPerformed

    private void STL_UDPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_STL_UDPActionPerformed
        sliceToUsb(true);
    }//GEN-LAST:event_STL_UDPActionPerformed

    private void closeWindow(){
        notify.log("Закрытие сеанса - 3 сек.");
        ws().sendEvent(this,Events.Close,1,0,"",null);
        ws().saveSettings();
        sendEvent(Events.LogFileClose,0,0,null,null);
        Utils.runAfterDelay(3,()->{
            ws().sendEvent(this,Events.LogOut,1,0,"",null);
            onClose();
            });
        }
    
    private void closeWindow(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeWindow
        if (ws().printing()==Events.PStateWorking){
            notify.log("Для выхода - прекратить печать");
            }
        else{
            if (ws().sliceChanged()){
                new OKFull(getBounds(),"Сохранить изменения",(yes)->{
                    if (yes)
                        saveSLM3D(false);
                    closeWindow();                
                    });
                }
            else
                closeWindow();
            }
    }//GEN-LAST:event_closeWindow

    private void openSTLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openSTLActionPerformed
        WorkSpace ws = ws();
        ws.removeAll();
        setMenuVisible();
        openModel();
        setMenuVisible();
    }//GEN-LAST:event_openSTLActionPerformed

    private void allSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allSettingsActionPerformed
        /*
        if (global==null) {
            global = new M3DSettings(() -> global = null, ws().global(), "Глобальные настройки");
            global.setVisible(true);
            }
        else global.toFront();
         */
    }//GEN-LAST:event_allSettingsActionPerformed

    private void USBSequenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_USBSequenceActionPerformed
        new M3DSequencer(notify,false).setVisible(true);
    }//GEN-LAST:event_USBSequenceActionPerformed

    private void testMSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testMSActionPerformed
        testMassStorage();
    }//GEN-LAST:event_testMSActionPerformed

    private void toLog(String mes){
        sendEvent(Events.Log,Values.common,0,mes,null);
        }
    
    private void view3DActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_view3DActionPerformed
        if (!ws().model().loaded()){
            toLog("Не загружен STL-файл");
            return;
            }
        new STLViewer(ws().ws().viewCommon()).setVisible(true);
    }//GEN-LAST:event_view3DActionPerformed

    private void directToUsbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_directToUsbActionPerformed
        sendDirectToUsb();
    }//GEN-LAST:event_directToUsbActionPerformed

    private void viewDirectToUsbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewDirectToUsbActionPerformed
        viewDirectToUsb(); //------------------
    }//GEN-LAST:event_viewDirectToUsbActionPerformed

    private void dumpoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dumpoutActionPerformed
        dumpMarkOut();
    }//GEN-LAST:event_dumpoutActionPerformed

    private void STL_USBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_STL_USBActionPerformed
        sliceToUsb(false);
    }//GEN-LAST:event_STL_USBActionPerformed

    private void UDPSequenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UDPSequenceActionPerformed
        new M3DSequencer(notify,true).setVisible(true);
    }//GEN-LAST:event_UDPSequenceActionPerformed

    private void fileSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileSettingsActionPerformed
        if (!ws().model().loaded()){
            notify.notify(Values.error,"Нет открытой модели для редактирования настроек");
            return;
            }
        /*
        if (local==null){
            local = new M3DSettings(()-> local=null,ws().local(),"Настройки модели");
            local.setVisible(true);
            }
        else
            local.toFront();
         */
    }//GEN-LAST:event_fileSettingsActionPerformed

    private void STL_CIRCUIT_SHOWActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_STL_CIRCUIT_SHOWActionPerformed
        showSlice(true);
    }//GEN-LAST:event_STL_CIRCUIT_SHOWActionPerformed

    private void STL_3D_DATAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_STL_3D_DATAActionPerformed
        sliceTo3D();
    }//GEN-LAST:event_STL_3D_DATAActionPerformed

    private void viewSLM3DActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewSLM3DActionPerformed
        if (!ws().slicePresent()){
            toLog("Не выполнен слайсинг в память");
            return;
            }
        new Slice2DViewer(notify);
    }//GEN-LAST:event_viewSLM3DActionPerformed



    private void loadSLM3DActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadSLM3DActionPerformed
        if (test1()) return;
        final String fname = getInputFileName("Файл слайсинга",Values.FileType,true);
        if (fname==null) return;
        startView(0,0);
        new Thread(
                ()->{
                    try {
                        ws().removeAll();
                        setMenuVisible();
                        ws().load(new DataInputStream(new FileInputStream(fname)));
                        ws().lastName(fname);
                        ws().fileStateChanged();
                        } catch (IOException e) { notify.notify(Values.error,e.toString()); }
                    finishOperation();
                }).start();
    }//GEN-LAST:event_loadSLM3DActionPerformed

    private void saveSLM3D(final boolean defName){
        if (test3()) return;
        String dir = ws().defaultFileName();
        final String outname = defName ? dir : getOutputFileName("Файл слайсинга",Values.FileType,dir);
        notify.log("Сохранен файл "+outname);
        startView(0,0);
        new Thread(
                ()->{
                    try {
                        ws().save(new DataOutputStream(new FileOutputStream(outname)));
                        ws().lastName(defName ? "" : outname);
                        ws().fileStateChanged();                          
                        } catch (IOException e) { notify.notify(Values.error,e.toString()); }
                    finishOperation();
                }).start();
        }
    
    private void exportGCode(){
        if (test3()) return;
        String dir = ws().defaultFileName();
        dir = Utils.changeFileExt(dir, "gcode");
        final String outname = getOutputFileName("Файл gcode","gcode",dir);
        if (outname==null)
            return;
        notify.log("Экспорт в файл "+outname);
        startView(0,0);
        new Thread(
                ()->{
                    try {
                        ws.operate().exportToGCode(ws().viewCommon(),new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outname))));
                        } catch (IOException e) { notify.notify(Values.error,e.toString()); }
                    finishOperation();
                }).start();
        }
    
    private void saveSLM3DActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSLM3DActionPerformed
        saveSLM3D(true);
    }//GEN-LAST:event_saveSLM3DActionPerformed

   
    private void SLM3D_UDPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SLM3D_UDPActionPerformed
        copyToUSB(false,new USBProtocol(new USBUDPEmulator()),true);
    }//GEN-LAST:event_SLM3D_UDPActionPerformed

    public void copyToUSB(final boolean stopPoint,final USBProtocol protocol,final boolean init){
        if (test3()) return;
        startView(0,0);
        new Thread(
            ()->{
                try{
                    if (init) protocol.init();
                    ws.operate().copySLM3DtoUSB(ws().ws().viewCommon(),stopPoint,protocol);
                    if (init) protocol.close();
                    } catch (UNIException ee){ notify.notify(Values.error,ee.toString());}
                finishOperation();
            }).start();        
        }
    
    private void SLM3D_USBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SLM3D_USBActionPerformed
        copyToUSB(false,new USBProtocol(new USBLineController()),true);
    }//GEN-LAST:event_SLM3D_USBActionPerformed

    private void operationConsoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_operationConsoleActionPerformed
        //new ConsoleOfOperator(notify,false);
    }//GEN-LAST:event_operationConsoleActionPerformed

    private void generateSTLModel(I_ModelGenerator gen){
        STLModel3D model = new STLModel3D();
        model.generate(gen);
        WorkSpace ws = ws();
        ws.model(model);
        setMenuVisible();
        startView(0,0);
        try {
            ws.local(ws.loadSettings());            // Копия настроек из файла
            ws.local().setZStartFinish();
            ws.data(null);
            String outname = getOutputFileName("Файл stl","stl",gen.name()+".stl");
            model.saveSTL(outname);
        } catch (Exception e) { notify.notify(Values.error,e.getMessage());}
        finishOperation();
    }

    private void createQubeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createQubeActionPerformed
        generateSTLModel(new STLGeneratorQube(10));
    }//GEN-LAST:event_createQubeActionPerformed

    private void createWallsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createWallsActionPerformed
        generateSTLModel(new STLGeneratorWalls(10));
    }//GEN-LAST:event_createWallsActionPerformed

    private void STL_ConcurentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_STL_ConcurentActionPerformed
        sliceConcurent();
    }//GEN-LAST:event_STL_ConcurentActionPerformed

    private void STL_ConcurentSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_STL_ConcurentSaveActionPerformed
        sliceConcurentToFile(true);
    }//GEN-LAST:event_STL_ConcurentSaveActionPerformed

    private void operationConsoleUDPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_operationConsoleUDPActionPerformed
        //new ConsoleOfOperator(notify,true);
    }//GEN-LAST:event_operationConsoleUDPActionPerformed

    private void settings2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settings2ActionPerformed
        new M3DSettings_2(notify);
    }//GEN-LAST:event_settings2ActionPerformed

    private void UserListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UserListActionPerformed
        new UserListEditor().setVisible(true);
    }//GEN-LAST:event_UserListActionPerformed

    private void saveSLM3DasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSLM3DasActionPerformed
        saveSLM3D(false);
    }//GEN-LAST:event_saveSLM3DasActionPerformed

    private void STL_ConcurentSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_STL_ConcurentSaveAsActionPerformed
        sliceConcurentToFile(false);
    }//GEN-LAST:event_STL_ConcurentSaveAsActionPerformed

    private void viewLoops3DActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewLoops3DActionPerformed
        new Loop3DViewer();
    }//GEN-LAST:event_viewLoops3DActionPerformed

    private void ExportGCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportGCodeActionPerformed
        exportGCode();
    }//GEN-LAST:event_ExportGCodeActionPerformed

    private void distortionTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_distortionTableActionPerformed
        new DistortionEditor();
    }//GEN-LAST:event_distortionTableActionPerformed

    private void LaserConsoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LaserConsoleActionPerformed
        new LaserConsole();
    }//GEN-LAST:event_LaserConsoleActionPerformed

    private void TestConsoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TestConsoleActionPerformed
        new TestConsole(notify);
    }//GEN-LAST:event_TestConsoleActionPerformed

    public boolean test1(){
        if (ws().viewCommon().isRunning()){
            toLog("Прервать предыдущую операцию");
            return true;
            }
        return false;
        }
    public boolean test2(){
        if (test1()) return true;
        if (!ws().modelPresent()){
            toLog("Отсутствует STL-модель");
            return true;
            }
        return false;
        }
    public boolean test3(){
        if (test1()) return true;
        if (!ws().slicePresent()){
            toLog("Отсутствует растр");
            return true;
            }
        return false;
        }
    public void startView(int lineDelay,int layerDelay){
        sendEvent(Events.OnWarning,0,0,null,null);
        ws().viewCommon().start(lineDelay,layerDelay);
        //PAUSE.setText("остановить");
        //STOP.setText("прервать");
        }
    private void dumpMarkOut(){
        if (test1()) return;
        final String fname = getInputFileName("Файл прототипа","out",false);
        if (fname==null) return;
        startView(10,100);
        new Thread(
            ()->{
                ws.operate().dumpMarkOut(fname);
                finishOperation();
                }).start();
        }
    private void openModel(){
        try {
            if (test1()) return;
            final String fname = getInputFileName("Файл STL","stl",false);
            if (fname==null) return;
            ws().loadModel(fname, notify);
            refreshPanels();
        } catch (UNIException ee){ toLog(ee.toString());}
    }
    private void showMarkOut() {
        if (test1()) return;
        final String fname = getInputFileName("Двоичный файл", "out",false);
        if (fname==null) return;
        startView(5,300);
        new Thread(
            ()->{
                FileBinInputStream bb=null;
                try {
                    bb = new M3DFileBinInputStream(fname);
                } catch(FileNotFoundException ee){
                    notify.notify(Values.error,"Файл не найден");
                    return;
                    }
                ws.visio().show(bb,ws().viewCommon());
                finishOperation();
                }).start();
        }

    private void copyMarkOut() {
        if (test1()) return;
        final String fname = getInputFileName("Файл прототипа","out",false);
        if (fname==null) return;
        startView(0,0);
        new Thread(
            ()->{
                ws.operate().copy(fname);
                finishOperation();
                }).start();
        }

    private void sliceToMarkOut() {
        if (test2()) return;
        String dir = ws().defaultFileName()+".out" ;
        final String outname = getOutputFileName("Файл слайсинга",Values.FileType,dir);
        M3DFileBinOutputStream out=null;
        BinOutputStream out2=null;
        try {
            out = new M3DFileBinOutputStream(outname);
            } catch(FileNotFoundException ee){
                notify.notify(Values.fatal,ee.getMessage());
                return;
                }
        startView(1,100);
        final int mode = ws().local().slice.Mode.getVal();
        CommandGenerator gen = new FileCommandGenerator(out);
        new Thread(
                ()->{ws.operate().sliceTo(gen,ws().viewCommon());
                    finishOperation();
                    notify.log("Время выполнения: "+ws().viewCommon().timeInMs()/1000+" сек.");
                    }).start();
            }


    private void sliceToUsb(boolean test) {
        if (test2()) return;
        startView(0,0);
        final int mode = ws().local().slice.Mode.getVal();
        USBFace face = test ? new USBUDPEmulator() : new USBLineController();
        new Thread(
             ()->{
                 ws.operate().sliceTo(new USBCommandGenerator(face,notify), ws().viewCommon());
                 finishOperation();
                 }).start();
            }

    private void dataChanged(){
        WorkSpace ws = ws();
        ws.dataChanged();
        ws.fileStateChanged();        
        }
    private void sliceTo3D() {
        if (test2()) return;
        startView(0,0);
        new Thread(
                ()->{
                    SliceData data = new SliceData();
                    ws().data(data);
                    ws.operate().sliceTo(new SliceDataGenerator(data,ws().viewCommon()),ws().viewCommon());
                    if (!data.isSliceStop())
                        dataChanged();
                    finishOperation();
                    }).start();
            }
     private void sliceConcurent() {
        if (test2()) return;
        startView(0,0);
        new Thread(
                ()->{
                    SliceData data = ws.operate().sliceConcurent(ws().viewCommon());
                    ws().data(data);
                    ws().lastName("");
                    if (!data.isSliceStop())
                        dataChanged();                    
                    finishOperation();
                    }).start();
            }
     private void sliceConcurentToFile(boolean defName) {
        if (test2()) return;
        String dir = ws().defaultFileName();
        final String outname = defName ? dir : getOutputFileName("Файл слайсинга",Values.FileType,dir);
        if (outname == null) return;
        
        startView(0,0);
        new Thread(
                ()->{
                    try {
                        notify.log("Слайсинг в файл "+outname);
                        SliceData data = ws.operate().sliceConcurent(ws().viewCommon(),new DataOutputStream(new FileOutputStream(outname)));
                        ws().lastName(defName ? "" : outname);
                        //------------- Состояние dataState не меняется ----------------------------------------
                        ws().fileStateChanged();
                        } catch (IOException e) { notify.notify(Values.error,e.toString()); }
                    finishOperation();
                }).start();
            }

    private void showSlice(final boolean circuit) {
        if (test3()) return;
        startView(10,500);
        sendEvent(Events.OnWarning,0,0,null,null);
        final int mode = ws().local().slice.Mode.getVal();
        setWidth(true);
        new Thread(
                ()->{
                    if (circuit)
                        ws.operate().sliceTo(new VisualCommandGenerator(ws().viewCommon()),ws().viewCommon());
                    else
                        ws.operate().sliceTo(new VisualCommandGenerator(ws().viewCommon()),ws().viewCommon());
                    finishOperation();
                    }).start();
            }
    private void testMouse() {
            new Thread(()->{testing.testMouse();}).start();
        }
    private void testMassStorage() {
        new Thread(()->{
            testing.testMassStorageAsync();}
            ).start();
        }

    private void sendToUsb() {
        if (test1()) return;
        final String fname = getInputFileName("Файл прототипа","out",false);
        if (fname==null) return;
        startView(0,0);
        new Thread(
            ()->{
                ws.operate().toUsb(fname);
                finishOperation();
                }).start();
        }
    private void sendDirectToUsb() {
        if (test1()) return;
        final String fname = getInputFileName("Файл прототипа","out",false);
        if (fname==null) return;
        startView(0,0);
        new Thread(
                ()->{
                    testing.copyDirectToUsb(fname);
                    finishOperation();
                }).start();
        }
    private void viewDirectToUsb() {
        if (test1()) return;
        final String fname = getInputFileName("Файл прототипа","out",false);
        if (fname==null) return;
        startView(0,0);
        new Thread(
                ()->{
                    testing.wievDirectToUsb(fname);
                    finishOperation();
                }).start();
        }

    @Override
    public void refresh() {
    }

    @Override
    public void shutDown() {
        for(BasePanel panel : getPanels()) {
            if (panel.isSelected())
                panel.onDeactivate();
            panel.shutDown();
            }
    dispose();
    }



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
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(CNCViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        //</editor-fold>
        java.awt.EventQueue.invokeLater(()->{ new CNCViewer().setVisible(true); });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Common;
    private java.awt.Menu Console;
    private java.awt.Menu Copy;
    private java.awt.MenuItem ExportGCode;
    private java.awt.MenuItem LaserConsole;
    private java.awt.Menu MarkOut;
    private javax.swing.JTabbedPane PanelList;
    private java.awt.MenuItem SLM3D_UDP;
    private java.awt.MenuItem SLM3D_USB;
    private java.awt.MenuItem STL_3D_DATA;
    private java.awt.MenuItem STL_CIRCUIT_SHOW;
    private java.awt.MenuItem STL_Concurent;
    private java.awt.MenuItem STL_ConcurentSave;
    private java.awt.MenuItem STL_ConcurentSaveAs;
    private java.awt.MenuItem STL_MARK;
    private java.awt.MenuItem STL_SHOW;
    private java.awt.MenuItem STL_UDP;
    private java.awt.MenuItem STL_USB;
    private java.awt.Menu Settings;
    private java.awt.Menu SliceTo;
    private java.awt.MenuItem TestConsole;
    private java.awt.Menu Tests;
    private java.awt.MenuItem UDPSequence;
    private java.awt.MenuItem USBSequence;
    private java.awt.MenuItem UserList;
    private java.awt.MenuItem allSettings;
    private java.awt.MenuItem createQube;
    private java.awt.MenuItem createWalls;
    private java.awt.MenuItem directToUsb;
    private java.awt.MenuItem distortionTable;
    private java.awt.MenuItem dumpout;
    private java.awt.Menu file;
    private java.awt.MenuItem fileSettings;
    private java.awt.MenuItem loadSLM3D;
    private java.awt.MenuItem markcopy;
    private java.awt.MenuItem markout;
    private java.awt.MenuBar menuBar1;
    private java.awt.MenuItem openSTL;
    private java.awt.MenuItem operationConsole;
    private java.awt.MenuItem operationConsoleUDP;
    private java.awt.Menu other;
    private java.awt.Menu printer;
    private java.awt.MenuItem saveSLM3D;
    private java.awt.MenuItem saveSLM3Das;
    private java.awt.Menu settings;
    private java.awt.MenuItem settings2;
    private java.awt.Menu slice;
    private java.awt.MenuItem testMS;
    private java.awt.MenuItem testUsbMouse;
    private java.awt.MenuItem usb;
    private java.awt.Menu view;
    private java.awt.MenuItem view3D;
    private java.awt.MenuItem viewDirectToUsb;
    private java.awt.MenuItem viewLoops3D;
    private java.awt.MenuItem viewSLM3D;
    // End of variables declaration//GEN-END:variables
}
