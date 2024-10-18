package romanow.cnc.commands;

import romanow.cnc.controller.USBCodes;
import romanow.cnc.utils.UNIException;

import java.util.ArrayList;

/**
 * Created by romanow on 02.12.2017.
 */
public class USBCommandFactory {
    private Command lst[]={
            //----------------- управление печатью --------------------------------------
            new Command(USBCodes.StopPrint,"Отменить печать"),
            new Command(USBCodes.SuspendPrint,"Приостановить печать"),
            new Command(USBCodes.ResumePrint,"Возобновить печать"),
            new Command(USBCodes.StartPrint,"Начало печати"),
            new Command(USBCodes.StartLayer,"Начало слоя"),
            //---------------- команды печати -------------------------------------------
            new CommandLine(),
            new CommandLayer(),
            //new CommandIntList(M3DValues.cmdLayer,"Засыпка слоя",2),
            //----------------- управление печатью --------------------------------------
            new Command(USBCodes.EndOfPrint,"Конец файла печати"),
            new Command(USBCodes.IsReady,"Состояние принтера"),
            new Command(USBCodes.GetAvailMemory,"Свободная память"),
            new Command(USBCodes.GetLineCount,"Напечатано слоев/строк"),
            new Command(USBCodes.StopPrintAfterLayer,"Отменить после слоя"),
            new Command(USBCodes.SuspendPrintAfterLayer,"Приостановить после слоя"),
            //----------------- сбросы и восстановление ----------------------------------------------
            new Command(USBCodes.HardReset,"Жесткий сброс"),
            new Command(USBCodes.SoftReset,"Мягкий сброс"),
            new Command(USBCodes.USBReset,"Сброс интерфейса"),
            new Command(USBCodes.ShutDown,"Выключение"),
            new Command(USBCodes.OffLine,"Автоном"),
            //----------------- параметры ---------------------------------------------
            new CommandFloat(USBCodes.MarkingMicroStepsMarkInt,"Время прожига микро-точек"),
            new CommandInt(USBCodes.ControlNextLayerMovingM4Step,"Шагов мотора с образцом"),
            new CommandInt(USBCodes.ControlNextLayerMovingM3Step,"Шагов мотора с порошком"),
            new CommandInt(USBCodes.DelaysMovingPenJumpDelay,"Задержка перед включением излучения"),
            new CommandInt(USBCodes.DelaysMovingPenMarkDelay,"Задержка после выключения излучения"),
            //------------------ прочее ----------------------------------------------------------------
            //new Command(USBCodes.GetShortStatus,"Состояние кратко"),
            //new Command(USBCodes.ReadLog,"Читать лог-файл"),
            //new Command(USBCodes.ReadMessages,"Читать сообщения"),
            //new Command(USBCodes.GetExtendedStatus,"Состояние подробно"),
            new Command(USBCodes.NOP,"Нет операции"),
            //---------------- команды технологические -------------------------------------------
            new CommandIntList(USBCodes.StartTest,"Калибровка мотора",1),
            new CommandIntList(USBCodes.StartMainTest,"Полный тест оборудования",2),
            new CommandIntList(USBCodes.GetMotorStatus,"Запрос состояния мотора",1),
            new CommandIntList(USBCodes.SetMotorParam,"Задание параметров мотора",4),
            new CommandIntList(USBCodes.GoMotorPos,"Запуск мотора на +-N шагов",2),
            new CommandIntList(USBCodes.StopMotor,"Останов мотора",1),
            new CommandIntList(USBCodes.ChangeLayerMotorsEnable,"Включение моторов при смене слоя",1),
            new Command(USBCodes.GetBeamStatus,"Cтатус дистанционного управления лазером"),
            new CommandIntList(USBCodes.BeamPowerOn,"Включение сетевого напряжения лазера",1),
            new CommandIntList(USBCodes.BeamStart,"Запуск лазера",1),
            new CommandIntList(USBCodes.BeamModOn,"Включение модуляции лазера",1),
            new CommandIntList(USBCodes.BeamEmisOn,"Включение эмиссии лазера",1),
            new CommandIntList(USBCodes.BeamSetGuide,"Включение указателя местоположения",1),
            new CommandIntList(USBCodes.BeamResetErr,"Сброс ошибки лазера",1),
            new CommandIntList(USBCodes.SetPosXY_hex,"Задание HEX позиции",2),
            new CommandIntList(USBCodes.SetPosXY_Q31,"Задание Q31 позиции",2),
            new CommandIntList(USBCodes.SetLineCounter,"Установка номеров слоя/линии",2),
            new CommandIntList(USBCodes.OxygenSensorSetParam,"Управление опросом датчиков кислорода",3),
            new CommandIntList(USBCodes.OxygenSensorGetData,"Опрос датчика кислорода",1),
            new CommandIntList(USBCodes.SetMirrorPhysLimits,"Oграничения поворота зеркал 5..65530",4),
            new CommandIntList(USBCodes.FillCorrectionTbl,"Заполнение корректирующей таблицы константой",1),
            new CommandIntList(USBCodes.SetCorrectionElement,"Установка коэффициента в таблице корректировки",1),
            new CommandIntList(USBCodes.SetBlockCorrElements,"Блочная установка коэффициентов",4),
            new CommandIntList(USBCodes.SetDropParameter,"Изменение скорости у начала/конца линии",2),
            //----------------------------------- Убраны в 1.1.
            //new CommandParam(-1,"Установка параметра печати"),
            //new CommandParam(USBCodes._DelaysLaserOn,"Время включения лазера"),
            //new CommandParam(USBCodes._DelaysLaserOff,"Время выключения лазера"),
            //new CommandParam(USBCodes._DelaysMovingPenJumpDelay,"Время установки в заданную позицию"),
            //new CommandParam(USBCodes._DelaysMovingPenMarkDelay,"Время активации режима прожига"),
            //new CommandParam(USBCodes._DelaysMovingPenStrokeDelay,"Время установки на заданную строку"),
            //new CommandParam(USBCodes._MarkingMicroStepsMarkInt,"Скорость прожига (мм/c)"),
            //new CommandParam(USBCodes._MarkingMicroStepsJumpInt,"Время перемещения  между микро-точками"),
            //new CommandParam(USBCodes._MarkingMarkTailsInputInt,"Отступ от начала линии"),
            //new CommandParam(USBCodes._MarkingMarkTailsOutputInt,"Отступ от конца линии"),
            //new CommandParam(USBCodes._PulseLaserFrequence,"Частота лазера"),
            //new CommandParam(USBCodes._PulseLaserPumpPower,"Процент мощности лазера"),
            //new CommandParam(USBCodes._PulseLaserPulseType,"Тип модуляции"),
            //new CommandParam(USBCodes._PulseLaserPulseSuppress,"Подавление ... лазера"),
            //new CommandParam(USBCodes._PulseDACFrequence,"Частота...лазера"),
            //new CommandParam(USBCodes._ControlNextLayerMovingM4Step,"Шагов мотора с образцом (0.01 мм)"),     
            //new CommandParam(USBCodes._ControlNextLayerMovingM3Step,"Шагов мотора с порошком  (0.01 мм)"),     
            //---------------------------- Анахронизм -----------------------------------------------
            //new CommandFire(),
            //new CommandMove(),
        };
    public ArrayList<String> commandList(){
        ArrayList<String> out = new ArrayList<>();
        for (Command xx : lst)
            out.add(xx.name());
        return out;
        }
    public boolean findCommand(int code0){
        for(int i=0;i<lst.length;i++){
            if (!lst[i].canFind())
                continue;
            if (lst[i].code == code0)
                return true;
            }
        return false;
        }
    public Command getCommand(int code0) throws UNIException {
        for(int i=0;i<lst.length;i++)
            if (lst[i].code == code0){
                try {
                    Command oo = (Command)lst[i].getClass().newInstance();
                    oo.name(lst[i].name());
                    oo.code = lst[i].code;
                    oo.signature = lst[i].signature;
                    oo.dataSize = lst[i].dataSize;
                    oo.idx = lst[i].idx;
                    return oo;
                    }
                    catch(IllegalAccessException e1){ throw UNIException.bug("Не создается "+lst[i].getClass().getSimpleName()); }
                    catch(InstantiationException e2){ throw UNIException.bug("Не создается "+lst[i].getClass().getSimpleName()); }
                }
             return null;
        }
    public Command getCommand(String tName) throws UNIException {
        for(int i=0;i<lst.length;i++)
            if (lst[i].name().equals(tName)){
                try {
                    Command oo = (Command)lst[i].getClass().newInstance();
                    oo.name(lst[i].name());
                    oo.code = lst[i].code;
                    oo.signature = lst[i].signature;
                    oo.dataSize = lst[i].dataSize;
                    oo.idx = lst[i].idx;
                    return oo;
                }
                catch(IllegalAccessException e1){ throw UNIException.bug("Не создается "+lst[i].getClass().getSimpleName()); }
                catch(InstantiationException e2){ throw UNIException.bug("Не создается "+lst[i].getClass().getSimpleName()); }
            }
        return null;
        }
}
