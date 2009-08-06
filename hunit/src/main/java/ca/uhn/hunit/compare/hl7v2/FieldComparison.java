package ca.uhn.hunit.compare.hl7v2;

import java.util.List;

import ca.uhn.hl7v2.model.Type;

public class FieldComparison {

	private List<Type> mySameFields;
	private List<Type> myDiffFields1;
	private List<Type> myDiffFields2;

	public FieldComparison(List<Type> theSameFields, List<Type> theDiffFields1, List<Type> theDiffFields2) {
		mySameFields = theSameFields;
		myDiffFields1 = theDiffFields1;
		myDiffFields2 = theDiffFields2;
	}

	public List<Type> getSameFields() {
		return mySameFields;
	}

	public List<Type> getDiffFields1() {
		return myDiffFields1;
	}

	public List<Type> getDiffFields2() {
		return myDiffFields2;
	}

}
