/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.graph;

import romanow.cnc.slicer.SliceLayer;
import romanow.cnc.stl.STLLine;
import romanow.cnc.stl.STLLineGroup;
import romanow.cnc.stl.STLPoint2D;
import romanow.cnc.Values;

import java.awt.Color;

/**
 *
 * @author romanow
 */
public class O2DOval extends GraphObject{
    public O2DOval(){}
    public O2DOval(GraphPoint p00, GraphPoint p01) {
        super(p00, p01);
        }
    public O2DOval(double x0, double y0, double x1, double y1) {
        super(new GraphPoint(x0,y0),new GraphPoint(x1,y1));       
        }
    
    public void paint(Color color,GraphPanel panel, boolean mid){
        super.paint(color,panel,mid);
        panel.drawOval(color, this);
        }
    public String name(){ return "Овал"; }
    public GraphObject clone(){ return new O2DOval(x0(),y0(),x1(),y1());}
    
    @Override
    public SliceLayer createPrintData() {
        SliceLayer out =  super.createPrintData(); 
        STLLineGroup ss = out.segments();
        double df = Math.PI*2/ Values.EllipseLineCount;
        double midX = midX();
        double midY = midY();
        double szX = szX()/2;
        double szY = szY()/2;
        STLPoint2D p00 = new STLPoint2D(midX+szX,midY);
        STLPoint2D p0 = p00;        
        STLPoint2D p1 = null;
        double fi = df;
        for(int i=1; i<Values.EllipseLineCount;i++){
            p1 = new STLPoint2D(midX+szX*Math.cos(fi),midY+szY*Math.sin(fi)); 
            ss.add(new STLLine(p0,p1));
            fi+=df;
            p0 = p1;
            }
        ss.add(new STLLine(p1,p00));
        return out;
        }   

    
}
