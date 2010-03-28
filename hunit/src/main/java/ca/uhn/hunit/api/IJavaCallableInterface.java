/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uhn.hunit.api;

/**
 * Code being tested using a JavaCallable interface should implement this interface
 */
public interface IJavaCallableInterface {

    /**
     * Implementations will have this method invoked for any messages
     * sent to the interface
     *
     * @param theMessage The message that is being sent
     * @param theOutboundAcceptor Any outbound messages being produced by the interface as a result of the message being sent in should be posted back to the reply acceptor
     */
    void message(String theMessage, IJavaCallableInterface theOutboundAcceptor);
}
