/*
 * Created on Jul 20, 2006
 */
package com.openedit.events;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.fortuna.ical4j.filter.PeriodRule;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Period;

import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

import com.openedit.OpenEditException;

public class UserCalendar
{
	private static final Log log = LogFactory.getLog(UserCalendar.class);

	protected String fieldEventsId;
	protected Map fieldMixedInCalendars;
	protected Calendar fieldSelectedCalendar;
	protected Month fieldSelectedMonth;
	protected List fieldMonths;
	protected Element fieldCategories;
	protected Element fieldLocations;
	protected boolean isAuthenticated;
	
	public DateFormat getDateFormat() {
		return fieldDateFormat;
	}

	public void setDateFormat(DateFormat inDateFormat) {
		fieldDateFormat = inDateFormat;
	}

	protected DateFormat fieldDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	public Map getMixedInCalendars()
	{
		if (fieldMixedInCalendars == null)
		{
			fieldMixedInCalendars = ListOrderedMap.decorate(new HashMap());
		}
		return fieldMixedInCalendars;
	}

	public Calendar getMixedInCalendar(String inCal)
	{
		Calendar cal = (Calendar)getMixedInCalendars().get(inCal);
		return cal;
	}
	
	public List getAllCalendars()
	{
		List all = new ArrayList(getMixedInCalendars().size());
		for (Iterator iter = getMixedInCalendars().keySet().iterator(); iter.hasNext();)
		{
			String calid = (String)iter.next();
			all.add(getMixedInCalendars().get(calid)); 
		}
		Collections.sort(all);
		return all;
	}
	
	public void addMixedInCalendar(Calendar inCal)
	{
		getMixedInCalendars().put(inCal.getId(),inCal);
	}
	public String getEventsId()
	{
		return fieldEventsId;
	}

	public void setEventsId(String inEventsId)
	{
		fieldEventsId = inEventsId;
	}

	public Calendar getSelectedCalendar()
	{
//		This is probably to deal with a null pointer somewhere, but it's not a good idea - it causes a lot of problems where other methods expect to aggregate calendars
//		based on whether or not this is null
		if( fieldSelectedCalendar == null && getMixedInCalendars().size() > 0)
		{
			return (Calendar)getAllCalendars().get(0);
		}
		return fieldSelectedCalendar;
	}

	public void setSelectedCalendar(Calendar inCalendar)
	{
		fieldSelectedCalendar = inCalendar;
	}

	public List getMonthList()
	{
		if (fieldMonths == null)
		{
			fieldMonths = new ArrayList();
			GregorianCalendar cal = new GregorianCalendar();
			cal.set(GregorianCalendar.MINUTE,0);
			cal.set(GregorianCalendar.SECOND,0);
			cal.set(GregorianCalendar.MILLISECOND,0);
			cal.set(GregorianCalendar.HOUR,0);
			cal.set(GregorianCalendar.HOUR_OF_DAY,0);
			
			cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
			cal.add(GregorianCalendar.MONTH, -7);

			System.out.println(SimpleDateFormat.getDateTimeInstance().format(cal.getTime()));

			GregorianCalendar today = new GregorianCalendar();

			Month prevMonth = null;
			for (int i = 0; i < 24; i++)
			{
				cal.add(GregorianCalendar.MONTH, 1); //Increate our counter
				GregorianCalendar newMonth = new GregorianCalendar();
				newMonth.setTime(cal.getTime()); //Save off this value
				Month stub = new Month();
				stub.setMonthStart(newMonth);
				fieldMonths.add(stub);
				stub.setPrevMonth(prevMonth);
				if (prevMonth != null)
				{
					prevMonth.setNextMonth(stub);
				}
				prevMonth = stub;
				if (today.get(GregorianCalendar.MONTH) == cal.get(GregorianCalendar.MONTH)
					&& today.get(GregorianCalendar.YEAR) == cal.get(GregorianCalendar.YEAR)
				)
				{
					setSelectedMonth(stub);
				}
			}
		}
		return fieldMonths;
	}

	/**
	 * @param inMonth numeric
	 * @param inYear numeric
	 * @return
	 */
	public Month findMonth(String startDate) throws OpenEditException
	{
		Month themonth = null;
		for (Iterator iter = getMonthList().iterator(); iter.hasNext();)
		{
			Month month = (Month) iter.next();
			if (month.getId().equals(startDate))
			{
				themonth = month;
				break;
			}
		}
		//themonth.setCalendar(this);
		return themonth;
	}
	public List findEventsForMonth(Month inMonth)
	{
		//use the period search thing
		List all = new ArrayList();
		
		Date start = inMonth.getMonthStart().getTime();
		Date end = inMonth.getNextMonth().getMonthStart().getTime();
		
		for (Iterator iter = getMixedInCalendars().values().iterator(); iter.hasNext();)
		{
			Calendar cal = (Calendar) iter.next();
			boolean include = true;
			if( fieldSelectedCalendar != null)
			{
				if( cal != fieldSelectedCalendar)
				{
					include = false;
				}
			}
			if( include )
			{
				List hits = cal.findEvents(start,end);
				all.addAll(hits);
			}
		}
		return all;
		
	}
	public List findEventsForCategories(List categories )
	{
		List all = new ArrayList();

		for (Iterator iter = getMixedInCalendars().values().iterator(); iter.hasNext();)
		{
			Calendar cal = (Calendar) iter.next();
			List hits = cal.findEventsForCategories(categories);
			all.addAll(hits);
		}
		return all;
	}
	
	public List findEventsForLocations(List inLocations)
	{
		List all = new ArrayList();

		for (Iterator iter = getMixedInCalendars().values().iterator(); iter.hasNext();)
		{
			Calendar cal = (Calendar) iter.next();
			List hits = cal.findEventsForLocations(inLocations);
			all.addAll(hits);
		}
		return all;
	}

	public List findEvents(String inFrom, String inTo ) throws Exception
	{
		Date from = getDateFormat().parse(inFrom);
		Date to = getDateFormat().parse(inTo);
		
		return findEvents(null,null, from,to );

	}
	
	
	
	public List findEvents(Date inFrom, Date inTo )
	{
		return findEvents(null,null, inFrom,inTo );

	}
	
	public List findEvents(List inCategories, List inLocations, Date inFrom, Date inTo )
	{
		List all = new ArrayList();

		for (Iterator iter = getMixedInCalendars().values().iterator(); iter.hasNext();)
		{
			Calendar cal = (Calendar) iter.next();
			List dated = cal.findEvents(inFrom, inTo);
			for (Iterator iterator = dated.iterator(); iterator.hasNext();)
			{
				Event event = (Event) iterator.next();
				if (inCategories == null 
						|| inCategories.contains("ANY")
						|| event.getCategoryNames().containsAll(inCategories))
				{
					if (inLocations == null
							|| inLocations.contains("ANY")
							|| inLocations.contains(event.getLocation()))
					{
						all.add(event);
					}
				}
			}
		}
		return all;
	}

	public List findEventsForDay(Day inDay, Collection inEventsPool)
	{
		if( inDay.getDate() == null)
		{
			return Collections.EMPTY_LIST;
		}
		//loop over all the calendars and get the date range
//		List all = new ArrayList();

//		long start = inDay.getDate().getTime();
//		
//					//add 24 hours to start
//		long oneday = 24L * 60L * 60L * 1000L;
//		long	end = start + oneday;
//		
//		Date from = new Date(start);
//		Date finish = new Date( end );

		Period period = new Period(inDay.getDate(), new Dur("1D"));
	    PeriodRule rule = new PeriodRule(period);
	     
        List results = new ArrayList();
        // must contain at least one component
        for (Iterator i = inEventsPool.iterator(); i.hasNext();) {
            Event event = (Event) i.next();
            if( rule.match(event.getEvent()))
            	{
            	//log.info("Adding " + event.getStart() +" summary: "+ event.getSummary() + " end in " + event.getEnd());
            	results.add(event);
            	}
        }
        
        return results;		
	}

	public Month getSelectedMonth()
	{
		return fieldSelectedMonth;
	}

	public void setSelectedMonth(Month inSelectedMonth)
	{
		fieldSelectedMonth = inSelectedMonth;
	}

	public Month getMonth(String inId)
	{
		for (Iterator iter = getMonthList().iterator(); iter.hasNext();)
		{
			Month month = (Month) iter.next();
			if (month.getId().equals(inId))
			{
				return month;
			}
		}
		return null;
	}

	public Event getEvent(String inEventId)
	{
		if(fieldSelectedCalendar != null)
		{
			Event event = getSelectedCalendar().getEvent(inEventId);
			if( event != null)
			{
				return event;
			}
		}
		for (Iterator iter = getMixedInCalendars().values().iterator(); iter.hasNext();)
		{
			Calendar mixedin = (Calendar) iter.next();
			Event event = mixedin.getEvent(inEventId);
			if( event != null)
			{
				return event;
			}

		}
		return null;
	}

	public List getAllSelectedEvents()
	{
		List results = null;
		if( fieldSelectedCalendar != null)
		{
			//pick all
			results = getSelectedCalendar().getAllEvents();
		}
		else
		{
			results = new ArrayList();
			for (Iterator iter = getAllCalendars().iterator(); iter.hasNext();)
			{
				Calendar cal = (Calendar) iter.next();
				results.addAll(cal.getAllEvents());
			}
		}
		
		return results;
	}
	
	public Element getCategories()
	{
		return fieldCategories;
	}

	public void setCategories(Element inCategories)
	{
		fieldCategories = inCategories;
	}

	public Element getLocations()
	{
		return fieldLocations;
	}

	public void setLocations(Element inLocations)
	{
		fieldLocations = inLocations;
	}

	public void addCategory(String inName)
	{
		getCategories().addElement("category").addAttribute("id",inName);
	}
	public void deleteCategory(String inName)
	{
		Element toRemove = null;
		for (Iterator iter = getCategories().elementIterator("category"); iter.hasNext();)
		{
			Element element = (Element) iter.next();
			String id = element.attributeValue("id");
			if (id.equals(inName))
			{
				toRemove = element;
				break;
			}
		}
		if (toRemove != null)
		{
			getCategories().remove(toRemove);
		}
	}
	public void addLocation(String inName)
	{
		getLocations().addElement("location").addAttribute("id",inName);
	}
	public void deleteLocation(String inName)
	{
		Element toRemove = null;
		for (Iterator iter = getLocations().elementIterator("location"); iter.hasNext();)
		{
			Element element = (Element) iter.next();
			String id = element.attributeValue("id");
			if (id.equals(inName))
			{
				toRemove = element;
				break;
			}
		}
		if (toRemove != null)
		{
			getLocations().remove(toRemove);
		}
	}

	public boolean isAuthenticated()
	{
		return isAuthenticated;
	}

	public void setAuthenticated(boolean inIsAuthenticated)
	{
		isAuthenticated = inIsAuthenticated;
	}
	
}
