package romanow.cnc.settings;

import romanow.cnc.io.I_File;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by romanow on 13.08.2018.
 */
public class IndexedParameter implements I_File{
    private int idx=0;
    public int idx(){ return idx; }
    private int min,max,def,val;
    public  IndexedParameter(int idx0, int min0, int max0, int def0, int val0){
        idx = idx0;
        min = min0;
        max = max0;
        def = def0;
        val = val0;
        }
    public  IndexedParameter(int idx0, int val0){
        this(idx0,0,1000,0,val0);
        }
    public  IndexedParameter(int idx0, int min0, int max0, int val0){
        this(idx0,min0,max0,val0,val0);
        }
    public IndexedParameter clone(){ return new  IndexedParameter(idx,min,max,def,val); }
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
    @Override
    public void load(DataInputStream in) throws IOException {
        idx = in.readInt();
        min = in.readInt();
        max = in.readInt();
        def = in.readInt();
        val = in.readInt();
    }
    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeInt(idx);
        out.writeInt(min);
        out.writeInt(max);
        out.writeInt(def);
        out.writeInt(val);
    }
}
