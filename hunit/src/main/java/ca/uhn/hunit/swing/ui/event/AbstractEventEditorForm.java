/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.uhn.hunit.swing.ui.event;

import ca.uhn.hunit.event.AbstractEvent;
import ca.uhn.hunit.swing.controller.ctx.TestEditorController;
import javax.swing.JPanel;

/**
 *
 * @author James
 */
public abstract class AbstractEventEditorForm<T extends AbstractEvent> extends JPanel {
    private static final long serialVersionUID = 1L;

    public abstract void setController(TestEditorController theController, T theEvent);

}
