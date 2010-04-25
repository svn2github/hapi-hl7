/**
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.Resource;

import ca.uhn.hunit.event.InterfaceInteractionEnum;
import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.iface.AbstractInterface;
import ca.uhn.hunit.iface.JavaCallableInterfaceImpl;
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

public class TestBatteryImpl extends AbstractModelClass {
    // ~ Static fields/initializers
    // -------------------------------------------------------------------------------------

    public static final String PROP_INTERFACES = "PROP_INTERFACES";
    public static final String PROP_FILE = "PROP_FILE";
    public static final String PROP_MESSAGES = "PROP_MESSAGES";

    // ~ Instance fields
    // ------------------------------------------------------------------------------------------------
    private final BatteryTestModel myTestModel = new BatteryTestModel(this);
    private File myFile;
    private final List<AbstractInterface<?>> myInterfaces = new ArrayList<AbstractInterface<?>>();
    private final List<AbstractMessage<?>> myMessages = new ArrayList<AbstractMessage<?>>();
    private Map<String, AbstractMessage<?>> myId2Message = new HashMap<String, AbstractMessage<?>>();
    private String myName;

    // ~ Constructors
    // ---------------------------------------------------------------------------------------------------
    public TestBatteryImpl(ILogProvider theLogProvider) {
        myName = "Untitled";
    }

    public TestBatteryImpl() {
        this(new CommonsLoggingLog());
    }

    public TestBatteryImpl(Resource theDefFile, ILogProvider theLogProvider) throws ConfigurationException, JAXBException {
        load(theDefFile);

        try {
            myFile = theDefFile.getFile();
        } catch (IOException ex) {
            // nothing
        }
    }

    /**
     * Constructor which uses commons logging log
     */
    public TestBatteryImpl(Resource theDefFile) throws ConfigurationException, JAXBException {
        this(theDefFile, new CommonsLoggingLog());

        try {
            myFile = theDefFile.getFile();
        } catch (IOException ex) {
            // nothing
        }
    }

    /**
     * Constructor which accepts programmatic config
     */
    public TestBatteryImpl(TestBattery theBatteryConfig) throws ConfigurationException {
        load(theBatteryConfig);
    }

    // ~ Methods
    // --------------------------------------------------------------------------------------------------------
    public void addEmptyInterfaceJms() {
        String id = IdUtil.nextId(getInterfaceIds());
        addInterface(new JmsInterfaceImpl(this, id));
    }

    public void addEmptyInterfaceMllpHl7V2() {
        String id = IdUtil.nextId(getInterfaceIds());
        addInterface(new MllpHl7V2InterfaceImpl(this, id));
    }

    public void addEmptyMessageHl7V2() {
        Hl7V2MessageImpl newMessage = new Hl7V2MessageImpl(IdUtil.nextId(myId2Message.keySet()));
        addMessage(newMessage);
    }

    public void addEmptyMessageXml() {
        XmlMessageImpl newMessage = new XmlMessageImpl(IdUtil.nextId(myId2Message.keySet()));
        addMessage(newMessage);
    }

    /**
     * Adds a new test with no events
     */
    public void addEmptyTest() {
        List<String> currentTestNames = myTestModel.getTestNames();
        String newName = IdUtil.nextId(currentTestNames);
        TestImpl test = new TestImpl(this, newName);
        myTestModel.addTest(test);
    }

    private void addInterface(AbstractInterface<?> theInterface) {
        myInterfaces.add(theInterface);
        firePropertyChange(PROP_INTERFACES, null, theInterface);
    }

    private void addMessage(AbstractMessage<?> newMessage) {
        myId2Message.put(newMessage.getId(), newMessage);
        myMessages.add(newMessage);
        firePropertyChange(PROP_MESSAGES, null, newMessage);
    }

    /**
     * To be called when this battery is being closed or disposed, allowing it
     * to clean up any held resources.
     */
    public void dispose() {
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

        for (AbstractInterface<?> next : myInterfaces) {
            retVal.getInterfaces().getInterface().add(next.exportConfigToXml());
        }

        return retVal;
    }

    /**
     * Returns the config file associated with this battery, if any
     */
    public File getFile() {
        return myFile;
    }

    public AbstractInterface<?> getInterface(String theId) throws ConfigurationException {
        for (AbstractInterface<?> next : myInterfaces) {
            if (theId.equals(next.getId())) {
                return next;
            }
        }

        throw new ConfigurationException("Unknown interface ID[" + theId + "] - Valid values are: " + getInterfaceIds());
    }

    public List<String> getInterfaceIds() {
        ArrayList<String> retVal = new ArrayList<String>();

        for (AbstractInterface<?> next : myInterfaces) {
            retVal.add(next.getId());
        }

        return retVal;
    }

    public Set<InterfaceInteractionEnum> getInterfaceInteractionTypes(AbstractInterface<?> theInterfaceId) {
        Set<InterfaceInteractionEnum> retVal = new HashSet<InterfaceInteractionEnum>();

        for (TestImpl nextTest : getTests()) {
            if (nextTest.getEventsModel().getInterfaceInteractionTypes().containsKey(theInterfaceId)) {
                retVal.addAll(nextTest.getEventsModel().getInterfaceInteractionTypes().get(theInterfaceId));
            }
        }

        return retVal;
    }

    public List<AbstractInterface<?>> getInterfaces() {
        ArrayList<AbstractInterface<?>> retVal = new ArrayList<AbstractInterface<?>>(myInterfaces);
        Collections.sort(retVal);

        return retVal;
    }

    public AbstractMessage<?> getMessage(String theId) throws ConfigurationException {
        if (!myId2Message.containsKey(theId)) {
            throw new ConfigurationException("Unknown message ID[" + theId + "] - Valid values are: " + myId2Message.keySet());
        }

        return myId2Message.get(theId);
    }

    public Set<String> getMessageIds() {
        return myId2Message.keySet();
    }

    public List<AbstractMessage<?>> getMessages() {
        return myMessages;
    }

    public String getName() {
        return myName;
    }

    public TestImpl getTestByName(String theName) {
        return myTestModel.getTestByName(theName);
    }

    public BatteryTestModel getTestModel() {
        return myTestModel;
    }

    public List<String> getTestNames() {
        return myTestModel.getTestNames();
    }

    public List<TestImpl> getTests() {
        return myTestModel.getTests();
    }

    private void initInterfaces(TestBattery theConfig) throws ConfigurationException {
        myInterfaces.clear();

        for (AnyInterface next : theConfig.getInterfaces().getInterface()) {
            AbstractInterface<?> nextIf;

            if (next.getMllpHl7V2Interface() != null) {
                nextIf = new MllpHl7V2InterfaceImpl(this, next.getMllpHl7V2Interface());
            } else if (next.getJmsInterface() != null) {
                nextIf = new JmsInterfaceImpl(this, next.getJmsInterface());
            } else if (next.getJavaCallableInterface() != null) {
                nextIf = new JavaCallableInterfaceImpl(this, next.getJavaCallableInterface());
            } else {
                throw new ConfigurationException("Unknown interface type \"" + next.getClass().getName() + "\" in battery " + myName);
            }

            myInterfaces.add(nextIf);
        }

        firePropertyChange(PROP_INTERFACES, null, null);
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

    private void initTests(TestBattery theConfig) throws ConfigurationException {
        myTestModel.initFromXml(theConfig.getTests().getTest());
    }

    /**
     * Returns true if this battery has no data
     */
    public boolean isEmpty() {
        return myInterfaces.isEmpty() && myId2Message.isEmpty() && (myTestModel.getRowCount() == 0);
    }

    public void load(Resource inputFile) throws JAXBException, ConfigurationException {
        Source source;

        try {
            source = new StreamSource(inputFile.getInputStream());
        } catch (IOException ex) {
            try {
                source = new StreamSource(inputFile.getFile());
            } catch (IOException ex1) {
                throw new ConfigurationException(ex1.getMessage());
            }
        }

        TestBattery config = unmarshal(source);

        load(config);
    }

    private void load(TestBattery config) throws ConfigurationException {
        myName = config.getName();
        initInterfaces(config);
        initMessages(config);

        // Init tests last since they will depend on other things to be ready
        initTests(config);
    }

    /**
     * Saves the battery config to the file provided by {@link #getFile() }.
     * 
     * @throws JAXBException
     *             If a save error occurs
     * @throws IllegalStateException
     *             If no file is defined for this battery
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

    /**
     * Sets the config file associated with this battery, if any
     */
    public void setFile(File theFile) {
        File oldValue = this.myFile;
        this.myFile = theFile;
        firePropertyChange(PROP_FILE, oldValue, myFile);
    }

    /**
     * @param theName
     *            Sets the name for this battery
     */
    public void setName(String theName) {
        myName = theName;
    }

    private static TestBattery unmarshal(final Source source) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance("ca.uhn.hunit.xsd");
        Unmarshaller u = jaxbContext.createUnmarshaller();
        JAXBElement<TestBattery> root = u.unmarshal(source, TestBattery.class);
        TestBattery battery = root.getValue();

        return battery;
    }

    public void addEmptyInterfaceJavaCallable() {
        String id = IdUtil.nextId(getInterfaceIds());
        addInterface(new JavaCallableInterfaceImpl(this, id));
    }
}
