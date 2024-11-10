package romanow.cnc.stl;

import java.util.ArrayList;

public class GCodeLayer {
    public final double layerZ;
    public final ArrayList<ArrayList<STLLine>> groups = new ArrayList<>();
    public GCodeLayer(double layerZ) {
        this.layerZ = layerZ;
        }
}
