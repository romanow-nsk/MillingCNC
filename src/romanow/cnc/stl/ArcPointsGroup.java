package romanow.cnc.stl;

public class ArcPointsGroup {
    public final int idx;
    public final int count;
    public final I_STLPoint2D center;
    public final double radius;
    public ArcPointsGroup(int idx, int count, I_STLPoint2D center, double radius) {
        this.idx = idx;
        this.count = count;
        this.center = center;
        this.radius = radius;
    }
}
