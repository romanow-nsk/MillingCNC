/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epos.slm3d.io;

import epos.slm3d.utils.UNIException;

import java.io.*;

/**
 *
 * @author romanow
 */
public class BinOutputStream{
    private OutputStream dest;
    public BinOutputStream(OutputStream ff) {
        dest = ff;
        }
    public BinOutputStream(String name) throws FileNotFoundException {
        dest = new FileOutputStream(name);
        }    
    public BinOutputStream() {
        dest = null;
        }
    private int addr=0;
    public static int doubleToQ31(double vv0) throws UNIException {
        double vv=vv0;
        //if (vv==0) return 0x80000000;
        if (vv==0) return 0;
        boolean plus = (vv>0);
        if (!plus)
            vv=-vv;
        if (vv>=1)
            throw UNIException.format("Q31 больше 1");
        int out=0;
        for(int i=0;i<31;i++){
            vv*=2;
            int zz = (int)vv;
            vv -=zz;
            out = (out << 1) | zz;
            }
        if (plus)
            out = out;              // | 0x80000000;
        else{
            out = (-out & 0x7FFFFFFF) | 0x80000000;
           }
        return out;
        }
    public void writeQ31(double vv) throws UNIException{
        try {
            writeInt(BinOutputStream.doubleToQ31(vv));
            } catch (IOException ee){ throw UNIException.io(ee); }
        }
    public void writeFloat(float vv) throws IOException{
        writeInt(Float.floatToIntBits(vv));
        }
    public void writeDouble(double vv) throws IOException{
        long dd = Double.doubleToLongBits(vv);
        writeInt((int)dd);
        writeInt((int)(dd>>32));
        }
    public void writeInt(int vv) throws IOException{
        for(int i=0;i<4;i++){
            dest.write((byte)vv & 0x0FF);
            vv >>=8;
            }
        }
    public void writeInt(int vv[]) throws IOException{
        for(int xx:vv)
            writeInt(xx);
        }
    public void close(){
        try {
            dest.close();
            } catch (IOException e) {}
        }
    /*
    public Command get8000() throws IOException{
        for(int i=0;i<5;i++)
            readInt();
        return new Command(addr-4,0x8000,0,0);
        }
    public Command get(int code) throws IOException{
        return new Command(addr-4,code,q31ToDouble(readInt()),q31ToDouble(readInt()));
        }
    public void skip(int cnt) throws IOException{
        while(cnt--!=0)
            readInt();
        }
    public int findCommand() throws IOException{
        int vv=0;
        do  {
            vv = readInt();
            } while(!(vv==1 || vv==0x101));
        return vv;
        }
    private boolean found=false;
    public Command getNext() throws IOException{
        int code;
        if (found){
            code = readInt();
            if (code == 0 || code==1 || code == 0x101){
                return get(code);
                }
            else{
                if (code==0x08000)
                    return get8000();
                else{
                    System.out.println(code);
                    found = false;
                    }
                }
            }
        System.out.println("synch="+Integer.toHexString(addr));
        code = findCommand();
        found=true;
        return get(code);
        }
    public void procFile(OnCommand back) throws IOException{
        skip(0x160/4);
        while(true)
            back.onCommand(getNext());
        }
    public static void main(String argv[]) {
        BinOutputStream bb;
        try {
            bb = new BinOutputStream("mark.out");
            } catch(FileNotFoundException ee){ return; }
        System.out.println(bb.q31ToDouble(0x80000000));
        System.out.println(bb.q31ToDouble(0x80000001));
        System.out.println(bb.q31ToDouble(0x88000000));
        System.out.println(bb.q31ToDouble(0x84000000));
        System.out.println(bb.q31ToDouble(0xffffffff));
        System.out.println(bb.q31ToDouble(0x7fffffff));
        System.out.println(bb.q31ToDouble(0x70000001));
        System.out.println(bb.q31ToDouble(0x20000001));
        System.out.println(bb.q31ToDouble(0x00000001));
        /*
        try {
            bb.procFile(new OnCommand(){
                @Override
                public void onCommand(Command cmd) {
                    System.out.print(cmd);
                }        
            });
        } catch(IOException ee){}
        finally { try { bb.close(); } catch(Exception ee){}}
        }
        */
    }
