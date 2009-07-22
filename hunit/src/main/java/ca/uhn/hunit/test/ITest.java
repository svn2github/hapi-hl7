package ca.uhn.hunit.test;

import java.util.Set;

import ca.uhn.hunit.ex.TestFailureException;
import ca.uhn.hunit.run.ExecutionContext;

public interface ITest {

	String getName();

	Set<String> getInterfacesUsed();
	
}