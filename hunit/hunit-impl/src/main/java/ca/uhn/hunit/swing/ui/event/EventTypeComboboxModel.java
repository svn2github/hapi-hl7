/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.uhn.hunit.swing.ui.event;

import ca.uhn.hunit.event.AbstractEvent;
import ca.uhn.hunit.event.EventFactory;
import javax.swing.DefaultComboBoxModel;

/**
 * List model for a list of test event types
 */
public class EventTypeComboboxModel extends DefaultComboBoxModel {
    private static final long serialVersionUID = 1L;


    public EventTypeComboboxModel(AbstractEvent theStartingEvent) {
        for (Class<?> next : EventFactory.INSTANCE.getEventClasses()) {
            addElement(next);
        }

        setSelectedItem(theStartingEvent.getClass());
    }

}
