package epos.slm3d.settings;

import epos.slm3d.io.BinInputStream;
import epos.slm3d.io.BinOutputStream;
import epos.slm3d.io.I_File;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by romanow on 01.12.2017.
 */
public class IntParameter implements I_File{
    private int min,max,def,val;
    public  IntParameter(int min0, int max0, int def0, int val0){
        min = min0;
        max = max0;
        def = def0;
        val = val0;
        }
    public  IntParameter(int min0, int max0, int def0){
        this(min0,max0,def0,def0);
        }
    public  IntParameter(int def0){
        this(0,1000,def0);
        }
    public void incValue(int dd){
        val+=dd; 
        }
    public int getMin() {
        return min;
        }
    public int getMax() {
        return max;
    }
    public int getDef() {
        return def;
    }
    public int getVal() {
        return val;
    }
    public void setVal(int val) {
        this.val = val;
    }
    public void save(BinOutputStream out) throws IOException {
        out.writeInt(val);
        }
    public void load(BinInputStream out) throws IOException {
        val = out.readInt();
        }
    @Override
    public void load(DataInputStream in) throws IOException {
        min = in.readInt();
        max = in.readInt();
        def = in.readInt();
        val = in.readInt();
        }
    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeInt(min);
        out.writeInt(max);
        out.writeInt(def);
        out.writeInt(val);
        }
    public IntParameter clone(){ return new IntParameter(min,max,def,val); }
}
