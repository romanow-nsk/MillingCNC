package epos.slm3d.m3d;

import epos.slm3d.commands.Command;
import epos.slm3d.utils.UNIException;

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
