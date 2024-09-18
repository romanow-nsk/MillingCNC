/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epos.slm3d.console;

import java.util.Date;

/**
 *
 * @author romanow
 */
public class LaserPowerData {
    public boolean cycle=false; // Включение режима накопления
    public int count=0;         // Количестов отсчетов мощности
    public double sum=0;        // Сумма значений мощности
    public long time=0;         // Значение времени (интервал работы) в ms
    public int time(){
        if (cycle)
            return (int)(new Date().getTime()- time);
        else
            return (int)time;
        }
    public double middle(){
        return count==0 ? 0 : sum/count;
        }
    public double energy(){
        if (count==0) return 0;
        return middle()*time()/1000;
        }
    public String toString(){ return String.format("Среднее=%6.2f Отсчетов=%6d Время=%4d Энергия=%8.2f",middle(),count,time()/1000,energy()); }
}
