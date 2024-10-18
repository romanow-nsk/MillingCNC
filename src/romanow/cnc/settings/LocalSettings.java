/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.settings;

/**
 *
 * @author romanow
 */
public class LocalSettings {
    //--------------------------- Собственные вычисляемые -------------------------------------
    /** размер X мм */
    public FloatParameter MarkingFieldWidth = new FloatParameter(0);
    /** размер Y мм */
    public FloatParameter MarkingFieldHight = new FloatParameter(0);
    /** размер Z мм */
    public FloatParameter Z = new FloatParameter(0);
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
    /** диаметр фрезы мм */
    public FloatParameter CutterDiameter = new FloatParameter(1.0);
    /** коррекция шага для диаметра/2 мм */
    public FloatParameter StepMinus = new FloatParameter(0.02);
    /** шаг фрезерования - толщина слоя  */
    public FloatParameter VerticalStep = new FloatParameter(1.0);
    //---------------------------------------------------------------------------
    public void setNotNull(){
        if (MarkingFieldWidth==null) MarkingFieldWidth = new FloatParameter(0);
        if (MarkingFieldHight==null) MarkingFieldHight = new FloatParameter(0);
        if (BlankWidth==null) BlankWidth = new FloatParameter(0);
        if (BlankHight==null) BlankHight = new FloatParameter(0);
        if (CutterDiameter==null) CutterDiameter = new FloatParameter(0);
        if (Z==null) Z = new FloatParameter(0);
        if (ZStart==null) ZStart = new FloatParameter(0);
        if (ZFinish==null) ZFinish = new FloatParameter(0);
        if (PageServoOffsetsLeft==null) PageServoOffsetsLeft = new FloatParameter(0);
        if (PageServoOffsetsTop==null) PageServoOffsetsTop = new FloatParameter(0);
    }
}
