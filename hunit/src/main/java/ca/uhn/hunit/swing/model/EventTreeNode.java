package ca.uhn.hunit.swing.model;

import javax.swing.tree.DefaultMutableTreeNode;

import ca.uhn.hunit.event.AbstractEvent;
import ca.uhn.hunit.l10n.Strings;
import ca.uhn.hunit.test.TestImpl;

public class EventTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 6003333822124043738L;
	private AbstractEvent myEvent;
	private TestImpl myTest;
	
	public EventTreeNode(TestImpl theTest, AbstractEvent theEvent) {
		myTest = theTest;
		myEvent = theEvent;
	}

	public String getDescription() {
		return Strings.getMessage("event.summary." + myEvent.getClass().getName());
	}

	public AbstractEvent getEvent() {
		return myEvent;
	}

	public TestImpl getTest() {
		return myTest;
	}
	

}
