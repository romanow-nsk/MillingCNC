package epos.slm3d.stl;

import java.util.ArrayList;

/**
 * Created by romanow on 05.12.2017.
 */
public class STLLineIndex implements I_GraphIndex{
    /** исходный вектор линий  */
    private STLLineGroup src = new STLLineGroup();
    /** индексный массив,отсортированный по координате */
    private ArrayList<STLLine> index=new ArrayList<>();
    /** вид сортировки - 0-x[0],1-x[1], 2-[y] */
    private int mode=0;
    /** Текущая размерность индекса - при удалении уменьшается*/
    public int size(){ return src.size(); }
    /** Удаление линии из индекса */
    public boolean remove(STLLine xx,boolean isSwap){
        if (!index.remove(xx)){
            System.out.println("6: не найден в индексе "+src.size()+": "+xx);
            return false;
            }
        return true;
        }
    public ArrayList<STLLine> lines(){ return src.lines(); }
    /** создание индекса с типом упорядоченности mode0 */
    public STLLineIndex(STLLineGroup src0, int mode0){
        mode = mode0;
        src = src0;
        sort(mode);
        }
    public STLLine get(int idx){ return index.get(idx); }
    /** поиск ближайшей точки с использованием индексирования по X */
    public STLLineGroup nearestX(I_STLPoint2D point,double diff){     // Поиск только по координате X
        STLLineGroup out = new STLLineGroup();
        if (src.size()==0)
            return out;
        ArrayList<STLLine> zz = out.lines();
        double vv = mode < 2 ? point.x() : point.y();
        int idx = binary(vv,diff);
        for(int ii=idx; ii >=0; ii--){
            STLLine ln = index.get(ii);
            double v1 = ln.value(mode);
            if (Math.abs(vv-v1 )>diff)
                break;
            zz.add(ln);             // Добавить найденный
            }
        for(int ii=idx; ii < index.size(); ii++) {
            STLLine ln = index.get(ii);
            double v1 = ln.value(mode);
            if (Math.abs(vv - v1) > diff)
                break;
            zz.add(ln);             // Добавить найденный
            }
        return out;
        }
    /** итератор перебора по индексу до первого подходящего */
    public STLLine firstThat(I_STLPoint2D point, double diff, I_LineFirstThat back){
        if (index.size()==0)
            return null;
        double vv = mode < 2 ? point.x() : point.y();
        int idx = binary(vv,diff);
        for(int ii=idx; ii >=0; ii--){
            STLLine ln = index.get(ii);
            double v1 = ln.value(mode);
            if (Math.abs(vv-v1 )>diff)
                break;
            if (back.test(ln))
                return ln;
            }
        for(int ii=idx; ii < index.size(); ii++) {
            STLLine ln = index.get(ii);
            double v1 = ln.value(mode);
            if (Math.abs(vv - v1) > diff)
                break;
            if (back.test(ln))
                return ln;
            }
        return null;
        }
    /** итератор перебора по индексу до первого подходящего */
    public void forEach(I_STLPoint2D point, double diff, I_LineFirstThat back){
        if (index.size()==0)
            return;
        int idx = binary(point.x(),diff);
        for(int ii=idx; ii >=0; ii--){
            STLLine ln = index.get(ii);
            double v1 = ln.value(mode);
            if (Math.abs(point.x()-v1 )>diff)
                break;
            back.test(ln);
            }
        for(int ii=idx; ii < index.size(); ii++) {
            STLLine ln = index.get(ii);
            double v1 = ln.value(mode);
            if (Math.abs(point.x() - v1) > diff)
                break;
            back.test(ln);
            }
        }
    //------------------------------------------------------------------------------------------------------------------
    /** создание индекса - быстрая сортировка */
    public void sort(int mode0) {
        mode = mode0;
        STLLine tmp[] = new STLLine[lines().size()];
        for(int i=0;i<tmp.length;i++)
            tmp[i]=src.get(i);
        sort(tmp,0,tmp.length-1);
        index = new ArrayList<>();
        for (STLLine xx : tmp)
            index.add(xx);
        //ArrayList<STLLine> lines = src.lines();
        //lines.clear();
        //for(STLLine ll : index){
        //    lines.add(ll);
        //    }
        }
    /** рекурсивная  сортировка диапазона индекса */
    private void sort(STLLine index[],int a, int b){
        int i,j,modeX;
        if (a>=b) return;                   // Размер части =0
        for (i=a, j=b, modeX=1; i < j; ){    // Сокращение слева или справа
            double v1=index[i].value(mode);
            double v2=index[j].value(mode);
            if (v1 > v2) {                  // Очередной не на своем месте
                STLLine c = index[i];
                index[i] = index[j];
                index[j] = c;               // Перестановка медианы с концевым
                modeX = -modeX;               // элементом со сменой сокращаемого конца
            }
            if (modeX>0) j--; else i++;
        }
        sort(index,a,i-1); sort(index,i+1,b);
    }   // рекурсия для частей БЕЗ медианы
    /** Двоичный поиск по индексу */
    public int binary(double x,double diff){// Возвращает индекс ближайшего
        int a,b,m=0;                        // Левая, правая границы и
        for(a=0,b=size()-1; a <= b;) {       // середина поиск любого  интервала 1
            m = (a + b)/2;                  // середина интервала
            STLLine gg = index.get(m);
            double vv = gg.value(mode);
            if (Math.abs(vv-x) <diff)        // Значение найдено -
                return m;                   // вернуть индекс найденного
            if (x < vv)
                b = m-1;                     // Выбрать левую половину
            else
                a = m+1;                      // Выбрать правую половину
        }
        //if (m>=size()) m=size()-1;
        //if (m<0) m=0;
        return m;                           // Ближайшее по несовпадению
    }                                   // Значение не найдено

}
