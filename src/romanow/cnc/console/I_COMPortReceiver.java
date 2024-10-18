package romanow.cnc.console;

/**
 * Created by romanow on 28.03.2018.
 */
public interface I_COMPortReceiver {
    public void onVolume(double val);       // Ответ коианды установки мощности лазера
    public void onTemperature(double val);  // Ответ команды запроса температуры лазера
    public void onOther(String mes);        // Нестандартные или ошибочные ответы
    public void onState(int state);         // Ответ команды запроса слова состояния
    public void onErrorCode(int state);     // Код ошибки лазера
    public void onHZ(double state);         // Ответ команды запроса частоты испульсов
    public void onMS(double state);         // Ответ команды дапроса длительности испульсов
    public void onPower(double state);      // Ответ
    }
