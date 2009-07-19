package ca.uhn.hunit.test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.iface.MllpInterfaceImpl;
import ca.uhn.hunit.msg.AbstractMessage;
import ca.uhn.hunit.msg.Hl7V2MessageImpl;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.xsd.AnyInterface;
import ca.uhn.hunit.xsd.AnyMessageDefinitions;
import ca.uhn.hunit.xsd.Hl7V2MessageDefinition;
import ca.uhn.hunit.xsd.MessageSource;
import ca.uhn.hunit.xsd.Test;
import ca.uhn.hunit.xsd.TestBattery;

public class TestBatteryImpl {

	private TestBattery myConfig;
	private Map<String, AbstractInterface> myId2Interface = new HashMap<String, AbstractInterface>();
	private Map<String, AbstractMessage> myId2Message = new HashMap<String, AbstractMessage>();
	private String myName;
	private List<TestImpl> myTests = new ArrayList<TestImpl>();

	public TestBatteryImpl(TestBattery theConfig)
			throws ConfigurationException, InterfaceWontStartException {
		myConfig = theConfig;
		myName = theConfig.getName();
		initInterfaces();
		initTests();
		initMessages();
	}

	public TestBatteryImpl(File theDefFile) throws InterfaceWontStartException,
			ConfigurationException, JAXBException {
		this(unmarshal(theDefFile));
	}

	private static TestBattery unmarshal(File theDefFile) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance("ca.uhn.hunit.xsd");
		Unmarshaller u = jaxbContext.createUnmarshaller();
		JAXBElement<TestBattery> root = u.unmarshal(
				new StreamSource(theDefFile), TestBattery.class);
		TestBattery battery = root.getValue();
		return battery;
	}

	private void initMessages() {
		AnyMessageDefinitions messages = myConfig.getMessages();
		if (messages != null) {
			for (Hl7V2MessageDefinition next : messages.getHl7V2()) {
				AbstractMessage nextMessage = new Hl7V2MessageImpl(next);
				myId2Message.put(nextMessage.getId(), nextMessage);
			}
		}
	}

	public AbstractMessage getMessage(MessageSource theSource) {
		return myId2Message.get(theSource.getRef());
	}

	private void initTests() throws ConfigurationException {
		for (Test next : myConfig.getTests().getTest()) {
			TestImpl nextTest = new TestImpl(this, next);
			myTests.add(nextTest);
		}

	}

	public AbstractInterface getInterface(String theId) {
		return myId2Interface.get(theId);
	}

	private void initInterfaces() throws ConfigurationException,
			InterfaceWontStartException {
		for (AnyInterface next : myConfig.getInterfaces().getInterface()) {

			AbstractInterface nextIf;
			if (next.getMllpInterface() != null) {
				nextIf = new MllpInterfaceImpl(next.getMllpInterface());
			} else {
				throw new ConfigurationException(
						"Unknown interface type in battery " + myName);
			}

			myId2Interface.put(nextIf.getId(), nextIf);
		}
	}

	public String getName() {
		return myName;
	}

	public void execute(ExecutionContext theCtx) {
		theCtx.getLog().info(this, "About to execute battery");

		for (AbstractInterface next : myId2Interface.values()) {
			if (next.isAutostart()) {
				try {
					next.start(theCtx);
				} catch (InterfaceWontStartException e) {
					theCtx.addFailure(this, e);
				}
			}
		}
		
		for (TestImpl next : myTests) {
			try {
				next.execute(theCtx);
			} catch (InterfaceException e) {
				theCtx.getLog().error(
						this,
						"Test " + next.getName() + " failed with message: "
								+ e.getMessage());
				theCtx.addFailure(next, e);
			} catch (TestFailureException e) {
				theCtx.getLog().error(
						this,
						"Test " + next.getName() + " failed with message: "
								+ e.getMessage());
				theCtx.addFailure(next, e);
			}
		}

		theCtx.getLog().info(this, "Finished executing battery");
	}

}
