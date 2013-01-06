//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyPrinterDriverWrapper.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.output.print;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.standard.PrinterName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Provides communication to attached printers that are communicated with via printer drivers 
 * installed on the host Operating System.</p>
 * 
 * <p>This class does not process any graphics from its client users. Simply, it serves
 * as a means of communicating with attached printers via their drivers installed on
 * the host PC operating system. For example Windows printers.</p>
 * 
 * @author rdavis
 *
 */
public class XyPrinterDriverWrapper implements XyBasicPrinterCommunication
{
	/**
	 *  R = Release L = Level G = date U = time
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyPrinterDriverWrapper.java  %R%.%L%, %G% %U%";
	
	/**
	 * log object used for logging activity in the form of String/character messages for debug, diagnostic and analysis purposes.
	 * The output destination for the log could be console output and/or file(s) depending on the user-definable overall configuration
	 * of logs in the system.
	 */
	private static Log log = LogFactory.getLog(XyPrinterDriverWrapper.class);
	
	/**
	 * Name of the printer
	 */
	private String printerName = null;
		
  /**
   * When searching for a print service, DocFlavor is part of the search criteria. 
   * For some searches we don't care about the DocFlavor, and use this defined value to represent that 'don't care'
   */
	public final static DocFlavor anyDocFlavorWillDo = null;
	
  /**
   * When searching for a print service, AttributeSet is part of the search criteria. 
   * For some searches we don't care about the AttributeSet, and use this defined value to represent that 'don't care'
   */
	public final static AttributeSet anyAttributeSetWillDo = null;
	
	/**
	 * The PrintService provided by Java
	 */
	private PrintService printService = null;
	
	/**
	 * Constructor. Uses aPrinterName to find a Java PrintService that represents the printer with this name.
	 * 
	 * @param aPrinterName
	 */
	public XyPrinterDriverWrapper( final String aPrinterName )
	{
		log.trace( XyBasicPrinterCommunication.sccsid
				       + "\n" + sccsid );
		
		String sPrinterName = null;
		
		// find printers available
		// explain the parameters - we'll have any sort of printer
		// TODO: ideally we'd change this so that we can specify what kind of printer we want
    // e.g. if we want label printers then we wouldnt want to list network laser printers

		PrintService[] services = PrintServiceLookup.lookupPrintServices(anyDocFlavorWillDo,
				anyAttributeSetWillDo);
		for (int i = 0; i < services.length; i++)
		{
			PrintServiceAttribute attr = services[i]
			    .getAttribute(PrinterName.class);
			sPrinterName = ((PrinterName) attr).getValue();
			
			//old comparison logic: if (sPrinterName.toLowerCase().indexOf(aPrinterName) >= 0)
			
			if ( sPrinterName.toLowerCase().contains(aPrinterName.toLowerCase()) )
			{
				printService = services[i];
				break;
			}
		}
		
		if (printService == null)
		{
			// TODO - raise exception?
			System.out.println("Printer not found.");
			return;
		}
		else
		{
			printerName = printService.getName();
		}
		
		System.out.println("Found printer: " + sPrinterName);
		
	}
	
	/**
	 * Get the full printer name
	 */
	public String getPrinterName()
	{
		return printerName;
	}
	
	/**
	 * Send an array of bytes to the printer
	 */
  public void sendToPrinter( final byte by[] ) throws PrintException
  {
			DocPrintJob job = printService.createPrintJob();
			
			DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
			// MIME type = "application/octet-stream",
			// print data representation class name = "[B" (byte array).
			Doc doc = new SimpleDoc(by, flavor, null);
			job.print(doc, null);
  }
}
