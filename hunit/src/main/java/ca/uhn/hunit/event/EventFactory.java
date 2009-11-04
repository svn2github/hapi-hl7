/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.uhn.hunit.event;

import ca.uhn.hunit.util.ClassNameComparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author James
 */
public class EventFactory {

    public static final EventFactory INSTANCE = new EventFactory();

    private final List<Class<? extends AbstractEvent>> myEventClasses;

    public EventFactory() {
        myEventClasses = new ArrayList<Class<? extends AbstractEvent>>();

        myEventClasses.add(ca.uhn.hunit.event.expect.ExpectNoMessageImpl.class);
        myEventClasses.add(ca.uhn.hunit.event.expect.Hl7V2ExpectRulesImpl.class);
        myEventClasses.add(ca.uhn.hunit.event.expect.Hl7V2ExpectSpecificMessageImpl.class);
        myEventClasses.add(ca.uhn.hunit.event.expect.XmlExpectSpecificMessageImpl.class);

        myEventClasses.add(ca.uhn.hunit.event.send.Hl7V2SendMessageImpl.class);
        myEventClasses.add(ca.uhn.hunit.event.send.XmlSendMessageImpl.class);

        Collections.sort(myEventClasses, new ClassNameComparator());
    }

    /**
     * Returns a list, ordered by full class name, of the registered event types
     */
    public List<Class<? extends AbstractEvent>> getEventClasses() {
        return myEventClasses;
    }
}
