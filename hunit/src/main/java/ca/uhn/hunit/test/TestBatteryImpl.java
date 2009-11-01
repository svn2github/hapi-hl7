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

import ca.uhn.hunit.event.InterfaceInteractionEnum;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.iface.JmsInterfaceImpl;
import ca.uhn.hunit.iface.MllpHl7V2InterfaceImpl;
import ca.uhn.hunit.l10n.Strings;
import ca.uhn.hunit.msg.AbstractMessage;
import ca.uhn.hunit.msg.Hl7V2MessageImpl;
import ca.uhn.hunit.msg.XmlMessageImpl;
import ca.uhn.hunit.util.AbstractModelClass;
import ca.uhn.hunit.util.IdUtil;
import ca.uhn.hunit.util.log.CommonsLoggingLog;
import ca.uhn.hunit.util.log.ILogProvider;
import ca.uhn.hunit.xsd.AnyInterface;
import ca.uhn.hunit.xsd.AnyMessageDefinitions;
import ca.uhn.hunit.xsd.Hl7V2MessageDefinition;
import ca.uhn.hunit.xsd.MessageDefinition;
import ca.uhn.hunit.xsd.ObjectFactory;
import ca.uhn.hunit.xsd.TestBattery;
import ca.uhn.hunit.xsd.XmlMessageDefinition;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.HashSet;
import javax.xml.bind.Marshaller;
import org.apache.commons.logging.Log;

public class TestBatteryImpl extends AbstractModelClass {

    public static final String PROP_INTERFACES = "PROP_INTERFACES";
    public static final String PROP_MESSAGES = "PROP_MESSAGES";
    private Map<String, AbstractInterface> myId2Interface = new HashMap<String, AbstractInterface>();
    private Map<String, AbstractMessage<?>> myId2Message = new HashMap<String, AbstractMessage<?>>();
    private String myName;
    private final BatteryTestModel myTestModel = new BatteryTestModel(this);
    private final List<AbstractMessage<?>> myMessages = new ArrayList<AbstractMessage<?>>();
    private final List<AbstractInterface> myInterfaces = new ArrayList<AbstractInterface>();
    private File myFile;
    private final ILogProvider myLogProvider;
    private final Log myLog;

    public TestBatteryImpl(File theDefFile, ILogProvider theLogProvider) throws ConfigurationException, JAXBException {
        load(theDefFile);

        myLogProvider = theLogProvider;
        myLog = myLogProvider.getSystem(TestBatteryImpl.class);
        myFile = theDefFile;
    }

    /**
     * Constructor which uses commons logging log
     */
    public TestBatteryImpl(File theDefFile) throws ConfigurationException, JAXBException {
        this(theDefFile, new CommonsLoggingLog());

        myFile = theDefFile;
    }

    private static TestBattery unmarshal(File theDefFile) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance("ca.uhn.hunit.xsd");
        Unmarshaller u = jaxbContext.createUnmarshaller();
        JAXBElement<TestBattery> root = u.unmarshal(new StreamSource(theDefFile), TestBattery.class);
        TestBattery battery = root.getValue();
        return battery;
    }

    public TestBatteryImpl(ILogProvider theLogProvider) {
        myName = "Untitled";
        myLogProvider = theLogProvider;
        myLog = myLogProvider.getSystem(TestBatteryImpl.class);
    }

    public TestBatteryImpl() {
        this(new CommonsLoggingLog());
    }

    public BatteryTestModel getTestModel() {
        return myTestModel;
    }

    private void initMessages(TestBattery theConfig) throws ConfigurationException {
        myId2Message.clear();
        myMessages.clear();
        AnyMessageDefinitions messages = theConfig.getMessages();
        if (messages != null) {
            for (MessageDefinition next : messages.getHl7V2OrXml()) {
                AbstractMessage<?> nextMessage;
                if (next instanceof Hl7V2MessageDefinition) {
                    nextMessage = new Hl7V2MessageImpl((Hl7V2MessageDefinition) next);
                } else if (next instanceof XmlMessageDefinition) {
                    nextMessage = new XmlMessageImpl((XmlMessageDefinition) next);
                } else {
                    throw new ConfigurationException("Unknown message type: " + next.getClass().getName());
                }
                myId2Message.put(nextMessage.getId(), nextMessage);
                myMessages.add(nextMessage);
            }
        }
        firePropertyChange(PROP_MESSAGES, null, null);
    }

    public AbstractMessage<?> getMessage(String theId) throws ConfigurationException {
        if (!myId2Message.containsKey(theId)) {
            throw new ConfigurationException("Unknown message ID[" + theId + "] - Valid values are: " + myId2Message.keySet());
        }
        return myId2Message.get(theId);
    }

    private void initTests(TestBattery theConfig) throws ConfigurationException {
        myTestModel.initFromXml(theConfig.getTests().getTest());
    }

    public AbstractInterface getInterface(String theId) throws ConfigurationException {
        if (!myId2Interface.containsKey(theId)) {
            throw new ConfigurationException("Unknown interface ID[" + theId + "] - Valid values are: " + myId2Interface.keySet());
        }
        return myId2Interface.get(theId);
    }

    private void initInterfaces(TestBattery theConfig) throws ConfigurationException {
        myInterfaces.clear();
        myId2Interface.clear();
        for (AnyInterface next : theConfig.getInterfaces().getInterface()) {

            AbstractInterface nextIf;
            if (next.getMllpHl7V2Interface() != null) {
                nextIf = new MllpHl7V2InterfaceImpl(this, next.getMllpHl7V2Interface());
            } else if (next.getJmsInterface() != null) {
                nextIf = new JmsInterfaceImpl(this, next.getJmsInterface());
            } else {
                throw new ConfigurationException("Unknown interface type in battery " + myName);
            }

            myInterfaces.add(nextIf);
            myId2Interface.put(nextIf.getId(), nextIf);
        }
        firePropertyChange(PROP_INTERFACES, null, null);
    }

    public String getName() {
        return myName;
    }

    public TestImpl getTestByName(String theName) {
        return myTestModel.getTestByName(theName);
    }

    public Set<String> getInterfacesUsed() {
        return myId2Interface.keySet();
    }

    public List<AbstractInterface> getInterfaces() {
        ArrayList<AbstractInterface> retVal = new ArrayList<AbstractInterface>(myId2Interface.values());
        Collections.sort(retVal);
        return retVal;
    }

    public List<String> getTestNames() {
        return myTestModel.getTestNames();
    }

    public List<AbstractMessage<?>> getMessages() {
        return myMessages;
    }

    public List<TestImpl> getTests() {
        return myTestModel.getTests();
    }

    public Set<String> getInterfaceIds() {
        return myId2Interface.keySet();
    }

    public Set<InterfaceInteractionEnum> getInterfaceInteractionTypes(String theInterfaceId) {
        Set<InterfaceInteractionEnum> retVal = new HashSet<InterfaceInteractionEnum>();
        for (TestImpl nextTest : getTests()) {
            if (nextTest.getEventsModel().getInterfaceInteractionTypes().containsKey(theInterfaceId)) {
                retVal.addAll(nextTest.getEventsModel().getInterfaceInteractionTypes().get(theInterfaceId));
            }
        }
        return retVal;
    }

    public void addEmptyMessageHl7V2() {
        Hl7V2MessageImpl newMessage = new Hl7V2MessageImpl(IdUtil.nextId(myId2Message.keySet()));
        addMessage(newMessage);
    }

    public void addEmptyMessageXml() {
        XmlMessageImpl newMessage = new XmlMessageImpl(IdUtil.nextId(myId2Message.keySet()));
        addMessage(newMessage);
    }

    private void addMessage(AbstractMessage<?> newMessage) {
        myId2Message.put(newMessage.getId(), newMessage);
        myMessages.add(newMessage);
        firePropertyChange(PROP_MESSAGES, null, newMessage);
    }

    public void addEmptyInterfaceMllpHl7V2() {
        String id = IdUtil.nextId(myId2Interface.keySet());
        addInterface(new MllpHl7V2InterfaceImpl(this, id));
    }

    public void addEmptyInterfaceJms() {
        String id = IdUtil.nextId(myId2Interface.keySet());
        addInterface(new JmsInterfaceImpl(this, id));
    }

    private void addInterface(AbstractInterface theInterface) {
        myId2Interface.put(myName, theInterface);
        myInterfaces.add(theInterface);
        firePropertyChange(PROP_INTERFACES, null, theInterface);
    }

    public Set<String> getMessageIds() {
        return myId2Message.keySet();
    }

    @Override
    public TestBattery exportConfigToXml() {
        TestBattery retVal = new TestBattery();
        retVal.setTests(new TestBattery.Tests());
        retVal.setInterfaces(new TestBattery.Interfaces());
        retVal.setMessages(new AnyMessageDefinitions());

        retVal.setName(myName);
        for (TestImpl next : myTestModel.getTests()) {
            retVal.getTests().getTest().add(next.exportConfigToXml());
        }
        for (AbstractInterface next : myInterfaces) {
            retVal.getInterfaces().getInterface().add(next.exportConfigToXml());
        }
        for (AbstractMessage<?> next : myMessages) {
            retVal.getMessages().getHl7V2OrXml().add(next.exportConfigToXml());
        }
        return retVal;
    }

    /**
     * Returns the config file associated with this battery, if any
     */
    public File getFile() {
        return myFile;
    }

    /**
     * Sets the config file associated with this battery, if any
     */
    public void setFile(File myFile) {
        this.myFile = myFile;
    }

    /**
     * Saves the battery config to the file provided by {@link #getFile() }.
     *
     * @throws JAXBException If a save error occurs
     * @throws IllegalStateException If no file is defined for this battery
     */
    public void save() throws JAXBException, IOException {
        if (myFile == null) {
            throw new IllegalStateException("Battery has no file to save to");
        }

        JAXBContext jaxbContext = JAXBContext.newInstance("ca.uhn.hunit.xsd");
        Marshaller m = jaxbContext.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        JAXBElement<TestBattery> object = new ObjectFactory().createBattery(exportConfigToXml());

        StringWriter sw = new StringWriter();
        m.marshal(object, sw);

        StringBuffer xmlString = sw.getBuffer();
        int declarationIndex = xmlString.lastIndexOf("?>");
        if (declarationIndex != -1) {
            String prolog = Strings.getMessage("xml.prolog");
            prolog = prolog.replaceAll("\\\\n", Strings.getLineSeparator());
            xmlString.insert(declarationIndex + 2, prolog);
        }

        if (myFile.exists()) {
            myFile.delete();
        }
        FileWriter writer = new FileWriter(myFile);
        writer.append(xmlString);
        writer.close();
    }

    public void load(File inputFile) throws JAXBException, ConfigurationException {
        TestBattery config = unmarshal(inputFile);

        myName = config.getName();
        initInterfaces(config);
        initMessages(config);

        // Init tests last since they will depend on other things to be ready
        initTests(config);
    }
}
