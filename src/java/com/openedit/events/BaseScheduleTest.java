/*
 * Created on Jul 21, 2006
 */
package com.openedit.events;

import com.openedit.BaseTestCase;

public class BaseScheduleTest extends BaseTestCase
{

	public BaseScheduleTest(String inName)
	{
		super( inName);
		String base = System.getProperty("oe.root.path");
		if ( base == null)
		{
			System.setProperty("oe.root.path","webapp");
		}
	}
}
