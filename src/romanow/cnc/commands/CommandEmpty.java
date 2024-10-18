/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.cnc.commands;

import romanow.cnc.io.BinInputStream;
import romanow.cnc.m3d.M3DValues;
import romanow.cnc.utils.UNIException;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author romanow
 */
public class CommandEmpty extends PrintCommand {
    private ArrayList<Integer> codeList = new ArrayList<>();
    public CommandEmpty(){
        super(M3DValues.cmdEmpty);
        src.add(M3DValues.cmdEmpty);        // Для ненайденной команды
        }
    public void add(int code){ codeList.add(code); }
    public String toString(){
        String out = super.toString()+"\n";
        for(Integer vv:codeList){
            out+=Integer.toHexString(vv)+"\n";
            }
        return out;
        }
    @Override
    public int byteSize() {
        return codeList.size()*4;
        }

    public String name(){ return "????? код"; }
    @Override
    public void load(BinInputStream in) throws IOException {
        }
    @Override
    public boolean canFind() {
        return false;
        }
    @Override
    public int[] CreateBynary() throws UNIException {
        int out[] = new int[codeList.size()];
        for(int i=0;i<codeList.size();i++)
            out[i]=codeList.get(i).intValue();
        return out;
        }
    public boolean isWarning(){ return true; }
    public boolean canGenerate(){ return false; }
}
