/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uhn.hunit.iface;

import ca.uhn.hunit.api.IJavaCallableInterface;
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
public class JavaCallableInterfaceImpl extends AbstractInterface {

    private Class<IJavaCallableInterface> myClazz;
    private boolean myNewInstanceForEachTest;
    private MesssageTypeEnum myMessageType;
    private IJavaCallableInterface myInstance;

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

        setNewInstanceForEachTest(theInterface.isNewInstanceForEachTest());

        setMessageType(theInterface.getMessageType());

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
    protected JavaCallableInterface exportConfig(Interface theConfig) {
        JavaCallableInterface config = (JavaCallableInterface) theConfig;
        super.exportConfig(config);

        config.setClazz(myClazz.getName());
        config.setNewInstanceForEachTest(myNewInstanceForEachTest);

        return config;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TestMessage generateDefaultReply(TestMessage theTestMessage) throws TestFailureException {
        throw new UnsupportedOperationException("Not supported");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected TestMessage internalSendMessage(TestMessage theMessage) throws TestFailureException {

        IJavaCallableInterface outboundReceiver = new IJavaCallableInterface() {

            @Override
            public void message(String theMessage, IJavaCallableInterface theOutboundAcceptor) {
                TestMessage testMessage = new TestMessage(theMessage);
                internalReceiveMessage(testMessage);
            }
        };

        getClassInstance().message(theMessage.getRawMessage(), outboundReceiver);

        return null;
    }

    private synchronized IJavaCallableInterface getClassInstance() throws UnexpectedTestFailureException {
        if (myInstance == null || myNewInstanceForEachTest) {
            try {
                myInstance = myClazz.newInstance();
            } catch (InstantiationException ex) {
                throw new UnexpectedTestFailureException("Exception instantiating " + myClazz.getName() + " using no-arg constructor", ex);
            } catch (IllegalAccessException ex) {
                throw new UnexpectedTestFailureException("Exception instantiating " + myClazz.getName() + " using no-arg constructor", ex);
            }
        }

        return myInstance;
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
    protected void doStart() throws InterfaceWontStartException {
        // ignore
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void doStop() throws InterfaceWontStopException {
        myInstance = null;
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
    protected boolean getCapabilitySupportsReply() {
        return false;
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

        if (!Callable.class.isAssignableFrom(classInstance)) {
            throw new PropertyVetoException("Class \"" + clazz + "\" does not implement " + IJavaCallableInterface.class.getName(), null);
        }

        myClazz = (Class<IJavaCallableInterface>) classInstance;
    }

    /**
     * Setter
     */
    public void setNewInstanceForEachTest(boolean theNewInstanceForEachTest) {
        myNewInstanceForEachTest = theNewInstanceForEachTest;
    }

    /**
     * Getter
     */
    public Class<IJavaCallableInterface> getMyClazz() {
        return myClazz;
    }

    /**
     * Getter
     */
    public boolean isMyNewInstanceForEachTest() {
        return myNewInstanceForEachTest;
    }

    /**
     * Setter
     */
    public void setMessageType(MesssageTypeEnum theMesssageTypeEnum) {
        myMessageType = theMesssageTypeEnum;
    }

    /**
     * Getter
     */
    public MesssageTypeEnum getMessageType() {
        return myMessageType;
    }
}
