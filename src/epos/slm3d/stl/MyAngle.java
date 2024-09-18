package epos.slm3d.stl;

/**
 * Created by romanow on 09.12.2017.
 */
/** класс для хранения синуса и косинуса угла */
public class MyAngle {
    public final double angle;
    public final double sinf;
    public final double cosf;
    public MyAngle(double ang){
        angle = ang;
        cosf = Math.cos(angle);
        sinf = Math.sin(angle);
    }
}
