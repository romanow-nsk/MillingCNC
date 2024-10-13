package epos.slm3d.utils;

/**
 * Created by romanow on 02.12.2017.
 */
public class Values {
    public final static int version=1;
    public final static int release=1;
    public final static OwnDateTime releaseDate = new OwnDateTime(13,10,2024);
    public static String getVersion(){
        return "Milling CNC NSTU  version "+version+"."+release+" "+releaseDate.dateToString();
        }
    /** Классы сообщений в логе*/
    public final static int info=0;
    public final static int important=1;
    public final static int warning=2;
    public final static int error=3;
    public final static int fatal=4;
    public final static String infoLevels[]={"прочее","важное","предупр.","ошибка","сбой"};
    /** Типы 2D точек */
    public final static int classIdRefered=0;
    public final static int classId2D=1;
    public final static int classId2dFloat=2;
    /** Категории пользоователей */
    public final static int userGuest=0;
    public final static int userOperator=1;
    public final static int userConstructor=2;
    public final static int userAdmin=3;
    /** состояния программы по данным */
    public final static int NoData=0;           // Нет данных
    public final static int Loaded=1;           // Модель заружена
    public final static int Sliced=2;           // Растр загружен
    public final static int Changed =3;         // Растр изменен (тест сгенерирован)
    public final static String DataStates[]={"Нет данных","Модель","Растр","Растр+"};
    public final static String SettingsFileName="m3dSettings.xml";
    /** коэффициент заполнения моделью пространства при автомасштабировании +/-1 */
    public final static double DefaultModelScale=1;
    /** размер рабочего поля в мм*/
    public final static double WorkFieldSize=100;
    /** точность приближения к pi/2 в радианах */
    public final static double AxesGrad=0.5*Math.PI/180;
    /** точность фиксации прямого угла - синус-косинус */
    public final static double SinCosIs0=0.0001;
    /** точность поиска соседней точки при склеивании контура - мм */
    public final static double PointDiffenerce=0.00001;
    /** точность - считать точки одинаковыми - мм */
    public final static double EqualDifference=0.00000000001;
    /** точность поиска соседней точки удалении нечета мм */
    public final static double PointCrossDiffenerce=0.1;
    /** точность сравнения тангенсов углов при сглаживании  */
    public final static double EvenAngleDiffenerce=0.01;
    /** количество подряд идущих отрезков при сглаживаии  */
    public final static double EvenSeqLenght=2;
    /** точность поиска соседней точки при оптимизации */
    public final static int OptimizeRasterCount=30;
    /** Задержка USB между передачей и приемом  - мс */
    public final static int USBSendReceivePause=10;
    /** Глубина стека исключения при выводе */
    public final static int StackTraceDeepth=8;
    /** Количество потоков слайсинга */
    public final static int SliceThreadNum=5;
    /** Количество оптимизируемых перемещений */
    public final static int OptimizeSeqSize=2000;
    /** UDP IP*/
    public final static String UdpIP="192.168.1.100";
    /** UDP тайм-аут - сек */
    public final static int UdpTimeOut=30;
    /** интервал опроса принтера */
    public final static int PrinterStateLoopDelay=5;
    /** интервал восттановления соединения при фатальной ошибке */
    public final static int PrinterReconnectDelay=20;
    /** Графический индекс при оптимизации */
    public final static boolean GraphIndexForOptimize=true;
    /** Размер таблиц графического индекса при оптимизации по координате */
    public final static int GraphIndexSize=50;
    /** версия формата файла */
    public final static int FileFormatVersion = 3;      // 0 для тестирования
    // версия 2 - добавлен SliceLayer.angle
    // Добавлено явное имя слоя в сингатуру
    /** Коэффициент длины перемещения при непрерывном слайсинге в шагах растра */
    public final static double ContinuousK = 1.3;
    /** Размер блока usb 2.0*/
    public final static int USB2BlockByteSize=64;
    /** Размер блока usb 3.0*/
    public final static int USB3BlockByteSize=512;
    /** Размер блока UDP */
    public final static int UDPBlockByteSize=512;
    /** Строить контуры из цепочек, false - только слияние контуров из отдельных отрезков */
    public final static boolean CreateLoopsFull=false;
    /** Минимальное расстояние привязки - mm*/
    public final static double NearestPointDistance=1.0;
    /** Интервал двойного клика - ms*/
    public final static long DoubleClickInMS=300;
    /** Начальный пароль админа */
    public final static String FirstAdminPass="admin";
    /** Точность попадания в центр объекта при рисовании - мм */
    public final static double FindPointDistance = 1.;
    /** Число отрезков эллипса дл печати */
    public final static int EllipseLineCount = 100;
    /** Номер COM-порта */
    public final static String COMPort="COM3";
    public static void main(String argv[]){
        System.out.println(Double.parseDouble("0.00"));
        }
}
