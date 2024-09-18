/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epos.slm3d.m3d;

import epos.slm3d.io.BinOutputStream;
import epos.slm3d.settings.WorkSpace;
import epos.slm3d.utils.UNIException;
import epos.slm3d.utils.Utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author romanow
 */
public class M3DFileBinOutputStream {
    private BinOutputStream dest;
    private OutputStream is;
    public M3DFileBinOutputStream(String name) throws FileNotFoundException {
        is = new FileOutputStream(name);
        dest = new BinOutputStream(is);
        }
    public M3DFileBinOutputStream() throws FileNotFoundException {
        this("mark.out");
        }
    private int addr=0;
    public int addr(){ return addr; }
    /*
    public Command get8000() throws IOException{
        for(int i=0;i<5;i++)
            dest.readInt();
        return new Command(addr-4,0x8000,0,0);
        }
    public Command get(int code) throws IOException{
        return new Command(addr-4,code,dest.q31ToDouble(),dest.q31ToDouble());
        }
    public void skip(int cnt) throws IOException{
        while(cnt--!=0)
            dest.readInt();
        }
    public int findCommand() throws IOException{
        int vv=0;
        do  {
            vv = dest.readInt();
            } while(!(vv==1 || vv==0x101));
        return vv;
        }
    private boolean found=false;
    public Command getNext() throws IOException{
        int code;
        if (found){
            code = dest.readInt();
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
        boolean finish=false;
        readHeader();
        while(!finish)
            finish=back.onCommand(getNext());
        }
    */
    public void close() throws IOException {
        is.close();
        }
    public void write(int vv[]) throws IOException {
        addr+=vv.length;
        is.write(Utils.intToBytes(vv));
        }
    public void writeHeader() throws UNIException {
        try {
            is.write(WorkSpace.ws().temp().createHeader());
            } catch (IOException ee){ throw UNIException.io(ee); }
        }
    public static void main(String argv[]) {
        }
    }
