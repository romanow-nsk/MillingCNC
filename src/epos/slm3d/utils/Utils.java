package epos.slm3d.utils;

import java.io.*;
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
        return Math.round(vv * Values.PrinterFieldSize / 2);
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
    public static void main(String a[]){
         System.out.println(currentDateTime());
    }
}
