package romanow.cnc.view;

import romanow.cnc.Values;
import romanow.cnc.settings.WorkSpace;

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
        WorkSpace ws = WorkSpace.ws();
        winWidth=(int)(ww*ws.getScaleX());
        winHigh=(int)(hh*ws.getScaleY());
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
    public void positionOn(JTextField field, int x0, int y0, boolean dialog){
        Dimension dim = WorkSpace.ws().getDim();
        if (dim.width==0){
                int yy =  Values.FrameY0+y0+field.getY();
                if (yy<50)
                    yy=50;
            setBounds(Values.FrameX0+x0+field.getX(),yy,winWidth,winHigh);
            }
        else{
            //double scaleY = ((double)dim.height)/Values.FrameHeight;
            //double scaleX = ((double) dim.width)/Values.FrameWidth;
            //setBounds((int)(field.getX()*scaleX),(int)(field.getY()*scaleY),winWidth,winHigh);
            int yy = field.getY()+y0;
            if (yy < Values.FrameTop)
                yy = Values.FrameTop;
            int xx = field.getX()+(int)(x0*WorkSpace.ws().getScaleX());
            if (xx<10)
                xx=field.getX()+(int)(50*WorkSpace.ws().getScaleX());
            setBounds(xx,yy,winWidth,winHigh);
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
