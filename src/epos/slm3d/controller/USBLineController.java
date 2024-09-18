package epos.slm3d.controller;

import epos.slm3d.usb.USBController;
import epos.slm3d.utils.I_Notify;
import epos.slm3d.utils.UNIException;
import epos.slm3d.utils.Utils;
import epos.slm3d.utils.Values;

/**
 * Created by romanow on 28.10.2017.
 */

/**
 * Тестовая конфигурация
 * 1. Установить LibUsbK
 * 2. Библиотека из usb4java.zip - libusb4java-1.2.1
 * 3. Vendor 0x5665, Product 0x0200 - M3D printer Epos Engineering
 * 4. C:\libusbK-dev-kit/install-filter-win - перехватываем устройство
 * 5. Main - testMouse
 * */
public class USBLineController implements USBFace{
    /** VendorId устройства */
    private final short USBVendorId=0x5665;
    /** ProductId устройства */
    private final short USBProductId=0x0200;
    /** Тайм-аут устройства */
    private final int USBTimeOut=5000;
    /** Точка передачи  */
    private final byte EndPointSEND=0x01;
    /** Точка передачи блоков данных */
    private final byte EndPointREC=(byte)0x81;
    /** Размер буфера передачи */
    private final int sendBufSize = 512;
//    private final int sendBufSize = 64;    
    private epos.slm3d.usb.USBController usb;
    private I_Notify notify=null;
    public USBLineController() { usb = new USBController(USBVendorId,USBProductId); }
    public USBLineController(I_Notify note) {
        notify = note;
        usb = new USBController(USBVendorId,USBProductId,note);
        }
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public void write(int[] data) throws UNIException {
        write(data,data.length);
        }
    @Override
        public void write(int[] data, int sz) throws UNIException {
        byte bb[]=Utils.intToBytes(data,sz);
        System.out.println("send: "+Utils.toView(bb));
        usb.writeBulk(bb,EndPointSEND,USBTimeOut);
        }

    @Override
    public int[] read() throws UNIException {
        byte xx[] = usb.bulkRead(Values.USB3BlockByteSize,EndPointREC,USBTimeOut);
        int vv[] = Utils.bytesToIntArray(xx);
        return vv;
        }
    @Override
    public void init() throws UNIException {
        usb.initController();
        usb.openDevice();
        System.out.println(usb);
        usb.claimInterface((byte)0x0);
        }
    @Override
    public void close(){
        try {
            usb.releaseInterface();
            usb.closeDevice();
            usb.closeController();
            } catch (UNIException e) {}
        }
    //----------------------------------------------------------------------------------------
    public static void main(final String[] args){
        try {
            USBLineController usb = new USBLineController();
            usb.init();
            usb.close();
            } catch (UNIException ee){
                System.out.println(ee); }
            }
    @Override
    public int blockByteSize() {
        return Values.USB3BlockByteSize;
        }

}
