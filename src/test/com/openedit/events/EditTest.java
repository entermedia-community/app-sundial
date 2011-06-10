/*
 * Created on May 6, 2005
 */
package com.openedit.events;

import com.openedit.WebPageRequest;
import com.openedit.modules.events.CalendarEditModule;

/**
 * @author cburkey
 *
 */
public class EditTest extends BaseScheduleTest
{
	

	public EditTest(String inName)
	{
		super(inName);
		// TODO Auto-generated constructor stub
	}

	public void testAdd() throws Exception
	{
		CalendarEditModule mod = (CalendarEditModule)getModule("CalendarEditModule");
		WebPageRequest req = getFixture().createPageRequest("/events/index.html");

		UserCalendar cal  = mod.getUserCalendar(req);
		
		cal.setSelectedCalendar((Calendar)cal.getAllCalendars().get(0));
		
		req.setRequestParameter("summary","Test entry");
		req.setRequestParameter("vfrom","10-1-2006 7:39 PM");
		req.setRequestParameter("vto","10-2-2006 7:39 PM");
		req.setRequestParameter("category","Holiday");
		req.setRequestParameter("field","testproperty");
		req.setRequestParameter("testproperty.value","did it work?");
		req.setRequestParameter("eventId", "testevent");
		
		mod.saveEvent(req);
		
		mod.loadEvent(req);
		Event event = (Event) req.getPageValue("event");
		assertNotNull(event);
		event.getProperties();
		assertTrue(event.getProperty("testproperty").equals("did it work?"));
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
