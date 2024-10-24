package romanow.cnc.stl;

import romanow.cnc.Values;
import romanow.cnc.settings.WorkSpace;
import romanow.cnc.utils.Pair;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class GCodePoints {
    public final ArrayList<I_STLPoint2D> points = new ArrayList<>();
    private final String comment;
    public GCodePoints(String comment) {
        this.comment = comment;
        }
    public void add(I_STLPoint2D point2D){
        points.add(point2D);
        }
    public void write(BufferedWriter out, double layerZ, double x0, double y0) throws IOException {
        out.write(comment);
        out.newLine();
        out.write(String.format(Locale.US,"G30 G91 Z%6.3f",20.0));
        out.newLine();
        out.write(String.format(Locale.US,"G00 X%6.3f Y%6.3f F500",points.get(0).x()+x0,points.get(0).y()+y0));
        out.newLine();
        out.write(String.format(Locale.US,"G30 G91 Z%6.3f",-layerZ));
        out.newLine();
        int idx=0;
        if (WorkSpace.ws().global().slice.ARCGCodeMode.getVal()){
            ArrayList<ArcPointsGroup> groups = createArcs(layerZ);
            for(ArcPointsGroup group :  groups){
                for(int i=idx; i<=group.idx;i++){        // Начальную точку дуги надо вывести
                    out.write(String.format(Locale.US,"G01 X%6.3f Y%6.3f",points.get(i).x()+x0,points.get(i).y()+y0));
                    out.newLine();
                    }
                //------------ TODO определить G02 или G03
                int jj = idx+group.count-1;
                out.write(String.format(Locale.US,"G02 X%6.3f Y%6.3f R%6.3f F500 ",points.get(jj).x()+x0,points.get(jj).y()+y0,group.radius));
                out.newLine();
                idx = jj+1;
                }
            for(int i=idx; i<points.size();i++){        // Начальную точку дуги надо вывести
                out.write(String.format(Locale.US,"G01 X%6.3f Y%6.3f F500",points.get(i).x()+x0,points.get(i).y()+y0));
                out.newLine();
                }
            }
        else{
            for(int i=1;i<points.size();i++){
                out.write(String.format(Locale.US,"G01 X%6.3f Y%6.3f F500",points.get(i).x()+x0,points.get(i).y()+y0));
                out.newLine();
                }
            }
        }
    //------------------ Пара - индекс группы
    public ArrayList<ArcPointsGroup> createArcs(double layerZ){
        ArrayList<I_STLPoint2D> centers = new ArrayList<>();
        ArrayList<Double> radiuses = new ArrayList<>();
        ArrayList<ArcPointsGroup> out = new ArrayList<>();
        if (points.size()<3)
            return out;
        for(int i=1;i<points.size()-1;i++){
            if (points.get(i-1).diffXY(points.get(i))>Values.ARCLineMax)
                radiuses.add(Values.ARCRadiusMax*2);
            else
                radiuses.add(STLUtils.curvature(points.get(i-1),points.get(i),points.get(i+1)));
            centers.add(STLUtils.center(points.get(i-1),points.get(i),points.get(i+1)));
            }
        I_STLPoint2D ff = centers.get(0);
        double radius = radiuses.get(0);
        int idx=0;
        int cnt=3;
        for (idx=0; idx<centers.size() && radiuses.get(idx) > Values.ARCRadiusMax;idx++);
        if (idx == centers.size())
            return  out;
        for(int i=0;i<centers.size();i++) {
            boolean bb1 = radiuses.get(i) < Values.ARCRadiusMax;
            boolean bb2 = ff.diffXY(centers.get(i)) < Values.ARCCenterDiff &&  Math.abs(radius - radiuses.get(i)) < Values.ARCRadiusDiff;
            if (bb1 && bb2)
                cnt++;
            else {
                if (cnt > Values.ARCPointsCount){
                    WorkSpace.ws().notify(Values.info, "z="+layerZ+" Кривая R=" + radiuses.get(idx) + " точек=" + (cnt+2)+" idx="+idx);
                    out.add(new ArcPointsGroup(idx,cnt+2,ff,radius));
                    }
                cnt = 3;
                ff = centers.get(i);
                radius = radiuses.get(i);
                idx=i-1;
                }
            }
        if (cnt > Values.ARCPointsCount){
            WorkSpace.ws().notify(Values.info,"z="+layerZ+" Кривая R="+radiuses.get(idx)+" точек="+(cnt+2)+" idx="+idx);
            out.add(new ArcPointsGroup(idx,cnt+2,ff,radius));
            }
        return out;
        }
}
