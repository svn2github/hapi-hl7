/**
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
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
package ca.uhn.hunit.event;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.util.AbstractModelClass;
import ca.uhn.hunit.xsd.Event;
import java.beans.PropertyVetoException;

/**
 * An event is a single action taken within a test. A single test therefore is
 * a series of events executed in parallel
 */
public abstract class AbstractEvent extends AbstractModelClass {

    public static final String INTERFACE_ID_PROPERTY = "INTERFACE_ID_PROPERTY";

	private AbstractInterface myInterface;
	private TestImpl myTest;

	public AbstractEvent(TestImpl theTest, Event theConfig) throws ConfigurationException {
        String interfaceId = theConfig.getInterfaceId();
        myInterface = theTest.getBattery().getInterface(interfaceId);
		myTest = theTest;
	}
	
	public TestImpl getTest() {
		return myTest;
	}

	public abstract void execute(ExecutionContext theCtx) throws TestFailureException, ConfigurationException;

    public abstract InterfaceInteractionEnum getInteractionType();

    /**
     * Subclasses should override this and pass their config to the super implementation
     * before returning the generated object
     */
    public Event exportConfig(Event theConfigToPopulate) {
        theConfigToPopulate.setInterfaceId(myInterface.getId());
        return theConfigToPopulate;
    }

	public TestBatteryImpl getBattery() {
		return myTest.getBattery();
	}

	public String getInterfaceId() {
		return myInterface.getId();
	}

	public AbstractInterface getInterface() throws ConfigurationException {
		return myInterface;
	}

    /**
     * Returns the ResourceBundle key to be used to retrieve the description of this event
     */
    public String getResourceBundleSummaryKey() {
        return "event.summary." + getClass().getName();
    }

    public void setInterfaceId(String theInterfaceId) throws PropertyVetoException {
        AbstractInterface newInterface;
        try {
            newInterface = getBattery().getInterface(theInterfaceId);
        } catch (ConfigurationException ex) {
            throw new PropertyVetoException(ex.getMessage(), null);
        }

        String oldValue = myInterface.getId();
        fireVetoableChange(INTERFACE_ID_PROPERTY, oldValue, theInterfaceId);
        this.myInterface = newInterface;
        firePropertyChange(INTERFACE_ID_PROPERTY, oldValue, theInterfaceId);
    }

}
