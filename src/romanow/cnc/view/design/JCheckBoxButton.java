package romanow.cnc.view.design;

import lombok.Getter;
import romanow.cnc.Values;
import romanow.cnc.view.BasePanel;

import javax.swing.*;

public class JCheckBoxButton {
    private JButton button;
    @Getter private boolean selected=false;
    private String stateOffIcon = "toggle-off-48";
    private String stateOnIcon = "toggle-on-48";
    public JCheckBoxButton(JButton src, String off, String on){
        this(src);
        stateOffIcon = off;
        stateOnIcon = on;
        }
    public JCheckBoxButton(JButton src){
        button = src;
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        setSelected(false);
        }
    public void setEnabled(boolean sel){
        button.setEnabled(sel);
        }
    public void setSelected(boolean sel){
        selected = sel;
        button.setIcon(new javax.swing.ImageIcon(getClass().getResource(Values.mdpi+(selected ? stateOnIcon : stateOffIcon)+".png")));
        BasePanel.resizeIcon(button);
        button.revalidate();
        }
    public void itemStateChanged(){
        selected = !selected;
        button.setIcon(new javax.swing.ImageIcon(getClass().getResource(Values.mdpi+(selected ? stateOnIcon : stateOffIcon)+".png")));
        BasePanel.resizeIcon(button);
        button.revalidate();
        }
    public void setVisible(boolean bb){
        button.setVisible(bb);
        }
}
