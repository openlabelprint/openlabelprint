package com.xyratex.apps.daemon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class XyFileSystemWatcherThread extends Thread 
{
	/** The FileSave interface is implemented by the main class. */
	//protected FileSaver model;
	/** How long to sleep between tries */
	public static final int MINUTES = 5;
	private static final int SECONDS = MINUTES * 60;
	
	private static Log log = LogFactory.getLog(XyFileSystemWatcherThread.class);
	
	public XyFileSystemWatcherThread() 
	{
	  super("XyFileSystemWatcher Thread");
	  // setDaemon(true); // so we don't keep the main app alive
	  //model = m;
	}
	
	public void run()
	{
		while (true) 
		{ // entire run method runs forever.
		  try 
		  {
		    sleep(SECONDS*1000);
		  } 
		  catch (InterruptedException e)
		  {
		    // do nothing with it
		  }
		
		  log.trace("activity");
		  //if (model.wantAutoSave( ) && model.hasUnsavedChanges( ))
		  //model.saveFile(null);
		}
	}
}