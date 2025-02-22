/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package romanow.cnc.view;

//import com.sun.deploy.security.SelectableSecurityManager;
//import com.sun.j3d.utils.universe.SimpleUniverse;
import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Color3f;
import romanow.cnc.Values;
import romanow.cnc.settings.WorkSpace;
import romanow.cnc.slicer.SliceData;
import romanow.cnc.slicer.SliceLayer;
import romanow.cnc.stl.GCodeLayer;
import romanow.cnc.stl.STLLine;
import romanow.cnc.utils.Events;
import romanow.cnc.view.design.JCheckBoxButton;
import romanow.cnc.viewer3d.PCanvas3D171;
import romanow.cnc.viewer3d.PModel171;
//---------- Старая Java3D ----------------------------------
//import javax.media.j3d.Appearance;
//import javax.media.j3d.Background;
//import javax.media.j3d.LineAttributes;
//import javax.media.j3d.Shape3D;
//import javax.vecmath.Color3f;
//import javax.media.j3d.Appearance;
//import javax.vecmath.Color3f;

import java.awt.*;
import java.util.ArrayList;

import static romanow.cnc.viewer3d.PModel.colorAppearance;

/**
 *
 * @author Admin
 */
public class Loop3DPanel171 extends BasePanel {
    //private Shape3D modelView = null;
    private Node modelView = null;
    private SliceData data;
    private int cLayer=0;
    private Thread thread=null;
    private PCanvas3D171 canvas;
    private PModel171 model;
    private SimpleUniverse universe;
    private ArrayList<GCodeLayer> gCode=new ArrayList<>();
    private LineAttributes lineAttr = null;
    private final static float lineWidth=1.5f;
    private Object byStepSynch = new Object();
    private double layerZ=0;
    private boolean newLayer=true;
    private JCheckBoxButton byStep;
    private JCheckBoxButton modelViewButton;
    private JCheckBoxButton crossSection;
    private JCheckBoxButton animateButton;          // Управление анимацией
    private JCheckBoxButton gcodeMode;              // Источник анимации - выбор
    private boolean mlnPresent=false;
    private boolean gCodePresent=false;
    double Scale0 = 0.1;
    /**
     * Creates new form Loop3DPanel
     */
    public Loop3DPanel171(CNCViewer base) {
        super(base);
        Dimension dim = WorkSpace.ws().getDim();
        initComponents();
        byStep = new JCheckBoxButton(ByStep);
        byStep.setSelected(false);
        modelViewButton = new JCheckBoxButton(ModelView);
        modelViewButton.setSelected(true);
        crossSection = new JCheckBoxButton(LayerCut);
        crossSection.setSelected(false);
        animateButton = new JCheckBoxButton(AnimateButton,"animate-48","animate-run-48");
        animateButton.setSelected(false);
        animateButton.setEnabled(false);
        gcodeMode = new JCheckBoxButton(GodeModeButton);
        gcodeMode.setSelected(false);
        gcodeMode.setEnabled(false);
        setComponentsScale();
        setPreferredSize(createDim(dim,Values.FrameWidth-100, Values.FrameHeight-Values.FrameBottom*2));
        //universe = new SimpleUniverse(canvas);
        //canvas.initcanvas(universe);
        }
    @Override
    public boolean isSelectedMode(){            // Промотр 3D для любой загруженной модели
        int state = WorkSpace.ws().dataState();
        mlnPresent = state == Values.Sliced;
        return mlnPresent || gCodePresent;
        //return ((modeMask() & mode)!=0) && modeEnabled();
        }
    private void setLayersMLN(){
        LAYERS.removeAll();
        for(int i=0;i<data.size();i++){
            SliceLayer lr = data.get(i);
            LAYERS.addItem(lr.label());
            }
        cLayer = 0;
        }
    private void setLayersGCode(){
        LAYERS.removeAll();
        for(int i=0;i<gCode.size();i++){
            GCodeLayer lr = gCode.get(i);
            LAYERS.addItem(String.format("%-4.2f мм / %d",lr.getLayerZ(),i+1));
            }
        cLayer = 0;
        }

    private void paintView(final boolean full){
        if (!full)
            killThread();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (gcodeMode.isSelected())
                    paintViewGCode(full);
                else
                    paintViewMLN(full);
                }
            });
        thread.start();
        }

    @Override
    public void onEvent(int code, int par1, long par2, String par3, Object oo) {
        if (code== Events.GCode){
            gCodePresent = true;
            gCode = (ArrayList<GCodeLayer>) oo;
            if (mlnPresent)
                gcodeMode.setSelected(true);
            setModeButtons();
            }
        }

    private void setModeButtons(){
        if (!(mlnPresent || gCodePresent)){
            LAYERS.removeAll();
            gcodeMode.setEnabled(false);
            animateButton.setEnabled(false);
            return;
            }
        animateButton.setEnabled(true);
        boolean bb = mlnPresent && gCodePresent;
        gcodeMode.setEnabled(bb);
        if (!bb)                                // Если только один источник
            gcodeMode.setSelected(gCodePresent);
        if (bb && gcodeMode.isSelected()){
            setLayersGCode();
            gcodeMode.setSelected(true);
            }
        else{
            setLayersMLN();
            gcodeMode.setSelected(false);
            }
        }

    private final static Color[] colors = {
        Values.ColorDarkGreen,
        Color.blue,
        Color.cyan,
        Values.ColorYellow,
        Color.black,
        Color.magenta
        };

    private void renderSynch(){
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                canvas.rendermodel(model,universe);
                }
            });
        }


    private void paintViewGCode(final boolean full){
        model.cleanup();
        if (modelViewButton.isSelected()) {
            model.addChild(modelView);
            }
        data = WorkSpace.ws().data();
        if (data != null) {
            int idx = LAYERS.getSelectedIndex();
            if (idx!=-1){
                if (crossSection.isSelected())
                    model.addSource(data.get(idx));
                else
                    model.addLoop(data.get(idx));
                }
            }
        if (!full) {
           renderSynch();  // ?????????????????????????
            return;
            }
        int idx = LAYERS.getSelectedIndex();
        final ArrayList<STLLine> tmp = new ArrayList<>();
        GCodeLayer lr = gCode.get(idx);
           for(int ii=0;ii<lr.groups.size();ii++){
               for (STLLine line2 : lr.groups.get(ii)) {
                   tmp.add(line2);
                   paintView(tmp,(float) lr.getLayerZ());
                   try{
                       Thread.sleep((50-SPEED.getValue())*2);
                       } catch (InterruptedException e) {}
                   }
               tmp.add(null);
               }
              renderSynch();
              if (byStep.isSelected()) {
                  try {
                     synchronized (byStepSynch){
                        byStepSynch.wait();
                         }
                     } catch (InterruptedException e) {}
                  }
             }

    private void paintViewMLN(final boolean full){
        model.cleanup();
        if (modelViewButton.isSelected()) {
            model.addChild(modelView);
            }
        data = WorkSpace.ws().data();
        int idx = LAYERS.getSelectedIndex();
        if (idx!=-1){
             if (crossSection.isSelected())
                model.addSource(data.get(idx));
             else
                model.addLoop(data.get(idx));
                }
        if (!full) {
            renderSynch();  // ?????????????????????????
            return;
            }
        double zz = data.get(idx).z();
        final ArrayList<STLLine> tmp = new ArrayList<>();
        for (STLLine line2 : data.get(idx).segments().lines()) {
             tmp.add(line2);
                    paintView(tmp,(float) zz);
                    try{
                        Thread.sleep((50-SPEED.getValue())*2);
                    } catch (InterruptedException e) {}
                }
        renderSynch();
        if (byStep.isSelected()) {
            try {
                synchronized (byStepSynch){
                   byStepSynch.wait();
                   }
                } catch (InterruptedException e) {}
            }
        }


    private void paintView(ArrayList<STLLine> lines, float z){
        model.cleanup();
        if (modelViewButton.isSelected()) {
            model.addChild(modelView);
            }
        if (data != null) {
            int idx = LAYERS.getSelectedIndex();
            if (idx!=-1){
                if (crossSection.isSelected())
                    model.addSource(data.get(idx));
                else
                    model.addLoop(data.get(idx));
                }
            }
        int colorIdx=0;
        Appearance app = PModel171.colorAppearance(colors[colorIdx]);
        app.setLineAttributes(lineAttr);
        for(STLLine line1 : lines){
            if (line1==null){
                if (colorIdx<colors.length-1)
                    colorIdx++;
                else
                    colorIdx=0;
                app = PModel171.colorAppearance(colors[colorIdx]);
                app.setLineAttributes(lineAttr);
                continue;
                }
            model.addLine(line1,z,app);
            }
        renderSynch();
        if (byStep.isSelected()) {
            try {
                synchronized (byStepSynch){
                    byStepSynch.wait();
                }
            } catch (InterruptedException e) {}
        }
    }



    private void paintSlice(){
        Appearance app = PModel171.colorAppearance(Color.blue);
        app.setLineAttributes(lineAttr);
        int idx = LAYERS.getSelectedIndex();
        SliceLayer layer = data.get(idx);
        for (STLLine line : layer.segments().lines()) {
            model.addLine(line,(float) layer.z(),app);
            canvas.rendermodel(model,universe);
            try {
                Thread.sleep(10);
                } catch (InterruptedException e) {}
            }
        }

    @Override
    public String getName() {
        return "Анимация 3D";
        }

    @Override
    public int modeMask() {
        return Values.PanelSTL3DLoops;
        }

    @Override
    public boolean modeEnabled() {
        return true;
        }

    @Override
    public void onDeactivate() {
        universe.removeAllLocales();
        universe.cleanup();
        killThread();
        if (canvas!=null)
            remove(canvas);
        }

    @Override
    public void onActivate() {
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        canvas = new PCanvas3D171(config);
        Dimension dim = WorkSpace.ws().getDim();
        canvas.setBounds(createRec(dim,220,10,Values.FrameWidth-220, Values.FrameHeight-Values.FrameBottom));
        add(canvas);
        universe = new SimpleUniverse(canvas);
        canvas.initcanvas(universe);
        lineAttr = new LineAttributes(1,1,false);
        lineAttr.setLineWidth(lineWidth);
        data = WorkSpace.ws().data();
        NextStep.setVisible(byStep.isSelected());
        setModeButtons();
        /*
        setBounds(100,100,750,700);
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        canvas = new PCanvas3D(config);
        canvas.setBounds(200, 10, 700, 700);
        //add(canvas,BorderLayout.CENTER);
        add(canvas);
        universe = new SimpleUniverse(canvas);
        canvas.initcanvas(universe);
        //setLocationRelativeTo(null);
        setVisible(true);
        */
        //pack();
        //setLocationRelativeTo(null);
        setVisible(true);
        //addWindowListener(this);
        //---- вернуть обратно ----------------------
        canvas.homeview(universe);
        if(model != null)
            model.cleanup();
        model = new PModel171();
        model.setBnormstrip(true);
        model.addChild(new Background(new Color3f(0,0,0)));
        modelView = model.addTriangles(WorkSpace.ws().model().triangles(),0.6f);
        //thread = new Thread(()->{       // выполнить в потоке вне GUI
        //if (ws().isModelSliced())
        //    model.addLoops(ws().data());
        try {
            canvas.rendermodel(model, universe);
            } catch (Exception ee){}            //??????????????????????????????
        //    });
        //thread.start();
        }

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

        PREV = new javax.swing.JButton();
        NEXT = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        LAYERS = new javax.swing.JComboBox<>();
        SPEED = new javax.swing.JSlider();
        jLabel1 = new javax.swing.JLabel();
        NextStep = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        ByStep = new javax.swing.JButton();
        ModelView = new javax.swing.JButton();
        LayerCut = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        AnimateButton = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        GodeModeButton = new javax.swing.JButton();

        setLayout(null);

        PREV.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        PREV.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/icon2/icons8-left-48.png"))); // NOI18N
        PREV.setBorderPainted(false);
        PREV.setContentAreaFilled(false);
        PREV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PREVActionPerformed(evt);
            }
        });
        add(PREV);
        PREV.setBounds(10, 140, 50, 50);

        NEXT.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        NEXT.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/icon2/icons8-right-48.png"))); // NOI18N
        NEXT.setBorderPainted(false);
        NEXT.setContentAreaFilled(false);
        NEXT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NEXTActionPerformed(evt);
            }
        });
        add(NEXT);
        NEXT.setBounds(150, 140, 50, 50);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel2.setText("G-код");
        add(jLabel2);
        jLabel2.setBounds(140, 320, 70, 29);

        LAYERS.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        LAYERS.setToolTipText("");
        add(LAYERS);
        LAYERS.setBounds(10, 90, 190, 40);

        SPEED.setMaximum(50);
        SPEED.setMinimum(1);
        SPEED.setValue(20);
        SPEED.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                SPEEDStateChanged(evt);
            }
        });
        add(SPEED);
        SPEED.setBounds(10, 420, 180, 20);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("Анимация");
        add(jLabel1);
        jLabel1.setBounds(10, 260, 150, 30);

        NextStep.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        NextStep.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/icon2/icons8-next-48.png"))); // NOI18N
        NextStep.setBorderPainted(false);
        NextStep.setContentAreaFilled(false);
        NextStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NextStepActionPerformed(evt);
            }
        });
        add(NextStep);
        NextStep.setBounds(10, 480, 50, 50);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel3.setText("По шагам");
        add(jLabel3);
        jLabel3.setBounds(10, 450, 120, 30);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel4.setText("Сечение");
        add(jLabel4);
        jLabel4.setBounds(10, 210, 110, 29);

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel5.setText("Слой");
        add(jLabel5);
        jLabel5.setBounds(10, 60, 70, 29);

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel6.setText("Скорость");
        add(jLabel6);
        jLabel6.setBounds(10, 380, 150, 30);

        ByStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ByStepActionPerformed(evt);
            }
        });
        add(ByStep);
        ByStep.setBounds(140, 450, 50, 40);

        ModelView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModelViewActionPerformed(evt);
            }
        });
        add(ModelView);
        ModelView.setBounds(150, 40, 50, 40);

        LayerCut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LayerCutActionPerformed(evt);
            }
        });
        add(LayerCut);
        LayerCut.setBounds(150, 210, 50, 40);

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel7.setText("STL");
        add(jLabel7);
        jLabel7.setBounds(10, 20, 60, 29);

        AnimateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AnimateButtonActionPerformed(evt);
            }
        });
        add(AnimateButton);
        AnimateButton.setBounds(150, 260, 50, 40);

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel8.setText("MLN");
        add(jLabel8);
        jLabel8.setBounds(10, 320, 60, 29);

        GodeModeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GodeModeButtonActionPerformed(evt);
            }
        });
        add(GodeModeButton);
        GodeModeButton.setBounds(70, 320, 50, 40);
    }// </editor-fold>//GEN-END:initComponents

    private void PREVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PREVActionPerformed
        if (cLayer==0)
        return;
        cLayer--;
        LAYERS.setSelectedIndex(cLayer);
        paintView(false);
    }//GEN-LAST:event_PREVActionPerformed

    private void NEXTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NEXTActionPerformed
        if (cLayer==LAYERS.getItemCount()-1)
        return;
        cLayer++;
        LAYERS.setSelectedIndex(cLayer);
        paintView(false);
    }//GEN-LAST:event_NEXTActionPerformed

    private void SPEEDStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_SPEEDStateChanged
    }//GEN-LAST:event_SPEEDStateChanged

    private void NextStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NextStepActionPerformed
        synchronized (byStepSynch){
            byStepSynch.notifyAll();
            }
    }//GEN-LAST:event_NextStepActionPerformed

    private synchronized void killThread(){
        try{
            if (thread!=null){          // Тупо обломить поток
                thread.stop();
                thread=null;
                animateButton.setSelected(false);
                }
            } catch (Exception ee){}
        }


    private void ByStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ByStepActionPerformed
        byStep.itemStateChanged();
        NextStep.setVisible(byStep.isSelected());
        //if (!byStep.isSelected())
        synchronized (byStepSynch){
            byStepSynch.notifyAll();
            }
    }//GEN-LAST:event_ByStepActionPerformed

    private void ModelViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModelViewActionPerformed
        modelViewButton.itemStateChanged();
        paintView(false);
    }//GEN-LAST:event_ModelViewActionPerformed

    private void LayerCutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LayerCutActionPerformed
        crossSection.itemStateChanged();
        paintView(false);
    }//GEN-LAST:event_LayerCutActionPerformed

    private void AnimateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AnimateButtonActionPerformed
        if (animateButton.isSelected()){
            killThread();
            }
        else{
            paintView(true);
            }
        animateButton.itemStateChanged();
    }//GEN-LAST:event_AnimateButtonActionPerformed

    private void GodeModeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GodeModeButtonActionPerformed
        gcodeMode.itemStateChanged();
        setModeButtons();
        }//GEN-LAST:event_GodeModeButtonActionPerformed

    @Override
    public void refresh() {

        }
    @Override
    public void shutDown() {
        }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AnimateButton;
    private javax.swing.JButton ByStep;
    private javax.swing.JButton GodeModeButton;
    private javax.swing.JComboBox<String> LAYERS;
    private javax.swing.JButton LayerCut;
    private javax.swing.JButton ModelView;
    private javax.swing.JButton NEXT;
    private javax.swing.JButton NextStep;
    private javax.swing.JButton PREV;
    private javax.swing.JSlider SPEED;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    // End of variables declaration//GEN-END:variables
}
