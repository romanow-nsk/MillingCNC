/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epos.slm3d.settings;

import epos.slm3d.utils.Values;

/**
 *
 * @author romanow
 */
public class GlobalSettings {
    /** масштаб */
    public FloatParameter ScaleFactor = new FloatParameter(1);
    /** авто центрирование */
    public BooleanParameter AutoCenter = new BooleanParameter(true);
    /** авто масштабирование */
    public BooleanParameter AutoScale = new BooleanParameter(true);
    /** Блочный вывод линий */
    public BooleanParameter LineBlock = new BooleanParameter(false);
    /** IP - адрес контроллера (UDP) */
    public StringParameter ControllerIP = new StringParameter(Values.UdpIP);
    /** Количество потоков */
    public IntParameter SliceThreadNum = new IntParameter(Values.SliceThreadNum);
    /** Коррекция нуля рабочего поля по X мм */
    public FloatParameter CenterOffsetX = new FloatParameter(0);
    /** Коррекция нуля рабочего поля по Y мм */
    public FloatParameter CenterOffsetY = new FloatParameter(0);
    /** Печать - текущий слой */
    public IntParameter CurrentLayer = new IntParameter(-1);
    /** Печать - текущая линия */
    public IntParameter CurrentLine = new IntParameter(0);
    /** Мотор 1 - позиция */
    public IntParameter M1CurrentPos = new IntParameter(0);
    public IntParameter M1LowPos = new IntParameter(0);
    public IntParameter M1HighPos = new IntParameter(10000);
    /** Мотор 3 - текущая позиция */
    public IntParameter M3CurrentPos = new IntParameter(0);
    public IntParameter M3LowPos = new IntParameter(0);
    public IntParameter M3HighPos = new IntParameter(10000);
    /** Мотор 4 - текущая позиция */
    public IntParameter M4CurrentPos = new IntParameter(0);
    public IntParameter M4LowPos = new IntParameter(0);
    public IntParameter M4HighPos = new IntParameter(10000);
    /** Программа - напечатанных слоев */
    public IntParameter LayerCount = new IntParameter(0);
    /** Состояние процесса печати */
    public IntParameter PrintingState = new IntParameter(0);        
    /**  Номер COM-порта*/
    public StringParameter COMPort = new StringParameter(Values.COMPort);
    //---------------------------------------------------------------------------
    public void setNotNull(){
        if (ScaleFactor==null) ScaleFactor = new FloatParameter(1);
        if (AutoCenter==null)  AutoCenter = new BooleanParameter(true);
        if (AutoScale==null) AutoScale = new BooleanParameter(true);
        if (LineBlock==null) LineBlock = new BooleanParameter(false);
        if (CurrentLayer==null) CurrentLayer = new IntParameter(-1);
        if (CurrentLine==null) CurrentLine = new IntParameter(0);
        if (ControllerIP == null)  ControllerIP = new StringParameter(Values.UdpIP);
        if (CenterOffsetX==null) CenterOffsetX = new FloatParameter(0);
        if (CenterOffsetY==null) CenterOffsetY = new FloatParameter(0);
        if (SliceThreadNum==null) SliceThreadNum = new IntParameter(Values.SliceThreadNum);
        if (M1CurrentPos==null) M1CurrentPos = new IntParameter(0);
        if (M3CurrentPos==null) M3CurrentPos = new IntParameter(0);
        if (M4CurrentPos==null) M4CurrentPos = new IntParameter(0);
        if (M1LowPos==null) M1LowPos = new IntParameter(0);
        if (M3LowPos==null) M3LowPos = new IntParameter(0);
        if (M4LowPos==null) M4LowPos = new IntParameter(0);
        if (M1HighPos==null) M1HighPos = new IntParameter(0);
        if (M3HighPos==null) M3HighPos = new IntParameter(0);
        if (M4HighPos==null) M4HighPos = new IntParameter(0);
        if (LayerCount==null) LayerCount = new IntParameter(0);
        if (PrintingState==null) PrintingState = new IntParameter(0);
        if (COMPort == null)  COMPort = new StringParameter(Values.COMPort);
        }
}
