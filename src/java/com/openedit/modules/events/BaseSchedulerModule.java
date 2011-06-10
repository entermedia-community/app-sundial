/*
 * Created on Aug 20, 2006
 */
package com.openedit.modules.events;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import com.openedit.WebPageRequest;
import com.openedit.events.Event;
import com.openedit.events.UserCalendar;
import com.openedit.events.UserCalendarArchive;
import com.openedit.modules.BaseModule;
import com.openedit.util.PathUtilities;

public class BaseSchedulerModule extends BaseModule
{
	protected UserCalendarArchive fieldArchive;

	/*
	public void changeUserCalendar(WebPageRequest inReq) throws Exception
	{
		String id =  "usercalendar" ;  //this works for null people as well
		inReq.removeSessionValue(id);
		
		String userdefined = inReq.getRequestParameter("selecteduser");
		if ( userdefined == null)
		{
			userdefined = inReq.getUser().getUserName();
		}
		if( userdefined == null)
		{
			throw new OpenEditException("Must select a user");
		}
		getUserCalendar(inReq, userdefined);
	}
	*/
	
	public UserCalendar getUserCalendar(WebPageRequest inReq) throws Exception
	{	
		String eventsId= findValue("eventsid", inReq);
		if( eventsId == null)
		{
			eventsId= "events";
		}
		UserCalendar cal = (UserCalendar)inReq.getSessionValue("usercalendar" + eventsId );
		if( cal == null)
		{
			boolean draftOnly = inReq.getUser() != null && inReq.getUser().hasProperty("oe.edit.draftmode" );
			cal = getCalendarArchive().getUserCalendar(eventsId, draftOnly);
			inReq.putSessionValue("usercalendar" + eventsId, cal);
		}
		inReq.putPageValue("usercalendar",cal);
		inReq.putPageValue("eventsid",eventsId);
		return cal;
	}
	public void clear(WebPageRequest inReq) throws Exception
	{
		//Reload from disk

		String id =  "usercalendar" ;  //this works for null people as well

		UserCalendar cal = (UserCalendar)inReq.getSessionValue(id);

		List toremove = new ArrayList();

		Enumeration enuma = inReq.getSession().getAttributeNames();
		while( enuma.hasMoreElements())
		{
			String name = (String) enuma.nextElement();
			if ( name.startsWith(id))
			{
				toremove.add( name );
			}
		}
		
		for (Iterator iterator = toremove.iterator(); iterator.hasNext();)
		{
			String name = (String) iterator.next();
			inReq.removeSessionValue(name);
		}
//		if( cal != null)
//		{
//			getUserCalendar(inReq,cal.getUser().getUserName());
//		}
	}
	public void loadEvent(WebPageRequest inReq) throws Exception
	{
		UserCalendar usercal = getUserCalendar(inReq);
		String eventId = inReq.getRequestParameter("eventId");
		if( eventId == null)
		{
			eventId = PathUtilities.extractPageName(inReq.getContentPage().getName() );
		}
		Event event = usercal.getEvent(eventId);
		inReq.putPageValue("event",event);
	}

	public UserCalendarArchive getCalendarArchive()
	{
		return fieldArchive;
	}

	public void setCalendarArchive(UserCalendarArchive inArchive)
	{
		fieldArchive = inArchive;
	}
}
