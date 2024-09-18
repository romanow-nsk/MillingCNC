package epos.slm3d.slicer;

import epos.slm3d.utils.I_Notify;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by romanow on 07.02.2018.
 */
public class SliceData{
    private ArrayList<SliceLayer> layers = new ArrayList<>();
    private boolean merged=false;
    private volatile boolean sliceStop=false;
    private SliceRezult rez = new SliceRezult();
    private boolean fromFile=false;
    public boolean isMerged(){ return merged; }
    public synchronized void replace(SliceLayer layer, int idx){ layers.set(idx,layer); }
    public void addLayer(SliceLayer layer){
        layers.add(layer);
        }
    public int addLayerSorted(SliceLayer layer){
        int k=0,sz = layers.size();
        for(k=0; k<sz && layer.z() > layers.get(k).z();k++);
        if (k==sz)
            layers.add(layer);
        else
            layers.add(k,layer);
        return k;
        }
    public int size(){ return layers.size(); }
    public SliceLayer get(int idx){ return layers.get(idx); }
    public void result(SliceRezult rez0){ rez = rez0; }
    public void remove(int idx){ layers.remove(idx); }
    public void sliceStop(){
        sliceStop=true;
        synchronized (this){
            this.notify();
            }
        }
    public boolean isSliceStop(){ return sliceStop; }
    public void load(DataInputStream in, I_Notify notyfy) throws IOException {
        layers = new ArrayList<>();
        int sz = in.readInt();
        for(int i=0;i<sz;i++){
            SliceLayer lr = new SliceLayer();
            lr.load(in);
            layers.add(lr);
            notyfy.setProgress((i+1)*100/sz);
            }
        merged = in.readBoolean();
        rez = new SliceRezult();
        rez.load(in);
        fromFile = true;
        }
    /** Запускается в отдельном потоке */
    public void saveConcurent(DataOutputStream out, I_Notify notyfy) throws IOException {
        out.writeInt(layers.size());
        int i=0;
        sliceStop=false;
        int sz = layers.size();
        while(i!=sz && !sliceStop){
            SliceLayer lr = layers.get(i);
            if (!lr.isReady()){             // Пока не установится признак
                synchronized (this){
                    try { this.wait(); } catch (InterruptedException e) {}
                    }
                continue;
                }
            lr.save(out);
            replace(new SliceLayer(i+1),i);            // Заменить на пустой
            i++;
            }
        if (!sliceStop){
            synchronized (this){
                try { this.wait(); } catch (InterruptedException e) {}
                }
            out.writeBoolean(merged);
            rez.save(out);
            }
        out.close();
        }
    public void save(DataOutputStream out, I_Notify notyfy) throws IOException {
        out.writeInt(layers.size());
        int i=0;
        int sz = layers.size();
        for(SliceLayer lr:layers){
            lr.save(out);
            notyfy.setProgress((i+1)*100/sz);
            i++;
            }
        out.writeBoolean(merged);
        rez.save(out);
        }
    public void mergeLayers(int size){
        if (merged) return;
        double delta = 2./size;
        double delta0 = -1 + delta/2;      // В первую клетку
        for(int i=0;i<layers.size();i++){
            int j = i % (size*size);
            double xx = delta0 + (j%size)*delta;
            double yy = delta0 + (j/size)*delta;
            layers.get(i).shift(xx,yy,fromFile);
            }
        ArrayList<SliceLayer> out = new ArrayList<>();
        SliceLayer last=null;
        for(int i=0;i<layers.size();i++){
            SliceLayer layer = layers.get(i);
            if (i%(size*size)==0){
                last = layer;
                out.add(layer);
                }
            else{
                layer.z(last.z());
                last.addData(layer);
                }
            }
        layers = out;
        merged = true;
        }
}
