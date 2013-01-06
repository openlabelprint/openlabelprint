//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyPrinterOffsetManager.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.output.print.offset;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

/**
 * <p>Singleton class that manages the printer offsetting to compensate for
 * drift, due to wear-and-tear of the printer alignment mechanics.</p>
 * 
 * <p>Manages offset values for each printer attached to the host PC.</p>
 * 
 * <p>In future this will handle offsetting in millimetres better than it does now.
 * The implementation is rough and incomplete.</p>
 * 
 * @author rdavis
 *
 */
public class XyPrinterOffsetManager
{
	/**
	 *  R = Release L = Level G = date U = time
	 *  Values calculated by SCCS when file is checked out and compiled.
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyPrinterOffsetManager.java  %R%.%L%, %G% %U%";
	
  /**
   * <p>Defined value for the default top left origin of where the printing starts from.</p>
   * 
   * <p>Public so that we can tell people what the default is.</p>
   */
  public static final String defaultOffset = "0,0"; 
	
  /**
   * <p>The path where this program is executing from, and where it will put subfolders
   * containing offset values.
   * </p>
   */
	private static String openLabelPrintPath = System.getProperty("user.dir") + "\\" + "labelprintsettings"; // current working directory where OLP is executing from
	
	/**
	 * The name of the file to store offsets for a particular printer.
	 */
	public static final String printerOffsetFilename = "printerOffsetFile.txt"; 
	

	/**
	 * Save the printer offset
	 * 
	 * @param offset (input) the offset
	 * @param printerId (input) the printer to save the offset against
	 * @return
	 */
  public static String saveOffsetForPrinter( String offset, String printerId )
  {
  	String path = createPrinterOffsetFileIfAbsent( printerId );
  	
  	try
  	{
      FileUtils.writeStringToFile( new File(path), offset );
  	}
  	catch (Exception ex)
  	{
      ex.printStackTrace();
    }
  	
  	return path;
  }
  
  /**
   * Creates a printer offset file if there isn't one
   * 
   * @param aPrinterId (input) printer to create the offset file for
   * @return
   */
  private static String createPrinterOffsetFileIfAbsent( String aPrinterId )
  {
  	String printerId = null;
  	if ( aPrinterId == null || aPrinterId == "" )
  	{
  		printerId = "unspecified";
  	}
  	else
  	{
  		printerId = aPrinterId;
  	}
  	
  	String path = openLabelPrintPath +  "\\" + printerId + "\\" + printerOffsetFilename;
  	
  	try
  	{
    	System.out.println( "createPrinterOffsetFileIfAbsent" + path);
  	  File printerFolder = new File( openLabelPrintPath +  "\\" + printerId );
  	  if ( !printerFolder.exists() )
  	  {
  		  FileUtils.forceMkdir(printerFolder);
    	  File printerOffsetFile = new File( path );
        FileUtils.writeStringToFile( printerOffsetFile, "0,0" );
  	  }
  	}
    catch (Exception ex) 
  	{
      ex.printStackTrace();
  	}
    
    return path;
  }
  
  /**
   * Load the offset file for a particular printer
   * @param printerId (input) the printer that the offset is associated with
   * @return
   */
  public static String loadPrinterOffset( String printerId )
  {
  	// if the file doesnt exist
  	String path = createPrinterOffsetFileIfAbsent( printerId );
  	System.out.println( "loadPrinterOffset " + path);
  	
    File printerOffsetFile = new File( path );
    
    String offsetsAsString = null;
  	try
  	{
      offsetsAsString = FileUtils.readFileToString(printerOffsetFile);
    } 
  	catch (Exception ex)
  	{
      ex.printStackTrace();
    }
    
    return offsetsAsString;
  }  
  
  /**
   * Add a given offset value to that stored for a particular printer, to give an overall offset value.
   * 
   * @param anOffset (input) the given offset
   * @param printerId (input) the printer that an offset is already stored against
   * @return
   */
  public static String getCombinedValueWithPrinterOffset( String anOffset, String printerId )
  {
  	String offset = "";
  	if ( anOffset == null || anOffset == "" )
  	{
  		offset = "0,0";
  	}
  	else
  	{
  		offset = anOffset;
  	}
  	
  	String printerOffset = loadPrinterOffset( printerId );
  	
  	// supplied offset
		String[] coords = StringUtils.split(offset, "," );
		
		String xcoord = null;
		String ycoord = null;
		
		// if co-ords in format of x,y not specified then we'll use 0,0
		if ( coords.length == 2 )
		{
		  xcoord = coords[0];
		  ycoord = coords[1];
		}
		else
		{
			xcoord = "0";
		  ycoord = "0";
		}
		// printer offset
		String[] printerOffsetCoords = StringUtils.split(printerOffset, "," );
		
		String xprinterOffsetCoords = printerOffsetCoords[0];
		String yprinterOffsetCoords = printerOffsetCoords[1];
		
    String combinedOffset 
      = combineOffsetValues( xcoord, xprinterOffsetCoords )
      + ","
      + combineOffsetValues( ycoord, yprinterOffsetCoords );
    
    return combinedOffset;
  }
  
  /**
   * Combine offset values.
   * e.g. offset 1 = 5,5 and offset 2 = 10,10, the combined value will be 15,15
   * 
   * @param offset1
   * @param offset2
   * @return
   */
  public static String combineOffsetValues( String offset1, String offset2 )
  {
	  offset1 = offset1.replace("+", "");
	  offset2 = offset2.replace("+", "");
	  
	  int offset1AsInt = Integer.parseInt(offset1);
	  int offset2AsInt = Integer.parseInt(offset2);
	  
	  int sum = offset1AsInt + offset2AsInt;
	  
	  return "" + sum;
  }
  
  // get pixel value for offset given dpi or dpmm
  
}
