package romanow.cnc.slicer;


import romanow.cnc.stl.STLLine;
import romanow.cnc.stl.STLLineGroup;
import romanow.cnc.stl.STLLoop;
import romanow.cnc.stl.STLPoint2D;

import java.util.ArrayList;

/**
 * Created by romanow on 18.03.2018.
 */
public class SliceGenetarorQuadrat implements I_TestLoopGenerator{
    @Override
    public STLLineGroup createOrig(double z) {
        STLLineGroup out = new STLLineGroup();
        out.add(new STLLine(new STLPoint2D(-0.1,-0.1),new STLPoint2D(-0.1,0.1)));
        out.add(new STLLine(new STLPoint2D(-0.1,0.1),new STLPoint2D(0.1,0.1)));
        out.add(new STLLine(new STLPoint2D(0.1,0.1),new STLPoint2D(0.1,-0.1)));
        out.add(new STLLine(new STLPoint2D(0.1,-0.1),new STLPoint2D(-0.1,-0.1)));
        return out;
        }

    @Override
    public ArrayList<STLLoop> createLoops(double z) {
        ArrayList<STLLoop> out = new ArrayList<>();
        STLLoop x = new STLLoop();
        x.add(createOrig(z));
        out.add(x);
        return out;
        }

    @Override
    public double createZmax() {
        return 10;
    }
}
