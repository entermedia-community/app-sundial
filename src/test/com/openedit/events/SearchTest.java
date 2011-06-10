/*
 * Created on May 6, 2005
 */
package com.openedit.events;

import com.openedit.modules.events.EventsModule;

/**
 * @author imiller
 *
 */
public class SearchTest extends BaseScheduleTest
{
	

	public SearchTest(String inName)
	{
		super(inName);
		// TODO Auto-generated constructor stub
	}
	
	public void testSearch() throws Exception
	{
		EventsModule mod = (EventsModule)getModule("EventsModule");
		
		
	}
//	public void testPush() throws Exception
//	{
//		EventsModule mod = (EventsModule)getModule("EventsModule");
//		WebPageRequest req = getFixture().createPageRequest("/index.html");
//		mod.listDataDirs(req);
//		
//		List dirs = (List)req.getPageValue("dataDirs");
//		assertTrue( dirs.size() > 0);
//
//		UserCalendar cal  = mod.getUserCalendar(req);
//		cal.setSelectedCalendar((Calendar)cal.getAllCalendars().get(0));
//		
//		req.setRequestParameter("datadir", "cburkey");
//		mod.pushSelectedCalendar(req);
//		
//	}
}
