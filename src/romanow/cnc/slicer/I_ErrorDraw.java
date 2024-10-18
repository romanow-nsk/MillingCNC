package romanow.cnc.slicer;

import romanow.cnc.stl.STLLine;
import romanow.cnc.stl.STLReferedPoint;

/**
 * Created by romanow on 13.02.2018.
 */
public interface I_ErrorDraw {
    public void onLine(int mode, STLLine line);
    public void onReferedPoint(int mode, STLReferedPoint point);
}
