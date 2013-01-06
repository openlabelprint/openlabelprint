//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyBasicPrinterCommunicationFactory.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.output.print;

/**
 * <p>Given a String identifier of a printer attached to a host computer,
 * this provides a basic communication object used to send bytes to that printer.</p> 
 * 
 * @see com.xyratex.label.output.print.XyBasicPrinterCommunication
 * 
 * @author rdavis
 */
public class XyBasicPrinterCommunicationFactory
{
	/**
	 *  R = Release L = Level G = date U = time
	 *  Values calculated by SCCS when file is checked out and compiled.
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyBasicPrinterCommunicationFactory.java  %R%.%L%, %G% %U%";
	
	/**
	 * <p>Get a XyBasicPrinterCommunication object used for sending command bytes to the attached printer,
	 * specified by the known printer identity as a String.</p>
	 * 
	 * @param printerId - printer id as String 
	 * @return XyBasicPrinterCommunication object
	 */
  public static XyBasicPrinterCommunication getPrinterCommunication( String printerId )
  {
  	return new XyPrinterDriverWrapper( printerId );
  }
}
