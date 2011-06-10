/*
 * Created on May 9, 2005
 */
package com.openedit.events;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

/**
 * @author cburkey
 *
 */
public class Month
{
	GregorianCalendar fieldMonthStart;
	//Calendar fieldCalendar;
	Month fieldNextMonth;
	Month fieldPrevMonth;
	String fieldId;
	
	public List getAllDays()
	{
		//loop over each day of the month
		int count = getMonthStart().getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		List results = new ArrayList(count);
		//System.out.println(SimpleDateFormat.getDateTimeInstance().format(getMonthStart().getTime()));
		for (int i = 0; i < count; i++)
		{
			GregorianCalendar date = new GregorianCalendar();
			date.setTime(getMonthStart().getTime());
//			date.set(GregorianCalendar.HOUR_OF_DAY,0);
			date.add(GregorianCalendar.DAY_OF_MONTH,i);
			
//			GregorianCalendar dateend = new GregorianCalendar();
//			dateend.setTime(getMonthStart().getTime());
//			dateend.set(GregorianCalendar.HOUR_OF_DAY,0);
//			dateend.add(GregorianCalendar.DAY_OF_MONTH,i+1);
//			dateend.add(GregorianCalendar.MILLISECOND,-1);

			//find the list of events that is closest 
			//List events = getCalendar().findEventsFromTo(date,dateend);
			Day day = new Day();
			day.setDate(date);
			//day.setEvents(events);
			results.add(day);
		}
		return results;
	}
	public List getDaysAsWeeks()
	{
		//make a list of weeks, put blank days in to start the week
		List allweeks = new ArrayList();
		List currentWeek = new ArrayList(7);
		allweeks.add(currentWeek);
		//pad the end with blank days
		for (int i = 0; i < getMonthStart().get(GregorianCalendar.DAY_OF_WEEK) - 1; i++)
		{
			Day day = new Day();
			currentWeek.add(day);
		}
		
		for (Iterator iter = getAllDays().iterator(); iter.hasNext();)
		{
			Day day = (Day) iter.next();
			//getMonthStart().get(GregorianCalendar.W)
			if ( day.getWeekOfMonth() > allweeks.size())
			{
				currentWeek = new ArrayList();
				allweeks.add(currentWeek);
			}
			currentWeek.add(day);
		}
		//pad the end with blank days
		for (int i = currentWeek.size(); i < 7; i++)
		{
			Day day = new Day();
			currentWeek.add(day);
		}
		return allweeks;
	}
	public GregorianCalendar getMonthStart()
	{
		return fieldMonthStart;
	}
	public void setMonthStart(GregorianCalendar inMonthStart)
	{
		fieldMonthStart = inMonthStart;
		
		fieldId = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT).format(getMonthStart().getTime());
	}
	public String getId()
	{
		return fieldId;
	}
	public String getDescription()
	{
		SimpleDateFormat format = new SimpleDateFormat("MMMM yyyy");
		return format.format(getMonthStart().getTime());
	}
	public Month getNextMonth()
	{
		return fieldNextMonth;
	}
	public void setNextMonth(Month inNextMonth)
	{
		fieldNextMonth = inNextMonth;
	}
	public Month getPrevMonth()
	{
		return fieldPrevMonth;
	}
	public void setPrevMonth(Month inPrevMonth)
	{
		fieldPrevMonth = inPrevMonth;
	}
}
