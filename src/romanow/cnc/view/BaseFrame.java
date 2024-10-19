package romanow.cnc.view;

import romanow.cnc.m3d.I_PanelEvent;
import romanow.cnc.settings.Settings;
import romanow.cnc.settings.WorkSpace;
import lombok.Getter;
import lombok.Setter;

import java.awt.FileDialog;
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
        return WorkSpace.ws().tryToStart(this);
        }
    public void onClose(){
        WorkSpace.ws().onClose(this);
        }
    public WorkSpace ws(){ return WorkSpace.ws(); }
    public Settings local(){ return WorkSpace.ws().local(); }
    public Settings global(){ return WorkSpace.ws().global(); }
    public void createPanels(){}
    public void refreshPanels(){}
    public void toFront(int mask){}
    //-------------------------------------------------------------------------------

    public boolean isViewPanelEnable(int mode){
        return WorkSpace.ws().isViewModeEnable(mode);
        }
    public void setViewPanel(int mode){
        WorkSpace.ws().viewMode(mode);
        refreshPanels();
        }
    public void setViewPanelEnable(int mode){
        WorkSpace.ws().viewModeEnableOne(mode);
        refreshPanels();
        }
    public void setViewPanelDisable(int mode){
        WorkSpace.ws().viewModeDisableOne(mode);
        refreshPanels();
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
    public void sendEvent(int code, int par1, long par2, String par3,Object oo){
        for(BasePanel panel : panels)
            panel.onEvent(code,par1,par2,par3,oo);
        WorkSpace.ws().sendEvent(this,code,par1,par2,par3,oo);
        }
    @Override
    public void onEvent(int code, int par1, long par2, String par3,Object oo){
        for(BasePanel panel : panels)
            panel.onEvent(code,par1,par2,par3,oo);
        }

}
