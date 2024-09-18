/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epos.slm3d.m3d;

/**
 *
 * @author romanow
 */
public interface I_Important {
    public void onEvent(int code,boolean on, int value, String name);
}
