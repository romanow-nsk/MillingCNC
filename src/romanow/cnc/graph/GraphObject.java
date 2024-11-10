/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.graph;

import romanow.cnc.io.I_File;
import romanow.cnc.settings.Settings;
import romanow.cnc.settings.WorkSpace;
import romanow.cnc.slicer.SliceLayer;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author romanow
 */
public class GraphObject extends GraphLine implements I_File {
    /** локальные параметры  прожига */
    private Settings printSettings=null;
    //----------------------------------------------------------------------------------
    public Settings printSettings(){ return printSettings; }
    public void printSettings(Settings set0 ){ printSettings=set0; }
    public GraphObject(GraphPoint p00, GraphPoint p01) {
        super(p00, p01);
        }
    public GraphObject(double x0, double y0, double x1, double y1) {
        super(new GraphPoint(x0,y0),new GraphPoint(x1,y1));
        }
    public GraphObject(){}
    public void paint(Color color,GraphPanel panel, boolean mid){
        //panel.drawLine(color, new GraphLine(x0(),y0(),x1(),y0()));
        //panel.drawLine(color, new GraphLine(x0(),y1(),x1(),y1()));
        //panel.drawLine(color, new GraphLine(x0(),y0(),x0(),y1()));
        //panel.drawLine(color, new GraphLine(x1(),y0(),x1(),y1())); 
        if (!mid) return;
        panel.drawKreuz(color, new GraphPoint(midX(),midY()));
        }
    public double midX(){ return (x0()+x1())/2; }
    public double midY(){ return (y0()+y1())/2; }
    public double szX(){ return (x1()-x0()); }
    public double szY(){ return (y1()-y0()); }   
    public String toString(){ return name()+String.format(" [%-6.3f,%-6.3f]",midX(),midY()); }
    public String name(){ return "..."; }
    public GraphObject clone(){ return new GraphObject(x0(),y0(),x1(),y1());}
    public GraphPoint middle(){ return new GraphPoint(midX(),midY()); }
    public void setParam1(double vv){}
    public void setParam2(double vv){}
    public double getParam1(){ return 0; }
    public double getParam2(){ return 0; }
    @Override
    public void save(DataOutputStream out) throws IOException {
        super.save(out);
        out.writeBoolean(printSettings!=null);    
        if (printSettings!=null)
            WorkSpace.ws().saveSettings(out, printSettings);
        }
    @Override
    public void load(DataInputStream in) throws IOException {
        super.load(in); 
        printSettings = null;
        if (in.readBoolean())
            printSettings = WorkSpace.ws().loadSettings(in);
        }  
    public SliceLayer createPrintData(){
        SliceLayer out =  new SliceLayer();
        out.printSettings(printSettings);
        return out;
        }
}
