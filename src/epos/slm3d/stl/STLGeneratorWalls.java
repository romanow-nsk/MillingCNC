package epos.slm3d.stl;

import epos.slm3d.utils.Values;

import java.util.ArrayList;

/**
 * Created by romanow on 19.03.2018.
 */
public class STLGeneratorWalls implements I_ModelGenerator{
    private double sz;
    ArrayList<STLTriangle> out=new ArrayList<>();
    public STLGeneratorWalls(double size){
        sz = size;
        }
    private void add(ArrayList<STLTriangle> xx){
        for (STLTriangle zz : xx)
            out.add(zz);
        }
    @Override
    public ArrayList<STLTriangle> generate() {
        out.clear();
        double z0 = 0.2*sz;
        add(new STLPolygon(-sz,-sz,sz,sz,0).createTriangles());
        add(new STLPolygon(-sz,-sz,sz,sz,z0).createTriangles());
        add(new STLPolygon(-sz,-sz,-sz,sz,0,z0).createTriangles());
        add(new STLPolygon(-sz,-sz,sz,-sz,0,z0).createTriangles());
        add(new STLPolygon(-sz,sz,sz,sz,0,z0).createTriangles());
        add(new STLPolygon(sz,-sz,sz,sz,0,z0).createTriangles());
        for(int i=0;i<5;i++){
            double x0=-sz+2*i*(sz/5);
            double x1= x0+sz/5;
            add(new STLPolygon(x0,-sz,x1,sz,z0).createTriangles());
            add(new STLPolygon(x0,-sz,x1,sz,sz).createTriangles());
            add(new STLPolygon(x0,-sz,x0,sz,z0,sz).createTriangles());
            add(new STLPolygon(x1,-sz,x1,sz,z0,sz).createTriangles());
            add(new STLPolygon(x0,-sz,x1,-sz,z0,sz).createTriangles());
            add(new STLPolygon(x0,sz,x1,sz,z0,sz).createTriangles());
            }
        return out;
    }

    @Override
    public String name() {
        return "Решетка";
    }
}
