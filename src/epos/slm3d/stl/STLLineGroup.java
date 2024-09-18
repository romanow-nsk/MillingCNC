package epos.slm3d.stl;

import epos.slm3d.io.I_File;
import epos.slm3d.utils.Values;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by romanow on 05.12.2017.
 */
public class STLLineGroup implements I_File{
    /** вектор линий */
    private ArrayList<STLLine> lines = new ArrayList<>();
    //---------------------------------------------------------------------------------------------
    public ArrayList<STLLine> lines(){ return lines; }
    public STLLineGroup(){}
    /** добавить группу линий */
    public void add(ArrayList<STLLine> two){
        for(STLLine zz:two)
            lines.add(zz);
        }
    public STLLineGroup clone(){
        STLLineGroup out = new STLLineGroup();
        for(STLLine line:lines())
            out.add(line.clone());
        return out;
        }
    public int size(){ return lines.size(); }
    public STLLine get(int idx){ return lines.get(idx); }
    public STLLine remove(int idx){ return lines.remove(idx); }
    public void convertPointsToFloat(){
        for(STLLine zz:lines)
            zz.convertPointsToFloat();
        }
    public boolean remove(STLLine xx){
        int bf = lines.size();
        boolean zz = lines.remove(xx);
        if (bf == lines.size())
            System.out.println("7: не найден в векторе "+size()+": "+xx);
        return zz;
        }
    public void shift(double xx, double yy){
        for(STLLine line:lines)
            line.shift(xx,yy);
        }
    /** создает линию из 2 угловых точек ограничивающего прямоугольника */
    public STLLine minmax(){
        boolean first=true;
        double xmin=0,xmax=0,ymin=0,ymax=0;
            for(STLLine line : lines){
                if (first || line.one().x() < xmin) { first = false; xmin = line.one().x(); }
                if (first || line.two().x() < xmin) { first = false; xmin = line.two().x(); }
                if (first || line.one().x() > xmax) { first = false; xmax = line.one().x(); }
                if (first || line.two().x() > xmax) { first = false; xmax = line.two().x(); }
                if (first || line.one().y() < ymin) { first = false; ymin = line.one().y(); }
                if (first || line.two().y() < ymin) { first = false; ymin = line.two().y(); }
                if (first || line.one().y() > ymax) { first = false; ymax = line.one().y(); }
                if (first || line.two().y() > ymax) { first = false; ymax = line.two().y(); }
                }
        return new STLLine(new STLPoint2D(xmin,ymin),new STLPoint2D(xmax,ymax));
    }
    public void add(STLLine xx){
        lines.add(xx);
        }
    public void insert(int idx, STLLine xx){
        lines.add(idx,xx);
        }
    public void set(ArrayList<STLLine> xx){
        lines = xx;
        }
    /** Удаление коротких и одинаковых */
    public void addWithTest(ArrayList<STLLine> group) {
        for(STLLine line : group)
            addWithTest(line);
        }
    /** Удаление коротких и одинаковых */
    public void addWithTest(STLLine line) {
       if (line.lengthXY() < Values.EqualDifference)
            return;
        boolean bad=false;
        for(STLLine xx : lines)
            if (xx.equalsAbout(line)){
                bad = true;
                break;
            }
        if (!bad)
            add(line);
        }

    public void load0(DataInputStream in) throws IOException {
        lines = new ArrayList<>();
        int sz = in.readInt();
        while(sz--!=0){
            STLLine ln = new STLLine();
            ln.load(in);
            lines.add(ln);
            }
        }
    @Override
    public void load(DataInputStream in) throws IOException {
        lines = new ArrayList<>();
        int sz0= in.readInt();
        byte bb[]=new byte[sz0];
        in.read(bb);
        DataInputStream is = new DataInputStream(new ByteArrayInputStream(bb));
        int sz = is.readInt();
        while(sz--!=0){
            STLLine ln = new STLLine();
            ln.load(is);
            lines.add(ln);
        }
        is.close();
    }

    public void save0(DataOutputStream out) throws IOException {
        out.writeInt(lines.size());
        for(STLLine ln : lines)
            ln.save(out);
        }
    @Override
    public void save(DataOutputStream out) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataOutputStream xx = new DataOutputStream(os);
        xx.writeInt(lines.size());
        for(STLLine ln : lines)
            ln.save(xx);
        byte bb[]=os.toByteArray();
        out.writeInt(bb.length);
        out.write(bb);
        xx.close();
        os.close();
        }
    public void add(STLLineGroup two){
        for (STLLine line : two.lines())
            lines.add(line);
        }
    /** Поиск точки, ближайшей к линии по сумме расстояний */
    private double linePointDiff(I_STLPoint2D point,STLLine line){
        double v1 = line.lengthXY();
        double v2 = new STLLine(line.one(),point).lengthXY();
        double v3 = new STLLine(line.two(),point).lengthXY();
        return v2+v3-v1;
        }
    public I_STLPoint2D nearestPoint(I_STLPoint2D point, double dist){
        I_STLPoint2D out = null;
        double diff=10000;
        double diff1;
        int sz= lines.size();
        if (sz==0) return null;
        out = null;
        for (int i=0;i<sz;i++){
            STLLine line = lines.get(i);
            diff1 = point.diffXY(line.one());
            if (diff1 < dist && diff1 < diff){
                out = line.one();
                diff = diff1;
                }
            diff1 = point.diffXY(line.two());
            if (diff1 < dist && diff1 < diff){
                out = line.two();
                diff = diff1;
                }
        }
        return out;

        }
    public int nearest(I_STLPoint2D point){
        STLLine out = null;
        int idx=0;
        double diff=0;
        double diff1;
        int sz= lines.size();
        if (sz==0) return -1;
        out = lines.get(0);
        idx=0;
        diff = linePointDiff(point,out);
        for (int i=1;i<sz;i++){
            STLLine line = lines.get(i);
            diff1 = linePointDiff(point,line);
            if (diff1 < diff){
                out=line;
                diff = diff1;
                idx=i;
                }
            }
        return idx;
        }
    public STLPoint2D center(){
        if (lines().size()==0) return new STLPoint2D(0,0);
        double sx=0,sy=0;
        int cnt=0;
        for(STLLine ll : lines()){
            sx+=ll.one().x();
            sy+=ll.one().y();
            cnt++;
        }
        sx/=cnt;
        sy/=cnt;
        return new STLPoint2D(sx,sy);
        }
}
