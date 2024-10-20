/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.settings;

import romanow.cnc.io.I_File;
import romanow.cnc.slicer.SliceRezult;
import romanow.cnc.utils.Utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author romanow
 */
public class Statistic implements I_File {
    public IntParameter LineCount = new IntParameter(0);
    /** Длина линий в м*/
    public FloatParameter LineLength = new FloatParameter(0);
    /** Холостой ход в м*/
    public FloatParameter MoveLength = new FloatParameter(0);
    public IntParameter SliceTime = new IntParameter(0);
    public IntParameter PrintTime = new IntParameter(0);
    public int moveProc(){ return LineLength.getVal()==0 ? 0 : (int)(MoveLength.getVal()/LineLength.getVal()*100); }
    public String printLength(){
        return Utils.toMMString(LineLength.getVal());
        }
    public String sliceTime(){ return Utils.toTimeString(SliceTime.getVal()); }
    public String printTime(){
        return Utils.toTimeString(Utils.toMM(PrintTime.getVal()));
         }
    public void setNotNull(){
        if (LineCount==null) LineCount = new IntParameter(0);
        if (LineLength==null) LineLength = new FloatParameter(0);
        if (MoveLength==null) MoveLength = new FloatParameter(0);
        }
    public void setFromRezult(SliceRezult rez){
        LineCount.setVal(rez.lineCount());
        LineLength.setVal(rez.lineLength());
        MoveLength.setVal(rez.moveLength());
        SliceTime.setVal(rez.sliceTime());
        PrintTime.setVal(0);
        }

    @Override
    public void load(DataInputStream in) throws IOException {
        LineCount.load(in);
        LineLength.load(in);
        MoveLength.load(in);
        SliceTime.load(in);
        PrintTime.load(in);
    }

    @Override
    public void save(DataOutputStream in) throws IOException {
        LineCount.save(in);
        LineLength.save(in);
        MoveLength.save(in);
        SliceTime.save(in);
        PrintTime.save(in);
    }
}
