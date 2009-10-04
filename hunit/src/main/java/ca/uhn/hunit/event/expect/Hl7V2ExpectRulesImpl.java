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
package ca.uhn.hunit.event.expect;

import ca.uhn.hunit.test.*;
import java.util.ArrayList;
import java.util.List;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.IncorrectMessageReceivedException;
import ca.uhn.hunit.iface.TestMessage;
import ca.uhn.hunit.xsd.Hl7V2ExpectAck;
import ca.uhn.hunit.xsd.Hl7V2ExpectNak;
import ca.uhn.hunit.xsd.Hl7V2ExpectRules;
import ca.uhn.hunit.xsd.TerserMessageRule;

public class Hl7V2ExpectRulesImpl extends AbstractHl7V2ExpectMessage {

	private List<TerserRuleImpl> myRules = new ArrayList<TerserRuleImpl>();
	
	public Hl7V2ExpectRulesImpl(TestBatteryImpl theBattery, TestImpl theTest, Hl7V2ExpectRules theConfig) throws ConfigurationException {
		super(theTest, theBattery, theConfig);
		
		for (TerserMessageRule next : theConfig.getRule()) {
			addRule(new TerserRuleImpl(this, next));
		}
	}

	public Hl7V2ExpectRulesImpl(TestBatteryImpl theBattery, TestImpl theTest, Hl7V2ExpectAck theConfig) throws ConfigurationException {
		this(theBattery, theTest, (Hl7V2ExpectRules)theConfig);

		addRule(TerserRuleImpl.getValuesInstance(this, "/MSA-1", "AA"));
	}

	public Hl7V2ExpectRulesImpl(TestBatteryImpl theBattery, TestImpl theTest, Hl7V2ExpectNak theConfig) throws ConfigurationException {
		this(theBattery, theTest, (Hl7V2ExpectRules)theConfig);
		
		addRule(TerserRuleImpl.getNotValuesInstance(this, "/MSA-1", "AA"));
	}

	protected void addRule(TerserRuleImpl theTerserRuleImpl) {
		myRules.add(theTerserRuleImpl);
	}

	@Override
	public void validateMessage(TestMessage theMessage)	throws IncorrectMessageReceivedException {
		for (TerserRuleImpl next : myRules) {
			next.validate(theMessage);
		}
	}

}
