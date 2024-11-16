package romanow.cnc.io;

/**
 * Created by romanow on 28.03.2018.
 */

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import romanow.cnc.Values;
import romanow.cnc.console.I_COMPortReceiver;
import romanow.cnc.console.LaserPowerData;
import romanow.cnc.settings.WorkSpace;
import romanow.cnc.utils.Pair;
import romanow.cnc.utils.UNIException;
import romanow.cnc.utils.Utils;

import java.util.ArrayList;
import java.util.Date;

public class COMPortGDriver {
    private SerialPort serialPort=null;
    private String answer=null;
    private String data="";
    private String port="COM4";
    private int baudRate=SerialPort.BAUDRATE_115200;
    private int timeout=5;
    private I_COMPortGReceiver back=null;
    private long writeStamp = 0;                // !-0 - ждет ответа
    //------------------------------------------------------------------------------------------------------------------
    public String open(String port0,int baudRate0,int timeOut0,I_COMPortGReceiver back0){
        close();
        port = port0;
        baudRate = baudRate0;
        timeout = timeOut0;
        back = back0;
        return reOpen();
        }
    synchronized private String reOpen(){
        if (serialPort!=null)
            return port+": отсутствует драйвер";
        serialPort = new SerialPort(port);
        try {
            serialPort.openPort();
            serialPort.setParams(baudRate,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            serialPort.addEventListener(listen, SerialPort.MASK_RXCHAR);
            System.out.println(""+port+": порт открыт");
            } catch (SerialPortException ex) {
                return onError(ex);
                }
            return null;
            }
    public COMPortGDriver() {
        serialPort=null;
        }
    private String onError(Exception ex){
        UNIException uu = UNIException.io(ex);
        back.onError(uu);
        return ""+port+": "+uu.toString();
        }
    public Pair<String,String> write(String mes,int delay){
        try {
            System.out.println(""+port+": "+mes);
            serialPort.writeString(mes+"\n");           // КОНЕЦ СТРОКИ
            writeStamp = System.currentTimeMillis();
            } catch (SerialPortException ex) {
                return new Pair<>(onError(ex),null);
                }
        if (delay==0)
            return new Pair<>(null,"ok");
        else
            return getAnswer();
        }

    public void writeNoWait(String mes){
        try {
            System.out.println(""+port+": "+mes);
            serialPort.writeString(mes+"\n");           // КОНЕЦ СТРОКИ
            } catch (SerialPortException ex) {
                back.onError(UNIException.io(ex));
               }
            }

    public Pair<String,String> getAnswer(){
        synchronized (this){
            if (writeStamp==0)
                return new Pair<>(""+port+": не ждет ответа",null);
            }
        while (System.currentTimeMillis()-writeStamp < timeout*1000){
                synchronized (this){
                    if (answer!=null){
                        Pair<String,String> pp = new Pair<>(null,answer);
                        answer=null;
                        writeStamp=0;
                        return pp;
                        }
                    }
                try {
                    Thread.yield();
                    Thread.sleep(100);
                    } catch (InterruptedException e) {}
            }
        synchronized (this){
            writeStamp=0;
            }
        //------------------ Тайм-аут игнорируем
        System.out.println(">>>"+port+": тайм-аут");
        return new Pair<>(null,"ok");
        //return new Pair<>(""+port+": тайм-аут "+timeout+" сек, нет ответа",null);
        }
    synchronized public String close() {
        try {
            if (serialPort!=null)
                serialPort.closePort();
            } catch (SerialPortException ex) {
                return onError(ex);
                }
            serialPort=null;
        return null;
        }
    private void log(String ss){
        WorkSpace.ws().notify(Values.important, "SerialIO: "+ss);
        }
    private SerialPortEventListener listen = new SerialPortEventListener() {
        @Override
        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    data += serialPort.readString(event.getEventValue());
                    int idx=data.indexOf('\r');
                    if (idx==-1){
                        return;
                        }
                    else{
                        synchronized (COMPortGDriver.this){
                            answer = data.substring(0,idx);
                            System.out.println(">>>"+port+": "+answer);
                            data = "";
                           if (writeStamp==0){
                               final String ss = answer;
                               answer=null;
                               java.awt.EventQueue.invokeLater(()->{   // В потоке GUI
                                   back.onReceive(ss);
                                   System.out.println(ss);
                                   });
                               }
                           }
                        }
                   } catch (SerialPortException ex) {
                        onError(UNIException.io(ex));
                        }
                }
            }
        };
    public static void main(String argv[]){
        COMPortGDriver port = new COMPortGDriver();
        I_COMPortGReceiver rec = new I_COMPortGReceiver() {
            @Override
            public void onReceive(String mes) {
                System.out.println(mes);
                }
            @Override
            public void onError(UNIException ex) {
                System.out.println(ex.toString());
                }
            @Override
            public void onClose() {
                System.out.println("close");
                }
            @Override
            public void setOKTimeOut(int delyInMS) {}
        };
        port.open("COM4",115200,10,rec);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Pair<String,String> res = port.write("G00",1);
                if(res.o1!=null)
                    System.out.println(res.o1);
                else
                    System.out.println(res.o2);
                }
            }).start();
        try {
            Thread.sleep(20000);
            } catch (InterruptedException e) {}
        port.close();
    }
}