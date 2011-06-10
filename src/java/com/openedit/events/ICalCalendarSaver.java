/*
 * Created on May 9, 2005
 */
package com.openedit.events;

import java.io.Writer;

import net.fortuna.ical4j.data.CalendarOutputter;

/**
 * @author cburkey
 *
 */
public class ICalCalendarSaver
{
//	SimpleDateFormat fieldFormat;
	
	public void saveCalendar(Calendar inCal, Writer outFile) throws Exception
	{
	    CalendarOutputter outputter = new CalendarOutputter();
	    outputter.setValidating(false);
	    //temp file to catch any errors
    	outputter.output(inCal, outFile);

	}
	/*
	public void saveCalendarXml(Calendar inCal, File outFile) throws Exception
	{
			Document doc = DocumentHelper.createDocument();
			doc.setRootElement(DocumentHelper.createElement("calendar"));
			populate( doc.getRootElement(), inCal);

			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			OutputStream out = new FileOutputStream(outFile);
			try
			{
				XMLWriter writer = new XMLWriter(out,format);
				writer.write(doc);
			}
			finally
			{
				out.close();
			}
	}

	private void populate(Element inRootElement, Calendar inCal)
	{
		//crop off some dates? Remove
		GregorianCalendar start = new GregorianCalendar();
		start.add(GregorianCalendar.MONTH,-6);

		GregorianCalendar end = new GregorianCalendar();
		end.setTime(start.getTime());
		end.add(GregorianCalendar.MONTH,18);
		
		List events = inCal.findEventsFromTo(start,end);
		for (Iterator iter = events.iterator(); iter.hasNext();)
		{
			VEvent	event = (VEvent) iter.next();
			Element child = inRootElement.addElement("event");
			child.addAttribute("start",getFormat().format(event.getStartDate().getDate()));
			child.addAttribute("end",getFormat().format(event.getEndDate().getDate()));
			String val = event.getProperties().getProperty(Property.SUMMARY).getValue();
			child.addElement("summary").setText(val);
			val = event.getProperties().getProperty(Property.ATTENDEE).getValue();
			child.addElement("attendee").setText(val);
		}
	}
	
	public SimpleDateFormat getFormat()
	{
		if ( fieldFormat == null)
		{
			fieldFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		}
		return fieldFormat;
	}
	public void setFormat(SimpleDateFormat inFormat)
	{
		fieldFormat = inFormat;
	}
	*/
}
