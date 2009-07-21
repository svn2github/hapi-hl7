package ca.uhn.hunit.test;

import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.msg.AbstractMessage;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.xsd.MessageDefinition;
import ca.uhn.hunit.xsd.SendMessage;

public abstract class AbstractSendMessage extends AbstractEvent {


	private String myMessageId;


	public AbstractSendMessage(TestBatteryImpl theBattery, SendMessage theConfig) {
		super(theBattery, theConfig);
		
		MessageDefinition message = (MessageDefinition)theConfig.getMessageId();
		myMessageId = message.getId();
	}

	@Override
	public void execute(ExecutionContext theCtx) throws TestFailureException {

		AbstractMessage messageProvider = getBattery().getMessage(myMessageId);
		String message = messageProvider.getText();
		message = massageMessage(message);
		
		AbstractInterface iface = getBattery().getInterface(getInterfaceId());
		iface.sendMessage(theCtx, message);
		
	}

	
	public abstract String massageMessage(String theInput);
	
}
