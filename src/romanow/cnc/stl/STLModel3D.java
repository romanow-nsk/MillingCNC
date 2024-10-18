package romanow.cnc.stl;

import romanow.cnc.io.I_File;
import romanow.cnc.settings.Settings;
import romanow.cnc.settings.WorkSpace;
import romanow.cnc.utils.I_Notify;
import romanow.cnc.utils.UNIException;
import romanow.cnc.utils.Utils;
import romanow.cnc.Values;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by romanow on 04.12.2017.
 */
public class STLModel3D implements I_File {
    /** Путь к файлу модели */
    private String modelName="";
    public String modelName(){ return modelName; }
    public void modelName(String ss){ modelName=ss; }    
    /** Вектор треугольников */
    private ArrayList<STLTriangle> list=new ArrayList<>();
    private boolean loaded=false;
    private double normalizedScale=0;
    //--------------------------------------------------------------------------------
    public void generate(I_ModelGenerator generator){
        modelName = generator.name();
        list = generator.generate();
        normalizedScale=1;
        loaded = true;
        }
    public void removeAll(){ loaded=false; }
    public ArrayList<STLTriangle> triangles(){ return list; }
    public boolean loaded(){
        return loaded;
        }
    public double normalizedScale(){
        return normalizedScale;
        }

    public void rotate(int mode, MyAngle angle, I_Notify notify)  {
        if (!loaded){
            notify.log(String.format("Модель не загружена"));
            return;
            }
        //scale();
        for(STLTriangle xx : list)
            xx.rotate(mode,angle);
        setModelDimensions(notify,false);
        WorkSpace.ws().model(this);
        }
    /** вычислить размерности модели */
    public void setModelDimensions(I_Notify notify, boolean scaled){
        Settings set = WorkSpace.ws().global();
        if (set.global.AutoCenter.getVal())                        // Автоцентровка
            shiftToCenter();
        double zz = min().z();
        if (zz!=0)
            shift(2, - zz);                            // По Z - всегда
        STLPoint3D vmin = min();
        STLPoint3D vmax = max();
        double dim = getScaleXY();
        notify.log(String.format("исходная размерность %4.2f",dim));
        double dd = WorkSpace.ws().global().global.WorkFieldSize.getVal();
        if (set.global.AutoScale.getVal()){
            if (dim >dd/2){
                normalizedScale = (dd/2.)*Values.DefaultModelScale / dim;
                notify.log(String.format("Автонормализация в масштабе %4.2f", normalizedScale));
                //scale(normalizedScale);
                }
            else
                normalizedScale=1;
            }
        else{
            if (scaled){
                normalizedScale = set.global.ScaleFactor.getVal();
                if (normalizedScale!=1){
                    notify.log(String.format("Масштабирование %4.2f", normalizedScale));
                    }
                if (dim * normalizedScale > dd/2)
                    notify.log(String.format("Превышена размерность модели  %4.2f > %d", (dim * normalizedScale), dd/2));
                if (normalizedScale!=1)
                    scale(normalizedScale);
                }
            }
        notify.log(String.format("Высота (z) %4.2f", max().z()));
        //scale(2/Values.PrinterFieldSize);                   // Нормализовать к диапазону -1...+1
        saveModelDimensions();
        }
    public void saveModelDimensions(){
        STLPoint3D vmin = min();
        STLPoint3D vmax = max();
        double dx = vmax.x()-vmin.x();
        double dy = vmax.y()-vmin.y();
        Settings set2 = WorkSpace.ws().local();
        set2.local.MarkingFieldWidth.setVal(dx);  // Размер в mm
        set2.local.MarkingFieldHight.setVal(dy);  // Размер в mm
        set2.local.Z.setVal(vmax.z());
        set2.local.PageServoOffsetsLeft.setVal(Math.abs(min().x()));
        set2.local.PageServoOffsetsTop.setVal(Math.abs(min().y()));
        }
    public void load(String fname, I_Notify notify) throws UNIException{
        loadOnly(fname,notify);
        setModelDimensions(notify,true);
        }
    public void loadOnly(String fname, I_Notify notify) throws UNIException{
        loaded = false;
        try {
            File ff = new File(fname);
            list = new STLParser().parseSTLFile(ff.toPath());
            modelName = ff.getName();
            modelName = modelName.substring(0,modelName.indexOf("."));
            loaded = true;
            } catch (IOException ee){ throw UNIException.io(ee.toString()); }
        }
    public void saveSTL(String name) throws UNIException{
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(name));
            byte zz[] = new byte[80];
            for(int i=0;i<zz.length;i++)
                zz[i]=0;
            char cc[] = "AutoCAD solid".toCharArray();
            for(int i=0;i<cc.length;i++)
                zz[i]=(byte)cc[i];
            out.write(zz);
            out.write(Utils.intToBytes(list.size()));
            for(STLTriangle vv : list){
                vv.saveFloat(out);
                }
            out.close();
            } catch (Exception ee){ throw UNIException.io(ee); }
        }
    public STLModel3D(){}
    public void invertY(){
        for(STLTriangle tr : list)
            tr.invertY();
        }
    /** Максимальное значение - idx - индекс координаты */
    public double max(int idx){
        boolean first=true;
        double zMax = 0;
        for(STLTriangle zz:list){
            STLPoint3D[] vv = zz.getVertices();
            for (STLPoint3D dd : vv) {
                double gg=0;
                switch (idx){
                    case 0: gg = dd.x(); break;
                    case 1: gg = dd.y(); break;
                    case 2: gg = dd.z(); break;
                    }
                if (first || gg > zMax)
                    zMax = gg;
                first = false;
                }
            }
         return zMax;
        }
    /** Минимальное значение - idx - индекс координаты */
    public double min(int idx){
        boolean first=true;
        double zMax = 0;
        for(STLTriangle zz:list){
            STLPoint3D[] vv = zz.getVertices();
            for (STLPoint3D dd : vv) {
                double gg=0;
                switch (idx){
                    case 0: gg = dd.x(); break;
                    case 1: gg = dd.y(); break;
                    case 2: gg = dd.z(); break;
                }
                if (first || gg < zMax)
                    zMax = gg;
                first = false;
                }
            }
        return zMax;
        }
    /** Сдвиг - idx - индекс координаты */
    public void shift(int idx, double ll){
        for(STLTriangle zz:list){
            STLPoint3D[] vv = zz.getVertices();
            for (STLPoint3D dd : vv) {
                switch (idx){
                    case 0: dd.x(dd.x()+ll); break;
                    case 1: dd.y(dd.y()+ll); break;
                    case 2: dd.z(dd.z()+ll); break;
                }
            }
        }
    }
    /**  Масштабирование  */
    public void scale(double ll){
        for(STLTriangle zz:list){
            STLPoint3D[] vv = zz.getVertices();
            for (STLPoint3D dd : vv) {
                dd.x(dd.x()*ll);
                dd.y(dd.y()*ll);
                dd.z(dd.z()*ll);
                }
            }
        }
    /**  Нормализация -  сдвиг к центру по XY, по Z - к 0  */
    public void shiftToCenter(){
        STLPoint3D vmin = min();
        STLPoint3D vmax = max();
        shift(0, - (vmin.x()+vmax.x())/2);
        shift(1, - (vmin.y()+vmax.y())/2);
        }
    /** Масштаб относительно 1 */
    public double getScaleXY(){
        double vv = max(0);
        double zz = max(1);
        if (zz > vv) vv = zz;
        zz = Math.abs(min(0));
        if (zz > vv) vv = zz;
        zz = Math.abs(min(1));
        if (zz > vv) vv = zz;
        return vv;
        }
    /** Точка - максимальные значения */
    public STLPoint3D max(){
        STLPoint3D vec = new STLPoint3D(max(0),max(1),max(2));
        return vec;
        }
    /** Точка - минимальные значения */
    public STLPoint3D min(){
        STLPoint3D vec = new STLPoint3D(min(0),min(1),min(2));
        return vec;
        }

    @Override
    public void load(DataInputStream in) throws IOException {
        loaded = false;
        modelName = in.readUTF();
        normalizedScale = in.readDouble();
        int sz = in.readInt();
        list = new ArrayList<>();
        while(sz--!=0){
            STLTriangle tr = new STLTriangle();
            tr.load(in);
            list.add(tr);
        }
        loaded = true;
        }
    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeUTF(modelName);
        out.writeDouble(normalizedScale);
        out.writeInt(list.size());
        for(STLTriangle tr : list){
            tr.save(out);
            }
        }
}
