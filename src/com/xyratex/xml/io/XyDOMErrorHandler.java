//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyDOMErrorHandler.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.xml.io;

import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xyratex.debug.XyDebug;

/**
  * 
  */
public class XyDOMErrorHandler implements DOMErrorHandler 
{
	/**
	 *  R = Release L = Level G = date U = time
	 *  Values calculated by SCCS when file is checked out and compiled.
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyDOMErrorHandler.java  %R%.%L%, %G% %U%";
	
	private static final Log log = LogFactory.getLog(XyDOMErrorHandler.class);
	
	XyDOMErrorHandler()
	{
		log.trace(sccsid);
	}
	
	/**
	 * Ignore non-fatal errors during DOM parsing of the SVG document
	 * 
	 */
	public boolean handleError(DOMError domError ) 
	{
		String domErrorString = domError.getMessage() 
               + "\ndomError.getLocation()" + domError.getLocation()
		           + "\ndomError.getMessage()" + domError.getMessage()
		           + "\ndomError.getRelatedData()" + domError.getRelatedData()
		           + "\ndomError.getRelatedException()" + domError.getRelatedException()
		           + "\ndomError.getSeverity()" + domError.getSeverity()
		           + "\ndomError.getType()" + domError.getType();

		// determines where we should continue processing the XML Document or not
	  boolean continueProcessing = true; // by default
		
		if ( XyDebug.getWarningMode() == XyDebug.WARNING_ON )
		{
		  log.warn( domErrorString );
		}
		else
		{
			log.trace( domErrorString );
		}
		
		if ( XyDebug.getErrorMode() == XyDebug.ERRORMODE_STRICT )
		{
			continueProcessing = false;
		}
		else
		{
			continueProcessing = true;
		}
		
		return continueProcessing;
	}
}

