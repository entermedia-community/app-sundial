/*
 * Created on May 5, 2005
 */
package com.openedit.modules.events;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.entermedia.upload.FileUpload;
import org.entermedia.upload.UploadRequest;

import com.openedit.WebPageRequest;
import com.openedit.events.Calendar;
import com.openedit.events.Event;
import com.openedit.events.ICalCalendarBuilder;
import com.openedit.events.UserCalendar;
import com.openedit.page.Page;
import com.openedit.util.PathUtilities;

/**
 * @author cburkey
 * 
 */
public class CalendarEditModule extends BaseSchedulerModule {

	public void addNewCategory(WebPageRequest inReq) throws Exception {
		String name = inReq.getRequestParameter("newcategory");
		if (name != null) {
			UserCalendar cal = getUserCalendar(inReq);
			cal.addCategory(name);
			getCalendarArchive().saveCategories(cal, inReq.getUser());
		}
	}

	public void deleteCategory(WebPageRequest inReq) throws Exception {
		UserCalendar cal = getUserCalendar(inReq);
		String[] names = inReq.getRequestParameters("deletecategory");
		if (names != null) {
			for (int i = 0; i < names.length; i++) {
				cal.deleteCategory(names[i]);
			}
			getCalendarArchive().saveCategories(cal, inReq.getUser());
		}
	}

	public void addNewLocation(WebPageRequest inReq) throws Exception {
		String name = inReq.getRequestParameter("newlocation");
		if (name != null) {
			UserCalendar cal = getUserCalendar(inReq);
			cal.addLocation(name);
			getCalendarArchive().saveLocations(cal, inReq.getUser());
		}
	}

	public void deleteLocation(WebPageRequest inReq) throws Exception {
		UserCalendar cal = getUserCalendar(inReq);
		String[] names = inReq.getRequestParameters("deletelocation");
		if (names != null) {
			for (int i = 0; i < names.length; i++) {
				cal.deleteLocation(names[i]);
			}
			getCalendarArchive().saveLocations(cal, inReq.getUser());
		}
	}

	public void addNewCalendar(WebPageRequest inReq) throws Exception {
		String name = inReq.getRequestParameter("name");
		String eventid = inReq.findValue("eventsid");
		if (name != null) {
			if (!name.endsWith(".ics")) {
				name += ".ics";
			}

			String path = eventid + "/data/" + name;
			Page page = getPageManager().getPage(path);
			if(page.exists()){
				inReq.putPageValue("status", name + " already existed.");
				return;
			}
			Calendar newcal = new ICalCalendarBuilder().build();
			
			newcal.setPage(page);
			populateProperties(inReq, newcal);
		
			getCalendarArchive().saveCalendar(newcal, inReq.getUser());
			inReq.putPageValue("status", name + " created");
			inReq.setRequestParameter("calendar", newcal.getId());
		} else {
			FileUpload command = new FileUpload();
			command.setPageManager(getPageManager());
			UploadRequest properties = command.parseArguments(inReq);
			if (properties == null) {
				return;
			}
			String id = inReq.getRequestParameter("filepath");
			if (id != null && id.length() > 0) {
				String fname = PathUtilities.extractPageName(id.replace('\\',
						'/'));
				String path = eventid + "/data/" + fname + ".ics";
				properties.saveFirstFileAs(path, inReq.getUser());
				// command.saveFile(properties,path,inReq);
			}
			inReq.putPageValue("status", "Upload completed");
		}
		
		clear(inReq);
		
	}

	public void deleteCalendar(WebPageRequest inReq) throws Exception {
		String name = inReq.getRequestParameter("name");
		UserCalendar calendar = getUserCalendar(inReq);
		getCalendarArchive().deleteCalendar(calendar.getSelectedCalendar());
		clear(inReq);
	}

	// public void saveUserState(WebPageRequest inReq) throws Exception
	// {
	// String username = inReq.getRequestParameter("username");
	// User user = getUserManager().getUser(username);
	//
	// String in = inReq.getRequestParameter("currentstate");
	// if ( in != null && in.equals("in"))
	// {
	// user.put("isOut","false");
	// }
	// else
	// {
	// user.put("isOut","true");
	// }
	//		
	// Date d = new Date();
	// DateFormat format = new SimpleDateFormat("M/d h:mm a");
	//		
	//		
	// user.put("inOutModified", format.format(d));
	//		
	// String comments = inReq.getRequestParameter("comments");
	// if ( comments != null)
	// {
	// user.put("inoutcomment", comments);
	// }
	// else
	// {
	// user.remove("inoutcomment");
	// }
	//		
	// getUserManager().saveUser( user );
	// }

	public void saveEvent(WebPageRequest inReq) throws Exception {
		// if they are on vacation then go update the links tree as well
		String vfrom = inReq.getRequestParameter("vfrom");
		String vto = inReq.getRequestParameter("vto");
		if (vto == null) {
			vto = vfrom;
		}
		String summary = inReq.getRequestParameter("summary");
		String description = inReq.getRequestParameter("description");

		UserCalendar usercal = getUserCalendar(inReq);
		Calendar cal = usercal.getSelectedCalendar();

		String eventId = inReq.getRequestParameter("eventId");
		Event oldEvent = null;
		if (eventId != null && eventId.length() > 0) {
			oldEvent = cal.getEvent(eventId);
			if (oldEvent != null) {
				cal.removeEvent(oldEvent);
			}
		}
		// append event
		DateFormat format = null;
		if (vfrom.length() > "MM-dd-yyyy".length()) {
			format = new SimpleDateFormat("M-dd-yyyy h:mm aa");
		} else {
			format = new SimpleDateFormat("M-dd-yyyy");
		}
		// format.setLenient(true);

		Date from = format.parse(vfrom);
		Date to = format.parse(vto);
		Event newEvent = cal.addEvent(from, to, summary);
		if (eventId != null && eventId.length() > 0) {
			newEvent.setId(eventId); // set the old id on the new event
			if (oldEvent != null) {
				newEvent.getAttendes().addAll(oldEvent.getAttendes());
			}
		}

		String[] categories = inReq.getRequestParameters("category");

		if (categories != null) {
			for (int i = 0; i < categories.length; i++) {
				newEvent.addCategory(categories[i]);
			}
		}
		
	 categories = inReq.getRequestParameters("category.value");

		if (categories != null) {
			for (int i = 0; i < categories.length; i++) {
				newEvent.addCategory(categories[i]);
			}
		}
		
		
		String location = inReq.getRequestParameter("location");
		if(location == null){
			location = inReq.getRequestParameter("location.value");
		}
		newEvent.setLocation(location);
		newEvent.setDescription(description);

		for (Iterator iterator = inReq.getParameterMap().keySet().iterator(); iterator
				.hasNext();) {
			String key = (String) iterator.next();
			if (key.startsWith("property.")) {
				String value = inReq.getRequestParameter(key);
				newEvent
						.setProperty(key.substring("xproperty".length()), value);
			}
		}
		populateProperties(inReq, newEvent);
		getCalendarArchive().saveCalendar(cal, inReq.getUser());

	}

	
	
	public void saveCalendar(WebPageRequest inReq) throws Exception {
		
		UserCalendar usercal = getUserCalendar(inReq);
		Calendar cal = usercal.getSelectedCalendar();

		populateProperties(inReq, cal);
		getCalendarArchive().saveCalendar(cal, inReq.getUser());
		
	}
	
	
	
	protected void populateProperties(WebPageRequest inReq, Event inNewEvent) {
		String[] fields = inReq.getRequestParameters("field");
		if (fields != null) {
			for (String field : fields) {
				if (field != null && field.length() > 0) {
					String val = inReq.getRequestParameter(field + ".value");
					inNewEvent.setProperty(field, val);
				}
			}
		}
	}

	
	protected void populateProperties(WebPageRequest inReq, Calendar inCalendar) {
		String[] fields = inReq.getRequestParameters("field");
		if (fields != null) {
			for (String field : fields) {
				if (field != null && field.length() > 0) {
					String val = inReq.getRequestParameter(field + ".value");
					inCalendar.setProperty(field, val);
				}
			}
		}
		
	}
	
	
	public void deleteEvent(WebPageRequest inReq) throws Exception {
		UserCalendar usercal = getUserCalendar(inReq);
		Calendar cal = usercal.getSelectedCalendar();

		// find the row
		String eventname = inReq.getRequestParameter("deleteevent");
		Event event = cal.getEvent(eventname);
		cal.removeEvent(event);
		getCalendarArchive().saveCalendar(cal, inReq.getUser());
		// save to xml
	}

}
