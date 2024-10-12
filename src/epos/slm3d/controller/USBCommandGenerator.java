package epos.slm3d.controller;

import epos.slm3d.commands.*;
import epos.slm3d.settings.GlobalSettings;
import epos.slm3d.settings.WorkSpace;
import epos.slm3d.slicer.CommandGenerator;
import epos.slm3d.slicer.SliceRezult;
import epos.slm3d.stl.STLLine;
import epos.slm3d.stl.STLLineGroup;
import epos.slm3d.stl.STLLoop;
import epos.slm3d.utils.I_Notify;
import epos.slm3d.utils.UNIException;
import epos.slm3d.utils.Values;

import java.util.ArrayList;

/**
 * Created by romanow on 07.12.2017.
 */
public class USBCommandGenerator extends CommandGenerator {
    private int lineCount=0;
    private int layerCount=-1;
    private I_Notify notify;
    private USBProtocol usb;
    private boolean asBlock=false;
    private CommandLineBlock block = new CommandLineBlock();
    public void setBlockPrinting(){ asBlock=true; }
    private double cOffsetX=0,cOffsetY=0;
    private double maxSize=1;
    private void setCenterOffsets(){
        GlobalSettings gen = WorkSpace.ws().global().global;
        cOffsetX = gen.CenterOffsetX.getVal();
        cOffsetY = gen.CenterOffsetY.getVal();
        double out1 = Math.abs(cOffsetX);
        double out2 = Math.abs(cOffsetY);
        maxSize =  out1 > out2 ? out1 : out2;
        maxSize = 0.99 - maxSize;
        }
    public double getWorkSize(){
        return maxSize;
        }
    private STLLine correctCenterOffsets(STLLine orig){
        STLLine out = new STLLine(orig);
        out.invertX();
        out.shift(cOffsetX, cOffsetY);
        return out;
        }
    
    USBBack back = new USBBack() {
        @Override
        public void onSuccess(int code, int[] data) {
            System.out.print(String.format("code=%x answer=%x",code,data[0]));
            for(int i=1;i < 10 && i<data.length;i++)
                System.out.print(String.format(" %x",code,data[i]));
            System.out.println();
        }
        @Override
        public void onError(int code, int[] data) {
            notify.log("Ошибка исполнения команды:" +code+": "+USBCodes.answerName(data[0]));
        }
        @Override
        public void onFatal(int code, String mes) {
            notify.notify(Values.error,"Ошибка протокола: "+mes);
        }
    };

    public USBCommandGenerator(USBFace face, I_Notify notify0){
        notify = notify0;
        usb = new USBProtocol(face);
        asBlock = WorkSpace.ws().global().global.LineBlock.getVal();
        }
    /** Получает готовый инициализированный протокол */
    public USBCommandGenerator(USBProtocol usb0, I_Notify notify0){
        notify = notify0;
        usb = usb0;
        asBlock = WorkSpace.ws().global().global.LineBlock.getVal();
        }
    private boolean compareLineCounts() throws UNIException {
        usb.waitForState(USBCodes.STATE_WAITFORDATA,USBCodes.STATE_PRINT,0);
        int xx[] = usb.getLineCount(back);
        if (xx!=null){
            notify.log("Напечатано слоёв "+xx[1]+", линий передано "+lineCount+ ", напечатано "+xx[2]);
            return true;
            }
        else return false;
        }
    private void printBlock() throws UNIException {
        if (!asBlock || block.size()==0)
            return;
        usb.BurnLineBlock(block,back);
        block.clear();
        }
    @Override
    public void line(STLLine line0) throws UNIException {
        STLLine line = correctCenterOffsets(line0);
        if (lineCount==0){
            int state = usb.getPrinterState(back);
            if (state != USBCodes.STATE_READY)
                throw UNIException.io("Состояние "+USBCodes.stateName(state)+" вместо "+USBCodes.stateName(USBCodes.STATE_READY));
            usb.Startlayer(back);
            }
        if (!asBlock)
            usb.BurnLine(line,back);
        else{
            block.add(line);
            if (block.size()==usb.blockByteSize()/16-1){
                printBlock();
                }
            }
        lineCount++;
        }
    //------------------ Допечатать буфер -----------------------
    public void flushAndWait() throws UNIException {
       if (layerCount!=-1){
            printBlock();           // Вывести "хвост" при блочной передаче
            compareLineCounts();    // Ждать состояния "Готов к приему данных" и сравнить строки
            }
        }
    @Override
    public void layer() throws UNIException {
        usb.NextLayer(back);
        layerCount++;
        lineCount=0;
        }
    @Override
    public void init() throws UNIException {
        usb.init();
        }
    @Override
    public void start() throws UNIException {
        setCenterOffsets();
        int state = usb.getPrinterState(back);
        if (state != USBCodes.STATE_STANDBY)
            throw UNIException.io("Состояние "+USBCodes.stateName(state)+" вместо "+USBCodes.stateName(USBCodes.STATE_STANDBY));
        usb.StartPrint(back);
        lineCount=0;
        layerCount=-1;
        }
    @Override
    public void end(SliceRezult rez) throws UNIException {
        printBlock();
        compareLineCounts();
        usb.EndOfPrint(back);
        }
    @Override
    public void loops(ArrayList<STLLoop> loops) {
        }
    @Override
    public void lines(STLLineGroup lines) {
        }
    @Override
    public void command(Command cmd) throws UNIException {
        usb.oneCommand(cmd.toIntArray());
        }
    @Override
    public void close() {
        try {
            usb.close();
            } catch (UNIException e) {}
        }
    @Override
    public void cancel() throws UNIException {
        usb.CancelPrint(back);
        }
}
