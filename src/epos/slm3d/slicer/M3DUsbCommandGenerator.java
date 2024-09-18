package epos.slm3d.slicer;

import epos.slm3d.commands.Command;
import epos.slm3d.commands.CommandFire;
import epos.slm3d.commands.CommandLayer;
import epos.slm3d.commands.CommandMove;
import epos.slm3d.settings.WorkSpace;
import epos.slm3d.stl.STLLine;
import epos.slm3d.stl.STLLineGroup;
import epos.slm3d.stl.STLLoop;
import epos.slm3d.usb.M3DUSBController;
import epos.slm3d.utils.I_Notify;
import epos.slm3d.utils.UNIException;

import java.util.ArrayList;

/**
 * Created by romanow on 07.12.2017.
 */
public class M3DUsbCommandGenerator extends CommandGenerator{
    I_Notify notify;
    M3DUSBController usb;
    public M3DUsbCommandGenerator(I_Notify notifyo){
        notify = notifyo;
        }
    @Override
    public void line(STLLine line) throws UNIException {
        try {
            usb.sendData(new CommandMove(line.one().x(),line.one().y()).CreateBynary());
            usb.sendData(new CommandFire(line.two().x(),line.two().y()).CreateBynary());
            } catch (UNIException ee){ throw UNIException.io(ee); }
    }
    @Override
    public void layer() throws UNIException {
        try {
            int stepM4 = WorkSpace.ws().local().control.NextLayerMovingM4Step.getVal();   
            int stepM3 = WorkSpace.ws().local().control.NextLayerMovingM3Step.getVal();            
            usb.sendData(new CommandLayer(stepM4, stepM3).CreateBynary());
            } catch (UNIException ee){ throw UNIException.io(ee); }
        }

    @Override
    public void init() throws UNIException {
        }

    @Override
    public void start() throws UNIException {
        usb = new M3DUSBController(notify);
        try {
            usb.open();
            usb.sendCMD0();
            usb.sendData(epos.slm3d.settings.WorkSpace.ws().local().createHeader());
            } catch (UNIException ee){ throw UNIException.io(ee); }
        }
    @Override
    public void end(SliceRezult rez) throws UNIException {
        }
    @Override
    public void loops(ArrayList<STLLoop> loops) throws UNIException{
        }
    @Override
    public void lines(STLLineGroup lines) {
        }
    @Override
    public void command(Command cmd) throws UNIException {
        usb.sendData(cmd.CreateBynary());
        }
    @Override
    public void close() {
        try {
            usb.close();
            } catch (UNIException e) {}
        }
    @Override
    public void cancel() throws UNIException {}    
}
