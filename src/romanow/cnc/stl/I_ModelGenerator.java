package romanow.cnc.stl;

import java.util.ArrayList;

/**
 * Created by romanow on 19.03.2018.
 */
public interface I_ModelGenerator {
    public ArrayList<STLTriangle> generate();
    public String name();
}
