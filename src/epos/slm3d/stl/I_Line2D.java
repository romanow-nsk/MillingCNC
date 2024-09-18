/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epos.slm3d.stl;

import epos.slm3d.io.I_File;

/**
 *
 * @author romanow
 */
public interface I_Line2D extends I_File {
    double x0();
    double y0(); 
    double x1();
    double y1();    
}
