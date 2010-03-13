/**
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the
 * specific language governing rights and limitations under the License.
 *
 * The Initial Developer of the Original Code is University Health Network. Copyright (C)
 * 2001.  All Rights Reserved.
 *
 * Alternatively, the contents of this file may be used under the terms of the
 * GNU General Public License (the  "GPL"), in which case the provisions of the GPL are
 * applicable instead of those above.  If you wish to allow use of your version of this
 * file only under the terms of the GPL and not to allow others to use your version
 * of this file under the MPL, indicate your decision by deleting  the provisions above
 * and replace  them with the notice and other provisions required by the GPL License.
 * If you do not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the GPL.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uhn.hunit.test;

import ca.uhn.hunit.event.AbstractEvent;
import ca.uhn.hunit.event.InterfaceInteractionEnum;
import ca.uhn.hunit.event.expect.ExpectNoMessageImpl;
import ca.uhn.hunit.event.expect.Hl7V2ExpectRulesImpl;
import ca.uhn.hunit.event.expect.Hl7V2ExpectSpecificMessageImpl;
import ca.uhn.hunit.event.expect.XmlExpectSpecificMessageImpl;
import ca.uhn.hunit.event.send.Hl7V2SendMessageImpl;
import ca.uhn.hunit.event.send.XmlSendMessageImpl;
import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.l10n.Strings;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.xsd.Event;
import ca.uhn.hunit.xsd.ExpectMessageAny;
import ca.uhn.hunit.xsd.ExpectNoMessage;
import ca.uhn.hunit.xsd.SendMessageAny;
import ca.uhn.hunit.xsd.Test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author James
 */
public class TestEventsModel extends AbstractTableModel implements PropertyChangeListener {
    //~ Static fields/initializers -------------------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    //~ Instance fields ------------------------------------------------------------------------------------------------
    private final List<AbstractEvent> myEvents = new ArrayList<AbstractEvent>();
    private final List<AbstractInterface> myInterfaces = new ArrayList<AbstractInterface>();
    private final Map<AbstractInterface, AbstractEvent[]> myInterface2Events = new HashMap<AbstractInterface, AbstractEvent[]>();
    private final Map<AbstractInterface, Set<InterfaceInteractionEnum>> myInterface2InterfaceInteractionEnums = new HashMap<AbstractInterface, Set<InterfaceInteractionEnum>>();
    private final TestImpl myTest;
    private AbstractEvent[] myUnconfiguredEvents;

    //~ Constructors ---------------------------------------------------------------------------------------------------
    public TestEventsModel(TestImpl theTest) {
        myTest = theTest;
    }

    //~ Methods --------------------------------------------------------------------------------------------------------
    public void addEvent(AbstractEvent event) {
        myEvents.add(event);
        event.addPropertyChangeListener(AbstractEvent.INTERFACE_ID_PROPERTY, this);
        sortInterfaces();
    }

    /**
     * Populates theConfig with the events in this model
     */
    public void exportConfig(Test theConfig) {
        for (AbstractEvent next : myEvents) {
            theConfig.getSendMessageOrExpectMessageOrExpectNoMessage().add(next.exportConfigToXmlAndEncapsulate());
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getColumnCount() {
        int retVal = myInterfaces.size();

        if (myUnconfiguredEvents != null) {
            retVal++;
        }

        return retVal;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getColumnName(int column) {
        if (myUnconfiguredEvents != null) {
            if (column == 0) {
                return Strings.getMessage("eventlist.unconfigured");
            } else {
                column--;
            }
        }

        return myInterfaces.get(column).getId();
    }

    public AbstractEvent getEvent(int selectedRow) {
        return myEvents.get(selectedRow);
    }

    public List<AbstractEvent> getEventsByInterface(AbstractInterface theInterface) {
        return Arrays.asList(myInterface2Events.get(theInterface));
    }

    public List<AbstractInterface> getInterfaces() {
        return myInterfaces;
    }

    public Map<AbstractInterface, Set<InterfaceInteractionEnum>> getInterfaceInteractionTypes() {
        return myInterface2InterfaceInteractionEnums;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getRowCount() {
        return myEvents.size();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AbstractEvent getValueAt(int rowIndex, int columnIndex) {
        if (myUnconfiguredEvents != null) {
            if (columnIndex == 0) {
                return myUnconfiguredEvents[rowIndex];
            } else {
                columnIndex--;
            }
        }

        AbstractInterface column = myInterfaces.get(columnIndex);
        return myInterface2Events.get(column)[rowIndex];
    }

    /**
     * Initializes this model from an XML definition
     */
    public void initFromXml(Test theConfig) throws ConfigurationException {
        myEvents.clear();

        for (Object next : theConfig.getSendMessageOrExpectMessageOrExpectNoMessage()) {
            AbstractEvent event = null;

            if (next instanceof SendMessageAny) {
                SendMessageAny nextSm = (SendMessageAny) next;

                if (nextSm.getXml() != null) {
                    event = (new XmlSendMessageImpl(myTest,
                            nextSm.getXml()));
                } else if (nextSm.getHl7V2() != null) {
                    event = (new Hl7V2SendMessageImpl(myTest,
                            nextSm.getHl7V2()));
                } else {
                    throw new ConfigurationException("Unknown event type: " + next.getClass());
                }
            } else if (next instanceof ExpectMessageAny) {
                ExpectMessageAny nextEm = (ExpectMessageAny) next;

                if (nextEm.getHl7V2Specific() != null) {
                    event = (new Hl7V2ExpectSpecificMessageImpl(myTest,
                            nextEm.getHl7V2Specific()));
                } else if (nextEm.getHl7V2Rules() != null) {
                    event = (new Hl7V2ExpectRulesImpl(myTest,
                            nextEm.getHl7V2Rules()));
                } else if (nextEm.getHl7V2Ack() != null) {
                    event = (new Hl7V2ExpectRulesImpl(myTest,
                            nextEm.getHl7V2Ack()));
                } else if (nextEm.getXmlSpecific() != null) {
                    event =
                            new XmlExpectSpecificMessageImpl(myTest,
                            nextEm.getXmlSpecific());
                } else {
                    throw new ConfigurationException("Unknown event type: " + next.getClass());
                }
            } else if (next instanceof ExpectNoMessage) {
                event = new ExpectNoMessageImpl(myTest, (ExpectNoMessage) next);
            } else {
                throw new ConfigurationException("Unknown event type: " + next.getClass());
            }

            if (event == null) {
                continue;
            }

            event.addPropertyChangeListener(AbstractEvent.INTERFACE_ID_PROPERTY, this);
            myEvents.add(event);
        }

        sortInterfaces();
    }

    /**
     * Moves the item down in the list
     */
    public int moveDown(AbstractEvent selectedEvent) {
        int oldIndex = myEvents.indexOf(selectedEvent);

        if (oldIndex < (myEvents.size() - 1)) {
            AbstractEvent replacing = myEvents.get(oldIndex + 1);
            myEvents.set(oldIndex + 1, selectedEvent);
            myEvents.set(oldIndex, replacing);
            sortInterfaces();

            return oldIndex + 1;
        }

        return oldIndex;
    }

    /**
     * Moves the item up in the list
     */
    public int moveUp(AbstractEvent selectedEvent) {
        int oldIndex = myEvents.indexOf(selectedEvent);

        if (oldIndex > 0) {
            AbstractEvent replacing = myEvents.get(oldIndex - 1);
            myEvents.set(oldIndex - 1, selectedEvent);
            myEvents.set(oldIndex, replacing);
            sortInterfaces();

            return oldIndex - 1;
        }

        return oldIndex;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (AbstractEvent.INTERFACE_ID_PROPERTY.equals(evt.getPropertyName())) {
            sortInterfaces();
        }
    }

    /**
     * Replaces an event in the event list with a new event at the same index
     * @param theEvent The event to replace
     * @param theNewEvent The event to replace it with
     */
    public int replaceEvent(AbstractEvent theEvent, AbstractEvent theNewEvent) {
        int index = myEvents.indexOf(theEvent);
        myEvents.set(index, theNewEvent);
        sortInterfaces();

        return index;
    }

    private void sortInterfaces() {
        // Sort events by interface
        myInterfaces.clear();
        myInterface2Events.clear();
        myInterface2InterfaceInteractionEnums.clear();
        myUnconfiguredEvents = null;

        int index = 0;

        for (AbstractEvent nextEvent : myEvents) {
            if (!nextEvent.isConfigured()) {
                if (myUnconfiguredEvents == null) {
                    myUnconfiguredEvents = new AbstractEvent[myEvents.size()];
                }

                myUnconfiguredEvents[index] = nextEvent;
            } else {
                AbstractInterface interfaceId = nextEvent.getInterface();

                if (!myInterfaces.contains(interfaceId)) {
                    myInterfaces.add(interfaceId);
                }

                if (!myInterface2Events.containsKey(interfaceId)) {
                    myInterface2Events.put(interfaceId,
                            new AbstractEvent[myEvents.size()]);
                }

                if (!myInterface2InterfaceInteractionEnums.containsKey(interfaceId)) {
                    myInterface2InterfaceInteractionEnums.put(interfaceId,
                            new HashSet<InterfaceInteractionEnum>());
                }

                AbstractEvent[] array = myInterface2Events.get(interfaceId);
                array[index] = nextEvent;
                myInterface2InterfaceInteractionEnums.get(interfaceId).add(nextEvent.getInteractionType());
            }

            index++;
        }

        // Figure out which interfaces are actually being used
        myInterfaces.clear();

        for (AbstractInterface nextInterface : myTest.getBattery().getInterfaces()) {
            if (myInterface2Events.containsKey(nextInterface)) {
                myInterfaces.add(nextInterface);
            }
        }

        super.fireTableStructureChanged();
    }

}
