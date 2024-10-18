package romanow.cnc.slicer;

import romanow.cnc.commands.Command;
import romanow.cnc.m3d.ViewAdapter;
import romanow.cnc.stl.STLLine;
import romanow.cnc.stl.STLLineGroup;
import romanow.cnc.stl.STLLoop;
import romanow.cnc.stl.STLPoint2D;
import romanow.cnc.utils.UNIException;

import java.util.ArrayList;

/**
 * Created by romanow on 07.12.2017.
 */
public class SliceDataGenerator extends CommandGenerator{
    private ViewAdapter back;
    private int lCount=0;
    private SliceData data = null;
    private SliceLayer current = new SliceLayer(1);         // С номером 1
    private STLPoint2D p0 = new STLPoint2D();
    public SliceDataGenerator(SliceData data0, ViewAdapter back0){
        data = data0;
        back=back0;
        }
    //--------------------------------------------------------------------------------------------------------
    @Override
    public void line(STLLine line) throws UNIException {
        current.addSegment(line);
        }
    @Override
    public void layerFinished(SliceRezult rez, double z, double angle){
        current.result(rez);
        current.z(z);
        current.angle(angle);
        current.convertPointsToFloat();
        data.addLayer(current);
        lCount++;
        current = new SliceLayer(lCount+1);
        }
    @Override
    public void layer() throws UNIException {
        }
    @Override
    public void init() throws UNIException {
        }
    @Override
    public void start() throws UNIException {
        lCount=0;
        }
    @Override
    public void end(SliceRezult rez) throws UNIException {
        data.result(rez);
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
        current.loops(loops);
        }
    @Override
    public void lines(STLLineGroup lines) {
        current.lines(lines);
        }
    @Override
    public void onError(SliceError error){
        current.addError(error);
        }
    @Override
    public void cancel() throws UNIException {}    
}
