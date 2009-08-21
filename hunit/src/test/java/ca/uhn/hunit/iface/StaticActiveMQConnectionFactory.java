/*
 * Created on Aug 17, 2009
 */
package ca.uhn.hunit.iface;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * TODO: add!
 * 
 */
public class StaticActiveMQConnectionFactory implements ConnectionFactory
{
    private static ActiveMQConnectionFactory ourConnectionFactory;

    static {
        ourConnectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
    }

    /**
     * {@inheritDoc}
     */
    public Connection createConnection() throws JMSException {
        return ourConnectionFactory.createConnection();
    }

    /**
     * {@inheritDoc}
     */
    public Connection createConnection(String theArg0, String theArg1) throws JMSException {
        return createConnection();
    }
    
}
