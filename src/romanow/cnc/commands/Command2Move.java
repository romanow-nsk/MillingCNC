/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.commands;

import romanow.cnc.m3d.M3DValues;

/**
 *
 * @author romanow
 */
public class Command2Move extends CommandPoint {
    public Command2Move(double x0, double y0){
        super(M3DValues.cmdMove2,x0,y0);
        name = "Перемещение-2";
        }
    public Command2Move(){
        super(M3DValues.cmdMove2);
        name = "Перемещение-2";
        }
    @Override
    public boolean canFind() {
        return false;
        }
}
