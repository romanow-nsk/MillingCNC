package romanow.cnc.utils;

/**
 * Created by romanow on 02.12.2017.
 */

import javax.swing.*;

/** Обратный вызов нотификации */
public interface I_Notify {
    /** Обратный вызов нотификации - уровень (Values), сообщение */
    public void notify(int level, String mes);
    public void info(String mes);
    public void log(String mes);
    public void setProgress(int proc);
    public void setProgressView(JProgressBar progressView);
}
