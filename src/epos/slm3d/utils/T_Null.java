package epos.slm3d.utils;

/**
 * Created by romanow on 24.02.2017.
 */
/** Контейнер для ошибок */
public class T_Null<T> {
    /** Признак валидности */
    private boolean valid=true;
    /** Сохраненное исключение */
    private UNIException exept=null;
    /** Ссылка на валидный объект */
    private T ref=null;
    /** Констуктор для валидного значения */
    public T_Null(T ref){
        valid = true;
        this.ref = ref;
        }
    /** Невалидное значение по умолчанию */
    public T_Null(){
        valid = false;
        exept = null;
        }
    /** Невалидное значение - исключение */
    public T_Null(UNIException ex){
        valid = false;
        exept = ex;
    }
    /** Получение ссылки или null */
    public T refOrNull(){			    // Получение ссылки на объект или null
        return valid ? ref : null;
        }
    public UNIException exception(){
        if (exept==null)
            return UNIException.bug("исключение не сохранено");
        else
            return exept;
        }
    /** Получение ссылки или генерация исключения */
    public T refOrExept() throws UNIException {
        if (!valid)
            throw exception();
        return ref;
        }
    public boolean valid(){ return valid; }

}
