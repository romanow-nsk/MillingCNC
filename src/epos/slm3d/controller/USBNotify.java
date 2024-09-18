package epos.slm3d.controller;

import epos.slm3d.commands.Command;
import epos.slm3d.commands.USBCommandFactory;
import epos.slm3d.utils.Events;
import epos.slm3d.settings.WorkSpace;
import epos.slm3d.utils.I_Notify;
import epos.slm3d.utils.UNIException;
import epos.slm3d.utils.Values;

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
        notify.notify(Values.error,"Ошибка принтера: "+nameByCode(code)+": "+USBCodes.answerName(data[0]));
        if (USBCodes.USBAnswerHard[data[0]]==0 )      // Не требуют перегрузки нитерфейса
            return;
        errorsCounter++;
        if (errorsCounter == USBCodes.USBErrorsCount){
            WorkSpace.ws().sendEvent(Events.USBFatal);
            errorsCounter=0;
            }
        }
    @Override
    public void onFatal(int code, String mes) {
        notify.notify(Values.fatal,"Ошибка интерфейса: "+nameByCode(code)+ " "+mes);
        WorkSpace.ws().sendEvent(Events.USBFatal);
        }
}
