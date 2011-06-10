/*
 * Created on May 9, 2005
 */
package com.openedit.events;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.HasCategoryRule;
import net.fortuna.ical4j.filter.PeriodRule;
import net.fortuna.ical4j.model.CategoryList;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.XProperty;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.openedit.page.Page;

/**
 * @author cburkey
 *
 */
public class Calendar  extends net.fortuna.ical4j.model.Calendar implements Comparable 
{
	private static final Log log = LogFactory.getLog(Calendar.class);
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 134234324;
	protected String fieldId;
	protected Page fieldPage;
	protected String fieldColor;
	public DateFormat getDateFormat() {
		return fieldDateFormat;
	}

	public void setDateFormat(DateFormat inDateFormat) {
		fieldDateFormat = inDateFormat;
	}

	protected DateFormat fieldDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    protected VTimeZone tz;

	public void setCommponents(ComponentList inList)
	{
		components = inList;
	}
	
	
	public List findEvents(String inFrom, String inTo ) throws Exception
	{
		Date from = getDateFormat().parse(inFrom);
		Date to = getDateFormat().parse(inTo);
		
		return findEvents( from,to );

	}
	
	
	public List findEvents(Date inDay)
	{
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.setTime(inDay);
	    int year = cal.get(cal.YEAR);
		int month = cal.get(cal.MONTH);
		int day = cal.get(cal.DAY_OF_MONTH);
		
		cal.set(year, month, day, 0, 0 );
	    Date from = cal.getTime();
	    
	    cal.set(year,month, day, 23, 59 );
		Date to = cal.getTime();
	    return findEvents(from, to, getComponents());
	}
	public List findEventsInCategory(Date inDay, String inCategoryId){
		List events = findEvents(inDay);
		if(events == null){
			return null;
		}
		for (Iterator iterator = events.iterator(); iterator.hasNext();) {
			Event event = (Event) iterator.next();
			if(!event.isInCategory(inCategoryId)){
				iterator.remove();
			}
		}
		return events;
	}
	
	
	public List findEventsInCategory(String inCategoryId){
		List events =getAllEvents();
		if(events == null){
			return null;
		}
		for (Iterator iterator = events.iterator(); iterator.hasNext();) {
			Event event = (Event) iterator.next();
			if(!event.isInCategory(inCategoryId)){
				iterator.remove();
			}
		}
		return events;
	}
	
	
	
	
	
	public List findEvents(Date inFrom , Date inTo)
	{
		return findEvents(inFrom, inTo, getComponents());
	}
	public List findEvents(Date inFrom , Date inTo, Collection inComponents)
	{
        Period period = new Period(new DateTime(inFrom), new DateTime( inTo ) );// new Dur(inFrom,inTo));
        Filter filter = new Filter(new PeriodRule(period));
	
        Collection components = filter.filter(inComponents);
        
        List results = new ArrayList();
        // must contain at least one component
        for (Iterator i = components.iterator(); i.hasNext();) {
            Component component = (Component) i.next();
           VEvent event = (VEvent) component;
           results.add(new Event( event, this ) );
        }
        Collections.sort(results);
        return results;
	}

	public List findEventsForCategories(List inCategories)
	{
		CategoryList list = new CategoryList();
		for (Iterator iter = inCategories.iterator(); iter.hasNext();)
		{
			String cat  = (String) iter.next();
			list.add(cat);
		}
		
		Categories	cats  = new Categories(list);
        Filter filter = new Filter(new HasCategoryRule(cats));
        Collection components = filter.filter(getComponents());
        
        List results = new ArrayList();
        // must contain at least one component
        for (Iterator i = components.iterator(); i.hasNext();) {
            Component component = (Component) i.next();
           VEvent event = (VEvent) component;
           results.add(new Event( event, this ) );
        }
        Collections.sort(results);
		return results;
	}
	public List findEventsForLocations(List inLocations)
	{
		List list = new ArrayList();
		for (Iterator iter = inLocations.iterator(); iter.hasNext();)
		{
			String loca  = (String) iter.next();
			list.add(new Location(loca) );
		}
		
        Filter filter = new Filter(new HasLocationRule(list));
        Collection components = filter.filter(getComponents());
        
        List results = new ArrayList();
        // must contain at least one component
        for (Iterator i = components.iterator(); i.hasNext();) {
            Component component = (Component) i.next();
           VEvent event = (VEvent) component;
           results.add(new Event( event, this ) );
        }
        Collections.sort(results);
		return results;
	}

	public List findEventsForAttendee(String inAttendee)
	{
        List results = new ArrayList();
        // must contain at least one component
        for (Iterator i = getComponents().iterator(); i.hasNext();) {
            Component component = (Component) i.next();
            if (Component.VEVENT.equals(component.getName())) {
                VEvent event = (VEvent) component;
                PropertyList props = event.getProperties().getProperties(Property.ATTENDEE);
                for (Iterator iter = props.iterator(); iter.hasNext();)
				{
					Property prop = (Property) iter.next();
	                if ( inAttendee.equalsIgnoreCase( prop.getValue() ) )
	                {
	                	results.add(new Event(event, this));
	                	break;
	                }					
				}
            }
        }
        Collections.sort(results);
        return results;
	}

	
	protected VTimeZone getTimeZone()
	{
		if( tz == null)
		{
			TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
			String name = java.util.TimeZone.getDefault().getID();
			net.fortuna.ical4j.model.TimeZone zone = registry.getTimeZone(name);
			if( zone == null)
			{
				log.error("No default time zone set or ICAL not configured for " + name + " using NY time");
				zone = registry.getTimeZone("America/New_York");
				//Look in the ical jar file for what cities are supported
			}
			tz = zone.getVTimeZone();
		}
		return tz;

	}

	/**
	 * @param inStart
	 * @param inEnd
	 * @param inTitle
	 */
	public Event createEvent(Date inStart, Date inEnd, String inTitle )
	{ 
		DateTime start = new DateTime(inStart);
		DateTime end = new DateTime(inEnd);
        VEvent entry = new VEvent(start, end,inTitle);
        
        TzId tzId = new TzId(getTimeZone().getProperty(Property.TZID).getValue());
        entry.getProperty(Property.DTSTART).getParameters().add(tzId);

        String uid = new Date().getTime() + String.valueOf(getComponents().size());
        
        Uid id = new Uid(uid);
        entry.getProperties().add(id);
        
        return new Event(entry, this);
	}

	public Event addEvent(Date inStart, Date inEnd, String inTitle )
	{ 
		Event event = createEvent(inStart, inEnd, inTitle);
        return addEvent(event);
	}

	public Event addEvent(Event inEvent )
	{ 
        getComponents().add(inEvent.getEvent());
        return inEvent;
	}

	
	/**
	 * @param inProperties
	 */
	public void setProperties(PropertyList inProperties)
	{
		properties = inProperties;
	}
	
	
	/**
	 * @param inEventId
	 * @return
	 */
	public Event getEvent(String inEventId)
	{
        for (Iterator i = getComponents().iterator(); i.hasNext();) 
        {
            Component component = (Component) i.next();
            if (Component.VEVENT.equals(component.getName())) 
            {
                VEvent event = (VEvent) component;
                Property id = event.getProperties().getProperty(Property.UID);
                if ( id != null)
                {
                	if( inEventId.equals(id.getValue()))
                	{
                		return new Event(event, this );
                	}
                }
            }
        }
		return null;
	}
	public String getId()
	{
		return fieldId;
	}
	public void setId(String inId)
	{
		fieldId = inId;
	}
	public Page getPage()
	{
		return fieldPage;
	}
	public void setPage(Page inPage)
	{
		fieldPage = inPage;
	}
	public String getColor()
	{
		return fieldColor;
	}
	public void setColor(String inColor)
	{
		fieldColor = inColor;
	}
	public List getAllEvents()
	{
	        
        List results = new ArrayList();
        // must contain at least one component
        for (Iterator i = getComponents("VEVENT").iterator(); i.hasNext();) {
            VEvent event = (VEvent) i.next();
           results.add(new Event( event, this ) );
        }
        Collections.sort(results);
        return results;
	}
	public void removeEvent(Event inEvent)
	{
		getComponents().remove(inEvent.getEvent());
	}
	public boolean equals(Object inObject)
	{
		boolean top = super.equals(inObject);
		Calendar cal = (Calendar)inObject;
		return top && getId().equals(cal.getId());
	}
	
	
	public void setProperty(String inName, String inValue)
	{		
        PropertyList properties = getProperties();
        for (Iterator i = properties.iterator(); i.hasNext();) 
        {
        	Property c = (Property) i.next();
        	if( c.getName().equalsIgnoreCase("X-" + inName) )
        	{
        		properties.remove(c);
        		break;
        	}
        }
		addProperty(inName, inValue);
	}
	
	public void addProperty(String inName, String inValue)
	{
		XProperty prop = new XProperty( "X-" + inName.toUpperCase(),inValue);
		//prop.getParameters().add(new XParameter("id",inName));
		//prop.setValue(inValue);
		getProperties().add(prop);
	}
	
	public String get(String inKey)
	{
		inKey = inKey.toUpperCase();
        PropertyList properties = getProperties();
        for (Iterator i = properties.iterator(); i.hasNext();) 
        {
        	Property c = (Property) i.next();
        	if( c.getName().startsWith("X-" + inKey) )
        	{
        		return c.getValue();
        	}
        }            
		return null;
	}
	public String getName(){
		if(get("name") != null){
			return get("name");
		}
		else{
			return getId();
		}
		
	}

	
	public String getSourcePath() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void setName(String inName) {
	setProperty("name", inName);
		
	}

	
	public int compareTo(Object inO)
	{
		if( inO instanceof Calendar )
		{
			Calendar cal = (Calendar)inO;
			String name = cal.getName();
			if(name == null){
				name = cal.getId();
			}
			String name2 = getName();
			if(name2 == null){
				name2 = getId();
			}
			return name2.compareTo(name);
		}
		
		return -1;
	}

	
	
	
}
