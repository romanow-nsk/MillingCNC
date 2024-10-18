/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.commands;

import romanow.cnc.controller.DataBuffer;
import romanow.cnc.m3d.M3DValues;

/**
 *
 * @author romanow
 */
public class CommandMove extends CommandPoint {
    public CommandMove(double x0, double y0){
        super(M3DValues.cmdMove,x0,y0);
        name = "Перемещение";
        }
    public CommandMove(){
        super(M3DValues.cmdMove);
        name = "Перемещение";
        }
    public boolean canPut(DataBuffer out){
        return out.canPut(5);
        }
    public void toDataBuffer(DataBuffer out){
        out.putInt(code);
        out.putDouble(x);
        out.putDouble(y);
        }
    }
