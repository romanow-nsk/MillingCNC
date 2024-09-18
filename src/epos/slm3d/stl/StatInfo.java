package epos.slm3d.stl;

import java.util.ArrayList;

/**
 * Created by romanow on 14.06.2018.
 */
public class StatInfo {
    public double mid=0;
    public double sko=0;
    public double min=0;
    public double max=0;
    public double sq=0;
    public int count=0;
    public ArrayList<Double> values=new ArrayList<>();
    public void addValue(double vv){
        if (count==0)
            min=max=vv;
        if (vv < min) min = vv;
        if (vv > max) max = vv;
        mid += vv;
        sq += vv * vv;
        count++;
        values.add(new Double(vv));
        }
    public void calcStatistic(){
        mid/=count;
        sko = Math.sqrt(sq/count-mid*mid);
        }
    public String toString(){
        return String.format("%8.5f +/- %8.5f (%8.5f...%8.5f)",mid,sko,min,max);
        }
}
