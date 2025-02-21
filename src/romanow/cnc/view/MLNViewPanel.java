/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package romanow.cnc.view;

import romanow.cnc.Values;
import romanow.cnc.graph.GraphPanel;
import romanow.cnc.graph.I_Mouse;
import romanow.cnc.m3d.M3DOperations;
import romanow.cnc.m3d.M3DReSlice;
import romanow.cnc.m3d.OK;
import romanow.cnc.m3d.ViewAdapter;
import romanow.cnc.settings.Settings;
import romanow.cnc.settings.WorkSpace;
import romanow.cnc.settingsView.StatisticPanel;
import romanow.cnc.slicer.*;
import romanow.cnc.stl.*;
import romanow.cnc.utils.Events;
import romanow.cnc.utils.I_Notify;
import romanow.cnc.utils.Utils;
import romanow.cnc.view.design.JCheckBoxButton;
import romanow.cnc.view.panels.DigitPanel;
import romanow.cnc.view.panels.I_RealValue;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 *
 * @author Admin
 */
public class MLNViewPanel extends BasePanel {
    private final int  masMax=20;
    private SliceData data;
    private int mode=0;
    private int cLayer=-1;
    private double dz;
    private int lIndexes[];
    private int lSize=0;
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
    private WorkSpace ws;
    private JCheckBoxButton grid;
    private JCheckBoxButton points0;
    private JCheckBoxButton points2;
    private JCheckBoxButton points3;
    private JCheckBoxButton onlyLoop;
    private JCheckBoxButton moveToNearest;
    private JCheckBoxButton showPrint;
    /**
     * Creates new form MLNViewPanel
     */

    @Override
    public boolean isSelectedMode(){            // Промотр 3D для любой загруженной модели
        return WorkSpace.ws().slicePresent();
        }

    public MLNViewPanel(CNCViewer base) {
        super(base);
        initComponents();
        ws = WorkSpace.ws();
        double scaleY = ws.getScaleX();
        double scaleX = ws.getScaleY();
        grid = new JCheckBoxButton(GridButton);
        grid.setSelected(true);
        points0 = new JCheckBoxButton(Points0Button);
        points0.setSelected(true);
        points2 = new JCheckBoxButton(Points2Button);
        points2.setSelected(true);
        points3 = new JCheckBoxButton(Points3Button);
        points3.setSelected(true);
        onlyLoop = new JCheckBoxButton(OnlyLoopButton);
        onlyLoop.setSelected(true);
        moveToNearest = new JCheckBoxButton(MoveToNearestButton);
        moveToNearest.setSelected(true);
        showPrint = new JCheckBoxButton(ShowPrint,"animate-48","animate-run-48");
        showPrint.setSelected(false);
        setComponentsScale();
        statView = new StatisticPanel();
        if (ws.global().fullScreen)
            statView.setBounds((int)(scaleX*980), (int)(scaleY*10), (int)(scaleX*250), (int)(scaleY*220));
        else
            statView.setBounds(890, 10, 250, 220);
        BasePanel.setComponentsScale(statView);
        add(statView);
        gPanel = new GraphPanel(mBack);
        if (ws.global().fullScreen)
            gPanel.setBounds((int)(scaleX*250), (int)(scaleY*10), (int)(ws.getScaleMin()*Values.MLNPanelSize));
        else
            gPanel.setBounds(210,10,Values.MLNPanelSize);
        BasePanel.setComponentsScale(gPanel);
        add(gPanel);
        MODE.addItem("Растр");
        MODE.addItem("Сечение");
        MODE.addItem("Контуры");
        MODE.addItem("Контур замкнут");
        MODE.addItem("Нечетные точки");
        MODE.addItem("Нечет исправлен");
        MAS.setValue(GraphPanel.MASOffset);
        }

    @Override
    public void onActivate() {
        gPanel.setPaintParams(HORIZ,VERTIC);
        dz = ws.local().model.VerticalStep.getVal();
        data = ws.data();
        setLayers();
        selectMode(false);
        paintView(true);
        }
    @Override
    public void onDeactivate() {
        }

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
    //------------------------------------------------------------------------------------------------------------------
    private boolean testOnlyLoopMode(){
        if (layer==null)
            return false;
        if (mode==2 && onlyLoop.isSelected())
            return true;
        return false;
    }
    private void setDrawNoSelected(){
        selectedConturLine=null;
        one = null;
        two = null;
    }
    private I_STLPoint2D nearest(I_STLPoint2D pp){
        if (!moveToNearest.isSelected()) return pp;
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
            onlyLoop.setSelected(false);
        LoopPlus.setVisible(onlyLoop.isSelected());
        LoopMinus.setVisible(onlyLoop.isSelected());
        LoopList2.setVisible(onlyLoop.isSelected());
        LoopCalc.setVisible(onlyLoop.isSelected());
        DeleteLoop.setVisible(onlyLoop.isSelected());
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
        LAYERS.setSelectedIndex(cLayer);
    }
    private void setLayers(){
        lIndexes = new int[data.size()];
        gPanel.setGraphics();
        gPanel.clear();
        mode = MODE.getSelectedIndex();
        LAYERS.removeAll();
        double z0 = ws.local().model.ZStart.getVal();
        int k=0;
        for(int i=0;i<data.size();i++){
            SliceLayer lr = data.get(i);
            if (mode<3 || lr.hasErrors(mode-3)){
                lIndexes[k]=i;
                LAYERS.addItem(lr.label());
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
    private void paintViewMode0(){   //Запуск в потоке
        paintViewOrig(false);
        for(STLLine line :layer.segments().lines()){
            if (!showPrint.isSelected()) {
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
            LoopList2.removeAll();
            for (int i=0;i<layer.loops().size();i++)
                LoopList2.addItem(""+(i+1));
            cLoop=0;
            }
        gPanel.setPaintParams(HORIZ,VERTIC);
        if (grid.isSelected())
            gPanel.paintGrid(gridColor);
        gPanel.setColor(Color.black);
        //MES.setText("z="+String .format("%5.2f",layer.z()*)+" длина="+layer.rezult().printLength()+" время="+layer.rezult().printTime());
        if (mode<=2)
            statView.setValues(layer.rezult());
        if (mode==2) {
            if (onlyLoop.isSelected()) {
                if (layer.loops().size()!=0)
                    paintLoop(layer.loops().get(cLoop));
            } else {
                for (STLLoop loop : layer.loops()) {
                    int loopType = loop.loopLineMode();
                    boolean viz = loopType == 0 && points0.isSelected() || loopType == 2 && points2.isSelected() || loopType == 3 && points3.isSelected();
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
    
    private void toPrev(){
        if (cLayer==0)
            return;
        cLayer--;
        LAYERS.setSelectedIndex(cLayer);
        selectedConturLine=null;
        paintView(true);
        }

    private void toNext(){
        if (cLayer==lSize-1)
            return;
        cLayer++;
        LAYERS.setSelectedIndex(cLayer);
        selectedConturLine=null;
        paintView(true);
        }
    private void selectMode(boolean repaint){
        int oldMode = mode;
        if (mode<3) lastLayer = cLayer;         // Последний слой в первых 3 категориях
        mode = MODE.getSelectedIndex();
        if (mode!=2)
            onlyLoop.setSelected(false);
        if (oldMode>=3 || mode>=3)              // Если не происходит смены первых трех
            setLayers();
        onlyLoopMode(repaint);
        if (oldMode>=3 && mode<3){
            selectLayer();
            LAYERS.setSelectedIndex(lastLayer);
        }
        boolean vv = mode<3;
        B2.setVisible(vv);
        B3.setVisible(vv);
        B5.setVisible(vv);
        B6.setVisible(vv);
        B7.setVisible(vv);
        boolean loops = mode==2;
        points0.setVisible(loops);
        points2.setVisible(loops);
        points3.setVisible(loops);
        onlyLoop.setVisible(loops);
        LineRemove.setVisible(mode==0 || mode==1);
        LineInsert.setVisible(mode==0 || mode==1);
        ShowPrint.setVisible(mode==0);
        selectedConturLine=null;
        one=null;
        two=null;
        }

    private void selectLayer(){
        cLayer = LAYERS.getSelectedIndex();
        paintView(true);
        }
    private void reSlice(boolean full){
        if (!test1()) return;
        SliceLayer layer = data.get(cLayer);
        Settings filling = layer.sliceSettings();
        boolean copy = filling == null;
        final Settings ss = copy ? ws.local() : filling;
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

    private void dataChanged(){
        ws.dataChanged();
        ws.fileStateChanged();
        }      
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public void onEvent(int code,int value, long par2, String name, Object oo) {
        if (!isSelected())
            return;
        if (code== Events.Layer){
            MODE.setSelectedIndex(0);
            selectMode(false);
            LAYERS.setSelectedIndex(value);
            selectLayer();
            }
        if (code== Events.Close){
            onClose();                      // TODO - сохранить файл ????
            }
        if (code==Events.NewData){
            data = ws.data();
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
    //-------------------------------------------------------------------------------------------------------------------
    @Override
    public String getName() {
        return "Слайсинг"; }

    @Override
    public int modeMask() {
        return Values.PanelMLN; }

    @Override
    public boolean modeEnabled() {
        return true; }


    @Override
    public void onClose() {
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
        MAS = new javax.swing.JSlider();
        PREV = new javax.swing.JButton();
        NEXT = new javax.swing.JButton();
        MES = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        LoopPlus = new javax.swing.JButton();
        LoopMinus = new javax.swing.JButton();
        DeleteLoop = new javax.swing.JButton();
        LoopCalc = new javax.swing.JButton();
        B5 = new javax.swing.JTextField();
        B3 = new javax.swing.JButton();
        B2 = new javax.swing.JButton();
        LineRemove = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        B6 = new javax.swing.JButton();
        B7 = new javax.swing.JButton();
        LineLabel1 = new javax.swing.JLabel();
        LineInsert = new javax.swing.JButton();
        MY = new javax.swing.JTextField();
        PY = new javax.swing.JTextField();
        X = new javax.swing.JLabel();
        X1 = new javax.swing.JLabel();
        MX = new javax.swing.JTextField();
        PX = new javax.swing.JTextField();
        GroupSize = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        MergeLayers = new javax.swing.JButton();
        ShowDelay = new javax.swing.JSlider();
        LAYERS = new javax.swing.JComboBox<>();
        MODE = new javax.swing.JComboBox<>();
        LoopList2 = new javax.swing.JComboBox<>();
        X2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        X3 = new javax.swing.JLabel();
        ShowPrint = new javax.swing.JButton();
        Points2Button = new javax.swing.JButton();
        Z0_1 = new javax.swing.JLabel();
        GridButton = new javax.swing.JButton();
        Z0_2 = new javax.swing.JLabel();
        Points3Button = new javax.swing.JButton();
        Z0_3 = new javax.swing.JLabel();
        Points0Button = new javax.swing.JButton();
        Z0_4 = new javax.swing.JLabel();
        OnlyLoopButton = new javax.swing.JButton();
        Z0_5 = new javax.swing.JLabel();
        MoveToNearestButton = new javax.swing.JButton();
        Z0_6 = new javax.swing.JLabel();
        Z0_7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        LineLabel2 = new javax.swing.JLabel();

        setLayout(null);

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
        add(HORIZ);
        HORIZ.setBounds(910, 430, 180, 20);

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
        add(VERTIC);
        VERTIC.setBounds(180, 360, 20, 270);

        MAS.setMaximum(50);
        MAS.setMinimum(1);
        MAS.setValue(1);
        MAS.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                MASStateChanged(evt);
            }
        });
        add(MAS);
        MAS.setBounds(910, 470, 180, 20);

        PREV.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        PREV.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/icon2/icons8-left-48.png"))); // NOI18N
        PREV.setBorderPainted(false);
        PREV.setContentAreaFilled(false);
        PREV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PREVActionPerformed(evt);
            }
        });
        add(PREV);
        PREV.setBounds(10, 160, 50, 50);

        NEXT.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        NEXT.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/icon2/icons8-right-48.png"))); // NOI18N
        NEXT.setBorderPainted(false);
        NEXT.setContentAreaFilled(false);
        NEXT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NEXTActionPerformed(evt);
            }
        });
        add(NEXT);
        NEXT.setBounds(140, 160, 50, 50);

        MES.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        MES.setEnabled(false);
        add(MES);
        MES.setBounds(10, 730, 890, 30);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Скорость");
        add(jLabel1);
        jLabel1.setBounds(1000, 370, 110, 20);

        LoopPlus.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        LoopPlus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/right.png"))); // NOI18N
        LoopPlus.setContentAreaFilled(false);
        LoopPlus.setDefaultCapable(false);
        LoopPlus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoopPlusActionPerformed(evt);
            }
        });
        add(LoopPlus);
        LoopPlus.setBounds(130, 620, 50, 50);

        LoopMinus.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        LoopMinus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/left.png"))); // NOI18N
        LoopMinus.setBorderPainted(false);
        LoopMinus.setContentAreaFilled(false);
        LoopMinus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoopMinusActionPerformed(evt);
            }
        });
        add(LoopMinus);
        LoopMinus.setBounds(10, 620, 50, 50);

        DeleteLoop.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        DeleteLoop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/icon2/icons8-remove-48.png"))); // NOI18N
        DeleteLoop.setBorderPainted(false);
        DeleteLoop.setContentAreaFilled(false);
        DeleteLoop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteLoopActionPerformed(evt);
            }
        });
        add(DeleteLoop);
        DeleteLoop.setBounds(70, 670, 50, 50);

        LoopCalc.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        LoopCalc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/icon2/icons8-question-48.png"))); // NOI18N
        LoopCalc.setBorderPainted(false);
        LoopCalc.setContentAreaFilled(false);
        LoopCalc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoopCalcActionPerformed(evt);
            }
        });
        add(LoopCalc);
        LoopCalc.setBounds(70, 620, 50, 50);

        B5.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        B5.setText("0.0");
        B5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        B5.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        B5.setEnabled(false);
        B5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B5ActionPerformed(evt);
            }
        });
        add(B5);
        B5.setBounds(70, 280, 70, 40);

        B3.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        B3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/icon2/icons8-add-48.png"))); // NOI18N
        B3.setBorderPainted(false);
        B3.setContentAreaFilled(false);
        B3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B3ActionPerformed(evt);
            }
        });
        add(B3);
        B3.setBounds(10, 270, 50, 50);

        B2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        B2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/icon2/icons8-remove-48.png"))); // NOI18N
        B2.setBorderPainted(false);
        B2.setContentAreaFilled(false);
        B2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B2ActionPerformed(evt);
            }
        });
        add(B2);
        B2.setBounds(150, 270, 50, 50);

        LineRemove.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        LineRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/icon2/icons8-remove-48.png"))); // NOI18N
        LineRemove.setBorderPainted(false);
        LineRemove.setContentAreaFilled(false);
        LineRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LineRemoveActionPerformed(evt);
            }
        });
        add(LineRemove);
        LineRemove.setBounds(1030, 670, 50, 50);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel2.setText("Слой");
        add(jLabel2);
        jLabel2.setBounds(10, 50, 150, 29);
        add(jSeparator1);
        jSeparator1.setBounds(10, 493, 180, 0);
        add(jSeparator3);
        jSeparator3.setBounds(10, 550, 210, 0);

        B6.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        B6.setText("Загрузить сечение");
        B6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B6ActionPerformed(evt);
            }
        });
        add(B6);
        B6.setBounds(910, 730, 170, 30);

        B7.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        B7.setText("Слайсинг");
        B7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B7ActionPerformed(evt);
            }
        });
        add(B7);
        B7.setBounds(10, 220, 190, 40);

        LineLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        LineLabel1.setText("Линия");
        add(LineLabel1);
        LineLabel1.setBounds(910, 680, 70, 22);

        LineInsert.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        LineInsert.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/icon2/icons8-add-48.png"))); // NOI18N
        LineInsert.setBorderPainted(false);
        LineInsert.setContentAreaFilled(false);
        LineInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LineInsertActionPerformed(evt);
            }
        });
        add(LineInsert);
        LineInsert.setBounds(980, 670, 50, 50);

        MY.setEditable(false);
        MY.setBackground(new java.awt.Color(200, 200, 200));
        MY.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        MY.setText("0");
        add(MY);
        MY.setBounds(1010, 570, 70, 40);

        PY.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        PY.setText("0");
        PY.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        PY.setEnabled(false);
        PY.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PYMouseClicked(evt);
            }
        });
        add(PY);
        PY.setBounds(930, 570, 70, 40);

        X.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        X.setText("Y");
        add(X);
        X.setBounds(180, 330, 20, 30);

        X1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        X1.setText("X");
        add(X1);
        X1.setBounds(1050, 410, 20, 20);

        MX.setEditable(false);
        MX.setBackground(new java.awt.Color(200, 200, 200));
        MX.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        MX.setText("0");
        add(MX);
        MX.setBounds(1010, 525, 70, 38);

        PX.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        PX.setText("0");
        PX.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        PX.setEnabled(false);
        PX.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PXMouseClicked(evt);
            }
        });
        add(PX);
        PX.setBounds(930, 525, 70, 40);

        GroupSize.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        GroupSize.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        GroupSize.setMinimumSize(new java.awt.Dimension(37, 25));
        GroupSize.setPreferredSize(new java.awt.Dimension(37, 25));
        add(GroupSize);
        GroupSize.setBounds(1010, 240, 70, 40);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel3.setText("NxN : 1");
        add(jLabel3);
        jLabel3.setBounds(910, 250, 100, 30);

        MergeLayers.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        MergeLayers.setText("Слить слои");
        MergeLayers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MergeLayersActionPerformed(evt);
            }
        });
        add(MergeLayers);
        MergeLayers.setBounds(910, 295, 170, 40);

        ShowDelay.setMajorTickSpacing(1);
        ShowDelay.setValue(10);
        ShowDelay.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ShowDelayStateChanged(evt);
            }
        });
        add(ShowDelay);
        ShowDelay.setBounds(900, 390, 190, 20);

        LAYERS.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        LAYERS.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                LAYERSItemStateChanged(evt);
            }
        });
        add(LAYERS);
        LAYERS.setBounds(10, 80, 190, 40);

        MODE.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        MODE.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                MODEItemStateChanged(evt);
            }
        });
        add(MODE);
        MODE.setBounds(10, 10, 190, 40);

        LoopList2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        add(LoopList2);
        LoopList2.setBounds(10, 580, 170, 30);

        X2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        X2.setText("X");
        add(X2);
        X2.setBounds(910, 540, 20, 20);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel4.setText("Контуры");
        add(jLabel4);
        jLabel4.setBounds(10, 350, 110, 30);

        X3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        X3.setText("Y");
        add(X3);
        X3.setBounds(910, 580, 20, 20);

        ShowPrint.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        ShowPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShowPrintActionPerformed(evt);
            }
        });
        add(ShowPrint);
        ShowPrint.setBounds(910, 340, 50, 50);

        Points2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Points2ButtonActionPerformed(evt);
            }
        });
        add(Points2Button);
        Points2Button.setBounds(120, 380, 50, 40);

        Z0_1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        Z0_1.setText("Сетка (мм)");
        add(Z0_1);
        Z0_1.setBounds(50, 120, 120, 32);

        GridButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GridButtonActionPerformed(evt);
            }
        });
        add(GridButton);
        GridButton.setBounds(80, 160, 50, 40);

        Z0_2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Z0_2.setText("По контурам");
        add(Z0_2);
        Z0_2.setBounds(10, 540, 120, 25);

        Points3Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Points3ButtonActionPerformed(evt);
            }
        });
        add(Points3Button);
        Points3Button.setBounds(120, 430, 50, 40);

        Z0_3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Z0_3.setText("Сторона");
        add(Z0_3);
        Z0_3.setBounds(10, 390, 110, 25);

        Points0Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Points0ButtonActionPerformed(evt);
            }
        });
        add(Points0Button);
        Points0Button.setBounds(120, 480, 50, 40);

        Z0_4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Z0_4.setText("Плоскость");
        add(Z0_4);
        Z0_4.setBounds(10, 440, 100, 25);

        OnlyLoopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OnlyLoopButtonActionPerformed(evt);
            }
        });
        add(OnlyLoopButton);
        OnlyLoopButton.setBounds(120, 530, 50, 40);

        Z0_5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Z0_5.setText("к ближайшей");
        add(Z0_5);
        Z0_5.setBounds(910, 640, 120, 25);

        MoveToNearestButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MoveToNearestButtonActionPerformed(evt);
            }
        });
        add(MoveToNearestButton);
        MoveToNearestButton.setBounds(1030, 620, 50, 40);

        Z0_6.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Z0_6.setText("Сечение");
        add(Z0_6);
        Z0_6.setBounds(10, 490, 100, 25);

        Z0_7.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Z0_7.setText("Привязка");
        add(Z0_7);
        Z0_7.setBounds(910, 620, 90, 25);

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Масштаб");
        add(jLabel5);
        jLabel5.setBounds(1000, 450, 120, 20);

        LineLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        LineLabel2.setText("Редактировать");
        add(LineLabel2);
        LineLabel2.setBounds(910, 490, 140, 22);
    }// </editor-fold>//GEN-END:initComponents

    private void HORIZStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_HORIZStateChanged
        paintView(false);
    }//GEN-LAST:event_HORIZStateChanged

    private void HORIZCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_HORIZCaretPositionChanged
        paintView(false);
    }//GEN-LAST:event_HORIZCaretPositionChanged

    private void VERTICStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_VERTICStateChanged
        paintView(false);
    }//GEN-LAST:event_VERTICStateChanged

    private void VERTICCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_VERTICCaretPositionChanged
        paintView(false);
    }//GEN-LAST:event_VERTICCaretPositionChanged

    private void MASStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_MASStateChanged
        gPanel.mas = MAS.getValue();
        paintView(false);
    }//GEN-LAST:event_MASStateChanged

    private void PREVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PREVActionPerformed
        toPrev();
    }//GEN-LAST:event_PREVActionPerformed

    private void NEXTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NEXTActionPerformed
        toNext();
    }//GEN-LAST:event_NEXTActionPerformed

    private void LoopPlusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoopPlusActionPerformed
        if (cLoop == layer.loops().size()-1)
        return;
        cLoop++;
        LoopList2.setSelectedIndex(cLoop);
        setDrawNoSelected();
        paintView(false);
    }//GEN-LAST:event_LoopPlusActionPerformed

    private void LoopMinusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoopMinusActionPerformed
        if (cLoop == 0 || layer.loops().size()==0)
        return;
        cLoop--;
        LoopList2.setSelectedIndex(cLoop);
        setDrawNoSelected();
        paintView(false);
    }//GEN-LAST:event_LoopMinusActionPerformed

    private void DeleteLoopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteLoopActionPerformed
        if (!testOnlyLoopMode()) return;
        new OK(getBounds(),"Удалить контур", ()->{
            layer.loops().remove(cLoop);
            layer.refreshLinesFromLoops();
            ws.dataChanged();
            ws.fileStateChanged();
            paintView(true);
        });
    }//GEN-LAST:event_DeleteLoopActionPerformed

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
            Settings set = ws.local();
            double z0 = set.model.ZStart.getVal();
            double vStep = set.model.VerticalStep.getVal();
            int lNum = (int)((z-z0+vStep/2)/vStep);
            final double z2 = z;
            new M3DReSlice("Добавить слой",ws.local(),true,(fil)->{
                M3DOperations oper = new M3DOperations(notify);
                ViewAdapter adapter = new ViewAdapter(null);
                SliceParams par = new SliceParams(lNum,z2);
                SliceRezult rez = oper.reSliceLayer(par, data,adapter,fil !=null ? fil : ws.local());
                if (fil!=null)
                data.get(cLayer).sliceSettings(fil);
                dataChanged();
                setLayers();
                cLayer = rez.layerIdx();
                LAYERS.setSelectedIndex(cLayer);
                paintView(true);
                notify.info("Операция завершена");
            },notify).setVisible(true);
        } catch (Exception ee){ notify.notify(Values.error, ee.toString());}
    }//GEN-LAST:event_B3ActionPerformed

    private void B2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B2ActionPerformed
        if (!test1()) return;
        new OK(getBounds(),"Удалить слой", ()->{
            int cc = cLayer;
            data.remove(cLayer);
            dataChanged();
            setLayers();
            if (cc == lSize && cc!=0) cc--;
            cLayer = cc;
            LAYERS.setSelectedIndex(cLayer);
            paintView(true);
        });
    }//GEN-LAST:event_B2ActionPerformed

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

    private void B6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B6ActionPerformed
        try {
            STLModel3D model = new STLModel3D();
            String fname = getBaseFrame().getInputFileName("Контур в STL", "stl",false);
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

    private void MergeLayersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MergeLayersActionPerformed
        if (data.isMerged()){
            notify.notify(Values.error,"Повторное слияние невозможно");
            return;
            }
        int gg = Integer.parseInt(GroupSize.getSelectedItem().toString());
        double sz = ws.model().getScaleXY();
        int nn = (int)(1/sz);
        if (gg > nn){
            notify.notify(Values.error,"Допустимое слияние "+nn + "x"+nn);
            return;
            }
        new OK(getBounds(),"Слить "+gg+" :1", ()->{
            data.mergeLayers(gg);
            setLayers();
            });
    }//GEN-LAST:event_MergeLayersActionPerformed

    private void ShowDelayStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ShowDelayStateChanged
        showDelay = ShowDelay.getValue();
    }//GEN-LAST:event_ShowDelayStateChanged

    private void MODEItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_MODEItemStateChanged
        selectMode(true);
        paintView(true);
    }//GEN-LAST:event_MODEItemStateChanged

    private void LAYERSItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_LAYERSItemStateChanged
        selectLayer();
    }//GEN-LAST:event_LAYERSItemStateChanged

    private void ShowPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShowPrintActionPerformed
        if (!showPrint.isSelected()){
            ShowPrint.setText("Прервать");
            showDelay=10;
            new Thread(()->{
                paintViewMode0();
                Utils.runAfterDelayMS(10, ()->{
                    ShowPrint.setText("Развертка");
                    showPrint.setSelected(false);
                    });
                }).start();
            }
        else{
            ShowPrint.setText("Развертка");
            }
        showPrint.itemStateChanged();
    }//GEN-LAST:event_ShowPrintActionPerformed

    private void Points2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Points2ButtonActionPerformed
        points2.itemStateChanged();
        paintView(false);
    }//GEN-LAST:event_Points2ButtonActionPerformed

    private void GridButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GridButtonActionPerformed
        grid.itemStateChanged();
        paintView(false);
    }//GEN-LAST:event_GridButtonActionPerformed

    private void Points3ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Points3ButtonActionPerformed
        points3.itemStateChanged();
        paintView(false);
    }//GEN-LAST:event_Points3ButtonActionPerformed

    private void Points0ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Points0ButtonActionPerformed
        points0.itemStateChanged();
        paintView(false);
    }//GEN-LAST:event_Points0ButtonActionPerformed

    private void OnlyLoopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OnlyLoopButtonActionPerformed
        onlyLoop.itemStateChanged();
        selectMode(true);
    }//GEN-LAST:event_OnlyLoopButtonActionPerformed

    private void MoveToNearestButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MoveToNearestButtonActionPerformed
        moveToNearest.itemStateChanged();
    }//GEN-LAST:event_MoveToNearestButtonActionPerformed

    private void PXMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PXMouseClicked
        if (one==null && two==null){
            WorkSpace.ws().popup("Не выбрана точка");
            return;
            }
        DigitPanel digit = new DigitPanel("Координата X",PX, new I_RealValue() {
            @Override
            public void onEvent(String value) {
                PX.setText(value);
                I_STLPoint2D pp = two!=null ? two :one;
                try {
                    double vv = Double.parseDouble(value);
                    pp.x(vv);
                    paintView(false);
                    } catch(Exception ee){ notify.notify(Values.error,"Формат вещественного числа ????");}
                }
            });
    }//GEN-LAST:event_PXMouseClicked

    private void PYMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PYMouseClicked
        if (one==null && two==null){
            WorkSpace.ws().popup("Не выбрана точка");
            return;
            }
        DigitPanel digit = new DigitPanel("Координата X",PY, new I_RealValue() {
            @Override
            public void onEvent(String value) {
                PY.setText(value);
                I_STLPoint2D pp = two!=null ? two :one;
                try {
                    double vv = Double.parseDouble(value);
                    pp.y(vv);
                    paintView(false);
                    } catch(Exception ee){ notify.notify(Values.error,"Формат вещественного числа ????");}
                }
            });
    }//GEN-LAST:event_PYMouseClicked

    private void B5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B5ActionPerformed
        DigitPanel digit = new DigitPanel("Z слоя", B5, false, new I_RealValue() {
            @Override
            public void onEvent(String value) {
                B5.setText(value);
                }
            });
    }//GEN-LAST:event_B5ActionPerformed

    @Override
    public void refresh() {
        }
        
    @Override
    public void shutDown() {
        }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton B2;
    private javax.swing.JButton B3;
    private javax.swing.JTextField B5;
    private javax.swing.JButton B6;
    private javax.swing.JButton B7;
    private javax.swing.JButton DeleteLoop;
    private javax.swing.JButton GridButton;
    private javax.swing.JComboBox<String> GroupSize;
    private javax.swing.JSlider HORIZ;
    private javax.swing.JComboBox<String> LAYERS;
    private javax.swing.JButton LineInsert;
    private javax.swing.JLabel LineLabel1;
    private javax.swing.JLabel LineLabel2;
    private javax.swing.JButton LineRemove;
    private javax.swing.JButton LoopCalc;
    private javax.swing.JComboBox<String> LoopList2;
    private javax.swing.JButton LoopMinus;
    private javax.swing.JButton LoopPlus;
    private javax.swing.JSlider MAS;
    private javax.swing.JTextField MES;
    private javax.swing.JComboBox<String> MODE;
    private javax.swing.JTextField MX;
    private javax.swing.JTextField MY;
    private javax.swing.JButton MergeLayers;
    private javax.swing.JButton MoveToNearestButton;
    private javax.swing.JButton NEXT;
    private javax.swing.JButton OnlyLoopButton;
    private javax.swing.JButton PREV;
    private javax.swing.JTextField PX;
    private javax.swing.JTextField PY;
    private javax.swing.JButton Points0Button;
    private javax.swing.JButton Points2Button;
    private javax.swing.JButton Points3Button;
    private javax.swing.JSlider ShowDelay;
    private javax.swing.JButton ShowPrint;
    private javax.swing.JSlider VERTIC;
    private javax.swing.JLabel X;
    private javax.swing.JLabel X1;
    private javax.swing.JLabel X2;
    private javax.swing.JLabel X3;
    private javax.swing.JLabel Z0_1;
    private javax.swing.JLabel Z0_2;
    private javax.swing.JLabel Z0_3;
    private javax.swing.JLabel Z0_4;
    private javax.swing.JLabel Z0_5;
    private javax.swing.JLabel Z0_6;
    private javax.swing.JLabel Z0_7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    // End of variables declaration//GEN-END:variables
}
