package romanow.cnc.view;

import romanow.cnc.Values;

import javax.swing.*;
import java.awt.*;

public class BasePopupDialog extends javax.swing.JFrame {
    protected int winHigh=250;
    protected int winWidth=280;
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.BLACK);
        g.drawRect(2,2,winWidth-5,winHigh-5);
        }
    public BasePopupDialog(int ww, int hh){
        winWidth=ww;
        winHigh=hh;
        setUndecorated(true);
        }
    public BasePopupDialog(Dimension dim, int ww, int hh){
        double scaleY = dim.width==0 ? 1 : ((double) dim.height)/Values.FrameHeight;
        double scaleX = dim.width==0 ? 1 : ((double) dim.width)/Values.FrameWidth;
        winWidth=(int)(ww*scaleX);
        winHigh=(int)(hh*scaleY);
        setUndecorated(true);
        }
    public void setWH(int ww, int hh){
        winWidth=ww;
        winHigh=hh;
        }
    public void positionOn(int x0, int y0){
        setBounds(x0,y0,winWidth,winHigh);
        setVisible(true);
        }
    public void positionOn(JTextField field, Dimension dim, int x0, int y0, boolean dialog){
        if (dim.width==0){
            setBounds(Values.FrameX0+x0+field.getX(),Values.FrameY0+y0+field.getY(),winWidth,winHigh);
            }
        else{
            //double scaleY = ((double)dim.height)/Values.FrameHeight;
            //double scaleX = ((double) dim.width)/Values.FrameWidth;
            //setBounds((int)(field.getX()*scaleX),(int)(field.getY()*scaleY),winWidth,winHigh);
            setBounds(field.getX()+x0,field.getY()+y0,winWidth,winHigh);
            }
        if (!dialog)
            setUndecorated(true);
        }
    private Thread thread=null;
    public void delayIt(){
        delayIt(Values.PopupMessageDelay);
        }
    public void delayIt(final  int delay){
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay*1000);
                } catch (InterruptedException e) {
                    return;
                }
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        dispose();
                        }
                    });
                }
            });
            thread.start();
        }
    public void closeView(){
        if(thread!=null){
            thread.interrupt();
            thread=null;
            }
        dispose();
        }
    public void retryLongDelay(){
        if(thread!=null){
            thread.interrupt();
            thread=null;
            }
        delayIt(Values.PopupLongDelay);
        }
}
