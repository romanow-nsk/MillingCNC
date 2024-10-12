/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epos.slm3d.console;

import epos.slm3d.graph.*;
import epos.slm3d.m3d.*;
import epos.slm3d.settings.Settings;
import epos.slm3d.settingsView.LayerPrintSettings;
import epos.slm3d.stl.*;
import epos.slm3d.utils.Events;
import epos.slm3d.utils.Utils;
import epos.slm3d.utils.Values;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

/**
 *
 * @author romanow
 */
public class TestConsole extends BaseFrame{
    private final int  masMax=20;
    private ViewNotifyer notify;
    private DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
    private DecimalFormat df = new DecimalFormat("00.000", dfs);
    private Color gridColor = new Color(220,220,20);
    private Color pointColor = new Color(20,150,20);
    private GraphPanel gPanel;              // Панель редакторв
    private int mouseX=-1;                  // Координаты мыши
    private int mouseY;
    private int drag=-1;                    // Индекс кнопки перетаскивания
    private int clickCount=0;               // счетчик кликов
    private int nb=0;
    private GraphPoint pp;
    private O2DGroup model=new O2DGroup();  // Группа объектов редактирования
    private int cObj=0;                     // Индекс выбранного объекта
    private String testName="Без_имени";
    private GraphFactory fac = new GraphFactory(); // Фабрика графических примитивов
    private GraphObject selected=null;      // Ссылка на выбранный объект
    
    private void refresh(){
        int cc = cObj;
        setLayers();
        cObj = cc;
        OBJ.select(cObj);
        selected = model.get(cObj);
        objectView(); 
        paintView();
        }
    private void groupOperation(MouseEvent evt){
        GraphPoint qq = new GraphPoint(gPanel.pixelToX(evt.getX()),gPanel.pixelToY(evt.getY()));
        int idx = model.nearest(qq, Values.FindPointDistance);
        if (idx!=-1){
            GraphObject pp = model.get(idx);
            if (pp.getClass()==O2DGroup.class){
                new OK(getBounds(),"Разгруппировать",()->{
                    model.list().remove(idx);
                    ArrayList<GraphObject> list = ((O2DGroup)pp).list();
                    for(GraphObject zz : list)
                        model.add(zz);                    
                    setLayers();
                    objectView();            
                    paintView();
                    });
                }
            else{
                if (cObj!=-1 && selected.getClass()==O2DGroup.class){
                     new OK(getBounds(),"Добавить в группу",()->{
                        ((O2DGroup)selected).add(model.list().remove(idx));
                        if (idx < cObj) cObj--;
                        refresh();
                        });
                    }
                }
            }
        }
    private void selectObject(MouseEvent evt){
        GraphPoint qq = new GraphPoint(gPanel.pixelToX(evt.getX()),gPanel.pixelToY(evt.getY()));
        int idx = model.nearest(qq, Values.FindPointDistance);
        if (idx!=-1){
            cObj = idx;
            OBJ.select(cObj);
            selectLayer();
            objectView(); 
            fac.select(ObjectType, selected);
            }
        }
    private I_Mouse mBack = new I_Mouse(){
        @Override
        public void MouseClicked(MouseEvent evt) {
            if (clickCount++ == 0){
                nb = evt.getButton();
                pp = new GraphPoint(gPanel.pixelToX(evt.getX()),gPanel.pixelToY(evt.getY()));
                Utils.runAfterDelayMS((int)Values.DoubleClickInMS, ()->{
                    if (clickCount==1){
                        if (nb==1){ selectObject(evt); }
                        }
                    else{  groupOperation(evt); }
                    clickCount=0;
                });
            }}
        @Override
        public void MousePressed(MouseEvent evt) {
            drag=evt.getButton();
            mouseX = evt.getX();
            mouseY = evt.getY();
            if (drag==3)  selectObject(evt);
            }
        @Override
        public void MouseReleased(MouseEvent evt) {
            if (drag==1)  mouseShift(evt);
            if (drag==3 && cObj!=-1){ objectShift(evt); }
            mouseX=-1;
            drag=-1;           
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
                    paintView();
                    }}
            else{
                if (gPanel.mas > 1){
                    gPanel.mas--;
                    MAS.setValue(gPanel.mas);
                    paintView();
                    }
                }            
            }
        @Override
        public void MouseDragged(MouseEvent evt) {
            if (drag==1)
                mouseShift(evt); 
            if (drag==3 && cObj!=-1)
                objectShift(evt); 
            }
        };
    //--------------------------------------------------------------------------
    private void createTestModel(){
        model = new O2DGroup();
        model.add(new O2DOval(-0.1,-0.1,0.2,0.3));
        model.add(new O2DRectangle(-0.2,-0.1,0.3,0.2));
        model.add(new O2DLine(-0.2,-0.1,0.3,0.2));
        model.add(new O2DLine(-0.2,0.4,0.3,0.2));
        O2DGroup xx = new O2DGroup();
        xx.add(new O2DOval(-0.4,-0.3,-0.3,-0.1));
        xx.add(new O2DRectangle(0.1,0.1,0.2,0.2));
        xx.add(new O2DLine(-0.2,-0.1,0.2,0.4));
        model.add(xx);       
        }
    public TestConsole(ViewNotifyer notify0) {
        if (!tryToStart()) return;
        notify = notify0;
        initComponents();
        setTitle("2D редактор тестов");
        setBounds(100,100,815,730);
        gPanel = new GraphPanel(mBack);
        gPanel.setBounds(200, 40, 600);
        getContentPane().add(gPanel);
        gPanel.setPaintParams(HORIZ,VERTIC);
        fac.toBox(ObjectType);
        setLayers();
        objectView();
        paintView();
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
        OBJ = new java.awt.Choice();
        MAS = new javax.swing.JSlider();
        PREV = new javax.swing.JButton();
        NEXT = new javax.swing.JButton();
        ObjectType = new java.awt.Choice();
        jLabel2 = new javax.swing.JLabel();
        B4 = new javax.swing.JButton();
        MY = new javax.swing.JTextField();
        PY0 = new javax.swing.JTextField();
        Grid = new javax.swing.JCheckBox();
        MX = new javax.swing.JTextField();
        PX0 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        X2 = new javax.swing.JLabel();
        X3 = new javax.swing.JLabel();
        PY1 = new javax.swing.JTextField();
        PX1 = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        X6 = new javax.swing.JLabel();
        X7 = new javax.swing.JLabel();
        Y0 = new javax.swing.JTextField();
        X0 = new javax.swing.JTextField();
        SZY = new javax.swing.JTextField();
        SZX = new javax.swing.JTextField();
        X8 = new javax.swing.JLabel();
        Param1 = new javax.swing.JTextField();
        X4 = new javax.swing.JLabel();
        Param2 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
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
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                HORIZCaretPositionChanged(evt);
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
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                VERTICCaretPositionChanged(evt);
            }
        });
        getContentPane().add(VERTIC);
        VERTIC.setBounds(170, 440, 20, 200);

        OBJ.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                OBJItemStateChanged(evt);
            }
        });
        getContentPane().add(OBJ);
        OBJ.setBounds(10, 10, 250, 20);

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
        PREV.setBounds(10, 40, 41, 30);

        NEXT.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        NEXT.setText(">");
        NEXT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NEXTActionPerformed(evt);
            }
        });
        getContentPane().add(NEXT);
        NEXT.setBounds(150, 40, 41, 30);

        ObjectType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ObjectTypeItemStateChanged(evt);
            }
        });
        getContentPane().add(ObjectType);
        ObjectType.setBounds(10, 390, 180, 20);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Объект");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(270, 10, 80, 14);

        B4.setText("Параметры печати");
        B4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B4ActionPerformed(evt);
            }
        });
        getContentPane().add(B4);
        B4.setBounds(10, 80, 180, 23);

        MY.setEditable(false);
        MY.setBackground(new java.awt.Color(200, 200, 200));
        MY.setText("0");
        getContentPane().add(MY);
        MY.setBounds(130, 130, 60, 25);

        PY0.setText("0");
        PY0.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PY0KeyPressed(evt);
            }
        });
        getContentPane().add(PY0);
        PY0.setBounds(130, 180, 60, 25);

        Grid.setSelected(true);
        Grid.setText("Сетка (мм)");
        Grid.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                GridItemStateChanged(evt);
            }
        });
        getContentPane().add(Grid);
        Grid.setBounds(60, 40, 90, 23);

        MX.setEditable(false);
        MX.setBackground(new java.awt.Color(200, 200, 200));
        MX.setText("0");
        getContentPane().add(MX);
        MX.setBounds(60, 130, 60, 25);

        PX0.setText("0");
        PX0.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PX0KeyPressed(evt);
            }
        });
        getContentPane().add(PX0);
        PX0.setBounds(60, 180, 60, 25);

        jButton1.setText("Сохранить");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(20, 610, 140, 23);

        jButton2.setText("Загрузить");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2);
        jButton2.setBounds(20, 580, 140, 23);

        jButton3.setText("На печать");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3);
        jButton3.setBounds(20, 480, 140, 23);

        X2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        X2.setText("Параметры");
        getContentPane().add(X2);
        X2.setBounds(10, 330, 80, 15);

        X3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        X3.setText("Y");
        getContentPane().add(X3);
        X3.setBounds(130, 110, 20, 15);

        PY1.setText("0");
        PY1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PY1KeyPressed(evt);
            }
        });
        getContentPane().add(PY1);
        PY1.setBounds(130, 210, 60, 25);

        PX1.setText("0");
        PX1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PX1KeyPressed(evt);
            }
        });
        getContentPane().add(PX1);
        PX1.setBounds(60, 210, 60, 25);

        jButton4.setText("Добавить");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton4);
        jButton4.setBounds(20, 420, 140, 23);

        X6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        X6.setText("X");
        getContentPane().add(X6);
        X6.setBounds(60, 110, 20, 15);

        X7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        X7.setText("Граница");
        getContentPane().add(X7);
        X7.setBounds(10, 160, 60, 15);

        Y0.setText("0");
        Y0.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Y0KeyPressed(evt);
            }
        });
        getContentPane().add(Y0);
        Y0.setBounds(130, 250, 60, 25);

        X0.setText("0");
        X0.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                X0KeyPressed(evt);
            }
        });
        getContentPane().add(X0);
        X0.setBounds(60, 250, 60, 25);

        SZY.setText("0");
        SZY.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SZYKeyPressed(evt);
            }
        });
        getContentPane().add(SZY);
        SZY.setBounds(130, 300, 60, 25);

        SZX.setText("0");
        SZX.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SZXKeyPressed(evt);
            }
        });
        getContentPane().add(SZX);
        SZX.setBounds(60, 300, 60, 25);

        X8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        X8.setText("Центр");
        getContentPane().add(X8);
        X8.setBounds(10, 230, 60, 15);

        Param1.setText("1");
        Param1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Param1KeyPressed(evt);
            }
        });
        getContentPane().add(Param1);
        Param1.setBounds(60, 350, 60, 25);

        X4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        X4.setText("Размеры");
        getContentPane().add(X4);
        X4.setBounds(10, 280, 60, 15);

        Param2.setText("1");
        Param2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Param2KeyPressed(evt);
            }
        });
        getContentPane().add(Param2);
        Param2.setBounds(130, 350, 60, 25);

        jButton5.setText("Удалить");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton5);
        jButton5.setBounds(20, 450, 140, 23);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private I_STLPoint2D nearest(I_STLPoint2D pp){
        /*
        if (!MoveToNearest.isSelected()) return pp;
        if (pp==null) return pp;
        STLLineGroup gg = mode==0 ? layer.segments() : layer.lines();
        I_STLPoint2D pp2 = gg.nearestPoint(pp,Values.NearestPointDistance/Values.PrinterFieldSize);
        return pp2 == null ? pp : pp2;
        */
        return null;
        } 


    private boolean visible(I_Point2D point){
        return point.x()>=gPanel.xmin && point.x()<=gPanel.xmax && point.y()>=gPanel.ymin && point.y()<=gPanel.ymax;
        }
    private boolean visible(I_Line2D line){
        //boolean in = visible(line.one()) || visible(line.two());
        //if (in) return true;
        //return line.lengthXY()>gPanel.dxy*gPanel.vSize;       // Длина большая !!!!!!
        return false;
        }


    private void setLayers(){
        cObj=-1;
        selected = null;
        OBJ.removeAll();
        for(int i=0; i<model.size();i++)
            OBJ.add(model.get(i).toString());
        if (model.size()!=0){
            cObj = 0;
            selected = model.get(cObj);
            }
        }
 
    private boolean inUse=false;
    private void paintView(){
        if (inUse)
            return;
        inUse=true;
        new Thread(()->{
            try {
                Thread.sleep(10);
                java.awt.EventQueue.invokeLater(()->{ 
                    paintViewOrig();
                    inUse=false;
                    });
                } catch (InterruptedException ex) {}
            }).start();
        }



    private void paintViewOrig(){
        gPanel.clear();
        gPanel.setPaintParams(HORIZ,VERTIC);
        if (Grid.isSelected())
            gPanel.paintGrid(gridColor);
        gPanel.setColor(Color.black);
        for(int i=0;i<model.size();i++){
            GraphObject pp = model.get(i);
            if (i==cObj){
                gPanel.bold(true);
                pp.paint(Color.red,gPanel,true);
                gPanel.bold(false);
                }
            else
                pp.paint(Color.black,gPanel,true);
            }
        }

    private void VERTICStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_VERTICStateChanged
        paintView();
    }//GEN-LAST:event_VERTICStateChanged

    private void HORIZCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_HORIZCaretPositionChanged
        paintView();
    }//GEN-LAST:event_HORIZCaretPositionChanged

    private void VERTICCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_VERTICCaretPositionChanged
        paintView();
    }//GEN-LAST:event_VERTICCaretPositionChanged

    private void selectLayer(){
        cObj = OBJ.getSelectedIndex();
        objectView();
        paintView();
        }

    private void OBJItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_OBJItemStateChanged
        selectLayer();
    }//GEN-LAST:event_OBJItemStateChanged

    private void HORIZStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_HORIZStateChanged
        paintView();
    }//GEN-LAST:event_HORIZStateChanged

    private void MASStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_MASStateChanged
        gPanel.mas = MAS.getValue();
        paintView();        
    }//GEN-LAST:event_MASStateChanged

    @Override
    public void paint(Graphics g){
        super.paint(g);
        paintView();      // НЕ РУКАМИ, а по событию
        }
    
    private void mouseShift(MouseEvent evt){
        int xfin = evt.getX();
        int yfin = evt.getY();
        if (xfin == mouseX && yfin == mouseY)
            return;
        gPanel.x0 += gPanel.dxy * (mouseX - xfin);
        HORIZ.setValue((int)(-gPanel.x0*100));
        gPanel.y0 += gPanel.dxy * (mouseY - yfin);
        VERTIC.setValue((int)(-gPanel.y0*100));
        paintView();
        mouseX = xfin;
        mouseY = yfin;
        }
    
    private void objectView(){
        if (cObj==-1){
            PX0.setText("");
            PY0.setText("");
            PX1.setText("");
            PY1.setText("");
            X0.setText("");
            Y0.setText("");
            SZX.setText("");
            SZY.setText("");            
            return;
            }
        selected = model.get(cObj);
        PX0.setText(df.format(selected.x0()));
        PY0.setText(df.format(selected.y0()));  
        PX1.setText(df.format(selected.x1()));
        PY1.setText(df.format(selected.y1()));
        X0.setText(df.format(selected.midX()));
        Y0.setText(df.format(selected.midY()));  
        SZX.setText(df.format(selected.szX()));
        SZY.setText(df.format(selected.szY()));  
        Param1.setText(df.format(selected.getParam1()));
        Param2.setText(df.format(selected.getParam2()));
        }
    private void objectShift(MouseEvent evt){ 
        int xfin = evt.getX();
        int yfin = evt.getY();
        if (xfin == mouseX && yfin == mouseY)
            return;
        double dx = gPanel.dxy * (xfin - mouseX);
        double dy = gPanel.dxy * (mouseY - yfin);
        GraphObject gg = model.get(cObj);
        gg.shift(dx, dy);
        objectView();
        paintView();
        mouseX = xfin;
        mouseY = yfin;
        }
    
    private void NEXTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NEXTActionPerformed
        if (cObj==model.size())
            return;
        cObj++;
        OBJ.select(cObj);
        selected = model.get(cObj);
        objectView();       
        paintView();
    }//GEN-LAST:event_NEXTActionPerformed



    private void PREVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PREVActionPerformed
        if (cObj==0)
            return;
        cObj--;            
        selected = model.get(cObj);        
        OBJ.select(cObj);
        objectView();
        paintView();
    }//GEN-LAST:event_PREVActionPerformed

    private void ObjectTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ObjectTypeItemStateChanged

    }//GEN-LAST:event_ObjectTypeItemStateChanged

    
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        onClose();
    }//GEN-LAST:event_formWindowClosing

    private void setObjectsNoMove(){
        int cl = cObj;
        setLayers();
        cObj = cl;       
        OBJ.select(cObj);
        }
    private void B4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B4ActionPerformed
        if (cObj==-1) return;
        Settings print = selected.printSettings();
        boolean copy = print == null;
        Settings ss = copy ? ws().local() : print;
        new LayerPrintSettings(ss,copy,notify,(set)->{
            selected.printSettings(set);
            setObjectsNoMove();
            }).setVisible(true);

    }//GEN-LAST:event_B4ActionPerformed

    private void GridItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_GridItemStateChanged
        paintView();
    }//GEN-LAST:event_GridItemStateChanged

    private boolean testObject(KeyEvent evt){
        if(evt.getKeyCode()!=10) return false;
        if (cObj==-1) return false;
        if (selected.getClass()==O2DGroup.class){
            notify.notify(Values.error,"Группа не редактируется");
            return false;
            }        
        return true;
        }
    
    private void PX0KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PX0KeyPressed
        if (!testObject(evt)) return;
        try {
            double vv = Double.parseDouble(PX0.getText());
            if (notLine() && vv >= selected.x1()){
                notify.notify(Values.error,"Должно быть x0 < x1");
                objectView();
                return;
                }
            selected.x0(vv);
            objectView();            
            paintView();
            } catch(Exception ee){ notify.notify(Values.error,"Формат целого числа ????");}
    }//GEN-LAST:event_PX0KeyPressed

    private void PY0KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PY0KeyPressed
        if (!testObject(evt)) return;
        try {
            double vv = Double.parseDouble(PY0.getText());
            if (notLine() && vv >= selected.y1()){
                notify.notify(Values.error,"Должно быть y0 < y1");
                objectView();
                return;
                }
            selected.y0(vv);
            objectView();            
            paintView();
            } catch(Exception ee){ notify.notify(Values.error,"Формат целого числа ????");}
    }//GEN-LAST:event_PY0KeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String ss = getOutputFileName("Тест печати","layer","PrintTest.layer");
        if (ss==null) return;
        testName = Utils.fileName(ss);
        try{
            DataOutputStream out = new DataOutputStream(new FileOutputStream(ss));
            model.save(out);
            out.close();
            }catch(Exception ee){ ws().notify(Values.error, ee.getMessage());}
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        String ss = getInputFileName("Тест печати","layer",false);
        if (ss==null) return;
        testName = Utils.fileName(ss);
        try{
            DataInputStream out = new DataInputStream(new FileInputStream(ss));
            model = new O2DGroup();
            model.load(out);
            out.close();
            setLayers();
            paintView();
            }catch(Exception ee){ ws().notify(Values.error, ee.getMessage());}
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        ws().data(model.createForPrint());
        ws().dataChanged();
        ws().model().modelName(testName);
        ws().sendEvent(Events.FileState);
    }//GEN-LAST:event_jButton3ActionPerformed

    private boolean notLine(){
        return selected.getClass()!=O2DLine.class;
        }
    private void PY1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PY1KeyPressed
        if (!testObject(evt)) return;
        try {
            double vv = Double.parseDouble(PY1.getText());
            if (notLine() && vv <= selected.y0()){
                notify.notify(Values.error,"Должно быть y0 < y1");
                objectView();                
                return;
                }            
            selected.y1(vv);
            objectView();            
            paintView();
            } catch(Exception ee){ notify.notify(Values.error,"Формат целого числа ????");}
    }//GEN-LAST:event_PY1KeyPressed

    private void PX1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PX1KeyPressed
        if (!testObject(evt)) return;
        try {
            double vv = Double.parseDouble(PX1.getText());
            if (notLine() && vv <= selected.x0()){
                notify.notify(Values.error,"Должно быть x0 < x1");
                objectView();                
                return;
                }
            selected.x1(vv);
            objectView();            
            paintView();
            } catch(Exception ee){ notify.notify(Values.error,"Формат целого числа ????");}
    }//GEN-LAST:event_PX1KeyPressed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        GraphObject pp = fac.create(ObjectType, new GraphPoint(0,0));
            try {
                pp.setParam1(Double.parseDouble(Param1.getText()));
                pp.setParam2(Double.parseDouble(Param2.getText()));
                model.add(pp);
                setLayers();
                OBJ.select(model.size()-1);
                selectLayer();
                } catch(Exception ee){ notify.notify(Values.error,"Формат параметра ????");}
    }//GEN-LAST:event_jButton4ActionPerformed

    private void Y0KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Y0KeyPressed
        if (!testObject(evt)) return;
        try {
            double vv = Double.parseDouble(Y0.getText());
            selected.shift(0,vv/ - selected.midY());
            objectView();            
            paintView();
            } catch(Exception ee){ notify.notify(Values.error,"Формат целого числа ????");}
    }//GEN-LAST:event_Y0KeyPressed

    private void X0KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_X0KeyPressed
        if (!testObject(evt)) return;
        try {
            double vv = Double.parseDouble(X0.getText());
            selected.shift(vv/ - selected.midX(), 0);
            objectView();            
            paintView();
            } catch(Exception ee){ notify.notify(Values.error,"Формат целого числа ????");}
    }//GEN-LAST:event_X0KeyPressed

    private void SZYKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SZYKeyPressed
        if (!testObject(evt)) return;
        try {
            double vv = Double.parseDouble(SZY.getText());
            if (vv<=0){
                notify.notify(Values.error,"Размерность <=0");  
                objectView();                
                return;
                }
            double sz = selected.szY();            
            double mid = selected.midY();            
            selected.y0(mid - vv/2);
            selected.y1(mid + vv/2);
            objectView();            
            paintView();
            } catch(Exception ee){ notify.notify(Values.error,"Формат целого числа ????");}
    }//GEN-LAST:event_SZYKeyPressed

    private void SZXKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SZXKeyPressed
        if (!testObject(evt)) return;
        try {
            double vv = Double.parseDouble(SZX.getText());
            if (vv<=0){
                notify.notify(Values.error,"Размерность <=0");                
                return;
                }
            double sz = selected.szX();            
            double mid = selected.midX();            
            selected.x0(mid - vv/2);
            selected.x1(mid + vv/2);
            objectView();            
            paintView();
            } catch(Exception ee){ notify.notify(Values.error,"Формат целого числа ????");}
    }//GEN-LAST:event_SZXKeyPressed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        if (cObj == -1) return;
        new OK(getBounds(),"Удалить",()->{
            model.list().remove(cObj);
            setLayers();
            objectView();            
            paintView();
            });
    }//GEN-LAST:event_jButton5ActionPerformed

    private void Param1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Param1KeyPressed
        if (!testObject(evt)) return;
        try {
            double vv = Double.parseDouble(Param1.getText());
            if (vv<=0){
                notify.notify(Values.error,"Размерность <=0");                
                return;
                }
            selected.setParam1(vv);
            objectView();            
            paintView();
            } catch(Exception ee){ notify.notify(Values.error,"Формат вещественного числа ????");}
    }//GEN-LAST:event_Param1KeyPressed

    private void Param2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Param2KeyPressed
        if (!testObject(evt)) return;
        try {
            double vv = Double.parseDouble(Param2.getText());
            if (vv<=0){
                notify.notify(Values.error,"Размерность <=0");                
                return;
                }
            selected.setParam2(vv);
            objectView();            
            paintView();
            } catch(Exception ee){ notify.notify(Values.error,"Формат вещественного числа ????");}
        // TODO add your handling code here:
    }//GEN-LAST:event_Param2KeyPressed

    @Override
    public void onEvent(int code,boolean on, int value, String name) {
        super.onEvent(code,on,value,name);
        if (code== Events.Close){
            onClose();                      // TODO - сохранить файл ????
            }

        }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton B4;
    private javax.swing.JCheckBox Grid;
    private javax.swing.JSlider HORIZ;
    private javax.swing.JSlider MAS;
    private javax.swing.JTextField MX;
    private javax.swing.JTextField MY;
    private javax.swing.JButton NEXT;
    private java.awt.Choice OBJ;
    private java.awt.Choice ObjectType;
    private javax.swing.JButton PREV;
    private javax.swing.JTextField PX0;
    private javax.swing.JTextField PX1;
    private javax.swing.JTextField PY0;
    private javax.swing.JTextField PY1;
    private javax.swing.JTextField Param1;
    private javax.swing.JTextField Param2;
    private javax.swing.JTextField SZX;
    private javax.swing.JTextField SZY;
    private javax.swing.JSlider VERTIC;
    private javax.swing.JTextField X0;
    private javax.swing.JLabel X2;
    private javax.swing.JLabel X3;
    private javax.swing.JLabel X4;
    private javax.swing.JLabel X6;
    private javax.swing.JLabel X7;
    private javax.swing.JLabel X8;
    private javax.swing.JTextField Y0;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}
