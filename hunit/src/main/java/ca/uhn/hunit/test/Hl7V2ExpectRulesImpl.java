package ca.uhn.hunit.test;

import java.util.ArrayList;
import java.util.List;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hunit.ex.IncorrectMessageReceivedException;
import ca.uhn.hunit.iface.TestMessage;
import ca.uhn.hunit.xsd.Hl7V2ExpectAck;
import ca.uhn.hunit.xsd.Hl7V2ExpectNak;
import ca.uhn.hunit.xsd.Hl7V2ExpectRules;
import ca.uhn.hunit.xsd.TerserMessageRule;

public class Hl7V2ExpectRulesImpl extends AbstractHl7V2ExpectMessage {

	private List<TerserRuleImpl> myRules = new ArrayList<TerserRuleImpl>();
	
	public Hl7V2ExpectRulesImpl(TestBatteryImpl theBattery, TestImpl theTest, Hl7V2ExpectRules theConfig) {
		super(theTest, theBattery, theConfig);
		
		for (TerserMessageRule next : theConfig.getRule()) {
			addRule(new TerserRuleImpl(this, next));
		}
	}

	public Hl7V2ExpectRulesImpl(TestBatteryImpl theBattery, TestImpl theTest, Hl7V2ExpectAck theConfig) {
		this(theBattery, theTest, (Hl7V2ExpectRules)theConfig);

		addRule(TerserRuleImpl.getValuesInstance(this, "/MSA-1", "AA"));
	}

	public Hl7V2ExpectRulesImpl(TestBatteryImpl theBattery, TestImpl theTest, Hl7V2ExpectNak theConfig) {
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
