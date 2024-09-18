package epos.slm3d.slicer;

import epos.slm3d.stl.STLLine;
import epos.slm3d.stl.STLLoop;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by romanow on 13.02.2018.
 */
public class SliceErrorLoopList extends SliceError{
    private ArrayList<STLLoop> loops;
    public SliceErrorLoopList(){super(0);}
    public SliceErrorLoopList(int mode, ArrayList<STLLoop> loops0){
        super(mode);
        loops = loops0;
        }
    public ArrayList<STLLoop> loops(){ return loops; }
    @Override
    public void setZ(double z) {
        }
    @Override
    public void drawAll(I_ErrorDraw onEvent) {
        for(STLLoop loop : loops)
            for(STLLine line : loop.lines())
                onEvent.onLine(getErrorCode(),line);
        }
    public void load(DataInputStream in) throws IOException{
        super.load(in);
        int sz = in.readInt();
        loops = new ArrayList<>();
        while(sz--!=0){
            STLLoop loop = new STLLoop();
            loop.load(in);
            loops.add(loop);
            }
        }
    public void save(DataOutputStream out) throws IOException{
        super.save(out);
        out.writeInt(loops.size());
        for(STLLoop loop : loops)
            loop.save(out);
        }
    public void shift(double xx, double yy){
        for (STLLoop loop : loops)
            loop.shift(xx,yy);
        }
}
