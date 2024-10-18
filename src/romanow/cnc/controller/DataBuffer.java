package romanow.cnc.controller;

import romanow.cnc.Values;

/**
 * Created by romanow on 21.12.2017.
 */
public class DataBuffer {
    private int reserved=1;
    private int data[]=new int[Values.USB3BlockByteSize/4-reserved];
    private int idx=0;
    //public DataBuffer(){}
    public DataBuffer(USBProtocol usb, int reserved0){
        reserved = reserved0;
        data = new int[usb.blockByteSize()/4-reserved];
        }
    public int dataLength(){ return idx; }
    public boolean canPut(int sz){
        return idx+sz < data.length;
        }
    public void putInt(int vv){
        data[idx++] = vv;
        }
    public void clear(){ idx=0; }
    public void put(int vv[]){
        for(int i=0;i<vv.length;i++)
            data[idx++] = vv[i];
        }
    public void putDouble(double vv){
        long zz = Double.doubleToLongBits(vv);
        data[idx++] = (int)zz;
        data[idx++] = (int)(zz>>32);
        }
    public void putDouble(double vv,int offset){
        long zz = Double.doubleToLongBits(vv);
        data[offset] = (int)zz;
        data[offset+1] = (int)(zz>>32);
        }
    public int []getData(){
        int out[] = new int[idx];
        for(int i=0;i<idx;i++)
            out[i]=data[i];
        return out;
        }
    /** Первое слово - резерв для кода команды */
    public int []getDataForCommand(){
        int out[] = new int[idx+reserved];
        out[0]=0;
        for(int i=0;i<idx;i++)
            out[i+reserved]=data[i];
        return out;
    }
    public static void putDouble(int data[], double vv,int offset){
        long zz = Double.doubleToLongBits(vv);
        data[offset] = (int)zz;
        data[offset+1] = (int)(zz>>32);
    }
}
