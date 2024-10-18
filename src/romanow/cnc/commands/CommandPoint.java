/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.commands;

import romanow.cnc.controller.DataBuffer;
import romanow.cnc.io.BinInputStream;
import romanow.cnc.io.BinOutputStream;
import romanow.cnc.stl.STLPoint2D;
import romanow.cnc.utils.UNIException;

import java.io.IOException;

/**
 *
 * @author romanow
 */
public class CommandPoint extends PrintCommand {
    double x,y;
    public CommandPoint(int code, double x0, double y0){
        super(code);
        x = x0; y = y0;
        }
    public CommandPoint(int code, STLPoint2D point){
        super(code);
        x = point.x(); y = point.y();
        }
    public double x(){ return x; }
    public double y(){ return y; }
    public CommandPoint(int code){
        super(code);
        signature = SIGN_POINT;
        name = "Точка";
        }
    public CommandPoint(){
        super();
        signature = SIGN_POINT;
        name = "Точка";
        }
    @Override
    public int byteSize() {
        return 3*4;
        }
    public String toString(){
        return super.toString() + String.format("%5.3f %5.3f",x,y);
        }
    @Override
    public void value(STLPoint2D point){ x = point.x(); y = point.y(); }
    @Override
    public int[] CreateBynary() throws UNIException {
        int out[] = new int[3];
        out[0]=code;
        out[1]= BinOutputStream.doubleToQ31(x);
        out[2]= BinOutputStream.doubleToQ31(y);
        return out;
        }

    @Override
    public void load(BinInputStream in) throws IOException {
        src.add(code);
        int x1 = in.readInt();
        src.add(x1);
        int y1 = in.readInt();
        src.add(y1);
        x = BinInputStream.q31ToDouble(x1);
        y = BinInputStream.q31ToDouble(y1);
        }
    public String toView(){ return super.toView() + " "+(int)(x*1000)+","+(int)(y*1000); }
    public int wordSize(){ return 5; }
    public int []toIntArray(){
        int out[]=new int[5];
        out[0]=code;
        long vv = Double.doubleToLongBits(x);
        out[1]=(int)vv;
        out[2]=(int)(vv>>32);
        vv = Double.doubleToLongBits(y);
        out[3]=(int)vv;
        out[4]=(int)(vv>>32);
        return out;
        }
    public boolean canFind(){ return true; }
    public boolean canPut(DataBuffer out){
        return out.canPut(1);
    }
    public void toDataBuffer(DataBuffer out){
        super.toDataBuffer(out);
        out.putDouble(x);
        out.putDouble(y);
        }
    public void loadSTD(BinInputStream in)throws IOException{
        x = in.readDouble();
        y = in.readDouble();
    }
}
