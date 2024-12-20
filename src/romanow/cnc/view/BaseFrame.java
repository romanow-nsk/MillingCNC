package romanow.cnc.view;

import romanow.cnc.m3d.I_PanelEvent;
import romanow.cnc.settings.Settings;
import romanow.cnc.settings.WorkSpace;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import javax.swing.*;
import romanow.cnc.m3d.I_Important;

/**
 * Created by romanow on 16.09.2018.
 */
public abstract class BaseFrame extends JFrame implements I_Important, I_PanelEvent {

    @Getter private ArrayList<BasePanel> panels = new ArrayList();            // панели
    public boolean tryToStart(){
        return ws.tryToStart(this);
        }
    public void onClose(){
        ws.onClose(this);
        }
    public WorkSpace ws(){ return ws; }
    public Settings local(){ return ws.local(); }
    public Settings global(){ return ws.global(); }
    public void createPanels(Dimension dim){}
    public void refreshPanels(){}
    public void toFront(int mask){}
    private WorkSpace ws;
    //-------------------------------------------------------------------------------
    public BaseFrame(){
        ws = WorkSpace.ws();
        }
    public boolean isViewPanelEnable(int mode){
        return ws.isViewModeEnable(mode);
        }
    public void setViewPanel(int mode){
        ws.viewMode(mode);
        }
    public void setViewPanelEnable(int mode){
        ws.viewModeEnableOne(mode);
        }
    public void setViewPanelDisable(int mode){
        ws.viewModeDisableOne(mode);
        }

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
        srcName = srcName.replace("\\","/");
        int idx=srcName.lastIndexOf("/");
        if (idx!=-1)
            srcName = srcName.substring(idx+1);
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
        fname = fname.replace("\\","/");
        return fname;
        }
    public void sendEvent(int code, int par1, long par2, String par3,Object oo){
        for(BasePanel panel : panels)
            panel.onEvent(code,par1,par2,par3,oo);
        ws.sendEvent(this,code,par1,par2,par3,oo);
        }
    public void sendEventSynch(int code, int par1, long par2, String par3,Object oo){
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                for(BasePanel panel : panels)
                    panel.onEvent(code,par1,par2,par3,oo);
                ws.sendEvent(BaseFrame.this,code,par1,par2,par3,oo);
                }
            });
        }
    @Override
    public void onEvent(int code, int par1, long par2, String par3,Object oo){
        for(BasePanel panel : panels)
            panel.onEvent(code,par1,par2,par3,oo);
        }

}
