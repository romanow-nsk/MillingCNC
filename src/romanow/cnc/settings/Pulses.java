package romanow.cnc.settings;

import romanow.cnc.controller.USBCodes;
import romanow.cnc.io.I_File;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static romanow.cnc.controller.USBCodes.*;
import static romanow.cnc.controller.USBCodes._PulseDACFrequence;

/**
 * Created by romanow on 01.12.2017.
 */
public class Pulses implements I_File{
    public IndexedParameter LaserFrequence = new IndexedParameter(USBCodes._PulseLaserFrequence,0,100000,75000);
    /** мощность накачки - в процентах */
    public IndexedParameter LaserPumpPower = new IndexedParameter(USBCodes._PulseLaserPumpPower,20);
    public IndexedParameter LaserPulseType = new IndexedParameter(USBCodes._PulseLaserPulseType,0,4,4);
    public IndexedParameter LaserPulseSuppress = new IndexedParameter(USBCodes._PulseLaserPulseSuppress,10);
    public IndexedParameter DACFrequence = new IndexedParameter(USBCodes._PulseDACFrequence,0,100000,25000);
    public void load(DataInputStream in) throws IOException {
        LaserFrequence.load(in);
        LaserPumpPower.load(in);
        LaserPulseType.load(in);
        LaserPulseSuppress.load(in);
        DACFrequence.load(in);
        }
    public void save(DataOutputStream in) throws IOException {
        LaserFrequence.save(in);
        LaserPumpPower.save(in);
        LaserPulseType.save(in);
        LaserPulseSuppress.save(in);
        DACFrequence.save(in);
        }
    public void setNotNull(){
        if (LaserFrequence==null)  LaserFrequence = new IndexedParameter(_PulseLaserFrequence,0,100000,75000);
        /** мощность накачки - в процентах */
        if (LaserPumpPower==null)  LaserPumpPower = new IndexedParameter(_PulseLaserPumpPower,20);
        if (LaserPulseType==null)  LaserPulseType = new IndexedParameter(_PulseLaserPulseType,0,4,4);
        if (LaserPulseSuppress==null)  LaserPulseSuppress = new IndexedParameter(_PulseLaserPulseSuppress,10);
        if (DACFrequence==null)  DACFrequence = new IndexedParameter(_PulseDACFrequence,0,100000,25000);
        }
}
