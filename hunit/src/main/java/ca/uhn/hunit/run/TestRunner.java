/**
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the
 * specific language governing rights and limitations under the License.
 *
 * The Initial Developer of the Original Code is University Health Network. Copyright (C)
 * 2001.  All Rights Reserved.
 *
 * Alternatively, the contents of this file may be used under the terms of the
 * GNU General Public License (the  "GPL"), in which case the provisions of the GPL are
 * applicable instead of those above.  If you wish to allow use of your version of this
 * file only under the terms of the GPL and not to allow others to use your version
 * of this file under the MPL, indicate your decision by deleting  the provisions above
 * and replace  them with the notice and other provisions required by the GPL License.
 * If you do not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the GPL.
 */
package ca.uhn.hunit.run;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.swing.controller.SwingRunnerController;
import ca.uhn.hunit.swing.ui.DialogUtil;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestImpl;
import ca.uhn.hunit.util.log.LogFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;


import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Map;

import javax.xml.bind.JAXBException;

public class TestRunner {
    //~ Methods --------------------------------------------------------------------------------------------------------

    private static void executeInGuiMode(Resource defFile, String[] testsToExecute) {
        SwingRunnerController controller;

        if (defFile == null) {
            controller = new SwingRunnerController();
        } else {
            try {
                controller = new SwingRunnerController(defFile);
            } catch (Exception ex) {
                DialogUtil.showErrorMessage(null,
                                            ex.getMessage());
            }
        }
    }

    private static void executeInTextMode(Resource theDefFile, String[] theTestsToExecute)
                                   throws ConfigurationException, JAXBException {
        TestBatteryImpl batteryImpl = new TestBatteryImpl(theDefFile);
        ExecutionContext ctx = new ExecutionContext(batteryImpl);
        ctx.execute(theTestsToExecute);

        LogFactory.INSTANCE.get(batteryImpl).info("----------------------------------------------------");
        LogFactory.INSTANCE.get(batteryImpl).info("The following tests passed:");

        for (TestImpl next : ctx.getTestSuccesses()) {
            LogFactory.INSTANCE.get(batteryImpl).info(" * " + next.getName());
        }

        LogFactory.INSTANCE.get(batteryImpl).info("----------------------------------------------------");

        if (! ctx.getTestFailures().isEmpty()) {
            LogFactory.INSTANCE.get(batteryImpl).info("Warning, the some tests failed!");

            for (Map.Entry<TestImpl, TestFailureException> next : ctx.getTestFailures().entrySet()) {
                LogFactory.INSTANCE.get(batteryImpl)
                   .info("The following test failed: " + next.getKey().getName() + " - Reason: " +
                         next.getValue().describeReason());
            }
        }
    }

    /**
     * @param args
     * @throws URISyntaxException
     * @throws JAXBException
     * @throws ConfigurationException
     * @throws InterfaceWontStartException
     * @throws FileNotFoundException
     * @throws ParseException
     */
    public static void main(String[] theArgs)
                     throws URISyntaxException, JAXBException, InterfaceWontStartException, ConfigurationException,
                            FileNotFoundException, ParseException {
        Options options = new Options();

        OptionGroup fileOptionGroup = new OptionGroup();
        fileOptionGroup.setRequired(false);

        Option option = new Option("f", "file", true, "The path to the file to load the test battery from");
        option.setValueSeparator('=');
        fileOptionGroup.addOption(option);
        option = new Option("c", "classpath", true, "The classpath path to the file to load the test battery from");
        option.setValueSeparator('=');
        fileOptionGroup.addOption(option);
        options.addOptionGroup(fileOptionGroup);

        OptionGroup uiOptionGroup = new OptionGroup();
        option = new Option("g", "gui", false, "Start hUnit in GUI mode (default)");
        uiOptionGroup.addOption(option);
        option = new Option("x", "text", false, "Start hUnit in Text mode");
        uiOptionGroup.addOption(option);
        options.addOptionGroup(uiOptionGroup);
        
        option = new Option("t", "tests", true, "A comma separated list of tests to execute (default is all)");
        option.setValueSeparator('=');
        option.setRequired(false);
        options.addOption(option);

        Resource defFile = null;
        CommandLine parser;
        boolean textMode = false;

        try {
            parser = new PosixParser().parse(options, theArgs);

            if (parser.hasOption("f")) {
                defFile = new FileSystemResource(parser.getOptionValue("f"));
            } else if (parser.hasOption("c")) {
                defFile = new ClassPathResource(parser.getOptionValue("c"));
            }

            if (parser.hasOption("x")) {
                textMode = true;
            }
        } catch (Exception e) {
            HelpFormatter hf = new HelpFormatter();
            hf.printHelp("java -jar hunit-[version]-jar-with-dependencies.jar [-c FILE|-f FILE] [options]", options);

            return;
        }

        String[] testsToExecute = null;

        if (parser.hasOption("t")) {
            testsToExecute = parser.getOptionValue("t").split(",");
        }

        if (textMode) {
            executeInTextMode(defFile, testsToExecute);
        } else {
            executeInGuiMode(defFile, testsToExecute);
        }
    }
}
