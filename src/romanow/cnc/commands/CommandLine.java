package romanow.cnc.commands;

import romanow.cnc.controller.USBCodes;
import romanow.cnc.io.BinOutputStream;
import romanow.cnc.stl.STLLine;
import romanow.cnc.stl.STLPoint2D;
import romanow.cnc.utils.UNIException;

/**
 * Created by romanow on 04.03.2018.
 */
public class CommandLine extends PrintCommand{
    private STLLine line = new STLLine(new STLPoint2D(),new STLPoint2D());
    private boolean burn = true;
    private boolean sendAsDouble = false;
    public CommandLine(boolean mode){
        this();
        sendAsDouble = mode;
        }
    public CommandLine(){
        super(USBCodes.SetBurnLine);
        name = "Линия";
        signature = SIGN_LINE;
        }
    public void noBurn(){ burn = false; }
    public CommandLine(STLLine line0){
        this();
        line = line0;
        }
    @Override
    public void value(STLLine line0) { line=line0; }
    @Override
    public int[] toIntArray(){
        int out[] = new int[sendAsDouble ? 11 : 7];
        out[0] = code;
        out[1] = 0;
        out[2] = burn ? 0x101 : 0x01;
        if (!sendAsDouble) {
            out[3] = out[4] = out[5] = out[6];
            try {
                out[3] = BinOutputStream.doubleToQ31(line.one().x());
                out[4] = BinOutputStream.doubleToQ31(line.one().y());
                out[5] = BinOutputStream.doubleToQ31(line.two().x());
                out[6] = BinOutputStream.doubleToQ31(line.two().y());
            } catch (UNIException ee) {}
            }
        else{
            long vv = Double.doubleToLongBits(line.one().x());
            out[3]=(int)vv;
            out[4]=(int)(vv>>32);
            vv = Double.doubleToLongBits(line.one().y());
            out[5]=(int)vv;
            out[6]=(int)(vv>>32);
            vv = Double.doubleToLongBits(line.two().x());
            out[7]=(int)vv;
            out[8]=(int)(vv>>32);
            vv = Double.doubleToLongBits(line.two().y());
            out[9]=(int)vv;
            out[10]=(int)(vv>>32);
            }
        return out;
        }
    @Override
    public int[] CreateBynary() throws UNIException {
        return CreateBynary();
        }
    @Override
    public boolean canFind(){ return true; }
    public String toString(){
        return super.toString() + String.format("(%5.3f,%5.3f) (%5.3f,%5.3f)",line.one().x(),line.one().y(),line.two().x(),line.two().y());
        }
    @Override
    public String toView(){
        return super.toView() + String.format("(%5.3f,%5.3f) (%5.3f,%5.3f)",line.one().x(),line.one().y(),line.two().x(),line.two().y());
        }

}
