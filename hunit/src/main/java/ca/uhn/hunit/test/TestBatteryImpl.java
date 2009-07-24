package ca.uhn.hunit.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.ex.InterfaceWontStopException;
import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.iface.MllpHl7V2InterfaceImpl;
import ca.uhn.hunit.msg.AbstractMessage;
import ca.uhn.hunit.msg.Hl7V2MessageImpl;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.xsd.AnyInterface;
import ca.uhn.hunit.xsd.AnyMessageDefinitions;
import ca.uhn.hunit.xsd.Hl7V2MessageDefinition;
import ca.uhn.hunit.xsd.Test;
import ca.uhn.hunit.xsd.TestBattery;

public class TestBatteryImpl implements ITest {

	private TestBattery myConfig;
	private Map<String, AbstractInterface> myId2Interface = new HashMap<String, AbstractInterface>();
	private Map<String, AbstractMessage> myId2Message = new HashMap<String, AbstractMessage>();
	private String myName;
	private List<TestImpl> myTests = new ArrayList<TestImpl>();
	private HashMap<String, TestImpl> myTestNames2Tests = new HashMap<String, TestImpl>();

	public TestBatteryImpl(TestBattery theConfig) throws ConfigurationException, InterfaceWontStartException {
		myConfig = theConfig;
		myName = theConfig.getName();
		initInterfaces();
		initTests();
		initMessages();
	}

	public TestBatteryImpl(File theDefFile) throws InterfaceWontStartException, ConfigurationException, JAXBException {
		this(unmarshal(theDefFile));
	}

	private static TestBattery unmarshal(File theDefFile) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance("ca.uhn.hunit.xsd");
		Unmarshaller u = jaxbContext.createUnmarshaller();
		JAXBElement<TestBattery> root = u.unmarshal(new StreamSource(theDefFile), TestBattery.class);
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

	public AbstractMessage getMessage(String theId) {
		return myId2Message.get(theId);
	}

	private void initTests() throws ConfigurationException {
		for (Test next : myConfig.getTests().getTest()) {
			TestImpl nextTest = new TestImpl(this, next);
			if (myTestNames2Tests.containsKey(nextTest.getName())) {
				throw new ConfigurationException("Duplicate test name detected: " + nextTest.getName());
			}
			myTestNames2Tests.put(nextTest.getName(), nextTest);
			myTests.add(nextTest);
		}

	}

	public AbstractInterface getInterface(String theId) {
		return myId2Interface.get(theId);
	}

	private void initInterfaces() throws ConfigurationException, InterfaceWontStartException {
		for (AnyInterface next : myConfig.getInterfaces().getInterface()) {

			AbstractInterface nextIf;
			if (next.getMllpHl7V2Interface() != null) {
				nextIf = new MllpHl7V2InterfaceImpl(next.getMllpHl7V2Interface());
			} else {
				throw new ConfigurationException("Unknown interface type in battery " + myName);
			}

			myId2Interface.put(nextIf.getId(), nextIf);
		}
	}

	public String getName() {
		return myName;
	}

	public void execute(ExecutionContext theCtx) {
		execute(theCtx, myTestNames2Tests.keySet());
	}

	
	
	public void execute(ExecutionContext theCtx, String... theTestNamesToExecute) {
		Set<String> testNames = new HashSet<String>(Arrays.asList(theTestNamesToExecute));
		execute(theCtx, testNames);
	}

	public void execute(ExecutionContext theCtx, Set<String> theTestNamesToExecute) {
		theCtx.getLog().info(this, "About to execute battery");

		for (TestImpl next : myTests) {
			if (!theTestNamesToExecute.contains(next.getName())) {
				continue;
			}
			
			next.execute(theCtx);
		}
		
		for (AbstractInterface next : myId2Interface.values()) {
			if (next.isStarted()) {
				try {
					next.stop(theCtx);
				} catch (InterfaceWontStopException e) {
					theCtx.getLog().error(this, "Failed to stop interface " + next.getId() + ": " + e.getMessage());
					theCtx.addFailure(this, e);
				}
			}
		}

		theCtx.getLog().info(this, "Finished executing battery");
	}

	public HashMap<String, TestImpl> getTestNames2Tests() {
		return myTestNames2Tests;
	}

	@Override
	public Set<String> getInterfacesUsed() {
		return myId2Interface.keySet();
	}

	public List<AbstractInterface> getInterfaces() {
		ArrayList<AbstractInterface> retVal = new ArrayList<AbstractInterface>(myId2Interface.values());
		Collections.sort(retVal);
		return retVal;
	}

}
