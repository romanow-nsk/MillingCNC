package romanow.cnc.slicer;

/**
 * Created by romanow on 10.12.2017.
 */

import romanow.cnc.settings.Settings;

import romanow.cnc.utils.I_Notify;
import romanow.cnc.Values;
import romanow.cnc.stl.*;

/** перехватчик результата слайсирования для chess */
public class ChessAdapter implements I_LineSlice{
    private I_LineSlice prevBack;
    private boolean chessMode;
    private MyAngle angleXYBack;
    private MyAngle angleXY90Back;
    private MyAngle angleXY;
    private MyAngle angleXY90;
    private I_Notify notify;
    /** шаг шахматной сетки */
    private double cellStep;
    void setMode(boolean md){ chessMode=md; }
    ChessAdapter(Settings set, I_LineSlice old, double angle, I_Notify notify0){
        notify = notify0;
        angleXYBack = new MyAngle(-angle);                   // поворот в обратную сторону
        angleXY90Back = new MyAngle(-angle - Math.PI/2);// поворот в обратную сторону
        angleXY = new MyAngle(angle);                        // поворот в обратную сторону
        angleXY90 = new MyAngle(angle + Math.PI/2);     // поворот в обратную сторону
        prevBack = old;
        cellStep = set.slice.FillParametersFillCell.getVal();
    }
    /** Отрезать часть линии по длине - кроме вертикальной */
    public STLLine cutByLentgh(STLLine line, double len){
        MyAngle angle = chessMode ? angleXY : angleXY90;
        double dy = len * angle.sinf;
        double dx = len * angle.cosf;
        double x1 = line.one().x();
        double y1 = line.one().y();
        double x2 = line.one().x()+dx;          // координаты со сдвигом
        double y2 = line.one().y()+dy;          // новый отрезок
        STLLine first = new STLLine(new STLPoint2D(x1,y1),new STLPoint2D(x2,y2));
        line.one().x(x2);                       // коррекция начала старого
        line.one().y(y2);
        return first;
    }

    @Override
    public void onSliceLayer() {
        prevBack.onSliceLayer();
    }

    @Override
    public void onLineGroup() {
        prevBack.onLineGroup();
        }
    // поворот системы координат
    // x = x′ cosφ − y′⋅sinφ
    // y = x′ sinφ + y′⋅cosφ.
    // Сетка параллеьно направлению лучей
    private void chess1(STLLine line){
        MyAngle angle = angleXYBack;
        line = new STLLine(line);
        I_STLPoint2D p1 = line.one().rotateXY(angle);
        I_STLPoint2D p2 = line.two().rotateXY(angle);
        if (p1.x() > p2.x()){
            I_STLPoint2D c=p1; p1=p2;p2=c;
            }
        if (angle.angle < -Math.PI/2)
            line.swap();
        int idx1 = (int)((1+p1.x())/cellStep);
        int idx2 = (int)((1+p2.x())/cellStep);
        int idy1 = (int)((1+p1.y())/cellStep);
        int idy2 = (int)((1+p2.y())/cellStep);
        STLLineGroup out = new STLLineGroup();      // вектор разрезанных
        boolean enable = true;
        if (idx1%2 == idy1%2)                       // совпадение четности клеток
            enable = !enable;
        if (idx1==idx2){                            // отрезок целиком в квадратике
            if (enable){
                prevBack.onSliceLine(line);
            }
            return;
        }
        double len = 0;                             // длина разрезаемого отрезка
        len = (idx1+1)*cellStep - p1.x()-1;         // неполная длина в первом квадрате
        STLLine cut = cutByLentgh(line,len);
        if (enable)
            out.add(cut);
        enable = !enable;
        for(int ii=idx1+1; ii<idx2; ii++){
            len = cellStep;
            cut = cutByLentgh(line,len);
            if (enable)
                out.add(cut);
            enable = !enable;
            }
        len = p2.x()-idx2*cellStep +1;              // неполная длина в последнем квадрате
        cut = cutByLentgh(line,len);
        if (enable)
            out.add(cut);
        for (STLLine xx: out.lines())
            prevBack.onSliceLine(xx);
        }
    private void chess2(STLLine line){
        line = new STLLine(line);
        MyAngle angle = angleXYBack;
        I_STLPoint2D p1 = line.one().rotateXY(angle);
        I_STLPoint2D p2 = line.two().rotateXY(angle);
        if (p1.y() > p2.y()){
            I_STLPoint2D c=p1; p1=p2;p2=c;
            }
        if (Math.abs(angle.angle % Math.PI) < Values.EqualDifference)
            line.swap();
        if (angle.angle < Math.PI/2)
            line.swap();
        int idx1 = (int)((1+p1.x())/cellStep);
        int idx2 = (int)((1+p2.x())/cellStep);
        int idy1 = (int)((1+p1.y())/cellStep);
        int idy2 = (int)((1+p2.y())/cellStep);
        STLLineGroup out = new STLLineGroup();      // вектор разрезанных
        boolean enable = false;
        if (idx1%2 == idy1%2)                       // совпадение четности клеток
            enable = !enable;
        if (idy1==idy2){                            // отрезок целиком в квадратике
            if (enable){
                prevBack.onSliceLine(line);
            }
            return;
        }
        double len = 0;                             // длина разрезаемого отрезка
        len = (idy1+1)*cellStep - p1.y()-1;         // неполная длина в первом квадрате
        STLLine cut = cutByLentgh(line,len);
        if (enable)
            out.add(cut);
        enable = !enable;
        for(int ii=idy1+1; ii<idy2; ii++){
            len = cellStep;
            cut = cutByLentgh(line,len);
            if (enable)
                out.add(cut);
            enable = !enable;
        }
        len = p2.y()-idy2*cellStep +1;              // неполная длина в последнем квадрате
        cut = cutByLentgh(line,len);
        if (enable)
            out.add(cut);
        for (STLLine xx: out.lines())
            prevBack.onSliceLine(xx);
        }
    @Override
    public void onSliceLine(STLLine line) {
        if (chessMode)  chess1(line); else  chess2(line);
        /*
        MyAngle angle = chessMode ? angleXYBack : angleXY90Back;
        I_STLPoint2D p1 = line.one().rotateXY(angle);
        I_STLPoint2D p2 = line.two().rotateXY(angle);
        if (p1.x() > p2.x()){
            I_STLPoint2D c=p1; p1=p2;p2=c;
            }
        if (angle.angle < -Math.PI/2)
            line.swap();
        int idx1 = (int)((1+p1.x())/cellStep);
        int idx2 = (int)((1+p2.x())/cellStep);
        int idy1 = (int)((1+p1.y())/cellStep);
        int idy2 = (int)((1+p2.y())/cellStep);
        STLLineGroup out = new STLLineGroup();      // вектор разрезанных
        boolean enable = chessMode;
        if (idx1%2 == idy1%2)                       // совпадение четности клеток
            enable = !enable;
        if (idx1==idx2){                            // отрезок целиком в квадратике
            if (enable){
                prevBack.onSliceLine(line);
                }
            return;
            }
        double len = 0;                             // длина разрезаемого отрезка
        int id1 = idx1;
        int id2 = idx2;
        double xy1 = p1.x();
        double xy2 = p2.x();
        len = (id1+1)*cellStep - xy1-1;         // неполная длина в первом квадрате
        STLLine cut = cutByLentgh(line,len);
        if (enable)
            out.add(cut);
        enable = !enable;
        for(int ii=id1+1; ii<id2; ii++){
            len = cellStep;
            cut = cutByLentgh(line,len);
            if (enable)
                out.add(cut);
            enable = !enable;
        }
        len = xy2-id2*cellStep +1;              // неполная длина в последнем квадрате
        cut = cutByLentgh(line,len);
        if (enable)
            out.add(cut);
        for (STLLine xx: out.lines())
            prevBack.onSliceLine(xx);
            */
        }
    @Override
    public boolean isFinish() {
        return prevBack.isFinish();
    }

    @Override
    public void onSliceError(SliceError points) {
        prevBack.onSliceError(points);
    }

    @Override
    public void notify(int level, String mes) {
        notify.notify(level,mes);
    }
}

