package romanow.cnc.slicer;

import romanow.cnc.commands.Command;
import romanow.cnc.m3d.ViewAdapter;
import romanow.cnc.utils.UNIException;
import romanow.cnc.stl.*;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by romanow on 07.12.2017.
 */
public class VisualCommandGenerator extends CommandGenerator{
    private ViewAdapter back;
    private Graphics gg;
    private STLPoint2D p0 = new STLPoint2D();
    public VisualCommandGenerator(ViewAdapter back0){
        back=back0;
        gg = back.preview().getGraphics();
        }
    //--------------------------------------------------------------------------------------------------------
    private void drawLine(Color color, STLLine vv, Graphics gg){
        double mas = back.mas;
        int sz = back.sz();
        gg.setColor(color);
        gg.drawLine((int)(vv.one().x()*mas+sz), (int)(-vv.one().y()*mas+sz), (int)(vv.two().x()*mas+sz), (int)(-vv.two().y()*mas+sz));
        }
    private void drawPoint(Color color, I_STLPoint2D vv, Graphics gg){
        double mas = back.mas;
        int sz = back.sz();
        gg.setColor(color);
        gg.drawOval((int)(vv.x()*mas+sz)-3, (int)(-vv.y()*mas+sz)-3,5,5);
        }
    @Override
    public void line(STLLine line) throws UNIException {
        drawLine(Color.black,line,gg);
        drawPoint(Color.red,line.one(),gg);
        back.onStepLine();
        }
    @Override
    public void layer() throws UNIException {
        gg.setColor(Color.white);
        gg.fillRect(0, 0, back.sz()*2-1, back.sz()*2-1);
        gg.setColor(Color.black);
        back.onStepLayer();
        }

    @Override
    public void init() throws UNIException {
        }

    @Override
    public void start() throws UNIException {
        }
    @Override
    public void end(SliceRezult rez) throws UNIException {
        }
    @Override
    public void command(Command cmd) throws UNIException {
        throw UNIException.user("Не поддерживается");
        }

    @Override
    public void close() {
        }

    @Override
    public void loops(ArrayList<STLLoop> loops) {
        //------- Рисование по контурам ----------------------------
        double mas = back.mas;
        int sz = back.sz();
        int ii=0;
        for(STLLoop one : loops){
            for (STLLine vv : one.lines()){
                drawLine(ii%2==0 ? Color.green : Color.CYAN,vv,gg);
                if (back.onStepLine());
                //gg.drawLine((int)(vv.one().x()*mas+sz), (int)(-vv.one().y()*mas+sz), (int)(vv.two().x()*mas+sz), (int)(-vv.two().y()*mas+sz));
               }
            ii++;
            if (one.isRepaired()){
                STLLine xx = one.lines().get(one.lines().size()-1);
                drawPoint(Color.magenta,xx.one(),gg);
                drawPoint(Color.magenta,xx.two(),gg);
                drawLine(Color.magenta,xx,gg);
                }
            }
        }

    @Override
    public void lines(STLLineGroup lines) {
        //-----Рисование по отрезкам -----------------------------------
        double mas = back.mas;
        int sz = back.sz();
        for (STLLine vv : lines.lines()){
            drawLine(Color.black,vv,gg);
            }
        }
    @Override
    public void onError(SliceError error){
        if (error instanceof SliceErrorPointList){
            ArrayList<STLReferedPoint> points = ((SliceErrorPointList)error).points();
            for(STLReferedPoint pp : points){
                drawPoint(Color.red,pp,gg);
                drawLine(Color.magenta,pp.reference(),gg);
                }
            for(int i=0;i<points.size()-1;i++){
                drawLine(Color.red,new STLLine(points.get(i),points.get(i+1)),gg);
                }
            }
        }
    @Override
    public void cancel() throws UNIException {}
}
