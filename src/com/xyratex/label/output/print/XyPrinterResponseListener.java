//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyPrinterResponseListener.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.output.print;

/**
 * Classes implement this interface if they want to listen for printer response,
 * following a command sent to the printer.
 * 
 * @author rdavis
 *
 */
public interface XyPrinterResponseListener
{
	/**
	 *  R = Release L = Level G = date U = time
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyPrinterResponseListener.java  %R%.%L%, %G% %U%";
}
