/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uhn.hunit.swing.ui.event;

import ca.uhn.hunit.event.AbstractEvent;
import ca.uhn.hunit.l10n.Strings;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author James
 */
public class EventTypeComboboxRenderer extends DefaultListCellRenderer{
    //~ Static fields/initializers -------------------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    //~ Methods --------------------------------------------------------------------------------------------------------

    @Override
    public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected,
                                                   boolean cellHasFocus ){
        super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );

        Class<?extends AbstractEvent> clazz = (Class<?extends AbstractEvent>) value;
        String description = Strings.getMessage( "event.summary." + clazz.getName(  ) );
        setText( description );

        return this;
    }
}
