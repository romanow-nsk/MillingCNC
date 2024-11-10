/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.settings;

import romanow.cnc.Values;
import romanow.cnc.io.I_File;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author romanow
 */
public class ModelSettings implements I_File {
    //--------------------------- Собственные вычисляемые -------------------------------------
    /** размер X мм */
    public FloatParameter ModelWidth = new FloatParameter(0);
    /** размер Y мм */
    public FloatParameter ModelHight = new FloatParameter(0);
    /** размер Z мм */
    public FloatParameter ModelZ = new FloatParameter(0);
    /** размер Z мм */
    public FloatParameter ZStart = new FloatParameter(0);
    /** размер Z мм */
    public FloatParameter ZFinish = new FloatParameter(0);
    /** Смещение по X влево мм */
    public FloatParameter PageServoOffsetsLeft = new FloatParameter(0);
    /** Смещение по Y вверх мм */
    public FloatParameter PageServoOffsetsTop = new FloatParameter(0);
    /** заготовка X мм */
    public FloatParameter BlankWidth = new FloatParameter(0);
    /** заготовка Y мм */
    public FloatParameter BlankHight = new FloatParameter(0);
    /** заготовка Z мм */
    public FloatParameter BlankZ = new FloatParameter(0);
    /** диаметр фрезы мм */
    public FloatParameter CutterDiameter = new FloatParameter(1.0);
    /** коррекция шага для диаметра/2 мм */
    public FloatParameter StepMinus = new FloatParameter(0.02);
    /** шаг фрезерования - толщина слоя  */
    public FloatParameter VerticalStep = new FloatParameter(1.0);
    /** масштаб */
    public FloatParameter ScaleFactor = new FloatParameter(1);
    /** авто центрирование */
    public BooleanParameter AutoCenter = new BooleanParameter(true);
    /** авто масштабирование */
    public BooleanParameter AutoScale = new BooleanParameter(true);
    public ModelSettings clone(){
        ModelSettings out = new ModelSettings();
        out.ModelWidth = ModelWidth.clone();
        out.ModelHight = ModelHight.clone();
        out.ModelZ = ModelZ.clone();
        out.ZStart = ZStart.clone();
        out.ZFinish = ZFinish.clone();
        out.PageServoOffsetsTop = PageServoOffsetsTop.clone();
        out.PageServoOffsetsLeft =PageServoOffsetsLeft.clone();
        out.BlankWidth = BlankWidth.clone();
        out.BlankHight = BlankHight.clone();
        out.BlankZ = BlankZ.clone();
        out.CutterDiameter = CutterDiameter.clone();
        out.StepMinus = StepMinus.clone();
        out.VerticalStep = VerticalStep.clone();
        out.ScaleFactor = ScaleFactor.clone();
        out.AutoScale = AutoScale.clone();
        out.AutoCenter = AutoCenter.clone();
        return out;
        }
    //---------------------------------------------------------------------------
    public void setNotNull(){
        if (ModelWidth==null) ModelWidth = new FloatParameter(0);
        if (ModelHight==null) ModelHight = new FloatParameter(0);
        if (BlankWidth==null) BlankWidth = new FloatParameter(0);
        if (BlankHight==null) BlankHight = new FloatParameter(0);
        if (CutterDiameter==null) CutterDiameter = new FloatParameter(0);
        if (ModelZ ==null) ModelZ = new FloatParameter(0);
        if (ZStart==null) ZStart = new FloatParameter(0);
        if (ZFinish==null) ZFinish = new FloatParameter(0);
        if (PageServoOffsetsLeft==null) PageServoOffsetsLeft = new FloatParameter(0);
        if (PageServoOffsetsTop==null) PageServoOffsetsTop = new FloatParameter(0);
        if (BlankWidth==null) BlankWidth = new FloatParameter(0);
        if (BlankHight==null) BlankHight = new FloatParameter(0);
        if (BlankZ==null) BlankZ = new FloatParameter(0);
        if (CutterDiameter==null) CutterDiameter = new FloatParameter(1);
        if (StepMinus==null) StepMinus = new FloatParameter(0.02);
        if (VerticalStep==null) VerticalStep = new FloatParameter(1);
        if (ScaleFactor==null) ScaleFactor = new FloatParameter(1);
        if (AutoCenter==null) AutoCenter = new BooleanParameter(true);
        if (AutoScale==null) AutoScale = new BooleanParameter(true);


        }

    @Override
    public void load(DataInputStream in) throws IOException {
        ModelWidth.load(in);
        ModelHight.load(in);
        ModelZ.load(in);
        ZStart.load(in);
        ZFinish.load(in);
        PageServoOffsetsLeft.load(in);
        PageServoOffsetsTop.load(in);
        BlankWidth.load(in);
        BlankHight.load(in);
        BlankZ.load(in);
        CutterDiameter.load(in);
        StepMinus.load(in);
        VerticalStep.load(in);
        ScaleFactor.load(in);
        AutoCenter.load(in);
        AutoScale.load(in);
        }

    @Override
    public void save(DataOutputStream in) throws IOException {
        ModelWidth.save(in);
        ModelHight.save(in);
        ModelZ.save(in);
        ZStart.save(in);
        ZFinish.save(in);
        PageServoOffsetsLeft.save(in);
        PageServoOffsetsTop.save(in);
        BlankWidth.save(in);
        BlankHight.save(in);
        BlankZ.save(in);
        CutterDiameter.save(in);
        StepMinus.save(in);
        VerticalStep.save(in);
        ScaleFactor.save(in);
        AutoCenter.save(in);
        AutoScale.save(in);
    }
}
