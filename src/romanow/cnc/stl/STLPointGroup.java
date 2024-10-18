package romanow.cnc.stl;

import java.util.ArrayList;

/**
 * Created by romanow on 05.12.2017.
 */
public class STLPointGroup {
    /** вектор точек */
    private ArrayList<STLReferedPoint> points = new ArrayList<>();
    public ArrayList<STLReferedPoint> points(){ return points; }
    public STLPointGroup(){}
    /** добавить группу линий */
    public void add(ArrayList<STLReferedPoint> two){
        for(STLReferedPoint zz:two)
            points.add(zz);
        }
    public int size(){ return points.size(); }
    public STLReferedPoint get(int idx){ return points.get(idx); }
    public STLReferedPoint remove(int idx){ return points.remove(idx); }
    public boolean remove(STLReferedPoint xx){
        int bf = points.size();
        boolean zz = points.remove(xx);
        if (bf == points.size())
            System.out.println("7: не найден в векторе "+size()+": "+xx);
        return zz;
        }
    public void add(STLReferedPoint xx){
        points.add(xx);
        }
    public void insert(int idx, STLReferedPoint xx){
        points.add(idx,xx);
        }
    public void set(ArrayList<STLReferedPoint> xx){
        points = xx;
        }
}
