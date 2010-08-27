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
package ca.uhn.hunit.test;

import ca.uhn.hunit.event.AbstractEvent;
import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.l10n.Strings;
import ca.uhn.hunit.util.AbstractModelClass;
import ca.uhn.hunit.xsd.Test;

import org.apache.commons.lang.StringUtils;

import java.beans.PropertyVetoException;

public class TestImpl extends AbstractModelClass {
    //~ Static fields/initializers -------------------------------------------------------------------------------------

    public static final String NAME_PROPERTY = "TEST_NAME_PROPERTY";

    //~ Instance fields ------------------------------------------------------------------------------------------------

    private String myName;
    private TestBatteryImpl myBattery;
    private final TestEventsModel myEventsModel = new TestEventsModel(this);

    //~ Constructors ---------------------------------------------------------------------------------------------------

    /**
     * Constructor
     */
    public TestImpl(TestBatteryImpl theBattery, String theName) {
        myName = theName;
        myBattery = theBattery;
    }

    /**
     * Constructor
     */
    public TestImpl(TestBatteryImpl theBattery, Test theConfig)
             throws ConfigurationException {
        myName = theConfig.getName();
        myBattery = theBattery;

        myEventsModel.initFromXml(theConfig);
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    @Override
    public Test exportConfigToXml() {
        Test retVal = new Test();
        retVal.setName(myName);
        myEventsModel.exportConfig(retVal);

        return retVal;
    }

    public TestBatteryImpl getBattery() {
        return myBattery;
    }

    public TestEventsModel getEventsModel() {
        return myEventsModel;
    }

    /**
    * {@inheritDoc }
     */
    public String getName() {
        return myName;
    }

    public void setName(String theName) throws PropertyVetoException {
        if (StringUtils.equals(myName, theName)) {
            return;
        }

        if (StringUtils.isEmpty(theName)) {
            throw new PropertyVetoException(Strings.getInstance().getString("test.name.empty"), null);
        }

        if (myBattery.getTestNames().contains(theName)) {
            throw new PropertyVetoException(Strings.getInstance().getString("test.name.duplicate"), null);
        }

        String oldValue = myName;
        fireVetoableChange(NAME_PROPERTY, oldValue, theName);
        myName = theName;
        firePropertyChange(NAME_PROPERTY, oldValue, theName);
    }
}
