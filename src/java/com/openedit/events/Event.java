/*
 * Created on May 10, 2005
 */
package com.openedit.events;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.fortuna.ical4j.model.CategoryList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Priority;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.XProperty;

import com.openedit.OpenEditRuntimeException;
import com.openedit.users.User;

/**
 * @author cburkey
 *
 */
public class Event implements Comparable
{
	protected VEvent fieldEvent;
	protected Calendar fieldParent;
	
	public Event()
	{
		
	}
	public Event(VEvent inEvent, Calendar inParent)
	{
		setEvent(inEvent);
		setParent(inParent);
	}
	public VEvent getEvent()
	{
		return fieldEvent;
	}
	public void setEvent(VEvent inEvent)
	{
		fieldEvent = inEvent;
	}
	public String getId()
	{
		Property prop = getEvent().getProperties().getProperty( Property.UID );
		return prop.getValue();
	}
	public void setId(String inId)
	{
		Property old = getEvent().getUid();
		if (old != null)
		{
			getEvent().getProperties().remove(old);
		}
		Uid id = new Uid(inId);
		getEvent().getProperties().add(id);
	}
	public String getSummary()
	{
		String sum = getEvent().getProperties().getProperty(Property.SUMMARY).getValue();
		return sum;
	}
	public String getDescription()
	{
		Property prop = getEvent().getDescription();
		if( prop == null)
		{
			return null;
		}
		String desc = prop.getValue();
		return desc;
	}
	public void setDescription(String inDesc)
	{
		Property desc = getEvent().getDescription();
		if (desc != null)
		{
			getEvent().getProperties().remove(desc);
		}
		getEvent().getProperties().add(new Description(inDesc));
	}
	public String getAttendee()
	{
		return getEvent().getProperties().getProperty(Property.ATTENDEE).getValue();
	}
	public String getStart()
	{
		return SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT).format(getEvent().getStartDate().getDate());
	}
	public Date getStartDate(){
		
		return getEvent().getStartDate().getDate();
	}
	
	public String getStartTime()
	{
		return new SimpleDateFormat("M-dd-yyyy h:mm aa").format(getEvent().getStartDate().getDate());
	}
	public String getEnd()
	{
		return SimpleDateFormat.getDateInstance().format(getEvent().getEndDate().getDate());
	}
	public String getEndTime()
	{		
		return new SimpleDateFormat("M-dd-yyyy h:mm aa").format(getEvent().getEndDate().getDate());
	}
	public String getToFromHours()
	{
		SimpleDateFormat hour = new SimpleDateFormat("h:mm");
		SimpleDateFormat pm = new SimpleDateFormat("a");
		return hour.format(getEvent().getStartDate().getDate()) + "-" 
		+ hour.format(getEvent().getEndDate().getDate() ) + " "
		+  pm.format(getEvent().getEndDate().getDate() );
		
	}
	public List getAttendes()
	{
		List all = new ArrayList();
		PropertyList props = getEvent().getProperties().getProperties(Property.ATTENDEE);
		for (Iterator iter = props.iterator(); iter.hasNext();)
		{
			Property prop = (Property) iter.next();
			all.add(prop.getValue());
			
		}
		return all;
	}
	/**
	 * @param inUser
	 */
	public void addAttendee(User inUser)
	{
		try
		{
			Attendee att = new Attendee(inUser.getUserName() );
			getEvent().getProperties().add(att);
		}
		catch (URISyntaxException ex)
		{
			throw new OpenEditRuntimeException(ex);
		}
	}
	public void setProperty(String inName, String inValue)
	{		
		if(inName.equalsIgnoreCase("location")){
        	setLocation(inValue);
        	return;
        }
		PropertyList properties = getEvent().getProperties();
        for (Iterator i = properties.iterator(); i.hasNext();) 
        {
        	Property c = (Property) i.next();
        	if( c.getName().equals("X-" + inName) )
        	{
        		getEvent().getProperties().remove(c);
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
		getEvent().getProperties().add(prop);
	}
	
	public String getProperty(String inKey)
	{
		if(inKey.equalsIgnoreCase("location")){
			return getLocation();
		}
		inKey = inKey.toUpperCase();
        PropertyList properties = getEvent().getProperties();
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
	public String get(String inKey)
	{
		return getProperty(inKey);
	}
	public String getPriority()
	{
		Priority priotiyy = getEvent().getPriority();
		if( priotiyy != null)
		{
			return "" + priotiyy.getLevel();
		}
		return null;
	}
	public Collection getCategories()
	{
        PropertyList properties = getEvent().getProperties("CATEGORIES");
		List categories = new ArrayList(properties.size());
        for (Iterator i = properties.iterator(); i.hasNext();) {
        	Categories p = (Categories) i.next();
        	categories.add(p);
        }            
        return categories;
	}
	
	public boolean isInCategory(String catid){
		
		Collection cats = getCategories();
		
		for (Iterator iterator = cats.iterator(); iterator.hasNext();) {
			
			Categories	cat = (Categories) iterator.next();
			CategoryList categorylist = cat.getCategories();
			for (Iterator iterator2 = categorylist.iterator(); iterator2.hasNext();) {
				String firstcat = (String) iterator2.next();
				if(firstcat.equals(catid)){
					return true;
				}
			}
		}
		return false;
	}
	
	

	
	
	public Collection getCategoryNames()
	{
        PropertyList properties = getEvent().getProperties("CATEGORIES");
		List categories = new ArrayList(properties.size());
        for (Iterator i = properties.iterator(); i.hasNext();) {
        	Categories p = (Categories) i.next();
        	for (Iterator iter = p.getCategories().iterator(); iter.hasNext();)
			{
				String element = (String) iter.next();
				categories.add(element);
			}
        }            
        return categories;
	}
	public Calendar getParent()
	{
		return fieldParent;
	}
	public void setParent(Calendar inParent)
	{
		fieldParent = inParent;
	}

	public String getLocation()
	{
		Location loc = getEvent().getLocation();
		if( loc != null)
		{
			return loc.getValue();
		}
		return null;
	}
	public void setLocation(String inLocation)
	{
		Location loc = getEvent().getLocation();
		if( loc != null)
		{
			getEvent().getProperties().remove(loc);
		}
		if( inLocation != null)
		{
			loc = new Location(inLocation);
			getEvent().getProperties().add(loc);
		}
	}
	public void addCategory(String inString)
	{
		Categories cats = (Categories)getEvent().getProperty("CATEGORIES");
		if( cats == null)
		{
			cats = new Categories();
			getEvent().getProperties().add(cats);
		}
		CategoryList list = cats.getCategories();
		if( list == null)
		{
			cats.setValue(inString);
		}
		else
		{
			list.add(inString);
		}
	}
	public boolean hasCategory(String inCategory)
	{
		return getCategoryNames().contains(inCategory);
	}
	public int compareTo(Object arg0)
	{
		if( arg0 instanceof Event && getEvent() != null && getEvent().getStartDate() != null)
		{
			Event event = (Event)arg0;
			if( event.getEvent() != null && event.getEvent().getStartDate() != null)
			{
				return getEvent().getStartDate().getDate().compareTo( event.getEvent().getStartDate().getDate() );
			}
		}
		return -1;
	}
	public PropertyList getProperties() {
		 PropertyList properties = getEvent().getProperties();
		return properties;
	}
}
