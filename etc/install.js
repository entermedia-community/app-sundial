importPackage( Packages.com.openedit.util );
importPackage( Packages.java.util );
importPackage( Packages.java.lang );
importPackage( Packages.com.openedit.modules.update );


var war = "http://dev.entermediasoftware.com/jenkins/job/app-sundial/lastSuccessfulBuild/artifact/deploy/ROOT.war";

var root = moduleManager.getBean("root").getAbsolutePath();
var web = root + "/WEB-INF";
var tmp = web + "/tmp";

log.add("1. GET THE LATEST WAR FILE");
var downloader = new Downloader();
downloader.download( war, tmp + "/ROOT.war");

log.add("2. UNZIP WAR FILE");
var unziper = new ZipUtil();
unziper.unzip(  tmp + "/ROOT.war",  tmp );

log.add("3. REPLACE LIBS");
var files = new FileUtils();
files.deleteMatch( web + "/lib/openedit-intranet*.jar");
files.deleteMatch( web + "/lib/openedit-events*.jar");
files.deleteMatch( web + "/lib/ical*.jar");

files.copyFileByMatch( tmp + "/WEB-INF/lib/openedit-events*.jar", web + "/lib/");
files.copyFileByMatch( tmp + "/WEB-INF/lib/ical*.jar", web + "/lib/");

log.add("4. UPGRADE BASE DIR");
files.deleteAll( root + "/WEB-INF/base/events");
files.copyFiles( tmp + "/WEB-INF/base/events", root + "/WEB-INF/base/events");

log.add("5. CLEAN UP");
files.deleteAll(tmp);

log.add("6. EVENTS INSTALL COMPLETED");