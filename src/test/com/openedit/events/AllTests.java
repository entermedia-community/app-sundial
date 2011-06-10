/*
 * Created on May 5, 2005
 */
package com.openedit.events;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author cburkey
 *
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.openedit.intranet");
		//$JUnit-BEGIN$
		suite.addTestSuite(EditTest.class);
		suite.addTestSuite(UserCalendarTest.class);
		//$JUnit-END$
		return suite;
	}
}
