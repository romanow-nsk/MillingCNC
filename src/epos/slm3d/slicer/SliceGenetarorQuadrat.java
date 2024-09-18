package epos.slm3d.slicer;

import epos.slm3d.stl.*;
import epos.slm3d.utils.Values;

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
        return 10/(Values.PrinterFieldSize/2);
    }
}
