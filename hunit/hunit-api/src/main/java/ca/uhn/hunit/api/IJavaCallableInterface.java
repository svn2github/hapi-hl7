/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uhn.hunit.api;

/**
 * Code being tested using a JavaCallable interface should implement this
 * interface
 */
public interface IJavaCallableInterface {

    /**
     * Called once, when the interface is being started.
     * 
     * @param theReceiveAcceptor
     *            The object to which the implementation may pass any messages
     *            "received" by the interface. Note that this is completely
     *            asynchronous. Implementations may call
     *            {@link IJavaCallableInterfaceReceiver#receiveMessage(String)}
     *            at any time before stop() is invoked.
     */
    void start(IJavaCallableInterfaceReceiver theReceiveAcceptor);

    /**
     * Called when the interface is stopping
     */
    void stop();

    /**
     * <p>
     * Implementations will have this method invoked for any messages sent to
     * the interface.
     * </p>
     * 
     * <p>
     * Note: The naming is a bit tricky for this method. It is so-named because
     * it is invoked by hUnit when a message is being sent to the callable
     * interface. Therefore, the role of the implementation of this method isn't
     * to send a message, but to act as though a message is being sent.
     * </p>
     * 
     * @param theMessage
     *            The message that is being sent
     * @return The message to reply. May also be <code>null</code> if no reply.
     */
    String sendMessageToInterface(String theMessage);

}
