package epos.slm3d.utils;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by romanow on 17.02.2017.
 */
    /** Обобщенное исключение */
    public class UNIException extends Exception{
        private final static String exceptLevel[]={
                "Пользователь",
                "Ошибка исполнения",
                "Программная ошибка",
                "Фатальная ошибка"
        };
        public final static int warning=0;
        public final static int runTime=1;
        public final static int bug=2;
        public final static int fatal=3;
        //------------------ Типовые ошибки ----------------------------------------
        public final static String sysCode="Ошибка кода исполнительной системы";
        public final static String sql="Ошибка базы данных";
        public final static String net="Ошибка сети";
        public final static String format="Ошибка формата даннных";
        public final static String noFunc="Функция не реализована";
        public final static String other="Прочие ошибки";
        public final static String indirect="Ошибка удаленной компоненты";
        public final static String settings="Ошибка настроек";
        public final static String io="Ошибка в/в";
        public final static String usb="Ошибка USB";
        public final static String userData="Ошибка пользователя";
        public final static String userCode="Ошибка кода пользователя";
        public final static String userVars="Ошибка данных пользователя";
        //--------------------------------------------------------------------------
        private String message="";           // Текст исключения и стек
        private int type=0;
        //--------------------------------------------------------------------------
        public String getMessage() {
            return message;
        }
        public int getType() {
            return type;
        }
        public String toString(){
            return exceptLevel[type]+":"+message;
        }
        //--------------------------------------------------------------------------
        public UNIException(int type, String message){
            this.type=type;
            this.message=message;
            }
        public UNIException(int type, String message0, Throwable ee, boolean stackTrace){
            this.type=type;
            message=message0;
            if (ee==null)
                return;
            message+=" : "+ee.getMessage();
            if (!stackTrace)
                return;
            StackTraceElement dd[]=ee.getStackTrace();
            for (int i = 0; dd != null && i < dd.length && i < Values.StackTraceDeepth; i++) {
                message += ""+dd[i].getClassName() + "." + dd[i].getMethodName() + ":" + dd[i].getLineNumber();
                }
            }
        public UNIException(int type){
            this(type,"");
        }
        public static UNIException fatal(String mes, Throwable ee){
            return new UNIException(bug, mes, ee, true);
            }
        public static UNIException fatal(Throwable ee){
            return new UNIException(bug, sysCode, ee, true);
            }
        public static UNIException fatal(String mes){
            return new UNIException(bug, sysCode+ ":" + mes, null, false);
            }
        public static UNIException warning(String mes){
            return new UNIException(warning,mes);
            }
        public static UNIException sql(Throwable ee){
            return new UNIException(bug,sql,ee,false);
            }
        public static UNIException net(Throwable ee){
            return new UNIException(runTime,net,ee,false);
            }
        public static UNIException net(String mes){
            return new UNIException(runTime,net+": "+mes);
            }
        public static UNIException format(String mes){
            return new UNIException(bug,format+": "+mes);
            }
        public static UNIException user(Throwable ee){
        return new UNIException(warning,userData,ee,false);
        }
        public static UNIException noFunc(){
            return new UNIException(bug,noFunc);
            }
        public static UNIException noFunc(String mes){
            return new UNIException(bug,noFunc+": "+mes);
            }
        public static UNIException bug(String mes){
            return new UNIException(bug,sysCode+": "+mes);
            }
        public static UNIException user(String mes){
            return new UNIException(warning,userData+": "+mes);
            }
        public static UNIException code(String mes){
            return new UNIException(runTime,userCode+": "+mes);
            }
        public static UNIException vars(String mes){
            return new UNIException(runTime,userVars+": "+mes);
            }
        public static UNIException other(){
            return new UNIException(bug,other);
            }
        public static UNIException other(Throwable ee){
            return new UNIException(bug, other, ee, true);
            }
        public static UNIException other(String mes){
            return new UNIException(bug,mes);
            }
        public static UNIException indirect(String mes){
            return new UNIException(runTime,indirect+": "+mes);
            }
        public static UNIException config(String mes){
            return new UNIException(warning,settings+": "+mes);
            }
        public static UNIException io(Throwable ee){
            return new UNIException(runTime,io,ee,false);
            }
        public static UNIException usb(String mes,Throwable ee){
            return new UNIException(runTime,usb+":"+mes,ee,false);
            }
        public static UNIException usb(String mes){
            return new UNIException(runTime,usb+":"+mes);
            }
        public static UNIException io(String mes){
            return new UNIException(runTime,io+": "+mes);
            }
        public static UNIException total(Throwable ee){
            if (ee instanceof UNIException){
                UNIException vv=(UNIException)ee;
                vv.message = indirect + ": " + vv.message;
                return vv;
                }
            if (ee instanceof SQLException)
                return sql(ee);
            if (ee instanceof IOException)
                return io(ee);
            if (ee instanceof Error)
                return fatal(ee);
            return other(ee);
            }
}
