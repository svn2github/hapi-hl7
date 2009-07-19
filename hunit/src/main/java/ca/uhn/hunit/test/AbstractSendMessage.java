package ca.uhn.hunit.test;

import ca.uhn.hunit.ex.IncorrectMessageReceivedException;
import ca.uhn.hunit.ex.InterfaceException;
import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.msg.AbstractMessage;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.xsd.MessageSource;
import ca.uhn.hunit.xsd.SendMessage;

public abstract class AbstractSendMessage extends AbstractEvent {


	private MessageSource mySource;


	public AbstractSendMessage(TestBatteryImpl theBattery, SendMessage theConfig) {
		super(theBattery, theConfig);
		
		mySource = theConfig.getSource();
	}

	@Override
	public void execute(ExecutionContext theCtx) throws InterfaceException,
			IncorrectMessageReceivedException {

		AbstractMessage messageProvider = getBattery().getMessage(mySource);
		String message = messageProvider.getText();
		message = massageMessage(message);
		
		AbstractInterface iface = getBattery().getInterface(getInterfaceId());
		iface.sendMessage(theCtx, message);
		
	}

	
	public abstract String massageMessage(String theInput);
	
}
