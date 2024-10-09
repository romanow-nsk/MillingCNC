package epos.slm3d.slicer;

/**
 * Created by romanow on 10.12.2017.
 */

import epos.slm3d.settings.Settings;
import epos.slm3d.stl.*;
import epos.slm3d.utils.I_Notify;
import epos.slm3d.utils.Values;

/** перехватчик результата слайсирования для случайного деления
 * 1. Поворачивает исходный контур -angle - обратно и определяет границы (Cliser)
 * 2. Строит дерево с поочережным делением на каждом уровне по вертикали и горизонтали
 * деление пополам со случайным отклонением на K=0.15 в обе стороны
 * 3. Slicer создает адаптер и делает 2 линейных слайсинга для основных и перпендикуляреных линий, настраивая адаптер
 * 4. Обработка линии
 * 4.1. поворачивает -angle линию
 * 4.2. препускает через дерево
 * 4.3. если направление линии и направление деления совпадают, то передает линию полностью в одно из поддеревьев
 * 4.4. иначе - смотрит, как средняя линия делит входную - в одно из поддеревьев, либо на 2 части
 * 4.5. в концевой вершине выбирается половина линий в зависимости от ориентации входной линии, последней и предпоследней
 * вершин
 * */
public class RandomAdapter implements I_LineSlice{
    private boolean vertical=false;             // Ориентация ЛИНИЙ
    private double size=0;                      // Размерность по разрезаемой координате
    private double middle=0;                    // Координата разрезания (середина)
    private RandomAdapter left=null,right=null; // Вершины поддерева
    private STLLine minmax=null;
    private I_LineSlice prevBack;       // Перпехваченный адаптер
    private static double K=0.15;       // Коэффициент неравномерности
    private MyAngle angleXYBack;        // Угол обратного поворота
    private MyAngle angleXY;
    private boolean lineVertical=false;
    private I_Notify notify;
    public void lineVertical(boolean vv){ lineVertical=vv; }
    RandomAdapter(I_LineSlice old, double angle, boolean vertical0, STLLine minmax0, int level, I_Notify notify0){
        notify = notify0;
        vertical = vertical0;
        minmax = minmax0;
        angleXYBack = new MyAngle(-angle);                   // поворот в обратную сторону
        angleXY = new MyAngle(angle);                        // поворот в обратную сторону
        prevBack = old;
        STLLine minmax1 = minmax.clone();
        STLLine minmax2 = minmax.clone();
        if (vertical){
            size = minmax.two().x() - minmax.one().x();
            middle =  minmax.one().x() + size/2 + (K*size*(Math.random()*2-1));
            minmax1.two().x(middle);
            minmax2.one().x(middle);
            }
        else{
            size = minmax.two().y() - minmax.one().y();
            middle =  minmax.one().y() + size/2 + (K*size*(Math.random()*2-1));
            minmax1.two().y(middle);
            minmax2.one().y(middle);
            }
        if (level!=0){
            left = new RandomAdapter(prevBack,angle,!vertical,minmax1,level-1,notify);
            right = new RandomAdapter(prevBack,angle,!vertical,minmax2,level-1,notify);
            }
        }

    @Override
    public void onSliceLayer() {
        prevBack.onSliceLayer();
    }
    @Override
    public void onSliceLine(STLLine line) {         // Срабатывает для КОРНЯ
        STLLine xx = line.rotateXY(angleXYBack);
        onSliceLine(xx,lineVertical,0);
        }
    private void onSliceLine(STLLine line, boolean lineType, int level) {
        if (left==null){
            level &= 0x3;
            if (lineType && (level==0 || level==3) || !lineType && (level==1 || level==2)){
                prevBack.onSliceLine(line.rotateXY(angleXY));
                }
            return;
            }
        if (!lineType && line.one().x()>line.two().x() || lineType && line.one().y()>line.two().y())
            line.swap();
        if (lineType == vertical){      // тип линии и сечения совпадают - выбрать одну
            if (lineType && line.one().x()<middle || !lineType && line.one().y()<middle)
                left.onSliceLine(line,lineType,2*level);
            else
                right.onSliceLine(line,lineType,2*level+1);
                }
            else{
                if (lineType){
                    if (line.two().y() < middle)
                        left.onSliceLine(line,lineType,2*level);
                    else
                    if (line.one().y() > middle)
                        right.onSliceLine(line,lineType,2*level+1);
                    else{
                        STLLine line0 = line.clone();
                        STLLine line1 = line.clone();
                        line0.two().y(middle);
                        line1.one().y(middle);
                        left.onSliceLine(line0,lineType,2*level);
                        right.onSliceLine(line1,lineType,2*level+1);
                        }
                    }
                else{
                    if (line.two().x() < middle)
                        left.onSliceLine(line,lineType,2*level);
                    else
                    if (line.one().x() > middle)
                        right.onSliceLine(line,lineType,2*level+1);
                    else{
                        STLLine line0 = line.clone();
                        STLLine line1 = line.clone();
                        line0.two().x(middle);
                        line1.one().x(middle);
                        left.onSliceLine(line0,lineType,2*level);
                        right.onSliceLine(line1,lineType,2*level+1);
                    }
                }
                }
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

