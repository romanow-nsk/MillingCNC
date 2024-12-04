package romanow.cnc.stl;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class GCodeLayer {
    @Getter @Setter private double layerZ;
    public final ArrayList<ArrayList<STLLine>> groups = new ArrayList<>();
    public GCodeLayer() {
        }
}
