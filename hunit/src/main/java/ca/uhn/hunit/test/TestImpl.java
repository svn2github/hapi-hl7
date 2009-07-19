package ca.uhn.hunit.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.IncorrectMessageReceivedException;
import ca.uhn.hunit.ex.InterfaceException;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.xsd.ExpectMessageAny;
import ca.uhn.hunit.xsd.SendMessageAny;
import ca.uhn.hunit.xsd.Test;

public class TestImpl {

	private Test myConfig;
	private String myName;
	private List<AbstractEvent> myEvents = new ArrayList<AbstractEvent>(); 
	
	public TestImpl(TestBatteryImpl theBattery, Test theConfig) throws ConfigurationException {
		myConfig = theConfig;
		myName = theConfig.getName();
		
		for (Object next : theConfig.getSendMessageOrExpectMessage()) {
			if (next instanceof SendMessageAny) {
				SendMessageAny nextSm = (SendMessageAny)next;
				if (nextSm.getHl7V2() != null) {
					myEvents.add(new Hl7V2SendMessageImpl(theBattery, nextSm.getHl7V2()));
				}
			} else if (next instanceof ExpectMessageAny) {
				ExpectMessageAny nextEm = (ExpectMessageAny) next;
				if (nextEm.getHl7V2Specific() != null) {
					myEvents.add(new Hl7V2ExpectSpecificMessageImpl(theBattery, nextEm.getHl7V2Specific()));
				}
				if (nextEm.getHl7V2Rules() != null) {
					myEvents.add(new Hl7V2ExpectRulesImpl(theBattery, nextEm.getHl7V2Rules()));
				}
				if (nextEm.getHl7V2Ack() != null) {
					myEvents.add(new Hl7V2ExpectRulesImpl(theBattery, nextEm.getHl7V2Ack()));
				}
			} else {
				throw new ConfigurationException("Unknown event type: " + next.getClass());
			}
		}
	}
	
	
	public void execute(ExecutionContext theCtx) throws InterfaceException, IncorrectMessageReceivedException {
		for (AbstractEvent next : myEvents) {
			next.execute(theCtx);
		}
	}


	public String getName() {
		return myName;
	}
	
}
