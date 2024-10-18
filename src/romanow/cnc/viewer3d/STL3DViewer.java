/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.viewer3d;

import com.sun.j3d.utils.universe.SimpleUniverse;
import romanow.cnc.view.BaseFrame;
import romanow.cnc.utils.Events;
import romanow.cnc.m3d.ViewAdapter;
import romanow.cnc.Values;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 *
 * @author romanow
 */
public class STL3DViewer extends BaseFrame implements ActionListener{
    PCanvas3D canvas;
    JLabel lstatusline;
    PModel model;
    SimpleUniverse universe;
    JCheckBoxMenuItem mnstrp;

    @Override
    public synchronized void onEvent(int code, boolean on, int value, String name) {
        if (code==Events.Rotate){
            refresh();
            }
        }
    /**
     * Creates new form STL3DViewer
     */
    
    public STL3DViewer(ViewAdapter view) {
        if (!tryToStart()) return;
        initComponents();
        this.setBounds(150,150,1024,768);
        super.setTitle("STL Viewer ("+Values.getVersion()+")");
        setPreferredSize(new Dimension(1024, 768));
        JMenuBar mbar = new JMenuBar();
        JMenu mtools = new JMenu("");
        mnstrp = new JCheckBoxMenuItem("Regen Normals/Connect strips",true);
        mnstrp.addActionListener(this);
        mtools.add(mnstrp);
        mbar.add(mtools);
        setJMenuBar(mbar);
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        canvas = new PCanvas3D(config);
        getContentPane().add(canvas, BorderLayout.CENTER);
        universe = new SimpleUniverse(canvas);
        canvas.initcanvas(universe);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        refresh();
        }
    public void refresh(){
        if(model != null)
            model.cleanup();
        model = new PModel();
        model.setBnormstrip(mnstrp.isSelected());
        model.addTriangles(ws().model().triangles(),0);
        canvas.rendermodel(model, universe);
        }

    @Override
    public void shutDown() {

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
		universe.removeAllLocales();
		universe.cleanup();
        canvas.finish();
        onClose();
    }//GEN-LAST:event_formWindowClosing

    @Override
    public void actionPerformed(ActionEvent e) {

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
