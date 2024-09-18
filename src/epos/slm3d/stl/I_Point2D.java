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
public interface I_Point2D extends I_File{
    double x();
    double y();
    void x(double vv);
    void y(double vv);
}
