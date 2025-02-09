/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package romanow.cnc.view;

import lombok.Setter;
import romanow.cnc.Values;
import lombok.Getter;
import romanow.cnc.m3d.I_PanelEvent;
import romanow.cnc.m3d.ViewNotifyer;
import romanow.cnc.settings.*;
import romanow.cnc.view.design.JCheckBoxButton;
import romanow.cnc.view.panels.DigitPanel;
import romanow.cnc.view.panels.I_RealValue;
import romanow.cnc.view.panels.KeyBoardPanel;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Admin
 */
public abstract class BasePanel extends javax.swing.JPanel implements I_PanelEvent{

    //-----------------------------------------------------------------------------------------------------
    @Getter private CNCViewer baseFrame;
    @Getter private boolean selected=false;
    public abstract  String getName();
    public abstract  int modeMask();
    public abstract  boolean modeEnabled();
    public abstract void onActivate();
    public abstract void onDeactivate();
    public abstract void onClose();

    public boolean isSelectedMode(int mode){
        return ((modeMask() & mode)!=0) && modeEnabled();
        }
    public void setSelected(boolean bb){
        selected = bb;
        }
    protected ViewNotifyer notify;
    /**
     * Creates new form BasePanel
     */

    public Dimension createDim(Dimension src,int w, int h){
        if (src.width==0)
            return new Dimension(w,h);
        double scaleY = ((double) src.height)/Values.FrameHeight;
        double scaleX = ((double) src.width)/Values.FrameWidth;
        return new Dimension((int)(w*scaleX), (int)(h*scaleY));
        }
    public Rectangle createRec(Dimension src, int x0, int y0, int w, int h){
        if (src.width==0)
            return new Rectangle(x0,y0,w,h);
        double scaleY = ((double) src.height)/Values.FrameHeight;
        double scaleX = ((double) src.width)/Values.FrameWidth;
        return new Rectangle((int)(x0*scaleX), (int)(y0*scaleY),(int)(w*scaleX), (int)(h*scaleY));
        }
    public void setComponentsScale(){
        setComponentsScale(this);
        }
    public static void setComponentsScale(JFrame frame){
        setComponentsScale(frame.getContentPane().getComponents());
        frame.revalidate();
        }
    public static void setComponentsScale(JPanel panel){
        setComponentsScale(panel.getComponents());
        panel.revalidate();
        }
    public static void setComponentsScale(Component list[]){
        Dimension dim = WorkSpace.ws().getDim();
        if (dim.width==0)
            return;
        //if (dim.width!=0)
        //    panel.setBounds(0,0,dim.width,dim.height);
        double scaleY = WorkSpace.ws().getScaleY();
        double scaleX = WorkSpace.ws().getScaleX();
        for(Component component :  list){
            Rectangle rec = component.getBounds();
            rec.height = (int)(rec.height*scaleY);
            rec.width = (int)(rec.width*scaleX);
            rec.x = (int)(rec.x*scaleX);
            rec.y = (int)(rec.y*scaleY);
            component.setBounds(rec);
            //int fontSize = (int)(14. * scaleY);
            if (component instanceof JButton){
                JButton button = (JButton)component;
                int fontSize = button.getFont().getSize();
                int style = button.getFont().getStyle();
                button.setFont(new java.awt.Font("Segoe UI", style, (int)(fontSize*scaleY)));
                }
            if (component instanceof JCheckBox){
                JCheckBox button = (JCheckBox)component;
                int fontSize = button.getFont().getSize();
                int style = button.getFont().getStyle();
                button.setFont(new java.awt.Font("Segoe UI", style, (int)(fontSize*scaleY)));
                }
            if (component instanceof JTextField){
                JTextField button = (JTextField) component;
                int fontSize = button.getFont().getSize();
                int style = button.getFont().getStyle();
                button.setFont(new java.awt.Font("Segoe UI", style, (int)(fontSize*scaleY)));
                }
            if (component instanceof JComboBox){
                JComboBox button = (JComboBox) component;
                int fontSize = button.getFont().getSize();
                int style = button.getFont().getStyle();
                button.setFont(new java.awt.Font("Segoe UI", style, (int)(fontSize*scaleY)));
                }
            if (component instanceof JLabel){
                JLabel button = (JLabel) component;
                int fontSize = button.getFont().getSize();
                int style = button.getFont().getStyle();
                button.setFont(new java.awt.Font("Segoe UI", style, (int)(fontSize*scaleY)));
                }
            if (component instanceof TextArea){
                TextArea button = (TextArea) component;
                int fontSize = button.getFont().getSize();
                int style = button.getFont().getStyle();
                button.setFont(new java.awt.Font("Segoe UI", style, (int)(fontSize*scaleY)));
                }
            if (component instanceof JPasswordField){
                JPasswordField button = (JPasswordField) component;
                int fontSize = button.getFont().getSize();
                int style = button.getFont().getStyle();
                button.setFont(new java.awt.Font("Segoe UI", style, (int)(fontSize*scaleY)));
                }
            if (component instanceof JPanel){
                JPanel panel2 = (JPanel) component;
                panel2.setBounds(rec);
                }
            if (component instanceof JProgressBar){
                JProgressBar progress = (JProgressBar) component;
                progress.setBounds(rec);
                }
            //System.out.println(component);
            }
        }

    public void updateField(String label, JTextField field, FloatParameter par){
        updateField(label,field,par,1.0);
    }
    public void updateField(String label, JTextField field, FloatParameter par,double koeff){
        Dimension dim = WorkSpace.ws().getDim();
        DigitPanel digit = new DigitPanel(label, field, false, new I_RealValue() {
            @Override
            public void onEvent(String value) {
                field.setText(value);
                WorkSpace.ws().getNotify().notify(Values.info,"Изменен параметр: "+label+"="+value);
                par.setVal(Float.parseFloat(value)/koeff);
                WorkSpace.ws().saveSettings();
            }
        });
    }
    public void updateField(String label, JTextField field, IntParameter par){
        Dimension dim = WorkSpace.ws().getDim();
        DigitPanel digit = new DigitPanel(label, field, true, new I_RealValue() {
            @Override
            public void onEvent(String value) {
                field.setText(value);
                WorkSpace.ws().getNotify().notify(Values.info,"Изменен параметр: "+label+"="+value);
                par.setVal(Integer.parseInt(value));
                WorkSpace.ws().saveSettings();
            }
        });
    }
    public void updateField(String label, JTextField field, StringParameter par){
        KeyBoardPanel keyBoard = new KeyBoardPanel(label, field, false,new I_RealValue() {
            @Override
            public void onEvent(String value) {
                field.setText(value);
                WorkSpace.ws().getNotify().notify(Values.info,"Изменен параметр: "+label+"="+value);
                par.setVal(value);
                WorkSpace.ws().saveSettings();
            }
        });
    }


    public void updateField(String label, JCheckBoxButton field, BooleanParameter par){
        field.itemStateChanged();
        WorkSpace.ws().getNotify().notify(Values.info,"Изменен параметр: "+label+"="+field.isSelected());
        par.setVal(field.isSelected());
        WorkSpace.ws().saveSettings();
        }

    public BasePanel(CNCViewer baseFrame0) {
        initComponents();
        baseFrame = baseFrame0;
        notify = WorkSpace.ws().getNotify();
        Dimension dim = WorkSpace.ws().getDim();
        if (dim.width==0)
            setBounds(0, 0,Values.FrameWidth-Values.FrameMenuRightOffet,Values.FrameHeight-100);
        else
            setBounds(0, 0, dim.width-Values.FrameMenuRightOffet, dim.height-100);
        }
    public void sendEvent(int code, int par1, long par2, String par3,Object o){
        baseFrame.sendEvent(code,par1,par2,par3,o);
        }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(null);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
