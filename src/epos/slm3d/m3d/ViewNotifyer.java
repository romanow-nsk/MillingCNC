package epos.slm3d.m3d;

import epos.slm3d.utils.I_Notify;
import epos.slm3d.utils.Values;

import javax.swing.*;
import java.awt.*;

/**
 * Created by romanow on 02.12.2017.
 */
public class ViewNotifyer implements I_Notify{
    private TextArea LOG;
    private JProgressBar progress;
    private int level= Values.info;
    private boolean logSusupendState=false;
    private StringBuffer savedLog = new StringBuffer();
    public void logSuspendState(boolean state){
        logSusupendState = state;
        if (!logSusupendState){
            LOG.append(savedLog.toString());
            savedLog = new StringBuffer();
            }
        }
    public ViewNotifyer(TextArea log0,JProgressBar progress0){
        progress = progress0;
        LOG = log0;
        }
    public void setLevel(int lv){
        level = lv;
        }
    public void info(String mes){
        notify(Values.info,mes);
        }   
    public void log(String mes){
        notify(Values.important,mes);
        }   
    @Override
    public synchronized void notify(final int level0, final String mes) {
        if (level0 < level)
            return;
        java.awt.EventQueue.invokeLater(
                ()->{
                    if (logSusupendState)
                        savedLog.append(mes+"\n");
                    else
                        LOG.append(mes+"\n");
                    });
        }

    @Override
    public synchronized void setProgress(int proc) {
        java.awt.EventQueue.invokeLater(
                ()->{
                    progress.setValue(proc);
                });
    }
}
