package epos.slm3d.settings;

import epos.slm3d.controller.USBCodes;
import epos.slm3d.io.BinInputStream;
import epos.slm3d.io.BinOutputStream;
import epos.slm3d.io.I_File;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by romanow on 01.12.2017.
 */
public class Control implements I_File{
    /** в команде смены слоя - в единицах 0.01 мм */
    public IndexedParameter NextLayerMovingM4Step = new IndexedParameter(USBCodes._ControlNextLayerMovingM4Step,5);
    /** в команде смены слоя - в единицах  0.01 мм */
    public IndexedParameter NextLayerMovingM3Step = new IndexedParameter(USBCodes._ControlNextLayerMovingM3Step,5);
    public final IntParameter LayerProcessingRepeat = new IntParameter(1);  //--
    public final IntParameter LayerProcessingM_Shift = new IntParameter(0); //--
    public void load(DataInputStream in) throws IOException {
        NextLayerMovingM4Step.load(in);
        NextLayerMovingM3Step.load(in);
        LayerProcessingRepeat.load(in);
        LayerProcessingM_Shift.load(in);
    }
    public void save(DataOutputStream in) throws IOException {
        NextLayerMovingM4Step.save(in);
        NextLayerMovingM3Step.save(in);
        LayerProcessingRepeat.save(in);
        LayerProcessingM_Shift.save(in);
        }
    public void setNotNull() {
        /** в команде смены слоя - в единицах 0.01 мм */
        if (NextLayerMovingM4Step == null)
            NextLayerMovingM4Step = new IndexedParameter(USBCodes._ControlNextLayerMovingM4Step, 5);
        /** в команде смены слоя - в единицах  0.01 мм */
        if (NextLayerMovingM3Step == null)
            NextLayerMovingM3Step = new IndexedParameter(USBCodes._ControlNextLayerMovingM3Step, 5);
        }
    }
