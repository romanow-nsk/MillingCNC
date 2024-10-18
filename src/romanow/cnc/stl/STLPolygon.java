package romanow.cnc.stl;

/**
 * Created by romanow on 19.03.2018.
 */

import java.util.ArrayList;

/** Полигон в плоскости - без контроля */
public class STLPolygon {
    private ArrayList<STLPoint3D> points = new ArrayList<>();
    private STLTriangle createOne(int idx){
        STLTriangle xx = new STLTriangle(new STLPoint3D(points.get(idx)),new STLPoint3D(points.get(idx+1)),new STLPoint3D(points.get(idx+2)));
        points.remove(idx|+1);
        return xx;
        }
    public void add(STLPoint3D pp){ points.add(pp); }
    ArrayList<STLTriangle> createTriangles(){
        ArrayList<STLTriangle> out = new ArrayList<>();
        if (points.size()<=2)
            return out;
        while(points.size()>=3){
            for(int i=0;i<points.size()-2;i++){
                out.add(createOne(i));
                }
            }
        return out;
        }
    /** Горизонтальный кважрат */
    public STLPolygon(double x1,double y1, double x2, double y2, double z){
        add(new STLPoint3D(x1,y1,z));
        add(new STLPoint3D(x1,y2,z));
        add(new STLPoint3D(x2,y2,z));
        add(new STLPoint3D(x2,y1,z));
        }
    /** Вертикальный кважрат */
    public STLPolygon(double x1,double y1, double x2, double y2, double z1, double z2){
        add(new STLPoint3D(x1,y1,z1));
        add(new STLPoint3D(x1,y1,z2));
        add(new STLPoint3D(x2,y2,z2));
        add(new STLPoint3D(x2,y2,z1));
    }
}
