/*
The MIT License (MIT)

Copyright (c) 2014 CCHall (aka Cyanobacterium aka cyanobacteruim)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package epos.slm3d.stl;

import epos.slm3d.utils.Utils;
import epos.slm3d.utils.Values;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class STLPoint3D extends STLPoint2D{
    private double z;
    public double z(){ return z; }
    public void z(double x0){ z=x0; }
    public STLPoint3D() {}
    public STLPoint3D(double x, double y, double z) {
        super(x,y);
        this.z = z;
        }
    public STLPoint3D(STLPoint3D two){
        super(two);
        z=two.z;
        }
    public double diff(STLPoint3D two){
        return Math.sqrt((x()-two.x())*(x()-two.x())+(y()-two.y())*(y()-two.y())+(z-two.z)*(z-two.z));
        }
    public boolean equals(STLPoint3D two){
        return super.equals(two) && z==two.z;
        }

    /** поворот системы координат - новая точка */
    public void rotate(int axe, MyAngle ang){
        double xx = z, yy=y(), zz=z;
        switch(axe){
            case 0:
                yy = y()*ang.cosf-z*ang.sinf;
                zz = y()*ang.sinf+z*ang.cosf;
                y(yy);
                z = zz;
                break;
            case 1:
                xx = x()*ang.cosf-z*ang.sinf;
                zz = x()*ang.sinf+z*ang.cosf;
                x(xx);
                z = zz;
                break;
            case 2:
                xx= x()*ang.cosf-y()*ang.sinf;
                yy= x()*ang.sinf+y()*ang.cosf;
                x(xx);
                y(yy);
                break;
            }
    }

    public STLPoint3D sub(STLPoint3D t1) {
        return new STLPoint3D(x() - t1.x(), y() - t1.y(), z - t1.z);
        }

    public STLPoint3D add(STLPoint3D t1) {
        return new STLPoint3D(t1.x() + x(), t1.y() + y(), t1.z + z );
        }

    public double length() {
        return Math.sqrt(this.x()*this.x() + this.y()*this.y() + this.z*this.z);
    }

    public STLPoint3D normalize() {
        double norm = 1.0 / length();
        return new STLPoint3D(this.x() * norm, this.y() * norm, this.z * norm );
        }

    public static STLPoint3D cross(STLPoint3D v1, STLPoint3D v2) {
        double tmpX;
        double tmpY;
        tmpX = v1.y() * v2.z - v1.z * v2.y();
        tmpY = v2.x() * v1.z - v2.z * v1.x();
		return new STLPoint3D(v1.x() * v2.y() - v1.y() * v2.x(),  tmpX, tmpY );
        }

    @Override
    public int hashCode() {
        long bits = 7L;
        bits = 31L * bits + Double.doubleToLongBits(x());
        bits = 31L * bits + Double.doubleToLongBits(y());
        bits = 31L * bits + Double.doubleToLongBits(z);
        return (int) (bits ^ (bits >> 32));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
            }
        if (obj instanceof STLPoint3D) {
            STLPoint3D v = (STLPoint3D) obj;
            return (x() == v.x()) && (y() == v.y()) && (z == v.z);
            }
        return false;
        }
    public STLPoint3D clone(){
        return new STLPoint3D(x(),y(),z);
        }
    public I_STLPoint2D clone2D(){
        return super.clone();
        }

    @Override
    public String toString() {
        return super.toString()+String.format(" z=%5.3f ",z);
        }
	public String dump(){
        return super.dump()+String.format(" z=%25.23f ",z);
        }

    /** Копирование координат другой точки */
    public void setCoordinates(STLPoint3D two){
        super.setCoordinates(two);
        z = two.z;
        }

    @Override
    public void load(DataInputStream in) throws IOException {
        super.load(in);
        z=in.readDouble();
        }

    @Override
    public void save(DataOutputStream out) throws IOException {
        super.save(out);
        out.writeDouble(z);
        }
    public void saveFloat(DataOutputStream out) throws IOException {
        super.saveFloat(out);
        double mas = Values.PrinterFieldSize/2;
        out.write(Utils.intToBytes(Float.floatToIntBits((float)(z*mas))));
        }
    }
