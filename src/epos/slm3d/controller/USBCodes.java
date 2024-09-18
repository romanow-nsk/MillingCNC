package epos.slm3d.controller;

/**
 * Created by romanow on 20.12.2017.
 */
public class USBCodes {
    //------------------------------------------------------------------------------------------------------------------
    public final static int USBErrorsCount=5;             // Подряд идущих ошибок до события
    public final static int USBEmulatorInetPort=7778;
    /** Тайм-аут ожидания состояния в команде прожига линии */
    public final static int BurnLineTimeOut=20;
    /** Тайм-аут ожидания состояния в команде смены слоя  */
    public final static int LayerTimeOut=60;
    public final static byte EndPointOut=0x01;
    public final static byte EndPointIn=(byte)0x81;
    //------------------------------коды прототипа -----------------------------------------------------------------------------------
    /** Команда смены слоя */
    public final static int СmdLayer=0x08000;
    /** Команда линия - перемещение и прожиг  */
    public final static int СmdLine=0x08001;
    /** Команда перемещения луча - удалена*/
    //public final static int СmdMove=0x1;
    /** Команда прожига - удалена  */
    //public final static int СmdFire=0x101;
    //------------------------------------------------------------------------------------------------------------------
    /** Нет операции */
    public final static int NOP = 0x0200;
    /** Жесткий сброс */
    public final static int HardReset=0x0201;
    /** Мягкий сброс с синхронизированной приостановкой печати */
    public final static int SoftReset=0x0202;
    /** Мягкий сброс протокола для длинных команд */
    public final static int USBReset=0x0203;
    /** Приостановка печати  */
    public final static int SuspendPrint=0x0004;
    /** Возобновление печати  */
    public final static int ResumePrint=0x0005;
    /** Старт отложенной печати  */
    public final static int StartPrint=0x0006;
    /** Остановка печати без возобновлния */
    public final static int StopPrint=0x0007;
    /** Остановка печати после завершения слоя */
    public final static int StopPrintAfterLayer=0x0008;
    /** Приостановка печати после завершения слоя */
    public final static int SuspendPrintAfterLayer=0x0009;
    /** Запрос свободной памяти */
    public final static int GetAvailMemory=0x000B;
    /** Выполнить стартовый тест */
    public final static int StartMainTest=0x000E;
    /** Текущее состояние принтера */
    public final static int IsReady=0x000F;
    /** Получить состояние - кратко */
    //public final static int GetShortStatus=0x0010;
    /** Передача параметра  - Смещение, значение int-float - удалена*/
    public final static int SendParamInt=0x0011;
    /** Передача параметра  - Смещение, значение */
    //public final static int SendParamFloat=0x0012;
    /** Завершение работы */
    public final static int ShutDown=0x0013;
    /** Перевод в нирвану */
    public final static int OffLine=0x0014;
    /** Конец файла печати */
    public final static int EndOfPrint=0x0015;
    /** Выполнить тест оборудования */
    public final static int StartTest=0x0016;
    /** Старт вывода слоя */
    public final static int StartLayer=0x0017;
    /** Остановка печати без возобновлния */
    /** Запрос статуса мотора */
    public final static int GetMotorStatus=0x0020;
    /** Задание границ движения мотора */
    public final static int SetMotorParam=0x0021;
    /*  Остановка мотора  */
    public final static int StopMotor=0x0022;
    /*  запуск мотора на N шагов  */		
    public final static int GoMotorPos=0x0023;
    /*  включение моторов при смене слоя  */		
    public final static int ChangeLayerMotorsEnable = 0x0024;		
    /*  статус дистанционного управления лазером */
    public final static int GetBeamStatus=0x0030;
    /*  команды дистанционного управления лазером */
    public final static int BeamPowerOn=0x0033;
    public final static int BeamStart=0x0034;
    public final static int BeamModOn=0x0035;
    public final static int BeamEmisOn=0x0036;
    public final static int BeamSetGuide=0x0037;
    public final static int BeamResetErr=0x0038;
   /* команды передачи координат */   
    public final static int SetPosXY_hex=0x0040;
    public final static int SetPosXY_Q31=0x0041;
    public final static int SetBurnLine=0x0050;
    public final static int GetLineCount=0x0051;
    public final static int SetBurnLineBlock=0x0052;
    public final static int SetLineCounter=0x0053;
    //----------------- Клманды параметров
    public final static int DelaysMovingPenJumpDelay=0x0062;
    public final static int DelaysMovingPenMarkDelay=0x0063;    
    public final static int MarkingMicroStepsMarkInt = 0x0065;
    public final static int ControlNextLayerMovingM4Step = 0x0069;        //0.01 мм
    public final static int ControlNextLayerMovingM3Step  =  0x006A;      //0.01 мм
    /** Управление опросом датчиков кислорода */
    public final static int OxygenSensorSetParam = 0x0080;
    /** Опрос датчика кислорода */
    public final static int OxygenSensorGetData = 0x0081;
    /** Oграничения поворота зеркал 5..65530 */
    public final static int SetMirrorPhysLimits = 0x0085;
    /**  Заполнение корректирующей таблицы константой */
    public final static int FillCorrectionTbl = 0x0086;
    /**  Индивидуальная установка коэффициента в таблице корректировки */
    public final static int SetCorrectionElement = 0x0087;
    /**  Блочная установка коэффициентов в таблице корректировки */
    public final static int SetBlockCorrElements = 0x0088;
    /**  Изменение скорости у начала/конца линии */
    public final static int SetDropParameter = 0x0074;
    //----------------- смещения команд параметров = индексы в Settings
    public final static int _MarkingMicroStepsMarkInt = 0;
    public final static int _MarkingMicroStepsJumpInt = 1;
    public final static int _MarkingMarkTailsInputInt = 2;
    public final static int _MarkingMarkTailsOutputInt = 3;
    public final static int _PulseLaserFrequence = 4;
    public final static int _PulseLaserPumpPower = 5;
    public final static int _PulseLaserPulseType = 6;
    public final static int _PulseLaserPulseSuppress  = 7;
    public final static int _PulseDACFrequence = 8;
    public final static int _DelaysLaserOn = 9;
    public final static int _DelaysLaserOff = 10;
    public final static int _DelaysMovingPenJumpDelay = 11;
    public final static int _DelaysMovingPenMarkDelay = 12;
    public final static int _DelaysMovingPenStrokeDelay = 13;
    public final static int _ControlNextLayerMovingM4Step = 14;        //0.01 мм
    public final static int _ControlNextLayerMovingM3Step  =  15;      //0.01 мм

    /** Передача блока команд печати */
    //public final static int SendPrintBlock=0x100;
    /** Передача блока параметров  */
    //public final static int SendParameterBlock=0x106;
    /** Повторение блока команд (счетчик==0 - до остановки) */
    //public final static int StartPrintBlockRepeat=0x102;
    /** Получить состояние - подробно */
    public final static int GetExtendedStatus=0x103;
    /** Читать лог */
    public final static int ReadLog=0x104;
    /** Читать сообщения */
    public final static int ReadMessages=0x105;
    //------------------------------------------------------------------------------------------------------------------
    public final static int ACK_OK=0;
    public final static int ACK_ERROR=1;
    public final static int ACK_FATAL=2;
    /** Не выполняется во время печати */
    public final static int ACK_WORKING=3;
    /** Временно занят */
    public final static int ACK_BUSY=4;
    /** Недопустимое значение параметра или набор параметров */
    public final static int ACK_ILLEGAL=5;
    /** Функция не поддерживается */
    public final static int ACK_NOFUN=6;
    /** Не хватает памяти */
    public final static int ACK_NOMEM=7;
    /** Не готов */
    public final static int ACK_NOREADY=8;
    /** исключение - ответ драйвера */
    public final static int ACK_EXCEPTION=-1;
    public final static String USBAnswerNames[]={"Принято","Ошибка","Авария","идет печать","Занят","Параметры?","Не поддерживается","Нет памяти","Не готов"};
    public final static int USBAnswerHard[]={0,1,1,0,0,0,0,0,0};
    public  static String answerName(int state){
        if (state<0 || state>=USBAnswerNames.length)
            return "Состояние:"+state;
        return USBAnswerNames[state];
        }
    //------------------------------------------------------------------------------------------------------------------
    public final static int FATAL_TIMEOUT=0;
    public final static int FATAL_EXCEPTION=1;
    //-------------------------------------------------------------------------------------
    /** Готов */
    public final static int  STATE_READY=0;
    /** Фатальная ошибка */
    public final static int  STATE_FATAL=1;
    /** исправимая ошибка */
    public final static int  STATE_ERROR=2;
    /** Печать */
    public final static int  STATE_PRINT=3;
    /** Повторнеие */
    // public final static int  STATE_REPEAT=4;
    /** Временно занят */
    public final static int  STATE_BUSY =5;
    /** Автоном */
    public final static int  STATE_OFFLINE=6;
    /** Тестирование оборудования */
    public final static int  STATE_TEST=7;
    /** Завершение работы */
    public final static int  STATE_SHUTDOWN=8;
    /** инициализация - сброс */
    public final static int  STATE_INIT=9;
    /** Печать приостановлена */
    public final static int  STATE_SUSPEND=10;
    /** Печать не выполняется */
    public final static int  STATE_STANDBY=11;
    /** Ожидание данных */
    public final static int  STATE_WAITFORDATA=12;
    public final static String USBStateNames[]={"Готов к приему команд","Авария","Ошибка","Печать","","Занят","Автоном","Тест","Завершение работы","","Приостановлен","Простаивает","Ждет данных",};
    public  static String stateName(int state){
        if (state<0 || state>=USBStateNames.length)
            return "???";
        return USBStateNames[state];
        }
    //------------------------------------------------------------------------------------------------------------------
    /** сигнатуры форматов команд */
    public final static int  FORMAT_EMPTY=0;
    public final static int  FORMAT_INT=1;
    public final static int  FORMAT_POINT=2;
    public final static int  FORMAT_LINE=3;
}
