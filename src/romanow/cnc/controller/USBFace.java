package romanow.cnc.controller;

import romanow.cnc.utils.UNIException;

/**
 * Created by romanow on 21.12.2017.
 */
public interface USBFace {
    public void write(int data[]) throws UNIException;
    public void write(int data[], int sz) throws UNIException;
    public int []read() throws UNIException;
    public void init()  throws UNIException;
    public void close();
    public int blockByteSize();
}
