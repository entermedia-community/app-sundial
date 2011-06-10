/*
 * Created on Jul 20, 2006
 */
package com.openedit.events;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.openedit.WebPageRequest;
import com.openedit.modules.events.EventsModule;

public class UserCalendarTest extends BaseScheduleTest
{
	private static final Log log = LogFactory.getLog(UserCalendarTest.class);
	
	public UserCalendarTest(String inName)
	{
		super(inName);
	}
	
	public void testLoadMonths() throws Exception
	{
		EventsModule mod = (EventsModule)getFixture().getModuleManager().getModule("EventsModule");
		WebPageRequest req = getFixture().createPageRequest("/index.html");
		mod.loadMonthList(req);		
		
	}

	public void testLoadByCategory() throws Exception
	{
		EventsModule mod = (EventsModule)getFixture().getModuleManager().getModule("EventsModule");
		WebPageRequest req = getFixture().createPageRequest("/index.html");
		UserCalendar cals = mod.getUserCalendar(req);		

		List categories = new ArrayList();
		categories.add("Holiday");
		List events = cals.findEventsForCategories(categories);
		assertTrue( events.size() > 0);
	}
	
	public void testListCalendars() throws Exception
	{
		EventsModule mod = (EventsModule)getFixture().getModuleManager().getModule("EventsModule");
		
		WebPageRequest req = getFixture().createPageRequest("/index.html");
		UserCalendar cals = mod.getUserCalendar(req);
		assertNotNull(cals);
		
		Calendar cal = cals.getSelectedCalendar(); //This is this users calendar

		assertTrue(cals.getMixedInCalendars().size() > 0);


		//Not a valid test now since the year is now 2007
		Month aug = cals.getMonth("7/1/06");
//		assertNotNull(aug);
//		
//		List events = cals.findEventsForMonth(aug);
//		assertNotNull(events);
//		
//		assertTrue(events.size() > 10);
//		
//		GregorianCalendar start = new GregorianCalendar();
//		start.set(2006,7-1,4);
//				
//		Day day = new Day();
//		day.setDate(new DateTime(start.getTime()) );
//		List list = cals.findEventsForDay(day, events);
//
//		assertTrue( list.size() > 0 );
//		//this should have the fourth of July
//
//		GregorianCalendar the28th = new GregorianCalendar();
//		the28th.set(2006,7-1,28);
//		Day day2 = new Day();
//		day2.setDate(new DateTime(the28th.getTime()) );
//		List list2 = cals.findEventsForDay(day2, events);
//
//		if( list2.size() == 1)
//		{
//			//this is a bug
//			Event event = (Event)list2.get(0);
//			assertEquals(event.getEvent().getLocation().getValue() , "Peru" );
//			
//		}
		
	}
	
}
