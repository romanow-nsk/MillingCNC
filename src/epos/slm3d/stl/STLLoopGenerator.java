package epos.slm3d.stl;

/**
 * Created by romanow on 04.12.2017.
 */

import epos.slm3d.settings.WorkSpace;
import epos.slm3d.utils.I_Notify;
import epos.slm3d.utils.UNIException;
import epos.slm3d.utils.Values;

import java.util.ArrayList;

/** Набор линий и контур*/
public class STLLoopGenerator {
    private double diff=0;
    private I_Notify notify;
    private double z=0;
    private int id=1;
    private ArrayList<STLTriangle> triang;
    private ArrayList<STLLoop> loops = new ArrayList<>();
    private STLLineGroup src =  new STLLineGroup();
    private STLLineGroup orig = new STLLineGroup();
    private STLLineIndex index1;
    private STLLineIndex index2;
    public STLLineGroup orig(){ return orig; }
    public ArrayList<STLLoop> loops(){ return loops; }
    public void orig(STLLineGroup  xx){
        orig=xx;
        }
    public void loops(ArrayList<STLLoop>  xx){ loops=xx; }
    public STLLoopGenerator(ArrayList<STLTriangle> triang0, double z0, double diff0, I_Notify not) throws UNIException {
        id=1;
        z = z0;
        diff = diff0;
        triang = triang0;
        notify = not;
        // notify.info( "z="+z);
        }
    /** Ускоренный поиск контуров  + слияние */
    public ArrayList<STLLoop> createLoops(boolean full) throws UNIException{
        if (full){
            createCrossLine();
            }
        loops.clear();
        if (WorkSpace.ws().local().filling.FlateCircuitSlice.getVal())
            createTriangleLoops();          // Создает контура сам по себе ПОЗЖЕ ОСТАЛЬНЫХ
        createIndexedSource();
        boolean xx = WorkSpace.ws().local().filling.LoopsWithSomeLineTypes.getVal();
        while(true){
            STLLoop loop = createLoop(xx);
            if (loop==null)
                break;
            loops.add(loop);
            }
        int sz2=total();
        int sz3=loops.size();
        while (mergeOne(diff,xx));
        if (sz2!=total())
            System.out.println("11: До и после: "+sz2 + "("+sz3+")" +total()+"("+loops.size()+")");
        for (STLLoop loop:loops){
            loop.correct();
            }
        ArrayList<STLLoop> repaired =  repair();
        double lnt = WorkSpace.ws().local().filling.FillingFlatness.getVal()/(Values.PrinterFieldSize/2);
        int cnt=0;
        for (STLLoop loop:loops)
            cnt += loop.evenLoop(orig,lnt);
        if (cnt!=0)
            notify.log("Сглажено "+cnt+" отрезков");
        // Обновить orig из сглаженных контуров
        refreshOrigFromLoops();
        return repaired;
        }
    /** Создает сечение из построенных контуров */
    public void refreshOrigFromLoops(){
        STLLineGroup out = new STLLineGroup();
        for (STLLoop loop : loops){
            if (loop.loopLineMode()==3) continue;
            for(STLLine line : loop.lines()){
                out.addWithTest(line);
                }
           }
        orig = out;
    }
    /** принудительное замыкание */
    public ArrayList<STLLoop> repair(){
        ArrayList<STLLoop> out = new ArrayList<>();
       if (!WorkSpace.ws().local().filling.RepairLoops.getVal())
           return out;
        for (STLLoop loop : loops){
            STLLine xx = loop.repair();
            if (xx!=null){
                orig.add(xx);         // Оригинальное сечение НЕ ТРОГАТЬ
                out.add(loop);
                }
            }
        return out;
        }
    public int total(){
        int sz1=0;
        for (STLLoop cc : loops)
            sz1 += cc.size();
        return sz1;
        }
    public int totalCompleted(){
        int sz1=0;
        for (STLLoop cc : loops)
            if (cc.isCompleted()) sz1++;
        return sz1;
        }
    /** поиск контуров для возможного слияние и первое нацденное слияние */
    private boolean mergeOne(double diff2, boolean withTypes){
        int count=0;
        boolean merged=false;
        int n = loops.size();
        for(int i=0;i<n-1;i++){          // Склеить последний с первым
            STLLoop loop1 = loops.get(i);
            int mode1 = loop1.loopLineMode();
            if (mode1==3)
                continue;
            for(int j=i+1;j<n;j++){
                STLLoop loop2 = loops.get(j);
                int mode2 = loop2.loopLineMode();
                if (mode2==3)
                    continue;
                if (withTypes && mode1!=mode2)
                    continue;
                double xx = loop1.last().diffXY(loop2.first());
                if (xx<diff2){
                    loop1.add(loop2.lines());
                    loops.remove(j);
                    j--; n--;
                    merged=true;
                    continue;
                    }
                xx=loop1.last().diffXY(loop2.last());
                if (xx<diff2){
                    loop2.swap();
                    loop1.add(loop2.lines());
                    loops.remove(j);
                    j--; n--;
                    merged=true;
                    continue;
                    }
                xx = loop1.first().diffXY(loop2.last());
                if (xx<diff2){
                    loop1.swap();
                    loop2.swap();
                    loop1.add(loop2.lines());
                    loops.remove(j);
                    j--; n--;
                    merged=true;
                    continue;
                    }
                xx = loop1.first().diffXY(loop2.first());
                if (xx<diff2){
                    loop1.swap();
                    loop1.add(loop2.lines());
                    loops.remove(j);
                    j--; n--;
                    merged=true;
                    continue;
                    }
                }
            }
        return merged;
        }
    /** создание очередного контура */
    public STLLoop createLoop(boolean withTypes) throws UNIException{
        if (src.size()==0)
            return null;
        STLLoop out = new STLLoop(diff);
        out.id(id++);
        STLLine one = src.remove(0);
        out.add(one);
        if (!withTypes)
            out.setLoopLineMode(0);         // Тип всегда сечение
        else
            out.setLoopLineMode();          // Тип контура по точке
        if (Values.CreateLoopsFull){
            index1.remove(one,false);
            index2.remove(one,false);
            }
        else
            return out;                 // 1 контур = 1 линия
        while(true){
            int bf = src.size();
            if (out.isCompleted())
                return out;
            if (!addToLoop(out,withTypes))
                return out;
            if (src.size()==0)
                return out;
            if (bf == src.size()){
                System.out.println("8: loop size="+out.size()+" src.size="+src.size()+"-"+bf);
                return out;
                }
            //System.out.println("5: loop size="+out.size()+" src.size="+src.size());
            }
        }
    /** Заглушка */
    public boolean addToLoop0(STLLoop loop){ return false; }
    /** Добавление отрезка к контуру - прямой цикл */
    public boolean addToLoop(STLLoop loop,boolean withTypes){
        STLLineGroup rez;
        I_STLPoint2D pp = loop.get(0).one();
        rez = index2.nearestX(pp,diff);
        for(STLLine xx : rez.lines()){          // Линии того же типа
            if (withTypes && loop.loopLineMode()!=xx.nPoint())
                continue;
            if (pp.equals(xx.two())){
            //if (pp.diffXY(xx.two())<=diff){
                src.remove(xx);
                index1.remove(xx,false);
                index2.remove(xx,false);
                loop.insert(0,xx);
                return true;
                }
            }
        rez = index1.nearestX(pp,diff);
        for(STLLine xx : rez.lines()){
            if (pp.equals(xx.one())){
            //if (pp.diffXY(xx.one())<=diff){
                src.remove(xx);
                index1.remove(xx,false);
                index2.remove(xx,false);
                xx.swap();
                loop.insert(0,xx);
                return true;
                }
            }
        pp = loop.get(loop.size()-1).two();
        rez = index2.nearestX(pp,diff);
        for(STLLine xx : rez.lines()){
            if (pp.equals(xx.two())){
            //if (pp.diffXY(xx.two())<=diff){
                src.remove(xx);
                index1.remove(xx,false);
                index2.remove(xx,false);
                xx.swap();
                loop.add(xx);
                return true;
                }
            }
        rez = index1.nearestX(pp,diff);
        for(STLLine xx : rez.lines()){
            if (pp.equals(xx.one())){
            //if (pp.diffXY(xx.one())<=diff){
                src.remove(xx);
                index1.remove(xx,false);
                index2.remove(xx,false);
                loop.add(xx);
                return true;
                }
            }
        return false;
        }
    //-------------------------------------------------------------------------------------------------
    /** Добавление отрезка к контуру - итератор */
    public boolean addToLoop2(STLLoop loop){
        STLLineGroup rez;
        final I_STLPoint2D pp = loop.get(0).one();
        STLLine xx = index2.firstThat(pp,  diff,(a)->{
            return pp.diffXY(a.two())<=diff;
            });
        if (xx!=null){
            src.remove(xx);
            index1.remove(xx,false);
            index2.remove(xx,false);
            loop.insert(0,xx);
            return true;
            }
        xx = index1.firstThat(pp, diff,(a)->{
            return pp.diffXY(a.one())<=diff;
            });
        if (xx!=null){
            src.remove(xx);
            index1.remove(xx,false);
            index2.remove(xx,false);
            xx.swap();
            loop.insert(0,xx);
            return true;
            }
        final I_STLPoint2D pp2 = loop.get(loop.size()-1).two();
        xx = index2.firstThat(pp, diff, (a)->{
            return pp.diffXY(a.two())<=diff;
            });
        if (xx!=null){
            src.remove(xx);
            index1.remove(xx,false);
            index2.remove(xx,false);
            xx.swap();
            xx.loopId(loop.id());              // ссылка на текущий контур
            loop.add(xx);
            return true;
            }
        xx = index1.firstThat(pp,  diff,(a)->{
            return pp.diffXY(a.one())<=diff;
            });
        if (xx!=null){
            src.remove(xx);
            index1.remove(xx,false);
            index2.remove(xx,false);
            loop.add(xx);
            return true;
            }
        return false;
        }
    /** Селекция по углу наклона = углы наклона 2 точек к линии прожига  больше-меньше */
    public STLPointIndex intersect(STLLine line, boolean forX){
        MyAtan b0 = new MyAtan(line);
        STLPointGroup out = new STLPointGroup();
        for(STLLine xx: orig.lines()) {
            if (xx.noIntersection(line))            //8%
                continue;
            STLPoint2D pp = line.intersection(xx);  //23%
            if (pp!=null){
                STLReferedPoint onePoint = new STLReferedPoint(pp,xx); // Тот же контур, что у отрезка
                out.add(onePoint);
                }
            }
        return new STLPointIndex(out,forX ? 0 : 2);          // Отсортировать по X или Y
        }
    /** Вектор отрезков пересечений треугольников с плоскостью z */
    private void createCrossLine() throws UNIException {
        orig = new STLLineGroup();
        for (STLTriangle xx : triang) {
            int np = xx.equalsAboutCount(z);
            if (np == 3 || np == 1)
                continue;
            ArrayList<STLLine> yy = xx.getCrossLine(z);
            if (yy == null)
                continue;
            for (STLLine ln : yy)
                ln.nPoint(np);
            orig.addWithTest(yy);
            //orig.add(yy);
            }
        }
    public void createIndexedSource(){
        src = new STLLineGroup();
        for(STLLine xx: orig.lines()) {
            src.add(xx);
            }
        if (Values.CreateLoopsFull){
            index1 = new STLLineIndex(src,1);
            index2 = new STLLineIndex(src,2);
            }
        }
    private STLLoop createTriangeLoop(STLLine ln[]){
        STLLoop loop = new STLLoop(diff);
        loop.id(id++);
        loop.add(ln[0]);
        loop.add(ln[1]);
        loop.add(ln[2]);
        return loop;
        }
    private STLLine []createLines(STLTriangle triangle){
        STLPoint3D ver[] = triangle.getVertices();
        STLLine ln[] = new STLLine[3];
        ln[0]=new STLLine(ver[0].clone2D(),ver[1].clone2D());
        ln[1]=new STLLine(ver[1].clone2D(),ver[2].clone2D());
        ln[2]=new STLLine(ver[2].clone2D(),ver[0].clone2D());
        return ln;
        }
    /** Попытка привязать любой к любому */
    private boolean tryToLinkOne(ArrayList<STLTriangle> temp){
        for (STLLoop loop : loops){
            for(STLTriangle tr :temp)
                if (loop.linkToLoop(createLines(tr))){
                    temp.remove(tr);
                    return true;
                    }
            }
        return false;
        }
    public void createTriangleLoops(){
        ArrayList<STLTriangle> temp = new ArrayList<>();
        for(STLTriangle xx : triang) {
            int np = xx.equalsAboutCount(z);
            if (np == 3)
                temp.add(xx);
            }
        if (temp.size()==0)
            return;
        notify.notify(Values.warning,"Горизонтальных треугольников: "+temp.size());
        while(temp.size()!=0){
            if(!tryToLinkOne(temp)){            // Новый, если только не было привязки для всех пар
                loops.add(createTriangeLoop(createLines(temp.remove(0))));
                }
            }
        notify.notify(Values.warning,"Контуры горизонтальных поверхностей: "+loops.size());
        for(STLLoop loop : loops){               // Добавить в исходный массив отрезкоd
            loop.setLoopLineMode(3);
            //----------- Для плоских не добавлять в сечение --------------------
            //for(STLLine line : loop.lines()){
            //    line.nPoint(3);             // Контур из линий горизонтального треугольника
            //    orig.addWithTest(line);     // Оригинал - только из несовпадающий
            //    }
            }
        }
    }

