package ca.uhn.hunit.iface;

import org.junit.Assert;
import org.junit.Test;

import ca.uhn.hunit.api.IJavaCallableInterface;
import ca.uhn.hunit.api.IJavaCallableInterfaceReceiver;
import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.run.ExecutionContext;
import ca.uhn.hunit.run.TestRunner;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.xsd.AnyInterface;
import ca.uhn.hunit.xsd.ExpectMessageAny;
import ca.uhn.hunit.xsd.Hl7V2ExpectSpecificMessage;
import ca.uhn.hunit.xsd.Hl7V2MessageDefinition;
import ca.uhn.hunit.xsd.Hl7V2SendMessage;
import ca.uhn.hunit.xsd.JavaCallableInterface;
import ca.uhn.hunit.xsd.MesssageTypeEnum;
import ca.uhn.hunit.xsd.SendMessageAny;
import ca.uhn.hunit.xsd.TestBattery;
import ca.uhn.hunit.xsd.TestBattery.Interfaces;
import ca.uhn.hunit.xsd.TestBattery.Tests;

public class JavaCallableInterfaceTest {

    private static final String MESSAGE_1 = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838|T|2.3";
    private static final String MESSAGE_2 = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|99999999|T|2.3";
    private static final String MESSAGE_3 = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|88888888|T|2.3";
    private static final String REPLY_KEY = "REPLY_KEY";



    @Test
    public void testNormal() throws ConfigurationException {
        
        TestBattery batteryConfig = new TestBattery();
        
        JavaCallableInterface jciConfig = new JavaCallableInterface();
        jciConfig.setId("java_callable_interface");
        jciConfig.setMessageType(MesssageTypeEnum.HL_7_V_2);
        jciConfig.setClazz(MyCallable.class.getName());
        
        batteryConfig.setInterfaces(new Interfaces());
        batteryConfig.getInterfaces().getInterface().add(new AnyInterface());
        batteryConfig.getInterfaces().getInterface().get(0).setJavaCallableInterface(jciConfig);
        
        Hl7V2MessageDefinition messageDef1 = new Hl7V2MessageDefinition();
        messageDef1.setId("message_1");
        messageDef1.setText(MESSAGE_1);

        Hl7V2MessageDefinition messageDef2 = new Hl7V2MessageDefinition();
        messageDef2.setId("message_2");
        messageDef2.setText(MESSAGE_2);
        
        batteryConfig.setTests(new Tests());
        batteryConfig.getTests().getTest().add(new ca.uhn.hunit.xsd.Test());
        batteryConfig.getTests().getTest().get(0).setName("Test1");
        ca.uhn.hunit.xsd.Test test1 = batteryConfig.getTests().getTest().get(0);

        Hl7V2SendMessage sendMessageConfig = new Hl7V2SendMessage();
        sendMessageConfig.setMessage(messageDef1);
        sendMessageConfig.setInterfaceId(jciConfig.getId());
        
        SendMessageAny sendMessageAnyConfig = new SendMessageAny();
        sendMessageAnyConfig.setHl7V2(sendMessageConfig);
        test1.getSendMessageOrExpectMessageOrExpectNoMessage().add(sendMessageAnyConfig);

        Hl7V2ExpectSpecificMessage expectMessageCOnfig = new Hl7V2ExpectSpecificMessage();
        expectMessageCOnfig.setMessage(messageDef2);
        expectMessageCOnfig.setInterfaceId(jciConfig.getId());

        ExpectMessageAny expectMessageAnyConfig = new ExpectMessageAny();
        expectMessageAnyConfig.setHl7V2Specific(expectMessageCOnfig);
        test1.getSendMessageOrExpectMessageOrExpectNoMessage().add(expectMessageAnyConfig);
        
        System.setProperty(REPLY_KEY, MESSAGE_2);
        TestBatteryImpl battery = new TestBatteryImpl(batteryConfig);
        ExecutionContext ctx = new ExecutionContext(battery);
        ctx.execute();
        Assert.assertEquals(0, ctx.getTestFailures().size());
        Assert.assertEquals(1, ctx.getTestSuccesses().size());
        
        System.setProperty(REPLY_KEY, MESSAGE_3);
        battery = new TestBatteryImpl(batteryConfig);
        ctx = new ExecutionContext(battery);
        ctx.execute();
        Assert.assertEquals(1, ctx.getTestFailures().size());
        Assert.assertEquals(0, ctx.getTestSuccesses().size());
        
    }
    
    
    
    public static class MyCallable implements IJavaCallableInterface
    {
        private String myReply;
        private IJavaCallableInterfaceReceiver myReceiveAcceptor;

        @Override
        public String sendMessageToInterface(String theMessage) {
            myReceiveAcceptor.receiveMessage(System.getProperty(REPLY_KEY));
            return null;
        }

        @Override
        public void start(IJavaCallableInterfaceReceiver theReceiveAcceptor) {
            myReceiveAcceptor = theReceiveAcceptor;
        }

        @Override
        public void stop() {
            // nothing
        }

    }
    
    
}
