/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.viewer3d;

import com.sun.j3d.utils.universe.SimpleUniverse;
import romanow.cnc.view.BaseFrame;
import romanow.cnc.slicer.SliceData;
import romanow.cnc.slicer.SliceLayer;

import java.awt.*;
import javax.media.j3d.Background;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;

public class Loop3DViewer extends BaseFrame{
    Shape3D modelView = null;
    SliceData data;
    int cLayer=0;
    Thread thread=null;
	PCanvas3D canvas;
	PModel model;
	SimpleUniverse universe;
	double Scale0 = 0.1;
    public Loop3DViewer() {
        if (!tryToStart()) return;
        initComponents();
        setTitle("Просмотр модели и контуров 3D");
        data = ws().data();
        setLayers();
        setBounds(100,100,750,700);
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		canvas = new PCanvas3D(config);
        canvas.setBounds(200, 10, 600, 600);
		getContentPane().add(canvas);
		universe = new SimpleUniverse(canvas);
		canvas.initcanvas(universe);
		setLocationRelativeTo(null);
		setVisible(true);
		if(model != null)
			model.cleanup();
		model = new PModel();
		model.setBnormstrip(true);
        model.addChild(new Background(new Color3f(0,0,0)));
		modelView = model.addTriangles(ws().model().triangles(),0.6f);
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

    
    private void setLayers(){
        LAYERS.removeAll();
        if (!ws().slicePresent())
            return;
        for(int i=0;i<data.size();i++){
            SliceLayer lr = data.get(i);
            LAYERS.add(lr.label());
            }
        cLayer = 0;
        }
    
    private void paintView(){
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
        canvas.rendermodel(model, universe);        
        }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ModelView = new javax.swing.JCheckBox();
        LAYERS = new java.awt.Choice();
        PREV = new javax.swing.JButton();
        NEXT = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        Source = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        ModelView.setSelected(true);
        ModelView.setText("STL-модель 3D");
        ModelView.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ModelViewItemStateChanged(evt);
            }
        });
        getContentPane().add(ModelView);
        ModelView.setBounds(20, 20, 110, 23);

        LAYERS.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                LAYERSItemStateChanged(evt);
            }
        });
        getContentPane().add(LAYERS);
        LAYERS.setBounds(20, 70, 150, 20);

        PREV.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        PREV.setText("<");
        PREV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PREVActionPerformed(evt);
            }
        });
        getContentPane().add(PREV);
        PREV.setBounds(20, 100, 41, 30);

        NEXT.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        NEXT.setText(">");
        NEXT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NEXTActionPerformed(evt);
            }
        });
        getContentPane().add(NEXT);
        NEXT.setBounds(130, 100, 41, 30);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Слой");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(20, 50, 34, 14);

        Source.setText("Исходное  сечение");
        Source.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                SourceItemStateChanged(evt);
            }
        });
        getContentPane().add(Source);
        Source.setBounds(20, 140, 150, 23);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
		universe.removeAllLocales();
		universe.cleanup();
        if (thread!=null){          // Тупо обломить поток
            thread.stop();
            thread=null;
            }
        onClose();
    }//GEN-LAST:event_formWindowClosing

    private void ModelViewItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ModelViewItemStateChanged
        paintView();
    }//GEN-LAST:event_ModelViewItemStateChanged

    private void LAYERSItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_LAYERSItemStateChanged
        cLayer = LAYERS.getSelectedIndex();
        paintView();
    }//GEN-LAST:event_LAYERSItemStateChanged

    private void PREVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PREVActionPerformed
        if (cLayer==0)
        return;
        cLayer--;
        LAYERS.select(cLayer);
        paintView();
    }//GEN-LAST:event_PREVActionPerformed

    private void NEXTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NEXTActionPerformed
        if (cLayer==data.size()-1)
            return;
        cLayer++;
        LAYERS.select(cLayer);
        paintView();
    }//GEN-LAST:event_NEXTActionPerformed

    private void SourceItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_SourceItemStateChanged
        paintView();
    }//GEN-LAST:event_SourceItemStateChanged

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Loop3DViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Loop3DViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Loop3DViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Loop3DViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Loop3DViewer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Choice LAYERS;
    private javax.swing.JCheckBox ModelView;
    private javax.swing.JButton NEXT;
    private javax.swing.JButton PREV;
    private javax.swing.JCheckBox Source;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}
