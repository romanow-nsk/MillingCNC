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
public class Scanners implements I_File{
    public final  FloatParameter ScalingX = new FloatParameter(-1,1,0.95);
    public final  FloatParameter ScalingY = new FloatParameter(-1,1,0.95);
    public final  BooleanParameter EnableXCorrection = new BooleanParameter(false);
    public final  BooleanParameter EnableYCorrection = new BooleanParameter(false);
    public void load(DataInputStream in) throws IOException {
        ScalingX.load(in);
        ScalingY.load(in);
        EnableXCorrection.load(in);
        EnableYCorrection.load(in);
        }
    public void save(DataOutputStream in) throws IOException {
        ScalingX.save(in);
        ScalingY.save(in);
        EnableXCorrection.save(in);
        EnableYCorrection.save(in);
        }
    }
