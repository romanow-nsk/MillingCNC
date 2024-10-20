package romanow.cnc.controller;

import romanow.cnc.commands.Command;
import romanow.cnc.commands.USBCommandFactory;
import romanow.cnc.utils.Events;
import romanow.cnc.settings.WorkSpace;
import romanow.cnc.utils.I_Notify;
import romanow.cnc.utils.UNIException;
import romanow.cnc.Values;

/**
 * Created by romanow on 29.12.2017.
 */
public class USBNotify implements USBBack{
    private int errorsCounter=0;
    private I_Notify notify;
    private USBCommandFactory factory=null;
    public String nameByCode(int code){
        String ss = "...";
        try {
            Command cmd = factory.getCommand(code);
            if (cmd==null) ss="???";
            else ss = cmd.name();
            } catch (UNIException e) {}
        return ss;
        }
    public USBNotify(USBCommandFactory factory0,I_Notify notify0){
        factory = factory0;
        notify = notify0;
        }
    @Override
    public void onSuccess(int code, int[] data) {
        errorsCounter=0;
        notify.log("Выполнено: "+nameByCode(code));
        }
    @Override
    public void onError(int code, int[] data) {
        WorkSpace ws = WorkSpace.ws();
        notify.notify(Values.error,"Ошибка принтера: "+nameByCode(code)+": "+USBCodes.answerName(data[0]));
        if (USBCodes.USBAnswerHard[data[0]]==0 )      // Не требуют перегрузки нитерфейса
            return;
        errorsCounter++;
        if (errorsCounter == USBCodes.USBErrorsCount){
            ws.sendEvent(Events.USBFatal);
            errorsCounter=0;
            }
        }
    @Override
    public void onFatal(int code, String mes) {
        WorkSpace ws = WorkSpace.ws();
        notify.notify(Values.fatal,"Ошибка интерфейса: "+nameByCode(code)+ " "+mes);
        ws.sendEvent(Events.USBFatal);
        }
}
