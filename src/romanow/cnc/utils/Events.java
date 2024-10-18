package romanow.cnc.utils;

/**
 * Created by romanow on 18.09.2018.
 */
public class Events {
    //-------------------------------------------- События ---------------------------------------------------
    public final static int Model=0;        // Открытие/закрытие модели
    public final static int FileState=1;    // Изменение стауса модели-файла
    public final static int Print=3;        // Процесс печати - состояние
    public final static int Layer=4;        // Смена слоя
    public final static int Motors=5;       // Моторы подвинулись
    public final static int Settings=6;     // Установки изменились и сохранились
    public final static int Close=7;        // Закрытие приложения
    public final static int USBFatal=8;     // Фатальная ошибка интерфейса
    public final static int Notify=9;       // Сообщение в окно
    public final static int LogOut=10;      // Закрытие сеанса
    public final static int Rotate=11;      // Поворот
    public final static int NewData=12;     // Растр изменился
    public final static int LayerPrint=13;  // Печать линии
    public final static int LinePrint=14;   // Печать линии
    public final static int Clock=15;       // Часы
    //-------------------------------------------- Состояния принтера в программе  -------------------------------------
    public final static int PStateStandBy=0;
    public final static int PStateWorking=1;        
    public final static int PStateSuspend=2;        
    public final static int PStateFullLayer=3;        
    public final static int PStateFail=4;        
    public final static int PStateCancel=5;        
    public final static String PStates[]={"Простаивает","Печать","Приостановлен","После слоя","Авария","Отмена"};
}
