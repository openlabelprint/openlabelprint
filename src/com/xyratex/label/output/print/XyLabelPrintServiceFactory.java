//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyLabelPrintServiceFactory.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.output.print;

import java.util.Vector;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.standard.PrinterName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xyratex.label.output.print.zebra.XyZebraPrinting;

/**
 * <p>Provides print service based on string identification supplied by client calling code.</p>
 * <p>Calling code asks for a print service, giving a string id of the printer.</p>
 * <p>This factory provides a corresponding print service object (if the printer service is registered with the factory.)</p>
 * <p>the client calling code can then use the print service to print!</p>
 * 
 * <p>This is a stateless singleton class with stateless methods, therefore all methods are static.
 * This provides the convenience of not needing to instantiate an instance of this class
 * to use its functionality.</p>
 * 
 * <p>Because there is no constructor, we output the version number of the class
 * as part of debug trace on the methods. Though we may choose not to do this on certain
 * methods that are called within a loop and where doing so would cause performance issues
 * due to producing log outout.</p>
 * 
 * @author rdavis
 *
 */
public class XyLabelPrintServiceFactory
{
	/**
	 *  R = Release L = Level G = date U = time
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyLabelPrintServiceFactory.java  %R%.%L%, %G% %U%";
	
	/**
	 * log object used for logging activity in the form of String/character messages for debug, diagnostic and analysis purposes.
	 * The output destination for the log could be console output and/or file(s) depending on the user-definable overall configuration
	 * of logs in the system.
	 */
	private static final Log log = LogFactory.getLog(XyLabelPrintServiceFactory.class);
	
	/**
   * <p>Gets the print service based on string identification supplied by client calling code.</p>
	 * @param printerId - id string of printer
	 * @return a XyLabelPrintService for use in actual printing.
	 */
  public static XyLabelPrintService getPrintService( String printerId )
  {
  	log.trace("sccsid" + "\n" + "getPrintService(" + printerId + ")");
  	
  	// currently only zebra printers
  	//
  	// extend to add new printers here
  	return new XyZebraPrinting( printerId );
  }
  
  /**
   * Get list of print services known to this factory.
   * @return Vector list of printerids as Strings
   */
	public static Vector<String> getPrinters()
	{
  	log.trace("sccsid" + "\n" + "Vector<String> getPrinters()");
		
		String sPrinterName = null;
		
		Vector<String> printers = new Vector<String>();
		
		PrintService[] services = PrintServiceLookup.lookupPrintServices(XyPrinterDriverWrapper.anyDocFlavorWillDo,
				XyPrinterDriverWrapper.anyAttributeSetWillDo);
		
		for (int i = 0; i < services.length; i++)
		{
			PrintServiceAttribute attr = services[i]
			    .getAttribute(PrinterName.class);
			sPrinterName = ((PrinterName) attr).getValue();
			
			printers.add(sPrinterName.toLowerCase());
		}
		
		return printers;
	}
}
