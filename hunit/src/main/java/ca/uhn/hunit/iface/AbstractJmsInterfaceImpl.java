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
package ca.uhn.hunit.iface;

import java.beans.PropertyVetoException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang.StringUtils;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import ca.uhn.hunit.event.InterfaceInteractionEnum;
import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceWontSendException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.ex.InterfaceWontStopException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.l10n.Strings;
import ca.uhn.hunit.run.IExecutionContext;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.util.TypedValueListTableModel;
import ca.uhn.hunit.util.log.LogFactory;
import ca.uhn.hunit.xsd.AbstractJmsInterface;
import ca.uhn.hunit.xsd.Interface;
import ca.uhn.hunit.xsd.JavaArgument;
import ca.uhn.hunit.xsd.NamedJavaArgument;

public abstract class AbstractJmsInterfaceImpl<T extends Object> extends AbstractInterface<T> {
    //~ Static fields/initializers -------------------------------------------------------------------------------------

    public static final String CONNECTION_FACTORY_CLASS_PROPERTY =
        AbstractJmsInterfaceImpl.class + "CONNECTION_FACTORY_CLASS_PROPERTY";

    //~ Instance fields ------------------------------------------------------------------------------------------------

    private Class<?> myConnectionFactoryClass;
    private Constructor<?> myConstructor;
    private DefaultMessageListenerContainer myMessageListenerContainer;
    private JmsTemplate myJmsTemplate;
    private String myPassword;
    private String myQueueName;
    private String myUsername;
    private final TypedValueListTableModel myConstructorArgsTableModel = new TypedValueListTableModel(false);
    private final TypedValueListTableModel myMessagePropertyTableModel = new TypedValueListTableModel(true);
    private boolean myPubSubDomain;
    private boolean myStarted;
    private boolean myStopped;
	private ConnectionFactory myConnectionFactory;

    //~ Constructors ---------------------------------------------------------------------------------------------------

    public AbstractJmsInterfaceImpl(TestBatteryImpl theBattery, String theId) {
        super(theBattery, theId);

        init();
    }

    public AbstractJmsInterfaceImpl(TestBatteryImpl theBattery, AbstractJmsInterface theConfig)
                             throws ConfigurationException {
        super(theBattery, theConfig);

        myQueueName = theConfig.getQueueName();

        if (myQueueName == null) {
            myQueueName = theConfig.getTopicName();
            myPubSubDomain = true;
        }

        if (theConfig.getConnectionFactory() != null) {
            try {
                myConnectionFactoryClass = Class.forName(theConfig.getConnectionFactory());
            } catch (ClassNotFoundException e) {
                throw new ConfigurationException("Unknown connection factory: " + theConfig.getConnectionFactory());
            }
        }

        myConstructorArgsTableModel.importValuesFromXml(theConfig.getConnectionFactoryConstructorArg());

        try {
            myConstructor = myConnectionFactoryClass.getConstructor(myConstructorArgsTableModel.getArgTypeArray());
        } catch (SecurityException e) {
            throw new ConfigurationException("Error creating connection factory: ", e);
        } catch (NoSuchMethodException e) {
            throw new ConfigurationException("Error creating connection factory: ", e);
        }

        myUsername = theConfig.getUserName();
        myPassword = theConfig.getPassword();
        myStarted = false;
        myStopped = false;

        myMessagePropertyTableModel.importValuesFromXml(theConfig.getMessageProperty());

        init();
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc }
     */
    protected Interface exportConfig(AbstractJmsInterface retVal) {
        super.exportConfig(retVal);

        myConstructorArgsTableModel.exportValuesToXml(retVal.getConnectionFactoryConstructorArg());
        myMessagePropertyTableModel.exportValuesToXml(retVal.getMessageProperty());

        retVal.setConnectionFactory(myConnectionFactoryClass.getName());
        retVal.setPassword(myPassword);
        retVal.setQueueName(myPubSubDomain ? null : myQueueName);
        retVal.setTopicName(myPubSubDomain ? myQueueName : null);
        retVal.setUserName(myUsername);

        return retVal;
    }

    public static void extractArgsFromXml(List<Class<?>> theArgTypeList, List<String> theNames, List<Object> theArgs,
                                          List<JavaArgument> theArgDefinitions)
                                   throws ConfigurationException {
        for (JavaArgument next : theArgDefinitions) {
            if ("java.lang.String".equals(next.getType())) {
                theArgTypeList.add(String.class);
                theArgs.add(next.getValue());
            } else if ("java.lang.Integer".equals(next.getType())) {
                theArgTypeList.add(Integer.class);
                theArgs.add(Integer.parseInt(next.getValue()));
            } else if ("int".equals(next.getType())) {
                theArgTypeList.add(int.class);
                theArgs.add(Integer.parseInt(next.getValue()));
            } else {
                throw new ConfigurationException("Unknown arg type: " + next.getType());
            }

            if (theNames != null) {
                theNames.add(((NamedJavaArgument) next).getName());
            }
        }
    }

    public static void extractNamedArgsFromXml(List<Class<?>> theArgTypeList, List<String> theNames,
                                               List<Object> theArgs, List<NamedJavaArgument> theArgDefinitions)
                                        throws ConfigurationException {
        List<JavaArgument> argDefs = new ArrayList<JavaArgument>();

        for (NamedJavaArgument next : theArgDefinitions) {
            argDefs.add(next);
        }

        extractArgsFromXml(theArgTypeList, theNames, theArgs, argDefs);
    }

    public Class<?> getConnectionFactoryClass() {
        return myConnectionFactoryClass;
    }

    public TypedValueListTableModel getConstructorArgsTableModel() {
        return myConstructorArgsTableModel;
    }

    public TypedValueListTableModel getMessagePropertyTableModel() {
        return myMessagePropertyTableModel;
    }

    public String getPassword() {
        return myPassword;
    }

    public String getQueueName() {
        return myQueueName;
    }

    public String getUsername() {
        return myUsername;
    }

    private void init() {
        myUsername = StringUtils.defaultString(myUsername);
        myPassword = StringUtils.defaultString(myPassword);
    }

    public boolean isPubSubDomain() {
        return myPubSubDomain;
    }


    @Override
	protected TestMessage<T> internalSendMessage(final TestMessage<T> theMessage) throws TestFailureException {
        LogFactory.INSTANCE.get(this).info("Sending message (" + theMessage.getRawMessage().length() + " bytes)");

        try {
            MessageCreator mc =
                new MessageCreator() {
                    @Override
                    public javax.jms.Message createMessage(Session theSession)
                                                    throws JMSException {
                        TextMessage textMessage = theSession.createTextMessage(theMessage.getRawMessage());

                        for (int i = 0; i < myMessagePropertyTableModel.getRowCount(); i++) {
                            if (java.lang.String.class.equals(myMessagePropertyTableModel.getArgType(i))) {
                                textMessage.setStringProperty(myMessagePropertyTableModel.getName(i),
                                                              (String) myMessagePropertyTableModel.getArg(i));
                            } else if (java.lang.Integer.class.equals(myMessagePropertyTableModel.getArgType(i))) {
                                textMessage.setIntProperty(myMessagePropertyTableModel.getName(i),
                                                           (Integer) myMessagePropertyTableModel.getArg(i));
                            } else if (int.class.equals(myMessagePropertyTableModel.getArgType(i))) {
                                textMessage.setIntProperty(myMessagePropertyTableModel.getName(i),
                                                           (Integer) myMessagePropertyTableModel.getArg(i));
                            }
                        }

                        return textMessage;
                    }
                };

            myJmsTemplate.send(myQueueName, mc);
            LogFactory.INSTANCE.get(this).info("Sent message");
            
        } catch (JmsException e) {
            throw new InterfaceWontSendException(this,
                                                 e.getMessage(), e);
        }

        return null;
	}


    public void setConnectionFactoryClass(Class<?> theConnectionFactoryClass)
                                   throws PropertyVetoException {
        Class<?> oldValue = theConnectionFactoryClass;

        if (! theConnectionFactoryClass.isAssignableFrom(ConnectionFactory.class)) {
            throw new PropertyVetoException("Must extend " + ConnectionFactory.class, null);
        }

        fireVetoableChange(CONNECTION_FACTORY_CLASS_PROPERTY, oldValue, theConnectionFactoryClass);
        this.myConnectionFactoryClass = theConnectionFactoryClass;
        firePropertyChange(CONNECTION_FACTORY_CLASS_PROPERTY, oldValue, theConnectionFactoryClass);
    }

    public void setPassword(String myPassword) {
        this.myPassword = myPassword;
    }

    public void setPubSubDomain(boolean myPubSubDomain) {
        this.myPubSubDomain = myPubSubDomain;
    }

    public void setQueueName(String myQueueName) throws PropertyVetoException {
        if (StringUtils.isBlank(myQueueName)) {
            throw new PropertyVetoException(Strings.getInstance().getString("interface.queue.empty"), null);
        }

        this.myQueueName = myQueueName;
    }

    public void setUsername(String myUsername) {
        this.myUsername = myUsername;
    }



    //~ Inner Classes --------------------------------------------------------------------------------------------------

    private class MyMessageListener implements MessageListener {

        public void onMessage(Message message) {
        	LogFactory.INSTANCE.get(AbstractJmsInterfaceImpl.this).info("Message arrived");

            String messageText;
                try {
                    TextMessage jmsTextMessage = (TextMessage) message;
					messageText = jmsTextMessage.getText();
		            internalReceiveMessage(new TestMessage<T>(messageText, null));
				} catch (JMSException e) {
					LogFactory.INSTANCE.get(AbstractJmsInterfaceImpl.this).warn("Failure while extracting JMS message: " + e.getMessage(), e);
				}
            
        }
    }
    
    /**
     * {@inheritDoc}
     */
	@Override
	protected void doStartReceiving() throws InterfaceWontStartException {
    	LogFactory.INSTANCE.get(this).info("Starting JMS listener");
        myMessageListenerContainer = new DefaultMessageListenerContainer();
        myMessageListenerContainer.setBeanName(getId());
        myMessageListenerContainer.setAutoStartup(true);
        myMessageListenerContainer.setAcceptMessagesWhileStopping(false);
        myMessageListenerContainer.setClientId("hUnit-" + getId());
        myMessageListenerContainer.setConcurrentConsumers(1);
        myMessageListenerContainer.setConnectionFactory(myConnectionFactory);
        myMessageListenerContainer.setPubSubDomain(myPubSubDomain);
        myMessageListenerContainer.setDestinationName(myQueueName);
        myMessageListenerContainer.setMessageListener(new MyMessageListener());
        myMessageListenerContainer.afterPropertiesSet();
	}

    /**
     * {@inheritDoc}
     */
	@Override
	protected void doStart() throws InterfaceWontStartException {
        try {
            myConnectionFactory = (ConnectionFactory) myConstructor.newInstance(myConstructorArgsTableModel.getArgArray());
        } catch (IllegalArgumentException e1) {
            throw new InterfaceWontStartException(this,
                                                  e1.getMessage());
        } catch (InstantiationException e1) {
            throw new InterfaceWontStartException(this,
                                                  e1.getMessage());
        } catch (IllegalAccessException e1) {
            throw new InterfaceWontStartException(this,
                                                  e1.getMessage());
        } catch (InvocationTargetException e1) {
            throw new InterfaceWontStartException(this,
                                                  e1.getMessage());
        }

        myConnectionFactory = new JmsConnectionFactory(myConnectionFactory, myUsername, myPassword);
        myJmsTemplate = new JmsTemplate(myConnectionFactory);
        myJmsTemplate.setReceiveTimeout(1000);
        myJmsTemplate.setPubSubDomain(myPubSubDomain);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	protected void doStartSending() throws InterfaceWontStartException {
		// nothing
	}

    /**
     * {@inheritDoc}
     */
	@Override
	protected void doStop() throws InterfaceWontStopException {
		// nothing
	}

    /**
     * {@inheritDoc}
     */
	@Override
	protected void doStopReceiving() throws InterfaceWontStopException {
        if (myMessageListenerContainer != null) {
            myMessageListenerContainer.shutdown();
        }
	}

    /**
     * {@inheritDoc}
     */
	@Override
	protected void doStopSending() throws InterfaceWontStopException {
		// nothing
	}
	
}
