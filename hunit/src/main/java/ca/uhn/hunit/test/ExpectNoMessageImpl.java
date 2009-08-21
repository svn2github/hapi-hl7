/*
 * Created on Aug 20, 2009
 */
package ca.uhn.hunit.test;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.ex.UnexpectedMessageException;
import ca.uhn.hunit.iface.TestMessage;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.xsd.Event;
import ca.uhn.hunit.xsd.ExpectNoMessage;

/**
 * TODO: add!
 * 
 * @author <a href="mailto:james.agnew@uhn.on.ca">James Agnew</a>
 * @version $Revision: 1.1 $ updated on $Date: 2009-08-21 20:02:58 $ by $Author: jamesagnew $
 */
public class ExpectNoMessageImpl extends AbstractExpect
{

    private long myReceiveTimeout;


    /**
     * @param theBattery
     * @param theTest
     * @param theConfig
     * @throws ConfigurationException 
     */
    public ExpectNoMessageImpl(TestBatteryImpl theBattery, TestImpl theTest, ExpectNoMessage theConfig) throws ConfigurationException {
        super(theBattery, theTest, theConfig);
        
        Long receiveTimeout = theConfig.getReceiveTimeoutMillis();
        myReceiveTimeout = receiveTimeout != null ? receiveTimeout : 120000L;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(ExecutionContext theCtx) throws TestFailureException, ConfigurationException {
        
        TestMessage message = getInterface().receiveMessage(getTest(), theCtx, myReceiveTimeout);
        if (message != null) {
            throw new UnexpectedMessageException(getTest(), message, "Unexpected message received");
        }
        
    }

}
