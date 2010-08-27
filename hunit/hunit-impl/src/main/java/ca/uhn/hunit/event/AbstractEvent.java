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
package ca.uhn.hunit.event;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.msg.AbstractMessage;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.run.IExecutionContext;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.util.AbstractModelClass;
import ca.uhn.hunit.xsd.Event;

import org.apache.commons.lang.StringUtils;

import java.beans.PropertyVetoException;
import java.util.LinkedHashMap;

/**
 * An event is a single action taken within a test. A single test therefore is
 * a series of events executed in parallel
 */
public abstract class AbstractEvent extends AbstractModelClass {
    //~ Static fields/initializers -------------------------------------------------------------------------------------

    public static final String INTERFACE_MESSAGES_PROPERTY = "INTERFACE_MESSAGES_PROPERTY";
    public static final String INTERFACE_ID_PROPERTY = "INTERFACE_ID_PROPERTY";

    //~ Instance fields ------------------------------------------------------------------------------------------------

    private AbstractInterface myInterface;
    private TestImpl myTest;

    //~ Constructors ---------------------------------------------------------------------------------------------------

    public AbstractEvent(TestImpl theTest, Event theConfig)
                  throws ConfigurationException {
        String interfaceId = theConfig.getInterfaceId();

        if (! StringUtils.isBlank(interfaceId)) {
            myInterface = theTest.getBattery().getInterface(interfaceId);
        }

        myTest = theTest;
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    public abstract void execute(IExecutionContext theCtx)
                          throws TestFailureException, ConfigurationException;

    /**
     * Subclasses should override this and pass their config to the super implementation
     * before returning the generated object
     */
    public Event exportConfig(Event theConfigToPopulate) {
        theConfigToPopulate.setInterfaceId(myInterface.getId());

        return theConfigToPopulate;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public abstract Event exportConfigToXml();

    /**
     * Overriding to provide a specific type requirement
     */
    public abstract Object exportConfigToXmlAndEncapsulate();

    public TestBatteryImpl getBattery() {
        return myTest.getBattery();
    }

    public abstract InterfaceInteractionEnum getInteractionType();

    /**
     * Returns the interface associated with this event
     */
    public AbstractInterface getInterface() {
        return myInterface;
    }

    /**
     * Returns the ResourceBundle key to be used to retrieve the summary title of this event
     */
    public String getResourceBundleSummaryKey() {
        return "event.summary." + getClass().getName();
    }

    /**
     * Returns the ResourceBundle key to be used to retrieve the description of this event
     */
    public String getResourceBundleDescriptionKey() {
        return "event.description." + getClass().getName();
    }

    public TestImpl getTest() {
        return myTest;
    }

    /**
     * @return Returns true if this event is configured and ready for use.
     */
    public boolean isConfigured() {
        return myInterface != null;
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

    /**
     * <p>
     * Subclasses should implement this method to provide a collection
     * of all messages used by this event, in a natural order if one makes sense.
     * </p><p>
     * Keys should be a description of the use of the message,
     * e.g. "reply", or just "message". Values should be the message itself.
     * </p>
     * <p>
     * Events are expected to fire a property change with key {@link #INTERFACE_MESSAGES_PROPERTY}
     * if this method's return value changes
     * </p>
     */
    public abstract LinkedHashMap<String, AbstractMessage<?>> getAllMessages();


}
