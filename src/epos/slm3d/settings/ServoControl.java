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
public class ServoControl implements I_File{
    /** мм/c */
    public final  FloatParameter ServoControlYSpeed = new FloatParameter(20);
    /** мм/c^2 */
    public final  FloatParameter ServoControlYAcc = new FloatParameter(10);
    /** мс */
    public final  IntParameter YPause = new IntParameter(500);
    /** мм/c */
    public final  FloatParameter ServoControlZSpeed = new FloatParameter(20);
    /** мм/c^2 */
    public final  FloatParameter ServoControlZAcc = new FloatParameter(10);
    /** мс */
    public final  IntParameter ZPause = new IntParameter(200);
    public void load(DataInputStream in) throws IOException {
        ServoControlYSpeed.load(in);
        ServoControlYAcc.load(in);
        YPause.load(in);
        ServoControlZSpeed.load(in);
        ServoControlZAcc.load(in);
        ZPause.load(in);
    }
    public void save(DataOutputStream in) throws IOException {
        ServoControlYSpeed.save(in);
        ServoControlYAcc.save(in);
        YPause.save(in);
        ServoControlZSpeed.save(in);
        ServoControlZAcc.save(in);
        ZPause.save(in);
    }

}
