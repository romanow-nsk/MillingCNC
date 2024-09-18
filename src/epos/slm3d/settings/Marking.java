package epos.slm3d.settings;

import epos.slm3d.controller.USBCodes;
import epos.slm3d.io.BinInputStream;
import epos.slm3d.io.BinOutputStream;
import epos.slm3d.io.I_File;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static epos.slm3d.controller.USBCodes.*;

/**
 * Created by romanow on 01.12.2017.
 */
public class Marking implements I_File{
    public IndexedParameter MicroStepsMark = new IndexedParameter(USBCodes._MarkingMicroStepsMarkInt,160);
    public IndexedParameter MicroStepsJump = new IndexedParameter(USBCodes._MarkingMicroStepsJumpInt,64);
    public IndexedParameter MarkTailsInput = new IndexedParameter(USBCodes._MarkingMarkTailsInputInt,0);
    public IndexedParameter MarkTailsOutput = new IndexedParameter(USBCodes._MarkingMarkTailsOutputInt,0);
    public void load(DataInputStream in) throws IOException {
        MicroStepsMark.load(in);
        MicroStepsJump.load(in);
        MarkTailsInput.load(in);
        MarkTailsOutput.load(in);
        }
    public void save(DataOutputStream in) throws IOException {
        MicroStepsMark.save(in);
        MicroStepsJump.save(in);
        MarkTailsInput.save(in);
        MarkTailsOutput.save(in);
        }
    public void setNotNull(){
        if (MicroStepsMark==null)  MicroStepsMark = new IndexedParameter(_MarkingMicroStepsMarkInt,160);      //0.000004
        if (MicroStepsJump==null)  MicroStepsJump = new IndexedParameter(_MarkingMicroStepsJumpInt,64);
        if (MarkTailsInput==null)  MarkTailsInput = new IndexedParameter(_MarkingMarkTailsInputInt,0);
        if (MarkTailsOutput==null)  MarkTailsOutput = new IndexedParameter(_MarkingMarkTailsOutputInt,0);
        }
}
