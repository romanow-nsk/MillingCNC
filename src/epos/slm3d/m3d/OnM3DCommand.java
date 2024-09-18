/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epos.slm3d.m3d;

import epos.slm3d.commands.Command;

/**
 *
 * @author romanow
 */
public interface OnM3DCommand {
    public boolean onCommand(Command cmd);
    public void onFinish();
    public void onHeader(int hd[]);
}
