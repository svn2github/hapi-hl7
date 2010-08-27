package ca.uhn.hunit.api;


/**
 * @See IJavaCallableInterface
 */
public interface IJavaCallableInterfaceReceiver {

    /**
     * Implementations may invoke this method to simulate a message being received by a particular
     * interface.
     *
     * @param theMessage The message that is being received
     */
    void receiveMessage(String theMessage);
    
}
