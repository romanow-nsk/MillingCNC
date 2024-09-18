/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epos.slm3d.graph;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 *
 * @author romanow
 */
public interface I_Mouse {
    public void MouseClicked(MouseEvent evt);
    public void MousePressed(MouseEvent evt);    
    public void MouseReleased(MouseEvent evt);    
    public void MouseMoved(MouseEvent evt);
     public void MouseDragged(MouseEvent evt);
     public void MouseWheelMoved(MouseWheelEvent evt);
}
