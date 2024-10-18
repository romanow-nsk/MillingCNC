package romanow.cnc.m3d;

public interface I_PanelEvent {
    public void refresh();
    public void onEvent(int code, int par1, long par2, String par3,Object oo);
    public void shutDown();
    }
