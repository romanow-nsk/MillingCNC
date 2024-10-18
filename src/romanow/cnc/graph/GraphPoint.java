/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.graph;

import romanow.cnc.stl.I_Point2D;
import romanow.cnc.stl.STLPoint2D;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GraphPoint implements I_Point2D {
    private  double x=0,y=0;
    public GraphPoint(double x0, double y0){ x=x0; y=y0; }
    public GraphPoint(){}
    @Override
    public double x() { return x; }
    @Override
    public double y() { return y; }

    @Override
    public void x(double vv) { x = vv; }
    @Override
    public void y(double vv) { y = vv; }
    @Override
    public void load(DataInputStream in) throws IOException {
        x = in.readDouble();
        y = in.readDouble();
        }
    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeDouble(x);
        out.writeDouble(y);
        }
    public void shift(double dx, double dy){
        x+=dx; y+=dy;
        }
    public double diffXY(GraphPoint vv){
        double dx = x - vv.x;
        double dy = y - vv.y;
        return Math.sqrt(dx*dx+dy*dy);
        }
    public STLPoint2D toSTLPoint(){
        return new STLPoint2D(x,y);
        }
} 
