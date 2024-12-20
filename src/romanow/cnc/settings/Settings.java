package romanow.cnc.settings;

import romanow.cnc.io.I_File;
import romanow.cnc.Values;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by romanow on 01.12.2017.
 */
public class Settings implements I_File{
    public SliceSettings slice = new SliceSettings();
    public ModelSettings model = new ModelSettings();
    public MashineSettings mashine = new MashineSettings();
    public Statistic statistic = new Statistic();
    public ArrayList<UserProfile> userList = new ArrayList();
    public boolean fullScreen = false;
    //-----------------------------------------------------------------------------------------------
    public Settings(){}

    public void setNotNull(){
        mashine.setNotNull();
        slice.setNotNull();
        model.setNotNull();
        statistic.setNotNull();
        if (userList==null || userList.size()==0){
            userList = new ArrayList();
            userList.add(new UserProfile("Посторонний","",Values.userGuest,""));
            userList.add(new UserProfile("Администратор",Values.FirstAdminPass,Values.userAdmin,""));
            }
        }
    public Settings clone(){
        Settings out = new Settings();
        out.slice = slice.clone();
        out.model = model.clone();
        out.mashine = mashine.clone();
        out.fullScreen = fullScreen;
        return out;
        }
    public void setZStartFinish(){
        model.ZFinish.setVal(model.ModelZ.getVal());
        model.ZStart.setVal(0);
        }
    /** Десериализация из собственного двоичного форматв */
    public void load(DataInputStream in) throws IOException{
        mashine.load(in);
        slice.load(in);
        model.load(in);
        statistic.load(in);
        fullScreen = in.readBoolean();
        }
    /** Сериализация в собственный двоичный формат */
    public void save(DataOutputStream in) throws IOException{
        mashine.save(in);
        slice.save(in);
        model.save(in);
        statistic.save(in);
        in.writeBoolean(fullScreen);
        }
    }
