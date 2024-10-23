package romanow.cnc.stl;

import jdk.internal.org.objectweb.asm.tree.analysis.Value;
import romanow.cnc.Values;
import romanow.cnc.settings.WorkSpace;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class STLGCodePoints {
    public final ArrayList<I_STLPoint2D> points = new ArrayList<>();
    private final String comment;
    public STLGCodePoints(String comment) {
        this.comment = comment;
        }
    public void add(I_STLPoint2D point2D){
        points.add(point2D);
        }
    public void write(BufferedWriter out, double layerZ) throws IOException {
        prepare(layerZ);
        out.write(comment);
        out.newLine();
        out.write(String.format(Locale.US,"G30 G91 Z%6.3f",20.0));
        out.newLine();
        out.write(String.format(Locale.US,"G00 X%6.3f Y%6.3f",points.get(0).x(),points.get(0).y()));
        out.newLine();
        out.write(String.format(Locale.US,"G30 G91 Z%6.3f",-layerZ));
        out.newLine();
        for(int i=1;i<points.size();i++){
            out.write(String.format(Locale.US,"G01 X%6.3f Y%6.3f",points.get(i).x(),points.get(i).y()));
            out.newLine();
            }
        }
    public void prepare(double layerZ){
        ArrayList<I_STLPoint2D> centers = new ArrayList<>();
        ArrayList<Double> radiuses = new ArrayList<>();
        if (points.size()<3)
            return;
        for(int i=1;i<points.size()-1;i++){
            radiuses.add(STLUtils.curvature(points.get(i-1),points.get(i),points.get(i+1)));
            centers.add(STLUtils.center(points.get(i-1),points.get(i),points.get(i+1)));
            }
        I_STLPoint2D ff = centers.get(0);
        double radius = radiuses.get(0);
        int idx=0;
        int cnt=1;
        for(int i=1;i<centers.size();i++) {
            if ( /* ff.diffXY(centers.get(i)) < 1 && */ Math.abs(radius - radiuses.get(i))<0.5)
                cnt++;
            else {
                if (cnt > 2)
                    WorkSpace.ws().notify(Values.info, "z="+layerZ+" Кривая R=" + radiuses.get(idx) + " точек=" + (cnt+2)+" idx="+idx);
                cnt = 1;
                ff = centers.get(i);
                radius = radiuses.get(i);
                idx=i;
                }
            }
        if (cnt>2)
            WorkSpace.ws().notify(Values.info,"z="+layerZ+" Кривая R="+radiuses.get(idx)+" точек="+(cnt+2)+" idx="+idx);
        }
}
