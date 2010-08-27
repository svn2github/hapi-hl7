package ca.uhn.hunit.iface;

import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.ConnectionHub;
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hunit.compare.hl7v2.Hl7V2MessageCompare;
import ca.uhn.hunit.test.*;
import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.example.MllpHl7v2MessageSwapper;
import ca.uhn.hunit.run.ExecutionContext;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;

import org.springframework.core.io.ClassPathResource;

import java.net.URISyntaxException;

import java.util.concurrent.Semaphore;
import javax.xml.bind.JAXBException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Hl7V2Test {
    private static final Log ourLog = LogFactory.getLog(Hl7V2Test.class);

    //~ Methods --------------------------------------------------------------------------------------------------------

    @Test
    public void testFailureExpectSpecific()
                                   throws URISyntaxException, InterfaceWontStartException, ConfigurationException,
                                          JAXBException {
        MllpHl7v2MessageSwapper swapper = new MllpHl7v2MessageSwapper(true, "LEIGHTON", "TEST2");
        swapper.start();

        ClassPathResource defFile = new ClassPathResource("unit_tests_hl7.xml");
        TestBatteryImpl battery = new TestBatteryImpl(defFile);
        ExecutionContext ctx = new ExecutionContext(battery);
        ctx.execute("ExpectSpecific Test");

        TestImpl test = battery.getTestByName("ExpectSpecific Test");
        Assert.assertFalse(ctx.getTestSuccesses().contains(test));
        Assert.assertTrue(ctx.getTestFailures().containsKey(test));

        ourLog.info("Waiting for swapper to stop");
        swapper.waitForStopped();
        ourLog.info("Finished test");
    }

    @Test
    public void testMultipleTests()
                           throws URISyntaxException, InterfaceWontStartException, ConfigurationException, JAXBException {
        MllpHl7v2MessageSwapper swapper = new MllpHl7v2MessageSwapper(true, "LEIGHTON", "TEST", 2);
        swapper.start();

        ClassPathResource defFile = new ClassPathResource("unit_tests_hl7.xml");
        TestBatteryImpl battery = new TestBatteryImpl(defFile);
        ExecutionContext ctx = new ExecutionContext(battery);
        ctx.execute("ExpectSpecific Test", "ExpectSecond Test");

        TestImpl test = battery.getTestByName("ExpectSpecific Test");
        Assert.assertTrue(ctx.getTestSuccesses().contains(test));
        Assert.assertFalse(ctx.getTestFailures().containsKey(test));

        test = battery.getTestByName("ExpectSecond Test");
        Assert.assertFalse(ctx.getTestSuccesses().contains(test));
        Assert.assertTrue(ctx.getTestFailures().containsKey(test));

        ourLog.info("Waiting for swapper to stop");
        swapper.waitForStopped();
        ourLog.info("Finished test");
    }

    @Test
    public void testSuccessfulExpectSpecific()
                                      throws URISyntaxException, InterfaceWontStartException, ConfigurationException,
                                             JAXBException {
        MllpHl7v2MessageSwapper swapper = new MllpHl7v2MessageSwapper(true, "LEIGHTON", "TEST");
        swapper.start();

        ClassPathResource defFile = new ClassPathResource("unit_tests_hl7.xml");
        TestBatteryImpl battery = new TestBatteryImpl(defFile);
        ExecutionContext ctx = new ExecutionContext(battery);
        ctx.execute("ExpectSpecific Test");

        Assert.assertFalse(ctx.getTestFailures().containsKey(battery.getTestByName("ExpectSpecific Test")));
        Assert.assertTrue(ctx.getTestSuccesses().contains(battery.getTestByName("ExpectSpecific Test")));


        ourLog.info("Waiting for swapper to stop");
        swapper.waitForStopped();
        ourLog.info("Finished test");

    }

    @Test
    public void testSpecificReply() throws Exception {
        MyThreadSpecificReply thread = new MyThreadSpecificReply();
        thread.start();

        ClassPathResource defFile = new ClassPathResource("unit_tests_hl7.xml");
        TestBatteryImpl battery = new TestBatteryImpl(defFile);
        ExecutionContext ctx = new ExecutionContext(battery);
        ctx.execute("SpecificReply Test");

        if (thread.myComparison == null) {
            Assert.fail("No comparison object");
        } else if (thread.myComparison.isSame() == false) {
            Assert.fail(thread.myComparison.describeDifference());
        }
        
    }


    private static class MyThreadSpecificReply extends Thread
    {
        private Hl7V2MessageCompare myComparison;

        @Override
        public void run() {

            try {
                sleep(3000);

                String inputMessageString = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838|T|2.3\r"+
                    "PID|||7005728^^^TML^MR||LEIGHTON^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r"+
                    "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r"+
                    "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r"+
                    "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r"+
                    "OBX|1|NM|Z114099^Erc^L||4.00|tril/L|3.90-5.60||||F|||200905011111|PMH";
                String outputMessageString = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ACK|20169838|T|2.3\r"+
                    "MSA|AR|20169838";

                PipeParser p = new PipeParser();
                Message inputMessage = p.parse(inputMessageString);
                Message expectedOutputMessage = p.parse(outputMessageString);

                Connection connection = ConnectionHub.getInstance().attach("localhost", 10200, new PipeParser(), MinLowerLayerProtocol.class);

                connection.getInitiator().setTimeoutMillis(3000);
                Message response = connection.getInitiator().sendAndReceive(inputMessage);

                myComparison = new Hl7V2MessageCompare();
                myComparison.compare(expectedOutputMessage, response);

            } catch (Exception ex) {
                ourLog.error("Failure in test thread: ", ex);
            } finally {

            }

        }

    }

    public static void main(String[] args) throws Exception {
        final Hl7V2Test hl7V2Test = new Hl7V2Test();

//        hl7V2Test.testFailureExpectSpecific();
//        hl7V2Test.testMultipleTests();
        hl7V2Test.testSuccessfulExpectSpecific();
    }
}
