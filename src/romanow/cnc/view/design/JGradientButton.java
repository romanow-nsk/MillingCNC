package romanow.cnc.view.design;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

public class JGradientButton extends JButton {
    private String title;
    private double colorCoeff = 0.7;
    private int blackSize=4;
    @Getter private Color gradientColor = Color.WHITE;
    public JGradientButton(String tt){
        super("");
        title = tt;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorder(javax.swing.BorderFactory.createLineBorder(Color.gray));
    }
    @Override
    public void setBackground(Color color){
        super.setBackground(color);
        gradientColor = color;
    }
    @Override
    public void paintComponent(Graphics g){
        //System.out.println(title);
        String text = getText();
        Graphics2D g2 = (Graphics2D)g.create();
        Color color1 = new Color(
                (int)(gradientColor.getRed()*colorCoeff),
                (int)(gradientColor.getGreen()*colorCoeff),
                (int)(gradientColor.getBlue()*colorCoeff));
        int hh = getHeight();
        int ww = getWidth();
        g2.setPaint(new GradientPaint(
                new Point(0, 0), color1, new Point(0, hh/blackSize), gradientColor));
        g2.fillRect(0, 0, ww, hh);
        g2.dispose();
        super.paintComponent(g);
    }

}
