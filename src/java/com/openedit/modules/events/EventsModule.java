/*
 * Created on Jul 20, 2006
 */
package com.openedit.modules.events;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.openedit.OpenEditException;
import com.openedit.WebPageRequest;
import com.openedit.events.Calendar;
import com.openedit.events.Month;
import com.openedit.events.UserCalendar;
import com.openedit.hittracker.HitTracker;
import com.openedit.hittracker.ListHitTracker;
import com.openedit.page.Page;
import com.openedit.util.PathUtilities;

public class EventsModule extends BaseSchedulerModule {
	public void selectDateRange(WebPageRequest inReq) throws Exception {
		String smonth = inReq.getRequestParameter("smonth");
		if (smonth != null) {
			UserCalendar cal = getUserCalendar(inReq);
			Month month = cal.getMonth(smonth);
			cal.setSelectedMonth(month);
		}
	}

	public HitTracker loadHits(WebPageRequest inReq) throws Exception {
		UserCalendar cal = getUserCalendar(inReq);
		HitTracker tracker = (HitTracker) inReq.getSessionValue("hits"
				+ cal.getEventsId());
		if (tracker != null) {
			inReq.putPageValue("hits", tracker);
		}
		return tracker;
	}

	public void selectCalendar(WebPageRequest inReq) throws Exception {
		String cal = inReq.findValue("calendar");
		if (cal == null) {
			String pagename = inReq.getPage().getName();
			cal = pagename.substring(0, pagename.lastIndexOf("."));

		}

		if (cal != null) {
			UserCalendar usercals = getUserCalendar(inReq);
			if (cal.equals("all")) {
				usercals.setSelectedCalendar(null);
			} else {
				Calendar calendar = usercals.getMixedInCalendar(cal);
				if (calendar != null) {
					usercals.setSelectedCalendar(calendar);
					inReq.putPageValue("calendarpage", calendar.getPage());
				}
			}
		}
	}

	public void loadMonthList(WebPageRequest inReq) throws Exception {
		UserCalendar cal = getUserCalendar(inReq);
		inReq.putSessionValue("months", cal.getMonthList());
	}

	public void searchByCategory(WebPageRequest inReq) throws Exception {
		String page = inReq.getRequestParameter("page");
		if (page == null) {
			UserCalendar user = getUserCalendar(inReq);

			String[] cats = inReq.getRequestParameters("category");
			if (cats == null || cats.length == 0) {
				String cat = inReq.getPage().getName();
				cat = PathUtilities.extractPageName(cat);
				cats = new String[] { cat };
			}
			List all = Arrays.asList(cats);
			List results = user.findEventsForCategories(all);

			HitTracker tracker = new ListHitTracker(results);
			tracker.setHitsPerPage(10);

		//	inReq.putSessionValue("hits" + user.getEventsId(), tracker);
			inReq.putPageValue("hits", tracker);
		} else {
			// switching page
			HitTracker tracker = loadHits(inReq);
			int p = Integer.parseInt(page);
			tracker.setPage(p);
		}
	}

	public void searchByLocation(WebPageRequest inReq) throws Exception {
		String page = inReq.getRequestParameter("page");
		if (page == null) {
			UserCalendar user = getUserCalendar(inReq);

			String[] cats = inReq.getRequestParameters("location");
			if (cats == null || cats.length == 0) {
				String cat = inReq.getPage().getName();
				cat = PathUtilities.extractPageName(cat);
				cats = new String[] { cat };
				inReq.putPageValue("locationid", cat);
			}
			List all = Arrays.asList(cats);
			List results = user.findEventsForLocations(all);

			HitTracker tracker = new ListHitTracker(results);
			tracker.setHitsPerPage(10);

		//	inReq.putSessionValue("hits" + user.getEventsId(), tracker);
			inReq.putPageValue("hits", tracker);

		} else {
			// switching page
			HitTracker tracker = loadHits(inReq);
			int p = Integer.parseInt(page);
			tracker.setPage(p);
		}
	}

	public void loadCurrentEvents(WebPageRequest inReq) throws Exception {
		String page = inReq.getRequestParameter("page");
		HitTracker tracker = loadHits(inReq);
		UserCalendar user = getUserCalendar(inReq);
		if (page == null || tracker == null) {

			Date from = new Date();
			GregorianCalendar to = new GregorianCalendar();
			to.add(java.util.Calendar.MONTH, 1);

			List results = user.findEvents(from, to.getTime());

			tracker = new ListHitTracker(results);
			tracker.setHitsPerPage(10);

		//	inReq.putSessionValue("hits" + user.getEventsId(), tracker);
			inReq.putPageValue("hits", tracker);

		} else {
			// switching page
			int p = Integer.parseInt(page);
			tracker.setPage(p);
		}
	}

	public void loadEventRange(WebPageRequest inReq) throws Exception {
		String page = inReq.getRequestParameter("page");
		String months = inReq.findValue("monthstoload");
		int add = Integer.parseInt(months);
		HitTracker tracker = loadHits(inReq);
		UserCalendar user = getUserCalendar(inReq);
		if (page == null || tracker == null) {

			Date from = new Date();
			GregorianCalendar to = new GregorianCalendar();
			to.add(java.util.Calendar.MONTH, add);

			List results = user.findEvents(from, to.getTime());

			tracker = new ListHitTracker(results);
			tracker.setHitsPerPage(10);

		//inReq.putSessionValue("hits" + user.getEventsId(), tracker);
			inReq.putPageValue("hits", tracker);

		} else {
			// switching page
			int p = Integer.parseInt(page);
			tracker.setPage(p);
		}
	}

	public void search(WebPageRequest inReq) throws Exception {
		String page = inReq.getRequestParameter("page");
		if (page == null) {
			UserCalendar user = getUserCalendar(inReq);

			String[] cats = inReq.getRequestParameters("category");
			String[] locs = inReq.getRequestParameters("location");
			List catlist = new ArrayList();
			catlist.add("ANY");
			if (cats != null) {
				catlist = Arrays.asList(cats);
			}
			List loclist = new ArrayList();
			loclist.add("ANY");
			if (locs != null) {
				loclist = Arrays.asList(locs);
			}

			String vfrom = inReq.getRequestParameter("vfrom");
			String vto = inReq.getRequestParameter("vto");
			DateFormat format = new SimpleDateFormat("M-dd-yyyy h:mm aa");
			Date from = new Date(0); // defaults to really far in the past
			if (vfrom != null) {
				from = format.parse(vfrom);
			}
			Date to = new GregorianCalendar(3000, 1, 1).getTime(); // defaults
																	// to really
																	// far in
																	// the
																	// future
			if (vto != null) {
				to = format.parse(vto);
			}

			List results = user.findEvents(catlist, loclist, from, to);

			HitTracker tracker = new ListHitTracker(results);
			tracker.setHitsPerPage(10);

		//	inReq.putSessionValue("hits" + user.getEventsId(), tracker);
			inReq.putPageValue("hits", tracker);

		} else {
			// switching page
			HitTracker tracker = loadHits(inReq);
			int p = Integer.parseInt(page);
			tracker.setPage(p);
		}
	}

	public void loadListView(WebPageRequest inReq) throws Exception {
		String page = inReq.getRequestParameter("page");
		if (page == null) {
			UserCalendar user = getUserCalendar(inReq);
			List results = user.getAllSelectedEvents();
			HitTracker tracker = new ListHitTracker(results);
			tracker.setHitsPerPage(10);
		//	inReq.putSessionValue("hits" + user.getEventsId(), tracker);
			inReq.putPageValue("hits", tracker);

		} else {
			// switching page
			HitTracker tracker = loadHits(inReq);
			int p = Integer.parseInt(page);
			tracker.setPage(p);
		}
	}

	public void listDataDirs(WebPageRequest inReq) throws Exception {
		String eventid = inReq.findValue("eventsid");
		List dirs = getCalendarArchive().listDataDirs(eventid);
		inReq.putPageValue("dataDirs", dirs);
	}

	public void pushSelectedCalendar(WebPageRequest inReq) throws Exception {
		String eventid = inReq.findValue("eventsid");
		String path = inReq.getRequestParameter("datadir");
		if (path == null) {
			throw new OpenEditException("Must specify a datadir");
		}
		UserCalendar user = getUserCalendar(inReq);
		Page source = user.getSelectedCalendar().getPage();
		getCalendarArchive().push(inReq.getUser(), source, path);
	}

}
