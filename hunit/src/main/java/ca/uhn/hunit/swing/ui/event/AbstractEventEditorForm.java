/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.uhn.hunit.swing.ui.event;

import javax.swing.JPanel;

import ca.uhn.hunit.event.AbstractEvent;
import ca.uhn.hunit.swing.controller.ctx.EventEditorContextController;
import ca.uhn.hunit.swing.ui.AbstractContextForm;

/**
 *
 * @author James
 */
public abstract class AbstractEventEditorForm<T extends AbstractEvent> extends AbstractContextForm<EventEditorContextController> {
    private static final long serialVersionUID = 1L;

    public abstract void setController(EventEditorContextController theController, T theEvent);

	@Override
	public void setController(EventEditorContextController theController) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tearDown() {
		// TODO Auto-generated method stub
		
	}
    
    

}
