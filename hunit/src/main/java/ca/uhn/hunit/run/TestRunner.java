/**
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
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

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.springframework.util.ResourceUtils;

import ca.uhn.hunit.ex.ConfigurationException;
import ca.uhn.hunit.ex.InterfaceWontStartException;
import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.test.TestBatteryImpl;
import ca.uhn.hunit.test.TestImpl;

public class TestRunner {

	/**
	 * @param args
	 * @throws URISyntaxException
	 * @throws JAXBException
	 * @throws ConfigurationException
	 * @throws InterfaceWontStartException
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	public static void main(String[] theArgs) throws URISyntaxException, JAXBException, InterfaceWontStartException, ConfigurationException, FileNotFoundException, ParseException {
		Options options = new Options();

		OptionGroup fileOptionGroup = new OptionGroup();
		fileOptionGroup.setRequired(true);
		Option option = new Option("f", "file", true, "The path to the file to load the test battery from");
		option.setValueSeparator('=');
		fileOptionGroup.addOption(option);
		option = new Option("c", "classpath", true, "The classpath path to the file to load the test battery from");
		option.setValueSeparator('=');
		fileOptionGroup.addOption(option);
		options.addOptionGroup(fileOptionGroup);

		option = new Option("t", "tests", true, "A comma separated list of tests to execute (default is all)");
		option.setValueSeparator('=');
		option.setRequired(false);
		options.addOption(option);
		
		File defFile;
		CommandLine parser;
		try {
			parser = new PosixParser().parse(options, theArgs);

			if (parser.hasOption("f")) {
				defFile = new File(parser.getOptionValue("f"));
			} else {
				defFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + parser.getOptionValue("c"));
			}
		} catch (Exception e) {
			HelpFormatter hf = new HelpFormatter();
			hf.printHelp("java -jar hunit-[version]-jar-with-dependencies.jar {-c FILE|-f FILE} [options]", options);
			return;
		}

		String[] testsToExecute = null;
		if (parser.hasOption("t")) {
			testsToExecute = parser.getOptionValue("t").split(",");
		}
		
		TestBatteryImpl batteryImpl = new TestBatteryImpl(defFile);
		ExecutionContext ctx = new ExecutionContext(batteryImpl);
		ctx.execute(testsToExecute);

		ctx.getLog().info(batteryImpl, "----------------------------------------------------");
		ctx.getLog().info(batteryImpl, "The following tests passed:");
		for (TestImpl next : ctx.getTestSuccesses()) {
			ctx.getLog().info(batteryImpl, " * " + next.getName());
		}
		ctx.getLog().info(batteryImpl, "----------------------------------------------------");

		if (!ctx.getTestFailures().isEmpty()) {
			ctx.getLog().info(batteryImpl, "Warning, the some tests failed!");
			for (Map.Entry<TestImpl, TestFailureException> next : ctx.getTestFailures().entrySet()) {
				ctx.getLog().info(batteryImpl, "The following test failed: " + next.getKey().getName() + " - Reason: " + next.getValue().describeReason());
			}
		}
	}

}
