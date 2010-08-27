/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uhn.hunit.iface;

import ca.uhn.hunit.api.IJavaCallableInterface;
import ca.uhn.hunit.api.IJavaCallableInterfaceReceiver;
import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.ex.InterfaceWontStopException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.ex.UnexpectedTestFailureException;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.xsd.AnyInterface;
import ca.uhn.hunit.xsd.Interface;
import ca.uhn.hunit.xsd.JavaCallableInterface;
import ca.uhn.hunit.xsd.MesssageTypeEnum;
import java.beans.PropertyVetoException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author James
 */
public class JavaCallableInterfaceImpl extends AbstractInterface<Object> {

    private Class<IJavaCallableInterface> myClazz;
    private MesssageTypeEnum myMessageType;
    private IJavaCallableInterface myInstance;
    private IJavaCallableInterfaceReceiver myReceiver = new MyReceiver();
    private boolean myNewInstanceForEachTest;

    /**
     * Should a new instance of the test class be instantiated for each test invocation?
     */
    public boolean isNewInstanceForEachTest() {
        return myNewInstanceForEachTest;
    }

    /**
     * {@inheritDoc }
     * @return Returns false
     */
    @Override
    public boolean isSupportsClear() {
        return false;
    }

    /**
     * Should a new instance of the test class be instantiated for each test invocation?
     */
    public void setNewInstanceForEachTest(boolean myNewInstanceForEachTest) {
        this.myNewInstanceForEachTest = myNewInstanceForEachTest;
    }

    /**
     * Constructor
     */
    public JavaCallableInterfaceImpl(TestBatteryImpl theBattery, JavaCallableInterface theInterface) throws ConfigurationException {
        super(theBattery, theInterface);

        try {
            setClazz(theInterface.getClazz());
        } catch (PropertyVetoException ex) {
            throw new ConfigurationException(ex.getMessage(), ex);
        }

        setMessageType(theInterface.getMessageType());

    }

    /**
     * Constructor
     */
    public JavaCallableInterfaceImpl(TestBatteryImpl theBattery, String theId) {
        super(theBattery, theId);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void doStart() throws InterfaceWontStartException {
        try {
            getClassInstance().start(myReceiver);
        } catch (UnexpectedTestFailureException e) {
            throw new InterfaceWontStartException(this, e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void doStartReceiving() throws InterfaceWontStartException {
        // ignore
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void doStartSending() throws InterfaceWontStartException {
        // ignore
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void doStop() throws InterfaceWontStopException {
        myInstance = null;
        try {
            getClassInstance().stop();
        } catch (UnexpectedTestFailureException e) {
            throw new InterfaceWontStopException(this, e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void doStopReceiving() throws InterfaceWontStopException {
        // ignore
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void doStopSending() throws InterfaceWontStopException {
        // ignore
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected JavaCallableInterface exportConfig(Interface theConfig) {
        JavaCallableInterface config = (JavaCallableInterface) theConfig;
        super.exportConfig(config);

        config.setClazz(myClazz.getName());

        return config;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AnyInterface exportConfigToXml() {
        JavaCallableInterface iface = new JavaCallableInterface();
        exportConfig(iface);

        AnyInterface retVal = new AnyInterface();
        retVal.setJavaCallableInterface(iface);
        return retVal;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TestMessage<Object> generateDefaultReply(TestMessage<Object> theTestMessage) throws TestFailureException {
        throw new UnsupportedOperationException("Not supported");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected boolean getCapabilitySupportsReply() {
        return false;
    }

    private synchronized IJavaCallableInterface getClassInstance() throws UnexpectedTestFailureException {
        if (myInstance == null) {
            try {
                myInstance = myClazz.newInstance();
            } catch (InstantiationException ex) {
                throw new UnexpectedTestFailureException("Exception instantiating " + myClazz.getName() + " using no-arg constructor: " + ex.getMessage(), ex);
            } catch (IllegalAccessException ex) {
                throw new UnexpectedTestFailureException("Exception instantiating " + myClazz.getName() + " using no-arg constructor: " + ex.getMessage(), ex);
            }
        }

        return myInstance;
    }

    /**
     * Getter
     */
    public MesssageTypeEnum getMessageType() {
        return myMessageType;
    }

    /**
     * Getter
     */
    public Class<IJavaCallableInterface> getClazz() {
        return myClazz;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected TestMessage<Object> internalSendMessage(TestMessage<Object> theMessage) throws TestFailureException {
        String reply = getClassInstance().sendMessageToInterface(theMessage.getRawMessage());
        if (reply == null) {
            return null;
        } else {
            return new TestMessage<Object>(reply);
        }
    }

    /**
     * Setter
     */
    public void setClazz(String clazz) throws PropertyVetoException {
        Class<?> classInstance;
        try {
            classInstance = Class.forName(clazz);
        } catch (ClassNotFoundException ex) {
            throw new PropertyVetoException(ex.getMessage(), null);
        }

        if (!IJavaCallableInterface.class.isAssignableFrom(classInstance)) {
            throw new PropertyVetoException("Class \"" + clazz + "\" does not implement " + IJavaCallableInterface.class.getName(), null);
        }

        myClazz = (Class<IJavaCallableInterface>) classInstance;
    }

    /**
     * Overridden to return false
     */
    @Override
    protected boolean isProducesReply() {
        return false;
    }

    /**
     * Setter
     */
    public void setMessageType(MesssageTypeEnum theMesssageTypeEnum) {
        myMessageType = theMesssageTypeEnum;
    }

    private final class MyReceiver implements IJavaCallableInterfaceReceiver {

        @Override
        public void receiveMessage(String theMessage) {
            TestMessage testMessage = new TestMessage(theMessage);
            JavaCallableInterfaceImpl.this.internalReceiveMessage(testMessage);
        }
    }
}
