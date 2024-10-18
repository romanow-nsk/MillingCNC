package romanow.cnc.utils;

import com.thoughtworks.xstream.XStream;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class OwnDateTime {
    private long timeInMS=0;
    transient GregorianCalendar calendar = new GregorianCalendar();
    //---------------------------------------------------------------------------
    public String toStringValue() {
        return ""+timeInMS;
    }
    public void parseValue(String ss) throws Exception {
        timeInMS=0;
        timeInMS = Long.parseLong(ss);
    }
    //---------------------------------------------------------------------------
    public boolean equals(OwnDateTime two){
        if (!dateTimeValid() || !two.dateTimeValid())
            return false;
        return timeInMS == two.timeInMS;
    }
    public OwnDateTime(String date){
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        timeInMS=0;
        try {
            timeInMS = format.parse(date).getTime();
        } catch (ParseException e) {}
    }
    public OwnDateTime clone(){ return  new OwnDateTime(timeInMS); }
    public void onlyDate(){
        calendar.setTimeInMillis(timeInMS);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        timeInMS = calendar.getTimeInMillis();
    }
    public boolean dateTimeValid(){ return timeInMS!=0; }
    public OwnDateTime(){
        timeInMS = System.currentTimeMillis();
    }
    public OwnDateTime(boolean ff){
        timeInMS = 0;
    }
    public OwnDateTime(int hh, int mm){
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,hh);
        calendar.set(Calendar.MINUTE,mm);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        timeInMS = calendar.getTimeInMillis();
    }
    public OwnDateTime(int day, int mm, int yy, int hh, int min){
        this(day,mm,yy,hh,min,0);
    }
    public OwnDateTime(int day, int mm, int yy){
        this(day,mm,yy,0,0,0);
    }
    public OwnDateTime(int day, int mm, int yy, int hh, int min, int sec){
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_MONTH,day);
        calendar.set(Calendar.MONTH,mm-1);
        calendar.set(Calendar.YEAR,yy);
        calendar.set(Calendar.HOUR_OF_DAY,hh);
        calendar.set(Calendar.MINUTE,min);
        calendar.set(Calendar.SECOND,sec);
        calendar.set(Calendar.MILLISECOND,0);
        timeInMS = calendar.getTimeInMillis();
    }
    public void setDate(int day, int mm, int yy){
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_MONTH,day);
        calendar.set(Calendar.MONTH,mm-1);
        calendar.set(Calendar.YEAR,yy);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        timeInMS = calendar.getTimeInMillis();
    }
    public void changeDateSaveTime(OwnDateTime two){
        calendar.setTimeInMillis(two.timeInMS);
        calendar.set(Calendar.HOUR_OF_DAY,hour());
        calendar.set(Calendar.MINUTE,minute());
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        timeInMS = calendar.getTimeInMillis();
    }
    public OwnDateTime(long ms){
        timeInMS = ms;
    }
    public int compareDate(OwnDateTime two){
        if (year()!=two.year())
            return year()-two.year();
        if (month()!=two.month())
            return month()-two.month();
        return day()-two.day();
    }
    public int elapsedTimeInSec(){
        long tt1=System.currentTimeMillis();
        int tt2 =  (int)((tt1-timeInMS())/1000);
        return tt2 < 0 ? 0 : tt2;
    }
    public Date date(){
        return new Date(timeInMS);
    }
    public long timeInMS(){ return timeInMS; }
    public int monthDifference(OwnDateTime two){
        if (!dateTimeValid() || !two.dateTimeValid())
            return 0;
        return year()*12+month()-two.year()*12-two.month();
    }
    public String monthToString(){
        return !dateTimeValid() ? "---" :
                new SimpleDateFormat("MM-yyyy").format(new Date(timeInMS)); }
    public String timeToString(){
        return !dateTimeValid() ? "---" :
                new SimpleDateFormat("HH:mm").format(new Date(timeInMS)); }
    public String timeFullToString(){

        return !dateTimeValid() ? "---" :
                new SimpleDateFormat("HH:mm:ss").format(new Date(timeInMS)); }
    public String dateToString(){

        return !dateTimeValid() ? "---" :
                new SimpleDateFormat("dd-MM-yyyy").format(new Date(timeInMS)); }
    public String toString(){

        return !dateTimeValid() ? "---" :
                new SimpleDateFormat("yyyy-MM-dd_HH.mm").format(new Date(timeInMS)); }
    public String toString2(){

        return !dateTimeValid() ? "---" :
                new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date(timeInMS)); }
    public String dateTimeToString(){

        return !dateTimeValid() ? "---" :
                new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date(timeInMS)); }
    public String fullToString(){

        return !dateTimeValid() ? "---" :
                new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(timeInMS)); }
    public String fullTimeToString(){

        return !dateTimeValid() ? "---" :
                new SimpleDateFormat("HH:mm:ss").format(new Date(timeInMS))+":"+timeInMS%1000; }
    public String dateTimeToString2(){

        return !dateTimeValid() ? "---" :
                new SimpleDateFormat("dd-MM HH:mm").format(new Date(timeInMS)); }
    public String toStringSec(String spDate,String spTime){

        return !dateTimeValid() ? "---" :
                new SimpleDateFormat("yyyy"+spDate+"MM"+spDate+"dd_HH"+spTime+"mm"+spTime+"ss").format(new Date(timeInMS));}
    public String toStringMark(){

        return !dateTimeValid() ? "---" :
                new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(timeInMS)); }
    public String toStringMarkPoints(){

        return !dateTimeValid() ? "---" :
                new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date(timeInMS)); }
    public String toStringSec(){
        return toStringSec("-","."); }
    public int year(){
        if (!dateTimeValid())
            return 0;
        calendar.setTimeInMillis(timeInMS);
        return calendar.get(Calendar.YEAR);
    }
    public int month(){
        if (!dateTimeValid())
            return 0;
        calendar.setTimeInMillis(timeInMS);
        return calendar.get(Calendar.MONTH)+1;
    }
    public int day(){
        if (!dateTimeValid())
            return 0;
        calendar.setTimeInMillis(timeInMS);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }
    public int second(){
        if (!dateTimeValid())
            return 0;
        calendar.setTimeInMillis(timeInMS);
        return calendar.get(Calendar.SECOND);
    }
    public int dayOfWeek(){
        if (!dateTimeValid())
            return 0;
        calendar.setTimeInMillis(timeInMS);
        return calendar.get(Calendar.DAY_OF_WEEK)-1;
    }
    public void incMonth(){
        calendar.setTimeInMillis(timeInMS);
        calendar.add(Calendar.MONTH,1);
        timeInMS = calendar.getTimeInMillis();
        //dd = dd.plusMonths(1); timeInMS = dd.getTime();
    }
    public void decMonth(){
        calendar.setTimeInMillis(timeInMS);
        calendar.add(Calendar.MONTH,-1);
        timeInMS = calendar.getTimeInMillis();
    }
    public OwnDateTime day(int vv){
        calendar.setTimeInMillis(timeInMS);
        calendar.set(Calendar.DAY_OF_MONTH,vv);
        timeInMS = calendar.getTimeInMillis();
        return this;}
    public OwnDateTime hour(int vv){
        calendar.setTimeInMillis(timeInMS);
        calendar.set(Calendar.HOUR_OF_DAY,vv);
        timeInMS = calendar.getTimeInMillis();
        return this; }
    public OwnDateTime minute(int vv){
        calendar.setTimeInMillis(timeInMS);
        calendar.set(Calendar.MINUTE,vv);
        timeInMS = calendar.getTimeInMillis();
        return this; }
    public OwnDateTime month(int vv){
        calendar.setTimeInMillis(timeInMS);
        calendar.set(Calendar.MONTH,vv-1);
        timeInMS = calendar.getTimeInMillis();
        return this; }
    public OwnDateTime year(int vv){

        calendar.setTimeInMillis(timeInMS);
        calendar.set(Calendar.YEAR,vv);
        timeInMS = calendar.getTimeInMillis();
        return this; }
    public int hour(){
        if (!dateTimeValid())
            return 0;
        calendar.setTimeInMillis(timeInMS);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }
    public int minute(){
        if (!dateTimeValid())
            return 0;
        calendar.setTimeInMillis(timeInMS);
        return calendar.get(Calendar.MINUTE);
    }
    public void plusDays(int vv){
        calendar.setTimeInMillis(timeInMS);
        calendar.add(Calendar.DAY_OF_MONTH,vv);
        timeInMS = calendar.getTimeInMillis();
    }
    public void minusDays(int vv){
        calendar.setTimeInMillis(timeInMS);
        calendar.add(Calendar.DAY_OF_MONTH,-vv);
        timeInMS = calendar.getTimeInMillis();
        //dd=dd.minusDays(vv); timeInMS = dd.getTime();
    }
    public void setOnlyDate(){
        calendar.setTimeInMillis(timeInMS);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        timeInMS = calendar.getTimeInMillis();
    }

    public void setAliases(XStream xs) {
        xs.alias("Date", OwnDateTime.class);
        xs.useAttributeFor("timeInMS", OwnDateTime.class);
    }
    public int getOnlyTime(){
        return hour()*60+minute();
    }
    public void setOnlyTime(int time){
        minute(time%60);
        hour(time/60);
    }
    public boolean isToday(){
        OwnDateTime tt = new OwnDateTime();
        tt.onlyDate();
        OwnDateTime tt2 = new OwnDateTime(timeInMS);
        tt2.onlyDate();
        return tt.timeInMS==tt2.timeInMS;
    }
    public long timeOfDayInMin(){
        return hour()*60+minute();
    }
    //------------------------------------------------------------------------------------------------------------------
    public void afterLoad() {
        }
    public static String time(int duration){
        return String.format("%2d:%2d",duration/60,duration%60);
    }
    public static void main(String a[]){
        String ss1,ss2;
        OwnDateTime xx = new OwnDateTime();
        ss2 = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(xx.date());
        ss2 = ""+xx.year()+"-"+xx.month()+"-"+xx.day()+" "+xx.hour()+":"+xx.minute()+":"+xx.second();
        xx.year(2024);
        xx.month(3);
        xx.day(15);
        xx.hour(20);
        xx.minute(25);
        try {
            xx.day(33);
        } catch (Exception ee){ System.out.println("OwnDateTime2: "+ee.toString());}
        xx = new OwnDateTime(1,11,2019);
        xx.incMonth();
        xx.decMonth();
        xx = new OwnDateTime(1,11,2019,18,25);
        xx = new OwnDateTime(1,11,2019,18,25,16);
        try {
            xx.parseValue(xx.toStringValue());
        } catch (Exception ee){ System.out.println("OwnDateTime2: "+ee.toString());}
        try {
            xx.parseValue(""+System.currentTimeMillis());
        } catch (Exception ee){ System.out.println("OwnDateTime2: "+ee.toString());}
        xx.onlyDate();
        xx.plusDays(100);
        xx.minusDays(150);
        xx.setOnlyTime(1000);
        xx.setOnlyDate();
        OwnDateTime xx0 = new OwnDateTime();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {}
        xx0.setOnlyDate();
        xx = new OwnDateTime("12.10.2020");
        xx.setDate(12,4,2008);
        xx = new OwnDateTime(12,50);
        xx.changeDateSaveTime(xx0);
    }
}

