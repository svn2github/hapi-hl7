package ca.uhn.hunit.swing.model;

import java.beans.PropertyChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;

import ca.uhn.hunit.event.AbstractEvent;
import ca.uhn.hunit.l10n.Strings;
import ca.uhn.hunit.msg.AbstractMessage;
import ca.uhn.hunit.test.TestImpl;
import java.beans.PropertyChangeListener;
import java.util.Map;

public class EventTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 6003333822124043738L;
	private AbstractEvent myEvent;
	private TestImpl myTest;
    private final MyTreeModel myModel;
	
	public EventTreeNode(MyTreeModel theModel, TestImpl theTest, AbstractEvent theEvent) {
        super(theEvent, true);

        myModel = theModel;
		myTest = theTest;
		myEvent = theEvent;

        theEvent.addPropertyChangeListener(AbstractEvent.INTERFACE_MESSAGES_PROPERTY, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                updateChildren();
            }
        });
        updateChildren();
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

    private void updateChildren() {
        while (getChildCount() > 0) {
            remove(0);
        }
        
        for (Map.Entry<String, AbstractMessage<?>> nextEntry : myEvent.getAllMessages().entrySet()) {
            add(new MessageTreeNode(nextEntry.getKey(), nextEntry.getValue()));
        }

        myModel.nodeStructureChanged(this);
    }
	

}
