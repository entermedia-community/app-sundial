/*
 * Created on May 9, 2005
 */
package com.openedit.events;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import net.fortuna.ical4j.model.DateTime;

/**
 * @author cburkey
 *
 */
public class Day
{
	DateTime fieldDate;
	public DateTime getDate()
	{
		return fieldDate;
	}
	public void setDate(DateTime inDate)
	{
		fieldDate = inDate;
	}
	public void setDate(GregorianCalendar inDate)
	{
		fieldDate = new DateTime( inDate.getTime() );
	}
	public String getNumber()
	{
		if( fieldDate == null)
		{
			return ""; //used to pad the GUI
		}
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(getDate());
		return String.valueOf( cal.get(Calendar.DAY_OF_MONTH) );
	}
	public int getWeekOfMonth()
	{
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(getDate());
		return cal.get(Calendar.WEEK_OF_MONTH);
	}
	public String toString()
	{
		return SimpleDateFormat.getDateTimeInstance().format(getDate());
	}
}
