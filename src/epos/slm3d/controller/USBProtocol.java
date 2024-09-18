package epos.slm3d.controller;

import epos.slm3d.commands.*;
import epos.slm3d.settings.GlobalSettings;
import epos.slm3d.settings.Settings;
import epos.slm3d.settings.WorkSpace;
import epos.slm3d.stl.STLLine;
import epos.slm3d.utils.I_Notify;
import epos.slm3d.utils.UNIException;
import epos.slm3d.utils.Values;

/**
 * Created by romanow on 21.12.2017.
 */
public class USBProtocol {
    private USBFace usb;
    private int printerState= USBCodes.STATE_READY;         // Текущее состояние принтера
    private int availMemory=-1;
    private boolean error=false;
    private USBBack back=null;
    private int layerCount=-1,lineCount=0;
    public void usbBack(USBBack back0 ){ back = back0; }
    public USBProtocol(USBFace usb0) {
        usb = usb0;
        }
    public void init() throws UNIException {
        usb.init();
        }
    public void close() throws UNIException {
        usb.close();
        }
    private boolean procAck(int data[]){
        if (data[0]==USBCodes.ACK_EXCEPTION){
            usb.close();
            error=true;
            return false;
            }
        if (data[0]==USBCodes.ACK_ERROR || data[0]==USBCodes.ACK_FATAL){
            error=true;
            return false;
            }
        return data[0]==USBCodes.ACK_OK;
        }
    //---------------------------------------------------------------------
    public int []oneCommand(int cmd, int par,USBBack back){
        int data[]=new int[2];
        data[0]=cmd;
        data[1]=par;
        return oneCommand(data,back);
    }
    public int []oneCommand(int cmd){
        int data[]=new int[1];
        data[0]=cmd;
        return oneCommand(data,null);
        }
    public int []oneCommand(int cmd,USBBack back){
        int data[]=new int[1];
        data[0]=cmd;
        return oneCommand(data,back);
        }
    public int []oneCommand(final int data[]) {
        return oneCommand(data,null);
        }
    public synchronized int []oneCommand(final int data[],USBBack back) {
        return oneCommand(data,data.length,back);
        }
    public synchronized int []oneCommand(final int data[],int sz, USBBack back) {
        String mes ="";
        error = false;
        int xx[] = new int[1];
        final int cmd = data[0];
        try {
            usb.write(data,sz);
            if (Values.USBSendReceivePause!=0){
                try {
                    Thread.sleep(Values.USBSendReceivePause);
                    } catch (InterruptedException e) {}
                }
            xx = usb.read();
            } catch (UNIException ee) {
                xx[0] = USBCodes.ACK_EXCEPTION;
                mes = ee.getMessage();
                }
        procAck(xx);
        if (back!=null){
            if (xx[0]==USBCodes.ACK_OK)
                back.onSuccess(cmd, xx);
            else
            if (xx[0]==USBCodes.ACK_EXCEPTION)
                back.onFatal(USBCodes.FATAL_EXCEPTION,String.format("Команда %x исключение usb: ",cmd)+mes);
            else
                back.onError(cmd, xx);
            }
        return xx;
        }
    public void setAvailMemory(int vv){ availMemory = vv; }
    //-----------------------------------------------------------------------------
    /** Сохранение данных о текущей печати */
    public void saveLayerLineCounts() throws UNIException {
        int xx[]=getLineCount(back);
        Settings set = WorkSpace.ws().global();
        if (xx==null){
            set.global.CurrentLayer.setVal(layerCount);
            set.global.CurrentLine.setVal(lineCount);
            }
        else{
            set.global.CurrentLayer.setVal(xx[1]);
            set.global.CurrentLine.setVal(xx[2]);
            }
        WorkSpace.ws().saveSettings();
        }
    /** Запрос свободной памяти */
    public int GetAvailMemory(USBBack back){
        int xx[] = oneCommand(USBCodes.GetAvailMemory,back);
        if (xx[0]==USBCodes.ACK_OK){
            availMemory = xx[1];
            System.out.println("!!!!! "+availMemory);
            return availMemory;
            }
        return -1;
        }
    /** Запрос текущего состояния */
    public int getPrinterState(USBBack back){
        int xx[] = oneCommand(USBCodes.IsReady,back);
        if (xx[0]==USBCodes.ACK_OK){
            printerState = xx[1];
            return printerState;
            }
        return -1;
        }
    /** Количество выведенных слоев и строк */
    public int []getLineCount(USBBack back){
        int xx[] = oneCommand(USBCodes.GetLineCount,back);
        if (xx[0]==USBCodes.ACK_OK){
            layerCount = xx[1];
            lineCount = xx[2];
            return xx;
            }
        return null;
        }
    public boolean isPrinterState(int state, USBBack back){
        return getPrinterState(back)==state;
        }
    public void waitForState(int waitForState,int currentState, int delay) throws UNIException {
        waitForState(waitForState,currentState,currentState,delay);
        }
    /** ждать состояния */
    private volatile boolean cancelWait=false;
    public void cancelWait(){ 
        cancelWait=true; 
        }
    public void waitForState(int waitForState,int currentState, int currentState2, int delay) throws UNIException {
        int cDelay=delay;
        while(true){
            getPrinterState(back);
            if (cancelWait){ 
                cancelWait=false;
                return;
                }
            if (error)
                throw UNIException.io("Ошибка USB-интерфейса:");
            if (printerState == waitForState)                                   // Ушел в требуемое состояние
                return;
            if (printerState != currentState && printerState !=currentState2)   // Не в исходном состоянии
                throw UNIException.io("Недопустимое состояние "+USBCodes.stateName(printerState)+ ": ждет "+ USBCodes.stateName(waitForState));
            if (delay!=0 && cDelay ==0)
                throw UNIException.io("Тайм-аут ожидания состояния "+ USBCodes.stateName(waitForState));
            cDelay--;
            System.out.println("Состояние "+USBCodes.stateName(printerState)+": ждет "+ USBCodes.stateName(waitForState));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw UNIException.io("Прервано ожидание состояния "+ USBCodes.stateName(waitForState));
                }
            }
        }
    /** Запуск действия в GUI */
    public void runWhenState(int waitForState, int currentState, int delay, final I_Notify notify, final Runnable code) {
        new Thread(()->{
            try {
                try {
                    Thread.sleep(1000);
                    } catch (InterruptedException ex) {}
                waitForState(waitForState,currentState, delay);
                java.awt.EventQueue.invokeLater(()->{
                    code.run();
                });
            } catch (UNIException ex) { notify.notify(Values.error,ex.toString()); }
        }).start();
    }

    /** ждать места в буфере контроллера */
    public void waitForMemory(int size, int delay, USBBack back) throws UNIException {
        int cDelay=delay;
        while(true){
            int state = getPrinterState(back);
            if (state== USBCodes.STATE_WAITFORDATA)
                return;
            //if (state== USBCodes.STATE_READY)
            //    return;
            if (state!= USBCodes.STATE_PRINT)
                throw UNIException.io("Недопустимое состояние "+USBCodes.stateName(state)+ " для ожидания памяти");
            GetAvailMemory(back);
            if (error)
                throw UNIException.io("Ошибка USB-интерфейса:");
            if (availMemory>=size)
                return;
            if (delay!=0 && cDelay ==0)
                throw UNIException.io("Тайм-аут ожидания памяти");
            cDelay--;
            System.out.println("Wait for buffer");
            try {
                Thread.sleep(1000);
                } catch (InterruptedException e) {
                throw UNIException.io("Прервано ожидание памяти");
                }
            }
        }
    //------------------------------------------------------------------------
    /** конец печати */
    public void EndOfPrint(USBBack back){
        oneCommand(USBCodes.EndOfPrint,back);
        }
    /** отмена печати */
    public void CancelPrint(USBBack back){
        oneCommand(USBCodes.StopPrint,back);
        }
    /** печать линии */
    public void BurnLine(STLLine line, USBBack back) throws UNIException {
        CommandLine cmd = new CommandLine(line);
        if (availMemory<1)             // Лишний раз не спрашивать
            waitForMemory(1,USBCodes.BurnLineTimeOut,back);
        int xx[] = new int[0];
        xx = cmd.toIntArray();
        oneCommand(xx,back);
        availMemory--;    
        }
    /** печать линии */
    public void BurnLineBlock(CommandLineBlock block, USBBack back) throws UNIException {
        int lineBlockSize = usb.blockByteSize()/16 - 1;
        if (availMemory<lineBlockSize)             // Лишний раз не спрашивать
            waitForMemory(lineBlockSize,USBCodes.BurnLineTimeOut,back);
        int xx[] = new int[0];
        xx = block.toIntArray();
        block.clear();
        oneCommand(xx,back);
        availMemory-=lineBlockSize;
        }
    private USBBack motorsBack = new USBBack() {
        @Override
        public void onSuccess(int code, int[] data) {
            int idx = data[1]-1;
            saveMotorPosition(data,idx);
            }
        @Override
        public void onError(int cmd, int[] data) {
            back.onError(cmd,data);
            }
        @Override
        public void onFatal(int errorCode, String message) {
            back.onFatal(errorCode,message);
            }
    };

    public void setLineCount(int layerCount,int lineCount){
        Command cmd = new CommandIntList(USBCodes.SetLineCounter,layerCount,lineCount);
        oneCommand(cmd.toIntArray(),back);
        }
    public void NextLayer(USBBack back) throws UNIException {
        CommandLayer cmd = new CommandLayer();
        oneCommand(cmd.toIntArray(),back);
        waitForState(USBCodes.STATE_READY,USBCodes.STATE_BUSY,USBCodes.STATE_WAITFORDATA,USBCodes.LayerTimeOut);
        }
    /** Старт отложенной печати  */
    public void StartPrint(USBBack back){
        oneCommand(USBCodes.StartPrint,back);
        }
    /** Старт отложенной печати  */
    public void Startlayer(USBBack back){
        oneCommand(USBCodes.StartLayer,back);
        }
    //------------------------------------------------------------------------------
    /** Нет операции */
    public void NOP(USBBack back){
        oneCommand(USBCodes.NOP,back);
        }
    /** Жесткий сброс */
    public void HardReset(USBBack back){
        oneCommand(USBCodes.HardReset,back);
        }
    /** Мягкий сброс с синхронизированной приостановкой печати */
    public void SoftReset(USBBack back){
        oneCommand(USBCodes.SoftReset,back);
        }
    /** Мягкий сброс протокола для длинных команд */
    public void USBReset(USBBack back){
        oneCommand(USBCodes.USBReset,back);
        }
    /** Приостановка печати  */
    public void SuspendPrint(USBBack back){
        oneCommand(USBCodes.SuspendPrint,back);
        }
    /** Возобновление печати  */
    public void ResumePrint(USBBack back){
        oneCommand(USBCodes.ResumePrint,back);
        }
    /** Остановка печати без возобновлния */
    public void StopPrint(USBBack back){
        oneCommand(USBCodes.StopPrint,back);
        }
    /** Остановка печати после завершения слоя */
    //public void StopPrintAfterLayer(USBBack back){
    //    oneCommand(USBCodes.StopPrintAfterLayer,back);
    //    }
    /** Приостановка печати после завершения слоя */
    //public void SuspendPrintAfterLayer(USBBack back){
    //   oneCommand(USBCodes.SuspendPrintAfterLayer,back);
    //    }
    /** Остановить циклическое исполнение блока команд */
    //public void StopPrintBlockRepeat(USBBack back){
    //    oneCommand(USBCodes.StopPrintBlockRepeat,back);
     //   }
    /** Выполнить стартовый тест */
    public void StartMainTest(USBBack back){
        oneCommand(USBCodes.StartMainTest,back);
        }
    /** Получить состояние - кратко */
    //public void GetShortStatus(USBBack back){
    //    oneCommand(USBCodes.GetShortStatus,back);
    //    }
    /** Завершение работы */
    public void shutDown(USBBack back){
        oneCommand(USBCodes.ShutDown,back);
        }
    /** Перевод в нирвану */
    public void offLine(USBBack back){
        oneCommand(USBCodes.OffLine,back);
        }
    private UNIException printErr=UNIException.io("Ошибка принтера");
    public int blockByteSize() { return usb.blockByteSize(); }
    //--------------------------------------------------------------------------------
    public void saveMotorPosition(int mdata[],int idx){
        GlobalSettings set = WorkSpace.ws().global().global;
        switch (idx){
            case 0:
                set.M1LowPos.setVal(mdata[2]);
                set.M1HighPos.setVal(mdata[3]);
                set.M1CurrentPos.setVal(mdata[4]);
                break;
            case 2:
                set.M3LowPos.setVal(mdata[2]);
                set.M3HighPos.setVal(mdata[3]);
                set.M3CurrentPos.setVal(mdata[4]);
                break;
            case 3:
                set.M4LowPos.setVal(mdata[2]);
                set.M4HighPos.setVal(mdata[3]);
                set.M4CurrentPos.setVal(mdata[4]);
                break;
        }
        WorkSpace.ws().saveSettings();
        }
    //--------------------------------------------------------------------------
    public void OxygenOff(USBBack back){
        int data[] = new int[4];
        data[0]=USBCodes.OxygenSensorSetParam;
        data[1]=0;
        data[2]=0;
        data[3]=0;
        oneCommand(data, back);        
        }
    public void OxygenOn(USBBack back){
        int data[] = new int[4];
        data[0]=USBCodes.OxygenSensorSetParam;
        data[1]=1;
        data[2]=1;
        data[3]=0x01000000;
        oneCommand(data, back);        
        }
    public void OxygenGetData(USBBack back){
        oneCommand(USBCodes.OxygenSensorGetData,1, back);        
        }
    public void SetDropParameter(int np, int delta,USBBack back){
        int data[] = new int[3];
        data[0]=USBCodes.SetDropParameter;
        data[1]=np;
        data[2]=delta;
        oneCommand(data, back);        
        }
    }


