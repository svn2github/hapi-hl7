package ca.uhn.hunit.compare.hl7v2;

import java.util.List;

public abstract class StructureComparison {

	/**
	 * @return Removes all group comparisons and extra groups and "flattens"
	 *         them by bringing their children up the hierarchy so that only
	 *         segment comparisons and extra segments are returned. Some entries
	 *         in the returned list may be null if that position would contain
	 *         an extra segment in the other message's list
	 */
	public abstract List<SegmentComparison> flattenMessage();

}
