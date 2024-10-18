package romanow.cnc.usb;

import romanow.cnc.utils.I_Notify;
import romanow.cnc.utils.UNIException;
import romanow.cnc.utils.Utils;
import org.usb4java.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * Created by romanow on 28.10.2017.
 */
/**
 * Тестовая конфигурация
 * 1. Установить LibUsbK
 * 2. Библиотека из usb4java.zip - libusb4java-1.2.1
 * 3. Vendor 275d, Product 0ba6 - тестовая оптическая мышь
 * 4. C:\libusbK-dev-kit/wizard - генерируем драйвер
 * 5. Устанавливаем драйвер - перехватывает устройство
 * 6. Main - testMouse
 * 7. Убрать - снести пакет
 * */
public class USBController {
    private short vendorID=0;
    private short productID=0;
    private int busNum=0;
    private int deviceNum=0;
    private int faceNum=0;
    private Device usbDevice=null;
    private DeviceHandle handle=null;
    private Context context=null;
    private I_Notify notify=null;
    public USBController(short vendorID, short productID) {
        this.vendorID = vendorID;
        this.productID = productID;
        }
    public USBController(short vendorID, short productID, I_Notify not) {
        this(vendorID,productID);
        notify = not;
    }
    public boolean isValid(){
        return usbDevice!=null;
        }
    public boolean isOpen(){
        return handle!=null;
    }
    private void setParams() throws UNIException {
        deviceNum = LibUsb.getDeviceAddress(usbDevice);
        busNum = LibUsb.getBusNumber(usbDevice);
        DeviceDescriptor desc = new DeviceDescriptor();
        LibUsb.getDeviceDescriptor(usbDevice,desc);
        System.out.print(desc.dump());
        int  n = desc.bNumConfigurations();
        for (int i=0; i<n;i++){
            ConfigDescriptor descriptor = new ConfigDescriptor();
            int result = LibUsb.getConfigDescriptor(usbDevice, (byte)i, descriptor);
            if (result < 0)
                throw UNIException.usb("Ошибка получения конфигурации:"+LibUsb.errorName(result));
            System.out.println(descriptor.dump());
            LibUsb.freeConfigDescriptor(descriptor);
            }
        }
    public void closeController() throws UNIException {
        try {
            LibUsb.exit(context);
            } catch(Exception e1){ throw UNIException.fatal(e1); }
            usbDevice = null;
        }
    public void initController() throws UNIException{
        // Create the libusb context
        usbDevice = null;
        context = new Context();
        // Initialize the libusb context
        int result = -1;
        try {
            result = LibUsb.init(context);
            } catch(Throwable e1){
                throw UNIException.io(e1.getCause()!=null ? e1.getCause().toString() : e1.getMessage());
                }
        if (result < 0) {
            throw UNIException.usb("Ошибка инициализации libusb:"+LibUsb.errorName(result));
            }
        // Read the USB device list
        DeviceList list = new DeviceList();
        result = LibUsb.getDeviceList(context, list);
        if (result < 0) {
            throw UNIException.usb("Ошибка получения списка устройств:"+LibUsb.errorName(result));
            }
        LibUsb.setDebug(context,LibUsb.LOG_LEVEL_INFO);
        try {
            // Iterate over all devices and list them
            for (Device device: list){
                DeviceDescriptor desc = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(device, desc);
                if (result < 0)
                    continue;
                    //{
                    //throw new LibUsbException(
                    //        "Unable to read device descriptor", result);
                    //}
                if (vendorID==desc.idVendor() && productID==desc.idProduct()){
                    usbDevice = device;
                    setParams();
                    break;
                    }
                }
            if (usbDevice==null)
                throw UNIException.usb(String.format("Устройство не найдено: Vendor %04x, Product %04x",vendorID, productID));
            }
        finally { LibUsb.freeDeviceList(list, true); }
        }
    public void openDevice() throws UNIException{
        if (handle!=null)
            return;
        if (usbDevice==null)
            throw UNIException.usb("Устройство отсутствует или не инициализировано");
        //---------------- Вариант 1
        //handle = new DeviceHandle();
        //LibUsb.open(usbDevice,handle);
        //---------------- Вариант 2
        handle = LibUsb.openDeviceWithVidPid(context, vendorID,productID);
        if (handle == null)
            throw UNIException.usb("Устройство не открыто");
        System.out.println(String.format("handle=%8x",handle.getPointer()));
        //LibUsb.resetDevice(handle);
        }
    public void closeDevice() throws UNIException{
        try {
            if (handle!=null)
                LibUsb.close(handle);
            } catch(Exception e1){
                handle=null; throw UNIException.fatal(e1); }
        handle = null;
        }
    public void releaseInterface() throws UNIException {
        if (handle==null)
            throw UNIException.usb("Устройство не открыто");
        int result = 0;
        try{
            result = LibUsb.releaseInterface(handle, faceNum);
        } catch(Exception e1){ throw UNIException.fatal(e1); }
        if (result != LibUsb.SUCCESS){
            throw UNIException.usb("Ошибка освобождения интерфейса:"+LibUsb.errorName(result));
            }
        }
    public void claimInterface(byte face) throws UNIException {
        if (handle==null)
            throw UNIException.usb("Устройство не открыто");
        int result = 0;
        /*
        try{
            result = LibUsb.kernelDriverActive(handle, face);
            } catch(Exception e1){ throw UNIException.fatal(e1); }
        if (result < 0){
            throw UNIException.usb("Unable to check kernel driver active:"+LibUsb.errorName(result));
            }
        // Detach kernel driver from interface 0 and 1. This can fail if
        // kernel is not attached to the device or operating system
        // doesn't support this operation. These cases are ignored here.
        */
        try{
            result = LibUsb.detachKernelDriver(handle, 1);
            } catch(Exception e1){ throw UNIException.fatal(e1); }
        if (result != LibUsb.SUCCESS &&
                result != LibUsb.ERROR_NOT_SUPPORTED &&
                result != LibUsb.ERROR_NOT_FOUND) {
            throw UNIException.usb("Unable to detach kernel driver active:"+LibUsb.errorName(result));        }
        try{
            result = LibUsb.claimInterface(handle, face);
            } catch(Exception e1){ throw UNIException.fatal(e1); }
        if (result != LibUsb.SUCCESS){
            throw UNIException.usb("Ошибка запроса интерфейса:"+LibUsb.errorName(result));
            }
        }
    public int writeBulk(byte[] data, byte endPoint, int timeOut) throws UNIException {
        if (handle==null)
            throw UNIException.usb("Устройство не открыто");
        ByteBuffer buffer = BufferUtils.allocateByteBuffer(data.length);
        buffer.put(data);
        IntBuffer transferred = BufferUtils.allocateIntBuffer();
        int result = 0;
        try{
            result = LibUsb.bulkTransfer(handle, endPoint, buffer,transferred, timeOut);
            } catch(Exception e1){ throw UNIException.fatal(e1); }
        if (result != LibUsb.SUCCESS){
            throw UNIException.usb("Ошибка передачи:"+LibUsb.errorName(result));
            }
        if (notify!=null)
            notify.info("Передано point="+Integer.toHexString(endPoint & 0x0FF)+": "+ Utils.toView(data));
        return transferred.get();
        }
    public byte[] bulkRead(int size, byte endPoint,int timeOut) throws UNIException {
        if (handle==null)
            throw UNIException.usb("Устройство не открыто");
        ByteBuffer buffer = BufferUtils.allocateByteBuffer(size).order(ByteOrder.LITTLE_ENDIAN);
        IntBuffer transferred = BufferUtils.allocateIntBuffer();
        int result=0;
        try{
            result = LibUsb.bulkTransfer(handle, endPoint, buffer, transferred, timeOut);
            } catch(Exception e1){ throw UNIException.fatal(e1); }
        if (result != LibUsb.SUCCESS){
            throw UNIException.usb("Ошибка приема:"+LibUsb.errorName(result));
            }
        int rsize =  transferred.get();
        buffer.rewind();
        byte[] data= new byte[rsize];
        buffer.rewind();
        for (int i=0; i<rsize; i++){
            data[i]= buffer.get();
            }
        System.out.println("rec: "+Utils.toView(data));
        if (notify!=null)
            notify.info("Принято point="+Integer.toHexString(endPoint & 0x0FF)+": "+ Utils.toView(data));
        return data;
        }
    public byte[] interruptRead(int size, byte endPoint,int timeOut) throws UNIException {
        if (handle==null)
            throw UNIException.usb("Устройство не открыто");
        ByteBuffer buffer = BufferUtils.allocateByteBuffer(size).order(ByteOrder.LITTLE_ENDIAN);
        IntBuffer transferred = BufferUtils.allocateIntBuffer();
        int result=0;
        try{
            result = LibUsb.interruptTransfer(handle, endPoint, buffer, transferred, timeOut);
        } catch(Exception e1){ throw UNIException.fatal(e1); }
        if (result!=LibUsb.SUCCESS){
            throw UNIException.usb("Ошибка приема:"+LibUsb.errorName(result));
            }
        int vv = buffer.position();
        System.out.println("size="+vv);
        if (vv==0)
            return new byte[0];
        else
            return buffer.array();
    }
    public byte[] control(int size, byte endPoint, int timeOut, int rid) throws UNIException {
        ByteBuffer buffer = ByteBuffer.allocateDirect(size);
        buffer.rewind();
        // handle - A handle for the device to communicate with. СТР.169
        // bmRequestType - The request type field for the setup packet. 0xA1 или 0x21
        // bRequest - The request field for the setup packet. GET_REPORT=0x01
        // wValue - The value field for the setup packet. GEPORT_ID
        // wIndex - The index field for the setup packet. номер интерфейса
        // data - A suitably-sized data buffer for either input or output (depending on direction bits within bmRequestType).
        // timeout - Timeout (in millseconds) that this function should wait before giving up due to no response being received. For an unlimited timeout, use value 0.
        int transfered = LibUsb.controlTransfer(handle,
                (byte)0xA1,(byte)0x01,(short)(rid>>8), (short)rid, buffer, timeOut); // 300 - IO, 1 - PARAM
        if (transfered <0)
            throw UNIException.usb("Ошибка приема:"+LibUsb.errorName(transfered));
        if (transfered != size)
            throw UNIException.usb("Not all data was received from device:"+transfered);
        byte[] data= new byte[size];
        buffer.rewind();
        for (int i=0; i<size; i++){
            data[i]= buffer.get();
            }
        return data;
        }
    public short getVendorID() {
        return vendorID;
    }
    public short getProductID() {
        return productID;
        }
    public int getBusNum() {
        return busNum;
        }
    public int getDeviceNum() {
        return deviceNum;
        }
    public String toString(){
        return String.format("Bus %03d, Device %03d: Vendor %04x, Product %04x%n",
                busNum, deviceNum, vendorID, productID);
        }
    public static void testKB() throws UNIException{
        short vendorID=(short)0x0518;
        short productID=(short)0x0001;
        USBController cc = new USBController(vendorID,productID);
        cc.initController();
        cc.openDevice();
        System.out.println(cc);
        cc.claimInterface((byte)0x0);
        for(int j=0;j<100;j++) {
            byte dd[] = cc.bulkRead(5, (byte) 0x81, 0);
            for (int i = 0; i < dd.length; i++) {
                System.out.print(String.format("%2x ", dd[i]));
            }
            System.out.println();
        }
        cc.releaseInterface();
        cc.closeDevice();
        cc.closeController();
        }

    public static void testMouse() throws UNIException{
        I_Notify not = new I_Notify() {
            @Override
            public void notify(int level, String mes) {
                System.out.println(mes);
                }
            @Override
            public void setProgress(int proc) {}
            @Override
            public void info(String mes) {System.out.println(mes); }
            @Override
            public void log(String mes)  {System.out.println(mes); }
            };
        short vendorID=(short)0x275d;    // МЫШКА
        short productID=(short)0x0ba6;
        USBController cc = new USBController(vendorID,productID,not);
        cc.initController();
        cc.openDevice();
        System.out.println(cc);
        cc.claimInterface((byte)0x0);
        for(int j=0;j<100;j++) {
            byte dd[] = cc.bulkRead(5, (byte) 0x81, 0);
            }
        cc.releaseInterface();
        cc.closeDevice();
        cc.closeController();
        }
    public static void testMassStorage() throws UNIException{
        short vendorID=(short)0x8564;
        short productID=(short)0x1000;
        USBController cc = new USBController(vendorID,productID);
        cc.initController();
        cc.openDevice();
        System.out.println(cc);
        cc.claimInterface((byte)0x0);
        byte ww1[]={0x55,0x53,0x42,0x43,0x68,(byte)0x84,(byte)0xCE,(byte)0x8B,0,0,0,0,0,0,0x06,
                0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        byte wwa[]={0x55,0x53,0x42,0x53,0x68,(byte)0x84,(byte)0xCE,(byte)0x8B,0,0,0,0,0};

        byte ww[]={0x55,0x53,0x42,0x43,0x20, (byte)0xDB,(byte)0xCF,(byte)0x87,0,0,0,0,0,0,0x06,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        int vv = cc.writeBulk(ww1,(byte)0x02,5000);
        System.out.println("send="+vv);
        byte dd[]=new byte[0];
        dd = cc.bulkRead(100, (byte) 0x81, 5000);
        if (dd.length!=wwa.length)
            System.out.println("Ответ ????");
        else{
        for(int i=0;i<dd.length;i++)
            if (dd[i]!=wwa[i])
                System.out.println("Ответ ????");
            }
        cc.releaseInterface();
        cc.closeDevice();
        cc.closeController();
    }
    public static void main(final String[] args){
        try {
            testMassStorage();
            //testMouse();
            //testKB();
            } catch (UNIException ee){
                System.out.println(ee); }
            }
    public static void main0(final String[] args){
        //short vendorID=(short)0x09da;    // МЫШКА
        //short productID=(short)0x0a;
        //short vendorID=(short)0x045e;    // МЫШКА
        //short productID=(short)0x0040;
        //short vendorID=(short)0x1004;    // LG первый
        //short productID=(short)0x618e;
        //short vendorID=(short)0x275D;    // Оптическая мышь
        //short productID=(short)0x0BA6;
        try {
            short vendorID=(short)0x275D;    // Оптическая мышь
            short productID=(short)0x0BA6;
            USBController cc = new USBController(vendorID,productID);
            cc.initController();
            cc.openDevice();
            System.out.println(cc);
            cc.claimInterface((byte)1);
            //byte bb[]={1,2,3,4,5};
            //byte ww[]={0x55,0x53,0x42,0x43,0x20, (byte)0xDB,(byte)0xCF,(byte)0x87,0,0,0,0,0,0,0x06,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
            //int vv = cc.writeBulk(ww,(byte)0x02,5000);
            //System.out.println("send="+vv);
            byte dd[]=new byte[0];
            dd= cc.bulkRead(5,(byte)0x81,5000);
            //dd = cc.interruptRead(8, (byte) 0x81, 0);
            cc.releaseInterface();
            cc.closeDevice();
            cc.closeController();
            } catch (UNIException ee){ System.out.println(ee); }
        }
}
