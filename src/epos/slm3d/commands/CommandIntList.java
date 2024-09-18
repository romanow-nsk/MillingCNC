package epos.slm3d.commands;

import epos.slm3d.controller.DataBuffer;

/**
 * Created by romanow on 21.12.2017.
 */
public class CommandIntList extends Command{
    int val[];
    int idx=2;
        {
        val = new int[6];
        for(int i=0;i<6;i++) val[i]=0;
        }
    public CommandIntList(){ super(0); }
    public CommandIntList(int code){
        super(code);
        signature = Command.SIGN_INTLIST;
        }
    public CommandIntList(int code, String name0, int size){
        this(code);
        name = name0;
        dataSize = size;
        }
    public CommandIntList(int code, int size, boolean mark){
        this(code);
        idx = 2;
        dataSize = size;
        }
    public CommandIntList(int code, int val0){
        this(code);
        idx = 3;
        val[2] = val0;
        }    
    public CommandIntList(int code, int val0, int val1){
        this(code);
        val[2] = val0;
        val[3] = val1;
        idx = 4;
        }
    public CommandIntList(int code, int val0, int val1, int val2){
        this(code);
        val[2] = val0;
        val[3] = val1;
        val[4] = val2;
        idx = 5;
        }
    public CommandIntList(int code, int val0, int val1, int val2, int val3){
        this(code);
        val[2] = val0;
        val[3] = val1;
        val[4] = val2;
        val[5] = val3;
        idx = 6;
        }
    @Override
    public void value(int vv) {
        if (idx>=4 || idx-2 >= dataSize) return;
        val[idx++] = vv;
        }
    public void valueIdx(int vv, int ii) {
        if (ii>=4 || ii >= dataSize) return;
        val[ii+2] = vv;
        }
    public int []toIntArray(){
        val[0] = code;
        return val;
        }
}
