//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyLabelTemplateMakerCli.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.apps.cli;

import java.io.File;

import org.w3c.dom.Document;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;

import org.apache.commons.cli.PosixParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xyratex.debug.XyDebug;
import com.xyratex.xml.io.XyDocumentProducer;

import com.xyratex.label.tags.XyLabelTagRegistry;
import com.xyratex.label.template.XyLabelTemplateProducer;



/**
 * <p>The command line tool for making a SVG label template from the tagged drawing exported as SVG from the industrial designers.</p>
 * 
 * <p>The assumption is that the industrial designer (or otherwise) has tagged the outline of the label in the drawing
 * and also the rectangular place holders for the barcodes and also the dummy text for the human readable part</p>
 * 
 *  <p>The class is for end-use command line execution of the tool 
 *  and is not intended to be called by other java code</p>
 *  
 *  @see XyLabelTemplateProducer
 * 
 * @author Rob Davis
 */
public class XyLabelTemplateMakerCli extends XyAbstractCli
{
	/**
	 *  R = Release L = Level G = date U = time
	 *  Values calculated by SCCS when file is checked out and compiled.
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyLabelTemplateMakerCli.java  %R%.%L%, %G% %U%";
	
  public final static String commandName = "maketemplate";
	
	/**
	 * log object used for logging activity in the form of String/character messages for debug, diagnostic and analysis purposes.
	 * The output destination for the log could be console output and/or file(s) depending on the user-definable overall configuration
	 * of logs in the system.
	 */
	private static Log log = LogFactory.getLog(XyLabelTemplateMakerCli.class);
  
  /**
   * The main entry point of the application, called when it is run at the command line.
   *
   * @param  args (input parameter) the argument list passed from the command line
   * 
   * <p>Takes two mandatory arguments:</p>
   * <ul>
   *  <li>-i the input file - the tagged drawing exported as SVG from the industrial designers</li>
   *  <li>-o the output file - the resultant SVG label template - i.e. a label with the barcode fields empty</li>
   * </ul>
   * 
   * <p>This class facilitates:</p>
   * <ul>
   *    <li>run-time integration with labelling existing systems, 
   *    particularly those that are non-java, 3rd-party, commercial or don't build/compile
   *    within the Java environment</li>
   *    <li>demonstration of the SVG Barcode Label project</li>
   *    <li>integration testing of the SVG Barcode label template generation</li>
   * </ul>
   */
  public static void main(String args[])
  {
  	log.trace(           XyAbstractCli.sccsid
        + "\n" + sccsid );
  	
  	String drawingFileInput;
  	String labelTemplateOutput;
  	
  	// the string/char option values are defined once
  	// as a variable and then this is used throughout
  	//
  	// this helps maintenance because there is a single point
  	// where the value is defined, as opposed to
  	// defining an option, e.g. "h" and then checking for
  	// it e.g. if ... "h", thereby logic errors are minimised
  	// also string/char equivalent of options can be changed
  	
    String helpOptionAsText = "h";
    Option helpOption 
      = new Option(helpOptionAsText, "help" );
    
    options.addOption(helpOption);
  	
    String tagsInFileOptionAsText = "t";
    Option listTagsInFileOption
      = new Option(tagsInFileOptionAsText, "list tags in file" );  
    listTagsInFileOption.setArgs(1);
    options.addOption(listTagsInFileOption);
    
    String allKnownTagsOptionAsText = "a";
    Option getListOfAllKnownTagsOption 
      = new Option(allKnownTagsOptionAsText, "get list of all known tags" );
    options.addOption(getListOfAllKnownTagsOption);
    
    String inputTemplateOptionAsText = "i";
    Option     drawingFileInputOption
      = new Option(inputTemplateOptionAsText, "input SVG drawing file" );  
    drawingFileInputOption.setArgs(1);
    options.addOption(drawingFileInputOption);
    
    String outputPopulatedLabelOptionAsText = "o";
    Option labelTemplateOutputOption
      = new Option(outputPopulatedLabelOptionAsText, "output SVG label template" );  
    labelTemplateOutputOption.setArgs(1);
    options.addOption(labelTemplateOutputOption);
    
    // create the parser
    CommandLineParser parser = new PosixParser();
    try {
        // parse the command line arguments
        CommandLine line = parser.parse( options, args );
        
      	if ( line.hasOption( errorsOnOptionAsText ) )
      	{
      		XyDebug.setErrorMode( XyDebug.ERRORMODE_STRICT );
      	}
      	
      	if ( line.hasOption( warningsOnOptionAsText ) )
      	{
      		XyDebug.setWarningMode( XyDebug.WARNING_ON );
      	}
        
        
        if (    line.hasOption( inputTemplateOptionAsText) 
        		 && line.hasOption( outputPopulatedLabelOptionAsText)
        	 )
        {
        	drawingFileInput = line.getOptionValue( inputTemplateOptionAsText );
        	labelTemplateOutput = line.getOptionValue( outputPopulatedLabelOptionAsText );
        	
        	XyLabelTemplateProducer.generateLabelTemplate(drawingFileInput, labelTemplateOutput );
        }
        
        if ( line.hasOption( allKnownTagsOptionAsText ) )
        {
        	System.out.println( XyLabelTagRegistry.getMasterListOfTags() );
        }
        
        if ( line.hasOption( tagsInFileOptionAsText ) )
        {
      		Document doc = XyDocumentProducer.getDocument( "svg", new File( line.getOptionValue( tagsInFileOptionAsText ) ) );
        	
        	System.out.println( XyLabelTagRegistry.listTagsInDrawingAsString(doc) );
        }
        
        if ( line.hasOption( helpOptionAsText))
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
