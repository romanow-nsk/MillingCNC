package romanow.cnc.usb;

/**
 * Created by romanow on 03.12.2017.
 */
/** Буфер накопления передаваемых данных (команд принтера)*/
public class M3DDataBuffer {
    private byte buf[];
    private int idx=0;
    public M3DDataBuffer(int sz){
        buf = new byte[sz];
        idx=0;
        }
    public int size(){ return idx; }
    public byte[] toBytes(){
        byte out[]= new byte[idx];
        for(int j=0;j<idx;j++)
            out[j]=buf[j];
         return out;
        }
    public byte[] flush(){
        byte out[] = toBytes();
        idx=0;
        return out;
        }
    public boolean put(int zz[]){
        if (idx+4*zz.length > buf.length)
            return false;
        for(int j=0,k=0;j<zz.length;j++){
            int vv = zz[j];
            for(int i=0;i<4;i++,k++){
                buf[idx++] = (byte)vv;
                vv >>=8;
                }
            }
        return true;
        }
    public boolean put(int vv){
        if (idx+4 > buf.length)
            return false;
        for(int i=0;i<4;i++){
            buf[idx++] = (byte)vv;
            vv >>=8;
            }
        return true;
        }
}
