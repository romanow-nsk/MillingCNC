package epos.slm3d.slicer;

import epos.slm3d.stl.STLLine;

/**
 * Created by romanow on 07.12.2017.
 */
public interface I_LineSlice {
    public void onSliceLayer();
    public void onCutterUpDown(boolean up, double z);
    public void onSliceLine(STLLine line);
    public boolean isFinish();
    public void onSliceError(SliceError error);
    public void notify(int level, String mes);
    }
