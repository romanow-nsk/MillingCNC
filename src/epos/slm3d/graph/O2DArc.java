/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epos.slm3d.graph;

import epos.slm3d.slicer.SliceLayer;
import epos.slm3d.stl.I_Point2D;
import epos.slm3d.stl.STLLine;
import epos.slm3d.stl.STLLineGroup;
import epos.slm3d.stl.STLPoint2D;
import epos.slm3d.utils.Values;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author romanow
 */
public class O2DArc extends GraphObject{
    public O2DArc(){}
    public O2DArc(GraphPoint p00, GraphPoint p01) {
        super(p00, p01);
        }
    public O2DArc(double x0, double y0, double x1, double y1) {
        super(new GraphPoint(x0,y0),new GraphPoint(x1,y1));       
        }
    private double angle0=0,angle1=Math.PI*2;
    public void paint(Color color,GraphPanel panel, boolean mid){
        super.paint(color,panel,mid);
        panel.drawArc(color, this,angle0,angle1);
        }

    @Override
    public void setParam1(double vv) {
        angle0= vv/180*Math.PI;
        }
    @Override
    public void setParam2(double vv) {
        angle1=vv/180*Math.PI;;
        }
    @Override
    public double getParam1(){ return angle0*180/Math.PI; }    
    @Override
    public double getParam2(){ return angle1*180/Math.PI; }    
    
    public String name(){ return "Дуга"; }
    public GraphObject clone(){
        O2DArc pp =  new O2DArc(x0(),y0(),x1(),y1());
        pp.angle0 = angle0;
        pp.angle1 = angle1;
        return pp;
        }
    
    @Override
    public SliceLayer createPrintData() {
        SliceLayer out =  super.createPrintData(); 
        STLLineGroup ss = out.segments();
        double midX = midX();
        double midY = midY();
        double szX = szX()/2;
        double szY = szY()/2;
        int np = (int)((angle1-angle0)/(2*Math.PI)*Values.EllipseLineCount);
        double df = Math.PI*2/Values.EllipseLineCount;
        double fi = angle0;
        STLPoint2D p00 = new STLPoint2D(midX+szX*Math.cos(fi),midY+szY*Math.sin(fi));
        STLPoint2D p0 = p00;        
        STLPoint2D p1 = null;
        fi+=df;
        for(int i=1; i<np+1;i++){
            p1 = new STLPoint2D(midX+szX*Math.cos(fi),midY+szY*Math.sin(fi)); 
            ss.add(new STLLine(p0,p1));
            fi+=df;
            p0 = p1;
            }
        return out;
        }
    @Override
    public void save(DataOutputStream out) throws IOException {
        super.save(out);
        out.writeDouble(angle0);
        out.writeDouble(angle1);
    }
    @Override
    public void load(DataInputStream in) throws IOException {
        super.load(in);
        angle0 = in.readDouble();
        angle1 = in.readDouble();
    }
    
}
