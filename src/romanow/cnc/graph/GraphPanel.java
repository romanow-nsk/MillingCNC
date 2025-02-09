/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.graph;

import romanow.cnc.settings.WorkSpace;
import romanow.cnc.stl.I_Line2D;
import romanow.cnc.stl.I_Point2D;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 *
 * @author romanow
 */
public class GraphPanel extends JPanel{
    public final static int MASOffset=8;
    public final static double MASPower=1.1;
    public int horiz=0;
    public int vert=0;
    public double xmin,xmax,ymin,ymax,x0,y0;// Координаты текущего положения панели в поле печати
    public double dxy;                      // Единиц модели в одном пикселе
    private Graphics gg;  
    public int mas=1;                       // Масштаб
    public int vSize;
    private I_Mouse mBack;                  // Интерфеейс обратного вызова событий мыши
    private boolean bold=false;             // Признак рисования bold
    private double paintSize;
    public void bold(boolean vv){ bold=vv; }
    public GraphPanel(I_Mouse mBack0){
        mBack = mBack0;
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent evt) {
                mBack.MouseMoved(evt);
                }
            public void mouseDragged(MouseEvent evt) {
                mBack.MouseDragged(evt);
                }
            });
        addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent evt) {
                mBack.MouseWheelMoved(evt);
                }
            });
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                mBack.MouseClicked(evt);
                }
            public void mousePressed(MouseEvent evt) {
                mBack.MousePressed(evt);
                }
            public void mouseReleased(MouseEvent evt) {
                mBack.MouseReleased(evt);
                }
            });        
        }
    public void setBounds(int x0,int y0, int dxy){
        super.setBounds(x0,y0,dxy,dxy);
        vSize = dxy;  
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        }
    public void setGraphics(){ gg = getGraphics(); }
    public void setPaintParams(JSlider HORIZ, JSlider VERTIC){
        setGraphics();
        //dxy = 2./((3+mas)/4.*vSize);
        paintSize = WorkSpace.ws().local().model.BlankWidth.getVal();
        double vy = WorkSpace.ws().local().model.BlankHight.getVal();
        if (vy > paintSize)
            paintSize = vy;
        horiz = (int)(HORIZ.getValue()*paintSize);
        vert = -(int)(VERTIC.getValue()*paintSize);
        dxy = paintSize*2./(Math.pow(MASPower,mas-MASOffset)*vSize);
        x0 = -horiz/100.;
        y0 = -vert/100.;
        xmin = x0 - dxy*vSize/2;
        xmax = x0 + dxy*vSize/2;
        ymin = y0 - dxy*vSize/2;
        ymax = y0 + dxy*vSize/2;
        gg.setColor(Color.white);
        gg.fillRect(0, 0, vSize-1, vSize-1);
        }
    public int xToPixel(double x){  return vSize/2+(int)((x-x0)/dxy); }
    public int yToPixel(double y){  return vSize/2+(int)((-y-y0)/dxy); }
    public int xSzToPixel(double vv){  return (int)(vv/dxy); }
    public int ySzToPixel(double vv){  return (int)(vv/dxy); }  
    public double pixelToX(int x){  return (x-vSize/2)*dxy+x0; }
    public double pixelToY(int y){  return -((y-vSize/2)*dxy+y0); }
    public void drawLine(Color color, I_Line2D vv){
        gg.setColor(color);
        int x1 = xToPixel(vv.x0());
        int y1 = yToPixel(vv.y0());
        int x2 = xToPixel(vv.x1());
        int y2 = yToPixel(vv.y1());
        gg.drawLine(x1,y1,x2,y2);
        if (bold){
            gg.drawLine(x1+1,y1,x2+1,y2);
            gg.drawLine(x1,y1+1,x2,y2+1);
            }
        }
    public int xMMToPixel(double v){
        return xToPixel(v);
        }
    public int yMMToPixel(double v){
        return yToPixel(v);
        }
    public void  paintGrid(Color gridColor){
        gg.setColor(Color.red);
        double dd = WorkSpace.ws().global().mashine.WorkFrameX.getVal();
        int sz = (int)dd/2;
        int x1=xMMToPixel(-sz);
        int x2=xMMToPixel(sz);
        int y1=yMMToPixel(-sz);
        int y2=yMMToPixel(sz);
        gg.drawLine(x1,y1,x2,y1);
        gg.drawLine(x1,y2,x2,y2);
        gg.drawLine(x1,y1,x1,y2);
        gg.drawLine(x2,y1,x2,y2);
        for(int x=-sz;x<=sz;x+=1){
            int xx = xMMToPixel(x);
            if (xx<0 || xx>=vSize) continue;
            gg.setColor(x%10==0 ? Color.red : gridColor);
            gg.drawLine(xx,y1,xx,y2);
            }
        for(int y=-sz;y<=sz;y+=1){
            int yy = yMMToPixel(y);
            if (yy<0 || yy>=vSize) continue;
            gg.setColor(y%10==0 ? Color.red : gridColor);
            gg.drawLine(x1,yy,x2,yy);
            }
        }
    public void drawLineNoBold(Color color, I_Line2D vv){
        int x1 = xToPixel(vv.x0());
        int y1 = yToPixel(vv.y0());
        int x2 = xToPixel(vv.x1());
        int y2 = yToPixel(vv.y1());
        gg.setColor(Color.white);
        gg.drawLine(x1+1,y1,x2+1,y2);
        gg.drawLine(x1,y1+1,x2,y2+1);
        gg.setColor(color);
        gg.drawLine(x1,y1,x2,y2);
        }
    public void drawPoint(Color color, I_Point2D vv){
        gg.setColor(color);
        gg.drawOval(xToPixel(vv.x())-3, yToPixel(vv.y())-3,5,5);
        }
    public void fillOval(Color color, GraphPoint vv, int size){
        gg.setColor(color);
        int x1 = xToPixel(vv.x());
        int y1 = yToPixel(vv.y());
        gg.fillOval(x1-size/2, y1-size/2 ,size, size);
        }
    public void drawOval(Color color, GraphObject vv){
        gg.setColor(color);
        int x1 = xToPixel(vv.x0());
        int y1 = yToPixel(vv.y1());
        int sz1 = xSzToPixel(vv.szX());
        int sz2 = ySzToPixel(vv.szY());
        gg.drawOval(x1, y1 ,sz1, sz2);
        if (bold){
            gg.drawOval(x1+1, y1 ,sz1, sz2);
            gg.drawOval(x1, y1+1 ,sz1, sz2);           
            }
        }
    public void drawArc(Color color, GraphObject vv, double angle0, double angle1){
        int a1 = (int)(180*angle0/Math.PI);
        int a2 = (int)(180*angle1/Math.PI);
        gg.setColor(color);
        int x1 = xToPixel(vv.x0());
        int y1 = yToPixel(vv.y1());
        int sz1 = xSzToPixel(vv.szX());
        int sz2 = ySzToPixel(vv.szY());
        gg.drawArc(x1, y1 ,sz1, sz2,a1,a2);
        if (bold){
            gg.drawArc(x1+1, y1 ,sz1, sz2,a1,a2);
            gg.drawArc(x1, y1+1 ,sz1, sz2,a1,a2);
        }
    }
    public void drawKreuz(Color color, I_Point2D vv){
        gg.setColor(color);
        int xx = xToPixel(vv.x());
        int yy = yToPixel(vv.y());
        gg.drawLine(xx-3,yy-3,xx+3,yy+3);
        gg.drawLine(xx-3,yy+3,xx+3,yy-3);
        if (bold){
            gg.drawLine(xx-2,yy-3,xx+4,yy+3);
            gg.drawLine(xx-3,yy-2,xx+3,yy+4);
            gg.drawLine(xx-2,yy+3,xx+4,yy-3);            }
            gg.drawLine(xx-3,yy+2,xx+3,yy-4);
            }
    public void drawLineBold(Color color, I_Line2D vv){
        gg.setColor(color);
        int x1 = xToPixel(vv.x0());
        int y1 = yToPixel(vv.y0());
        int x2 = xToPixel(vv.x1());
        int y2 = yToPixel(vv.y1());
        gg.drawLine(x1,y1,x2,y2);
        gg.drawLine(x1+1,y1,x2+1,y2);
        gg.drawLine(x1,y1+1,x2,y2+1);
        }
    public void drawPointBold(Color color, I_Point2D vv){
        gg.setColor(color);
        int x1 = xToPixel(vv.x());
        int y1 = yToPixel(vv.y());
        gg.drawLine(x1-5, y1, x1+5, y1);
        gg.drawLine(x1, y1-5, x1, y1+5);
        //gg.drawOval(x1, y1, 4, 4);
        //gg.drawOval(x1, y1, 3, 3);
        //gg.drawOval(x1, y1, 2, 2);
        }
    public void clear(){
        gg.setColor(Color.white);
        gg.fillRect(0, 0, vSize-1, vSize-1);
        }
    public void setColor(Color color){ gg.setColor(color); }
    }
