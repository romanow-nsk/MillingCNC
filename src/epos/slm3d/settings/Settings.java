package epos.slm3d.settings;

import epos.slm3d.io.BinInputStream;
import epos.slm3d.io.BinOutputStream;
import epos.slm3d.io.I_File;
import epos.slm3d.m3d.M3DValues;
import epos.slm3d.utils.UNIException;
import epos.slm3d.utils.Utils;
import epos.slm3d.utils.Values;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by romanow on 01.12.2017.
 */
public class Settings implements I_File{
    public Marking marking = new Marking();
    public Pulses pulses = new Pulses();
    public Delays delays = new Delays();
    public Filling filling = new Filling();
    public Control control = new Control();
    public OpticalHead opticalHead = new OpticalHead();
    public ServoControl servoControl = new ServoControl();
    public Scanners scanners = new Scanners();
    public LocalSettings local = new LocalSettings();
    public GlobalSettings global = new GlobalSettings();
    public Statistic statistic = new Statistic();
    public ArrayList<UserProfile> userList = new ArrayList();
    transient public int headerSaved[] = new int[3];
    //-----------------------------------------------------------------------------------------------
    public Settings(){}

    public void setNotNull(){
        global.setNotNull();
        pulses.setNotNull();
        delays.setNotNull();
        control.setNotNull();
        filling.setNotNull();
        local.setNotNull();
        statistic.setNotNull();
        if (userList==null || userList.size()==0){
            userList = new ArrayList();
            userList.add(new UserProfile("Посторонний","",Values.userGuest,""));
            userList.add(new UserProfile("Администратор",Values.FirstAdminPass,Values.userAdmin,""));
            }

        }
    public void setZStartFinish(){
        local.ZFinish.setVal(local.Z.getVal());
        local.ZStart.setVal(0);
        }
    /** Заголовок двоичного файла программы-прототипа */
    public byte[] createHeader() throws UNIException {
        Settings settings = this;
        int sz = M3DValues.headerSize/4;
        int hd[] = new int[sz];
        for (int i=0;i<sz;i++)
            hd[i]=0;
        int vv;
        //8 3  Setup Scanners Enable X,Y Correction [0,1]
        vv = (settings.scanners.EnableXCorrection.getVal() ? 1:0) + (settings.scanners.EnableYCorrection.getVal() ? 2:0);
        hd[0]=0x00a0010;        // Не расшифровано
        hd[1]=0x1000000;
        hd[0x2c/4]=0x110004;
        hd[0x38/4]=0x14;
        hd[0x74/4]=0x2c0020;
        hd[0x78/4]=0x70;
        hd[0xcc/4]=0x1f4;
        hd[0xd0/4]=0x41a00000;
        hd[0xd4/4]=0x41200000;
        hd[0xe0/4]=0x30fc;
        hd[0xf8/4]=0x3174;
        hd[0x128/4]=0x30025;
        hd[0x12c/4]=0x4;
        hd[0x138/4]=0x30025;
        hd[0x13c/4]=0x5;
        //----------------- изменяемые данные
        hd[0x078/4]=headerSaved[0];
        hd[0x0E0/4]=headerSaved[1];
        hd[0x0F8/4]=headerSaved[2];
        hd[8/4] = vv;
        hd[0xc/4] = BinOutputStream.doubleToQ31(settings.scanners.ScalingX.getVal());       //c 1073741824  Setup Scanners Scaling X (-1+1)
        hd[0x10/4] = BinOutputStream.doubleToQ31(settings.scanners.ScalingY.getVal());       //10 1073741824 Setup Scanners Scaling X (-1+1)
        //??????????????????????????????????????????????????????????????????????????????
        hd[0x14/4] = BinOutputStream.doubleToQ31(settings.opticalHead.MarkHight.getVal()/10);  //14 1172288059 Setup OH:Mark Hight (float)
        hd[0x18/4] = BinOutputStream.doubleToQ31(settings.opticalHead.MarkWidth.getVal()/10);  //18 1172288059 Setup OH:Mark Width (float)
        hd[0x30/4] = settings.pulses.LaserFrequence.getVal();                               //30 75000	Frequence
        hd[0x34/4] = settings.pulses.LaserPumpPower.getVal();                               //34 20		PumpPower
        hd[0x3C/4] = settings.pulses.LaserPulseSuppress.getVal();                           //3c 10		Pusle Suppress
        hd[0x40/4] = settings.pulses.LaserPulseType.getVal();                               //40 4		Pulse Type
        hd[0x44/4] = settings.delays.LaserOn.getVal();                                      //44 0		Delay Laser On
        hd[0x48/4] = settings.delays.LaserOff.getVal();                                     //48 0		Delay Laser Off
        hd[0x4C/4] = settings.marking.MicroStepsJump.getVal()<<16;                          //4c 4194304 (64-0) Micro Steps Jump
        hd[0x50/4] = settings.marking.MicroStepsMark.getVal()<<16;                          //50 2097152 (32-0) Micro Steps Mark
        hd[0x54/4] = settings.pulses.DACFrequence.getVal();                                 //54 25000	DAC
        hd[0x58/4] = settings.delays.MovingPenJumpDelay.getVal();                           //58 200    Movint Pen: Jump Delay
        hd[0x5C/4] = settings.delays.MovingPenMarkDelay.getVal();                           //5c 20		Movint Pen: Mark Delay
        hd[0x60/4] = settings.delays.MovingPenStrokeDelay.getVal();                         //60 200	Movint Pen: Stroke Delay
        hd[0x6C/4] = settings.marking.MarkTailsInput.getVal();                              //6c 0		(0-0) Mark Tails: Input (2b)
        hd[0x70/4] = settings.marking.MarkTailsOutput.getVal();                             //70 0		(0-0) Mark Tails: Output (2b)
        //e0 14500 	stroke 12500 chess 12960 !!!!!!!!!!!
        hd[0xE4/4] = settings.servoControl.ZPause.getVal();                                             //e4 200 Servo Control: Z-Pause (ms)
        hd[0xE8/4] = Float.floatToIntBits((float)settings.servoControl.ServoControlZSpeed.getVal());    //e8 1092616192 Servo Control: Z-Speed (mm/s) (float)
        hd[0xEC/4] = Float.floatToIntBits((float)settings.servoControl.ServoControlZAcc.getVal());      //ec 1092616192 Servo Control: Z-Acc (mm/s2)  (float)
        //f8 14500 	stroke 15000 chess 14540 !!!!!!!!!!!!!!!
        hd[0xFC/4] = settings.servoControl.YPause.getVal();                                             //e4 200 Servo Control: Y-Pause (ms)
        hd[0x100/4] = Float.floatToIntBits((float)settings.servoControl.ServoControlYSpeed.getVal());   //e8 1092616192 Servo Control: Y-Speed (mm/s) (float)
        hd[0x104/4] = Float.floatToIntBits((float)settings.servoControl.ServoControlYAcc.getVal());     //ec 1092616192 Servo Control: Y-Acc (mm/s2)  (float)
        hd[0x130/4] = settings.opticalHead.Focus.getVal();                                              //130 13000 Setup OH:Focus 0.01mm
        hd[0x140/4] = settings.opticalHead.Focus.getVal();                                              //140 14500 Setup OH:Focus 0.01mm ???????
        return Utils.intToBytes(hd);
        }
    /** извлечение данных из заголовка двоичного файла программы-прототипа */
    public void setHeader(int hd[]) throws IOException{
        int vv;
        //----------------- изменяемые данные
        headerSaved[0]=hd[0x078/4];
        headerSaved[1]=hd[0x0E0/4];
        headerSaved[2]=hd[0x0F8/4];
        //-----------------------------------------
        //8 3  Setup Scanners Enable X,Y Correction [0,1]
        scanners.EnableXCorrection.setVal((hd[8/4]& 1)!=0);
        scanners.EnableYCorrection.setVal((hd[8/4] & 2)!=0);
        scanners.ScalingX.setVal(BinInputStream.q31ToDouble(hd[0xc/4]));       //c 1073741824  Setup Scanners Scaling X (-1+1)
        scanners.ScalingY.setVal(BinInputStream.q31ToDouble(hd[0x10/4]));      //10 1073741824 Setup Scanners Scaling X (-1+1)
        //??????????????????????????????????????????????????????????????????????????????
        opticalHead.MarkHight.setVal(BinInputStream.q31ToDouble(hd[0x14/4])*10);  //14 1172288059 Setup OH:Mark Hight (float)
        opticalHead.MarkWidth.setVal(BinInputStream.q31ToDouble(hd[0x18/4])*10);  //18 1172288059 Setup OH:Mark Width (float)
        pulses.LaserFrequence.setVal(hd[0x30/4]);                              //30 75000	Frequence
        pulses.LaserPumpPower.setVal(hd[0x34/4]);                              //34 20		PumpPower
        pulses.LaserPulseSuppress.setVal(hd[0x3c/4]);                          //3c 10		Pusle Suppress
        pulses.LaserPulseType.setVal(hd[0x40/4]);                              //40 4		Pulse Type
        delays.LaserOn.setVal(hd[0x44/4]);                                     //44 0		Delay Laser On
        delays.LaserOff.setVal(hd[0x48/4]);                                    //48 0		Delay Laser Off
        marking.MicroStepsJump.setVal(hd[0x4c/4] >> 16);                       //4c 4194304	(64-0) Micro Steps Jump
        marking.MicroStepsMark.setVal(hd[0x50/4] >> 16);                       //50 2097152	(32-0) Micro Steps Mark
        pulses.DACFrequence.setVal(hd[0x54/4]);                                //54 25000	DAC
        delays.MovingPenJumpDelay.setVal(hd[0x58/4]);                          //58 200		Movint Pen: Jump Delay
        delays.MovingPenMarkDelay.setVal(hd[0x5c/4]);                          //5c 20		Movint Pen: Mark Delay
        delays.MovingPenStrokeDelay.setVal(hd[0x60/4]);                        //60 200		Movint Pen: Stroke Delay
        marking.MarkTailsInput.setVal(hd[0x6C/4]);                             //6c 0		(0-0) Mark Tails: Input (2b)
        marking.MarkTailsOutput.setVal(hd[0x70/4]);                            //70 0		(0-0) Mark Tails: Output (2b)
        //e0 14500 	stroke 12500 chess 12960 !!!!!!!!!!!
        servoControl.ZPause.setVal(hd[0xE4/4]);                                    //e4 200 Servo Control: Z-Pause (ms)
        servoControl.ServoControlZSpeed.setVal(Float.intBitsToFloat(hd[0xE8/4]));  //e8 1092616192 Servo Control: Z-Speed (mm/s) (float)
        servoControl.ServoControlZAcc.setVal(Float.intBitsToFloat(hd[0xEC/4]));    //ec 1092616192 Servo Control: Z-Acc (mm/s2)  (float)
        //f8 14500 	stroke 15000 chess 14540 !!!!!!!!!!!!!!!
        servoControl.YPause.setVal(hd[0xFC/4]);                                    //e4 200 Servo Control: Y-Pause (ms)
        servoControl.ServoControlYSpeed.setVal(Float.intBitsToFloat(hd[0x100/4])); //e8 1092616192 Servo Control: Y-Speed (mm/s) (float)
        servoControl.ServoControlYAcc.setVal(Float.intBitsToFloat(hd[0x104/4]));   //ec 1092616192 Servo Control: Y-Acc (mm/s2)  (float)
        opticalHead.Focus.setVal(hd[0x130/4]);                                     //130 13000 Setup OH:Focus 0.01mm
        opticalHead.Focus.setVal(hd[0x140/4]);                                     //140 14500 Setup OH:Focus 0.01mm ???????
        }
    /** Десериализация из собственного двоичного форматв */
    public void load(DataInputStream in) throws IOException{
        marking.load(in);
        pulses.load(in);
        delays.load(in);
        filling.load(in);
        control.load(in);
        opticalHead.load(in);
        servoControl.load(in);
        scanners.load(in);
    }
    /** Сериализация в собственный двоичный формат */
    public void save(DataOutputStream in) throws IOException{
        marking.save(in);
        pulses.save(in);
        delays.save(in);
        filling.save(in);
        control.save(in);
        opticalHead.save(in);
        servoControl.save(in);
        scanners.save(in);
    }

}
