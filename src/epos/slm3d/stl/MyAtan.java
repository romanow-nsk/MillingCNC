package epos.slm3d.stl;

/**
 * Created by romanow on 08.12.2017.
 */

import epos.slm3d.utils.Values;

/** угол наклона прямой через тангенс  */
public class MyAtan{
    /** вариант наклона по часовой стрелке 0,2,4,6 = по осям -x +y +x -y, 1,3,5 - соответствующие квадранты */
    private int qw=0;
    /** значение, вычисляемое из тангенса - возрастает */
    private double val;
    public MyAtan(int qw, double val) {
        this.qw = qw;       // 0,2,4,6 = по осям -x +y +x -y
        this.val = val;     // 1,3,5 - соответствующие квадранты
        }
    public boolean isVertical(){
        return qw==2 || qw==6;
        }
    public MyAtan(STLLine line){
        if (line.one().y()== line.two().y()){
            if (line.one().x() < line.two().x())
                qw=4;
            else
                qw=0;
            return;
            }
        if (line.one().x()== line.two().x()){
            if (line.one().y() < line.two().y())
                qw=2;
            else
                qw=6;
            return;
            }
        double b = (line.two().y() - line.one().y())/(line.two().x() - line.one().x());
        if (b>0){
            qw=3; val=1/b;
            }
        else{
            val = -b;
            if (line.one().x() < line.two().x())
                qw=5;
            else
                qw=1;
            }
        }
    /** сравнение углов по тангенсам */
    public double fCompare(MyAtan two){
        if (qw != two.qw)
            return qw - two.qw;
        if (Math.abs(val - two.val) < Values.EqualDifference)
            return 0;
        return val - two.val;
        }
    /** сравнение углов по тангенсам */
    public int compare(MyAtan two){
        if (qw != two.qw)
            return qw - two.qw;
        if (Math.abs(val - two.val) < Values.EqualDifference)
            return 0;
        return val < two.val ? -1 : 1;
        }
    public String toString(){ return String.format("%2d>>%5.2f",qw,val); }
    public static void main(String argv[]){
        System.out.println(new MyAtan(new STLLine(new STLPoint2D(-1,2),new STLPoint2D(1,2))));
    }
}