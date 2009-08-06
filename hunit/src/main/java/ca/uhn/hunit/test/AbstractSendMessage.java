package ca.uhn.hunit.test;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.iface.TestMessage;
import ca.uhn.hunit.msg.AbstractMessage;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.xsd.SendMessage;

public abstract class AbstractSendMessage extends AbstractEvent {


	private TestImpl myTest;
	private AbstractMessage myMessageProvider;
	private AbstractInterface myInterface;


	public AbstractSendMessage(TestBatteryImpl theBattery, TestImpl theTest, SendMessage theConfig) throws ConfigurationException {
		super(theBattery, theTest, theConfig);
		
		String messageId = theConfig.getMessageId();
		myMessageProvider = getBattery().getMessage(messageId);
		myInterface = getBattery().getInterface(getInterfaceId());
		myTest = theTest;
	}

	@Override
	public void execute(ExecutionContext theCtx) throws TestFailureException {

		String message = myMessageProvider.getText();
		message = massageMessage(message);
		
		myInterface.sendMessage(myTest, theCtx, new TestMessage(message));
		
	}

	
	public abstract String massageMessage(String theInput);
	
}
