package romanow.cnc.controller;

import romanow.cnc.settings.WorkSpace;
import romanow.cnc.utils.UNIException;
import romanow.cnc.utils.Utils;
import romanow.cnc.Values;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * Created by romanow on 22.12.2017.
 */
public class USBUDPEmulator implements USBFace{
    private boolean connected=false;
    private DatagramSocket socket=null;
    ByteArrayOutputStream bo;
    public boolean connected(){ return connected; }
    public USBUDPEmulator()  {
        }
    @Override
    public void init() throws UNIException{
        try {
            socket= new DatagramSocket(USBCodes.USBEmulatorInetPort+1);
            socket.setSoTimeout(Values.UdpTimeOut*1000);
            connected=true;
            } catch (IOException e) { throw UNIException.io(e); }
        }

    @Override
    public void close(){
        socket.close();
        }

    @Override
    public int blockByteSize() {
        return Values.USB3BlockByteSize;
        }

    @Override
    public void write(int[] data) throws UNIException {
        write(data,data.length);
        }
    @Override
    public void write(int[] data, int sz) throws UNIException {
        /*
        try {
            byte[] buffer = Utils.intToBytes(data,sz);
            DatagramPacket packet = new DatagramPacket(buffer, sz*4,
                    new InetSocketAddress(ws.global().global.ControllerIP.getVal(), USBCodes.USBEmulatorInetPort));
            socket.send(packet);
            } catch (IOException e) { throw UNIException.io(e); }
             */
        }

    @Override
    public int[] read() throws UNIException {
        try {
            byte[] buffer = new byte[512];                   // буфер для принятия данных
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length); // создаем абстрактный пакет
            socket.receive(packet);                          // принимаем данные, блокирующий вызов.
            //System.out.println(packet.getSocketAddress());   // выводи адрес, откуда получили пакет
            buffer = packet.getData();                       // получаем в буфер байты, из принятого пакета
            //System.out.println(buffer.length);
            return Utils.bytesToIntArray(buffer);
            } catch (IOException e) { throw UNIException.io(e); }
        }


    public static void main(String argv[]) throws UNIException{
        USBBack back = new USBBack(){
            @Override
            public void onSuccess(int cmd, int[] data) {
                System.out.println("++++"+data[0]);
            }
            @Override
            public void onError(int code, int[] data) { System.out.println("-----"+code + ":"+ data[0]); }
            @Override
            public void onFatal(int code, String mes) { System.out.println("-----"+code+":"+mes); }
            };
        USBUDPEmulator emu = new USBUDPEmulator();
        while(!emu.connected());
        USBProtocol ctrl = new USBProtocol(emu);
        ctrl.init();
        ctrl.HardReset(back);
        ctrl.offLine(back);
        ctrl.HardReset(back);
        }
}
