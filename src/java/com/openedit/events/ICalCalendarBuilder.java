/*
 * Created on May 9, 2005
 */
package com.openedit.events;

import java.io.Reader;
import java.text.SimpleDateFormat;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.openedit.OpenEditException;
import com.openedit.util.FileUtils;

/**
 * @author cburkey
 * 
 */
public class ICalCalendarBuilder {

	SimpleDateFormat fieldFormat;

	private static final Log log = LogFactory.getLog(ICalCalendarBuilder.class);

	public Calendar build() throws OpenEditException {
		return build(null);
	}

	public Calendar build(Reader inIn) throws OpenEditException {
		try {
			Calendar cal = new Calendar();
			if (inIn != null) {
				net.fortuna.ical4j.model.Calendar calendar = new CalendarBuilder()
						.build(inIn);
				cal.setCommponents(calendar.getComponents()); // copy the parts

				PropertyList props = calendar.getProperties();
				cal.setProperties(props);

			} else {
				cal.getProperties().add(
						new ProdId("-//OpenEdit//iCal4j 1.0//EN"));
				cal.getProperties().add(Version.VERSION_2_0);
				cal.getProperties().add(CalScale.GREGORIAN);
			}
			return cal;
		} catch (Exception ex) {
			log.error(ex);
			if (ex instanceof OpenEditException) {
				throw (OpenEditException) ex;
			}
			throw new OpenEditException(ex);
		} finally {
			FileUtils.safeClose(inIn);
		}

	}
	/*
	 * public Calendar buildOld(InputStream inIn) throws OpenEditException {
	 * Calendar cal = new Calendar(); cal.getProperties().add(new
	 * ProdId("-//OpenEdit//iCal4j 1.0//EN"));
	 * cal.getProperties().add(Version.VERSION_2_0);
	 * cal.getProperties().add(CalScale.GREGORIAN);
	 * 
	 * SAXReader reader = new SAXReader(); Document doc = null;
	 * //cal.getComponents().add() try { doc = reader.read( inIn ); for
	 * (Iterator iter = doc.getRootElement().elementIterator("event");
	 * iter.hasNext();) { Element element = (Element) iter.next();
	 * addEvent(cal,element); } //cal.validate();
	 * 
	 * } catch (Exception ex) { log.error( ex ); if ( ex instanceof
	 * OpenEditException) { throw (OpenEditException)ex; } throw new
	 * OpenEditException(ex); } finally { FileUtils.safeClose(inIn); }
	 * 
	 * return cal;
	 * 
	 * }
	 * 
	 * protected void addEvent(Calendar cal,Element event) throws
	 * OpenEditException {
	 * 
	 * try { Date start = getFormat().parse(event.attributeValue("start")); Date
	 * end = getFormat().parse(event.attributeValue("end"));
	 * 
	 * String title = event.elementText("summary"); String username =
	 * event.elementText("attendee"); cal.addEvent(start,end,title,username); }
	 * catch ( Exception ex ) { throw new OpenEditException(ex); }
	 * 
	 * }
	 * 
	 * public SimpleDateFormat getFormat() { if ( fieldFormat == null) {
	 * fieldFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss"); } return
	 * fieldFormat; } public void setFormat(SimpleDateFormat inFormat) {
	 * fieldFormat = inFormat; }
	 */
}
