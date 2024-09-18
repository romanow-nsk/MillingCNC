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
import java.awt.Color;

/**
 *
 * @author romanow
 */
public class O2DRectangle extends GraphObject{
    public O2DRectangle(){}
    public O2DRectangle(GraphPoint p00, GraphPoint p01) {
        super(p00, p01);
        }
    public O2DRectangle(double x0, double y0, double x1, double y1) {
        super(new GraphPoint(x0,y0),new GraphPoint(x1,y1));       
        }
    
    public void paint(Color color,GraphPanel panel,boolean mid){
        super.paint(color, panel,mid);
        panel.drawLine(color, new GraphLine(x0(),y0(),x1(),y0()));
        panel.drawLine(color, new GraphLine(x0(),y1(),x1(),y1()));
        panel.drawLine(color, new GraphLine(x0(),y0(),x0(),y1()));
        panel.drawLine(color, new GraphLine(x1(),y0(),x1(),y1())); 
        }

    public String name(){ return "Прямоугольник"; }
    public GraphObject clone(){ return new O2DRectangle(x0(),y0(),x1(),y1());}

    @Override
    public SliceLayer createPrintData() {
        SliceLayer out =  super.createPrintData(); 
        STLLineGroup ss = out.segments();
        STLPoint2D p1 = new STLPoint2D(x0(),y0());
        STLPoint2D p2 = new STLPoint2D(x1(),y0());
        STLPoint2D p3 = new STLPoint2D(x1(),y1()); 
        STLPoint2D p4 = new STLPoint2D(x0(),y1());
        ss.add(new STLLine(p1,p2));
        ss.add(new STLLine(p2,p3));
        ss.add(new STLLine(p3,p4));
        ss.add(new STLLine(p4,p1));
        return out;
    }
    
}
