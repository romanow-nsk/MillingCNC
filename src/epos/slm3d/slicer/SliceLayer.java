package epos.slm3d.slicer;

/**
 * Created by romanow on 07.02.2018.
 */

import epos.slm3d.io.I_File;
import epos.slm3d.settings.Settings;
import epos.slm3d.settings.WorkSpace;
import epos.slm3d.stl.STLLine;
import epos.slm3d.stl.STLLineGroup;
import epos.slm3d.stl.STLLoop;
import epos.slm3d.utils.Values;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

/** Данный слайсинга слоя */
public class SliceLayer implements I_File {
    /**  Отрезки прожига */
    private STLLineGroup segments = new STLLineGroup();
    /** Ошибки */
    private ArrayList<SliceError> errorList = new ArrayList<>();
    /** Контуры */
    private ArrayList<STLLoop> loops = new ArrayList<>();
    /**  исходные отрезки  */
    private STLLineGroup lines = new STLLineGroup();
    private SliceRezult rez = new SliceRezult();
    /** Координата z при генерации */
    private double z = 0;
    /** Угол наклолна растра при генерации */
    private double angle = 0 ;
    /** Порядковый номер при генерации, начинается с 1, 0 = дополнительный  */
    private int layerOrderNum=0;
    /** модифицирован */
    private boolean modified=false;
    /** локальные параметры слайсинга */
    private Settings sliceSettings=null;
    /** локальные параметры  прожига */
    private Settings printSettings=null;
    /** для параллельного заполнения */
    private boolean ready=false;
    /** собственная сингнатура */
    private String ownLabel="";
    //----------------------------------------------------------------------------------
    public Settings printSettings(){ return printSettings; }
    public void printSettings(Settings set0 ){ printSettings=set0; }
    public void setReady(){ ready=true; }
    public boolean isReady(){ return ready; }
    public int layerOrderNum() { return layerOrderNum; }
    public void layerOrderNum(int num) { layerOrderNum=num; }
    public boolean isModified() { return modified; }
    public void setModified() { modified=true; }
    public Settings sliceSettings() { return sliceSettings; }
    public void sliceSettings(Settings set) { sliceSettings=set; }
    public void convertPointsToFloat(){ segments.convertPointsToFloat(); }
    public SliceLayer(){}
    public SliceLayer(int num, double z0){ layerOrderNum=num; z=z0; }
    public SliceLayer(int num){ layerOrderNum=num; }
    public SliceRezult rezult() { return rez; }
    public void result(SliceRezult rez0) { rez = rez0; }
    public double z() {return z; }
    public void z(double z0) { z = z0; }
    public STLLineGroup segments() { return segments; }
    public void segments(STLLineGroup segments) { this.segments = segments; }
    public ArrayList<SliceError> errorList() { return errorList; }
    public void errorList(ArrayList<SliceError> errors) { this.errorList = errors; }
    public ArrayList<STLLoop> loops() { return loops; }
    public void loops(ArrayList<STLLoop> loops) { this.loops = loops; }
    public STLLineGroup lines() { return lines; }
    public void lines(STLLineGroup lines) { this.lines = lines; }
    public double angle(){ return angle; }
    public void angle(double vv){ angle=vv; }
    public void label(String ss){ ownLabel=ss; }
    public String label(){
        if (ownLabel.length()!=0)
            return ownLabel;
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df2 = new DecimalFormat("00.000", dfs);
        String zz="";
        if (layerOrderNum==0) zz="+ "; else zz=""+layerOrderNum+"  ";
        if (modified) zz+="*";
        if (printSettings!=null) zz+="x";
        return df2.format(z*(Values.PrinterFieldSize/2))+" мм / "+zz;
        }
    public SliceLayer(Settings filling){          // С оригинальными установками - повторный слайсинг
        layerOrderNum=0;
        modified=true;
        sliceSettings=filling;
        }

    public void addError(SliceError err) {
        errorList.add(err);
        }
    public void addSegment(STLLine line) {
        segments.add(line);
        }
    public boolean hasErrors(int mode) {
        for (SliceError err : errorList)
            if (err.getErrorCode() == mode)
                return true;
        return false;
    }
    public void shift(double xx, double yy, boolean fromFile) {
        segments.shift(xx, yy);
        for (SliceError err : errorList)
            err.shift(xx, yy);
        lines.shift(xx, yy);
        if (fromFile) {
            for (STLLoop loop : loops)
                loop.shift(xx, yy);
        }
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        z = in.readDouble();
        layerOrderNum = in.readInt();
        modified = in.readBoolean();
        if (WorkSpace.ws().fileFormatVersion()>=2){
            angle = in.readDouble();
            }
        if (WorkSpace.ws().fileFormatVersion()>=3){
            ownLabel = in.readUTF();
            }
        sliceSettings = null;
        if (in.readBoolean())
            sliceSettings = WorkSpace.ws().loadSettings(in);
        printSettings = null;
        if (WorkSpace.ws().fileFormatVersion()>=1){
            if (in.readBoolean())
                printSettings = WorkSpace.ws().loadSettings(in);
            }
        rez = new SliceRezult();
        rez.load(in);
        segments = new STLLineGroup();
        segments.load(in);
        lines = new STLLineGroup();
        lines.load(in);
        int sz = in.readInt();
        loops = new ArrayList<>();
        while (sz-- != 0) {
            STLLoop loop = new STLLoop();
            loop.load(in);
            loops.add(loop);
        }
        errorList = new ArrayList<>();
        sz = in.readInt();
        while (sz-- != 0) {
            String name = in.readUTF();
            Class cl;
            try {
                cl = Class.forName(name);
                SliceError err = (SliceError) cl.newInstance();
                err.load(in);
                errorList.add(err);
            } catch (Exception e) {
                throw new IOException("Не найден класс " + name);
            }
        }
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeDouble(z);
        out.writeInt(layerOrderNum);
        out.writeBoolean(modified);
        out.writeDouble(angle);                     // Версия формата = 2
        out.writeUTF(ownLabel);                     // Версия формата = 3
        out.writeBoolean(sliceSettings!=null);
        if (sliceSettings!=null)
            WorkSpace.ws().saveSettings(out, sliceSettings);
        out.writeBoolean(printSettings!=null);      // Версия формата = 1
        if (printSettings!=null)
            WorkSpace.ws().saveSettings(out, printSettings);
        rez.save(out);
        segments.save(out);
        lines.save(out);
        out.writeInt(loops.size());
        for (STLLoop loop : loops)
            loop.save(out);
        out.writeInt(errorList.size());
        for (SliceError error : errorList) {
            out.writeUTF(error.getClass().getName());
            error.save(out);
        }
    }

    public void addData(SliceLayer two) {
        segments.add(two.segments);
        for (SliceError err : two.errorList)
            errorList.add(err);
        lines.add(two.lines);
        for (STLLoop loop : two.loops)
            loops.add(loop);
        }
    public void refreshLinesFromLoops(){
        STLLineGroup out = new STLLineGroup();
        for (STLLoop loop : loops){
            for(STLLine line : loop.lines())
                out.addWithTest(line);
            }
        lines = out;
        }
}
