/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epos.slm3d.graph;

import epos.slm3d.slicer.SliceLayer;
import epos.slm3d.stl.STLLine;
import epos.slm3d.stl.STLLineGroup;
import epos.slm3d.stl.STLPoint2D;
import epos.slm3d.utils.Values;
import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author romanow
 */
public class O2DGrid extends GraphObject{
    private double step=1./(Values.PrinterFieldSize/2);
    public O2DGrid(){}
    public O2DGrid(GraphPoint p00, GraphPoint p01) {
        super(p00, p01);
        }
    //-------------- Обязательно x0 < x1 ------------------------
    public O2DGrid(double x0, double y0, double x1, double y1) {
        super(new GraphPoint(x0,y0),new GraphPoint(x1,y1));
        }
    public O2DGrid(double x0, double y0, double x1, double y1, double par) {
        super(new GraphPoint(x0,y0),new GraphPoint(x1,y1));
        if (par==0)
            par = 1/(Values.PrinterFieldSize/2);
        step = par;
        }
    @Override
    public void setParam1(double vv){ 
        if (vv==0) vv=1;
        step = vv/(Values.PrinterFieldSize/2); 
        }   
    @Override
    public double getParam1(){ return step*((Values.PrinterFieldSize/2)); }    
    
    public void paint(Color color,GraphPanel panel,boolean mid){
        super.paint(color, panel,mid);
        int nx = (int)(szX()/step)+1;
        int ny = (int)(szY()/step)+1;
        double vv;
        int i;
        for(vv=x0();vv<=x1()+Values.PointDiffenerce;vv+=step)
            panel.drawLine(color, new GraphLine(vv,y0(),vv,y1()));
        for(vv=y0();vv<y1()+Values.PointDiffenerce;vv+=step)
            panel.drawLine(color, new GraphLine(x0(),vv,x1(),vv));
        }

    public String name(){ return "Сетка"; }
    public GraphObject clone(){ return new O2DGrid(x0(),y0(),x1(),y1(),step);}

    @Override
    public void save(DataOutputStream out) throws IOException {
        super.save(out); 
        out.writeDouble(step);
        }
    @Override
    public void load(DataInputStream in) throws IOException {
        super.load(in); 
        step = in.readDouble();
        }
    @Override
    public SliceLayer createPrintData() {
        SliceLayer out =  super.createPrintData(); 
        STLLineGroup ss = out.segments();
        int nx = (int)(szX()/step)+1;
        int ny = (int)(szY()/step)+1;
        double vv;
        int i;
        for(vv=x0();vv<x1()+Values.PointDiffenerce;vv+=step)
            ss.add(new STLLine(new STLPoint2D(vv,y0()),new STLPoint2D(vv,y1())));
        for(vv=y0();vv<y1()+Values.PointDiffenerce;vv+=step)
            ss.add(new STLLine(new STLPoint2D(x0(),vv),new STLPoint2D(x1(),vv)));
        return out;
        }   
}
