package com.openedit.events;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.openedit.Data;
import org.openedit.data.BaseSearcher;

import com.openedit.OpenEditException;
import com.openedit.OpenEditRuntimeException;
import com.openedit.hittracker.HitTracker;
import com.openedit.hittracker.ListHitTracker;
import com.openedit.hittracker.SearchQuery;
import com.openedit.users.User;

public class CalendarSearcher extends BaseSearcher {

	protected UserCalendarArchive fieldCalendarArchive;

	public UserCalendarArchive getCalendarArchive() {
		return fieldCalendarArchive;
	}

	public void setCalendarArchive(UserCalendarArchive inCalendarArchive) {
		fieldCalendarArchive = inCalendarArchive;
	}

	
	public void clearIndex() {
		// TODO Auto-generated method stub

	}

	public SearchQuery createSearchQuery() {

		return new SearchQuery();
	}

	public void delete(Data inData, User inUser) {
		if (inData instanceof Calendar) {
			getCalendarArchive().deleteCalendar((Calendar) inData);
		}

	}

	public void deleteAll(User inUser) {
		try {
			List all = getCalendarArchive().getUserCalendar(getCatalogId(), true).getAllCalendars();
			for (Iterator iterator = all.iterator(); iterator.hasNext();) {
				Object object = (Object) iterator.next();
				delete((Data) object, inUser);
			}
		} catch (Exception e) {
			throw new OpenEditRuntimeException(e);
		}

	}

	
	public String getIndexId() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void reIndexAll() throws OpenEditException {
		// TODO Auto-generated method stub

	}

	
	public void saveAllData(Collection<Data> inAll, User inUser) {
		for (Iterator iterator = inAll.iterator(); iterator.hasNext();) {
			Data object = (Data) iterator.next();
			saveData(object, inUser);
		}

	}

	
	public void saveData(Data inData, User inUser) {
		if(inData instanceof Calendar){
			getCalendarArchive().saveCalendar((Calendar) inData, inUser);
		}


	}

	
	public HitTracker search(SearchQuery inQuery) {
		try {
			List calendars = getCalendarArchive().getUserCalendar(getCatalogId(), false).getAllCalendars();
			return new ListHitTracker(calendars);
		} catch (Exception e) {
			throw new OpenEditException(e);
		}
	}

	
	
	
}
