//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyDebug.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.debug;

import org.apache.commons.logging.Log;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * <P>Toolkit for handling debug and logging output, depending on mode selected by application and, ultimately, the user.</p>
 *
 * <p>Mode selected by application can be stored here to enable common system-wide behaviour when handling debug and logging.</p>
 */
public class XyDebug
{
	/**
	 *  R = Release L = Level G = date U = time
	 *  Values calculated by SCCS when file is checked out and compiled.
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyDebug.java  %R%.%L%, %G% %U%";
	
	/**
	 * Represents the situation where the error mode hasn't been set up yet.
	 * Therefore enables situations where a default error mode is not possible.
	 */
  public static final int ERRORMODE_UNSET = 0;
  
  /**
   * Strict error reporting mode. Exceptions will be logged and the application can choose
   * to halt in this mode if an exception is encountered.
   */
  public static final int ERRORMODE_STRICT = 1;
  
  /**
   * Relaxed error reporting mode. Exceptions can be logged and application can choose not
   * to halt if exceptions encountered.
   */
  public static final int ERRORMODE_RELAXED = 2;
  
  /**
   * Exceptions will not be logged.
   */
  public static final int WARNING_OFF = 3;
  
  /**
   * Exceptions will be logged.
   */
  public static final int WARNING_ON = 4;
  
  /**
   * Default value for error mode
   */
  private static int errorMode = XyDebug.ERRORMODE_RELAXED;
  
  /**
   * Default value for warning mode
   */
	private static int warningMode = XyDebug.WARNING_OFF;
  
	/**
	 * Handles exceptions depending on mode selected by application.
	 */
	public static void advise( final Exception exception, final Log log ) throws Exception
	{
		advise( errorMode, warningMode, exception, log );
	}
	
	/**
	 * Handles exceptions depending on mode selected by application.
	 * 
   * probably want to use advise( Exception cause, Log log )
   * most of the time, as the settings are looked after here rather than in client calling classes
   * - extra maintenance overhead.
   *
   * However, this method is provided as public in case the developer wants error reporting
   * to have different modes for different parts of the system. The modes will have to be
   * maintained by the client calling code
   */
  public static void advise( final int anErrorMode, final int aWarningMode, final Exception exception, final Log log ) throws Exception
  {  	
  	if ( anErrorMode == ERRORMODE_STRICT )
  	{
  		throw exception;
  	}
  	else
  	{
  		if ( aWarningMode == WARNING_ON)
  		{
  			log.warn( exception.getMessage() );
  		}
  	}
  }
  
  /** 
   * Outputs debug and stack information associated with an exception thrown
   */
  public static void debugException( Exception cause, Log log )
  {
  	debugException( cause, errorMode,  warningMode, log );
  }
  
  /** 
   * Outputs debug and stack information associated with an exception thrown
   * 
   * probably want to use debugException( Exception cause, Log log )
   * most of the time, as the settings are looked after here rather than in client calling classes
   * - extra maintenance overhead.
   *
   * However, this method is provided as public in case the developer wants error reporting
   * to have different modes for different parts of the system. The modes will have to be
   * maintained by the client calling code
   */
  public static void debugException( final Exception cause, final int anErrorMode, final int aWarningMode, final Log log )
  {
  	log.trace(sccsid);
  	
  	if ( anErrorMode == XyDebug.ERRORMODE_STRICT
		    || aWarningMode == XyDebug.WARNING_ON)
		{
			System.out.println(cause.getMessage());

			log.trace("trace log: cause.getMessage() = " + cause.getMessage());

			log.trace(cause.toString());

			log.trace("exception");

			log.trace("\n\n");
			log.trace(cause.getCause());
			log.trace("\n\n");

			StackTraceElement elements[] = cause.getStackTrace();

			for (int i = 0, n = elements.length; i < n; i++)
			{
				log.error(elements[i].getFileName() + ":"
				    + elements[i].getLineNumber() + ">> "
				    + elements[i].getMethodName() + "()");
			}
  	}
  }

  /**
   * Get the error mode currently in operation
   * @return the error mode
   */
  public static int getErrorMode()
  {
  	return errorMode;
  }
  
  /**
   * Get the warning mode currently in operation
   * @return the warning mode
   */
  public static int getWarningMode()
  { 
  	return warningMode;
  }

  /**
   * Change the error mode currently in operation.
   */
	public static void setErrorMode( final int anErrorMode )
	{
		errorMode = anErrorMode;
	}
	
  /**
   * Change the warning mode currently in operation.
   */
	public static void setWarningMode( final int aWarningMode )
	{
		warningMode = aWarningMode;
	}
	
  /**
   * Turn off the logging
   */
	public static void disableLogging()
	{
		Logger rootLogger = LogManager.getRootLogger(); //To get the Root Logger
		Level level = Level.OFF;
		rootLogger.setLevel(level);
	}
}
