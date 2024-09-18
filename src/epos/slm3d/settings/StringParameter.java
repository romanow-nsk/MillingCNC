package epos.slm3d.settings;

import epos.slm3d.io.I_File;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by romanow on 01.12.2017.
 */
public class StringParameter implements I_File{
    private String val="192.168.1.98";
    public StringParameter(String val0){
        val=val0;
    }
    public String getVal() {
        return val;
    }
    public void setVal(String val) {
        this.val = val;
    }
    @Override
    public void load(DataInputStream in) throws IOException {
        val = in.readUTF();
        }
    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeUTF(val);
    }
}
