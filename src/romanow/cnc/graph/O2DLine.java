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

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author romanow
 */
public class O2DLine extends GraphObject{
    private boolean down=false;
    public O2DLine(){}
    public O2DLine(GraphPoint p00, GraphPoint p01) {
        super(p00, p01);
        }
    //-------------- Обязательно x0 < x1 ------------------------
    public O2DLine(double x0, double y0, double x1, double y1) {
        super(new GraphPoint(x0,y0 < y1 ? y0 : y1),new GraphPoint(x1,y0 < y1 ? y1 : y0));
        down = y0 > y1;
        }
    public double y0(){ return down ? super.y1() : super.y0(); }
    public double y1(){ return down ? super.y0() : super.y1(); }
    public void x0(double vv){
        super.x0(vv);
        correct();
        }
    public void x1(double vv){
        super.x1(vv);
        correct();
        }
    public void y0(double vv){
        super.y0(vv);
        correct();
        }
    public void y1(double vv){
        super.y1(vv);
        correct();
        }
    private void correct(){
        if (x0()>=x1()){
            double cc = super.x0(); super.x0(super.x1()); super.x1(cc);
            cc = super.y0(); super.y0(super.y1()); super.y1(cc);
            }
        down = y1() < y0();
        }
    public void paint(Color color,GraphPanel panel,boolean mid){
        super.paint(color, panel,mid);
        panel.drawLine(color, new GraphLine(x0(),down ? y1() : y0(),x1(),down ? y0() : y1()));
        }

    public String name(){ return "Линия"; }
    public GraphObject clone(){ return new O2DLine(x0(),y0(),x1(),y1());}

    @Override
    public void save(DataOutputStream out) throws IOException {
        super.save(out); 
        out.writeBoolean(down);
        }
    @Override
    public void load(DataInputStream in) throws IOException {
        super.load(in); 
        down = in.readBoolean();
        }
    @Override
    public SliceLayer createPrintData() {
        SliceLayer out =  super.createPrintData(); 
        STLLineGroup ss = out.segments();
        STLPoint2D p1 = new STLPoint2D(x0(),down ? y1() : y0());
        STLPoint2D p2 = new STLPoint2D(x1(),down ? y0() : y1());
        ss.add(new STLLine(p1,p2));
        return out;
        }   
}
