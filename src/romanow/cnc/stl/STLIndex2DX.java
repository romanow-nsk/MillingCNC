package romanow.cnc.stl;

import romanow.cnc.utils.UNIException;

/**
 * Created by romanow on 06.12.2017.
 */
/**
 * Графический индекс, цифра - квадрат 4x4, начиная с младшей - не используется
 * */
public class STLIndex2DX {
    private int level;
    private double x0,y0,x1,y1,dx,dy;
    private STLIndex2DX next[]=null;
    private STLLineGroup own = new STLLineGroup();

    public STLIndex2DX() {
        this(3,-1,-1,1,1);
        }
    public STLIndex2DX(int level, double x0, double y0, double x1, double y1) {
        this.level = level;
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        dx=(x1-x0)/4;
        dy=(y1-y0)/4;
        next = new STLIndex2DX[16];
        if (level==0)
            return;
        for (int i=0;i<16;i++)
            next[i]=null;
            }
    public int createIndex(I_STLPoint2D point) throws UNIException{
        if (level==0)
            return 0;
        double x = point.x();
        double y = point.y();
        if (x<x0 || x>x1 || y<y0 || y> y1)
            throw new UNIException(UNIException.bug,String.format("Координаты ??? (%5.3f,%5.3f) (%5.3f,%5.3f) ",x0,y0,x1,y1)+point);
        int ix = (int)((point.x()-x0)/dx);
        int iy = (int)((point.y()-y0)/dy);
        int idx = ix*4+iy;
        if (next[idx]==null)
            return idx;
        return next[idx].createIndex(point) << 4 | idx;
        }
    public int insertToIndex(STLLine line) throws UNIException{
        if (level==0){
            own.add(line);
            return 0;
            }
        int idx1 = createIndex(line.one()) & 0x0F;
        int idx2 = createIndex(line.two()) & 0x0F;
        if (idx1 != idx2){     // Квадраты разные - в текущий
            own.add(line);
            return 0;
            }
        else{
            if (next[idx1]==null){          // Создать динамически
                double x=x0+(idx1/4)*dx;
                double y=y0+(idx1%4)*dy;
                next[idx1]= new STLIndex2DX(level-1,x,y,x+dx,y+dx);
                }
            return next[idx1].insertToIndex(line)  << 4 | idx1;
            }
        }

    public boolean remove(STLLine line) throws UNIException{
        if (own.remove(line))
            return true;
        int idx1 = createIndex(line.one()) & 0x0F;
        int idx2 = createIndex(line.two()) & 0x0F;
        if (idx1 != idx2){     // Квадраты разные - в текущий
            return false;
            }
        if (next==null || next[idx1]==null)
            return false;
        return next[idx1].remove(line);
        }
    /** итератор перебора по индексу до первого подходящего без сравнения по расстоянию */
    public STLLine firstThat(STLPoint2D point, I_LineFirstThat back) throws UNIException{
        for(STLLine xx : own.lines()){
            if (back.test(xx))
                return xx;
            }
        int idx1 = createIndex(point) & 0x0F;
        if (next==null || next[idx1]==null)
            return null;
        return next[idx1].firstThat(point,back);
        }

    public static void main(String argv[]) throws UNIException {
        STLIndex2DX idx = new STLIndex2DX(3,-1,-1,1,1);
        STLPoint2D p1 = new STLPoint2D(0.7,0.555);
        STLPoint2D p2 = new STLPoint2D(0.5,0.6);
        System.out.println(Integer.toHexString(idx.createIndex(p1)));
        System.out.println(Integer.toHexString(idx.createIndex(p2)));
        System.out.println(Integer.toHexString(idx.insertToIndex(new STLLine(p1,p2))));
    }
}
