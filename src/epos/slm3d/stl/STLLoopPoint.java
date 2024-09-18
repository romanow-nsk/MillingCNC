package epos.slm3d.stl;

/**
 * Created by romanow on 07.02.2018.
 */
public class STLLoopPoint extends STLPoint2D {
    private STLLoop loop=null;
    public STLLoop loop(){ return loop; }
    public void loop(STLLoop ll){ loop=ll; }
    public STLLoopPoint(double x, double y, STLLoop loop) {
        super(x, y);
        this.loop = loop;
    }
    public STLLoopPoint(double x, double y, double z) {
        super(x, y);
        }
}
