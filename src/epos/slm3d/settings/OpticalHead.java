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
public class OpticalHead implements I_File{
    /** мм */
    public final  IntParameter LensScanWidth = new IntParameter(9);
    /** мм */
    public final  IntParameter LensScanHight = new IntParameter(8);
    public final  FloatParameter LensBeamSize = new FloatParameter(0,1, 0.01);
    public final  IntParameter Focus = new IntParameter(125);
    /** мм */
    public final  FloatParameter MarkWidth = new FloatParameter(0.5,8,5);
    /** мм */
    public final  FloatParameter MarkHight = new FloatParameter(0.5,8,4);
    public final  BooleanParameter Rotation = new BooleanParameter(true);
    public void load(DataInputStream in) throws IOException {
        LensScanWidth.load(in);
        LensScanHight.load(in);
        LensBeamSize.load(in);
        Focus.load(in);
        MarkWidth.load(in);
        MarkHight.load(in);
        Rotation.load(in);
    }
    public void save(DataOutputStream in) throws IOException {
        LensScanWidth.save(in);
        LensScanHight.save(in);
        LensBeamSize.save(in);
        Focus.save(in);
        MarkWidth.save(in);
        MarkHight.save(in);
        Rotation.save(in);
    }
}
