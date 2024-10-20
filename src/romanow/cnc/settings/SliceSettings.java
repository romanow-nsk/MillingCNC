package romanow.cnc.settings;


import romanow.cnc.io.I_File;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by romanow on 01.12.2017.
 */
public class SliceSettings implements I_File {
    /** строчное слайсирование */
    public final static int STROKE=0;
    /** шахматное слайсирование */
    public final static int CHESS=1;
    public IntParameter Mode = new IntParameter(0,1,0);
    public FloatParameter FillParametersRaster = new FloatParameter(0.01);
    public FloatParameter FillParametersOffset = new FloatParameter(0.002);
    public FloatParameter FillParametersAngle = new FloatParameter(20);
    public FloatParameter FillParametersAngleInc = new FloatParameter(1);
    public FloatParameter FillParametersFillCell = new FloatParameter(1);
    public FloatParameter FillingFlatness = new FloatParameter(0.01);
    /** шаг слайсирования - толщина слоя  */
    public FloatParameter VerticalStep = new FloatParameter(0.05);
    /** Оптимизация перемещений */
    public BooleanParameter MoveOptimize = new BooleanParameter(true);
    /** Оконтуривание */
    public BooleanParameter SendLoops = new BooleanParameter(false);
    /** Слайсинг плоских контуров */
    public BooleanParameter FlateCircuitSlice = new BooleanParameter(false);
    /** Непрерывное слайсирование */
    public BooleanParameter FillContinuous = new BooleanParameter(false);
    /** Замыкание контуров */
    public BooleanParameter RepairLoops = new BooleanParameter(true);
    /** Контуры из отрезков одного типа */
    public BooleanParameter LoopsWithSomeLineTypes = new BooleanParameter(true);

    public void setNotNull(){
        if (Mode==null) Mode = new IntParameter(0,1,0);
        if (FillParametersRaster==null) FillParametersRaster = new FloatParameter(0.01);
        if (FillParametersOffset==null) FillParametersOffset = new FloatParameter(0.002);
        if (FillParametersAngle==null) FillParametersAngle = new FloatParameter(20);
        if (FillParametersAngleInc==null) FillParametersAngleInc = new FloatParameter(1);
        if (FillParametersFillCell==null) FillParametersFillCell = new FloatParameter(1);
        if (FillingFlatness==null) FillingFlatness = new FloatParameter(0.01);
        if (VerticalStep==null) VerticalStep = new FloatParameter(0.05);
        if (MoveOptimize==null) MoveOptimize = new BooleanParameter(true);
        if (SendLoops==null) SendLoops = new BooleanParameter(false);
        if (FlateCircuitSlice==null) FlateCircuitSlice = new BooleanParameter(false);
        if (FillContinuous==null) FillContinuous = new BooleanParameter(false);
        if (RepairLoops==null) RepairLoops = new BooleanParameter(false);
        if (LoopsWithSomeLineTypes==null) LoopsWithSomeLineTypes = new BooleanParameter(false);

        }
    public SliceSettings clone(){
        SliceSettings out = new SliceSettings();
        out.Mode = Mode.clone();
        out.FillParametersRaster = FillParametersRaster.clone();
        out.FillParametersOffset = FillParametersOffset.clone();
        out.FillParametersAngle = FillParametersAngle.clone();
        out.FillParametersAngleInc = FillParametersAngleInc.clone();
        out.FillParametersFillCell = FillParametersFillCell.clone();
        out.FillingFlatness = FillingFlatness.clone();
        out.VerticalStep = VerticalStep.clone();
        out.MoveOptimize = MoveOptimize.clone();
        out.SendLoops = SendLoops.clone();
        out.FlateCircuitSlice = FlateCircuitSlice.clone();
        out.FillContinuous = FillContinuous.clone();
        out.RepairLoops = RepairLoops.clone();
        out.LoopsWithSomeLineTypes = LoopsWithSomeLineTypes.clone();
        return out;
        }

    public void load(DataInputStream in) throws IOException {
        Mode.load(in);
        FillParametersRaster.load(in);
        FillParametersOffset.load(in);
        FillParametersAngle.load(in);
        FillParametersAngleInc.load(in);
        FillParametersFillCell.load(in);
        FillingFlatness.load(in);
        //VerticalStep.load(in);
        MoveOptimize.load(in);
        SendLoops.load(in);
        FlateCircuitSlice.load(in);
        FillContinuous.load(in);
        RepairLoops.load(in);
        LoopsWithSomeLineTypes.load(in);
        }
    public void save(DataOutputStream in) throws IOException {
        Mode.save(in);
        FillParametersRaster.save(in);
        FillParametersOffset.save(in);
        FillParametersAngle.save(in);
        FillParametersAngleInc.save(in);
        FillParametersFillCell.save(in);
        FillingFlatness.save(in);
        //VerticalStep.save(in);
        MoveOptimize.save(in);
        SendLoops.save(in);
        FlateCircuitSlice.save(in);
        FillContinuous.save(in);
        RepairLoops.save(in);
        LoopsWithSomeLineTypes.save(in);
        }
}

