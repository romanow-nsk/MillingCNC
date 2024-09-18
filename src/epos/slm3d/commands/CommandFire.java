/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epos.slm3d.commands;

import epos.slm3d.controller.DataBuffer;
import epos.slm3d.m3d.M3DValues;

/**
 *
 * @author romanow
 */
public class CommandFire extends CommandPoint {
    public CommandFire(double x0, double y0){
        super(M3DValues.cmdFire,x0,y0);
        name = "Прожиг";
        }
    public CommandFire(){
        super(M3DValues.cmdFire);
        name = "Прожиг";
        }
    public boolean canPut(DataBuffer out){
        return out.canPut(5);
        }
    public void toDataBuffer(DataBuffer out){
        out.putInt(code);
        out.putDouble(x);
        out.putDouble(y);
        }
    public boolean canFind(){ return true; }
}
