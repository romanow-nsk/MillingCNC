/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epos.slm3d.commands;

import epos.slm3d.controller.DataBuffer;
import epos.slm3d.io.BinInputStream;
import epos.slm3d.m3d.M3DValues;
import epos.slm3d.utils.UNIException;
import epos.slm3d.utils.Utils;

import java.io.IOException;

/**
 *
 * @author romanow
 */
public class CommandLayer extends CommandIntList {
        { name = "Засыпка слоя"; idx=4; }
    public CommandLayer(int step1,int step2){
        val[2]=step1;
        val[3]=step2;
        }
    public void step(double step){
        val[2]=(int)step*100;
        val[3]=val[1];
        }
    public boolean canFind(){ return true; }
    public CommandLayer(){
        super(M3DValues.cmdLayer);
        name = "Засыпка слоя";
        }
    public String toString(){
        return super.toString() + Utils.toView(val);
        }
    @Override
    public void load(BinInputStream in) throws IOException {
        src.add(code);
        for(int i=0;i<5;i++){
            val[i]=in.readInt();
            src.add(val[i]);
            }
        }
    @Override
    public int byteSize() {
        return 6*4;
        }
    @Override
    public int[] CreateBynary() throws UNIException{
        int out[] = new int[6];
        out[0]=code;
        for(int i=0;i<5;i++)
            out[i+1]=val[i];
        return out;
        }

    @Override
    public int wordSize() {
        return 3;
        }

    public boolean isImportant(){ return true; }
    public String toView(){ return super.toView() + val[1]; }
    public boolean canPut(DataBuffer out){
        return out.canPut(3);
        }
    public void toDataBuffer(DataBuffer out){
        out.putInt(code);
        out.putInt(val[2]);
        out.putInt(val[3]);
        }
    public int []toIntArray(){      // Версия 1.1 - без параметров
        int out[]=new int[2];
        out[0]=code;
        out[1]=0;
        //out[2]=val[2];
        //out[3]=val[3];
        return out;
        }
    public void loadSTD(BinInputStream in)throws IOException{
        val[1] = in.readInt();
        val[2] = in.readInt();
        }
    }
