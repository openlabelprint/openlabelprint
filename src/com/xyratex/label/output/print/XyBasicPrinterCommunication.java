//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyBasicPrinterCommunication.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.output.print;

import javax.print.PrintException;

/**
 * <p>Interface used to communicate with a particular attached printer.
 * To send command bytes to the attached printer.</p>
 * 
 * <p>The clients that use this interface are representations of printer models. 
 * They translate the graphics/text from their client into commands specific to that printer model
 * for printing those graphics.
 * They then pass these commands as an array of bytes, via this interface, to an implementation 
 * that can send the bytes to the printer.</p>
 * 
 * <p>This interface does not know about printer specifics, e.g. commands, rather it provides
 * a simple *basic* interface for sending bytes to an attached printer.</p>
 * 
 * <p>Example implementations of this interface might be:</p>
 * <ul>
 *  <li>A wrapper class that uses Java's standard print service for communicating 
 *   to the printer via its associated Windows printer driver.</li>
 * </li>A class that communicates directly with the printer via RS232, USB or other
 *  communications port</li>
 * </ul>
 * 
 * <p>This interface therefore ensures that clients can work with any implementation of this
 * interface. The interface abstracts and hides away the specifics of hardware/operating system
 * dependent implementation of communicating with the printer, but not the actual commands that
 * the printer itself understands, just basic the means of getting the bytes to the printer.</p>
 * 
 * <p>Software that provides communication with an attached printer should implement this
 * interface and register themselves with the XyBasicPrinterCommunicationFactory.</p>
 * 
 * @see com.xyratex.label.output.print.XyBasicPrinterCommunicationFactory
 * 
 * @author rdavis
 */
public interface XyBasicPrinterCommunication
{	
	/**
	 *  R = Release L = Level G = date U = time
	 *  Values calculated by SCCS when file is checked out and compiled.
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyBasicPrinterCommunication.java  %R%.%L%, %G% %U%";
	
	/**
	 * Send a stream of bytes to the printer.
	 */
	public void sendToPrinter( byte by[] ) throws PrintException;
	
	/**
	 * Get the full printer name.
	 * 
	 * @return the printer name as a string
	 */
	public String getPrinterName();
}
