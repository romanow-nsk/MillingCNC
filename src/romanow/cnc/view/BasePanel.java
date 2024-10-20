/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package romanow.cnc.view;

import lombok.Setter;
import romanow.cnc.Values;
import lombok.Getter;
import romanow.cnc.m3d.I_PanelEvent;

/**
 *
 * @author Admin
 */
public abstract class BasePanel extends javax.swing.JPanel implements I_PanelEvent{

    //-----------------------------------------------------------------------------------------------------
    @Getter private BaseFrame baseFrame;
    @Getter @Setter private boolean selected;
    public abstract  String getName();
    public abstract  int modeMask();
    public abstract  boolean modeEnabled();
    public abstract void onInit(boolean on);
    public abstract void onClose();

    public boolean isSelectedMode(int mode){
        return ((modeMask() & mode)!=0) && modeEnabled();
        }

    /**
     * Creates new form BasePanel
     */
    public BasePanel(BaseFrame baseFrame0) {
        initComponents();
        baseFrame = baseFrame0;
        setBounds(0, 0,Values.FrameWidth,Values.FrameHeight-100);
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
