/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.m3d;

import romanow.cnc.commands.Command;
import romanow.cnc.commands.CommandEmpty;
import romanow.cnc.commands.M3DCommandFactory;
import romanow.cnc.io.BinInputStream;
import romanow.cnc.settings.WorkSpace;
import romanow.cnc.utils.UNIException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author romanow
 */
public class M3DFileBinInputStream implements FileBinInputStream{
    private BinInputStream dest;
    private InputStream is;
    private M3DCommandFactory factory;
    public M3DFileBinInputStream(String name) throws FileNotFoundException {
        is = new FileInputStream(name);
        dest = new BinInputStream(is);
        }
    public M3DFileBinInputStream() throws FileNotFoundException {
        this("mark.out");
        }
    public int readInt() throws IOException{
        return dest.readInt();
        }
    public int []readBuf(int sz) throws UNIException{
        int out[]=new int[sz];
        int zz=0;
        try {
            for(zz=0;zz<sz;zz++)
                out[zz]=readInt();
            } catch(IOException ee){
                if (!ee.getMessage().equals("EOF"))
                        throw UNIException.io(ee);
                int xx[] = new int[zz];
                for (int i=0;i<zz;i++)
                    xx[i]=out[i];
                return xx;
                }
            return out;
        }
    private int addr=0;
    private boolean isSynch=false;
    private int lastCode = -1;
    public Command getNext() throws UNIException {
        try {
            CommandEmpty empty=null;
            int code;
            if (isSynch){
                if (lastCode == -1)
                    code = dest.readInt();
                else{
                    code = lastCode;
                    lastCode = -1;
                    }
                Command cmd = factory.getCommand(code);
                if (cmd!=null){
                    cmd.init();
                    cmd.load(dest);
                    addr+=cmd.byteSize();
                    return cmd;
                    }
                else{
                    isSynch = false;
                    empty = new CommandEmpty();
                    empty.add(code);
                    }
                }
            if (empty == null)
                empty = new CommandEmpty();
            while (true){
                int code2 = dest.readInt();
                if (factory.findCommand(code2)){
                    isSynch=true;
                    if (empty.byteSize()==0){
                        lastCode=code2;
                        return getNext();       // Сразу же следующую
                        }
                    else{
                        lastCode=code2;
                        addr+=empty.byteSize();
                        return empty;
                        }
                    }
                else{
                    empty.add(code2);
                    }
                }
            } catch (IOException ee) {
                if (ee.getMessage().equals("EOF"))
                    return null;
                throw UNIException.io(ee);
                }
        }
    public void procFile(OnM3DCommand back) throws UNIException {
        try {
            factory = new M3DCommandFactory();
            boolean finish = false;
            addr = M3DValues.headerSize;
            int hd[] = readHeader();
            back.onHeader(hd);
            //ws.temp().setHeader(hd);
            while (!finish){
                Command cmd = getNext();
                if (cmd==null)
                    break;
                finish = back.onCommand(cmd);
                }
            back.onFinish();
            } catch (IOException ee){
                back.onFinish();
                }
        }
    public void close() throws IOException {
        is.close();
        }
    public int []readHeader() throws IOException {
        int sz = M3DValues.headerSize / 4;
        int hd[] = new int[sz];
        for (int i = 0; i < sz; i++)
            hd[i] = dest.readInt();
        //ws.temp().setHeader(hd);
        return hd;
        }
    }
