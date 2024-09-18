package epos.slm3d.settings;

import epos.slm3d.io.I_File;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by romanow on 01.12.2017.
 */
public class BooleanParameter implements I_File{
    private boolean def,val;
    public BooleanParameter(boolean def0, boolean val0){
        def = def0;
        val = val0;
        }
    public BooleanParameter(boolean def0){
        this(def0,def0);
    }
    public boolean getDef() {
        return def;
    }
    public boolean getVal() {
        return val;
    }
    public void setVal(boolean val) {
        this.val = val;
    }
    @Override
    public void load(DataInputStream in) throws IOException {
        def = in.readBoolean();
        val = in.readBoolean();
    }
    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeBoolean(def);
        out.writeBoolean(val);
        }
    public BooleanParameter clone(){ return new BooleanParameter(def,val); }
}
