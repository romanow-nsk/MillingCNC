/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.m3d;

import romanow.cnc.view.BaseFrame;
import romanow.cnc.graph.GraphPanel;
import romanow.cnc.graph.I_Mouse;
import romanow.cnc.settings.Settings;
import romanow.cnc.settings.WorkSpace;
import romanow.cnc.settingsView.LayerPrintSettings;
import romanow.cnc.settingsView.StatisticPanel;

import romanow.cnc.slicer.*;
import romanow.cnc.stl.*;
import romanow.cnc.utils.Events;
import romanow.cnc.utils.I_Notify;
import romanow.cnc.utils.Utils;
import romanow.cnc.Values;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 *
 * @author romanow
 */
public class Slice2DViewer extends BaseFrame{
    private final int  masMax=20;
    private SliceData data;
    private int mode=0;
    private int cLayer=-1;
    private double dz;
    private int lIndexes[];
    private int lSize=0;
    private boolean onlyLoop=false;
    private int cLoop=0;
    private SliceLayer layer=null;
    private I_Notify notify;
    private STLLine selectedConturLine=null;
    private I_STLPoint2D one = null;            // Первая точка линии
    private I_STLPoint2D two = null;            // Вторая точка линии
    private int lastLayer=-1;
    private StatisticPanel statView;
    private DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
    private DecimalFormat df = new DecimalFormat("00.000", dfs);
    private Color gridColor = new Color(220,220,20);
    private Color pointColor = new Color(20,150,20);
    private GraphPanel gPanel;
    private int mouseX=-1;
    private int mouseY;
    private boolean drag=false;
    private int clickCount=0;
    private int nb=0;
    private STLPoint2D pp;

    /**
     * Creates new form M3DLayerViewer
     */
    private I_Mouse mBack = new I_Mouse(){
        @Override
        public void MouseDragged(MouseEvent evt) {
            mouseShift(evt);            
            }
        @Override
        public void MouseClicked(MouseEvent evt) {
            if (clickCount++ == 0){
                nb = evt.getButton();
                pp = new STLPoint2D(gPanel.pixelToX(evt.getX()),gPanel.pixelToY(evt.getY()));
                Utils.runAfterDelayMS((int)Values.DoubleClickInMS, ()->{
                    if (clickCount==1){
                        if (nb==1)
                            selectConturPoint(pp);
                        if (nb==3)
                            selectMousePoint(pp);
                        }
                    else{
                        if (nb==1)
                        selectConturLine(pp);
                        }
                    clickCount=0;
                });
            }
        }
        @Override
        public void MousePressed(MouseEvent evt) {
            if (evt.getButton()!=1) return;
            drag=true;
            mouseX = evt.getX();
            mouseY = evt.getY();       
            }
        @Override
        public void MouseReleased(MouseEvent evt) {
            if (evt.getButton()!=1) return;
            mouseShift(evt);
            mouseX=-1;
            drag=false;           
            }
        @Override
        public void MouseMoved(MouseEvent evt) {
            MX.setText(df.format(gPanel.pixelToX(evt.getX())));
            MY.setText(df.format(gPanel.pixelToY(evt.getY())));        
            }
        @Override
        public void MouseWheelMoved(MouseWheelEvent evt) {
            if (evt.getWheelRotation()>0){
                if (gPanel.mas < 50){
                    gPanel.mas++;
                    MAS.setValue(gPanel.mas);
                    paintView(false);
                    }
                }
            else{
                if (gPanel.mas > 1){
                    gPanel.mas--;
                    MAS.setValue(gPanel.mas);
                    paintView(false);
                }
            }            
            }
        };
    public Slice2DViewer(I_Notify notify0) {
        if (!tryToStart()) return;
        notify = notify0;
        dz = WorkSpace.ws().local().local.VerticalStep.getVal();
        data = ws().data();
        initComponents();
        statView = new StatisticPanel();
        statView.setBounds(800, 0, 200, 180);
        getContentPane().add(statView);
        setTitle("Просмотр слоев");
        setBounds(800,200,1015,730);
        gPanel = new GraphPanel(mBack);
        gPanel.setBounds(200, 40, 600);
        getContentPane().add(gPanel);
        gPanel.setPaintParams(HORIZ,VERTIC);
        MODE.add("Растр");
        MODE.add("Сечение");
        MODE.add("Контуры");
        MODE.add("Контур замкнут");
        MODE.add("Нечетные точки");
        MODE.add("Нечет исправлен");
        setLayers();
        selectMode(false);
        paintView(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        HORIZ = new javax.swing.JSlider();
        VERTIC = new javax.swing.JSlider();
        LAYERS = new java.awt.Choice();
        MAS = new javax.swing.JSlider();
        PREV = new javax.swing.JButton();
        NEXT = new javax.swing.JButton();
        MODE = new java.awt.Choice();
        MES = new javax.swing.JTextField();
        Points0 = new javax.swing.JCheckBox();
        Points2 = new javax.swing.JCheckBox();
        Points3 = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        LoopList = new java.awt.Choice();
        OnlyLoop = new javax.swing.JCheckBox();
        LoopPlus = new javax.swing.JButton();
        LoopMinus = new javax.swing.JButton();
        DeleteLoop = new javax.swing.JButton();
        LoopCalc = new javax.swing.JButton();
        B5 = new javax.swing.JTextField();
        B3 = new javax.swing.JButton();
        B2 = new javax.swing.JButton();
        LineRemove = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        LineLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        B4 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        B6 = new javax.swing.JButton();
        B7 = new javax.swing.JButton();
        LineLabel1 = new javax.swing.JLabel();
        LineInsert = new javax.swing.JButton();
        MoveToNearest = new javax.swing.JCheckBox();
        MY = new javax.swing.JTextField();
        PY = new javax.swing.JTextField();
        X = new javax.swing.JLabel();
        X1 = new javax.swing.JLabel();
        Grid = new javax.swing.JCheckBox();
        MX = new javax.swing.JTextField();
        PX = new javax.swing.JTextField();
        GroupSize = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        ShowPrint = new javax.swing.JToggleButton();
        ShowDelay = new javax.swing.JSlider();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        HORIZ.setMinimum(-100);
        HORIZ.setValue(0);
        HORIZ.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                HORIZStateChanged(evt);
            }
        });
        HORIZ.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                HORIZCaretPositionChanged(evt);
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
            }
        });
        getContentPane().add(HORIZ);
        HORIZ.setBounds(210, 650, 200, 23);

        VERTIC.setMinimum(-100);
        VERTIC.setOrientation(javax.swing.JSlider.VERTICAL);
        VERTIC.setValue(0);
        VERTIC.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                VERTICStateChanged(evt);
            }
        });
        VERTIC.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                VERTICCaretPositionChanged(evt);
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
            }
        });
        getContentPane().add(VERTIC);
        VERTIC.setBounds(170, 420, 20, 200);

        LAYERS.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                LAYERSItemStateChanged(evt);
            }
        });
        getContentPane().add(LAYERS);
        LAYERS.setBounds(10, 60, 180, 20);

        MAS.setMaximum(50);
        MAS.setMinimum(1);
        MAS.setValue(1);
        MAS.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                MASStateChanged(evt);
            }
        });
        getContentPane().add(MAS);
        MAS.setBounds(600, 650, 200, 23);

        PREV.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        PREV.setText("<");
        PREV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PREVActionPerformed(evt);
            }
        });
        getContentPane().add(PREV);
        PREV.setBounds(10, 90, 41, 30);

        NEXT.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        NEXT.setText(">");
        NEXT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NEXTActionPerformed(evt);
            }
        });
        getContentPane().add(NEXT);
        NEXT.setBounds(150, 90, 41, 30);

        MODE.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                MODEItemStateChanged(evt);
            }
        });
        getContentPane().add(MODE);
        MODE.setBounds(10, 10, 180, 20);
        getContentPane().add(MES);
        MES.setBounds(200, 10, 600, 25);

        Points0.setSelected(true);
        Points0.setText("Сечение");
        Points0.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                Points0ItemStateChanged(evt);
            }
        });
        getContentPane().add(Points0);
        Points0.setBounds(10, 290, 80, 23);

        Points2.setSelected(true);
        Points2.setText("Сторона");
        Points2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                Points2ItemStateChanged(evt);
            }
        });
        getContentPane().add(Points2);
        Points2.setBounds(90, 270, 80, 23);

        Points3.setSelected(true);
        Points3.setText("Плоскость");
        Points3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                Points3ItemStateChanged(evt);
            }
        });
        getContentPane().add(Points3);
        Points3.setBounds(90, 290, 100, 23);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Контуры");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(10, 270, 90, 14);

        LoopList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                LoopListItemStateChanged(evt);
            }
        });
        LoopList.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                LoopListCaretPositionChanged(evt);
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
            }
        });
        getContentPane().add(LoopList);
        LoopList.setBounds(10, 350, 180, 20);

        OnlyLoop.setText("Просмотр по контурам");
        OnlyLoop.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                OnlyLoopItemStateChanged(evt);
            }
        });
        getContentPane().add(OnlyLoop);
        OnlyLoop.setBounds(10, 320, 170, 23);

        LoopPlus.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        LoopPlus.setText(">");
        LoopPlus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoopPlusActionPerformed(evt);
            }
        });
        getContentPane().add(LoopPlus);
        LoopPlus.setBounds(120, 380, 41, 30);

        LoopMinus.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        LoopMinus.setText("<");
        LoopMinus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoopMinusActionPerformed(evt);
            }
        });
        getContentPane().add(LoopMinus);
        LoopMinus.setBounds(10, 380, 41, 30);

        DeleteLoop.setText("Удалить контур");
        DeleteLoop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteLoopActionPerformed(evt);
            }
        });
        getContentPane().add(DeleteLoop);
        DeleteLoop.setBounds(10, 420, 150, 23);

        LoopCalc.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        LoopCalc.setText("?");
        LoopCalc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoopCalcActionPerformed(evt);
            }
        });
        getContentPane().add(LoopCalc);
        LoopCalc.setBounds(60, 380, 39, 30);

        B5.setText("0.0");
        getContentPane().add(B5);
        B5.setBounds(10, 190, 50, 25);

        B3.setText("Добавить слой");
        B3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B3ActionPerformed(evt);
            }
        });
        getContentPane().add(B3);
        B3.setBounds(70, 190, 120, 23);

        B2.setText("Удалить слой");
        B2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B2ActionPerformed(evt);
            }
        });
        getContentPane().add(B2);
        B2.setBounds(70, 160, 120, 23);

        LineRemove.setText("Удалить линию");
        LineRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LineRemoveActionPerformed(evt);
            }
        });
        getContentPane().add(LineRemove);
        LineRemove.setBounds(10, 560, 150, 23);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Слой");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(10, 40, 34, 14);

        LineLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        LineLabel.setText("Сечение");
        getContentPane().add(LineLabel);
        LineLabel.setBounds(10, 620, 60, 14);
        getContentPane().add(jSeparator1);
        jSeparator1.setBounds(10, 450, 140, 2);

        B4.setText("Параметры печати");
        B4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B4ActionPerformed(evt);
            }
        });
        getContentPane().add(B4);
        B4.setBounds(10, 220, 180, 23);
        getContentPane().add(jSeparator2);
        jSeparator2.setBounds(10, 260, 180, 10);
        getContentPane().add(jSeparator3);
        jSeparator3.setBounds(10, 320, 170, 2);

        B6.setText("Загрузить сечение");
        B6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B6ActionPerformed(evt);
            }
        });
        getContentPane().add(B6);
        B6.setBounds(10, 640, 150, 23);

        B7.setText("Слайсинг");
        B7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B7ActionPerformed(evt);
            }
        });
        getContentPane().add(B7);
        B7.setBounds(70, 130, 120, 23);

        LineLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        LineLabel1.setText("Редактировать");
        getContentPane().add(LineLabel1);
        LineLabel1.setBounds(60, 460, 110, 14);

        LineInsert.setText("Добавить линию");
        LineInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LineInsertActionPerformed(evt);
            }
        });
        getContentPane().add(LineInsert);
        LineInsert.setBounds(10, 590, 150, 23);

        MoveToNearest.setSelected(true);
        MoveToNearest.setText("Привязка к ближайшей");
        getContentPane().add(MoveToNearest);
        MoveToNearest.setBounds(10, 535, 160, 23);

        MY.setEditable(false);
        MY.setBackground(new java.awt.Color(200, 200, 200));
        MY.setText("0");
        getContentPane().add(MY);
        MY.setBounds(100, 510, 60, 25);

        PY.setText("0");
        PY.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PYKeyPressed(evt);
            }
        });
        getContentPane().add(PY);
        PY.setBounds(30, 510, 60, 25);

        X.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        X.setText("Y");
        getContentPane().add(X);
        X.setBounds(10, 515, 20, 15);

        X1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        X1.setText("X");
        getContentPane().add(X1);
        X1.setBounds(10, 486, 20, 15);

        Grid.setSelected(true);
        Grid.setText("Сетка (мм)");
        Grid.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                GridItemStateChanged(evt);
            }
        });
        getContentPane().add(Grid);
        Grid.setBounds(60, 95, 90, 23);

        MX.setEditable(false);
        MX.setBackground(new java.awt.Color(200, 200, 200));
        MX.setText("0");
        getContentPane().add(MX);
        MX.setBounds(100, 480, 60, 25);

        PX.setText("0");
        PX.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PXKeyPressed(evt);
            }
        });
        getContentPane().add(PX);
        PX.setBounds(30, 480, 60, 25);

        GroupSize.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        GroupSize.setMinimumSize(new java.awt.Dimension(37, 25));
        GroupSize.setPreferredSize(new java.awt.Dimension(37, 25));
        getContentPane().add(GroupSize);
        GroupSize.setBounds(880, 190, 50, 25);

        jLabel3.setText("NxN : 1");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(810, 195, 50, 20);

        jButton1.setText("Слить слои");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(810, 220, 120, 23);

        ShowPrint.setText("Развертка");
        ShowPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShowPrintActionPerformed(evt);
            }
        });
        getContentPane().add(ShowPrint);
        ShowPrint.setBounds(810, 250, 120, 23);

        ShowDelay.setMajorTickSpacing(1);
        ShowDelay.setValue(10);
        ShowDelay.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ShowDelayStateChanged(evt);
            }
        });
        getContentPane().add(ShowDelay);
        ShowDelay.setBounds(820, 290, 160, 23);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private boolean testOnlyLoopMode(){
        if (layer==null)
            return false;
        if (mode==2 && onlyLoop)
            return true;
        return false;
        }
    private void setDrawNoSelected(){
        selectedConturLine=null;
        one = null;
        two = null;
        }
    private I_STLPoint2D nearest(I_STLPoint2D pp){
        if (!MoveToNearest.isSelected()) return pp;
        if (pp==null) return pp;
        STLLineGroup gg = mode==0 ? layer.segments() : layer.lines();
        I_STLPoint2D pp2 = gg.nearestPoint(pp,Values.NearestPointDistance);
        return pp2 == null ? pp : pp2;
        } 
    private void selectMousePoint(I_STLPoint2D point){
        if (layer==null) return;
        if (!(mode==0 || mode==1)) {
            notify.info("Точки строятся на растре или на сечении");
            return;
            }
        
        paintView(false);
        
        if (one==null){
            one = nearest(point);
            gPanel.drawPointBold(Color.blue,one);
            }
        else
        if (two==null){
            two = nearest(point);
            gPanel.drawPointBold(Color.blue,two);
            }
        else{
            notify.info("Создается более 2 точек - очистить");
            one = null;
            two = null;
            paintView(false);
            }
        }
    private void selectConturPoint(I_STLPoint2D point){
        if (layer==null) return;
        if (!(mode==0 || mode==1)) {
            notify.info("Точки строятся на растре или на сечении");
            return;
            }
        if (one!=null && two!=null){
            notify.info("Создается более 2 точек - очистить");
            one = null;
            two = null;
            PX.setText("");
            PY.setText("");
            paintView(false);
            return;
            }
        if (one==null){
            point = one = nearest(point);
            }
        else
        if (two==null){
            point = two = nearest(point);
            }
        PX.setText(df.format(point.x()));
        PY.setText(df.format(point.y()));
        paintView(false);           
        }
    private void selectConturLine(I_STLPoint2D point){
        if (layer==null) return;
        if (!(mode==0 || mode==1)) {
            notify.info("Линии выделяются на растре или на сечении");
            return;
            }
        if (selectedConturLine!=null){
            gPanel.drawLineNoBold(Color.black,selectedConturLine);
            }
        setDrawNoSelected();
        int idx=0;
        STLLineGroup gg = mode==0 ? layer.segments() : layer.lines();
        idx = gg.nearest(point);
        if (idx!=-1){
            selectedConturLine = gg.get(idx);
            }
        if (idx==-1) return;               
        String ss = "Линия: "+selectedConturLine.toString();
        MES.setText(ss);
        notify.info( ss);
        gPanel.drawLineBold(Color.black,selectedConturLine);
        }
    private void onlyLoopMode(boolean repaint){
        if (mode!=2)
            OnlyLoop.setSelected(false);
        else
            onlyLoop = OnlyLoop.isSelected();
        LoopPlus.setVisible(onlyLoop);
        LoopMinus.setVisible(onlyLoop);
        LoopList.setVisible(onlyLoop);
        LoopCalc.setVisible(onlyLoop);
        DeleteLoop.setVisible(onlyLoop);
        if (repaint)
            paintView(false);
        }
    private boolean visible(I_STLPoint2D point){
        return point.x()>=gPanel.xmin && point.x()<=gPanel.xmax && point.y()>=gPanel.ymin && point.y()<=gPanel.ymax;
        }
    private boolean visible(STLLine line){
        boolean in = visible(line.one()) || visible(line.two());
        if (in) return true;
        return line.lengthXY()>gPanel.dxy*gPanel.vSize;       // Длина большая !!!!!!
        }

    private void setLayersNoMove(){
        int cl = cLayer;
        setLayers();
        cLayer = cl;
        LAYERS.select(cLayer);
        }
    private void setLayers(){
        lIndexes = new int[data.size()];        
        gPanel.setGraphics();
        gPanel.clear();
        mode = MODE.getSelectedIndex();
        LAYERS.removeAll();
        double z0 = WorkSpace.ws().local().local.ZStart.getVal();
        int k=0;
        for(int i=0;i<data.size();i++){
            SliceLayer lr = data.get(i);
            if (mode<3 || lr.hasErrors(mode-3)){
                lIndexes[k]=i;
                LAYERS.add(lr.label());
                k++;
                }
            }
        lSize = k;
        if (lSize!=0){
            cLayer = 0;
            }
        else
            cLayer=-1;
        }
    private Color colors[] = {Color.darkGray,Color.green,Color.blue, Color.red};
    private void paintLoop(STLLoop loop){
        loop.testLoop();
        int loopType = loop.loopLineMode(); 
        Color cc = colors[loopType];
        for (int i=0;i<loop.size();i++){
            STLLine ln = loop.get(i);
            if (mode!=0){
                gPanel.drawPoint(cc,ln.one());
                gPanel.drawPoint(cc,ln.two());
                }
            gPanel.drawLine(cc,ln);
            }
        }
    private boolean inUse=false;
    private void paintView(final boolean newLayer){
        if (inUse)
            return;
        inUse=true;
        new Thread(()->{
            try {
                Thread.sleep(10);
                java.awt.EventQueue.invokeLater(()->{ 
                    paintViewOrig(newLayer);
                    inUse=false;
                    });
                } catch (InterruptedException ex) {}
            }).start();
        }
    private int showDelay=10;
    private boolean showRun=false;
    private void paintViewMode0(){   //Запуск в потоке
        paintViewOrig(false);
        for(STLLine line :layer.segments().lines()){
            if (!showRun) {
                break;
                }
            try { Thread.sleep(showDelay); } catch (InterruptedException ex) {}
            Utils.runInGUI(()->{
                //drawPoint(Color.red,line.one());
                gPanel.drawLine(Color.red,line);
                });
            }
        }

    private void paintViewOrig(boolean newLayer){
        if (cLayer == -1)
            return;
        if (newLayer){
            layer = data.get(lIndexes[cLayer]);
            onlyLoopMode(false);
            LoopList.removeAll();
            for (int i=0;i<layer.loops().size();i++)
                LoopList.addItem(""+(i+1));
            cLoop=0;
            }
        gPanel.setPaintParams(HORIZ,VERTIC);
        if (Grid.isSelected())
            gPanel.paintGrid(gridColor);
        gPanel.setColor(Color.black);
        //MES.setText("z="+String .format("%5.2f",layer.z()*)+" длина="+layer.rezult().printLength()+" время="+layer.rezult().printTime());
        if (mode<=2)
            statView.setValues(layer.rezult());
        if (mode==2) {
            if (onlyLoop) {
                if (layer.loops().size()!=0)
                    paintLoop(layer.loops().get(cLoop));
            } else {
                for (STLLoop loop : layer.loops()) {
                    int loopType = loop.loopLineMode();
                    boolean viz = loopType == 0 && Points0.isSelected() || loopType == 2 && Points2.isSelected() || loopType == 3 && Points3.isSelected();
                    if (viz)
                        paintLoop(loop);
                    }
                }
            }
        if (mode==0){
            for(STLLine line :layer.segments().lines()){
                gPanel.drawPoint(pointColor,line.one());
                gPanel.drawLine(Color.black,line);
                }
            }
        if (mode>2 || mode==1){
            for(STLLine line :layer.lines().lines()){
                gPanel.drawPoint(pointColor,line.one());
                gPanel.drawLine(Color.black,line);
                }
            }
        if (mode>=3){
            for(SliceError zz :layer.errorList()){
                if (zz.getErrorCode()==(mode-3))
                    zz.drawAll(new I_ErrorDraw() {
                        @Override
                        public void onLine(int mode, STLLine line) {
                            gPanel.drawLine(Color.red,line);
                            }
                        @Override
                        public void onReferedPoint(int mode, STLReferedPoint point) {
                            gPanel.drawPoint(Color.red,point);
                            gPanel.drawLine(Color.black,point.reference());
                            }
                        });
                    }
                }
        if (selectedConturLine!=null)
            gPanel.drawLineBold(Color.black,selectedConturLine);
        if (one!=null)
            gPanel.drawPointBold(two==null ? Color.red : Color.blue,one);
        if (two!=null)
            gPanel.drawPointBold(Color.red,two);
        }





    private void VERTICStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_VERTICStateChanged
        paintView(false);
    }//GEN-LAST:event_VERTICStateChanged

    private void HORIZCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_HORIZCaretPositionChanged
        paintView(false);
    }//GEN-LAST:event_HORIZCaretPositionChanged

    private void VERTICCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_VERTICCaretPositionChanged
        paintView(false);
    }//GEN-LAST:event_VERTICCaretPositionChanged

    private void selectLayer(){
        cLayer = LAYERS.getSelectedIndex();
        paintView(true);
        }

    private void LAYERSItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_LAYERSItemStateChanged
        selectLayer();
    }//GEN-LAST:event_LAYERSItemStateChanged

    private void HORIZStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_HORIZStateChanged
        paintView(false);
    }//GEN-LAST:event_HORIZStateChanged

    private void MASStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_MASStateChanged
        gPanel.mas = MAS.getValue();
        paintView(false);        
    }//GEN-LAST:event_MASStateChanged

    @Override
    public void paint(Graphics g){
        super.paint(g);
        paintView(false);      // НЕ РУКАМИ, а по событию
        }
    
    private void mouseShift(java.awt.event.MouseEvent evt){
        int xfin = evt.getX();
        int yfin = evt.getY();
        if (xfin == mouseX && yfin == mouseY)
            return;
        gPanel.x0 += gPanel.dxy * (mouseX - xfin);
        HORIZ.setValue((int)(-gPanel.x0*100));
        gPanel.y0 += gPanel.dxy * (mouseY - yfin);
        VERTIC.setValue((int)(-gPanel.y0*100));
        paintView(false);
        mouseX = xfin;
        mouseY = yfin;
        }
    
    private void NEXTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NEXTActionPerformed
        toNext();
    }//GEN-LAST:event_NEXTActionPerformed

    private void toPrev(){
        if (cLayer==0)
            return;
        cLayer--;
        LAYERS.select(cLayer);
        selectedConturLine=null;
        paintView(true);
        }

    private void toNext(){
        if (cLayer==lSize-1)
            return;
        cLayer++;
        LAYERS.select(cLayer);
        selectedConturLine=null;
        paintView(true);
        }

    private void PREVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PREVActionPerformed
        toPrev();
    }//GEN-LAST:event_PREVActionPerformed

    private void selectMode(boolean repaint){
        int oldMode = mode;
        if (mode<3) lastLayer = cLayer;         // Последний слой в первых 3 категориях
        mode = MODE.getSelectedIndex();
        if (mode!=2)
            OnlyLoop.setSelected(false);
        if (oldMode>=3 || mode>=3)              // Если не происходит смены первых трех
            setLayers();
        onlyLoopMode(repaint);
        if (oldMode>=3 && mode<3){
            selectLayer();
            LAYERS.select(lastLayer);
            }
        boolean vv = mode<3;
        B2.setVisible(vv);
        B3.setVisible(vv);
        B4.setVisible(vv);
        B5.setVisible(vv);
        B6.setVisible(vv);
        B7.setVisible(vv);
        boolean loops = mode==2;
        Points0.setVisible(loops);
        Points2.setVisible(loops);
        Points3.setVisible(loops);
        OnlyLoop.setVisible(loops);
        LineRemove.setVisible(mode==0 || mode==1);
        LineInsert.setVisible(mode==0 || mode==1);
        ShowPrint.setVisible(mode==0);
        selectedConturLine=null;
        one=null;
        two=null;
        }

    private void MODEItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_MODEItemStateChanged
        selectMode(true);
        paintView(true);
    }//GEN-LAST:event_MODEItemStateChanged

    private void Points0ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_Points0ItemStateChanged
        selectMode(true);
        paintView(true);
    }//GEN-LAST:event_Points0ItemStateChanged

    private void Points2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_Points2ItemStateChanged
        selectMode(true);
        paintView(true);
    }//GEN-LAST:event_Points2ItemStateChanged

    private void Points3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_Points3ItemStateChanged
        selectMode(true);
    }//GEN-LAST:event_Points3ItemStateChanged

    private void OnlyLoopItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_OnlyLoopItemStateChanged
        selectMode(true);
    }//GEN-LAST:event_OnlyLoopItemStateChanged

    private void LoopPlusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoopPlusActionPerformed
        if (cLoop == layer.loops().size()-1)
            return;
        cLoop++;
        LoopList.select(cLoop);
        setDrawNoSelected();        
        paintView(false);        
    }//GEN-LAST:event_LoopPlusActionPerformed

    private void LoopMinusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoopMinusActionPerformed
        if (cLoop == 0 || layer.loops().size()==0)
            return;
        cLoop--;
        LoopList.select(cLoop);
        setDrawNoSelected();
        paintView(false);        

    }//GEN-LAST:event_LoopMinusActionPerformed

    private void LoopListItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_LoopListItemStateChanged
        cLoop = LoopList.getSelectedIndex();
        paintView(false); 
    }//GEN-LAST:event_LoopListItemStateChanged

    private void LoopListCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_LoopListCaretPositionChanged
        cLoop = LoopList.getSelectedIndex();
        paintView(false); 
    }//GEN-LAST:event_LoopListCaretPositionChanged

    private void reSlice(boolean full){
        if (!test1()) return;
        SliceLayer layer = data.get(cLayer);
        Settings filling = layer.sliceSettings();
        boolean copy = filling == null;
        final Settings ss = copy ? ws().local() : filling;
        new M3DReSlice("Повторный слайсинг",ss,true,(set)->{
           M3DOperations oper = new M3DOperations(notify);
           ViewAdapter adapter = new ViewAdapter(null);
           SliceLayer lr = data.get(cLayer);
           SliceParams par = new SliceParams(full ? 2 : 3, lr,cLayer,-1);
           oper.reSliceLayer(par,data,adapter,set!=null ? set : ss);
           lr.setModified();
           dataChanged();  
           if (set!=null)
               data.get(cLayer).sliceSettings(set);
           setLayersNoMove();
           paintView(true);        
           notify.info("Операция завершена");     
           },notify).setVisible(true);        }
    
    private void DeleteLoopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteLoopActionPerformed
        if (!testOnlyLoopMode()) return;
        new OK(getBounds(),"Удалить контур", ()->{
            layer.loops().remove(cLoop);
            layer.refreshLinesFromLoops();
            ws().dataChanged();
            ws().fileStateChanged();              
            paintView(true);             
            });        
    }//GEN-LAST:event_DeleteLoopActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        onClose();
    }//GEN-LAST:event_formWindowClosing

    private void LoopCalcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoopCalcActionPerformed
        STLLoop loop = layer.loops().get(cLoop);
        StatInfo stat = loop.calcCurvature();
        String ss = "Кривизна контура "+stat.toString();
        notify.info(ss);
        MES.setText(ss);
    }//GEN-LAST:event_LoopCalcActionPerformed

    private void B3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B3ActionPerformed
        if (!test1()) return;
        try {
            double z = Double.parseDouble(B5.getText());
            Settings set = WorkSpace.ws().local();
            double z0 = set.local.ZStart.getVal();
            double vStep = set.local.VerticalStep.getVal();
            int lNum = (int)((z-z0+vStep/2)/vStep);
            final double z2 = z;
            new M3DReSlice("Добавить слой",ws().local(),true,(fil)->{
                M3DOperations oper = new M3DOperations(notify);
                ViewAdapter adapter = new ViewAdapter(null);
                SliceParams par = new SliceParams(lNum,z2);
                SliceRezult rez = oper.reSliceLayer(par, data,adapter,fil !=null ? fil : ws().local());
                if (fil!=null)
                   data.get(cLayer).sliceSettings(fil);
                dataChanged();                   
                setLayers();
                cLayer = rez.layerIdx();
                LAYERS.select(cLayer);
                paintView(true);        
                notify.info("Операция завершена");
                },notify).setVisible(true);
            } catch (Exception ee){ notify.notify(Values.error, ee.toString());}
    }//GEN-LAST:event_B3ActionPerformed

    private boolean test1(){
        if (cLayer == -1){
            notify.log("Слой не выбран");
            return false;
            }
        if (mode>=3){
            notify.log("Операции со слоями в первых 3 режимах просмотра");
            return false;
            }
        return true;
        }
    
    private void B2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B2ActionPerformed
        if (!test1()) return;
        new OK(getBounds(),"Удалить слой", ()->{
            int cc = cLayer;
            data.remove(cLayer);
            dataChanged(); 
            setLayers();
            if (cc == lSize && cc!=0) cc--;
            cLayer = cc;
            LAYERS.select(cLayer);
            paintView(true);            
            });
    }//GEN-LAST:event_B2ActionPerformed

    private void dataChanged(){
        ws().dataChanged();
        ws().fileStateChanged();              
        }
    
    private void LineRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LineRemoveActionPerformed
        if (selectedConturLine==null)
            return;
        new OK(20,300,getBounds(),"Удалить линию", ()->{
            STLLineGroup gg = mode==0 ? layer.segments() : layer.lines();
            gg.remove(selectedConturLine);
            dataChanged();
            setDrawNoSelected();
            paintView(false);            
            });          
    }//GEN-LAST:event_LineRemoveActionPerformed

    private void B4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B4ActionPerformed
        if (!test1()) return;
        SliceLayer layer = data.get(cLayer);
        Settings print = layer.printSettings();
        boolean copy = print == null;
        Settings ss = copy ? ws().local() : print;
        new LayerPrintSettings(ss,copy,notify,(set)->{
            layer.printSettings(set);
            setLayersNoMove();
            ws().sendEvent(Events.Settings);
            dataChanged();   
            }).setVisible(true);

    }//GEN-LAST:event_B4ActionPerformed

    private void B6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B6ActionPerformed
        try {
            STLModel3D model = new STLModel3D();
            String fname = this.getInputFileName("Контур в STL", "stl",false);
            model.load(fname, notify);
            model.shiftToCenter();
            //model.rotate(2, new MyAngle(Math.PI), notify);
            model.invertY();
            STLLoopGenerator slicer = new STLLoopGenerator( model.triangles(), 0.0, Values.PointDiffenerce, notify);
            slicer.createTriangleLoops();      
            notify.log("Контуров "+slicer.loops().size());
            if (layer!=null){
                for(STLLoop loop : slicer.loops()){
                    layer.loops().add(loop);
                    }
                }
            paintView(true);
            } catch (Exception ee){ notify.notify(Values.error, ee.getMessage());}
    }//GEN-LAST:event_B6ActionPerformed

    private void B7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B7ActionPerformed
        reSlice(true);
    }//GEN-LAST:event_B7ActionPerformed

    private void LineInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LineInsertActionPerformed
        if (layer==null) return;
        if (one==null || two==null) {
            notify.info("Необходимы 2 точки");
            return;
            }
        if (mode==1)
            layer.lines().add(new STLLine(one,two));
        if (mode==0)
            layer.segments().add(new STLLine(one,two));
        one=null;
        two=null;
        paintView(false);
    }//GEN-LAST:event_LineInsertActionPerformed

    private void GridItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_GridItemStateChanged
        paintView(false);
    }//GEN-LAST:event_GridItemStateChanged

    private void PXKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PXKeyPressed
        if(evt.getKeyCode()!=10) return;
        if (one==null && two==null) return;
        I_STLPoint2D pp = two!=null ? two :one;
        try {
            double vv = Double.parseDouble(PX.getText());
            pp.x(vv);
            paintView(false);
            }catch(Exception ee){ notify.notify(Values.error,"Формат целого числа ????");}
    }//GEN-LAST:event_PXKeyPressed

    private void PYKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PYKeyPressed
        if(evt.getKeyCode()!=10) return;
        if (one==null && two==null) return;
        I_STLPoint2D pp = two!=null ? two :one;
        try {
            double vv = Double.parseDouble(PY.getText());
            pp.y(vv);
            paintView(false);
            }catch(Exception ee){ notify.notify(Values.error,"Формат целого числа ????");}
    }//GEN-LAST:event_PYKeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (data.isMerged()){
            notify.notify(Values.error,"Повторное слияние невозможно");
            return;
            }
        int gg = Integer.parseInt(GroupSize.getSelectedItem().toString());
        double sz = ws().model().getScaleXY();
        int nn = (int)(1/sz);
        if (gg > nn){
            notify.notify(Values.error,"Допустимое слияние "+nn + "x"+nn);
            return;
            }
        new OK(getBounds(),"Слить "+gg+" :1", ()->{
            data.mergeLayers(gg);
            setLayers();
            });

    }//GEN-LAST:event_jButton1ActionPerformed

    private void ShowPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShowPrintActionPerformed
        if (!showRun){
            ShowPrint.setText("Прервать");
            showDelay=10;
            new Thread(()->{
                paintViewMode0();
                Utils.runAfterDelayMS(10, ()->{
                    ShowPrint.setText("Развертка");
                    showRun=false;
                    });
                }).start();
            }
        else{
            ShowPrint.setText("Развертка");
            }
        showRun=!showRun;
    }//GEN-LAST:event_ShowPrintActionPerformed

    private void ShowDelayStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ShowDelayStateChanged
        showDelay = ShowDelay.getValue();
    }//GEN-LAST:event_ShowDelayStateChanged

    @Override
    public void onEvent(int code,boolean on, int value, String name) {
        super.onEvent(code,on,value,name);
        if (code== Events.Layer){
            MODE.select(0);
            selectMode(false);
            LAYERS.select(value);
            selectLayer();
            }
        if (code== Events.Close){
            onClose();                      // TODO - сохранить файл ????
            }
        if (code==Events.NewData){
            data = ws().data();
            setLayers();
            selectMode(false);
            paintView(true);
            }
        if (code==Events.LinePrint){
            if (mode==0 && cLayer!=-1){
                gPanel.drawLine(Color.red,layer.segments().lines().get(value));
                }
            }
        }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton B2;
    private javax.swing.JButton B3;
    private javax.swing.JButton B4;
    private javax.swing.JTextField B5;
    private javax.swing.JButton B6;
    private javax.swing.JButton B7;
    private javax.swing.JButton DeleteLoop;
    private javax.swing.JCheckBox Grid;
    private javax.swing.JComboBox<String> GroupSize;
    private javax.swing.JSlider HORIZ;
    private java.awt.Choice LAYERS;
    private javax.swing.JButton LineInsert;
    private javax.swing.JLabel LineLabel;
    private javax.swing.JLabel LineLabel1;
    private javax.swing.JButton LineRemove;
    private javax.swing.JButton LoopCalc;
    private java.awt.Choice LoopList;
    private javax.swing.JButton LoopMinus;
    private javax.swing.JButton LoopPlus;
    private javax.swing.JSlider MAS;
    private javax.swing.JTextField MES;
    private java.awt.Choice MODE;
    private javax.swing.JTextField MX;
    private javax.swing.JTextField MY;
    private javax.swing.JCheckBox MoveToNearest;
    private javax.swing.JButton NEXT;
    private javax.swing.JCheckBox OnlyLoop;
    private javax.swing.JButton PREV;
    private javax.swing.JTextField PX;
    private javax.swing.JTextField PY;
    private javax.swing.JCheckBox Points0;
    private javax.swing.JCheckBox Points2;
    private javax.swing.JCheckBox Points3;
    private javax.swing.JSlider ShowDelay;
    private javax.swing.JToggleButton ShowPrint;
    private javax.swing.JSlider VERTIC;
    private javax.swing.JLabel X;
    private javax.swing.JLabel X1;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    // End of variables declaration//GEN-END:variables
}
