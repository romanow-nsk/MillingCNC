package epos.slm3d.settings;

import epos.slm3d.controller.USBCodes;
import epos.slm3d.io.BinInputStream;
import epos.slm3d.io.BinOutputStream;
import epos.slm3d.io.I_File;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static epos.slm3d.controller.USBCodes.*;
import static epos.slm3d.controller.USBCodes._DelaysMovingPenStrokeDelay;

/**
 * Created by romanow on 01.12.2017.
 */
public class Delays implements I_File{
    public IndexedParameter LaserOn = new IndexedParameter(USBCodes._DelaysLaserOn,0);
    public IndexedParameter LaserOff = new IndexedParameter(USBCodes._DelaysLaserOff,0);
    public IndexedParameter MovingPenJumpDelay = new IndexedParameter(USBCodes._DelaysMovingPenJumpDelay,20);
    public IndexedParameter MovingPenMarkDelay = new IndexedParameter(USBCodes._DelaysMovingPenMarkDelay,20);
    public IndexedParameter MovingPenStrokeDelay = new IndexedParameter(USBCodes._DelaysMovingPenStrokeDelay,200);
    public IntParameter LineSpeedPoints = new IntParameter(0,100,0);
    public IntParameter LineSpeedDelta = new IntParameter(0,10,0);
    public void load(DataInputStream in) throws IOException {
        LaserOn.load(in);
        LaserOff.load(in);
        MovingPenJumpDelay.load(in);
        MovingPenMarkDelay.load(in);
        MovingPenStrokeDelay.load(in);
        }
    public void save(DataOutputStream in) throws IOException {
        LaserOn.save(in);
        LaserOff.save(in);
        MovingPenJumpDelay.save(in);
        MovingPenMarkDelay.save(in);
        MovingPenStrokeDelay.save(in);
        }
    public void setNotNull(){
        if (LaserOn==null)  LaserOn = new IndexedParameter(_DelaysLaserOn,0);
        if (LaserOff==null)  LaserOff = new IndexedParameter(_DelaysLaserOff,0);
        if (MovingPenJumpDelay==null)  MovingPenJumpDelay = new IndexedParameter(_DelaysMovingPenJumpDelay,200);
        if (MovingPenMarkDelay==null)  MovingPenMarkDelay = new IndexedParameter(_DelaysMovingPenMarkDelay,20);
        if (MovingPenStrokeDelay==null)  MovingPenStrokeDelay = new IndexedParameter(_DelaysMovingPenStrokeDelay,200);
        if (LineSpeedPoints==null) LineSpeedPoints = new IntParameter(0,100,0);
        if (LineSpeedDelta==null) LineSpeedDelta = new IntParameter(0,10,0);
        }
}

