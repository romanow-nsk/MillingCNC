package epos.slm3d.slicer;

import epos.slm3d.io.I_File;
import epos.slm3d.settings.WorkSpace;
import epos.slm3d.utils.Utils;
import epos.slm3d.utils.Values;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by romanow on 10.12.2017.
 */
public class SliceRezult implements I_File {
    private int layerIdx=0;
    private boolean error=false;
    private int lineCount=0;
    private  double lineLength=0;
    private  double moveLength=0;
    private int sliceTime=0;
    public void sliceTime(int tt){ sliceTime=tt; }
    public int sliceTime(){ return sliceTime; }
    public int lineCount(){ return lineCount; }
    public double lineLength(){ return lineLength; }
    public double moveLength(){ return moveLength; }
    public void setError(){ error=true; }
    public boolean hasError(){ return  error; }
    public SliceRezult(){}
    public void layerIdx(int vv){ layerIdx=vv; }
    public int layerIdx(){ return layerIdx; }
    public void add(double line, double move){
        lineCount++;
        lineLength+=line;
        moveLength+=move;
        }
    public int moveProc(){ return (int)(moveLength/lineLength*100); }
    public String printLength(){
        return Utils.toMMString(lineLength);
    }
    public String printTime(){
        return Utils.toTimeString((Utils.toMM(lineLength)/ WorkSpace.ws().local().marking.MicroStepsMark.getVal()));
    }
    public synchronized void procLayer(SliceRezult layer){
        lineCount+=layer.lineCount;
        lineLength+=layer.lineLength;
        moveLength+=layer.moveLength;
        }
    @Override
    public void load(DataInputStream in) throws IOException {
        lineCount = in.readInt();
        lineLength = in.readDouble();
        moveLength = in.readDouble();
        }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeInt(lineCount);
        out.writeDouble(lineLength);
        out.writeDouble(moveLength);
        }
    public String toString(){ return lineCount + " "+printLength()+" "+printTime(); }
}
