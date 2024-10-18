package romanow.cnc.m3d;

import javax.swing.*;
import java.util.Date;

/**
 * Created by romanow on 06.12.2017.
 */
/** пустой адаптер для связи с View */
public class ViewAdapter {
    /** Панель для рисования */
    public final JPanel FLD;
    /** размер панели */
    private int sz;
    /** масштаб рисования */
    public volatile double mas;
    /** признак - процесс выполняется */
    private  volatile boolean running;
    /** признак - процесс завершен */
    private volatile boolean finish;
    /** признак - процесс приостановлен */
    private volatile boolean pause;
    /** задержка (мс) после отрисовки линии */
    private int lineDelay = 0;
    /** задержка (мс) после отрисовки слоя */
    private int layerDelay = 10;
    /** системное время старта */
    private long startTime=0;

    public ViewAdapter(JPanel fld) {
        FLD = fld;
        if (fld!=null)
            sz = fld.getWidth()/2;
        mas = (double)sz;
        running = false;
        finish = false;
        pause = false;
        }
    public synchronized void finish(){
        pause=false;
        finish=true;
        running = false;
        }
    public synchronized void pause(boolean ps){
        pause = ps;
        }
    public synchronized boolean isFinish(){
        return finish;
        };
    public synchronized boolean isPause(){
        return pause;
        };
    public synchronized boolean changePause(){
        pause = !pause;
        return pause;
        }
    public synchronized boolean isRunning(){
        return running;
        };
    public int sz(){ return sz; }
    private boolean pauseLoop(){
        while (!isFinish() && isPause()){
            try { Thread.sleep(1000); } catch (InterruptedException ex) {}
            }
        return finish;
        }
    public boolean onStepLayer(){
        return delay(layerDelay);
        }
    public boolean onStepLine(){
        return delay(lineDelay);
        };
    public boolean delay(int delay){
        if (pauseLoop()) return true;
        if (delay==0)
            return finish;
        try { Thread.sleep(delay); } catch (InterruptedException ex) {}        
        return finish;
        };
    public void start(int lineDelay0, int layerDelay0 ){
        lineDelay=lineDelay0;
        layerDelay=layerDelay0;
        start();
        }
    public void start(){
        running=true;
        finish=false;
        pause=false;
        startTime = new Date().getTime();
        }
    public long timeInMs(){
        return new Date().getTime()-startTime;
        }
}
