/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.m3d;

import romanow.cnc.commands.Command;

/**
 *
 * @author romanow
 */
public interface OnM3DCommand {
    public boolean onCommand(Command cmd);
    public void onFinish();
    public void onHeader(int hd[]);
}
