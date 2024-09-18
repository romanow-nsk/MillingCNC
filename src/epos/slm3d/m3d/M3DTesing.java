package epos.slm3d.m3d;

import epos.slm3d.usb.M3DUSBController;
import epos.slm3d.usb.USBController;
import epos.slm3d.utils.I_Notify;
import epos.slm3d.utils.UNIException;
import epos.slm3d.utils.Utils;
import epos.slm3d.utils.Values;

import java.io.FileNotFoundException;

/**
 * Created by romanow on 15.12.2017.
 */
public class M3DTesing {
    private I_Notify notify;
    public M3DTesing(I_Notify note){
        notify = note;
        }
    private boolean finish = false;
    //-----------------------------------------------------------------
    public void testMassStorage(){
        try {
            short vendorID = (short) 0x8564;
            short productID = (short) 0x1000;
            USBController cc = new USBController(vendorID, productID);
            cc.initController();
            cc.openDevice();
            notify.info( cc.toString());
            cc.claimInterface((byte) 0x0);
            byte ww1[] = {0x55, 0x53, 0x42, 0x43, 0x68, (byte) 0x84, (byte) 0xCE, (byte) 0x8B, 0, 0, 0, 0, 0, 0, 0x06,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            byte wwa[] = {0x55, 0x53, 0x42, 0x53, 0x68, (byte) 0x84, (byte) 0xCE, (byte) 0x8B, 0, 0, 0, 0, 0};
            int vv = cc.writeBulk(ww1, (byte) 0x02, 5000);
            notify.info("Передано " + vv);
            byte dd[] = new byte[0];
            dd = cc.bulkRead(100, (byte) 0x81, 5000);
            String out = "Принято "+dd.length+":";
            for (int i = 0; i < dd.length; i++)
                out+= String.format(" %2x",dd[i]);
            notify.info(out);
            cc.releaseInterface();
            cc.closeDevice();
            cc.closeController();
        } catch (UNIException ee){ notify.notify(Values.error,ee.getMessage());}
    }
    private boolean stop;
    public void testMassStorageAsync(){
        try {
            short vendorID = (short) 0x8564;
            short productID = (short) 0x1000;
            USBController cc = new USBController(vendorID, productID);
            cc.initController();
            cc.openDevice();
            notify.info( cc.toString());
            cc.claimInterface((byte) 0x0);
            byte ww1[] = {0x55, 0x53, 0x42, 0x43, 0x68, (byte) 0x84, (byte) 0xCE, (byte) 0x8B, 0, 0, 0, 0, 0, 0, 0x06,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            byte wwa[] = {0x55, 0x53, 0x42, 0x53, 0x68, (byte) 0x84, (byte) 0xCE, (byte) 0x8B, 0, 0, 0, 0, 0};
            stop = false;
            new Thread(()->{
                while(!finish){
                    byte dd[] = new byte[0];
                    try {
                        dd = cc.bulkRead(100, (byte) 0x81, 5000);
                    } catch (UNIException e) {
                        if (!stop)
                            notify.notify(Values.error,e.getMessage());
                        return;
                    }
                    String out = "Принято "+dd.length+":";
                    for (int i = 0; i < dd.length; i++)
                        out+= String.format(" %2x",dd[i]);
                    notify.info(out);
                }
            }).start();;
            for (int i=0;i<10;i++){
                try {
                    Thread.sleep(1000);
                    int vv = cc.writeBulk(ww1, (byte) 0x02, 5000);
                    notify.info("Передано " + vv);
                } catch (InterruptedException e) {}

            }
            finish = stop;
            cc.releaseInterface();
            cc.closeDevice();
            cc.closeController();
        } catch (UNIException ee){ notify.notify(Values.error,ee.getMessage());}
    }
    //------------------------------------------------------------------
    public void testMouse(){
        try {
            short vendorID = (short) 0x275d;    // МЫШКА
            short productID = (short) 0x0ba6;
            USBController cc = new USBController(vendorID, productID, notify);
            cc.initController();
            cc.openDevice();
            notify.info( cc.toString());
            cc.claimInterface((byte) 0x0);
            for (int j = 0; j < 100; j++) {
                byte dd[] = cc.bulkRead(5, (byte) 0x81, 0);
            }
            cc.releaseInterface();
            cc.closeDevice();
            cc.closeController();
            notify.info( "Тест закончен");
        } catch(UNIException ee){ notify.notify(Values.fatal,""+ee);}
    }
    //------------------------------------------------------------------------------------------------------------------
    public void copyDirectToUsb(String fname){
        finish = false;
        M3DFileBinInputStream bb;
        try {
            bb = new M3DFileBinInputStream(fname);
        } catch(FileNotFoundException ee){
            notify.notify(Values.fatal,ee.getMessage());
            return;
        }
        M3DUSBController usb = new M3DUSBController(notify);
        try {
            usb.open();
            } catch(UNIException ee){
                notify.notify(Values.error,ee.toString());
                return;
            }
        try {
            int xx[] = bb.readBuf(M3DValues.UsbBufSize0);
            usb.sendCMD0();
            usb.sendData(xx);
            while(true){
                xx = bb.readBuf(M3DValues.UsbBufSize1);
                if (xx.length==M3DValues.UsbBufSize1)
                    usb.sendData(xx);
                else{
                    if(xx.length!=0){
                        usb.sendCMD2();
                        usb.sendDataLast(xx);
                        }
                    usb.close();
                    return;
                    }
                }
            } catch(UNIException ee){
                notify.notify(Values.fatal,ee.getMessage());
                try { usb.close(); } catch (UNIException e) {}
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public void wievDirectToUsb(String fname){
        finish = false;
        M3DFileBinInputStream bb;
        try {
            bb = new M3DFileBinInputStream(fname);
        } catch(FileNotFoundException ee){
            notify.notify(Values.fatal,ee.getMessage());
            return;
            }
        try {
            int addr=0;
            int xx[] = bb.readBuf(M3DValues.UsbBufSize0/4);
            notify.info( Utils.toView(addr,xx));
            addr+=M3DValues.UsbBufSize0;
            while(true){
                xx = bb.readBuf(M3DValues.UsbBufSize1/4);
                if (xx.length==M3DValues.UsbBufSize1/4){
                    notify.info(Utils.toView(addr,xx));
                    addr+=M3DValues.UsbBufSize1;
                    }
                else{
                    if(xx.length!=0){
                        notify.info(Utils.toView(addr,xx));
                        }
                    return;
                }
            }
        } catch(UNIException ee){
            notify.notify(Values.fatal,ee.getMessage());
        }
    }

}
