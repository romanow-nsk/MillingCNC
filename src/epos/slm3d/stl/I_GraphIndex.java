package epos.slm3d.stl;

/**
 * Created by romanow on 17.06.2018.
 */
public interface I_GraphIndex {
    public STLLineGroup nearestX(I_STLPoint2D point, double diff);
    public boolean remove(STLLine xx, boolean isSwap);
}
