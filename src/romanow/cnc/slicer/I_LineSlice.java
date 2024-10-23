package romanow.cnc.slicer;

import romanow.cnc.stl.I_STLPoint2D;
import romanow.cnc.stl.STLLine;

/**
 * Created by romanow on 07.12.2017.
 */
public interface I_LineSlice {
    public void onSliceLayer();
    public void onLineGroup();
    public void onSliceLine(STLLine line);
    public boolean isFinish();
    public void onSliceError(SliceError error);
    public void notify(int level, String mes);
    }
