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
package ca.uhn.hunit.iface;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceWontReceiveException;
import ca.uhn.hunit.ex.InterfaceWontSendException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.ex.InterfaceWontStopException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.util.Log;
import ca.uhn.hunit.util.TypedValueListTableModel;
import ca.uhn.hunit.xsd.JavaArgument;
import ca.uhn.hunit.xsd.AbstractJmsInterface;
import ca.uhn.hunit.xsd.NamedJavaArgument;

public abstract class AbstractJmsInterfaceImpl<T extends Object> extends AbstractInterface {

	private boolean myStarted;
	private boolean myStopped;
    private String myQueueName;
    private Class< ? > myConnectionFactoryClass;
    private String myUsername;
    private String myPassword;
    private Constructor< ? > myConstructor;
    private JmsTemplate myJmsTemplate;
    private boolean myPubSubDomain;
    private final TypedValueListTableModel myConstructorArgsTableModel;
    private final TypedValueListTableModel myMessagePropertyTableModel;

	public AbstractJmsInterfaceImpl(AbstractJmsInterface theConfig) throws ConfigurationException {
		super(theConfig);
		
		myQueueName = theConfig.getQueueName();
		if (myQueueName == null) {
		    myQueueName = theConfig.getTopicName();
		    myPubSubDomain = true;
		}
		try {
            myConnectionFactoryClass = Class.forName(theConfig.getConnectionFactory());
        } catch (ClassNotFoundException e) {
            throw new ConfigurationException("Unknown connection factory: " + theConfig.getConnectionFactory());
        }
                
        myConstructorArgsTableModel = new TypedValueListTableModel(false);
        myConstructorArgsTableModel.setValues(theConfig.getConnectionFactoryConstructorArg());
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

        myMessagePropertyTableModel = new TypedValueListTableModel(true);
        myMessagePropertyTableModel.setValues(theConfig.getMessageProperty());
		
	}

    public static void extractArgsFromXml(List<Class< ? >> theArgTypeList, List<String> theNames, List<Object> theArgs,
            List<JavaArgument> theArgDefinitions) throws ConfigurationException {
        
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
                theNames.add(((NamedJavaArgument)next).getName());
            }
        }
    }

    
    public static void extractNamedArgsFromXml(List<Class< ? >> theArgTypeList, List<String> theNames, List<Object> theArgs,
            List<NamedJavaArgument> theArgDefinitions) throws ConfigurationException {
        List<JavaArgument> argDefs = new ArrayList<JavaArgument>();
        for (NamedJavaArgument next : theArgDefinitions) {
            argDefs.add(next);
        }
        
        extractArgsFromXml(theArgTypeList, theNames, theArgs, argDefs);
    }

    
	@Override
	public TestMessage<?> receiveMessage(TestImpl theTest, ExecutionContext theCtx, long theTimeout) throws TestFailureException {
		start(theCtx);

		Log.get(this).info( "Waiting to receive message");

		String message = null;
		try {
			long endTime = System.currentTimeMillis() + theTimeout;
			while (!myStopped && message == null && System.currentTimeMillis() < endTime) {
				try {
					message = doReceiveMessage();
				} catch (JmsException e) {
		            throw new InterfaceWontReceiveException(this, e.getMessage(), e);
				} catch (JMSException e) {
		            throw new InterfaceWontReceiveException(this, e.getMessage(), e);
                }
			}
			if (myStopped || message == null) {
				return null;
			}
						
			Log.get(this).info( "Received message (" + message.length() + " bytes)");

		    return new TestMessage<Object>(message, null);

		} catch (JmsException e) {
			throw new InterfaceWontReceiveException(this, e.getMessage(), e);
        }

	}

	@Override
	public void sendMessage(TestImpl theTest, ExecutionContext theCtx, final TestMessage<?> theMessage) throws TestFailureException {
		start(theCtx);

		Log.get(this).info( "Sending message (" + theMessage.getRawMessage().length() + " bytes)");

		try {
		    MessageCreator mc = new MessageCreator() {
                public javax.jms.Message createMessage(Session theSession) throws JMSException {
                    TextMessage textMessage = theSession.createTextMessage(theMessage.getRawMessage());
                    
                    for (int i = 0; i < myMessagePropertyTableModel.getRowCount(); i++) {
                        if (java.lang.String.class.equals(myMessagePropertyTableModel.getArgType(i))) {
                            textMessage.setStringProperty(myMessagePropertyTableModel.getName(i), (String)myMessagePropertyTableModel.getArg(i));
                        } else if (java.lang.Integer.class.equals(myMessagePropertyTableModel.getArgType(i))) {
                            textMessage.setIntProperty(myMessagePropertyTableModel.getName(i), (Integer)myMessagePropertyTableModel.getArg(i));
                        } else if (int.class.equals(myMessagePropertyTableModel.getArgType(i))) {
                            textMessage.setIntProperty(myMessagePropertyTableModel.getName(i), (Integer)myMessagePropertyTableModel.getArg(i));
                        }
                    }
                    return textMessage;
                }};
            myJmsTemplate.send(myQueueName, mc);
			Log.get(this).info( "Sent message");
		} catch (JmsException e) {
			throw new InterfaceWontSendException(this, e.getMessage(), e);
		}

	}

	@Override
	public void start(ExecutionContext theCtx) throws InterfaceWontStartException {
		if (myStarted) {
			return;
		}

		ConnectionFactory connectionFactory;
        try {
            connectionFactory = (ConnectionFactory)myConstructor.newInstance(myConstructorArgsTableModel.getArgArray());
        } catch (IllegalArgumentException e1) {
            throw new InterfaceWontStartException(this, e1.getMessage());
        } catch (InstantiationException e1) {
            throw new InterfaceWontStartException(this, e1.getMessage());
        } catch (IllegalAccessException e1) {
            throw new InterfaceWontStartException(this, e1.getMessage());
        } catch (InvocationTargetException e1) {
            throw new InterfaceWontStartException(this, e1.getMessage());
        }
        
		connectionFactory = new JmsConnectionFactory(connectionFactory, myUsername, myPassword);
		myJmsTemplate = new JmsTemplate(connectionFactory);
		myJmsTemplate.setReceiveTimeout(250);
        myJmsTemplate.setPubSubDomain(myPubSubDomain);

		if (isClear()) {
			long readUntil = System.currentTimeMillis() + getClearMillis();
			int cleared = 0;
			while (System.currentTimeMillis() < readUntil) {
				try {
                    String message = doReceiveMessage();
					
					if (message == null || message.length() == 0) {
					    try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            // nothing
                        }
                        continue;
					}
					cleared++;
					Log.get(this).info( "Cleared message");
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        // nothing
                    }
                    readUntil = System.currentTimeMillis() + getClearMillis();
				} catch (JMSException e) {
				    Log.get(this).warn("Error while clearing queue: " + e.getMessage());
                }
			}
			Log.get(this).info( "Cleared " + cleared + " messages from interface before starting");
		}
		
		Log.get(this).info( "Started interface successfully");
		myStarted = true;
	}

    private String doReceiveMessage() throws JMSException {
        String message = null;
        javax.jms.Message jmsMessage = myJmsTemplate.receive(myQueueName);
        if (jmsMessage != null) {
            TextMessage jmsTextMessage = (TextMessage)jmsMessage;
            message = jmsTextMessage.getText();
        }
        return message;
    }

	@Override
	public void stop(ExecutionContext theCtx) throws InterfaceWontStopException {
		if (!myStarted) {
			return;
		}
		if (myStopped) {
			return;
		}

		Log.get(this).info( "Stopping interface");

		myStarted = false;
	}

	@Override
	public boolean isStarted() {
		return myStarted;
	}

	protected void exportConfig(AbstractJmsInterface retVal) {
		super.exportConfig(retVal);
		retVal.setClear(isClear());
		retVal.setConnectionFactory(myConnectionFactoryClass.getName());
		retVal.setPassword(myPassword);
		retVal.setQueueName(myPubSubDomain ? null : myQueueName);
		retVal.setTopicName(myPubSubDomain ? myQueueName : null);
		retVal.setUserName(myUsername);
	}

    public TypedValueListTableModel getConstructorArgsTableModel() {
        return myConstructorArgsTableModel;
    }

    public TypedValueListTableModel getMessagePropertyTableModel() {
        return myMessagePropertyTableModel;
    }

    public Class<?> getConnectionFactoryClass() {
        return myConnectionFactoryClass;
    }

    public void setConnectionFactoryClass(Class<?> myConnectionFactoryClass) {
        this.myConnectionFactoryClass = myConnectionFactoryClass;
    }

    public String getPassword() {
        return myPassword;
    }

    public void setPassword(String myPassword) {
        this.myPassword = myPassword;
    }

    public boolean isPubSubDomain() {
        return myPubSubDomain;
    }

    public void setPubSubDomain(boolean myPubSubDomain) {
        this.myPubSubDomain = myPubSubDomain;
    }

    public String getQueueName() {
        return myQueueName;
    }

    public void setQueueName(String myQueueName) {
        this.myQueueName = myQueueName;
    }

    public String getUsername() {
        return myUsername;
    }

    public void setUsername(String myUsername) {
        this.myUsername = myUsername;
    }


}
