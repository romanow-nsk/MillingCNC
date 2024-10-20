package romanow.cnc.console;

/**
 * Created by romanow on 28.03.2018.
 */
import romanow.cnc.settings.WorkSpace;
import romanow.cnc.utils.Utils;
import romanow.cnc.Values;
import java.util.ArrayList;
import java.util.Date;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class COMPortDriver {
    private SerialPort serialPort=null;
    private String answer="";
    private String data="";
    private String lastCmd="";
    private ArrayList<I_COMPortReceiver> back=new ArrayList();
    private int cmdCount=0;
    private LaserPowerData lpd= new LaserPowerData();
    public  LaserPowerData laserPowerData(){ return lpd; }

    public void laserPoverCycleOnOff(boolean on){
        if (on){
            lpd.sum=0;
            lpd.count=0;
            lpd.time=new Date().getTime();
            lpd.cycle=on;
            write("ROP");
            }
        if (lpd.cycle && !on){
            lpd.time = new Date().getTime()- lpd.time;
            lpd.cycle=on;
            write("STA");
            }
        }
    synchronized public void connect(I_COMPortReceiver bb){
        if (back.contains(bb))
            return;
        back.add(bb);
        }
    synchronized public void disconnect(I_COMPortReceiver bb){
        back.remove(bb);
        }
    private void onOther(Exception ex){
        for (I_COMPortReceiver bb : back)
            bb.onOther("Лазер: "+ex.toString());         
        }
    private void onOther(String ss){
        for (I_COMPortReceiver bb : back)
            bb.onOther(ss);         
        }
    synchronized public void write(String mes){
        try {
            reOpen();
            System.out.println("cmd="+mes);
            lastCmd=mes;
            serialPort.writeString(mes+"\n");           // КОНЕЦ СТРОКИ
            if (lpd.cycle)
                return;
            cmdCount++;
            Utils.runAfterDelay(2, ()->{
                synchronized(COMPortDriver.this){
                    if (lpd.cycle)
                        return;
                    cmdCount--;
                    if (cmdCount<=0){
                        cmdCount=0;
                        try {
                            if (serialPort!=null)
                                serialPort.closePort();
                            } catch (SerialPortException ex) { 
                                onOther(ex); 
                                }
                            System.out.println("Порт закрыт");
                            serialPort=null;
                            }
                        }
                });
            } catch (SerialPortException ex) { 
                onOther(ex); 
                } 
        }
    synchronized public void getTemperature(){
        write("RCT");
        }
    synchronized public void getStateWord(){
        write("STA");
        }
    synchronized public void getStaticData(){
        write("RNC");
        //write("ROP");
        //-------------- Для импульсного режима
        //write("RPP");
        write("RCS");
        write("RPRR");
        write("RPW");      
        //-------------- Серийный номер
        //write("RFV");      
        }
    synchronized public void setVolume(double proc){
        write("SDC "+(int)proc);
        }
    synchronized public void close() {
        try {
            if (serialPort!=null)
                serialPort.closePort();
            } catch (SerialPortException ex) {
                onOther(ex); 
                }
            serialPort=null;
        }
    private void log(String ss){
        WorkSpace.ws().notify(Values.important, "Лазер: "+ss);
        }
    private String answers[]={"SDC:","RCT:","STA:","RMEC:","RNC:","ROP:","RPP:","RCS:","RPRR:","RPW:","SPRR:","SPW:",
        "EEABC","DEABC","ABF","ABN","DEC","EEC","ELE","DLE","EMON","EMOFF",
        "LFP","UFP","EPM","DPM","EGM","DGM","EMOD","DMOD","RERR"
        };
    void procAnswer(String data){
        int k=0;
        String xx = data;
        for(k=0;k<answers.length && !data.startsWith(answers[k]);k++);
        if (k==answers.length){
            onOther("Ответ лазера: "+lastCmd+"->"+data); 
            return;
            }
        xx = xx.substring(answers[k].length());
        if (!answers[k].endsWith(":"))
            return;
        try {
            //WorkSpace.ws().notify("Ответ лазера "+data);
            double vv = 0;
            if (xx.indexOf("Off")==-1)
                vv = Double.parseDouble(xx.trim());
            switch(k){
                case 0: for (I_COMPortReceiver bb : back) bb.onVolume(vv); break;
                case 1: for (I_COMPortReceiver bb : back) bb.onTemperature(vv); break;
                case 2: for (I_COMPortReceiver bb : back) bb.onState((int)Long.parseLong(xx.substring(1))); break;
                case 3: for (I_COMPortReceiver bb : back) bb.onErrorCode(Integer.parseInt(xx)); break; 
                case 4: log("Минимальный ток накачки"+String.format("%4.1f",vv)+"%"); break;  
                case 5: 
                        if (lpd.cycle){
                            if (vv!=0){
                                lpd.count++;
                                lpd.sum+=vv;
                                //WorkSpace.ws().notify(String.format("cnt=%d sum=%6.2f time=%d energy=%6.4f",lpd.count, lpd.sum,lpd.time(), lpd.energy()));
                                }
                            write("ROP");
                            }                            
                        for (I_COMPortReceiver bb : back) bb.onPower(vv); 
                        break;                  
                case 6: log("Пиковая мощность "+String.format("%4.0f",vv)+" ватт"); break; 
                case 7: for (I_COMPortReceiver bb : back) bb.onVolume(vv); break;
                case 8: for (I_COMPortReceiver bb : back) bb.onHZ(vv); break;
                case 9: for (I_COMPortReceiver bb : back) bb.onMS(vv); break;   
                case 10: for (I_COMPortReceiver bb : back) bb.onHZ(vv); break;
                case 11: for (I_COMPortReceiver bb : back) bb.onMS(vv); break;                  
                default:
                    onOther("Ответ лазера: "+lastCmd+"->"+data); break;
                    }
                } catch(Exception ee){
                    onOther("Ответ лазера: "+lastCmd+"->"+data+" "+ee.toString());                        
                    }                    
                }
    private SerialPortEventListener listen = new SerialPortEventListener() {
                    @Override
                    public void serialEvent(SerialPortEvent event) {
                        if (event.isRXCHAR() && event.getEventValue() > 0) {
                            try {
                                data += serialPort.readString(event.getEventValue());
                                while (true){
                                    int idx=data.indexOf('\r');
                                    if (idx==-1){
                                        return;
                                        }
                                    else{
                                        answer = data.substring(0,idx);
                                        data = data.substring(idx+1);
                                        final String ss = answer;
                                        java.awt.EventQueue.invokeLater(()->{   // В потоке GUI
                                            System.out.println(ss);
                                            procAnswer(ss);
                                            });
                                        }
                                    }
                            } catch (SerialPortException ex) {
                                onOther(ex);
                            }
                        }
                    }
                };
    synchronized private void reOpen(){
        if (serialPort!=null)
            return;
        cmdCount=0;
        String port = WorkSpace.ws().global().mashine.DeviceName.getVal()+WorkSpace.ws().global().mashine.DeviceNum.getVal();
        System.out.println("Порт открыт "+port);
        
        serialPort = new SerialPort(port);
            try {
                serialPort.openPort();
                serialPort.setParams(SerialPort.BAUDRATE_57600,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);
                serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
                serialPort.addEventListener(listen, SerialPort.MASK_RXCHAR);
                } catch (SerialPortException ex) {
                    onOther(ex);
                    }
        }
    public COMPortDriver() {
        serialPort=null;
        }
    public static void main(String argv[]){
        COMPortDriver port = new COMPortDriver();
        I_COMPortReceiver rec =   new I_COMPortReceiver() {
            @Override
            public void onOther(String mes) {
                System.out.println(mes);
                }

            @Override
            public void onVolume(double val) {
                System.out.println("Vol="+val);
                }
            @Override
            public void onTemperature(double val) {
                System.out.println("T="+val);
                }

            @Override
            public void onState(int state) {
                System.out.println("Status word="+String.format("%x",state));
                }
            @Override
            public void onErrorCode(int state) {
            }
            @Override
            public void onHZ(double state) {
            }
            @Override
            public void onMS(double state) {
            }
            @Override
            public void onPower(double state) {
                }
            };
        port.connect(rec);
        port.getTemperature();
        port.getStateWord();
        port.write("aaaaaaaaaaaaa\n");
        port.close();
    }
}