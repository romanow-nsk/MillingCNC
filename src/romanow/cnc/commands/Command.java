/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.commands;

import romanow.cnc.controller.DataBuffer;
import romanow.cnc.io.BinInputStream;
import romanow.cnc.stl.STLLine;
import romanow.cnc.stl.STLPoint2D;
import romanow.cnc.utils.UNIException;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author romanow
 */
public class Command {
    public final static int SIGN_NODATA=0;
    public final static int SIGN_INT=1;
    public final static int SIGN_FLOAT=2;
    public final static int SIGN_POINT=3;
    public final static int SIGN_LINE=4;
    public final static int SIGN_INTLIST=5;
    int code;
    String name="Не определена";            // Только в текущем пакете
    int signature=SIGN_NODATA;
    int dataSize=0;                         // Количество параметров - если переменное
    int idx=0;                              // индекс для команды - параметра
    public int idx(){ return idx; }
    public ArrayList<Integer> src = new ArrayList();
    public Command(int cc){
        code = cc;
        }
    public Command(){
        code = 0;
        }
    public Command(Command proto){
        code = proto.code;
        name = proto.name;
        signature = proto.signature;
        idx = proto.idx;
        }
    public int code(){ return code; }
    public int signature(){ return signature; }
    public String name(){ return name; }
    public void value(int vv){}
    public void value(float vv){}
    public void value(STLPoint2D point){}
    public void value(STLLine line){}
    public Command(int cc,String name0){
        code = cc; name = name0;
        }
    public int dataSize(){ return dataSize; }
    public void name(String name0){ name = name0; }
    public String toString(){
        return name()+"["+Integer.toHexString(code)+"] ";
        }
    public String toView(){ return name()+" "; }
    public void load(BinInputStream in)throws IOException{}
    public void loadSTD(BinInputStream in)throws IOException{
        code = in.readInt();
        }
    public boolean canFind(){ return false; }
    public boolean isImportant(){ return false; }
    public int byteSize(){ return 0; }
    public int []CreateBynary() throws UNIException { return new int[0]; }
    public void init(){
        src.add(code);
        }
    public String toDump(){
        String out = String.format("%08X %s\n",src.get(0),toView());
        for(int i=1;i<src.size();i++){
            out+=String.format("%08X\n",src.get(i));
        }
        return out;
        }
    public int wordSize(){ return 1; }
    public int []toIntArray(){
        int out[]=new int[1];
        out[0]=code;
        return out;
        }
    public boolean canPut(DataBuffer out){
        return out.canPut(1);
        }
    public void toDataBuffer(DataBuffer out){
        out.putInt(code);
        }
    }
