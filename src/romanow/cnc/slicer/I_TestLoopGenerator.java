package romanow.cnc.slicer;

import romanow.cnc.stl.STLLineGroup;
import romanow.cnc.stl.STLLoop;

import java.util.ArrayList;

/**
 * Created by romanow on 18.03.2018.
 */
public interface I_TestLoopGenerator {
    public STLLineGroup createOrig(double z);
    public ArrayList<STLLoop> createLoops(double z);
    public double createZmax();
}
