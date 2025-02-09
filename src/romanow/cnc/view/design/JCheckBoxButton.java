package romanow.cnc.view.design;

import lombok.Getter;

import javax.swing.*;

public class JCheckBoxButton {
    private JButton button;
    @Getter private boolean selected=false;
    public JCheckBoxButton(JButton src){
        button = src;
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        setSelected(false);
        }
    public void setSelected(boolean sel){
        selected = sel;
        button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/toggle-"+(selected ? "on" : "off")+"-48.png")));
        button.revalidate();
        }
    public void itemStateChanged(){
        selected = !selected;
        button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/drawable-mdpi/toggle-"+(selected ? "on" : "off")+"-48.png")));
        button.revalidate();
        }
    public void setVisible(boolean bb){
        button.setVisible(bb);
        }
}
