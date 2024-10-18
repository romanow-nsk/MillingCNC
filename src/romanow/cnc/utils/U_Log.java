package romanow.cnc.utils;

/**
 * Created by romanow on 12.10.2017.
 */
public class U_Log {
    private StringBuffer log = new StringBuffer();
    public  void clear() {  log = new StringBuffer(); }
    public String toString() {
        return log.toString();
        }
    public void put(String a){
        log.append(a);
        }
}
