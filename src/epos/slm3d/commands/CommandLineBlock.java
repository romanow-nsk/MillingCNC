package epos.slm3d.commands;

import epos.slm3d.controller.USBCodes;
import epos.slm3d.io.BinOutputStream;
import epos.slm3d.stl.STLLine;
import epos.slm3d.utils.UNIException;

import java.util.ArrayList;

/**
 * Created by romanow on 04.03.2018.
 */
public class CommandLineBlock extends PrintCommand{
    private ArrayList<STLLine> lines = new ArrayList();
    private boolean sendAsDouble = false;
    public CommandLineBlock(boolean mode){
        this();
        sendAsDouble = mode;
        }
    public CommandLineBlock(){
        super(USBCodes.SetBurnLineBlock);
        name = "Блок линий";
        signature = Command.SIGN_LINE;
        }
    public CommandLineBlock(STLLine line){
        this();
        lines.add(line);
        }
    public int size(){ return lines.size(); }
    public void add(STLLine line){ lines.add(line); }
    public void clear(){ lines.clear(); }
    @Override
    public void value(STLLine line0) { lines.add(line0); }
    @Override
    public int[] toIntArray(){
        int out[] = new int[2+lines.size()*(sendAsDouble ? 8 : 4)];
        out[0] = code;
        out[1] = lines.size();
        for(int i=0;i<lines.size();i++){
            STLLine line = lines.get(i);
            if (!sendAsDouble) {
                try {
                    out[2+i*4] = BinOutputStream.doubleToQ31(line.one().x());
                    out[3+i*4] = BinOutputStream.doubleToQ31(line.one().y());
                    out[4+i*4] = BinOutputStream.doubleToQ31(line.two().x());
                    out[5+i*4] = BinOutputStream.doubleToQ31(line.two().y());
                    } catch (UNIException ee) {}
                }
            else{
                long vv = Double.doubleToLongBits(line.one().x());
                out[2+i*8]=(int)vv;
                out[3+i*8]=(int)(vv>>32);
                vv = Double.doubleToLongBits(line.one().y());
                out[4+i*8]=(int)vv;
                out[5+i*8]=(int)(vv>>32);
                vv = Double.doubleToLongBits(line.two().x());
                out[6+i*8]=(int)vv;
                out[7+i*8]=(int)(vv>>32);
                vv = Double.doubleToLongBits(line.two().y());
                out[8+i*8]=(int)vv;
                out[9+i*8]=(int)(vv>>32);
                }
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
        String out = super.toString()+"\n";
        for(int i=0;i<lines.size();i++){
            STLLine line = lines.get(i);
            out += String.format("(%5.3f,%5.3f) (%5.3f,%5.3f)\n",line.one().x(),line.one().y(),line.two().x(),line.two().y());
            }
        return out;
        }
    @Override
    public String toView(){
        String out = super.toView()+"\n";
        for(int i=0;i<lines.size();i++){
            STLLine line = lines.get(i);
            out += String.format("(%5.3f,%5.3f) (%5.3f,%5.3f)\n",line.one().x(),line.one().y(),line.two().x(),line.two().y());
            }
        return out;
        }

}
