package epos.slm3d.m3d;

import epos.slm3d.commands.Command;
import epos.slm3d.commands.CommandPoint;
import epos.slm3d.slicer.VisualCommandGenerator;
import epos.slm3d.stl.STLLine;
import epos.slm3d.stl.STLPoint2D;
import epos.slm3d.utils.I_Notify;
import epos.slm3d.utils.UNIException;
import epos.slm3d.utils.Values;

import java.awt.*;

/**
 * Created by romanow on 06.12.2017.
 */
public class M3DVisio {
    private I_Notify notify;
    private ViewAdapter back;
    private STLPoint2D old=new STLPoint2D();
    CommandPoint cur = new CommandPoint(0);
    public M3DVisio(I_Notify note, ViewAdapter back0) {
        notify = note;
        back = back0;
        }
    //----------------------------------------------------------------------------------------------
    public void show(final FileBinInputStream bb, ViewAdapter view){
        VisualCommandGenerator gen = new VisualCommandGenerator(view);
        final Graphics gg = back.FLD.getGraphics();
        try{
            bb.procFile(new OnM3DCommand(){
            @Override
            public boolean onCommand(final Command cmd) {
                back.onStepLine();
                if (cmd.isImportant())
                    notify.log(""+cmd.toView());
                else
                    notify.info(""+cmd.toView());
                if (view.isFinish())
                    return true;
                java.awt.EventQueue.invokeLater(
                    ()->{
                        if (cmd.code()==M3DValues.cmdLayer){
                            try {
                                gen.layer();
                                } catch (UNIException e) {}
                            return;
                            }
                        if (cmd instanceof CommandPoint){
                            cur = (CommandPoint) cmd;
                            if (cmd.code() == M3DValues.cmdMove2 || cmd.code() == M3DValues.cmdMove)
                                old = new STLPoint2D(cur.x(),cur.y());
                            if (cmd.code() == M3DValues.cmdFire)
                                try {
                                    gen.line(new STLLine(old,new STLPoint2D(cur.x(),cur.y())));
                                    } catch (UNIException e) {}
                            double mas = back.mas;
                            }
                        });
                return view.isFinish();
                }
            @Override
            public void onFinish() {
                notify.info("Просмотр закончен");
                try { bb.close(); } catch(Exception ee){}
                }
            @Override
            public void onHeader(int hd[]) {
                }
            });
        } catch(UNIException ee){
            notify.notify(Values.fatal,ee.getMessage());
        }
    }
    //---------------------------------------------------------------------------------------------------------
}
