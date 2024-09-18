package epos.slm3d.slicer;

import epos.slm3d.stl.*;
import epos.slm3d.utils.Values;

import java.util.ArrayList;

/**
 * Created by romanow on 18.03.2018.
 */
public class SliceGenetarorWalls implements I_TestLoopGenerator{
    private STLLineGroup createOne(int i){
        STLLineGroup out = new STLLineGroup();
        double xx = -0.1+i*0.04;
        out.add(new STLLine(new STLPoint2D(xx,-0.1),new STLPoint2D(xx+0.02,-0.1)));
        out.add(new STLLine(new STLPoint2D(xx+0.02,-0.1),new STLPoint2D(xx+0.02,0.1)));
        out.add(new STLLine(new STLPoint2D(xx+0.02,0.1),new STLPoint2D(xx,0.1)));
        out.add(new STLLine(new STLPoint2D(xx,0.1),new STLPoint2D(xx,-0.1)));
        return out;
        }
    @Override
    public STLLineGroup createOrig(double z) {
        STLLineGroup out = new STLLineGroup();
        if (z < 1/(Values.PrinterFieldSize/2)){
            out.add(new STLLine(new STLPoint2D(-0.1,-0.1),new STLPoint2D(-0.1,0.1)));
            out.add(new STLLine(new STLPoint2D(-0.1,0.1),new STLPoint2D(0.1,0.1)));
            out.add(new STLLine(new STLPoint2D(0.1,0.1),new STLPoint2D(0.1,-0.1)));
            out.add(new STLLine(new STLPoint2D(0.1,-0.1),new STLPoint2D(-0.1,-0.1)));
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
        if (z < 1/(Values.PrinterFieldSize/2)){
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
        return 10/(Values.PrinterFieldSize/2);
    }
}
