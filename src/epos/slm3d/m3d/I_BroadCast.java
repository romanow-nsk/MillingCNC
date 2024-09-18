package epos.slm3d.m3d;

/**
 * Created by romanow on 16.09.2018.
 */
public interface I_BroadCast{
    void sendEvent(int code,boolean on, int value, String name);
}
