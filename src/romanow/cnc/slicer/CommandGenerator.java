package romanow.cnc.slicer;

import romanow.cnc.commands.Command;
import romanow.cnc.stl.STLLine;
import romanow.cnc.stl.STLLineGroup;
import romanow.cnc.stl.STLLoop;
import romanow.cnc.utils.UNIException;

import java.util.ArrayList;

/**
 * Created by romanow on 04.12.2017.
 */
public abstract class CommandGenerator {
    private int addr=0;
    public int addr(){ return addr; }
    public void addAddr(int sz){ addr+=sz; }
    public abstract void lineGroup() throws UNIException;
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
