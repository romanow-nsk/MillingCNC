/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package romanow.cnc.view;

import com.sun.j3d.utils.universe.SimpleUniverse;
import romanow.cnc.Values;
import romanow.cnc.settings.WorkSpace;
import romanow.cnc.slicer.SliceData;
import romanow.cnc.slicer.SliceLayer;
import romanow.cnc.stl.STLLine;
import romanow.cnc.viewer3d.PCanvas3D;
import romanow.cnc.viewer3d.PModel;
//---------- Старая Java3D ----------------------------------
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import java.awt.*;
import java.util.ArrayList;

import static romanow.cnc.viewer3d.PModel.colorAppearance;

/**
 *
 * @author Admin
 */
public class Loop3DPanel extends BasePanel {
    Shape3D modelView = null;
    SliceData data;
    int cLayer=0;
    Thread thread=null;
    PCanvas3D canvas;
    PModel model;
    SimpleUniverse universe;
    double Scale0 = 0.1;
    /**
     * Creates new form Loop3DPanel
     */
    public Loop3DPanel(BaseFrame base) {
        super(base);
        initComponents();
        }
    private void setLayers(){
        LAYERS.removeAll();
        if (!WorkSpace.ws().slicePresent())
            return;
        for(int i=0;i<data.size();i++){
            SliceLayer lr = data.get(i);
            LAYERS.addItem(lr.label());
            }
        cLayer = 0;
        }

    private void paintView(){
        paintView(null,0);
        }
    private void paintView(ArrayList<STLLine> lines, float z){
        model.cleanup();
        if (ModelView.isSelected()){
            model.addChild(modelView);
            }
        if (data!=null){
            int idx = LAYERS.getSelectedIndex();
            if (Source.isSelected())
                model.addSource(data.get(idx));
            else
                model.addLoop(data.get(idx));
            }
        if (lines!=null){
            Appearance app = colorAppearance(Color.red);
            for(STLLine line1 : lines)
            model.addLine(line1,z,app);
            canvas.rendermodel(model,universe);
            }
        else{
            if (MILLING.isSelected()){
                int idx = LAYERS.getSelectedIndex();
                final ArrayList<STLLine> tmp = new ArrayList<>();
                for (STLLine line2 : data.get(idx).segments().lines()) {
                    tmp.add(line2);
                    paintView(tmp,(float) data.get(idx).z());
                    try {
                        Thread.sleep(10);
                        } catch (InterruptedException e) {}
                    }
                }
            else
                canvas.rendermodel(model,universe);
            }
        }

    private void paintSlice(){
        Appearance app = colorAppearance(Color.blue);
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
        return "STL(3D)+контуры";
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
    public void onInit(boolean on) {
        if (!on)
            return;
        data = WorkSpace.ws().data();
        setLayers();
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
        setPreferredSize(new Dimension(Values.FrameWidth, Values.FrameHeight-Values.FrameBottom*2));
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        canvas = new PCanvas3D(config);
        canvas.setBounds(200,10,Values.FrameWidth-200, Values.FrameHeight-Values.FrameBottom*2);
        add(canvas);
        universe = new SimpleUniverse(canvas);
        canvas.initcanvas(universe);
        //pack();
        //setLocationRelativeTo(null);
        setVisible(true);
        //addWindowListener(this);
        //---- вернуть обратно ----------------------
        canvas.homeview(universe);
        if(model != null)
            model.cleanup();
        model = new PModel();
        model.setBnormstrip(true);
        model.addChild(new Background(new Color3f(0,0,0)));
        modelView = model.addTriangles(WorkSpace.ws().model().triangles(),0.6f);
        paintView();
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
        universe.removeAllLocales();
        universe.cleanup();
        if (thread!=null){          // Тупо обломить поток
            thread.stop();
            thread=null;
            }
        }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ModelView = new javax.swing.JCheckBox();
        PREV = new javax.swing.JButton();
        NEXT = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        Source = new javax.swing.JCheckBox();
        LAYERS = new javax.swing.JComboBox<>();
        MILLING = new javax.swing.JCheckBox();

        setLayout(null);

        ModelView.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        ModelView.setSelected(true);
        ModelView.setText("STL-модель 3D");
        ModelView.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ModelViewItemStateChanged(evt);
            }
        });
        add(ModelView);
        ModelView.setBounds(20, 20, 150, 24);

        PREV.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        PREV.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/left.PNG"))); // NOI18N
        PREV.setBorderPainted(false);
        PREV.setContentAreaFilled(false);
        PREV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PREVActionPerformed(evt);
            }
        });
        add(PREV);
        PREV.setBounds(20, 110, 40, 40);

        NEXT.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        NEXT.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/right.PNG"))); // NOI18N
        NEXT.setBorderPainted(false);
        NEXT.setContentAreaFilled(false);
        NEXT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NEXTActionPerformed(evt);
            }
        });
        add(NEXT);
        NEXT.setBounds(130, 110, 40, 40);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("Слой");
        add(jLabel2);
        jLabel2.setBounds(20, 50, 80, 17);

        Source.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Source.setText("Исходное  сечение");
        Source.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                SourceItemStateChanged(evt);
            }
        });
        add(Source);
        Source.setBounds(20, 150, 170, 24);

        LAYERS.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        LAYERS.setToolTipText("");
        add(LAYERS);
        LAYERS.setBounds(20, 70, 150, 30);

        MILLING.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        MILLING.setText("Фрезерование");
        MILLING.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                MILLINGItemStateChanged(evt);
            }
        });
        add(MILLING);
        MILLING.setBounds(20, 180, 140, 24);
    }// </editor-fold>//GEN-END:initComponents

    private void ModelViewItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ModelViewItemStateChanged
        paintView();
    }//GEN-LAST:event_ModelViewItemStateChanged

    private void PREVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PREVActionPerformed
        if (cLayer==0)
        return;
        cLayer--;
        LAYERS.setSelectedIndex(cLayer);
        paintView();
    }//GEN-LAST:event_PREVActionPerformed

    private void NEXTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NEXTActionPerformed
        if (cLayer==data.size()-1)
        return;
        cLayer++;
        LAYERS.setSelectedIndex(cLayer);
        paintView();
    }//GEN-LAST:event_NEXTActionPerformed

    private void SourceItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_SourceItemStateChanged
        paintView();
    }//GEN-LAST:event_SourceItemStateChanged

    private void MILLINGItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_MILLINGItemStateChanged
        paintView();
    }//GEN-LAST:event_MILLINGItemStateChanged

    @Override
    public void refresh() {

    }

    @Override
    public void onEvent(int code, int par1, long par2, String par3, Object oo) {

    }

    @Override
    public void shutDown() {

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> LAYERS;
    private javax.swing.JCheckBox MILLING;
    private javax.swing.JCheckBox ModelView;
    private javax.swing.JButton NEXT;
    private javax.swing.JButton PREV;
    private javax.swing.JCheckBox Source;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}
