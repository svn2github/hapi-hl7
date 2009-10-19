/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.uhn.hunit.util.log;

/**
 * A single logged entry
 */
public class LogEvent {

    private Object myModelObject;
    private LogLevel myLogLevel;
    private String myMessage;
    private Throwable myStackTrace;

    public LogEvent(Object theModelObject, LogLevel theLogLevel, String theMessage, Throwable theStackTrace) {
        this.myModelObject = theModelObject;
        this.myLogLevel = theLogLevel;
        this.myMessage = theMessage;
        this.myStackTrace = theStackTrace;
    }

    public LogLevel getLogLevel() {
        return myLogLevel;
    }

    public String getMessage() {
        return myMessage;
    }

    public Object getModelObject() {
        return myModelObject;
    }

    public Throwable getStackTrace() {
        return myStackTrace;
    }

}
