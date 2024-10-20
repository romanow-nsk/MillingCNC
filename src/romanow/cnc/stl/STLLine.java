package romanow.cnc.stl;

import romanow.cnc.io.I_File;
import romanow.cnc.settings.WorkSpace;
import romanow.cnc.utils.T_Pair;
import romanow.cnc.utils.UNIException;
import romanow.cnc.Values;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by romanow on 04.12.2017.
 */
public class STLLine implements I_Line2D, I_File {
    /*** точки линии */
    private I_STLPoint2D[] line = new I_STLPoint2D[2];
    /** текущее количество точек */
    private int idx=0;
    /** индекс контура */
    private int loopId=0;
    /** точек пересечения треугольника */
    private int nPoint=0;
    /** sin и cos наклона */
    private double sinf=0,cosf=0;
    /** отметка - просмотрена */
    private boolean done=false;
    public boolean isDone(){ return done; }
    public void done(boolean vv){ done = vv; }
    public STLLine(){
        idx=0;
        }
    public void nPoint(int np){ nPoint=np; }
    public int nPoint(){ return nPoint; }
    public void convertPointsToFloat(){
        if (line[0]!=null) line[0]=new STLPoint2DFloat((float)line[0].x(),(float)line[0].y());
        if (line[1]!=null) line[1]=new STLPoint2DFloat((float)line[1].x(),(float)line[1].y());
        }
    public STLLine(I_STLPoint2D one, I_STLPoint2D two){
        line[0] = one;
        line[1] = two;
        idx=2;
        }
    public STLLine(STLLine src){
        line[0] = src.one().clone();
        line[1] = src.two().clone();
        nPoint = src.nPoint;
        loopId = src.loopId;
        idx=2;
        }
    public void shift(double xx, double yy){
        line[0].shift(xx,yy);
        line[1].shift(xx,yy);
        }
    public void add(STLPoint2D one) throws UNIException{
        if (idx>=2) {
            if (line[0].equalsAbout(line[1])){           // заменить одну из двух одинаковых на третью
                line[1]=one;
                return;
                }
            if (one.equalsAbout(line[0])){               // игнорировать повторяющуюся
                return;
                }
            if (one.equalsAbout(line[1])){               // игнорировать повторяющуюся
                return;
                }
            idx++;
            WorkSpace set = WorkSpace.ws();
            set.notify("В линии "+idx+" точек");
            set.notify(line[0].dump());
            set.notify(line[1].dump());
            set.notify(one.dump());
            //throw UNIException.bug("В линии >2 точек: ");
            }
        else
            line[idx++] = one;
        }
    public int loopId(){ return loopId; }
    public void loopId(int id0) { loopId = id0; }
    public I_STLPoint2D one(){ return line[0]; }
    public I_STLPoint2D two(){ return line[1]; }
    public void one(I_STLPoint2D xx){ line[0]=xx; }
    public void two(I_STLPoint2D xx){ line[1]=xx; }
    public boolean valid(){ return idx==2; }
    /** Перестановка концов */
    public void swap(){ I_STLPoint2D x=line[0]; line[0]=line[1]; line[1]=x; }
    public String toString(){ return one().toString()+" - "+two().toString()+" loop="+loopId+" np="+nPoint; }
    public String dump(){ return one().dump()+" - "+two().dump(); }
    /** получение координаты по mode - для создания индекса и быстрого поиска */
    public double value(int mode){
        switch(mode){
            case 0:
                return line[0].x();
            case 1:
                return line[1].x();
            case 2:
                return line[0].y();
            case 3:
                return line[1].y();
            }
        return 0;
        }
    /** тангенс угла наклона */
    public double B(){
        if (line[0].x()== line[1].x())
            return 1e10;
        return (line[1].y() - line[0].y())/(line[1].x() - line[0].x());
        }
    /** вычисление sin и cos */
    public T_Pair<Double,Double> sinCosXY(){
        double dx = line[1].x()-line[0].x();
        double dy = line[1].y()-line[0].y();
        double dd =  Math.sqrt(dx*dx+dy*dy);
        if (dd==0)
            return new T_Pair<>(new Double(0),new Double(0));
        else
            return new T_Pair<>(new Double(dy/dd),new Double(dx/dd));
        }
    /** Совпадение абсолютное  */
    boolean equals(STLLine x){
        if (one().equals(x.one()) && two().equals(x.two()))
            return true;
        if (one().equals(x.two()) && two().equals(x.one()))
            return true;
        return false;
        }
    boolean equalsAbout(STLLine x){
        if (one().equalsAbout(x.one()) && two().equalsAbout(x.two()))
            return true;
        if (one().equalsAbout(x.two()) && two().equalsAbout(x.one()))
            return true;
        return false;
    }
    /** Совпадение по заданной точности */

    STLLineAB lineKoeffs(){
        double dx = line[1].x()-line[0].x();
        double dy = line[1].y()-line[0].y();
        if (Math.abs(dx)<Values.EqualDifference){
            if (Math.abs(dy)<Values.EqualDifference)
                return new STLLineAB();         // точка
            else
                return new STLLineAB(line[0].x());
            }
        double a = dy/dx;
        double b = line[0].y() - a * line[0].x();
        return new STLLineAB(a,b);
        }


    /** длина проекции на XY */
    public double lengthXY(){
        double dx = line[0].x()-line[1].x();
        double dy = line[0].y()-line[1].y();
        return  Math.sqrt(dx*dx+dy*dy);
        }
    /** точка пересечения линий по XY */
    public STLPoint2D intersection_0(STLLine line){
        I_STLPoint2D p1 = one();
        I_STLPoint2D p2 = two();
        I_STLPoint2D p3 = line.one();
        I_STLPoint2D p4 = line.two();
        //сначала расставим точки по порядку, т.е. чтобы было p1.x <= p2.x
        if (p2.x() < p1.x()) {
            I_STLPoint2D tmp = p1;
            p1 = p2;
            p2 = tmp;
            }
        //и p3.x <= p4.x
        if (p4.x() < p3.x()) {
            I_STLPoint2D tmp = p3;
            p3 = p4;
            p4 = tmp;
            }
        //проверим существование потенциального интервала для точки пересечения отрезков
        if (p2.x() < p3.x()) {
            return null;       //ибо у отрезков нету взаимной абсциссы
            }
        //если оба отрезка вертикальные
        if((p1.x() - p2.x() == 0) && (p3.x() - p4.x() == 0)) {
        //если они лежат на одном X
            if(p1.x() == p3.x()) {
            //проверим пересекаются ли они, т.е. есть ли у них общий Y
            //для этого возьмём отрицание от случая, когда они НЕ пересекаются
                if (!((Math.max(p1.y(), p2.y()) < Math.min(p3.y(), p4.y())) ||
                        (Math.min(p1.y(), p2.y()) > Math.max(p3.y(), p4.y())))) {
                    return new STLPoint2D(p1.x(),p1.y());
                    }
                }
            return null;
            }
        //найдём коэффициенты уравнений, содержащих отрезки
        //f1(x) = A1*x + b1 = y
        //f2(x) = A2*x + b2 = y
        //если первый отрезок вертикальный
        if (p1.x() - p2.x() == 0) {
        //найдём Xa, Ya - точки пересечения двух прямых
            double Xa = p1.x();
            double A2 = (p3.y() - p4.y()) / (p3.x() - p4.x());
            double b2 = p3.y() - A2 * p3.x();
            double Ya = A2 * Xa + b2;
            if (p3.x() <= Xa && p4.x() >= Xa && Math.min(p1.y(), p2.y()) <= Ya &&
                    Math.max(p1.y(), p2.y()) >= Ya) {
                return new STLPoint2D(Xa,Ya);
                }
            return null;
            }
        //если второй отрезок вертикальный
        if (p3.x() - p4.x() == 0) {
        //найдём Xa, Ya - точки пересечения двух прямых
            double Xa = p3.x();
            double A1 = (p1.y() - p2.y()) / (p1.x() - p2.x());
            double b1 = p1.y() - A1 * p1.x();
            double Ya = A1 * Xa + b1;
            if (p1.x() <= Xa && p2.x() >= Xa && Math.min(p3.y(), p4.y()) <= Ya &&
                    Math.max(p3.y(), p4.y()) >= Ya) {
                return new STLPoint2D(Xa,Ya);
                }
            return null;
            }
        //оба отрезка невертикальные
        double A1 = (p1.y() - p2.y()) / (p1.x() - p2.x());
        double A2 = (p3.y() - p4.y()) / (p3.x() - p4.x());
        double b1 = p1.y() - A1 * p1.x();
        double b2 = p3.y() - A2 * p3.x();
        if (A1 == A2) {
            return null; //отрезки параллельны
            }
        //Xa - абсцисса точки пересечения двух прямых
        double Xa = (b2 - b1) / (A1 - A2);
        if ((Xa < Math.max(p1.x(), p3.x())) || (Xa > Math.min( p2.x(), p4.x()))) {
            return null; //точка Xa находится вне пересечения проекций отрезков на ось X
            }
        else{
            return new STLPoint2D(Xa,A2*Xa+b2);
            }
        }
    private static boolean outside(double x, double x1, double x2){
        return x < x1 && x < x2 || x > x1 && x > x2;
        }

    STLPoint2D commonPoint(STLLine linex){
        if (line[0].equalsAbout(linex.line[0]))
            return new STLPoint2D(line[0]);
        if (line[0].equalsAbout(linex.line[1]))
            return new STLPoint2D(line[0]);
        if (line[1].equalsAbout(linex.line[0]))
            return new STLPoint2D(line[1]);
        if (line[1].equalsAbout(linex.line[1]))
            return new STLPoint2D(line[1]);
        return null;
        }

    STLPoint2D intersection(STLLine linex, boolean exact) {
        STLLineAB k1 = lineKoeffs();
        STLLineAB k2 = linex.lineKoeffs();
        if (!k1.valid || !k2.valid)
            return null;
        STLPoint2D out;
        if (k1.vertical && k2.vertical){
            return commonPoint(linex);
            }
        if (k1.vertical){
            double yy = k2.y(x0());                 // Пересечение с вертикалью
            out = new STLPoint2D(x0(),yy);
            if (!exact || (!outside(yy,y0(),y1()) && !outside(x0(),linex.x0(),linex.x1())))
                return out;
            return null;
            }
        if (k2.vertical){
            double yy = k1.y(linex.x0());                 // Пересечение с вертикалью
            out = new STLPoint2D(linex.x0(),yy);
            if (!exact || (!outside(yy,linex.y0(),linex.y1()) && !outside(linex.x0(),x0(),x1())))
                return out;
            return null;
            }
        out = commonPoint(linex);           // Соприкосновение концов
        if (out!=null)
            return out;
        double da = Math.abs(k1.a - k2.a);  // Пересечение наклонных
        if (da < Values.EqualDifference){
            if (line[0].equalsAbout(linex.line[0]))
                return new STLPoint2D(line[0].x(),line[0].y());
            return null;
            }
        double x = (k2.b-k1.b)/(k1.a-k2.a);
        double y = k1.a * x + k1.b;
        out = new STLPoint2D(x,y);
        if (exact && outside(out.x(),x0(),x1()))
            return null;
        return out;
        }
    //------------------------------------------------------------------------------------------------------
    STLPoint2D intersection1(STLLine line) {
        STLPoint2D dir1 = new STLPoint2D(two().x() - one().x(),two().y()-one().y());
        STLPoint2D dir2 = new STLPoint2D(line.two().x() - line.one().x(),line.two().y()-line.one().y());
        double x0=one().x(),y0=one().y();
        double x1=line.one().x(),y1=line.one().y();
        double x2=two().x(),y2=two().y();
        double x3=line.two().x(),y3=line.two().y();
        //считаем уравнения прямых проходящих через отрезки
        double a1 = - dir1.y();
        double b1 = + dir1.x();
        double d1 = -(a1*x0 + b1*y0);

        double a2 = -dir2.y();
        double b2 = +dir2.x();
        double d2 = -(a2*x1 + b2*y1);

        //подставляем концы отрезков, для выяснения в каких полуплоскотях они
        double seg1_line2_start = a2*x0 + b2*y0 + d2;
        double seg1_line2_end = a2*x2 + b2*y2 + d2;

        double seg2_line1_start = a1*x1 + b1*y1 + d1;
        double seg2_line1_end = a1*x3 + b1*y3 + d1;

        //если концы одного отрезка имеют один знак, значит он в одной полуплоскости и пересечения нет.
        if (seg1_line2_start * seg1_line2_end >= 0 || seg2_line1_start * seg2_line1_end >= 0){
            if (!pointIntersection(line.one()) && !pointIntersection(line.two()))
                return null;
            //else
            //    System.out.println("?");
            }
        double u = seg1_line2_start / (seg1_line2_start - seg1_line2_end);
        return new STLPoint2D(one().x()+ u*dir1.x(),one().y()+u*dir1.y());
        }
    //-------------------------------- Принадлежность точки отрезку -----------------------------------------
    public boolean pointIntersection(I_STLPoint2D point){
        //(x-x1)(y2-y1)-(y-y1)(x2-x1) = 0
        return Math.abs((point.x()-one().x())*(two().y()-one().y())-(point.y()-one().y())*(two().x()-one().x())) < Values.EqualDifference;
        }
    //-------------------------------------------------------------------------------------------------------
    private I_STLPoint2D pointFactory(int id){
        switch (id){
            case Values.classId2D: return new STLPoint2D();
            case Values.classId2dFloat: return new STLPoint2DFloat();
            case Values.classIdRefered: return new STLReferedPoint();
            }
        return null;
        }
    @Override
    public void load(DataInputStream in) throws IOException {
        int vv = in.readByte();
        idx = vv & 0x0F;
        line = new I_STLPoint2D[2];
        if (idx>=1) {
            line[0]=pointFactory(in.readByte());
            line[0].load(in);
            }
        if (idx==2) {
            line[1]=pointFactory(in.readByte());
            line[1].load(in);
            }
        loopId = in.readShort();
        nPoint = in.readByte();
        }
    @Override
    public void save(DataOutputStream out) throws IOException {
        int vv = idx;
        out.writeByte(vv);
        if (idx>=1){
            out.writeByte(line[0].classId());
            line[0].save(out);
            }
        if (idx==2){
            out.writeByte(line[1].classId());
            line[1].save(out);
            }
        out.writeShort(loopId);
        out.writeByte(nPoint);
        }
    private double getYL(){
        //YL = (y2x1-y1x2)/(x1-x2)
        return (line[1].y()*line[0].x() - line[0].y()*line[1].x())/(line[0].x()-line[1].x());
        }
    private double getXR(){
        //YL = (y1x2-y2x1)/(y1-y2)
        return (line[0].y()*line[1].x() - line[1].y()*line[0].x())/(line[0].y()-line[1].y());
        }
    private double getYR(){
        // YR = (y2-y1)(W-x1)/(x2-x2)+y1
        double dd = WorkSpace.ws().global().mashine.WorkFrameX.getVal();
        return (line[1].y()-line[0].y())*(dd-line[0].x())/(line[1].x()-line[0].x())+line[0].y();
        }
    private double getXL(){
        // YR = (x2-x1)(H-y1)/(y2-y2)+x1
        double dd = WorkSpace.ws().global().mashine.WorkFrameX.getVal();
        return (line[1].x()-line[0].x())*(dd-line[0].y())/(line[1].y()-line[0].y())+line[0].x();
    }
    //+++ 1.1 ------------------------------ Растянуть отрезов до полной линии рабочего стола --------------------------
    public STLLine expandToFullSize(){
        STLLine out = clone();
        STLLineAB koef = lineKoeffs();
        double FSize = WorkSpace.ws().global().mashine.WorkFrameX.getVal()/2;
        double y = koef.y(-FSize);
        double x=0;
        if (koef.vertical){
            out.one().y(-FSize);
            out.two().y(FSize);
            return out;
            }
        if (Math.abs(y) > FSize){
            x = koef.x(0);
            out.line[0].x(x);
            out.line[0].y(koef.a > 0 ? FSize : -FSize);
            }
        else{
            out.line[0].x(-FSize);
            out.line[0].y(y);
            }
        y = koef.y(FSize);
        x=0;
        if (Math.abs(y) > FSize){
            x = koef.x(FSize);
            out.line[1].x(x);
            out.line[1].y(koef.a > 0 ? FSize : -FSize);
            }
        else{
            out.line[1].x(FSize);
            out.line[1].y(y);
            }
        return out;
        /*
        double a,b;
        if (line[0].equalsAboutX(line[1])){ // Растянуть вертикально
            b = (line[1].x()-line[0].x())/(line[1].y()-line[0].y());
            a = line[0].x()-b*line[0].y();
            out.line[0].x(a-b*FSize);
            out.line[0].y(-FSize);
            out.line[1].x(a+b*FSize);
            out.line[1].y(FSize);
            }
        else
        if (line[0].equalsAboutY(line[1])) { // Растянуть горизонтально
            b = (line[1].y()-line[0].y())/(line[1].x()-line[0].x());
            a = line[0].y()-b*line[0].x();
            out.line[0].x(-FSize);
            out.line[0].y(a-b*FSize);
            out.line[1].x(FSize);
            out.line[1].y(a+b*FSize);
            }
        else{
            //----- y = b*x+a
            b = (line[1].y()-line[0].y())/(line[1].x()-line[0].x());
            a = line[0].y()-b*line[0].x();
            double yy = -b*FSize+a;     // Координата в левой точке
            if (yy>FSize){
                out.line[0].x((FSize-a)/b);
                out.line[0].y(FSize);
                }
            else
            if (yy<0){
                out.line[0].x(-a/b);
                out.line[0].y(0);
                }
            else{
                out.line[0].x(-FSize);
                out.line[0].y(yy);
                }
            yy = b*FSize+a;     // Координата в правой точке
            if (yy>FSize){
                out.line[1].x((FSize-a)/b);
                out.line[1].y(FSize);
            }
            else
            if (yy<0){
                out.line[1].x((FSize-a)/b);
                out.line[1].y(0);
                }
            else{
                out.line[1].x(FSize);
                out.line[1].y(yy);
                }
            }
        return out;
         */
        }

    /** true - точно не пересекает, false - пересечение возможно */
    public boolean noIntersection(STLLine line){
        double cc;
        double xmin = one().x(), xmax=two().x();
        if (xmin > xmax){ cc=xmin; xmin=xmax; xmax=cc; }
        double ymin = one().y(), ymax=two().y();
        if (ymin > ymax){ cc=ymin; ymin=ymax; ymax=cc; }
        if (line.one().x()< xmin && line.two().x()< xmin)
            return true;
        if (line.one().x()> xmax && line.two().x() > xmax)
            return true;
        if (line.one().y()< ymin && line.two().y()< ymin)
            return true;
        if (line.one().y()> ymax && line.two().y() > ymax)
            return true;
        return false;
        }
    /** Средняя точка */
    public I_STLPoint2D middle(){
        return new STLPoint2D((one().x()+two().x())/2, (one().y()+ two().y())/2);
        }
    public void invertX(){
        line[0].invertX();
        line[1].invertX();
        }
    public STLLine clone(){
        STLLine out = new STLLine(line[0].clone(),line[1].clone());
        return out;
        }
    public STLLine rotateXY(MyAngle angleXY){
        return new STLLine(one().rotateXY(angleXY),two().rotateXY(angleXY));
        }
    @Override
    public double x0() { return one().x(); }
    @Override
    public double y0() { return one().y(); }
    @Override
    public double x1() { return two().x(); }
    @Override
    public double y1() { return two().y(); }
    //----------- Параллельный сдвиг линии к/от центральной точки
    public STLLine shiftParallel(double step, I_STLPoint2D center, boolean toCenter){
        T_Pair<Double,Double> sc = sinCosXY();
        double dx,dy;
        STLLine line1 = clone();
        STLLine line2 = clone();
        if (Math.abs(sc._1().doubleValue())<Values.SinCosIs0){   // Горизонтально
            dx = 0;
            dy = step;
            }
        else
        if (Math.abs(sc._2().doubleValue())<Values.SinCosIs0){   // Вертикально
            dx = step;
            dy = 0;
            }
        else
        if (Math.abs(sc._1().doubleValue())>Math.abs(sc._2().doubleValue())){  // Ближе к вертикали
            dx = step * sc._2();
            dy = step * sc._1();
            }
        else{                                                   // Ближе к горизонтали
            dx = step * sc._1();
            dy = step * sc._2();
            }
        line1.shift(dx,dy);
        line2.shift(-dx,-dy);
        boolean bb = line1.one().diffXY(center) < line2.one().diffXY(center);
        return bb == toCenter ? line1 : line2;
        }
    public static void main(String ss[]){
        STLLine line1 = new STLLine(new STLPoint2D(5,5),new STLPoint2D(5,10));
        System.out.println(line1+" "+line1.expandToFullSize());
        line1 = new STLLine(new STLPoint2D(5,5),new STLPoint2D(25,5));
        System.out.println(line1+" "+line1.expandToFullSize());
        line1 = new STLLine(new STLPoint2D(5,5),new STLPoint2D(25,25));
        System.out.println(line1+" "+line1.expandToFullSize());
        line1 = new STLLine(new STLPoint2D(5,5),new STLPoint2D(25,20));
        System.out.println(line1+" "+line1.expandToFullSize());
        line1 = new STLLine(new STLPoint2D(5,5),new STLPoint2D(20,25));
        System.out.println(line1+" "+line1.expandToFullSize());
        line1 = new STLLine(new STLPoint2D(20,25),new STLPoint2D(5,5));
        System.out.println(line1+" "+line1.expandToFullSize());
        }
}
