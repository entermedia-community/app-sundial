/*
 * Created on Jul 20, 2006
 */
package com.openedit.events;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.openedit.data.FileSearchQuery;
import org.openedit.data.FileSearcher;
import org.openedit.data.SearcherManager;
import org.openedit.repository.filesystem.StringItem;

import com.openedit.OpenEditException;
import com.openedit.hittracker.HitTracker;
import com.openedit.page.Page;
import com.openedit.page.manage.PageManager;
import com.openedit.users.User;
import com.openedit.util.FileUtils;
import com.openedit.util.PathUtilities;
import com.openedit.util.XmlUtil;

public class UserCalendarArchive
{
	protected PageManager fieldPageManager;
	protected File fieldWebRoot;
	protected SearcherManager fieldSearcherManager;
	
	
	public Map getCalendarMap() {
		if (fieldCalendarMap == null) {
			fieldCalendarMap = new HashMap();
			
		}

		return fieldCalendarMap;
	}
	public void setCalendarMap(Map inCalendarMap) {
		fieldCalendarMap = inCalendarMap;
	}

	protected Map fieldCalendarMap;

	

	public SearcherManager getSearcherManager() {
		return fieldSearcherManager;
	}

	public void setSearcherManager(SearcherManager inSearcherManager) {
		fieldSearcherManager = inSearcherManager;
	}

	protected String[] fieldColors = new String[] { "red","green","yellow","brown","orange","pink","blue","black"};

	
	ICalCalendarBuilder builder = new ICalCalendarBuilder();
	ICalCalendarSaver saver = new ICalCalendarSaver();
	
	public UserCalendar getUserCalendar(String  inEventsId, boolean inDraftMode) throws Exception
	{
		//TODO: User default if there is no username
		String path = "/" + inEventsId + "/data/*.ics";
		
		UserCalendar cal = loadCalendar(inEventsId, null, inDraftMode);			
		//read in the shared and custom calendars
		Page catalogs = getPageManager().getPage("/" + inEventsId +"/categories.xml");
		Element root = new XmlUtil().getXml(catalogs.getReader(), catalogs.getCharacterEncoding());
		cal.setCategories(root);
		
		Page locations = getPageManager().getPage("/"  + inEventsId +"/locations.xml");
		Element lroot = new XmlUtil().getXml(locations.getReader(), locations.getCharacterEncoding());
		cal.setLocations(lroot);

		return cal;
	}

	public UserCalendar loadCalendar(String inEventsId, String inPath, boolean inDraftMode)
	{
		UserCalendar cal = new UserCalendar();
		cal.setEventsId(inEventsId);
		
		FileSearcher searcher = (FileSearcher) getSearcherManager().getSearcher(inEventsId, "file");
		if(inPath != null)
		{
			Page page = getPageManager().getPage(inPath);
			addCalendar(page, cal);
		}
		else
		{
			FileSearchQuery query = (FileSearchQuery) searcher.createSearchQuery();
			query.addMatches("name", "*.ics");
			query.setRootFolder("/" + inEventsId + "/data/");
			//boolean draftmode = inUser.hasProperty("oe.edit.draftmode" );
			query.setRecursive(true);
			HitTracker ics = searcher.search(query);
			if( inDraftMode )
			{
				for (int i = 0; i < ics.size(); i++)
				{
					Page page = (Page) ics.get(i);
					if( page.isDraft())
					{				
						addCalendar(page,cal);
					}
				}
			}
			//now take in any non draft files that we do not already have loaded as a draft
			for (int i = 0; i < ics.size(); i++)
			{
				Page page = (Page) ics.get(i);
				if( !page.isDraft())
				{
					String draftname = PathUtilities.createDraftPath( page.getName() );
					Calendar existing = cal.getMixedInCalendar(draftname);
					if( existing == null)
					{
						addCalendar(page,cal);
					}
				}
			}
		}
		return cal;
	}

	public void addCalendar(Page page,UserCalendar cal) throws OpenEditException
	{
		
		String calid = PathUtilities.extractPageName(page.getName());
		
		Calendar mixed =(Calendar) getCalendarMap().get(calid);
		if(mixed == null){
			if(page.exists()){
				mixed = builder.build(page.getReader());
			} else{
				mixed = new ICalCalendarBuilder().build();
				
				mixed.setPage(page);
				saveCalendar(mixed, null);
			}
			getCalendarMap().put(calid, mixed);
		}
		mixed.setId(calid );
		mixed.setPage(page);
		
		String color = page.get("color");
		if( color == null )
		{
			int size = cal.getAllCalendars().size();
			if( size >= fieldColors.length)
			{
				size = 0;
			}
			color = fieldColors[size];
		}
		mixed.setColor( color );
		cal.addMixedInCalendar(mixed);
	}

	protected Page getRealPage(Page inPage, boolean inUseDrafts) throws OpenEditException
	{
		if( inUseDrafts )
		{
			String draftPath = PathUtilities.createDraftPath(inPage.getPath());
			Page draft = getPageManager().getPage(draftPath);
			if( draft.exists() )
			{
				return draft;
			}
		}
		return inPage;
	}

	public PageManager getPageManager()
	{
		return fieldPageManager;
	}

	public void setPageManager(PageManager inPageManager)
	{
		fieldPageManager = inPageManager;
	}

	public File getWebRoot()
	{
		return fieldWebRoot;
	}

	public void setWebRoot(File inWebRoot)
	{
		fieldWebRoot = inWebRoot;
	}
	public void saveCalendar(Calendar inCal, User inUser) throws OpenEditException
	{
		Writer file  = null;
		try
		{
			//TODO: Use lock
			Page done = inCal.getPage();
			
			if( inUser != null)
			{
				done.getContentItem().setAuthor(inUser.getUserName());
			}
			//TODO: Save to another file in case of problems
			OutputStream out = getPageManager().saveToStream(done);
			file = new BufferedWriter(new OutputStreamWriter(out));
			saver.saveCalendar(inCal, file);
			getCalendarMap().put(inCal.getId(), inCal);
		}
		catch ( Exception ex)
		{
			throw new OpenEditException(ex);
		}
		finally
		{
			FileUtils.safeClose(file);
		}
	}
	
	public void deleteCalendar(Calendar inCal) throws OpenEditException
	{
		Page page = inCal.getPage();
		getPageManager().removePage(page);
	}

	public void saveCategories(UserCalendar inCal, User inUser) throws OpenEditException
	{
		
		Page catalogs = getPageManager().getPage(inCal.getEventsId() + "/events/categories.xml");
		StringWriter out = new StringWriter();
		new XmlUtil().saveXml(inCal.getCategories(), out, catalogs.getCharacterEncoding());
		
		StringItem item = new StringItem(catalogs.getPath(), out.toString(),catalogs.getCharacterEncoding());
		item.setAuthor(inUser.getUserName());
		catalogs.setContentItem(item);
		getPageManager().putPage(catalogs);
	}

	public void saveLocations(UserCalendar inCal, User inUser) throws OpenEditException
	{
		Page locations = getPageManager().getPage(inCal.getEventsId() + "/events/locations.xml");
		StringWriter out = new StringWriter();
		new XmlUtil().saveXml(inCal.getLocations(), out, locations.getCharacterEncoding());
		
		StringItem item = new StringItem(locations.getPath(), out.toString(),locations.getCharacterEncoding());
		item.setAuthor(inUser.getUserName());
		locations.setContentItem(item);
		getPageManager().putPage(locations);
	}

	public List listDataDirs(String eventsid)
	{
		String path = eventsid + "/data/";

		File dirs = new File( getWebRoot(), path);
		List children = new ArrayList();
		
		File[] found = dirs.listFiles();
		if( found != null)
		{
			for (int i = 0; i < found.length; i++)
			{
				File file = found[i];
				if( file.isDirectory() && !file.getName().equals("CVS") )
				{
					children.add(file.getName());
				}
			}
		}
		return children;
	}

	public void push(User inUser, Page inSource, String inDir) throws OpenEditException
	{
		String path = "/events/data/" + inDir  + "/" + inUser.getUserName() + "_" +  inSource.getName();
		
		String draft = PathUtilities.createDraftPath(path);
		Page destination = getPageManager().getPage(draft);
		
		destination.getContentItem().setActualPath(inUser.getUserName());
		
		getPageManager().copyPage(inSource, destination);
		
	}
	
}