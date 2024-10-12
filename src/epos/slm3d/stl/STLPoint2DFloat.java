package epos.slm3d.stl;

import epos.slm3d.utils.Utils;
import epos.slm3d.utils.Values;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by romanow on 15.06.2018.
 */
public class STLPoint2DFloat implements I_STLPoint2D {
    private float x=0;
    private float y=0;
    @Override
    public double x(){ return x; }
    @Override
    public void x(double x0){ x=(float)x0; }
    @Override
    public double y(){ return y; }
    @Override
    public void y(double x0){ y=(float)x0; }

    public STLPoint2DFloat(STLPoint2DFloat two){
        x=two.x; y=two.y;
        }
    public STLPoint2DFloat() {}
    public STLPoint2DFloat(float x, float y) {
        this.x = x;
        this.y = y;
        }
    /** Расстояние по плоскости XY */
    @Override
    public double diffXY2(I_STLPoint2D two){
        double vv = (x-two.x())*(x-two.x())+(y-two.y())*(y-two.y());
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
    public boolean equalsAboutX(I_STLPoint2D two) {
        return Math.abs(x-two.x())<Values.EqualDifference;
        }

    @Override
    public boolean equalsAboutY(I_STLPoint2D two) {
        return Math.abs(y-two.y())<Values.EqualDifference;
        }

    @Override
    public  void shift(double xx, double yy){
        x+=xx; y+=yy;
        }
    /** Копирование координат другой точки */
    @Override
    public void setCoordinates(I_STLPoint2D two){
        x = (float)two.x();
        y = (float)two.y();
        }
    @Override
    public I_STLPoint2D clone(){
        return new STLPoint2DFloat(x,y);
        }
    @Override
    public int classId() { return Values.classId2dFloat; }
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
        x=in.readFloat();
        y=in.readFloat();
        }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeFloat(x);
        out.writeFloat(y);
        }
    @Override
    public void saveFloat(DataOutputStream out) throws IOException {
        out.write(Utils.intToBytes(Float.floatToIntBits((float)(x))));
        out.write(Utils.intToBytes(Float.floatToIntBits((float)(y))));
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
    public STLPoint2DFloat rotateXY(MyAngle ang){
        return new STLPoint2DFloat((float)(x*ang.cosf-y*ang.sinf),(float)(x*ang.sinf+y*ang.cosf));
        }
    @Override
    public void invertX() {
        x = -x;
        }

}
