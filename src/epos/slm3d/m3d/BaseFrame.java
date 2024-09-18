package epos.slm3d.m3d;

import epos.slm3d.settings.Settings;
import epos.slm3d.settings.WorkSpace;
import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.*;

/**
 * Created by romanow on 16.09.2018.
 */
public abstract class BaseFrame extends JFrame implements I_Important{
    public boolean tryToStart(){
        return WorkSpace.ws().tryToStart(this);
        }
    public void onClose(){
        WorkSpace.ws().onClose(this);
        }
    public void sendEvent(int code,boolean on, int value, String name){
        WorkSpace.ws().sendEvent(code,on,value,name);
        }
    @Override
    synchronized public void onEvent(int code,boolean on, int value, String name) {
        //System.out.println(getClass().getSimpleName()+" "+code+" "+on+" "+value+" "+name);
        }
    public WorkSpace ws(){ return WorkSpace.ws(); }
    public Settings local(){ return WorkSpace.ws().local(); }
    public Settings global(){ return WorkSpace.ws().global(); }
    public String getInputFileName(String title, final String defName,boolean defDir){
        FileDialog dlg=new FileDialog(this,title,FileDialog.LOAD);
        if (defDir){
            String dir = ws().defaultDir();
            dlg.setDirectory(dir);
            }
        dlg.setFile("*."+defName);
        dlg.show();
        String fname=dlg.getDirectory();
        if (fname==null) return null;
        fname+="/"+dlg.getFile();
        return fname;
        }

    public String getOutputFileName(String title, final String defName, String srcName){
        FileDialog dlg=new FileDialog(this,title,FileDialog.SAVE);
        dlg.setFile(srcName);
        dlg.setFilenameFilter(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("."+defName);
            }
        });            
        dlg.show();
        String fname=dlg.getDirectory();
        if (fname==null) return null;
        fname+=dlg.getFile();
        if (!fname.endsWith("."+defName))
            fname+="."+defName;
        return fname;
        }
}
