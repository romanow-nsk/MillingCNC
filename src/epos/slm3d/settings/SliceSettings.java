package epos.slm3d.settings;

import epos.slm3d.settings.BooleanParameter;
import epos.slm3d.settings.FloatParameter;
import epos.slm3d.settings.IntParameter;


/**
 * Created by romanow on 01.12.2017.
 */
public class SliceSettings{
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
        return out;
    }
}

