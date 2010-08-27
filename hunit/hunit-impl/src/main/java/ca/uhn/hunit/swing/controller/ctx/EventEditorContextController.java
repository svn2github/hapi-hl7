package ca.uhn.hunit.swing.controller.ctx;

import ca.uhn.hunit.event.AbstractEvent;
import ca.uhn.hunit.event.EventFactory;
import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.swing.ui.event.EventEditorFormWrapper;
import ca.uhn.hunit.test.TestImpl;

public class EventEditorContextController extends AbstractContextController<EventEditorFormWrapper> {

	private AbstractEvent myEvent;
	private TestImpl myTest;
	private EventEditorFormWrapper myView;

	/**
	 * Constructor
	 * 
	 * @param theEvent
	 *            The event being edited
	 */
	public EventEditorContextController(TestImpl theTest, AbstractEvent theEvent) throws ConfigurationException {
		myEvent = theEvent;
		myTest = theTest;
		
        myView = new EventEditorFormWrapper();
        myView.setController(this);

	}

	public void changeEventType(AbstractEvent theEvent, Class<? extends AbstractEvent> theNewClass) throws ConfigurationException {
		AbstractEvent newEvent = EventFactory.INSTANCE.createEvent(theNewClass, myTest, theEvent.exportConfigToXml());
		myTest.getEventsModel().replaceEvent(theEvent, newEvent);
	}

    /**
     * @return Returns the event associated with this controller
     */
    public AbstractEvent getEvent() {
        return myEvent;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventEditorFormWrapper getView() {
		return myView;
	}

	/**
	 * @return Returns the test associated with this controller
	 */
	public TestImpl getTest() {
		return myTest;
	}

}
