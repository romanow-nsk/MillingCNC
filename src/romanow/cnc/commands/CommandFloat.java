package romanow.cnc.commands;

import romanow.cnc.controller.DataBuffer;

/**
 * Created by romanow on 21.12.2017.
 */
public class CommandFloat extends Command{
    private int val=0;
    public CommandFloat(){ super(0); }
    public CommandFloat(int code){
        this(code,0);
        }
    public CommandFloat(int code, String name0){
        super(code);
        name = name0;
        signature = Command.SIGN_FLOAT;
        }
    public CommandFloat(int code, float val0){
        super(code);
        val = Float.floatToIntBits(val0);
        signature = Command.SIGN_FLOAT;
        }
    @Override
    public void value(float vv) { val = Float.floatToIntBits(vv); }
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
