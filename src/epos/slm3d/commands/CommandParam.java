package epos.slm3d.commands;

import epos.slm3d.controller.DataBuffer;
import epos.slm3d.controller.USBCodes;

/**
 * Created by romanow on 21.12.2017.
 */
/** Удалена */
public class CommandParam extends Command{
    private int val=0;
    public CommandParam(){ super(USBCodes.SendParamInt); }
    public CommandParam(int idx0, String name0){
        super(USBCodes.SendParamInt);
        name = name0;
        signature = Command.SIGN_INT;
        idx = idx0;
        }
    public int idx(){return idx; }
    public CommandParam(int idx0, String name0, boolean isFloat){
        super(USBCodes.SendParamInt);
        name = name0;
        signature = isFloat ? Command.SIGN_FLOAT : Command.SIGN_INT;
        idx = idx0;
        }
    public CommandParam(int idx0, int val0){
        super(USBCodes.SendParamInt);
        idx = idx0;
        val = val0;
        signature = Command.SIGN_INT;
        }
    @Override
    public void value(float val0){ val = Float.floatToIntBits(val0); }
    @Override
    public void value(int vv) { val = vv; }
    public int wordSize(){ return 2; }
    public int []toIntArray(){
        int out[]=new int[3];
        out[0]=code;
        out[1]=idx;
        out[2]=val;
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
