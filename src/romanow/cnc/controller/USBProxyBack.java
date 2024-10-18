package romanow.cnc.controller;

/**
 * Created by romanow on 12.01.2018.
 */
public class USBProxyBack implements USBBack{
    private USBBack oldBack;
    public USBProxyBack(USBBack xx) { oldBack=xx; }
    @Override
    public void onSuccess(int cmd, int[] data) { oldBack.onSuccess(cmd,data); }
    @Override
    public void onError(int cmd, int[] data) {  oldBack.onError(cmd,data); }
    @Override
    public void onFatal(int errorCode, String mes) { oldBack.onFatal(errorCode, mes); }
    }
