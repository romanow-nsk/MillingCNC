package epos.slm3d.stl;

import epos.slm3d.utils.Utils;
import epos.slm3d.utils.Values;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by romanow on 15.06.2018.
 */
public class STLPoint2D implements I_STLPoint2D {
    private double x=0;
    private double y=0;
    @Override
    public double x(){ return x; }
    @Override
    public void x(double x0){ x=x0; }
    @Override
    public double y(){ return y; }
    @Override
    public void y(double x0){ y=x0; }
    public STLPoint2D(STLPoint2D two){
        x=two.x; y=two.y;
        }
    public STLPoint2D() {}
    public STLPoint2D(double x, double y) {
        this.x = x;
        this.y = y;
        }
    /** Расстояние по плоскости XY */
    @Override
    public double diffXY2(I_STLPoint2D two){
        double vv1 = x-two.x();
        double vv2 = y-two.y();
        double vv = vv1*vv1+vv2*vv2;
        return vv;
        }
    @Override
    public double diffXY(I_STLPoint2D two){
        return Math.sqrt(diffXY2(two));
        }
    @Override
    public boolean equalsAbout(I_STLPoint2D two){
        return diffXY(two) < Values.EqualDifference;
        }
    @Override
    public  void shift(double xx, double yy){
        x+=xx; y+=yy;
        }
    /** Копирование координат другой точки */
    @Override
    public void setCoordinates(I_STLPoint2D two){
        x = two.x();
        y = two.y();
        }
    @Override
    public int classId() { return Values.classId2D; }
    @Override
    public I_STLPoint2D clone(){
        return new STLPoint2D(x,y);
        }

    @Override
    public double value(int mode){
        switch(mode){
            case 0:
                return x;
            case 1:
                return 0;
            case 2:
                return y;
            case 3:
                return 0;
            }
        return 0;
        }

    @Override
    public void load(DataInputStream in) throws IOException {
        x=in.readDouble();
        y=in.readDouble();
        }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeDouble(x);
        out.writeDouble(y);
        }
    @Override
    public void saveFloat(DataOutputStream out) throws IOException {
        double mas = Values.PrinterFieldSize/2;
        out.write(Utils.intToBytes(Float.floatToIntBits((float)(x*mas))));
        out.write(Utils.intToBytes(Float.floatToIntBits((float)(y*mas))));
        }
    @Override
    public String toString() {
        return String.format("x=%6.4f y=%6.4f",x,y);
    }
    @Override
    public String dump(){
        return String.format("x=%25.23f y=%25.23f",x,y);
    }
    @Override
    public boolean equals(I_STLPoint2D two){
        return x==two.x() && y==two.y();
        }

    // поворот системы координат
    // x = x′ cosφ − y′⋅sinφ
    // y = x′ sinφ + y′⋅cosφ.
    // Сетка параллеьно направлению лучей
    /** поворот системы координат вокруг Z- новая точка */
    @Override
    public I_STLPoint2D rotateXY(MyAngle ang){
        return new STLPoint2D(x*ang.cosf-y*ang.sinf,x*ang.sinf+y*ang.cosf);
    }
    /** Угол между 3 точками  (градусы) - текущий, центр окружности */
    public double getAngleABC(I_STLPoint2D b, I_STLPoint2D c) {
        STLPoint2D ab = new STLPoint2D(b.x() - x, b.y() - y );
        STLPoint2D cb = new STLPoint2D(b.x() - c.x(), b.y() - c.y());
        double dot = (ab.x * cb.x + ab.y * cb.y); // dot product
        double cross = (ab.x * cb.y - ab.y * cb.x); // cross product
        double alpha = Math.atan2(cross, dot);
        return alpha * 180. / Math.PI;
        }
    public void invertX(){ x = -x;}
    public void invertY(){ y = -y;}
}
