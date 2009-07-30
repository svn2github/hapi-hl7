/*
 * Created on Jul 30, 2009
 */
package ca.uhn.hunit.iface;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

/**
 * Wraps a JMS connection factory to provide usernames and passwords
 * 
 * @author <a href="mailto:james.agnew@uhn.on.ca">James Agnew</a>
 * @version $Revision: 1.1 $ updated on $Date: 2009-07-30 14:57:11 $ by $Author: jamesagnew $
 */
public class JmsConnectionFactory implements ConnectionFactory
{

    private ConnectionFactory myConnectionFactory;
    private String myUsername;
    private String myPassword;


    /**
     * @param theConnectionFactory
     * @param theUsername
     * @param thePassword
     */
    public JmsConnectionFactory(ConnectionFactory theConnectionFactory, String theUsername, String thePassword) {
        myConnectionFactory = theConnectionFactory;
        myUsername = theUsername;
        myPassword = thePassword;
    }


    /**
     * {@inheritDoc}
     */
    public Connection createConnection() throws JMSException {
        if (myUsername != null) {
            return myConnectionFactory.createConnection(myUsername, myPassword);
        } else {
            return myConnectionFactory.createConnection();
        }
    }


    /**
     * {@inheritDoc}
     */
    public Connection createConnection(String theArg0, String theArg1) throws JMSException {
        return myConnectionFactory.createConnection(theArg0, theArg1);
    }

}
