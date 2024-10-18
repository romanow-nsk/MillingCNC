package romanow.cnc.graph;

import java.awt.Choice;
import java.util.ArrayList;

/**
 * Created by romanow on 16.11.2018.
 */
public class GraphFactory {
    private ArrayList<GraphObject> factory = new ArrayList();
    public GraphFactory(){
        factory.add(new O2DLine());
        factory.add(new O2DRectangle());
        factory.add(new O2DOval());
        factory.add(new O2DArc());
        factory.add(new O2DGrid());
        factory.add(new O2DGridV());
        factory.add(new O2DGridH());
        factory.add(new O2DGroup());
        }
    public void toBox(Choice cc){
        cc.removeAll();
        for(GraphObject vv : factory)
            cc.add(vv.name());
        }
    public void select(Choice cc, GraphObject vv){
        for(int i=0;i<factory.size();i++)
            if (factory.get(i).getClass()==vv.getClass()){
                cc.select(i);
                }
        }
    public GraphObject create(Choice cc, GraphPoint vv){
        int idx = cc.getSelectedIndex();
        GraphObject zz = factory.get(idx).clone();
        zz.x0(vv.x()-0.1);
        zz.y0(vv.y()-0.1);
        zz.x1(vv.x()+0.1);
        zz.y1(vv.y()+0.1);        
        return zz;
        }
}
