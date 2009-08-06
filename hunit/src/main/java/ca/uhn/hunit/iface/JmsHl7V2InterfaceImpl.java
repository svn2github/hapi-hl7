package ca.uhn.hunit.iface;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.DefaultXMLParser;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.ValidationContextImpl;
import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.IncorrectMessageReceivedException;
import ca.uhn.hunit.ex.InterfaceException;
import ca.uhn.hunit.ex.InterfaceWontReceiveException;
import ca.uhn.hunit.ex.InterfaceWontSendException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.ex.InterfaceWontStopException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.ex.UnexpectedTestFailureException;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.xsd.JavaArgument;
import ca.uhn.hunit.xsd.JmsHl7V2Interface;

public class JmsHl7V2InterfaceImpl extends AbstractInterface {

	private boolean myClientMode;
	private boolean myStarted;
	private Integer myConnectionTimeout;
	private Integer myReceiveTimeout = 60000;
	private boolean myStopped;
	private Parser myParser;
    private String myQueueName;
    private Class< ? > myConnectionFactoryClass;
    private String myUsername;
    private String myPassword;
    private ArrayList<Object> myConstructorArgs;
    private ArrayList<Class< ? >> myConstructorArgTypes;
    private Boolean myClear;
    private Constructor< ? > myConstructor;
    private JmsTemplate myJmsTemplate;
    private Integer myClearMillis;
    private boolean myPubSubDomain;

	public JmsHl7V2InterfaceImpl(JmsHl7V2Interface theConfig) throws ConfigurationException {
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
                
        myConstructorArgTypes = new ArrayList<Class<?>>();
        myConstructorArgs = new ArrayList<Object>();
        for (JavaArgument next : theConfig.getConnectionFactoryConstructorArg()) {
            if ("java.lang.String".equals(next.getType())) {
                myConstructorArgTypes.add(String.class);
                myConstructorArgs.add(next.getValue());
            } else if ("java.lang.Integer".equals(next.getType())) {
                myConstructorArgTypes.add(Integer.class);
                myConstructorArgs.add(Integer.parseInt(next.getValue()));
            } else if ("int".equals(next.getType())) {
                myConstructorArgTypes.add(int.class);
                myConstructorArgs.add(Integer.parseInt(next.getValue()));
            } else {
                throw new ConfigurationException("Unknown arg type: " + next.getType());
            }
        }
        try {
            
            myConstructor = myConnectionFactoryClass.getConstructor(myConstructorArgTypes.toArray(new Class<?>[0]));
        } catch (SecurityException e) {
            throw new ConfigurationException("Error creating connection factory: ", e);
        } catch (NoSuchMethodException e) {
            throw new ConfigurationException("Error creating connection factory: ", e);
        }
        
		myUsername = theConfig.getUserName();
		myPassword = theConfig.getPassword();
		myStarted = false;
		myStopped = false;
		
		if ("XML".equals(theConfig.getEncoding())) {
			myParser = new DefaultXMLParser();
		} else {
			myParser = new PipeParser();
		}
		myParser.setValidationContext(new ValidationContextImpl());

		myClear = theConfig.isClear();
		if (myClear == null) {
		    myClear = true;
		}
	}

	@Override
	public TestMessage receiveMessage(TestImpl theTest, ExecutionContext theCtx) throws TestFailureException {
		start(theCtx);

		theCtx.getLog().info(this, "Waiting to receive message");

		String message = null;
		Message parsedMessage;
		try {
			long endTime = System.currentTimeMillis() + myReceiveTimeout;
			while (!myStopped && message == null && System.currentTimeMillis() < endTime) {
				try {
					message = doReceiveMessage();
				} catch (JmsException e) {
		            throw new InterfaceWontReceiveException(this, e.getMessage(), e);
				} catch (JMSException e) {
		            throw new InterfaceWontReceiveException(this, e.getMessage(), e);
                }
			}
			if (myStopped) {
				return null;
			}
			
			if (message == null) {
				throw new InterfaceWontReceiveException(this, "Didn't receive a message after " + myReceiveTimeout + "ms");
			}
			
			theCtx.getLog().info(this, "Received message (" + message.length() + " bytes)");

			try {
				parsedMessage = myParser.parse(message);
			} catch (EncodingNotSupportedException e) {
				throw new IncorrectMessageReceivedException(theTest, message, e.getMessage());
			} catch (HL7Exception e) {
				throw new IncorrectMessageReceivedException(theTest, message, e.getMessage());
			}

		     return new TestMessage(myParser.encode(parsedMessage), parsedMessage);

		} catch (JmsException e) {
			throw new InterfaceWontReceiveException(this, e.getMessage(), e);
		} catch (HL7Exception e) {
            throw new InterfaceWontReceiveException(this, e.getMessage(), e);
        }

	}

	@Override
	public void sendMessage(TestImpl theTest, ExecutionContext theCtx, final TestMessage theMessage) throws InterfaceException, UnexpectedTestFailureException {
		start(theCtx);

		if (theMessage.getRawMessage() == null) {
			try {
				theMessage.setRawMessage(myParser.encode((Message) theMessage.getParsedMessage()));
			} catch (HL7Exception e) {
				throw new UnexpectedTestFailureException("Can't encode message to send it: " + e.getMessage());
			}
		}
		
		theCtx.getLog().info(this, "Sending message (" + theMessage.getRawMessage().length() + " bytes)");

		try {
		    MessageCreator mc = new MessageCreator() {
                public javax.jms.Message createMessage(Session theSession) throws JMSException {
                    TextMessage textMessage = theSession.createTextMessage(theMessage.getRawMessage());
                    textMessage.setStringProperty("SOURCE_SYSTEM", "UHN_Ultra");
                    return textMessage;
                }};
            myJmsTemplate.send(myQueueName, mc);
			theCtx.getLog().info(this, "Sent message");
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
            connectionFactory = (ConnectionFactory)myConstructor.newInstance(myConstructorArgs.toArray());
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

		if (myClear) {
		    myClearMillis = 600;
		}
		if (myClearMillis != null) {
			long readUntil = System.currentTimeMillis() + myClearMillis;
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
					theCtx.getLog().info(this, "Cleared message");
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        // nothing
                    }
                    readUntil = System.currentTimeMillis() + myClearMillis;
				} catch (JMSException e) {
				    theCtx.getLog().warn(this, "Error while clearing queue: " + e.getMessage());
                }
			}
			theCtx.getLog().info(this, "Cleared " + cleared + " messages from interface before starting");			
		}
		
		theCtx.getLog().info(this, "Started interface successfully");
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

		theCtx.getLog().info(this, "Stopping interface");

		myStarted = false;
	}

	@Override
	public boolean isStarted() {
		return myStarted;
	}

}
