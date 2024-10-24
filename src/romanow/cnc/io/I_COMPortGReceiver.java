package romanow.cnc.io;

import romanow.cnc.utils.UNIException;

/**
 * Created by romanow on 28.03.2018.
 */
public interface I_COMPortGReceiver {
    public void onError(UNIException ee);
    public void onReceive(String ss);
    public void onClose();
    }
