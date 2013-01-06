//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyLabelBitmapMakerCli.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.apps.cli;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.PosixParser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xyratex.debug.XyDebug;
import com.xyratex.label.output.XyLabelBitmapProducer;

/**
 * <p>The command line tool for generating the PNG from the populated label.</p>
 * 
 * <p>This class facilitates:</p>
 * <ul>
 *    <li>run-time integration with labelling existing systems, 
 *    particularly those that are non-java, 3rd-party, commercial or don't build
 *    within the Java environment</li>
 *    <li>demonstration of the SVG Barcode Label project</li>
 *    <li>integration testing of the SVG Barcode label rasterization</li>
 *  </ul>
 *  
 *  <p>The class is for end-use command line execution of the tool 
 *  and is not intended to be called by other java code</p>
 * 
 * @author Rob Davis
 */
final public class XyLabelBitmapMakerCli extends XyAbstractCli
{
	/**
	 *  R = Release L = Level G = date U = time
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyLabelBitmapMakerCli.java  %R%.%L%, %G% %U%";
	
  /**
   * holds the filename of the application - used as the command when running it from the command line 
   */
  public final static String commandName = "makebitmap";
	
	/**
	 * log object used for logging activity in the form of String/character messages for debug, diagnostic and analysis purposes.
	 * The output destination for the log could be console output and/or file(s) depending on the user-definable overall configuration
	 * of logs in the system.
	 */
	private static Log log = LogFactory.getLog(XyLabelBitmapMakerCli.class);
  
  /**
   * The main entry point of the application, called when it is run at the command line.
   *
   * @param args (input parameter)  the argument list passed from the command line
   *   <p>Takes three mandatory arguments:</p>
   * <ul>
   *  <li> -i the input file - the populated label in SVG format, a .svg file</li>
   *  <li> -o the output file - the label as a PNG for sending to the printer, a .png file</li> 
   *  <li> -d the dots per inch (dpi) of the the PNG, e.g. 203, 300, 600 etc.</li>
   * </ul>
   * 
   * <p>Optional argument</p>
   * <ul>
   *  <li>-h help</li>
   * </ul>
   */
  public static void main(String args[])
  {
  	log.trace(           XyAbstractCli.sccsid
  			        + "\n" + sccsid );
  	
  	String labelSVGFileInput;
  	String labelBitmapOutput;
  	String labelDpi;
  	
		String helpOptionAsText = "h";
		Option helpOption = new Option(helpOptionAsText, "help");
		options.addOption(helpOption);
    
		String inputPopulatedLabelOptionAsText = "i";
		Option labelSVGFileInputOption = new Option(
		    inputPopulatedLabelOptionAsText, "Populated SVG label input");
		labelSVGFileInputOption.setArgs(1);
		options.addOption(labelSVGFileInputOption);
        
		String labelFileOutputOptionAsText = "o";
		Option labelFileOutputOption = new Option(
		    labelFileOutputOptionAsText,
		    "Populated label as a PNG bitmap file");
		labelFileOutputOption.setArgs(1);
		options.addOption(labelFileOutputOption);
    
		String dotsPerInchOptionAsText = "d";
		Option dpiOption = new Option(dotsPerInchOptionAsText, "dots per inch");
		dpiOption.setArgs(1);
		options.addOption(dpiOption);
		
    // create the parser
    CommandLineParser parser = new PosixParser();
    try {
        // parse the command line arguments
        CommandLine line = parser.parse( options, args );
        
        if (    line.hasOption(inputPopulatedLabelOptionAsText) 
        		 && line.hasOption(labelFileOutputOptionAsText)
        		 && line.hasOption(dotsPerInchOptionAsText)
        	 )
        {
        	processDebugOptions(line);
        	
        	labelSVGFileInput = line.getOptionValue(inputPopulatedLabelOptionAsText);
        	labelBitmapOutput = line.getOptionValue(labelFileOutputOptionAsText);
        	labelDpi = line.getOptionValue(dotsPerInchOptionAsText);
        	
        	XyLabelBitmapProducer.generateBitmap( new File(labelSVGFileInput), new File(labelBitmapOutput), labelDpi );
        }
        
        if ( line.hasOption(helpOptionAsText))
        {
          // automatically generate the help statement
        	HelpFormatter formatter = new HelpFormatter();
        	formatter.printHelp( commandName, options );
        }
    }
    catch (Exception cause)
		{
    	XyDebug.debugException( cause, log );
		}
  }
}
