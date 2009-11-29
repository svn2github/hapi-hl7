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
package ca.uhn.hunit.iface;

import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.ex.InterfaceWontStopException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.l10n.Strings;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.util.AbstractModelClass;
import ca.uhn.hunit.xsd.AnyInterface;
import ca.uhn.hunit.xsd.Interface;

import org.apache.commons.lang.StringUtils;

import java.beans.PropertyVetoException;

public abstract class AbstractInterface extends AbstractModelClass implements Comparable<AbstractInterface> {
    //~ Static fields/initializers -------------------------------------------------------------------------------------

    public static final String INTERFACE_STARTED_PROPERTY = "INTERFACE_STARTED_PROPERTY";
    public static final String INTERFACE_ID_PROPERTY = "INTERFACE_ID_PROPERTY";

    //~ Instance fields ------------------------------------------------------------------------------------------------

    private Boolean myAutostart;
    private Boolean myClear;
    private Integer myClearMillis;
    private String myId;
    private final TestBatteryImpl myBattery;

    //~ Constructors ---------------------------------------------------------------------------------------------------

    public AbstractInterface(TestBatteryImpl theBattery, Interface theConfig) {
        myBattery = theBattery;
        myId = theConfig.getId();
        myAutostart = theConfig.isAutostart();
        myClearMillis = theConfig.getClearMillis();
        myClear = theConfig.isClear();

        init();
    }

    public AbstractInterface(TestBatteryImpl theBattery, String theId) {
        myBattery = theBattery;
        myId = theId;

        init();
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    public int compareTo(AbstractInterface theO) {
        return myId.compareTo(theO.myId);
    }

    /**
     * Subclasses should make use of this method to export AbstractInterface properties into
     * the return value for {@link #exportConfigToXml()}
     */
    protected Interface exportConfig(Interface theConfig) {
        theConfig.setAutostart(myAutostart);
        theConfig.setId(myId);
        theConfig.setClearMillis(myClearMillis);
        theConfig.setClear(myClear);

        return theConfig;
    }

    /**
     * Declare a concrete type for subclass implementations of this method
     */
    @Override
    public abstract AnyInterface exportConfigToXml();

    public int getClearMillis() {
        return myClearMillis;
    }

    public String getId() {
        return myId;
    }

    private void init() {
        if (myAutostart == null) {
            myAutostart = true;
        }

        if (myClearMillis == null) {
            myClearMillis = 100;
        }

        if (myClear == null) {
            myClear = true;
        }
    }

    public boolean isAutostart() {
        return myAutostart;
    }

    public boolean isClear() {
        return myClear;
    }

    public abstract boolean isStarted();

    public abstract TestMessage<?> receiveMessage(TestImpl theTest, ExecutionContext theCtx, long theTimeout)
                                           throws TestFailureException;

    public abstract void sendMessage(TestImpl theTest, ExecutionContext theCtx, TestMessage<?> theMessage)
                              throws TestFailureException;

    public void setAutostart(boolean theAutostart) {
        myAutostart = theAutostart;
    }

    public void setClear(boolean theClear) {
        myClear = theClear;
    }

    public void setClearMillis(int theClearMillis) {
        myClearMillis = theClearMillis;
    }

    public void setId(String theId) throws PropertyVetoException {
        if (StringUtils.equals(theId, myId)) {
            return;
        }

        if (StringUtils.isEmpty(theId)) {
            throw new PropertyVetoException(Strings.getInstance().getString("interface.id.empty"), null);
        }

        if (myBattery.getInterfaceIds().contains(theId)) {
            throw new PropertyVetoException(Strings.getInstance().getString("interface.id.duplicate"), null);
        }

        String oldValue = myId;
        firePropertyChange(INTERFACE_ID_PROPERTY, oldValue, theId);
        myId = theId;
    }

    /**
     * Starts the interface
     * @param theCtx The current exection context
     * @throws InterfaceWontStartException If the interface won't start
     */
    public abstract void start(ExecutionContext theCtx)
                        throws InterfaceWontStartException;

    public abstract void stop(ExecutionContext theCtx)
                       throws InterfaceWontStopException;
}
