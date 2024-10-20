package romanow.cnc.settings;

import romanow.cnc.Values;
import romanow.cnc.io.I_File;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MashineSettings implements I_File {
    public FloatParameter WorkFrameX = new FloatParameter(600);
    public FloatParameter WorkFrameY = new FloatParameter(400);
    public FloatParameter WorkFrameZ = new FloatParameter(200);
    /** Количество потоков */
    public IntParameter SliceThreadNum = new IntParameter(Values.SliceThreadNum);
    /** Печать - текущий слой */
    public IntParameter CurrentLayer = new IntParameter(-1);
    /** Печать - текущая линия */
    public IntParameter CurrentLine = new IntParameter(0);
    /**  Драйвер USB */
    public StringParameter DeviceName = new StringParameter(Values.COMPort);
    public IntParameter DeviceNum = new IntParameter(Values.COMPortNum);
    public void load(DataInputStream in) throws IOException {
        WorkFrameX.load(in);
        WorkFrameY.load(in);
        WorkFrameZ.load(in);
        SliceThreadNum.load(in);
        CurrentLayer.load(in);
        CurrentLine.load(in);
        DeviceName.load(in);
        DeviceNum.load(in);
        }
    public void save(DataOutputStream in) throws IOException {
        WorkFrameX.save(in);
        WorkFrameY.save(in);
        WorkFrameZ.save(in);
        SliceThreadNum.save(in);
        CurrentLayer.save(in);
        CurrentLine.save(in);
        DeviceName.save(in);
        DeviceNum.save(in);
    }
    public void setNotNull(){
        if (WorkFrameX==null)  WorkFrameX = new FloatParameter(600);
        if (WorkFrameY==null)  WorkFrameY = new FloatParameter(400);
        if (WorkFrameZ==null)  WorkFrameZ = new FloatParameter(200);
        if (SliceThreadNum==null) SliceThreadNum = new IntParameter(Values.SliceThreadNum);
        if (CurrentLayer==null) CurrentLayer = new IntParameter(0);
        if (CurrentLine==null) CurrentLine = new IntParameter(0);
        if (DeviceName==null) DeviceName = new StringParameter(Values.COMPort);
        if (DeviceNum==null) DeviceNum = new IntParameter(Values.COMPortNum);
    }
}
