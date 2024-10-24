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
    public IntParameter BaudRate = new IntParameter(Values.COMPortBaudRate);
    public IntParameter DeviceTimeOut = new IntParameter(5);
    public void load(DataInputStream in) throws IOException {
        WorkFrameX.load(in);
        WorkFrameY.load(in);
        WorkFrameZ.load(in);
        SliceThreadNum.load(in);
        CurrentLayer.load(in);
        CurrentLine.load(in);
        DeviceName.load(in);
        DeviceNum.load(in);
        BaudRate.load(in);
        DeviceTimeOut.load(in);
        }
    public MashineSettings clone(){
        MashineSettings out = new MashineSettings();
        out.WorkFrameX = WorkFrameX.clone();
        out.WorkFrameY = WorkFrameY.clone();
        out.WorkFrameZ = WorkFrameZ.clone();
        out.SliceThreadNum = SliceThreadNum.clone();
        out.CurrentLayer = CurrentLayer.clone();
        out.CurrentLine = CurrentLine.clone();
        out.DeviceName = new StringParameter(DeviceName.getVal());
        out.DeviceNum = DeviceNum.clone();
        out.BaudRate = BaudRate.clone();
        out.DeviceTimeOut = DeviceTimeOut.clone();
        return out;
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
        BaudRate.save(in);
        DeviceTimeOut.save(in);
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
        if (BaudRate==null) BaudRate = new IntParameter(Values.COMPortBaudRate);
        if (DeviceTimeOut==null) DeviceTimeOut = new IntParameter(5);
    }
}
