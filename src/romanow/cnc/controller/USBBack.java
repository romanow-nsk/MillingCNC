package romanow.cnc.controller;

/**
 * Created by romanow on 21.12.2017.
 */
public interface USBBack {
    public void onSuccess(int cmd, int data[]);
    public void onError(int cmd, int data[]);           // Код ошибки в data[0]
    public void onFatal(int errorCode, String message);
    }