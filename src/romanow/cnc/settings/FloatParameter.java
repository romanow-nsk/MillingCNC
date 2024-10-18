package romanow.cnc.settings;

import romanow.cnc.io.BinInputStream;
import romanow.cnc.io.BinOutputStream;
import romanow.cnc.io.I_File;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by romanow on 01.12.2017.
 */
public class FloatParameter implements I_File{
    private double min,max,def,val;
    public FloatParameter(double min0, double max0, double def0, double val0){
        min = min0;
        max = max0;
        def = def0;
        val = val0;
        }
    public  FloatParameter(double min0, double max0, double def0){
        this(min0,max0,def0,def0);
    }
    public  FloatParameter(double def0){
        this(0,1000,def0);
    }
    public double getMin() {
        return min;
    }
    public double getMax() {
        return max;
    }
    public double getDef() {
        return def;
    }
    public double getVal() {
        return val;
    }
    public void setVal(double val) {
        this.val = val;
    }
    public void save(BinOutputStream out) throws IOException {
        out.writeDouble(val);
        }
    public void load(BinInputStream out) throws IOException {
        val = out.readDouble();
        }

    @Override
    public void load(DataInputStream in) throws IOException {
        min = in.readDouble();
        max = in.readDouble();
        def = in.readDouble();
        val = in.readDouble();
        }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeDouble(min);
        out.writeDouble(max);
        out.writeDouble(def);
        out.writeDouble(val);
        }
    public FloatParameter clone(){ return new FloatParameter(min,max,def,val); }
}
