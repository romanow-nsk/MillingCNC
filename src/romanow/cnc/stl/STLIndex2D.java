package romanow.cnc.stl;

/**
 * Created by romanow on 06.12.2017.
 */

import romanow.cnc.Values;

/**
 * Одноуровневый индекс - таблица - не используется
 * */
public class STLIndex2D implements I_GraphIndex{
    private STLLineGroup table[]=null;
    private int N = Values.GraphIndexSize;
    private double dd;
    private boolean mode;

    public STLIndex2D(STLLineGroup src, boolean mode0) {
        dd = 2./N;
        mode = mode0;
        table = new STLLineGroup[N*N];
        for (int i=0;i<N*N;i++)
            table[i]=null;
        for (STLLine xx:src.lines())
            putToIndex(xx);
            }
    int idx(I_STLPoint2D pp){
        int i=(int)(pp.y()/dd)+N/2;
        int j=(int)(pp.x()/dd)+N/2;
        return i*N+j;
        }
    public void putToIndex(STLLine line){
        int idx = idx(mode ? line.one() : line.two());
        if (table[idx]==null)
            table[idx] = new STLLineGroup();
        table[idx].add(line);
        }
    @Override
    public STLLineGroup nearestX(I_STLPoint2D point, double diff) {
        int idx = idx(point);
        if (table[idx]!=null)
            return table[idx];
        else
            return new STLLineGroup();      // Проосто вернуть группу
        /*
        STLLineGroup out = new STLLineGroup();
        int i1=idx/N-1;
        if (i1<0) i1=0;
        int i2=idx/N+1;
        if (i2==N) i2=N-1;
        int j1=idx%N-1;
        if (j1<0) j1=0;
        int j2=idx%N+1;
        if (j2==N) j2=N-1;
        for(int i=i1;i<=i2;i++)
            for(int j=j1;j<=j2;j++){
                if (table[i*N+j]!=null)
                    out.add(table[i*N+j]);
                }
        return out;
        */
        }
    @Override
    public boolean remove(STLLine line, boolean isSwap){
        int idx = idx(mode ? line.one() : line.two());
        if (isSwap)
            idx = idx(mode ? line.two() : line.one());
        if (table[idx]==null)
            return false;
        return table[idx].lines().remove(line);
        }
}
