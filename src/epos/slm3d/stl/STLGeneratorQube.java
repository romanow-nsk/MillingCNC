package epos.slm3d.stl;

import epos.slm3d.utils.Values;

import java.util.ArrayList;

/**
 * Created by romanow on 19.03.2018.
 */
public class STLGeneratorQube implements I_ModelGenerator{
    private double sz;
    ArrayList<STLTriangle> out=new ArrayList<>();
    public STLGeneratorQube(double size){
        sz = size/ (Values.PrinterFieldSize/2);
        }
    private void add(ArrayList<STLTriangle> xx){
        for (STLTriangle zz : xx)
            out.add(zz);
        }
    @Override
    public ArrayList<STLTriangle> generate() {
        out.clear();
        add(new STLPolygon(-sz,-sz,sz,sz,0).createTriangles());
        add(new STLPolygon(-sz,-sz,sz,sz,sz).createTriangles());
        add(new STLPolygon(-sz,-sz,-sz,sz,0,sz).createTriangles());
        add(new STLPolygon(-sz,-sz,sz,-sz,0,sz).createTriangles());
        add(new STLPolygon(-sz,sz,sz,sz,0,sz).createTriangles());
        add(new STLPolygon(sz,-sz,sz,sz,0,sz).createTriangles());
        return out;
    }

    @Override
    public String name() {
        return "Куб";
    }
}
