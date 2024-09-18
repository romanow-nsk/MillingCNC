package epos.slm3d.slicer;

import epos.slm3d.stl.STLLine;
import epos.slm3d.stl.STLReferedPoint;

/**
 * Created by romanow on 13.02.2018.
 */
public interface I_ErrorDraw {
    public void onLine(int mode,STLLine line);
    public void onReferedPoint(int mode, STLReferedPoint point);
}
