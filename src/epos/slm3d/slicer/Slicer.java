package epos.slm3d.slicer;

import epos.slm3d.settings.Settings;
import epos.slm3d.stl.*;
import epos.slm3d.utils.I_Notify;
import epos.slm3d.utils.T_Pair;
import epos.slm3d.utils.UNIException;
import epos.slm3d.utils.Values;

import java.util.ArrayList;

/**
 * Created by romanow on 07.12.2017.
 */
public class Slicer extends STLLoopGenerator{
    /**  z */
    private double z;
    /** точность при поиск совпадения концов отрезков */
    private double diff;
    /** угол наклона линии луча */
    private double angle;
    /** шаг луча по перпендикуляру к линии прожига */
    private double step;
    /** нотификатор событий */
    private I_Notify notify;
    public Slicer(ArrayList<STLTriangle> src, double z0, double diff0, double angle0, double step0, I_Notify notify0) throws UNIException{
        super(src,z0,diff0/(Values.PrinterFieldSize/2),notify0);
        notify = notify0;
        z = z0;
        diff = diff0/(Values.PrinterFieldSize/2);
        step = step0/(Values.PrinterFieldSize/2);
        angle = (angle0/180)*Math.PI;
        }
    /** Удалить нечетную, если парная близко */
    private boolean testAndRemoveNearestOdd(ArrayList<STLReferedPoint> xx){
        int nn=xx.size();
        if (nn%2==0) return false;
        for(int i=0;i<nn-1;i++){
            double dd = Values.PointCrossDiffenerce/(Values.PrinterFieldSize/2);
            if (xx.get(i).diffXY(xx.get(i+1))<dd && xx.get(i).reference().loopId()==xx.get(i+1).reference().loopId()){
                notify.log("Удалена нечетная n="+xx.size()+" "+xx.get(i)+" id="+xx.get(i).reference().loopId());
                xx.remove(i);
                return true;
                }
            }
        return false;
        }
    /** Удалить нечетную из  найденного контура */
    private boolean testAndRemoveOddInLoop(ArrayList<STLReferedPoint> xx){
        int nn=xx.size();
        if (nn%2==0) return false;
        int idx[] = new int[xx.size()];
        for(int i=0;i<nn;i++){
            int k=0;
            for(int j=0;j<nn;j++) {
                if (xx.get(i).reference().loopId() == xx.get(j).reference().loopId())
                    idx[k++] = j;
                }
            if (k%2!=0){
                notify.log("Удалена нечетная n="+xx.size()+" "+xx.get(idx[k/2])+" id="+xx.get(idx[k/2]).reference().loopId());
                xx.remove(idx[k/2]);
                return true;
                }
            }
        return false;
        }
    private STLLineGroup pointsToLines(STLPointIndex points,I_LineSlice back) throws UNIException{
        STLLineGroup out = new STLLineGroup();
        ArrayList<STLReferedPoint> xx = points.indexedPoints();
        int nn = xx.size();
        if (nn<=1) return out;
        if (testAndRemoveNearestOdd(xx))
            back.onSliceError(new SliceErrorPointList(2,xx));           // Тип ошибки 2
        if (testAndRemoveOddInLoop(xx))
            back.onSliceError(new SliceErrorPointList(2,xx));           // Тип ошибки 2
        nn = xx.size();
        if (nn%2!=0){
            notify.notify(Values.warning,"Не кратно 2: "+xx.size());
            back.onSliceError(new SliceErrorPointList(1,xx));           // Тип ошибки 1
            nn--;
            }
        for (int i=0;i<nn;i+=2){
            STLLine zz = new STLLine(xx.get(i),xx.get(i+1));
            zz.loopId(xx.get(i).reference().loopId());          // Копировать ссылку на контур
            out.add(zz);
            }
        return out;
        }
    public void createLoops( boolean full,I_LineSlice back) throws UNIException{
        ArrayList<STLLoop> repaired = createLoops(full);
        if (repaired.size()!=0){
            notify.notify(Values.warning,"Принудительно замкнуты контуры: "+repaired.size());
            back.onSliceError(new SliceErrorLoopList(0,repaired));
            }
        }

   /** слайсинг с выбором типа */
    public boolean slice(int mode, I_LineSlice back,Settings set) throws UNIException{
        switch (mode){
            case 0: return sliceLinear(back);
            case 1: return sliceChess(set,back);
            case 2: return sliceRandom(set,back);
            case 3: return sliceMilling(set,back);
            }
        return false;
        }
    public boolean sliceChess(Settings set,I_LineSlice back) throws UNIException{
        ChessAdapter chessBack = new ChessAdapter(set,back,angle,notify);
        chessBack.setMode(true);
        boolean rez =  sliceLinear(chessBack,angle);                // Сетка через обратный адаптер
        chessBack.setMode(false);
        rez |=  sliceLinear(chessBack,angle+Math.PI/2);
        back.onSliceLayer();
        return rez;
        }
    public boolean sliceRandom(Settings set,I_LineSlice back) throws UNIException{
        STLLineGroup tmp = new STLLineGroup();
        //------------------ Определение границ повернутого сечения -----------------------
        MyAngle angleXY = new MyAngle(-angle);
        for(STLLine line : orig().lines()){
            tmp.add(line.rotateXY(angleXY));
            }
        STLLine minmax = tmp.minmax();
        double dx = minmax.two().x() -  minmax.one().x();
        double dy = minmax.two().y() -  minmax.one().y();
        if (dy > dx) dx=dy;
        //-------------------------------------------------------------------------------
        double cellStep = set.filling.FillParametersFillCell.getVal()/(Values.PrinterFieldSize/2);
        int level = 1;
        int dd = (int)(dx / cellStep);
        while(dd!=0){ level++; dd/=2; }
        RandomAdapter randomBack = new RandomAdapter(back,angle,true,minmax,level+1,notify);
        boolean rez=false;
        randomBack.lineVertical(false);
        rez =  sliceLinear(randomBack,angle);                // Сетка через обратный адаптер
        randomBack.lineVertical(true);
        rez |=  sliceLinear(randomBack,angle+Math.PI/2);
        back.onSliceLayer();
        return rez;
    }
    public boolean sliceLinear(I_LineSlice back) throws UNIException{
        boolean rez =  sliceLinear(back,angle);
        back.onSliceLayer();
        return rez;
        }
    public boolean sliceLinear(I_LineSlice back, double angle0) throws UNIException{
        boolean rez;
        double angle2 = angle0 - ((int)(angle0/Math.PI))*Math.PI;
        if (Math.abs(angle2)<Values.AxesGrad)
            rez  = sliceLinear0(back);
        else
        if (Math.abs(angle2-Math.PI)<Values.AxesGrad)
            rez =  sliceLinear0(back);
        else
        if (Math.abs(angle2-Math.PI/2)<Values.AxesGrad)
            rez =  sliceLinear90(back);
        else
        if (angle2 < Math.PI/2)
            rez =  sliceLinear0_90(back,angle2);
        else
            rez =  sliceLinear90_180(back,angle2-Math.PI/2);
        return rez;
        }
    public boolean sliceLinear0(I_LineSlice back) throws UNIException{
        STLLineGroup tmp = new STLLineGroup();          // Промежуточное накопление
        STLPoint2D point1,point2;
        int n=(int)(2/step);
        for(double y = -1; y<=1; y+=step){                 // для положительного не горизонтального
            point1 = new STLPoint2D(-1,y);
            point2 = new STLPoint2D(1,y);
            STLLine line = new STLLine(point1,point2);
            STLPointIndex points = intersect(line,true);
            tmp.add(pointsToLines(points,back).lines());     // слить по всем лучам
            }
        for (STLLoop loop : loops()){                // Каждый контур отдельно
            for(STLLine xx : tmp.lines())
                if (xx.loopId()==loop.id()){         // текущий контур
                    back.onSliceLine(xx);            // Досрочное завершение
                        if (back.isFinish())
                            return true;
                        }
            }
        return false;
        }
    public boolean sliceLinear90(I_LineSlice back) throws UNIException{
        STLLineGroup tmp = new STLLineGroup();          // Промежуточное накопление
        STLPoint2D point1,point2;
        int n=(int)(2/step);
        for(double x = -1+step; x<=1; x+=step){                 // для положительного не горизонтального
            point1 = new STLPoint2D(x,-1);
            point2 = new STLPoint2D(x,1);
            STLLine line = new STLLine(point1,point2);
            STLPointIndex points = intersect(line,false);
            tmp.add(pointsToLines(points,back).lines());     // слить по всем лучам
            }
        for (STLLoop loop : loops()){               // Каждый контур отдельно
            for(STLLine xx : tmp.lines())
                if (xx.loopId()==loop.id()){                // текущий контур
                    back.onSliceLine(xx);          // Досрочное завершение
                    if (back.isFinish())
                        return true;
                        }
            }
        return false;
        }
    public boolean sliceLinear0_90(I_LineSlice back,double angle2) throws UNIException{
        STLLineGroup tmp = new STLLineGroup();          // Промежуточное накопление
        double B = Math.tan(angle2);
        double step2 = step/Math.sin(angle2);
        STLPoint2D point1,point2;
        int n=(int)(2/step2);
        double x=1;
        for(x=-1;x<=1;x+=step2){                 // для положительного не горизонтального
            point1 = new STLPoint2D(x,-1);
            double y1=-1+(1-x)*B;
            if (y1<=1)
                point2 = new STLPoint2D(1,y1);
            else
                point2 = new STLPoint2D(x+2/B,1);
            STLLine line = new STLLine(point1,point2);
            STLPointIndex points = intersect(line,true);
            tmp.add(pointsToLines(points,back).lines());     // слить по всем лучам
            }
        step2 = step/Math.cos(angle2);
        n=(int)(2/step2);
        for(double y=-1+step2;  y<=1; y+=step2){            // для положительного не вертикального
            point1 = new STLPoint2D(-1,y);
            double x1=(1-y)/B-1;
            if (x1<=1)
                point2 = new STLPoint2D(x1,1);
            else
                point2 = new STLPoint2D(1,y+2*B);
            STLLine line = new STLLine(point1,point2);
            STLPointIndex points = intersect(line,true);
            tmp.add(pointsToLines(points,back).lines());     // слить по всем лучам
            }
        for (STLLoop loop : loops()){               // Каждый контур отдельно
            for(STLLine xx : tmp.lines())
                if (xx.loopId()==loop.id()){                // текущий контур
                    back.onSliceLine(xx);       // Досрочное завершение
                    if (back.isFinish())
                        return true;
                    }
                }
            return false;
            }
    /** для угла больше 90 */
    public boolean sliceLinear90_180(I_LineSlice back,double angle2) throws UNIException{
        STLLineGroup tmp = new STLLineGroup();          // Промежуточное накопление
        double B = Math.tan(angle2);
        double step2 = step/Math.sin(angle2);
        STLPoint2D point1,point2;
        int n=(int)(2/step2);
        for(double y = -1; y<=1; y+=step2){            // для положительного не вертикального
            point1 = new STLPoint2D(1,y);
            double x1=1-(1-y)*B;
            if (x1>=-1)
                point2 = new STLPoint2D(x1,1);
            else
                point2 = new STLPoint2D(-1,y+2/B);
            STLLine line = new STLLine(point1,point2);
            STLPointIndex points = intersect(line,true);
            tmp.add(pointsToLines(points,back).lines());     // слить по всем лучам
            }
        step2 = step/Math.cos(angle2);
        n=(int)(2/step2);
        for(double x = 1-step2; x>=-1; x-=step2){                 // для положительного не горизонтального
            point1 = new STLPoint2D(x,-1);
            double y1=-1+(x+1)/B;
            if (y1<=1)
                point2 = new STLPoint2D(-1,y1);
            else
                point2 = new STLPoint2D(x-2*B,1);
            STLLine line = new STLLine(point1,point2);
            STLPointIndex points = intersect(line,true);
            tmp.add(pointsToLines(points,back).lines());     // слить по всем лучам
            }
        for (STLLoop loop : loops()){               // Каждый контур отдельно
            for(STLLine xx : tmp.lines())
                if (xx.loopId()==loop.id()){                // текущий контур
                    back.onSliceLine(xx);       // Досрочное завершение
                    if (back.isFinish())
                        return true;
                    }
            }
        return false;
        }
    /** Слайсирование по контуру - пример */
    public boolean sliceCircuit(I_LineSlice back) throws UNIException{
        for (STLLoop loop : loops()) {                  // Каждый контур отдельно
            while(loop.size()>1){                       // Цикл сжатия контура
                for(STLLine xx : loop.lines()){         // Вывести отрезки контура
                    back.onSliceLine(xx);
                    if (back.isFinish())
                        return true;
                    }
                STLPoint2D center = loop.center();            // Найти центр контура
                for(STLLine xx : loop.lines()){             // Для всех отрезков
                    I_STLPoint2D one = xx.one();                // Линия от центра к первой точке контура
                    STLLine zz = new STLLine(center,one);
                    T_Pair<Double,Double> sc = zz.sinCosXY();
                    one.x(one.x()-step*sc._2());        // Перенести точку к цкнтру на step
                    one.y(one.y()-step*sc._1());
                    one = xx.two();                         // Линия от центра к первой точке контура
                    zz = new STLLine(center,one);
                    sc = zz.sinCosXY();
                    one.x(one.x()-step*sc._2());
                    one.y(one.y()-step*sc._1());
                    }
                for(int i=0;i<loop.size();i++){
                    if (loop.get(i).lengthXY()<step){       // Отрезок стал коротким
                        loop.removeWithCorrect(i);          // Уладить с коррекциоей соседнего
                        // notify.info("size="+loop.size());
                        //------ удаление коротких через одного ------------------------
                        // if (loop.size()>1)
                        //    i--;
                        }
                    }
                }
            }
        return false;
        }
    //-----------------------------------------------------------------------------------------------------------------
    /** Слайсирование фрезерное */
    public boolean sliceBlank(){
        notify.notify(Values.important,"z="+ z*(Values.PrinterFieldSize/2)+ ", внешнее фрезерование, нет контуров, снятие слоя");
        return true;
        }
    public boolean sliceBlankOne(STLLoop loop){
        notify.notify(Values.important,"z="+ z*(Values.PrinterFieldSize/2)+ ", внешнее фрезерование, контур id="+loop.id());
        for(STLLoop loop1 : loop.childs()){
            for (STLLoop loop2 : loop1.childs())
                sliceInside(loop2);
            }
        return true;
        }
    public boolean sliceBlankMany(ArrayList<STLLoop> loops){
        notify.notify(Values.important,"z="+ z*(Values.PrinterFieldSize/2)+ ", внешнее фрезерование, контуров "+loops.size());
        for(STLLoop loop1 : loops){
            for (STLLoop loop2 : loop1.childs())
                sliceInside(loop2);
            }
        return true;
        }
    public boolean sliceInside(STLLoop loop){
        notify.notify(Values.important,"z="+ z*(Values.PrinterFieldSize/2)+ ", внутреннее фрезерование, контур id="+loop.id());
        if (loop.childs().size()==0){
            notify.notify(Values.important,"z="+ z*(Values.PrinterFieldSize/2)+ ", внутреннее фрезерование полное");
            return true;
            }
        notify.notify(Values.important,"z="+ z*(Values.PrinterFieldSize/2)+ ", внутреннее фрезерование, контуров "+loop.childs().size());
        for(STLLoop loop1 : loop.childs()) {
            if (loop1.childs().size()==0)
                continue;
            for (STLLoop loop2 : loop1.childs())
                sliceInside(loop2);
            }
        return true;
        }
    public boolean sliceMilling(Settings set,I_LineSlice back) throws UNIException {
        STLLoop root = createNestingTree();
        if (root==null){
            sliceBlank();
            return true;
            }
        if  (!root.isMultiply()){
            sliceBlankOne(root);
            }
        else{
            sliceBlankMany(root.childs());
            }
        return false;
        }
    }

