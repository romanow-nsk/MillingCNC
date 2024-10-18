package romanow.cnc.m3d;

/**
 * Created by romanow on 02.12.2017.
 */
public class M3DValues {
    /** Размер начального буфера */
    public static int UsbBufSize0=0x14C;
    /** Размер промежуточного буфера */
    public static int UsbBufSize1=0x30;
    /** Размер заголовка */
    public static int headerSize=0x148;
    /** Команда смены слоя */
    public static int cmdLayer=0x00008000;
    /** Команда перемещения луча */
    public static int cmdMove2=0x0;
    /** Команда перемещения луча */
    public static int cmdMove=0x1;
    /** Команда прожига  */
    public static int cmdFire=0x101;
    /** Команда не определена  */
    public static int cmdEmpty=-1;
}
