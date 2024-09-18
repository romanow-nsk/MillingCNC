package epos.slm3d.commands;

import epos.slm3d.utils.UNIException;

import java.util.ArrayList;

/**
 * Created by romanow on 02.12.2017.
 */
public class M3DCommandFactory {
    private Command lst[]={new CommandMove(), new Command2Move(), new CommandFire(),new CommandLayer()};
    public ArrayList<String> commandList(){
        ArrayList<String> out = new ArrayList<>();
        for (Command xx : lst)
            out.add(xx.name());
        return out;
        }
    public boolean findCommand(int code0){
        for(int i=0;i<lst.length;i++)
            if (lst[i].code == code0 && lst[i].canFind())
                return true;
        return false;
        }
    public Command getCommand(int code0) throws UNIException {
        for(int i=0;i<lst.length;i++)
            if (lst[i].code == code0){
                try {
                    Object oo = lst[i].getClass().newInstance();
                    return (Command)oo;
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
                    Object oo = lst[i].getClass().newInstance();
                    return (Command)oo;
                }
                catch(IllegalAccessException e1){ throw UNIException.bug("Не создается "+lst[i].getClass().getSimpleName()); }
                catch(InstantiationException e2){ throw UNIException.bug("Не создается "+lst[i].getClass().getSimpleName()); }
            }
        return null;
    }
}
