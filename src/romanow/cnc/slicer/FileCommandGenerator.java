package romanow.cnc.slicer;

import romanow.cnc.commands.Command;
import romanow.cnc.commands.CommandFire;
import romanow.cnc.commands.CommandLayer;
import romanow.cnc.commands.CommandMove;
import romanow.cnc.settings.WorkSpace;
import romanow.cnc.stl.STLLine;
import romanow.cnc.stl.STLLineGroup;
import romanow.cnc.stl.STLLoop;
import romanow.cnc.utils.UNIException;
import romanow.cnc.m3d.M3DFileBinOutputStream;
import romanow.cnc.m3d.M3DValues;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by romanow on 07.12.2017.
 */
public class FileCommandGenerator extends CommandGenerator{
    private M3DFileBinOutputStream out;
    public FileCommandGenerator(M3DFileBinOutputStream out0){
        out=out0;
        }
    @Override
    public void line(STLLine line) throws UNIException {
        try {
            Command cmd = new CommandMove(line.one().x(),line.one().y());
            addAddr(cmd.byteSize());
            out.write(cmd.CreateBynary());
            cmd = new CommandFire(line.two().x(),line.two().y());
            addAddr(cmd.byteSize());
            out.write(cmd.CreateBynary());
            } catch (IOException ee){ throw UNIException.io(ee); }
        }
    @Override
    public void layer() throws UNIException {
        //try {
            //int stepM4 = ws.local().control.NextLayerMovingM4Step.getVal();
            //int stepM3 = ws.local().control.NextLayerMovingM3Step.getVal();
            //ommand cmd = new CommandLayer(stepM4,stepM3);
            //addAddr(cmd.byteSize());
            //out.write(cmd.CreateBynary());
            //} catch (IOException ee){ throw UNIException.io(ee); }
        }
    @Override
    public void init() throws UNIException {
        }
    @Override
    public void start() throws UNIException {
        try {
            out.writeHeader();
            addAddr(M3DValues.headerSize);
            } catch (UNIException ee){ throw UNIException.io(ee); }
        }
    @Override
    public void end(SliceRezult rez) throws UNIException {
        }

    @Override
    public void loops(ArrayList<STLLoop> loops) {
        }
    @Override
    public void lines(STLLineGroup lines) {
        }

    @Override
    public void command(Command cmd) throws UNIException {
        try {
            addAddr(cmd.byteSize());
            out.write(cmd.CreateBynary());
            } catch(IOException ee){ throw UNIException.io(ee);}
        }

    @Override
    public void close() {
        try {
            out.close();
        } catch (IOException ee){}
    }

    @Override
    public void cancel() throws UNIException {}
}
