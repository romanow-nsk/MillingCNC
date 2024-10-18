package romanow.cnc.console;

import romanow.cnc.controller.USBBack;
import romanow.cnc.controller.USBCodes;
import romanow.cnc.controller.USBProtocol;
import romanow.cnc.settings.WorkSpace;

import java.awt.*;
import java.io.*;

/**
 * Created by romanow on 23.10.2018.
 */
public class Distortion{
    private short data[] = new short[65536];
    public void load(InputStream in) throws IOException {
        byte bb[]=new byte[65536*4];
        in.read(bb);
        DataInputStream is = new DataInputStream(new ByteArrayInputStream(bb));
        for(int i=0;i<65536;i++)
            data[i]=is.readShort();
        is.close();
        }
    public void save(OutputStream out) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataOutputStream ss = new DataOutputStream(os);
        for(int i=0;i<65536;i++)
            ss.writeShort(data[i]);
        out.write(os.toByteArray());
        ss.close();
        }
    public void values(int vx, int vy){
        for(int i=0;i<65536;i++)
            data[i]= (short)( vx<<8 | vy & 0x0FF);
        }
    public void value(int x, int y, int vx,int vy){
        data[x<<8 | y & 0x0FF] = (short)( vx<<8 | vy & 0x0FF);
        }
    public int valueXY(int x, int y){
        return data[x<<8 | y & 0x0FF] & 0x0FFFF;
        }
    public int valueX(int x, int y){
        byte bb = (byte)(data[x<<8 | y & 0x0FF] >> 8 & 0x0FF);
        return bb;
        }
    public int valueY(int x, int y){            // Расширить знак
        byte bb = (byte)(data[x<<8 | y & 0x0FF] & 0x0FF);
        return bb;
        }
    public void setLinearXY(double kx, double ky){
        for(int y=0;y<256;y++)
            for(int x=0;x<256;x++)
                value(x,y,(int)(kx*(x-128)),(int)(ky*(y-128)));
        }
    public void setRadialXY(double k){
        for(int y=0;y<256;y++)
            for(int x=0;x<256;x++){
                double zz = Math.sqrt((x-128)*(x-128)+(y-128)*(y-128));
                value(x,y,(int)(100*k*(x-128)/zz),(int)(100*k*(y-128)/zz));
                }
        }
    public Color toColor(int x,int y){
        int vx = valueX(x,y);
        int vy = valueY(x,y);
        int zz = (int)Math.sqrt(vx*vx+vy*vy);
        int r=zz;
        int g=zz;
        int b=zz;
        if (vx>0) r+=vx; else { g-=vx/2; b-=vx/2; }
        if (vy>0) b+=vy; else { r-=vy/2; g-=vy/2; }
        if (r>255) r=255;
        if (g>255) g=255;
        if (b>255) b=255;
        return new Color(r,g,b);
        }
    /** Код коррекции геометрии */
    public int correctionValue(int x,int y){
        int vv = valueXY(x, y);
        return (x & 0x0ff)<<24 | (y & 0x0ff)<<16 | vv;
        }
    //----------------------------------------------------------------------------------------------
    public void setDistortion(USBProtocol usb, USBBack back, boolean notNull){
        if (notNull)
            usb.oneCommand(USBCodes.FillCorrectionTbl,0,back);
        for(int y=0;y<256;y++)
            for(int x=0;x<256;x++){
                if (notNull && valueXY(x,y)==0)
                    continue;
                usb.oneCommand(USBCodes.SetCorrectionElement,correctionValue(x, y), back);
                }
        }
    public void setDistortionBlock(USBProtocol usb, USBBack back, boolean notNull){
        if (notNull)
            usb.oneCommand(USBCodes.FillCorrectionTbl,0,back);
        int sz = usb.blockByteSize()/4-1;
        int data[] = new int[sz];
        data[0]=USBCodes.SetBlockCorrElements;
        int idx=2;
        for(int y=0;y<256;y++)
            for(int x=0;x<256;x++){
                if (notNull && valueXY(x,y)==0)
                    continue;
                data[idx]=correctionValue(x,y);
                idx++;
                if (idx==sz){
                    data[1]=idx-2;
                    usb.oneCommand(data,idx, back);
                    WorkSpace.ws().notify("Передан блок коррекции:"+(idx-2));
                    idx=2;
                    }
            }
        if (idx!=2){
            WorkSpace.ws().notify("Передан блок коррекции:"+(idx-2));
            data[1]=idx-2;
            usb.oneCommand(data,idx,back);
            }
    }




    public static void main(String argv[]){
        Distortion dd = new Distortion();
        dd.values(6,-7);
        System.out.println(dd.valueX(5,6));
        System.out.println(dd.valueY(5,6));
        dd.value(5,6,-12,-15);
        System.out.println(dd.valueX(5,6));
        System.out.println(dd.valueY(5,6));
        dd.setLinearXY(0.5,0.7);
        System.out.println(dd.valueX(50,60));
        System.out.println(dd.valueY(50,60));
    }
}
