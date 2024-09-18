package epos.slm3d.slicer;

import epos.slm3d.commands.Command;
import epos.slm3d.stl.STLLine;
import epos.slm3d.stl.STLLineGroup;
import epos.slm3d.stl.STLLoop;
import epos.slm3d.utils.UNIException;

import java.util.ArrayList;

/**
 * Created by romanow on 04.12.2017.
 */
public abstract class CommandGenerator {
    private int addr=0;
    public int addr(){ return addr; }
    public void addAddr(int sz){ addr+=sz; }
    /** команда печати линии */
    public abstract void line(STLLine line) throws UNIException;
    /** команда смены слоя */
    public abstract void layer() throws UNIException;
    /** инициализация генератора команд */
    public abstract void init() throws UNIException;
    /** начало печати  */
    public abstract void start() throws UNIException;
    /** отмена печати  */
    public abstract void cancel() throws UNIException;
    /** окончание печати  с передачей статистики */
    public abstract void end(SliceRezult rez) throws UNIException;
    /** передача контуров слоя */
    public abstract void loops(ArrayList<STLLoop> loops) throws UNIException;
    /** передача исходного сечения  */
    public abstract void lines(STLLineGroup lines);
    /** любая команда принтера */
    public abstract void command(Command cmd) throws UNIException;
    /** закрытие генератора */
    public abstract void close();
    /** ошибка слайсирования */
    public void onError(SliceError errData){}
    /** окончание слоя с передачей статистики */
    public void layerFinished(SliceRezult rez, double z,double angle){}
}
