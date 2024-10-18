package romanow.cnc.slicer;

import romanow.cnc.settings.WorkSpace;

import romanow.cnc.stl.STLLine;
import romanow.cnc.stl.STLLineGroup;
import romanow.cnc.stl.STLLoop;
import romanow.cnc.stl.STLPoint2D;

import java.util.ArrayList;

/**
 * Created by romanow on 18.03.2018.
 */
public class SliceGenetarorWalls implements I_TestLoopGenerator{
    private STLLineGroup createOne(int i){
        STLLineGroup out = new STLLineGroup();
        double dd = WorkSpace.ws().global().global.WorkFieldSize.getVal();
        double xx = (-0.1+i*0.04) * dd/2;
        out.add(new STLLine(new STLPoint2D(xx,-0.1),new STLPoint2D(xx+0.02,-0.1)));
        out.add(new STLLine(new STLPoint2D(xx+0.02,-0.1),new STLPoint2D(xx+0.02,0.1)));
        out.add(new STLLine(new STLPoint2D(xx+0.02,0.1),new STLPoint2D(xx,0.1)));
        out.add(new STLLine(new STLPoint2D(xx,0.1),new STLPoint2D(xx,-0.1)));
        return out;
        }
    @Override
    public STLLineGroup createOrig(double z) {
        STLLineGroup out = new STLLineGroup();
        double vv = WorkSpace.ws().global().global.WorkFieldSize.getVal();
        if (z < vv){
            double dd = 0.1*vv/2;
            out.add(new STLLine(new STLPoint2D(-dd,-dd),new STLPoint2D(-dd,dd)));
            out.add(new STLLine(new STLPoint2D(-dd,dd),new STLPoint2D(dd,dd)));
            out.add(new STLLine(new STLPoint2D(dd,dd),new STLPoint2D(dd,-dd)));
            out.add(new STLLine(new STLPoint2D(dd,-dd),new STLPoint2D(-dd,-dd)));
            return out;
            }
        for(int i=0;i<5;i++){
            out.add(createOne(i));
            }
        return out;
        }

    @Override
    public ArrayList<STLLoop> createLoops(double z) {
        ArrayList<STLLoop> out = new ArrayList<>();
        double dd = WorkSpace.ws().global().global.WorkFieldSize.getVal();
        if (z < dd){
            STLLoop x = new STLLoop();
            x.add(createOrig(z));
            out.add(x);
            return out;
            }
        for(int i=0;i<5;i++){
            STLLoop x = new STLLoop();
            x.add(createOne(i));
            out.add(x);
            }
        return out;
        }

    @Override
    public double createZmax() {
        return 10;
    }
}
