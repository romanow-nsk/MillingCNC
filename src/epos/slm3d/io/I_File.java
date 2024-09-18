package epos.slm3d.io;

import epos.slm3d.utils.UNIException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by romanow on 14.02.2018.
 */
public interface I_File {
    public void load(DataInputStream in) throws IOException;
    public void save(DataOutputStream out) throws IOException;
}
