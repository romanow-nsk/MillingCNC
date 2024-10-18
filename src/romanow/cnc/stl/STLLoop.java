package romanow.cnc.stl;

import romanow.cnc.io.I_File;
import romanow.cnc.settings.WorkSpace;
import romanow.cnc.Values;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by romanow on 04.12.2017.
 */
/** Набор линий и контур - связность косвенно при построении */
public class STLLoop extends STLLineGroup implements I_File {
    private double diff=0;
    private int id=0;
    private boolean repaired=false;
    // Тип контура по кол-ву точек пересечения
    // 1 - смешанный
    // 0 - сечение
    // 2 - поверхность
    // 3 - плоскость
    private boolean multiply=false;
    private int lineMode=0;
    private ArrayList<STLLoop> childs = new ArrayList<>();         //+++ 1.1 Дерево вложености контуров
    private ArrayList<STLLoop> parents = new ArrayList<>();        //+++ 1.1 Дерево вложености контуров
    public ArrayList<STLLoop> childs(){ return childs; }
    public ArrayList<STLLoop> parents(){ return parents; }
    public boolean isRepaired(){ return repaired; }
    public STLLoop(double diff0){
        diff = diff0;
        }
    public int id() {
        return id;
        }
    public void id(int id) {
        this.id = id;
        }

    public boolean isMultiply() {
        return multiply; }
    public void multiply(boolean multiply) {
        this.multiply = multiply; }
    public void childs(ArrayList<STLLoop> ss){
        childs = ss;
        }
    /** Тип контура по пересечению */
    public int loopLineMode(){
        //return lines().size()==0 ? -1 : lines().get(0).nPoint();
        return lineMode;
        }
    public void setLoopLineMode(int mode){ lineMode=mode; }
    public void setLoopLineMode(){
        lineMode=lines().get(0).nPoint();
        }
        /** Слить координаты соединенных точек */
    public void correct(){
        int n=size();
        for(int i=0;i<n-1;i++)
            get(i).two().setCoordinates(get(i+1).one());
        if (isCompleted())
            get(n-1).two().setCoordinates(get(0).one());
        }
    /** Для загрузки из потоков */
    public STLLoop(){}
    public STLLoop(STLLine line, double diff0){
        diff = diff0;
        add(line);
        line.loopId(id);
        }
    /** добавить отрезок */
    public void add(STLLine line){
        super.add(line);
        line.loopId(this.id);            // ссылка на текущий контур
        }
    /** добавить группу отрезков */
    public void add(ArrayList<STLLine> two){
        super.add(two);
        for(STLLine xx : two)
            xx.loopId(this.id);          // при слиянии - ссылка на новый контур
        }
    public void insert(int idx, STLLine xx) {
        super.insert(idx,xx);
        xx.loopId(this.id);
        }
    /** проверка замкнутости контура */
    public boolean isCompleted(){
        if (size()==0)
            return false;
        return get(0).one().diffXY(get(size()-1).two())<=diff;
        }
    /* принудительное замыкание контура */
    public STLLine repair(){
        repaired = false;
        if (isCompleted())
            return null;
        if (size()==0)
            return null;
        System.out.println(get(0).one().dump());
        System.out.println(get(size()-1).two().dump());
        STLLine out = new STLLine(get(size()-1).two().clone(), get(0).one().clone());
        add(out);
        WorkSpace.ws().notify(Values.important,"Замыкание контура, длина="+String.format("%6.3f",out.lengthXY())+" мм");
        System.out.println("Замыкание контура, длина="+out.lengthXY());
        repaired = true;
        return out;
        }
    /** первая точка контура */
    public I_STLPoint2D first(){ return get(0).one(); }
    /** последняя точка контура */
    public I_STLPoint2D last(){ return get(size()-1).two(); }
    /** поменять направление контура на противоположное */
    public void swap(){
        ArrayList<STLLine> out = new ArrayList<>();
        for(int i=size()-1;i>=0;i--){
            STLLine ln = get(i);
            ln.swap();
            out.add(ln);
            }
        set(out);
        }
    /** Определение центра контура */
    public STLPoint2D center(){
        ArrayList<STLLine> lines = lines();
        int n = lines.size();
        if (n==0) return null;
        double sx=0,sy=0;
        for(STLLine ln : lines){
            sx += ln.one().x();
            sy += ln.one().y();
            }
        return new STLPoint2D(sx/n,sy/n);
        }
    /** удаление отрезка с коррекцией концов соседа */
    public boolean removeWithCorrect(int idx){
        if (size()==1)
            return false;
        STLLine ln = get(idx);
        if (idx!=0){            // второй предыдущего - второй удаляемого
            I_STLPoint2D pp = get(idx-1).two();
            pp.x(ln.two().x());
            pp.y(ln.two().y());
            }
        else{                   // первый последующего - первый удаляемого
            I_STLPoint2D pp = get(idx+1).one();
            pp.x(ln.one().x());
            pp.y(ln.one().y());
            }
        remove(idx);
        return true;
        }
    /** Замена отрезка контура на 2 стороны треугольника при совпадающий третьей */
    private void fff(int i, int i0,int i1, int i2, STLLine triang[]){
        triang[0].loopId(id);
        triang[1].loopId(id);
        triang[2].loopId(id);
        if (get(i).one().equalsAbout(triang[i0].one())){
            triang[i2].swap();
            triang[i1].swap();
            remove(i);
            if (i==size()){
                add(triang[i2]);
                add(triang[i1]);
            }
            else{
                insert(i,triang[i2]);
                insert(i+1,triang[i1]);
            }
        }
        else{
            remove(i);
            if (i==size()){
                add(triang[i1]);
                add(triang[i2]);
            }
            else{
                insert(i,triang[i1]);
                insert(i+1,triang[i2]);
                }
            }
        }
    /** Вставка треугольника в контур по совпадению стороны */
    public boolean linkToLoop(STLLine triang[]){
        int n=size();
        for (int i=0;i<n;i++){
            STLLine line = get(i);
            if (line.equalsAbout(triang[0])){
                fff(i,0,1,2, triang);
                return true;
                }
            if (line.equalsAbout(triang[1])) {
                fff(i, 1, 2, 0, triang);
                return true;
                }
            if (line.equalsAbout(triang[2])){
                fff(i,2,0,1, triang);
                return true;
                }
            }
        return false;
        }
    /** Сглаживание контура при ограничении угла и длине */
    public int evenLoop(STLLineGroup orig, double lenght){
        int sz = lines().size();
        int cnt=0;
        ArrayList<STLLine> lines = lines();
        int seqCount=0;
        for(int i=0; i<sz-1;i++){
            STLLine l1 = lines.get(i);
            STLLine l2 = lines.get(i+1);
            if (l2.lengthXY() > lenght){
                seqCount=0;
                continue;
                }
            MyAtan a1 = new MyAtan(l1);
            MyAtan a2 = new MyAtan(l2);
            if (a1.fCompare(a2)> Values.EvenAngleDiffenerce){
                seqCount=0;
                continue;
                }
            seqCount++;
            if (seqCount>=Values.EvenSeqLenght){
                seqCount=0;
                //System.out.println("Прервано сглаживание");
                continue;
                }
            I_STLPoint2D p = l2.two();          // Сгладить
            l1.two().setCoordinates(p);
            lines.remove(i+1);
            //---------------------- Оригинал корректировать не надо (НАДО)
            orig.remove(l2);
            cnt++;
            i--;
            sz--;
            }
        return cnt;
        }
    @Override
    public void load(DataInputStream in) throws IOException {
        super.load(in);
        repaired = in.readBoolean();
        diff = in.readDouble();
        id = in.readInt();
    }
    @Override
    public void save(DataOutputStream out) throws IOException {
        super.save(out);
        out.writeBoolean(repaired);
        out.writeDouble(diff);
        out.writeInt(id);
        }
    /** Проверка на одинаковость происхождения точек */
    public void testLoop(){
        int np[]={0,0,0,0};
        for(STLLine ln : lines()){
            np[ln.nPoint()]++;
            }
        int xx=0;
        for(int i=0;i<4;i++)
            if (np[i]!=0) xx++;
        if (xx!=1)
            System.out.println(String.format("%d %d %d %d",np[0],np[1],np[2],np[3]));
        }
    /** Вычисление радиуса контура */
    public StatInfo calcRadiusToCenter(){
        StatInfo stat=new StatInfo();
        if (lines().size()==0) return stat;
        STLPoint2D pp = center();
        for(STLLine ll : lines()){
            stat.addValue(pp.diffXY(ll.one()));
            }
        stat.calcStatistic();
        return stat;
        }
    /** Вычисление кривизны */
    public StatInfo calcCurvature(){
        StatInfo stat=new StatInfo();
        if (lines().size()==0) return stat;
        STLPoint2D pp = center();
        for(STLLine ll : lines()){
            double vv = 90-Math.abs(pp.getAngleABC(ll.one(),ll.two()));
            stat.addValue(vv);
            double vv1 = -(90-Math.abs(pp.getAngleABC(ll.two(),ll.one())));
            System.out.println(vv+" "+vv1);
            stat.addValue(vv1);
            }
        stat.calcStatistic();
        return stat;
        }
    //+++ 1.1------------------------------------ Вложенность контуров 2 внутри 1 --------------------------------------
    public boolean nestingInCurrent(STLLoop outSide, boolean trace) {
        int idx1=0;
        for (STLLine own : outSide.lines()) {
            own = own.expandToFullSize();
            if (trace)
                System.out.print("id="+id+"["+idx1+"] "+own+" ");
            idx1++;
            int count = 0;
            int idx2=-1;
            for (STLLine other : lines()) {
                idx2++;
                //other = other.expandToFullSize();
                //if (own.noIntersection(other))            //8%
                //    continue;
                I_Point2D pp = own.intersection(other,true);  //23%
                if (pp != null) {
                    count++;
                    if (trace)
                        System.out.print(idx2+" ");
                    }
                }
            if (count == 0)
                return false;
            if (count %2 == 1) {
                System.out.println("["+count+"] ");
                return false;
                }
            if (trace)
                System.out.println();
            }
        return true;
        }
    //------------------------------------------------------------------------------------
    public void printTree(int level){
        System.out.println("level="+level+" id="+id+(parents.size()==0 ? "" : "<-"+parents.get(0).id));
        for(STLLoop loop : childs)
            loop.printTree(level+1);
        }
    /*
    public void removeExtra(int level){
        int idx;
        for(idx=0; idx<childs.size();){
            STLLoop ll = childs.get(idx);
            if (ll.maxLevel!=level+1)
                childs.remove(idx);
            else
                idx++;
            }
        for(STLLoop loop : childs)
            loop.removeExtra(level+1);
        }
     */
    public int getMaxPath(){
        if (parents.size()==0)
            return 0;
        int max=0;
        for(STLLoop loop : parents){
            int vv = loop.getMaxPath();
            if (vv > max)
                max = vv;
                }
        return max+1;
        }
    public String dimStr(){
        if (lines().size()==0)
            return "w=0 h=0";
        STLLine line = minmax();
        return String.format("w=%4.2f h=%4.2f",
                (line.two().x()-line.one().x()),
                (line.two().y()-line.one().y()));
        }
    //------------------------------------------------------------------------------------
    public static ArrayList<STLLoop> createLoopList(){
        ArrayList<STLLoop> loops = new ArrayList<>();
        STLLoop loop1;
        loop1 = new STLLoop();
        loop1.add(new STLLine(new STLPoint2D(50,50), new STLPoint2D(50,80)));
        loop1.add(new STLLine(new STLPoint2D(50,80), new STLPoint2D(80,80)));
        loop1.add(new STLLine(new STLPoint2D(80,80), new STLPoint2D(80,50)));
        loop1.add(new STLLine(new STLPoint2D(80,50), new STLPoint2D(50,50)));
        loop1.id=1;
        loops.add(loop1);
        STLLoop loop2 = new STLLoop();
        loop2.add(new STLLine(new STLPoint2D(55,55), new STLPoint2D(65,65)));
        loop2.add(new STLLine(new STLPoint2D(65,65), new STLPoint2D(75,55)));
        loop2.add(new STLLine(new STLPoint2D(75,55), new STLPoint2D(55,55)));
        loop2.id=2;
        loops.add(loop2);
        STLLoop loop3 = new STLLoop();
        loop3.add(new STLLine(new STLPoint2D(20,25), new STLPoint2D(30,90)));
        loop3.add(new STLLine(new STLPoint2D(30,90), new STLPoint2D(90,95)));
        loop3.add(new STLLine(new STLPoint2D(90,95), new STLPoint2D(85,15)));
        loop3.add(new STLLine(new STLPoint2D(85,15), new STLPoint2D(20,25)));
        loop3.id=3;
        loops.add(loop3);
        STLLoop loop4 = new STLLoop();
        loop4.add(new STLLine(new STLPoint2D(10,60), new STLPoint2D(10,62)));
        loop4.add(new STLLine(new STLPoint2D(10,62), new STLPoint2D(90,62)));
        loop4.add(new STLLine(new STLPoint2D(90,62), new STLPoint2D(90,60)));
        loop4.add(new STLLine(new STLPoint2D(90,60), new STLPoint2D(10,60)));
        loop4.id=4;
        loops.add(loop4);
        loop1 = new STLLoop();
        loop1.add(new STLLine(new STLPoint2D(75,75), new STLPoint2D(77,77)));
        loop1.add(new STLLine(new STLPoint2D(77,77), new STLPoint2D(77,73)));
        loop1.add(new STLLine(new STLPoint2D(77,73), new STLPoint2D(75,75)));
        loop1.id=5;
        loops.add(loop1);
        loop1 = new STLLoop();
        loop1.add(new STLLine(new STLPoint2D(75,75), new STLPoint2D(75,77)));
        loop1.add(new STLLine(new STLPoint2D(75,77), new STLPoint2D(77,77)));
        loop1.add(new STLLine(new STLPoint2D(77,77), new STLPoint2D(77,75)));
        loop1.add(new STLLine(new STLPoint2D(77,75), new STLPoint2D(75,75)));
        loop1.id=6;
        loops.add(loop1);
        loop1 = new STLLoop();
        loop1.add(new STLLine(new STLPoint2D(1,1), new STLPoint2D(1,5)));       // Один вне дерева
        loop1.add(new STLLine(new STLPoint2D(1,5), new STLPoint2D(5,5)));
        loop1.add(new STLLine(new STLPoint2D(5,5), new STLPoint2D(5,1)));
        loop1.add(new STLLine(new STLPoint2D(5,1), new STLPoint2D(1,1)));
        loop1.id=7;
        loops.add(loop1);
        return loops;
        }
    public static void main(String ss[]){
        ArrayList<STLLoop> loops = createLoopList();
        for(int i=1;i<=loops.size();i++){
            for(int j=1;j<=loops.size();j++){
                if (i==j)
                    continue;
                System.out.println(loops.get(i-1).id+"-"+loops.get(j-1).id +" "+loops.get(i-1).nestingInCurrent(loops.get(j-1),true));
                }
            }
        }
    }
