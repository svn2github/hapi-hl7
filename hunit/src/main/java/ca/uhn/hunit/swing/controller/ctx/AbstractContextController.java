/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.uhn.hunit.swing.controller.ctx;

import javax.swing.JPanel;

/**
 * Base class for a controller for the right hand pane
 */
public abstract class AbstractContextController<V extends JPanel> {

    public abstract V getView();

}
