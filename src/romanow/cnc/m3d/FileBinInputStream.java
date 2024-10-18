package romanow.cnc.m3d;

import romanow.cnc.commands.Command;
import romanow.cnc.utils.UNIException;

import java.io.IOException;

/**
 * Created by romanow on 03.01.2018.
 */
public interface FileBinInputStream {
    public int readInt() throws IOException;
    public Command getNext() throws UNIException;
    public void procFile(OnM3DCommand back) throws UNIException;
    public void close() throws IOException;
    }
