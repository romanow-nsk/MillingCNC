package romanow.cnc.slicer;

import romanow.cnc.settings.Settings;
import romanow.cnc.settings.WorkSpace;

import romanow.cnc.utils.I_Notify;
import romanow.cnc.utils.T_Pair;
import romanow.cnc.utils.UNIException;
import romanow.cnc.Values;
import romanow.cnc.stl.*;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by romanow on 07.12.2017.
 */
public class Slicer extends STLLoopGenerator {
    /**  z */
    private double z;
    /**  vStep */
    private double vStep;
    /** точность при поиск совпадения концов отрезков */
    private double diff;
    /** угол наклона линии луча */
    private double angle;
    /** шаг луча по перпендикуляру к линии прожига */
    private double step;
    Settings set;
    /** нотификатор событий */
    private I_Notify notify;
    public Slicer(ArrayList<STLTriangle> src, double z0, double diff0, Settings set0, I_Notify notify0) throws UNIException {
        super(src,z0,diff0,notify0);
        set = set0;
        vStep = set.model.VerticalStep.getVal();
        notify = notify0;
        z = z0;
        diff = diff0;
        step = set.model.CutterDiameter.getVal()-set.model.StepMinus.getVal();
        angle = (set.slice.FillParametersAngle.getVal()/180)*Math.PI;
        }
    /** Удалить нечетную, если парная близко */
    private boolean testAndRemoveNearestOdd(ArrayList<STLReferedPoint> xx){
        int nn=xx.size();
        if (nn%2==0) return false;
        for(int i=0;i<nn-1;i++){
            double dd = Values.PointCrossDiffenerce;
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
    private STLLineGroup pointsToLines(STLPointIndex points, I_LineSlice back) throws UNIException{
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
        double cellStep = set.slice.FillParametersFillCell.getVal();
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
                STLPoint2D center = loop.center();          // Найти центр контура
                for(STLLine xx : loop.lines()){             // Для всех отрезков
                    I_STLPoint2D one = xx.one();            // Линия от центра к первой точке контура
                    STLLine zz = new STLLine(center,one);
                    T_Pair<Double,Double> sc = zz.sinCosXY();
                    one.x(one.x()-step*sc._2());        // Перенести точку к центру на step
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
        notify.notify(Values.important,"z="+ z+ ", внешнее фрезерование, нет контуров, снятие слоя");
        return true;
        }
    public boolean sliceBlankOne(STLLoop loop,Settings set,I_LineSlice back){
        notify.notify(Values.important,"z="+ z+ ", внешнее фрезерование, контур id="+loop.id());
        for(STLLoop loop1 : loop.childs()){
            for (STLLoop loop2 : loop1.childs())
                sliceInside(loop2,set,back);
            }
        return true;
        }
    public boolean sliceBlankMany(ArrayList<STLLoop> loops,Settings set,I_LineSlice back){
        notify.notify(Values.important,"z="+ z+ ", внешнее фрезерование, контуров "+loops.size());
        for(STLLoop loop1 : loops){
            for (STLLoop loop2 : loop1.childs())
                sliceInside(loop2,set,back);
            }
        return true;
        }
    public boolean sliceLoop(STLLoop loop,I_LineSlice back){
        for(STLLine line : loop.lines())
            back.onSliceLine(line);
        return true;
        }

    public boolean sliceInside(STLLoop loop,Settings set,I_LineSlice back){
        boolean sliceError = false;
        double cutterSize = set.model.CutterDiameter.getVal()/2;
        double cutterStep = cutterSize - set.model.StepMinus.getVal();
        double workSize = WorkSpace.ws().global().mashine.WorkFrameX.getVal()/2;
        String zz = String.format("z=%4.2f ",z);
        notify.notify(Values.important,zz+" id="+loop.id()+" "+loop.dimStr());
        String inValid=null;
        if ((inValid = loop.isValidLoop())!=null){
            notify.notify(Values.important,"Внешний контур "+" id="+loop.id()+" "+loop.dimStr()+" "+inValid);
            sliceError = true;
            }
        else{
            notify.notify(Values.important,"Внешний контур "+" id="+loop.id()+" "+loop.dimStr()+" "+String.format("%6.2f",loop.linesLength()));
            }
        //---------------------- Внешний контур -----------------------------------------------
        notify.notify(Values.important,"Внешний контур "+" id="+loop.id()+" "+loop.dimStr());
        STLLineGroup copy = loop.shiftToCenter(cutterSize,true);
        if (!copy.errors().valid()){
            notify.notify(Values.important,copy.errors().toString());
            sliceError = true;
            }
        else{
            notify.notify(Values.important,"сдвинутый контур  lnt="+String.format("%6.2f",copy.linesLength()));
            }
        back.onLineGroup();
        for(STLLine line : copy.lines())
            back.onSliceLine(line);
        //-------------------------------------------------------------------------------------
        if (loop.childs().size()==0){
            notify.notify(Values.important,zz+", фрезерование полное");
            }
        else{
            notify.notify(Values.important,zz+", фрезерование,  контуров "+loop.childs().size());
            for(STLLoop loop1 : loop.childs()) {
                notify.notify(Values.important,"внутренний контур id="+loop1.id()+" "+loop1.dimStr()+" lnt="+String.format("%6.2f",loop1.linesLength()));
                if ((inValid = loop1.isValidLoop())!=null){
                    notify.notify(Values.important,"контур id="+loop1.id()+" "+loop1.dimStr()+""+inValid);
                    sliceError = true;
                    }
                else{
                    copy = loop1.shiftToCenter(cutterSize,false);
                    if (!copy.errors().valid()){
                        notify.notify(Values.important,copy.errors().toString());
                        sliceError = true;
                        }
                    else{
                        notify.notify(Values.important,"сдвинутый контур  lnt="+String.format("%6.2f",copy.linesLength()));
                        //notify.notify(Values.important,"!!!!!"+" "+loop1.size()+" "+copy.size());
                        //for (int i=0; i< loop1.size() && i< copy.size(); i++){
                        //    notify.notify(Values.important,""+i+" "+(copy.get(i).lengthXY()-loop1.get(i).lengthXY()));
                        //    }
                        }
                    back.onLineGroup();
                    for(STLLine line : copy.lines())
                        back.onSliceLine(line);
                    }
                }
            }
        //---------------------------------- Выборка вертикальная ------------------------------------------------------
        ArrayList<STLLoop> allLoops = new ArrayList<>();
        allLoops.add(loop);
        for(STLLoop loop1 : loop.childs())
            allLoops.add(loop1);
        STLLine vLine = new STLLine(new STLPoint2D(0,-workSize+cutterStep),new  STLPoint2D(0,workSize-cutterStep));
        int pointsCount=0;
        int pointsSize=0;
        double vX0=0;
        ArrayList<ArrayList<STLPoint2D>> pointsGroup = new ArrayList<>();
        for(double xV=-workSize+cutterStep; xV<workSize-cutterStep; xV+=cutterStep){
            vLine.one().x(xV);
            vLine.two().x(xV);
            ArrayList<STLPoint2D> points = new ArrayList<>();
            for(STLLoop loop1 : allLoops){
                ArrayList<STLPoint2D> xx = loop1.intersect(vLine);
                if (xx.size()%2==1) {           // Что делать????
                    notify.notify(Values.important, "Нечетное число точек пересечения с X=" + String.format("%6.2f - %d", xV, xx.size()));
                    }
                else{
                    for(STLPoint2D pp : xx)
                        points.add(pp);
                    }
                }
            if (pointsSize == points.size()){       // Продолжать накапливать группу
                if (pointsSize!=0){
                    points.sort(new Comparator<STLPoint2D>() {
                        @Override
                        public int compare(STLPoint2D o1, STLPoint2D o2) {
                            if (Math.abs(o1.y()-o2.y()) < Values.EqualDifference)
                                return 0;
                            if (o1.y()<o2.y())
                                return -1;
                            return 1;
                            }
                        });
                    pointsGroup.add(points);        // Отсортировать точки по Y и добавить в группу
                    }
                }
            else{
                if (pointsSize!=0){
                    notify.notify(Values.important,  String.format("Накоплена группа с X=%6.2f...%6.2f, групп-%d точек в группе-%d", vX0,xV-cutterStep, pointsGroup.size(),pointsSize));
                    //----------------------- Формировать линии по 2 точки
                    for(int i=0;i<pointsSize;i+=2){                 // по 2 очередные точки - создать линию
                        ArrayList<STLLine> lines = new ArrayList<>();
                        for(int j=0;j<pointsGroup.size();j++){      // направления меняются на противоположное
                            ArrayList<STLPoint2D> tmp = pointsGroup.get(j);
                            if (tmp.get(i).y()>tmp.get(i+1).y()){
                                STLPoint2D pp = tmp.get(i);
                                tmp.set(i,tmp.get(i+1));
                                tmp.set(i+1,pp);
                                }
                            tmp.get(i).shift(0,cutterSize);     // Концы подрезать
                            tmp.get(i+1).shift(0,-cutterSize);  // Концы подрезать
                            STLLine line = new STLLine(tmp.get(j%2==0 ? i : i+1),tmp.get(j%2==0 ? i+1 : i));
                            lines.add(line);
                            }
                        back.onLineGroup();
                        for(int ii=0;ii<lines.size();ii++){
                            if (ii!=0)
                                back.onSliceLine(new STLLine(lines.get(ii-1).two(),lines.get(ii).one())); // к след. линии
                            back.onSliceLine(lines.get(ii));         // текущая линия
                            }
                        }
                    }
                pointsSize = points.size();
                pointsGroup.clear();
                if (pointsSize!=0){
                    vX0 = xV;
                    pointsGroup.add(points);
                    }
                }
            }
        //--------------------------------------------------------------------------------------------------------------
        for(STLLoop loop1 : loop.childs()) {
            if (loop1.childs().size()==0)
                continue;
            for (STLLoop loop2 : loop1.childs())
                sliceError |= sliceInside(loop2,set,back);
                }
        return sliceError;
        }
    public STLLoop createBlankLoop(Settings set){
        double dx = set.model.BlankWidth.getVal();
        double dy = set.model.BlankHight.getVal();
        if (dx==0 || dy==0){
            dx = set.model.ModelWidth.getVal() ;
            dy = set.model.ModelHight.getVal() ;
            }
        double dCut = set.model.CutterDiameter.getVal()/2;
        dx += dCut;
        dy += dCut;
        STLLoop loop4 = new STLLoop();
        loop4.id(0);
        loop4.add(new STLLine(new STLPoint2D(-dx,-dy), new STLPoint2D(-dx,dy)));
        loop4.add(new STLLine(new STLPoint2D(-dx,dy), new STLPoint2D(dx,dy)));
        loop4.add(new STLLine(new STLPoint2D(dx,dy), new STLPoint2D(dx,-dy)));
        loop4.add(new STLLine(new STLPoint2D(dx,-dy), new STLPoint2D(-dx,-dy)));
        return loop4;
        }
    public boolean sliceMilling(Settings set,I_LineSlice back) throws UNIException {
        STLLoop blank = createBlankLoop(set);
        STLLoop root = createNestingTree();
        boolean sliceError=false;
        if (root!=null){
            if  (!root.isMultiply()){
                blank.childs().add(root);
                }
            else{
                blank.childs(root.childs());
                }
            }
        sliceError = beforeMillingSlice(blank,set,back);
        sliceError |= sliceInside(blank,set,back);
        return false;
        //return sliceError;
        }
    public boolean beforeMillingSlice(STLLoop loop,Settings set,I_LineSlice back){
        boolean error = false;
        String zz = String.format("z=%4.2f ",z);
        String inValid=null;
        if ((inValid = loop.isValidLoop())!=null){
            notify.notify(Values.important,zz+" id="+loop.id()+" "+loop.dimStr()+"\n"+inValid);
            error = true;
            }
        //-------------------------------------------------------------------------------------
        if (loop.childs().size()==0){
            return error;
            }
        for(STLLoop loop1 : loop.childs()) {
            notify.notify(Values.important,"контур id="+loop1.id()+" "+loop1.dimStr());
            if ((inValid = loop1.isValidLoop())!=null){
                notify.notify(Values.important,zz+" id="+loop1.id()+" "+loop1.dimStr()+"\n"+inValid);
                error = true;
                }
            }
        for(STLLoop loop1 : loop.childs()) {
            if (loop1.childs().size()==0)
                continue;
            for (STLLoop loop2 : loop1.childs())
                error |= beforeMillingSlice(loop2,set,back);
            }
        return error;
        }
    }

