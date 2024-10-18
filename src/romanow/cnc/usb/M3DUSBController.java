package romanow.cnc.usb;

import romanow.cnc.utils.I_Notify;
import romanow.cnc.utils.UNIException;
import romanow.cnc.utils.Utils;

/**
 * Created by romanow on 28.10.2017.
 */

/**
 * Тестовая конфигурация
 * 1. Установить LibUsbK
 * 2. Библиотека из usb4java.zip - libusb4java-1.2.1
 * 3. Vendor 275d, Product 0ba6 - тестовая оптическая мышь
 * 4. C:\libusbK-dev-kit/install-filter-win - перехватываем устройство
 * 5. Main - testMouse
 * */
public class M3DUSBController {
    /** VendorId устройства */
    private final short M3DVendorId=0x4149;
    /** ProductId устройства */
    private final short M3DProductId=0x4355;
    /** Тайм-аут устройства */
    private final int M3DTimeOut=5000;
    /** Точка передачи команд */
    private final byte EndPointCMDSEND=0x01;
    /** Точка ответа на команды */
    private final byte EndPointDATASEND=0x03;
    /** Точка передачи блоков данных */
    private final byte EndPointCMDREC=(byte)0x81;
    /** Точка ответа на блоки данных */
    private final byte EndPointDATAACK=(byte)0x82;
    /** Передача блока данных  - [1] - счетчик длины в байтах*/
    private final int SENDDATA[] = {0x010007,0};
    /** Подтверждение приема команды */
    private final int ACK = 0x03;
    private final int CMD1 = 0x01;
    private final int CMD3 = 0x06;
    private final int CMD2[] = { 0x020010, 0x01, 0x111000};
    private final int ACK1_1[] = {0x06, 0x32CB, 0x32C8, 0x32C8 };
    private final int ACK2_1 = 0x50000001;
    private final int ACK2_2 = 0x40000003;
    private final int ACK2_3 = 0x30000007;
    private final int ACK2_4 = 0x2000000a;
    /** Размер буфера передачи */
    private final int sendBufSize = 512;
    /** Размер буфера передачи - накопления  */
    private M3DDataBuffer sendBuffer;
    private USBController usb;
    private int dataPacketCount=0;
    private I_Notify notify=null;
    public M3DUSBController() { usb = new USBController(M3DVendorId,M3DProductId); }
    public M3DUSBController(I_Notify note) {
        notify = note;
        usb = new USBController(M3DVendorId,M3DProductId,note);
        }
    //------------------------------------------------------------------------------------------------------------------
    private boolean waitForAnswer(int ack, byte endPoint) throws UNIException {
        byte xx[] = usb.bulkRead(4,endPoint,M3DTimeOut);
        int vv = Utils.bytesToInt(xx);
        if (vv!=ack){
            System.out.println(String.format("Принято: %8x Ждем: %8x",vv, ack));
            }
        return vv == ack;
        }
    private boolean waitForAnswer(int ack[], byte endPoint) throws UNIException {
        byte xx[] = usb.bulkRead(4*ack.length,endPoint,M3DTimeOut);
        System.out.println("rec: "+Utils.toView(xx));
        byte vv[] = Utils.intToBytes(ack);
        if (xx.length!=vv.length)
            return false;
        for(int i=0;i<vv.length;i++)
            if (vv[i]!=xx[i])
                return false;
        return true;
    }
    public boolean sendCMD0() throws UNIException{
        sendCommand(CMD1);
        return sendCMD2();
        }
    public boolean sendCMD2() throws UNIException{
        sendCommand(CMD2);
        return waitForAnswer(ACK1_1,EndPointCMDREC);
    }
    public boolean sendCMD3() throws UNIException{
        return sendCommandWithAck(CMD3,ACK);
        }
    public boolean sendCommandWithAck(int cmd,int ack) throws UNIException{
        byte bb[]=Utils.intToBytes(cmd);
        System.out.println("send4: "+Utils.toView(bb));
        usb.writeBulk(bb,EndPointCMDSEND,M3DTimeOut);
        return waitForAnswer(ack,EndPointCMDREC);
        }
    public void sendCommand(int cmd) throws UNIException{
        byte bb[]=Utils.intToBytes(cmd);
        System.out.println("send2: "+Utils.toView(bb));
        usb.writeBulk(bb,EndPointCMDSEND,M3DTimeOut);
        }
    public void sendCommand(int cmd[]) throws UNIException{
        byte bb[]=Utils.intToBytes(cmd);
        System.out.println("send3: "+Utils.toView(bb));
        usb.writeBulk(bb,EndPointCMDSEND,M3DTimeOut);
        }
    public void sendData(int data[]) throws UNIException{
        sendData(Utils.intToBytes(data));
        }
    public void sendDataLast(int data[]) throws UNIException{
        sendDataLast(Utils.intToBytes(data));
    }
    /** Протокол передачи блока данных */
    public void sendDataLast(byte data[]) throws UNIException{
        SENDDATA[1]=data.length;
        byte bb[] = Utils.intToBytes(SENDDATA);
        System.out.println("send1: "+Utils.toView(bb));
        usb.writeBulk(bb,EndPointCMDSEND,M3DTimeOut);
        if (!waitForAnswer(ACK,EndPointCMDREC)) {
            //throw UNIException.usb("Не дождался команды "+Integer.toHexString(ACK));
            }
        System.out.println("send2: "+Utils.toView(data));
        usb.writeBulk(data,EndPointDATASEND,M3DTimeOut);
        if (!waitForAnswer(ACK2_1,EndPointDATAACK)) {
            //throw UNIException.usb("Не дождался команды " + Integer.toHexString(ACK2_1));
            }
        if (!waitForAnswer(ACK2_2,EndPointDATAACK)) {
            //throw UNIException.usb("Не дождался команды "+Integer.toHexString(ACK2_2));
            }
        if (!waitForAnswer(ACK2_3,EndPointDATAACK)){
            //throw UNIException.usb("Не дождался команды "+Integer.toHexString(ACK2_3));
            }
        }
    public void sendData(byte data[]) throws UNIException{
        sendData(data);         // Рекурсия ???????
        sendCMD3();
        }
    /** Промежуточное накопление команд принтера в буфере данных */
    public void sendDataBuffered(int data[]) throws UNIException{
        if (sendBuffer.put(data))
            return;
        dataFlush();
        sendDataBuffered(data);
        }
    /** Вывести накопленные данные и очистить буфер */
    public void dataFlush() throws UNIException{
        if (sendBuffer.size()==0)
            return;
        sendData(sendBuffer.flush());
        }
    public void open() throws UNIException{
        usb.initController();
        usb.openDevice();
        System.out.println(usb);
        usb.claimInterface((byte)0x0);
        dataPacketCount=0;
        }
    public void close() throws UNIException{
        usb.releaseInterface();
        usb.closeDevice();
        usb.closeController();
        }
    //----------------------------------------------------------------------------------------
    public static void main(final String[] args){
        try {
            M3DUSBController usb = new M3DUSBController();
            usb.open();
            boolean bb = usb.sendCMD0();
            System.out.println(bb);
            usb.close();
            } catch (UNIException ee){
                System.out.println(ee); }
            }
}
