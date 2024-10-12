package epos.slm3d.slicer;

import epos.slm3d.settings.WorkSpace;
import epos.slm3d.stl.I_STLPoint2D;
import epos.slm3d.stl.STLIndex2D;
import epos.slm3d.stl.STLLine;
import epos.slm3d.stl.STLLineGroup;
import epos.slm3d.utils.I_Notify;
import epos.slm3d.utils.Values;

/**
 * Created by romanow on 10.12.2017.
 */
/** Адаптер оптимизации перемещений */
public class OptimizeAdaprer implements I_LineSlice{
    private I_LineSlice prevBack;
    private double diff;
    private STLLine last=null;
    private STLLine prevLast=null;
    private STLLineGroup src = new STLLineGroup();
    private boolean continuous;
    private double length;
    private I_Notify notify;
    public OptimizeAdaprer(I_LineSlice old, double diff0, boolean contigous0, I_Notify notify0){
        notify = notify0;
        prevBack = old;
        diff = diff0;
        last=null;
        prevLast=null;
        continuous = contigous0;     // НЕПРЕРЫВНЫЙ СЛАЙСИНГ (короткие соединения) - меньше удвоенного растра
        length = WorkSpace.ws().local().filling.FillParametersRaster.getVal()*Values.ContinuousK;
        }
    /** возвращает линии в порядке оптимизации */
    public void onSliceLayerIndexed() {
        STLIndex2D index1 = new STLIndex2D(src,true);      //x0
        STLIndex2D index2 = new STLIndex2D(src,false);      //x1
        if (src.size()==0)
            return;
        prevLast=null;
        last = src.get(0);
        index1.remove(last,false);
        index2.remove(last,false);
        src.remove(last);
        prevBack.onSliceLine(last);
        while (true) {
            STLLineGroup gr1 = index1.nearestX(last.two(), diff);
            STLLineGroup gr2 = index2.nearestX(last.two(), diff);
            // найти группы ближайших
            STLLine ln1  = null;
            for(STLLine xx : gr1.lines()){
                if (xx.isDone()) continue;
                if (ln1==null)
                    ln1 = xx;
                else
                if (isNearest(last.two(),xx.one(),ln1.one()))
                //if (xx.one().diffXY2(last.two()) < ln1.one().diffXY2(last.two()))
                    ln1=xx;
                }
            STLLine ln2  = null;
            for(STLLine xx : gr2.lines()){
                if (xx.isDone()) continue;
                if (ln2==null)
                    ln2 = xx;
                else
                if (isNearest(last.two(),xx.two(),ln2.two()))
                //if (xx.two().diffXY2(last.two()) < ln2.two().diffXY2(last.two()))
                    ln2=xx;
                }
            if (ln1==null && ln2==null){        // Не нашли в индексе в ближайших - все
                if (!optimizeOne())
                    return;
                continue;
                }
            boolean isSwap=false;
            if (ln1 == null && ln2!=null) {
                last = ln2;
                last.swap();
                isSwap=true;
                }
            else
            if (ln1 != null && ln2==null) {
                last = ln1;
                }
            else
            if (last.two().diffXY2(ln2.two()) < last.two().diffXY2(ln1.one())) {
                last = ln2;
                last.swap();
                isSwap=true;
                }
            else{
                last = ln1;
                }
            last.done(true);
            if(continuous && prevLast!=null){
                STLLine xx = new STLLine(prevLast.two(),last.one());
                if (xx.lengthXY()<length)
                    prevBack.onSliceLine(xx);
                }
            prevBack.onSliceLine(last);
            prevLast = last;
            }
        }
    private boolean isNearest(I_STLPoint2D src, I_STLPoint2D p1, I_STLPoint2D p2){
        if (Math.abs(src.x()-p1.x()) < Math.abs(src.x()-p2.x()) &&
            Math.abs(src.y()-p1.y()) < Math.abs(src.y()-p2.y()))
            return true;
        return src.diffXY2(p1) < src.diffXY2(p2);
        }
    @Override
    public void onSliceLayer(){             // Конец слоя - оптимизировать остатки из буфера
        if (Values.GraphIndexForOptimize)
            onSliceLayerIndexed();
        else
            optimize(false);
        }

    @Override
    public void onCutterUpDown(boolean up, double z) {
        prevBack.onCutterUpDown(up, z);
        }

    public void onSliceLayerSimple() {
        optimize(false);
        }

    private void squeezy(){
        System.out.println("Сжатие "+src.size());
        STLLineGroup out = new STLLineGroup();
        for(STLLine xx : src.lines())
            if (!xx.isDone())
                out.add(xx);
        src = out;
        }


    public void optimize(boolean full) {            // Буфер опустошается наполовину
        if (src.size()==0)
            return;
        if (last==null){
            prevLast=null;
            last = src.get(0);
            last.done(true);
            prevBack.onSliceLine(last);
            }
        if (!full){
            while (optimizeOne());
            }
        else{
            int cnt = Values.OptimizeSeqSize/2;
            while (cnt--!=0 &&  optimizeOne());
            squeezy();
            }
        }

    public boolean optimizeOne(){
        STLLine ln1  = null;
        for(STLLine xx : src.lines()){
            if (xx.isDone()) continue;
            if (ln1==null)
                ln1 = xx;
            else{
                if (isNearest(last.two(),xx.one(),ln1.one()))
                //if (last.two().diffXY2(xx.one()) < last.two().diffXY2(ln1.one()))
                    ln1=xx;
                }
            }
        STLLine ln2  = null;
        for(STLLine xx : src.lines()){
            if (xx.isDone()) continue;
            if (ln2==null)
                ln2 = xx;
            else {
                if (isNearest(last.two(),xx.two(),ln2.two()))
                //if (xx.two().diffXY2(last.two()) < last.two().diffXY2(ln2.two()))
                    ln2=xx;
                }
            }
        if (ln1==null && ln2==null)
            return false;
        boolean isSwap=false;
        if (ln1 == null && ln2!=null) {
            last = ln2;
            last.swap();
            isSwap=true;
            }
        else
        if (ln1 != null && ln2==null) {
            last = ln1;
            }
        else
        if (last.two().diffXY2(ln2.two()) < last.two().diffXY2(ln1.one())) {
            last = ln2;
            last.swap();
            isSwap=true;
            }
        else{
            last=ln1;
            }
        last.done(true);
        if(continuous && prevLast!=null){
            STLLine xx = new STLLine(prevLast.two(),last.one());
            if (xx.lengthXY()<length)
                prevBack.onSliceLine(xx);
            }
        prevBack.onSliceLine(last);
        prevLast = last;
        return true;
        }
    /** накапливает линии */
    @Override
    public void onSliceLine(STLLine line) {
        line.done(false);
        src.add(line);
        if (!Values.GraphIndexForOptimize && src.size()>=Values.OptimizeSeqSize)
            optimize(false);
        }

    @Override
    public boolean isFinish() {
        return prevBack.isFinish();
        }

    @Override
    public void onSliceError(SliceError error) {
        prevBack.onSliceError(error);
    }

    @Override
    public void notify(int level, String mes) {
        notify.notify(level,mes);
        }
}
