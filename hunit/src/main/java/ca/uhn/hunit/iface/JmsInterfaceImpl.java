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

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.ex.InterfaceWontStopException;
import ca.uhn.hunit.run.IExecutionContext;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.xsd.AnyInterface;
import ca.uhn.hunit.xsd.JmsInterface;

/**
 * Interface implementation which sends/receives plain text
 */
public class JmsInterfaceImpl extends AbstractJmsInterfaceImpl<String> {
    //~ Constructors ---------------------------------------------------------------------------------------------------

    public JmsInterfaceImpl(TestBatteryImpl theBattery, String theId) {
        super(theBattery, theId);
    }

    public JmsInterfaceImpl(TestBatteryImpl theBattery, JmsInterface theConfig)
                     throws ConfigurationException {
        super(theBattery, theConfig);
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    /**
     * Subclasses should make use of this method to export AbstractInterface properties into
     * the return value for {@link #exportConfigToXml()}
     */
    protected JmsInterface exportConfig(JmsInterface theConfig) {
        super.exportConfig(theConfig);

        return theConfig;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AnyInterface exportConfigToXml() {
        AnyInterface retVal = new AnyInterface();
        retVal.setJmsInterface(exportConfig(new JmsInterface()));

        return retVal;
    }

    /**
     * Default implementation just returns the sent message back as the reply. This is
     * probably not great behaviour, but JMS probably doesn't need a reply anyhow.
     */
	@Override
	public TestMessage<String> generateDefaultReply(TestMessage<String> theTestMessage) {
		return theTestMessage;
	}

    /**
     * {@inheritDoc }
     */
    @Override
    protected boolean getCapabilitySupportsReply() {
        return false;
    }

}
