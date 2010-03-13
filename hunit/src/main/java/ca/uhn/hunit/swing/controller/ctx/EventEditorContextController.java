package ca.uhn.hunit.swing.controller.ctx;

import ca.uhn.hunit.event.AbstractEvent;
import ca.uhn.hunit.event.EventFactory;
import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.swing.ui.event.AbstractEventEditorForm;
import ca.uhn.hunit.swing.ui.event.BaseEventEditorForm;
import ca.uhn.hunit.test.TestImpl;

public class EventEditorContextController extends AbstractContextController<AbstractEventEditorForm<?>> {

	private AbstractEvent myEvent;
	private TestImpl myTest;
	private AbstractEventEditorForm<AbstractEvent> myView;

	/**
	 * Constructor
	 * 
	 * @param theEvent
	 *            The event being edited
	 */
	public EventEditorContextController(TestImpl theTest, AbstractEvent theEvent) {
		myEvent = theEvent;
		myTest = theTest;
		try {
			myView = EventFactory.INSTANCE.createEditorForm(this, theEvent);
		} catch (ConfigurationException e) {
			throw new Error(e);
		}
	}

	public void changeEventType(AbstractEvent theEvent, Class<? extends AbstractEvent> theNewClass) throws ConfigurationException {
		AbstractEvent newEvent = EventFactory.INSTANCE.createEvent(theNewClass, myTest, theEvent.exportConfigToXml());
		myTest.getEventsModel().replaceEvent(theEvent, newEvent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractEventEditorForm<?> getView() {
		return myView;
	}

	/**
	 * @return Returns the test associated with this controller
	 */
	public TestImpl getTest() {
		return myTest;
	}

}
