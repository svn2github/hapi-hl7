/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.uhn.hunit.swing.controller.ctx;

import ca.uhn.hunit.iface.AbstractInterface;
import javax.swing.JPanel;

/**
 *
 * @author James
 */
public abstract class AbstractInterfaceEditorContextController<T extends AbstractInterface, V extends JPanel> extends AbstractContextController<V> {
    
    private final T myModel;

    public AbstractInterfaceEditorContextController(T theModel) {
        myModel = theModel;
    }

    public T getModel() {
        return myModel;
    }

}
