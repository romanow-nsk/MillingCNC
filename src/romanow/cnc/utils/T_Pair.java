package romanow.cnc.utils;

/**
 * Created by romanow on 17.02.2017.
 */
/** Шаблон пары */
public class T_Pair<F,S> {
    private F first;
    private S second;
    public T_Pair(F fst, S sec){
        first = fst;
        second = sec;
    }
    public F _1(){ return first; }
    public S _2(){ return second; }
}
