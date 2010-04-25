/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uhn.hunit.event;

import ca.uhn.hunit.event.expect.ExpectNoMessageImpl;
import ca.uhn.hunit.event.expect.Hl7V2ExpectSpecificMessageImpl;
import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.swing.controller.ctx.EventEditorContextController;
import ca.uhn.hunit.swing.controller.ctx.TestEditorController;
import ca.uhn.hunit.swing.ui.event.AbstractEventEditorForm;
import ca.uhn.hunit.swing.ui.event.expect.ExpectNoMessageEditorForm;
import ca.uhn.hunit.swing.ui.event.expect.Hl7V2ExpectSpecificMessageEditorForm;
import ca.uhn.hunit.swing.ui.event.expect.XmlExpectSpecificMessageEditorForm;
import ca.uhn.hunit.swing.ui.event.send.Hl7V2SendMessageEditorForm;
import ca.uhn.hunit.swing.ui.event.send.XmlSendMessageEditorForm;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.util.ClassNameComparator;
import ca.uhn.hunit.xsd.Event;
import ca.uhn.hunit.xsd.ExpectNoMessage;
import ca.uhn.hunit.xsd.Hl7V2ExpectRules;
import ca.uhn.hunit.xsd.Hl7V2ExpectSpecificMessage;
import ca.uhn.hunit.xsd.Hl7V2SendMessage;
import ca.uhn.hunit.xsd.XMLExpectMessage;
import ca.uhn.hunit.xsd.XMLExpectSpecificMessage;
import ca.uhn.hunit.xsd.XMLSendMessage;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXB;

/**
 *
 * @author James
 */
public class EventFactory {
    //~ Static fields/initializers -------------------------------------------------------------------------------------

    public static final EventFactory INSTANCE = new EventFactory();

    //~ Instance fields ------------------------------------------------------------------------------------------------

    private final Class<?extends AbstractEvent> myDefaultEventClass;
    private final Event myDefaultEventConfig;
    private final List<Class<?extends AbstractEvent>> myEventClasses;
    private final Map<Class<?extends AbstractEvent>, Class<?extends Event>> myEventClasses2ConfigTypes;
    private final Map<Class<?extends AbstractEvent>, Class<?extends AbstractEventEditorForm>> myEventClasses2EditorForm;

    //~ Constructors ---------------------------------------------------------------------------------------------------

    public EventFactory() {
        myEventClasses2ConfigTypes = new HashMap<Class<?extends AbstractEvent>, Class<?extends Event>>();
        myEventClasses2EditorForm = new HashMap<Class<?extends AbstractEvent>, Class<?extends AbstractEventEditorForm>>();

        myEventClasses2ConfigTypes.put(ExpectNoMessageImpl.class, ExpectNoMessage.class);
        myEventClasses2EditorForm.put(ExpectNoMessageImpl.class, ExpectNoMessageEditorForm.class);

//        myEventClasses2ConfigTypes.put(ca.uhn.hunit.event.expect.Hl7V2ExpectRulesImpl.class, Hl7V2ExpectRules.class);
        myEventClasses2ConfigTypes.put(ca.uhn.hunit.event.expect.Hl7V2ExpectSpecificMessageImpl.class,
                                       Hl7V2ExpectSpecificMessage.class);
        myEventClasses2EditorForm.put(ca.uhn.hunit.event.expect.Hl7V2ExpectSpecificMessageImpl.class,
                                      Hl7V2ExpectSpecificMessageEditorForm.class);

        myEventClasses2ConfigTypes.put(ca.uhn.hunit.event.expect.XmlExpectSpecificMessageImpl.class,
                                       XMLExpectSpecificMessage.class);
        myEventClasses2EditorForm.put(ca.uhn.hunit.event.expect.XmlExpectSpecificMessageImpl.class,
                                      XmlExpectSpecificMessageEditorForm.class);

        myEventClasses2ConfigTypes.put(ca.uhn.hunit.event.send.Hl7V2SendMessageImpl.class, Hl7V2SendMessage.class);
        myEventClasses2EditorForm.put(ca.uhn.hunit.event.send.Hl7V2SendMessageImpl.class,
                                      Hl7V2SendMessageEditorForm.class);

        myEventClasses2ConfigTypes.put(ca.uhn.hunit.event.send.XmlSendMessageImpl.class, XMLSendMessage.class);
        myEventClasses2EditorForm.put(ca.uhn.hunit.event.send.XmlSendMessageImpl.class, XmlSendMessageEditorForm.class);

        myDefaultEventClass = ExpectNoMessageImpl.class;
        myDefaultEventConfig = new ExpectNoMessage();

        myEventClasses = new ArrayList<Class<?extends AbstractEvent>>(myEventClasses2ConfigTypes.keySet());
        Collections.sort(myEventClasses,
                         new ClassNameComparator());
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    private <T extends Event> T convertConfig(Event theInitialConfig, Class<T> theDesiredClass) {
        if (theInitialConfig.getClass().equals(theDesiredClass)) {
            return (T) theInitialConfig;
        }

        StringWriter stringWriter = new StringWriter();
        JAXB.marshal(theInitialConfig, stringWriter);

        return JAXB.unmarshal(new StringReader(stringWriter.toString()),
                              theDesiredClass);
    }

    /**
     * Creates a new default event, typically in response to a user clicking an
     * "add event" button. The user could then modify the type of event to
     * suit their purposes.
     */
    public AbstractEvent createDefaultEvent(TestBatteryImpl theBattery, TestImpl theTest)
                                     throws ConfigurationException {

        if (theBattery.getInterfaces().isEmpty()) {
            throw new ConfigurationException("Can't create event because no interfaces have been defined");
        }

        AbstractInterface iface = theBattery.getInterfaces().get(0);
        myDefaultEventConfig.setInterfaceId(iface.getId());

        // TODO: create blank event type for use here
        return createEvent(myDefaultEventClass, theTest, myDefaultEventConfig);
    }

    /**
     * Creates the appropriate editor form for the given event type
     *
     * @throws InstantiationException If the form can't be created for any reason
     */
    public <T extends AbstractEvent, V extends AbstractEventEditorForm> V createEditorForm(EventEditorContextController theController,
                                                                                              T event)
        throws ConfigurationException {
        Class<V> formClass = (Class<V>) myEventClasses2EditorForm.get(event.getClass());

        if (formClass == null) {
            throw new ConfigurationException("No editor for " + event.getClass());
        }

        try {
            Constructor<V> constructor = formClass.getConstructor();
            V instance = constructor.newInstance();
            instance.setController(theController);

            return instance;
        } catch (InstantiationException ex) {
            throw new ConfigurationException(ex.getMessage(), ex);
        } catch (IllegalAccessException ex) {
            throw new ConfigurationException(ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new ConfigurationException(ex.getMessage(), ex);
        } catch (InvocationTargetException ex) {
            throw new ConfigurationException(ex.getMessage(), ex);
        } catch (NoSuchMethodException ex) {
            throw new ConfigurationException(ex.getMessage(), ex);
        } catch (SecurityException ex) {
            throw new ConfigurationException(ex.getMessage(), ex);
        }
    }

    /**
     * Creates an instance of an event class using a config which may be new or recycled from
     * an existing event, possibly of another type
     *
     * @param theClass
     * @param theTest
     * @param theInitialConfig
     * @return
     */
    public AbstractEvent createEvent(Class<?extends AbstractEvent> theClass, TestImpl theTest, Event theInitialConfig)
                              throws ConfigurationException {
        Class<?extends Event> configType = myEventClasses2ConfigTypes.get(theClass);

        try {
            Constructor<?extends AbstractEvent> constructor = theClass.getConstructor(TestImpl.class, configType);
            final Event convertConfig = convertConfig(theInitialConfig, configType);
            AbstractEvent event = constructor.newInstance(theTest, convertConfig);

            return event;
        } catch (InstantiationException ex) {
            throw new ConfigurationException(ex.getMessage(), ex);
        } catch (IllegalAccessException ex) {
            throw new ConfigurationException(ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new ConfigurationException(ex.getMessage(), ex);
        } catch (InvocationTargetException ex) {
            throw new ConfigurationException(ex.getMessage(), ex);
        } catch (NoSuchMethodException ex) {
            throw new ConfigurationException(ex.getMessage(), ex);
        } catch (SecurityException ex) {
            throw new ConfigurationException(ex.getMessage(), ex);
        }
    }

    /**
     * Returns a list, ordered by full class name, of the registered event types
     */
    public List<Class<?extends AbstractEvent>> getEventClasses() {
        return myEventClasses;
    }
}
