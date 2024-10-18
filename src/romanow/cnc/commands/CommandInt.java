package romanow.cnc.commands;

import romanow.cnc.controller.DataBuffer;

/**
 * Created by romanow on 21.12.2017.
 */
public class CommandInt extends Command{
    private int val=0;
    public CommandInt(){ super(0); }
    public CommandInt(int code){
        this(code,0);
        }
    public CommandInt(int code, String name0){
        super(code);
        name = name0;
        signature = SIGN_INT;
        }
    public CommandInt(int code, int val0){
        super(code);
        val = val0;
        signature = SIGN_INT;
        }
    @Override
    public void value(int vv) { val = vv; }
    public int wordSize(){ return 2; }
    public int []toIntArray(){
        int out[]=new int[2];
        out[0]=code;
        out[1]=val;
        return out;
        }
    public boolean canPut(DataBuffer out){
        return out.canPut(1);
    }
    public void toDataBuffer(DataBuffer out){
        super.toDataBuffer(out);
        out.putInt(val);
        }
}
