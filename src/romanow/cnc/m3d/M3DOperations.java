package romanow.cnc.m3d;

import romanow.cnc.Values;
import romanow.cnc.commands.Command;
import romanow.cnc.commands.CommandLayer;
import romanow.cnc.controller.USBCommandGenerator;
import romanow.cnc.controller.USBProtocol;
import romanow.cnc.settings.Settings;
import romanow.cnc.settings.WorkSpace;


import romanow.cnc.slicer.*;
import romanow.cnc.stl.*;
import romanow.cnc.usb.M3DUSBController;
import romanow.cnc.utils.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by romanow on 02.12.2017.
 */
public class M3DOperations {
    private I_Notify notify;
    private volatile boolean finish = false;        // Старое !!!!!!!!!!!!!!!!!!
    private String outname="";
    private int lineCount=0;
    private int layerCount=0;
    private long startTime=0;
    private USBCommandGenerator gen=null;
    private boolean printEvent=false;
    public void printEvent(boolean vv){ printEvent=vv; }
    public void finish(){ 
        finish=true; 
        }
    public M3DOperations(I_Notify note){
        notify = note;
        }
    private int cnt=0;
    /**  копирование файла программы-прототипа с расшифровкой формата */
    public void copy(String fname){
        finish = false;
        M3DFileBinInputStream bb;
        M3DFileBinOutputStream out;
        cnt=0;
        try {
            bb = new M3DFileBinInputStream(fname);
            int idx=fname.lastIndexOf(".");
            outname = fname.substring(0,idx)+"_copy"+fname.substring(idx);
            out = new M3DFileBinOutputStream(outname);
            } catch(FileNotFoundException ee){
                notify.notify(Values.fatal,ee.getMessage());
                return;
                }
        try {
            bb.procFile(new OnM3DCommand(){
                @Override
                public boolean onCommand(Command cmd) {
                    try {
                        out.write(cmd.CreateBynary());
                        cnt++;
                        if (cnt%1000==0)
                            notify.info(Integer.toHexString(out.addr())+" команд "+cnt);
                        } catch (UNIException ee) {
                            notify.notify(Values.fatal,ee.getMessage());
                            }
                        catch (IOException ee) {
                            notify.notify(Values.fatal,ee.getMessage());
                            }
                    return finish;
                    }
                @Override
                public void onFinish() {
                    notify.info("Копирование закончено");
                    try {
                        bb.close();
                        out.close();
                        compare(fname,outname);
                        } catch(IOException ee){}
                    }
                @Override
                public void onHeader(int hd[]) {
                try { out.writeHeader(); }
                    catch(UNIException ee){}
                    }
                });
            } catch(UNIException ee){
                notify.notify(Values.fatal,ee.getMessage());
                }
        }
    private String prepareEOS(String ss){
        char cc[] = ss.toCharArray();
        StringBuffer out = new StringBuffer();
        for(char c:cc){
            if (c!='\n')
                out.append(c);
            else{
                out.append('\r');
                out.append('\n');
                }
            }
        return out.toString();
        }
    /** Вывод дампа файла программы - прототипа */
        public void dumpMarkOut(String fname){
            M3DFileBinInputStream bb;
            OutputStreamWriter out;
            try {
                bb = new M3DFileBinInputStream(fname);
                out = new OutputStreamWriter(new FileOutputStream(fname+".txt"),"Windows-1251");
            } catch(FileNotFoundException ee){
                notify.notify(Values.fatal,ee.toString());
                return;
                }
              catch (UnsupportedEncodingException e) {
                  notify.notify(Values.fatal,e.toString());
                  return;
                  }
            try{
                bb.procFile(new OnM3DCommand(){
                    @Override
                    public boolean onCommand(Command cmd) {
                        String ss;
                        try {
                            if (cmd instanceof CommandLayer){
                                ss = "-------------------";
                                notify.info(ss);
                                out.write(prepareEOS(ss));
                                }
                            ss = cmd.toDump();
                            notify.info(cmd.toDump());
                            out.write(prepareEOS(ss));
                            if (cmd instanceof CommandLayer){
                                ss = "-------------------";
                                notify.info(ss);
                                out.write(prepareEOS(ss));
                                }
                            } catch (IOException e2) {
                                notify.notify(Values.fatal,e2.toString());
                                return true;
                            }
                        return false;
                    }
                    @Override
                    public void onFinish() {
                        notify.info("Просмотр закончен");
                        try {
                            out.close();
                            bb.close();
                            } catch(Exception ee){}
                        }
                    @Override
                    public void onHeader(int hd[]) {
                        int addr=0;
                        String ss="";
                        for(int i=0;i<hd.length;i++,addr+=4){
                            if (i%8==0)
                                ss+=String.format("\n%08X ",addr);
                            ss+=String.format("%08X ",hd[i]);
                            }
                        ss+="";
                        notify.info(ss);
                        try {
                            out.write(prepareEOS(ss));
                            } catch (IOException e) { notify.notify(Values.fatal,e.toString()); }
                    }
                });
            } catch(UNIException ee){
                notify.notify(Values.fatal,ee.getMessage());
            }
        }
    /** Вывод файла программы - прототипа в USB-контроллер */
    public void toUsb(String fname){
        finish = false;
        M3DFileBinInputStream bb;
        cnt=0;
        try {
            bb = new M3DFileBinInputStream(fname);
            } catch(FileNotFoundException ee){
                notify.notify(Values.fatal,ee.getMessage());
                return;
                }
        M3DUSBController usb = new M3DUSBController(notify);
        try {
            usb.open();
            usb.sendCMD0();
            } catch(UNIException ee){
                notify.notify(Values.error,ee.toString());
                return;
                }
        try {
        bb.procFile(new OnM3DCommand(){
            @Override
            public boolean onCommand(Command cmd) {
                try {
                    usb.sendData(cmd.CreateBynary());
                    cnt++;
                    if (cnt%1000==0)
                        notify.info(" команд "+cnt);
                    } catch (Exception ee) {
                        notify.notify(Values.fatal,ee.toString());
                        }
                return finish;
                }
            @Override
            public void onFinish() {
                notify.info("Копирование закончено");
                try {
                    bb.close();
                    usb.close();
                    compare(fname,outname);
                    } catch(Exception ee){}
                }
            @Override
            public void onHeader(int hd[]) {
                try {
                    //usb.sendData(ws.temp().createHeader());
                    } catch(Exception ee){
                        notify.notify(Values.fatal,ee.getMessage());
                        }
                }
            });
        } catch(UNIException ee){
            notify.notify(Values.fatal,ee.getMessage());
            }
    }
    private boolean sliceStop=false;

     /**  Общий модуль слайсирования слоя */
    // layer !=null при повторном слайсировании существующих контуров, иначе нарезка
    public SliceRezult sliceCommon(SliceParams par, final CommandGenerator generator, final  ViewAdapter back, Settings set){
        final SliceRezult layerRez = new SliceRezult();
        try {
            WorkSpace ws = WorkSpace.ws();
            final STLPoint2D last = new STLPoint2D(0, 0);
            if (set==null)
                set = ws.local();
            int mode = set.slice.Mode.getVal();
            boolean optimize = set.slice.MoveOptimize.getVal();
            double plusAngle = set.slice.FillParametersAngle.getVal();
            double angle0 = set.slice.FillParametersAngle.getVal();
            double angleInc = set.slice.FillParametersAngleInc.getVal();
            double raster = set.slice.FillParametersRaster.getVal();
            double diff = set.slice.FillParametersRaster.getVal() * Values.OptimizeRasterCount;
            double vStep = set.model.VerticalStep.getVal();
            double z0 = ws.local().model.ZStart.getVal();
            cnt = 0;
            double angle = par.layer!=null ? par.layer.angle() :  (angle0 + par.layerNum * angleInc) % 180;
            double z = par.layer!=null ? par.layer.z() : z0 + par.layerNum * vStep;
            if (par.mode==1){
                z = par.zOrig;
                double xx = (z - z0)/vStep;
                angle = (angle0 + xx * angleInc) % 180;
                }                   // Для добавляемого слоя
            generator.layer();
            //--------------------------------------------------------------------------------------------------
            I_LineSlice sliceAdapter = new I_LineSlice() {
                @Override
                public void onSliceLayer() {
                    notify.log( "Команд " + cnt + "");
                }
                @Override
                public void onLineGroup() {
                    try{
                        generator.lineGroup();
                        } catch (UNIException ee) {
                            notify.notify(Values.fatal, ee.getMessage() + "");
                            sliceStop = true;
                            }
                    }
                @Override
                public void onSliceLine(STLLine pp) {
                    try {
                        generator.line(pp);
                        layerRez.add(pp.lengthXY(), new STLLine(last, pp.one()).lengthXY());
                        last.x(pp.two().x());                        // Координаты последной
                        last.y(pp.two().y());
                        cnt++;
                    } catch (UNIException ee) {
                        notify.notify(Values.fatal, ee.getMessage() + "");
                        sliceStop = true;
                        }
                    back.onStepLine();
                }
                @Override
                public boolean isFinish() {
                    return back.isFinish() || sliceStop;
                }
                @Override
                public void onSliceError(SliceError error) {
                    generator.onError(error);
                }
                @Override
                public void notify(int level,String mes) {
                    notify.notify(level,mes);
                    }
                };
            Slicer slicer = new Slicer( ws.model().triangles(), z, Values.PointDiffenerce, set, notify);
            ArrayList<STLLoop> loops=null;
            if (par.layer!=null){
                slicer.orig(par.layer.lines());
                loops = par.layer.loops();
                slicer.loops(loops);
                }
            if(par.mode!=3){
                slicer.createLoops(par.layer==null,sliceAdapter);
                loops = slicer.loops();
                if (par.layer!=null){
                    par.layer.lines(slicer.orig());         // Вернуть исходные (м.б.изменены)
                    par.layer.loops(loops);
                    }
                }
            generator.lines(slicer.orig());
            notify.log( String.format("z=%5.2f отрезков в контурах=%d", z, slicer.orig().size()));
            generator.loops(loops);
            notify.log( "Контуров " + loops.size() + " замкнутых " + slicer.totalCompleted() + "");
            //------------------------------ Оконтуривание ---------------------
            if (set.slice.SendLoops.getVal()){
                for(STLLoop loop : loops)
                    for(STLLine pp : loop.lines()){
                        generator.line(pp);
                        layerRez.add(pp.lengthXY(), new STLLine(last, pp.one()).lengthXY());
                        last.x(pp.two().x());                        // Координаты последной
                        last.y(pp.two().y());
                        cnt++;  
                        }
                }        
            //------------------------------------------------------------------
            boolean finish1;
            if (optimize)
                finish1 = slicer.slice(mode, new OptimizeAdaprer(sliceAdapter, diff, set.slice.FillContinuous.getVal(),notify),set);
            else
                finish1 = slicer.slice(mode, sliceAdapter,set);
            generator.layerFinished(layerRez,z,angle);
            back.onStepLayer();
            notify.log( String.format("слой: %d линий: %d длина: %s холостой ход: %d%% время: %s", par.layerNum, layerRez.lineCount(), layerRez.printLength(), layerRez.moveProc(), layerRez.printTime()));
            if (finish1) {
                notify.notify(Values.error, "Слайсинг прерван");
                generator.close();
                layerRez.setError();
                }
            } catch (UNIException ex) {
                notify.notify(Values.fatal,ex.getMessage());
                layerRez.setError();
                generator.close();
                }
        return layerRez;
        }

    private volatile int threadCount;
    private volatile int procCount;
    public SliceData sliceConcurent(final  ViewAdapter back){
        WorkSpace ws = WorkSpace.ws();
        SliceData xx = sliceConcurent(back,null);
        ws.sendEvent(Events.NewData);
        return xx;
        }
    /** Параллельное слайсирование в потоках */
    public SliceData sliceConcurent(final  ViewAdapter back,DataOutputStream out){
        WorkSpace ws = WorkSpace.ws();
        final SliceRezult rez = new SliceRezult();
        final SliceData data = new SliceData();
        int layerCount=0;
        Settings set = ws.local();
        double vStep = set.model.VerticalStep.getVal();
        double zz = ws.model().max().z();
        double z0 = set.model.ZStart.getVal();
        double z1 = set.model.ZFinish.getVal();
        if (zz < z1)
            z1 = zz;
        sliceStop = false;
        int nLayers = (int)((z1-z0)/vStep+1);
        for(layerCount=0; layerCount < nLayers && !sliceStop; layerCount++, z0+=vStep){
            data.addLayer(new SliceLayer(layerCount+1,z0));
            }
        if (out!=null){             // Поток вывода
            new Thread(()->{
                try {
                    ws.saveHead(out);
                    data.saveConcurent(out,notify);
                    } catch (IOException e) { notify.notify(Values.error,e.toString());}
            }).start();
            }
        notify.setProgress(0);
        int threadCount0=ws.global().mashine.SliceThreadNum.getVal();
        threadCount = threadCount0;
        procCount=0;
        for(layerCount=nLayers-1; layerCount >=0; layerCount--){
            final int layerNum = layerCount;
            if (back.isFinish()){
                sliceStop = true;
                data.sliceStop();
                notify.notify(Values.error,"Слайсинг прерван");
                break;
                }
            while(threadCount<=0 && !sliceStop){
                try {
                    Thread.sleep(1000);
                    }   catch (InterruptedException e) {}
                }
            threadCount--;
            new Thread(()->{
                SliceData tmp = new SliceData();
                CommandGenerator generator = new SliceDataGenerator(tmp,back);
                try {
                    generator.init();
                    generator.start();
                    SliceRezult layerRez = sliceCommon(new SliceParams(layerNum),generator,back,null);
                    generator.end(layerRez);
                    generator.close();
                    SliceLayer lr = tmp.get(0);
                    lr.layerOrderNum(layerNum+1);
                    synchronized (data){
                        lr.setReady();                  // Для параллельного вывода
                        data.replace(lr,layerNum);
                        data.notify();
                        }
                    rez.procLayer(layerRez);
                    procCount++;
                    notify.setProgress((int)((procCount+1)*100/nLayers));
                    }
                    catch (UNIException ee){
                        generator.close();
                        notify.notify(Values.fatal,ee.toString());
                        rez.setError();
                        }
                    threadCount++;
                    }).start();
                }
        while(threadCount!=threadCount0 && !sliceStop){
            try {
                Thread.sleep(1000);
                //System.out.println(threadCount);
                }   catch (InterruptedException e) {}
            }
        notify.log("Слайсинг закончен, слоев: "+layerCount);
        notify.log(String.format("линий: %d длина: %s холостой ход: %d%% время: %s",rez.lineCount(),rez.printLength(),rez.moveProc(),rez.printTime()));
        set.statistic.setFromRezult(rez);
        set.statistic.SliceTime.setVal((int)(back.timeInMs()/1000));
        ws.sendEvent(Events.Settings);
        if (sliceStop)
            rez.setError();
        data.result(rez);
        synchronized (data){
            data.notify();
            }
        return data;
        }
    //+++1.1 ---------------------------------------------------- Генерация контуров по слоям ---------------------------------
    public ArrayList<STLLoopGenerator> createLoops(final  ViewAdapter back){
        ArrayList<STLLoopGenerator> loopList = new ArrayList<>();
        final SliceRezult rez = new SliceRezult();
        WorkSpace ws = WorkSpace.ws();
        Settings set = ws.local();
        double vStep = set.model.VerticalStep.getVal();
        double zz = ws.model().max().z();
        double z0 = set.model.ZStart.getVal();
        double z1 = set.model.ZFinish.getVal();
        if (z1==0 || zz < z1)
            z1 = zz;
        sliceStop = false;
        int nLayers = (int)((z1-z0)/vStep+1);
        notify.setProgress(0);
        int layerCount=0;
        try {
            for(layerCount=0; layerCount < nLayers; layerCount++){
                if (back.isFinish()){
                    sliceStop = true;
                    notify.notify(Values.error,"Слайсинг прерван");
                    break;
                    }
                SliceParams par = new SliceParams(layerCount);
                final STLPoint2D last = new STLPoint2D(0, 0);
                if (set==null)
                    set = ws.local();
                double diff = set.slice.FillParametersRaster.getVal() * Values.OptimizeRasterCount;
                vStep = set.model.VerticalStep.getVal();
                z0 = ws.local().model.ZStart.getVal();
                cnt = 0;
                double z = par.layer!=null ? par.layer.z() : z0 + par.layerNum * vStep;
                double diff0 = Values.PointDiffenerce;
                STLLoopGenerator slicer = new STLLoopGenerator(ws.model().triangles(), z, diff0,notify);
                ArrayList<STLLoop> repaired = slicer.createLoops(true);
                if (repaired.size()!=0){
                    notify.notify(Values.warning,"Принудительно замкнуты контуры: "+repaired.size());
                    }
                ArrayList<STLLoop> loops=null;
                if (par.layer!=null){
                    slicer.orig(par.layer.lines());
                    loops = par.layer.loops();
                    slicer.loops(loops);
                    }
                if(par.mode!=3){
                    repaired = slicer.createLoops(true);
                    if (repaired.size()!=0){
                        notify.notify(Values.warning,"Принудительно замкнуты контуры: "+repaired.size());
                        }
                    slicer.createLoops(par.layer==null);
                        loops = slicer.loops();
                        if (par.layer!=null){
                            par.layer.lines(slicer.orig());         // Вернуть исходные (м.б.изменены)
                            par.layer.loops(loops);
                            }
                        }
                    notify.log( String.format("z=%5.2f отрезков в контурах=%d", z, slicer.orig().size()));
                    notify.log( "Контуров " + loops.size() + " замкнутых " + slicer.totalCompleted() + "");
                    loopList.add(slicer);
                    }
                notify.setProgress((int)((layerCount+1)*100/nLayers));
                }
            catch (UNIException ee){
                notify.notify(Values.fatal,ee.toString());
                return null;
                }
        notify.log("Слайсинг закончен, слоев: "+layerCount);
        notify.log(String.format("линий: %d длина: %s холостой ход: %d%% время: %s",rez.lineCount(),rez.printLength(),rez.moveProc(),rez.printTime()));
        set.statistic.setFromRezult(rez);
        set.statistic.SliceTime.setVal((int)(back.timeInMs()/1000));
        ws.sendEvent(Events.Settings);
        ws.sendEvent(Events.NewData);
        return loopList;
        }

    /** Слайсирование для разных алгоритмов и разных генераторов команд */
    public SliceRezult sliceTo(CommandGenerator generator, final  ViewAdapter back){
        ArrayList<STLLoopGenerator> loopList = createLoops(back);
        final SliceRezult rez = new SliceRezult();
        WorkSpace ws = WorkSpace.ws();
        Settings set = ws.local();
        double vStep = set.model.VerticalStep.getVal() ;
        double zz = ws.model().max().z();
        double z0 = set.model.ZStart.getVal();
        double z1 = set.model.ZFinish.getVal();
        if (z1==0 || zz < z1)
            z1 = zz;
        sliceStop = false;
        int nLayers = (int)((z1-z0)/vStep+1);
        notify.setProgress(0);
        int layerCount=0;
        try {
            generator.init();
            generator.start();
            for(layerCount=nLayers-1; layerCount >=0; layerCount--){
                if (back.isFinish()){
                    sliceStop = true;
                    notify.notify(Values.error,"Слайсинг прерван");
                    break;
                    }
                final SliceRezult layerRez = sliceCommon(new SliceParams(layerCount,z0+layerCount*vStep),generator,back,null);
                    if (layerRez.hasError()){
                        break;
                        }
                    rez.procLayer(layerRez);
                    notify.setProgress((int)((nLayers-layerCount+1)*100/nLayers));
                    }
            generator.end(rez);
            generator.close();
            }
            catch (UNIException ee){
                notify.notify(Values.fatal,ee.toString());
                return rez;
                }
        notify.log("Слайсинг закончен, слоев: "+layerCount);
        notify.log(String.format("линий: %d длина: %s холостой ход: %d%% время: %s",rez.lineCount(),rez.printLength(),rez.moveProc(),rez.printTime()));
        set.statistic.setFromRezult(rez);
        set.statistic.SliceTime.setVal((int)(back.timeInMs()/1000)); 
        ws.sendEvent(Events.Settings);
        ws.sendEvent(Events.NewData);
        return rez;
        }
    /** Повторный слайсинг слоя в файле */
    public SliceRezult reSliceLayer(SliceParams par, SliceData data, final  ViewAdapter back, Settings set){
        SliceData tmp = new SliceData();
        //SliceLayer layer = zOrig<0 ? data.get(layerNum) : null;
        CommandGenerator generator = new SliceDataGenerator(tmp,back);
        sliceStop = false;
        SliceRezult layerRez = new SliceRezult();
        try {
            generator.init();
            generator.start();
            layerRez = sliceCommon(par,generator,back,set);
            generator.end(layerRez);
            generator.close();
            SliceLayer lr = tmp.get(0);
            if (set.model!=null)
                lr.sliceSettings(set);
            if (par.zOrig<0){
                lr.setModified();
                lr.layerOrderNum(par.layer.layerOrderNum());
                data.replace(lr,par.layerNum);
                }
            else{
                int k = data.addLayerSorted(lr);
                layerRez.layerIdx(k);
                lr.layerOrderNum(0);
                }
            }
        catch (UNIException ee){
            generator.close();
            notify.notify(Values.fatal,ee.toString());
            layerRez.setError();
            }
        notify.log("Слайсинг закончен");
        return layerRez;
        }
    /** Сравнение файлов в формате программы - прототипа */
    public void compare(String fname, String fname2){
        finish = false;
        M3DFileBinInputStream bb;
        M3DFileBinInputStream bb2;
        int addr = 0;
        cnt=0;
        try {
            notify.info("Сравнение:");
            bb = new M3DFileBinInputStream(fname);
            bb2 = new M3DFileBinInputStream(fname2);
            try {
                while(true){
                    int vv = bb.readInt();
                    int vv2 = bb2.readInt();
                    if (vv!=vv2){
                        notify.info(Integer.toHexString(addr)+":"+Integer.toHexString(vv)+"-"+ Integer.toHexString(vv2) );
                        }
                    addr+=4;
                    }
                } catch(IOException ee){
                    try {
                        bb.close();
                        bb2.close();
                        } catch (IOException e2){}
                    }
            } catch(FileNotFoundException ee){
                notify.notify(Values.fatal,ee.getMessage());
                return;
                }
        notify.notify(Values.fatal,"Сравнение закончено");
        }
    
    public void copySLM3DtoUSB(ViewAdapter synch, boolean stopPoint, USBProtocol protocol) throws UNIException{
        if (!stopPoint)
            copySLM3DtoUSB(synch,protocol);
        else
            copySLM3DtoUSBStopPoint(synch,protocol);
        }
    public void cancelPrint() throws UNIException{
        gen.cancel();                
        }
    public boolean copyLayerToUSB(ViewAdapter synch,USBProtocol protocol,SliceLayer layer) throws UNIException{
        return copyLayerToUSB(synch,protocol,layer,-1);
        }
    public boolean copyLayerToUSB(ViewAdapter synch,USBProtocol protocol,SliceLayer layer, int lineNum) throws UNIException{
        testGenerator(protocol);
        WorkSpace ws = WorkSpace.ws();
        notify.log( Utils.toTimeString(timeInMs()/1000)+" Печать слоя "+layer.label());
        if (lineNum==-1){
            gen.layer();
            if (printEvent) ws.sendEvent(Events.LayerPrint);
            lineNum=0;
            }
        int sz = layer.segments().lines().size();
        for (int i=lineNum;i<sz;i++){
            STLLine line = layer.segments().lines().get(i);
            if (synch.onStepLine())
                return false;
            gen.line(line);
            if (printEvent) ws.sendEvent(null,Events.LinePrint,0,i,"",null);
            lineCount++;
            if (lineCount%100==0)
                notify.log( Utils.toTimeString(timeInMs()/1000)+" Передано "+(i+1)+"/"+lineCount+" линий");
                notify.setProgress((i+1)*100/sz);
            }
        gen.flushAndWait();
        return true;
        }
    private int layerCount(){ return layerCount; }
    private int lineCount(){ return lineCount; }
    private long timeInMs(){ return new Date().getTime() - startTime; }
    private void testGenerator(USBProtocol protocol){
        if (gen==null)
            gen = new USBCommandGenerator(protocol,notify);
        }
    public void startUSBPrint(USBProtocol protocol) throws UNIException{
        testGenerator(protocol);
        notify.setProgress(0);
        gen.start();
        startTime = new Date().getTime();
        lineCount=0;
        layerCount=0;
        }
    public void finishUSBPrint() throws UNIException{
        gen.end(null);
        notify.log( Utils.toTimeString(timeInMs()/1000)+" Передано "+layerCount+" слоев, "+lineCount+" линий");         
        }
    
    public void copySLM3DtoUSB(ViewAdapter synch,USBProtocol protocol) throws UNIException{
        startUSBPrint(protocol);
        WorkSpace ws = WorkSpace.ws();
        SliceData data = ws.data();
        for(int i=0;i<data.size();i++){
            SliceLayer layer = data.get(i);
            copyLayerToUSB(synch,protocol,layer);
            layerCount++;
            notify.log( Utils.toTimeString(timeInMs()/1000)+" Передано "+(i+1)+" слоев, "+lineCount+" линий");
            notify.setProgress((int)((i+1)*100/data.size()));
            }
        finishUSBPrint();
        }
    private boolean copySLM3DtoUSBStopPoint(ViewAdapter synch,USBProtocol protocol) throws UNIException{
        WorkSpace ws = WorkSpace.ws();
        int nLayer= ws.global().mashine.CurrentLayer.getVal();
        int nline = ws.global().mashine.CurrentLine.getVal();
        if (nLayer==-1){
            copySLM3DtoUSB(synch,protocol);
            return true;
            }
        testGenerator(protocol);        
        startUSBPrint(protocol);
        SliceData data = ws.data();
        int sz = data.size();
        notify.setProgress((int)((nLayer+1)*100/sz));
        boolean first=true;
        for(int i=nLayer;i<sz;i++){
            SliceLayer layer = data.get(i);
            if (!first) gen.layer();
            int lsize= layer.segments().lines().size();
            for (int j=first ? nline : 0; j<lsize;j++){
                STLLine line = layer.segments().lines().get(j);
                gen.line(line);
                lineCount++;
                if (lineCount%100==0)
                    notify.log( Utils.toTimeString(timeInMs()/1000)+" Передано "+lineCount+" линий");
                if (synch.onStepLine())
                    return false;
                }
            gen.flushAndWait();
            layerCount++;
            first=false;
            notify.log( Utils.toTimeString(timeInMs()/1000)+" Передано "+(i+1)+" слоев, "+lineCount+" линий");
            notify.setProgress((int)((i+1)*100/sz));
            }
        finishUSBPrint();
        return true;
        }

    public boolean exportToGCode(ViewAdapter synch,BufferedWriter out) throws IOException{
        WorkSpace ws = WorkSpace.ws();
        SliceData data = ws.data();
        Settings local = ws.local();
        double x0 = ws.global().mashine.WorkFrameX.getVal()/2;
        double y0 = ws.global().mashine.WorkFrameY.getVal()/2;
        double zUp = ws.global().model.ZUp.getVal();
        int sz = data.size();
        double dz = local.model.VerticalStep.getVal();
        double layerZ = dz;
        notify.setProgress(0);
        lineCount=0;
        out.write("G91");
        out.newLine();
        out.write(String.format("G00 Z%-6.3f",zUp).replace(",","."));
        out.newLine();
        I_STLPoint2D last = new STLPoint2D(0,0);
        for(int i=0;i<sz;i++,layerZ+=dz){
            int count=0;
            SliceLayer layer = data.get(i);
            ArrayList<STLLine> lines = layer.segments().lines();
            GCodePoints current = null;
            out.write("( -------------- Слой "+(i+1)+"---------------------)");
            out.newLine();
            out.write("G91");
            out.newLine();
            Settings ls = layer.printSettings();
            if (ls == null) ls = local;
            //out.write("G900");
            //out.newLine();
            int groupIdx=0;
            int lineIdx = 0;
            if (groupIdx<layer.groupIndexes().size()-1)
                layer.groupIndexes().get(groupIdx);
            for (int j=0; j<lines.size();j++){
                STLLine line = lines.get(j);
                I_STLPoint2D one = line.one();
                I_STLPoint2D two = line.two();
                if (j==lineIdx){            // Начало очередной группы фрезерования
                    if (current!=null){
                        count += current.points.size()-1;
                        current.write(out,layerZ,x0,y0);
                        }
                    current = new GCodePoints("( -------------- Группа "+(groupIdx+1)+"---------------------)",last);
                    current.add(one);
                    /*
                    out.write("( -------------- Группа "+(groupIdx+1)+"---------------------)");
                    out.newLine();
                    out.write(String.format(Locale.US,"G30 G91 Z%-6.3f",20.0));
                    out.newLine();
                    out.write(String.format(Locale.US,"G00 X%-6.3f Y%-6.3f",one.x(),one.y()));
                    out.newLine();
                    out.write(String.format(Locale.US,"G30 G91 Z%-6.3f",-layerZ));
                    out.newLine();
                     */
                    last = one;
                    groupIdx++;
                    if (groupIdx<layer.groupIndexes().size()-1)
                        lineIdx = layer.groupIndexes().get(groupIdx);
                    }
                if (!last.equalsAbout(one)){
                    count += current.points.size()-1;
                    current.write(out,layerZ,x0,y0);
                    current = new GCodePoints("( -------------- Перемещение в группе "+(groupIdx+1)+"---------------------)",last);
                    current.add(one);
                    /*
                    out.write("( -------------- Перемещение в группе "+(groupIdx+1)+"---------------------)");
                    out.newLine();
                    out.write(String.format(Locale.US,"G30 G91 Z%-6.3f",20.0));
                    out.newLine();
                    out.write(String.format(Locale.US,"G00 X%-6.3f Y%-6.3f",one.x(),one.y()));
                    out.newLine();
                    out.write(String.format(Locale.US,"G30 G91 Z%-6.3f",-layerZ));
                    out.newLine();
                     */
                    }
                //out.write(String.format(Locale.US,"G01 X%-6.3f Y%-6.3f",two.x(),two.y()));
                //out.newLine();
                current.add(two);
                last = two;
                if (synch.onStepLine()){
                    out.close();
                    return false;
                    }
                lineCount++;
                }
            count += current.points.size()-1;
            current.write(out,layerZ,x0,y0);
            notify.setProgress((int)((i+1)*100/sz));
            //----------- Для проверки группировки ------------------------------------------------------
            //notify.notify(Values.info,"Линий="+lines.size()+","+count);
            }
        out.write(String.format(Locale.US,"G30\nG91 Z%-6.3f",20.0));
        out.newLine();
        out.write("M30");
        out.newLine();
        notify.log("Передано "+sz+" слоев, "+lineCount+" линий");
        out.close();
        return true;
        }
    }
