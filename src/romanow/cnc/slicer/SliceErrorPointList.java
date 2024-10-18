package romanow.cnc.slicer;

import romanow.cnc.stl.STLLine;
import romanow.cnc.stl.STLReferedPoint;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by romanow on 13.02.2018.
 */
public class SliceErrorPointList extends SliceError{
    private ArrayList<STLReferedPoint> points;
    private double z=0;
    public SliceErrorPointList(){ super(0);}
    public SliceErrorPointList(int errorCode, ArrayList<STLReferedPoint> points0){
        super(errorCode);
        points = points0;
        }
    public ArrayList<STLReferedPoint> points(){ return points; }
    @Override
    public void setZ(double z0) {
        z = z0;
        }
    @Override
    public void drawAll(I_ErrorDraw onEvent) {
        for(STLReferedPoint pp : points)
            onEvent.onReferedPoint(getErrorCode(),pp);
        for(int i=0;i<points.size()-1;i++)
            onEvent.onLine(getErrorCode(),new STLLine(points.get(i),points.get(i+1)));
        }
    public void load(DataInputStream in) throws IOException {
        super.load(in);
        int sz = in.readInt();
        points = new ArrayList<>();
        while(sz--!=0){
            STLReferedPoint point = new STLReferedPoint();
            point.loadFull(in);
            points.add(point);
        }
    }
    public void save(DataOutputStream out) throws IOException{
        super.save(out);
        out.writeInt(points.size());
        for(STLReferedPoint point : points)
            point.saveFull(out);
        }
    public void shift(double xx, double yy){
        for(STLReferedPoint point : points)
            point.shift(xx,yy);
        }
}
