/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.io;

import java.io.*;

/**
    Кодировка Q31 - вещественное, веса разрядов 2^(-31+n), по убыванию от старшего
    [31] - Знаковый, 1 = полож, 0 = отриц
    0x80000000 - 0
    0x00000001 - 0.0001
    0x7FFFFFFF - 0.9999
    0x80000001 - -0.9999
    0xFFFFFFFF - -0.0001
 */
public class BinInputStream {
    private InputStream dest;
    public BinInputStream(InputStream ff) {
        dest = ff;
        }
    public BinInputStream() {
        dest = null;
        }
    public double q31ToDouble() throws IOException{
        return q31ToDouble(readInt());
        }
    public static double q31ToDouble(int vv){
        if (vv==0x80000000)
            return 0;
        int dd = convertInt(vv);
        boolean plus = (vv>0);
        if (!plus)
            dd = ~dd+1;
        double pp = 1/2.;
        double out=0;
        for(int i=0;i<31;i++){
            dd<<=1;
            if (dd < 0 )
                out+=pp;
                pp/=2;
            }
        if (!plus)
            out = -out;
        return out;
        }
    public float readFloat()  throws IOException{
        int vv = readInt();
        return Float.intBitsToFloat(vv);
        }
    public double readDouble()  throws IOException{
        long vv1 = ((long)readInt()) & 0x00000000FFFFFFFFL;
        long vv = ((long)readInt()) << 32;
        vv |= vv1;
        double dd = Double.longBitsToDouble(vv);
        return dd;
        }
    public int readInt() throws IOException{
        if (dest==null)
            throw new IOException("Не открыт файл");
        int vv=0;
        for(int i=0;i<4;i++){
            int dd = dest.read();
            if (dd==-1)
                throw new IOException("EOF");
            vv |= ((dd & 0x0FF)<<(i*8));
            }
        return vv;
        }
    public static int convertInt(int vv){
        if ((vv & 0x80000000)!=0)
            return vv & 0x7FFFFFFF;
        else
            return vv | 0x80000000;
        }
    public void skip(int cnt) throws IOException{
        while(cnt--!=0)
            readInt();
        }
    public static void main(String argv[]) {
        BinInputStream bb = new BinInputStream();
        System.out.println(bb.q31ToDouble(0x80000000));
        System.out.println(bb.q31ToDouble(0x80000001));
        System.out.println(bb.q31ToDouble(0x88000000));
        System.out.println(bb.q31ToDouble(0x84000000));
        System.out.println(bb.q31ToDouble(0xffffffff));
        System.out.println(bb.q31ToDouble(0x7fffffff));
        System.out.println(bb.q31ToDouble(0x70000001));
        System.out.println(bb.q31ToDouble(0x20000001));
        System.out.println(bb.q31ToDouble(0x00000001));
        System.out.println(bb.q31ToDouble(0x00000000));
        }
}
