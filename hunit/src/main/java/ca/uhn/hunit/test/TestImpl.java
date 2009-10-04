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
package ca.uhn.hunit.test;

import ca.uhn.hunit.event.AbstractEvent;
import ca.uhn.hunit.event.send.Hl7V2SendMessageImpl;
import ca.uhn.hunit.event.expect.Hl7V2ExpectRulesImpl;
import ca.uhn.hunit.event.expect.Hl7V2ExpectSpecificMessageImpl;
import ca.uhn.hunit.event.expect.ExpectNoMessageImpl;
import ca.uhn.hunit.event.send.XmlSendMessageImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.xsd.ExpectMessageAny;
import ca.uhn.hunit.xsd.ExpectNoMessage;
import ca.uhn.hunit.xsd.SendMessageAny;
import ca.uhn.hunit.xsd.Test;

public class TestImpl implements ITest {

	private String myName;
	private HashMap<String, ArrayList<AbstractEvent>> myEvents = new HashMap<String, ArrayList<AbstractEvent>>();
	private TestBatteryImpl myBattery; 
	
	public TestImpl(TestBatteryImpl theBattery, Test theConfig) throws ConfigurationException {
		myName = theConfig.getName();
		myBattery = theBattery;
		
		for (Object next : theConfig.getSendMessageOrExpectMessageOrExpectNoMessage()) {
			AbstractEvent event = null;
			
			if (next instanceof SendMessageAny) {
				SendMessageAny nextSm = (SendMessageAny)next;
				if (nextSm.getXml() != null) {
					event = (new XmlSendMessageImpl(theBattery, this, nextSm.getXml()));
				} else if (nextSm.getHl7V2() != null) {
					event = (new Hl7V2SendMessageImpl(theBattery, this, nextSm.getHl7V2()));
				}
			} else if (next instanceof ExpectMessageAny) {
				ExpectMessageAny nextEm = (ExpectMessageAny) next;
				if (nextEm.getHl7V2Specific() != null) {
					event = (new Hl7V2ExpectSpecificMessageImpl(theBattery, this, nextEm.getHl7V2Specific()));
				}
				if (nextEm.getHl7V2Rules() != null) {
					event = (new Hl7V2ExpectRulesImpl(theBattery, this, nextEm.getHl7V2Rules()));
				}
				if (nextEm.getHl7V2Ack() != null) {
					event = (new Hl7V2ExpectRulesImpl(theBattery, this, nextEm.getHl7V2Ack()));
				}
            } else if (next instanceof ExpectNoMessage) {
                event = new ExpectNoMessageImpl(theBattery, this, (ExpectNoMessage)next);
			} else {
				throw new ConfigurationException("Unknown event type: " + next.getClass());
			}
			
			if (event == null) {
				continue;
			}
			
			String interfaceId = event.getInterfaceId();
			if (!myEvents.containsKey(interfaceId)) {
				myEvents.put(interfaceId, new ArrayList<AbstractEvent>());
			}
			myEvents.get(interfaceId).add(event);
			
		}
	}
	
	



	/* (non-Javadoc)
	 * @see ca.uhn.hunit.test.ITest#getName()
	 */
	public String getName() {
		return myName;
	}


	public Set<String> getInterfacesUsed() {
		return myEvents.keySet();
	}


	public List<AbstractEvent> getEventsForInterface(String theInterfaceId) {
		return myEvents.get(theInterfaceId);
	}
	
}
