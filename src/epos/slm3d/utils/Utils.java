package epos.slm3d.utils;

import epos.slm3d.settings.FloatParameter;
import epos.slm3d.settings.Settings;
import epos.slm3d.settings.WorkSpace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * Created by romanow on 18.12.2017.
 */
public class Utils {
    /**
     * целое в массив байтов (1 к 4, младший байт вперед)
     */
    public static byte[] intToBytes(int vv) {
        byte out[] = new byte[4];
        for (int i = 0; i < 4; i++) {
            out[i] = (byte) vv;
            vv >>= 8;
        }
        return out;
    }

    /**
     * массив целых в массив байтов (1 к 4, младший байт вперед)
     */
    public static byte[] intToBytes(int vv[]) {
        return intToBytes(vv,vv.length);
        }
    public static byte[] intToBytes(int vv[],int sz) {
        byte out[] = new byte[sz * 4];
        int k = 0;
        for (int i = 0; i < sz; i++) {
            int zz = vv[i];
            for (int j = 0; j < 4; j++) {
                out[k++] = (byte) zz;
                zz >>= 8;
            }
        }
        return out;
    }

    /**
     * массив байтов в целое (4 к 1, младший байт вперед)
     */
    public static int bytesToInt(byte zz[]) {
        int vv = 0;
        for (int j = 3; j >= 0; j--) {
            vv <<= 8;
            vv |= (zz[j] & 0x0FF);
        }
        return vv;
    }

    /**
     * массив байтов в массив целых (4 к 1, младший байт вперед)
     */
    public static int[] bytesToIntArray(byte zz[]) {
        int out[] = new int[zz.length / 4];
        for (int i = 0; i < out.length; i++) {
            int vv = 0;
            int k = i * 4;
            for (int j = 3; j >= 0; j--) {
                vv <<= 8;
                vv |= (zz[k + j] & 0x0FF);
            }
            out[i] = vv;
        }
        return out;
    }

    /**
     * массив целых во внешнее представление в 16СС
     */
    public static String toView(int vv[]) {
        String out = "";
        for (int zz : vv)
            out += String.format("%8X ", zz);
        return out;
    }

    /**
     * массив целых с адресами во внешнее представление в 16СС
     */
    public static String toView(int addr, int vv[]) {
        String out = "------------------------\n";
        for (int zz : vv) {
            out += String.format("%6X %08X\n", addr, zz);
            addr += 4;
        }
        return out;
    }

    /**
     * массив байтов во внешнее представление в 16СС
     */
    public static String toView(byte vv[]) {
        String out = "";
        for (int zz : vv)
            out += String.format("%2X ", zz & 0x0FF);
        return out;
    }

    /**
     * извлечение строк из целого массива (первое слоко - количество, кодировка UTF8, строка ограничена 0)
     */
    public static String[] IntArrayToStrings(int vv[]) throws UNIException {
        int count = vv[1];
        String out[] = new String[count];
        byte bb[] = intToBytes(vv);
        int ii = 8;
        for (int k = 0; k < count; k++) {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            while (bb[ii] != 0) {
                bo.write(bb[ii++]);
            }
            ii++;
            StringBuffer xx = new StringBuffer();
            try {
                InputStreamReader in = new InputStreamReader(new ByteArrayInputStream(bo.toByteArray()), "UTF8");
                while (true) {
                    int cc = in.read();
                    if (cc == -1)
                        break;
                    xx.append((char) cc);
                }
            } catch (Exception e) {
                throw UNIException.bug(e.toString());
            }
            out[k] = xx.toString();
        }
        return out;
    }

    /* преобразование относительных единиц в мм*/
    public static long toMM(double vv) {
        return  (long) vv;
        //return Math.round(vv * Values.PrinterFieldSize / 2);
        }
    /* преобразование относительных единиц в мм*/
    public static String toMMString(double vv) {
        long xx = toMM(vv);
        long a1 = xx % 10;
        long a2 = xx / 10;
        return String.format("%d.%d.%d",a2/100,a2%100,a1);
        }
    public static String toTimeString(long sec){
        long a1 = sec%60;
        long a2 = sec/60;
        return String.format("%d:%d:%d",a2/60,a2%60,a1);
        }
    public static String currentDateTime(){
        Date dd = new Date();
        String s0 = String.format("%d.%d.%d",dd.getYear()+1900,dd.getMonth()+1,dd.getDate());
        String s1 = String.format("-%d:%d",dd.getHours(),dd.getMinutes());
        return s0+s1;
        }
    public static String currentTime(){
        Date dd = new Date();
        return String.format("%3d:%-2d:%-2d",dd.getHours(),dd.getMinutes(),dd.getSeconds());
        }
    public static String currentDate(){
        Date dd = new Date();
        return String.format("%4d.%-2d.%-2d",dd.getYear()+1900,dd.getMonth()+1,dd.getDate());
        }
    public static String currentLogName(){
        Date dd = new Date();
        return String.format("%4d.%-2d.%-2d",dd.getYear()+1900,dd.getMonth()+1,dd.getDate()) +
            String.format("%3d.%-2d.%-2d",dd.getHours(),dd.getMinutes(),dd.getSeconds());
        }
    //---------------------------------------------------------------------------
    public static void runAfterDelay(final int delay, final Runnable code){
        runAfterDelayMS(delay*1000,code);
        }
    public static void runInGUI(final Runnable code){
        java.awt.EventQueue.invokeLater(()->{
            code.run();
            });
        }
    public static void runAfterDelayMS(final int delay, final Runnable code){
        new Thread(()->{
            try {
                Thread.sleep(delay);
                java.awt.EventQueue.invokeLater(()->{
                    code.run();
                });
            } catch (InterruptedException ex) {}
        }).start();
        }
    public static String changeFileExt(String name,String ext){
        int idx=name.lastIndexOf(".");
        if (idx==-1)
            return name+"."+ext;
        return name.substring(0, idx+1)+ext;
        }
    public static boolean EqualAbout(double v1, double v2){
        return Math.abs(v1-v2) < Values.EqualDifference;
        }
    public static String fileName(String ss){
        int idx = ss.lastIndexOf("/");
        String testName = (idx == -1 ? ss : ss.substring(idx+1));
        if (idx==-1){
            idx = ss.lastIndexOf("\\");
            if (idx!=-1)
                testName = testName.substring(idx+1);
            }
        idx = testName.lastIndexOf(".");
        testName = (idx == -1 ? testName : testName.substring(0,idx)); 
        return testName;
        }
    public static String createFatalMessage(Throwable ee, int stackSize) {
        String ss = ee.toString() + "\n";
        StackTraceElement dd[] = ee.getStackTrace();
        for (int i = 0; i < dd.length && i < stackSize; i++) {
            ss += dd[i].getClassName() + "." + dd[i].getMethodName() + ":" + dd[i].getLineNumber() + "\n";
        }
        String out = "Программная ошибка:\n" + ss;
        return out;
        }
    //------------------------------------------------------------------------------------------------------------------
    public static void viewUpdate(final Component evt, boolean good){
        if (evt==null){
            System.out.println("Изменения приняты");
            return;
        }
        evt.setBackground(good ? Color.green : Color.yellow);
        delayInGUI(2, new Runnable() {
            @Override
            public void run() {
                evt.setBackground(Color.white);
            }
        });
    }

    public static String setValue(Object oo, String fName, double val){
        try{
            Field fld = oo.getClass().getField(fName);
            if (fld==null)
                return fName+": ошибка записи "+val + " поле не найдено";
            fld.setAccessible(true);
            fld.setDouble(oo,val);
        } catch (Exception ee){
            return fName+": ошибка записи "+val + " "+ee.toString();
        }
        return null;
    }

    public static void delayInGUI(final int sec,final Runnable code){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000*sec);
                    java.awt.EventQueue.invokeLater(code);
                } catch (InterruptedException e) {}
            }
        }).start();
    }

    public static void saveKeyPressed(java.awt.event.KeyEvent evt, FloatParameter par, Settings set, I_Notify notify) {//GEN-FIRST:event_ZstartKeyPressed
        if(evt.getKeyCode()!=10) return;
        String ss = ((JTextField)evt.getComponent()).getText();
        try {
            par.setVal(Float.parseFloat(ss));
            viewUpdate(evt,true);
            WorkSpace.ws().saveSettings();
        } catch (Exception ee){
            notify.notify(Values.error,"Формат вещественного: "+ss);
            viewUpdate(evt,false);
           }
        }


    public static void viewUpdate(final KeyEvent evt, boolean good){
        if (evt==null){
            System.out.println("Изменения приняты");
            return;
        }
        evt.getComponent().setBackground(good ? Color.green : Color.yellow);
        delayInGUI(2, new Runnable() {
            @Override
            public void run() {
                evt.getComponent().setBackground(Color.white);
            }
        });
    }
    public static void main(String a[]){
         System.out.println(currentDateTime());
    }
}
