package romanow.cnc.graph;

import romanow.cnc.slicer.SliceData;
import romanow.cnc.slicer.SliceLayer;
import romanow.cnc.stl.STLLineGroup;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by romanow on 16.11.2018.
 */
public class O2DGroup extends GraphObject{
    private ArrayList<GraphObject> list = new ArrayList();
    public O2DGroup(){}
    public void forEach(I_O2DForEach fun){
        for(GraphObject vv : list)
            fun.toDo(vv);
        }
    public GraphObject get(int i){ return list.get(i); }
    @Override
    public void paint(Color color, GraphPanel panel, boolean mid) {
        forEach((vv)->{
            vv.paint(color,panel,!mid);
            });
        if (mid){ 
            double mx = midX();
            double my = midY();
            //panel.drawKreuz(color, new GraphPoint(mx,my));
            panel.fillOval(Color.blue, new GraphPoint(mx,my),10);
            }
        }
    public void add(GraphObject vv){ list.add(vv);}
    public int size(){ return list.size(); }
    public  ArrayList<GraphObject> list(){ return list; }
    @Override
    public void load(DataInputStream in) throws IOException {
        int sz = in.readInt();
        while(sz--!=0){
            String name = in.readUTF();
            GraphObject vv = null;
            try {
                vv = (GraphObject)Class.forName(name).newInstance();
                } catch (Exception e) { throw new IOException("Ошибка создания объекта "+name); }
            vv.load(in);
            list.add(vv);
            }
        }
    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeInt(list.size());
        for(GraphObject vv : list){
            out.writeUTF(vv.getClass().getName());
            vv.save(out);
            }
        }
    private GraphObject minmax(){
        if (size()==0)
            return this;
        GraphObject out = list.get(0).clone();
        for(GraphObject vv : list) {
            if (vv.x0() < out.x0()) out.x0(vv.x0());
            if (vv.x1() > out.x1()) out.x1(vv.x1());
            if (vv.y0() < out.y0()) out.y0(vv.y0());
            if (vv.y1() > out.y1()) out.y1(vv.y1());
            }
        return out;
        }
    @Override
    public double x0() { return size()==0 ? super.x0() : minmax().x0(); }
    @Override
    public double x1() { return size()==0 ? super.x1() : minmax().x1(); }
    @Override
    public double y0() { return size()==0 ? super.y0() : minmax().y0(); }
    @Override
    public double y1() { return size()==0 ? super.y1() : minmax().y1(); }
    @Override
    public double midX() { return size()==0 ? super.midX() : minmax().midX(); }
    @Override
    public double midY() { return size()==0 ? super.midY() : minmax().midY(); }
    @Override
    public double szX() { return size()==0 ? super.szX() : minmax().szX(); }
    @Override
    public double szY() { return size()==0 ? super.szY() : minmax().szY(); }
    @Override
    public String name() {
        return "Группа";
        }
    @Override
    public GraphObject clone() {
        O2DGroup out = new O2DGroup();
        forEach((vv)->{
            out.add(vv.clone());
            });
        return out;
        }
    public int nearest(GraphPoint pp,double diff){
        if (size()==0) return -1;
        int idx=0;
        int sz = size();
        double vv = list.get(0).middle().diffXY(pp);
        for(int i=1;i<sz;i++){
            double vv2 = list.get(i).middle().diffXY(pp);
            if (vv2 < vv){ vv=vv2; idx=i; }
            }
        return vv < diff ? idx : -1;
        }
    public void shift(double dx, double dy){
        if (size()==0)
            super.shift(dx,dy);
        else
        forEach((vv)->{
            vv.shift(dx, dy);
            });
        }
    @Override
    public SliceLayer createPrintData() {
        SliceLayer out =  super.createPrintData(); 
        STLLineGroup ss = out.segments();
        forEach((vv)->{
            ss.add(vv.createPrintData().segments());
            });
        return out;
        }
    public SliceData createForPrint() {
        SliceData out =  new SliceData(); 
        forEach((vv)->{
            SliceLayer ss = vv.createPrintData();
            ss.label(vv.toString());
            out.addLayer(ss);
            });
        return out;
        } 
}
