/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epos.slm3d.graph;

import epos.slm3d.stl.I_Line2D;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class GraphLine implements I_Line2D{
    private GraphPoint p0 = new GraphPoint();
    private GraphPoint p1 = new GraphPoint();
    public GraphLine(){}
    public GraphLine(GraphPoint p00, GraphPoint p01){ p0=p00; p1=p01; }
    public GraphLine(double x0, double y0, double x1, double y1){ 
        p0 = new GraphPoint(x0,y0); 
        p1 = new GraphPoint(x1,y1);
        }
    public double x0(){ return p0.x(); }
    public double x1(){ return p1.x(); }
    public double y0(){ return p0.y(); }
    public double y1(){ return p1.y(); }
    public void x0(double vv){ p0.x(vv); }
    public void y0(double vv){ p0.y(vv); }
    public void x1(double vv){ p1.x(vv); }
    public void y1(double vv){ p1.y(vv); }
    @Override
    public void load(DataInputStream in) throws IOException {
        p0.load(in);
        p1.load(in);
        }
    @Override
    public void save(DataOutputStream out) throws IOException {
        p0.save(out);
        p1.save(out);
        }
    public void shift(double dx, double dy){
        p0.shift(dx, dy);
        p1.shift(dx, dy);
        }
}
