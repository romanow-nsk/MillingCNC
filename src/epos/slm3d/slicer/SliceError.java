package epos.slm3d.slicer;

import epos.slm3d.io.I_File;
import epos.slm3d.stl.STLReferedPoint;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by romanow on 13.02.2018.
 */
public abstract class SliceError implements I_File{
    /** тип ошибки */
    private int errorCode=0;
    public SliceError(int errorCode0) {
        errorCode = errorCode0;
        }
    public int getErrorCode() {
        return errorCode;
        }
    public abstract void setZ(double z);
    /** визуализация данных ошибки в передачаемый интерфейс I_ErrorDraw*/
    public abstract void drawAll(I_ErrorDraw onEvent);
    public abstract void shift(double xx, double yy);
    public void load(DataInputStream in) throws IOException {
        errorCode = in.readInt();
        }
    public void save(DataOutputStream out) throws IOException{
        out.writeInt(errorCode);
        }

}
