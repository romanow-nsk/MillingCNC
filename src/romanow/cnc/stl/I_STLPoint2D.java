package romanow.cnc.stl;

import romanow.cnc.io.I_File;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by romanow on 17.06.2018.
 */
public interface I_STLPoint2D extends I_Point2D, I_File {
    void x(double x0);
    void y(double x0);
    double diffXY(I_STLPoint2D two);
    double diffXY2(I_STLPoint2D two);
    boolean equalsAbout(I_STLPoint2D two);
    boolean equalsAboutX(I_STLPoint2D two);
    boolean equalsAboutY(I_STLPoint2D two);
    void shift(double xx, double yy);
    void setCoordinates(I_STLPoint2D two);
    I_STLPoint2D clone();
    double value(int mode);
    @Override
    void load(DataInputStream in) throws IOException;
    @Override
    void save(DataOutputStream out) throws IOException;
    void saveFloat(DataOutputStream out) throws IOException;
    @Override
    String toString();
    String dump();
    boolean equals(I_STLPoint2D two);
    I_STLPoint2D rotateXY(MyAngle ang);
    int classId();
    void invertX();
}
