//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyLabelProducerCli.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.apps.cli;

import java.util.Vector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.w3c.dom.Document;

import com.xyratex.debug.XyDebug;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.PosixParser;

import org.apache.commons.io.FileUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xyratex.xml.io.XyDocumentProducer;
import com.xyratex.xml.io.XyXMLDOMDocumentToStringService;

import com.xyratex.label.apps.cli.exception.XyExceptionCliOutputParamError;
import com.xyratex.label.config.XyCharacterEncoding;
import com.xyratex.label.output.XyLabelBitmapProducer;
import com.xyratex.label.output.print.XyLabelPrintService;
import com.xyratex.label.output.print.XyLabelPrintServiceFactory;
import com.xyratex.label.output.print.offset.XyPrinterOffsetManager;
import com.xyratex.label.population.XyLabelPopulator;
import com.xyratex.label.tags.XyLabelTagRegistry;




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
final public class XyLabelProducerCli extends XyAbstractCli
{
	/**
	 *  R = Release L = Level G = date U = time
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyLabelProducerCli.java  %R%.%L%, %G% %U%";

	/**
	 * log object used for logging activity in the form of String/character messages for debug, diagnostic and analysis purposes.
	 * The output destination for the log could be console output and/or file(s) depending on the user-definable overall configuration
	 * of logs in the system.
	 */
	private static final Log log = LogFactory.getLog(XyLabelProducerCli.class);
	
  /**
   * holds the filename of the application - used as the command when running it from the command line 
   */
  public final static String commandName = "producelabel";
	
  /**
   * The main entry point of the application, called when it is run at the command line.
   *
   * @param args (input parameter)  the argument list passed from the command line
   * <p>Arguments:</p>
   * <ul>
   *  <li> -i the input file - the populated label in SVG format, a .svg file</li>
   *  <li> -o the output file - the label as a PNG for sending to the printer, a .png file</li> 
   *  <li> -d the dots per inch (dpi) of the the PNG, e.g. 203, 300, 600 etc.</li>
   *  <li> -x the PopulatorML XML file that contains the complete set of field values to populate the label with</li>
   *  <li> -p print with the specified printer 
   * </ul>
   * 
   * <p>The arguments must be one of the following combinations:</p>
   * <ul>
   *  <li> -i -x -d -o:
   *   generate .png bitmap or .xml (o)utput file
   *   at given (d)pi 
	 *	 using a given template xml(i)nput file 
	 *   and populate (x)ml file for populating the template with barcodes and numbers 
	 *  </li>
	 *  
	 *  <li>-i -x -d -p:
	 *  generate bitmap 
	 *  and (p)rint with specified printer 
	 *	at given (d)pi given 
	 *	using a given template (i)nput file
	 *	and populate (x)ml file for populating the template with barcodes and numbers 
   *  </li>
   *
   *  <li>-i -x -d -p -o: 
	 *  generate .png bitmap or .xml (o)utput file (for debugging)
	 *  and (p)rint with specified printer 
	 *	at given (d)pi given 
	 *	using a given template (i)nput file
	 *	and populate (x)ml file for populating the template with barcodes and numbers 
   *  </li>  
   *  
   * </ul>
   */
  public static void main(String args[])
  {
  	log.trace(           XyAbstractCli.sccsid
        + "\n" + sccsid );
  	
		String helpOptionAsText = "h";
		Option helpOption = new Option(helpOptionAsText, "help");
		options.addOption(helpOption);

		String inputPopulatedLabelOptionAsText = "i";
		Option labelSVGFileInputOption = new Option(
		    inputPopulatedLabelOptionAsText, "Populated SVG label input");
		labelSVGFileInputOption.setArgs(1);
		options.addOption(labelSVGFileInputOption);

		String inputPopulatorMLOptionAsText = "x";
		Option labelPopulatorXmlFileOption = new Option(
		    inputPopulatorMLOptionAsText, "Label populatorML xml file");
		labelPopulatorXmlFileOption.setArgs(1);
		options.addOption(labelPopulatorXmlFileOption);

		String dotsPerInchOptionAsText = "d";
		Option dpiOption = new Option(dotsPerInchOptionAsText, "dots per inch");
		dpiOption.setArgs(1);
		options.addOption(dpiOption);

		String labelFileOutputOptionAsText = "o";
		Option labelFileOutputOption = new Option(
		    labelFileOutputOptionAsText,
		    "Output populated label as a PNG bitmap file if .png file extenstion or XML if .xml file extenstion");
		labelFileOutputOption.setArgs(1);
		options.addOption(labelFileOutputOption);

		String printerNameOptionAsText = "p";
		Option printerNameOption = new Option(printerNameOptionAsText,
		    "printer name");
		printerNameOption.setArgs(1);
		options.addOption(printerNameOption);
		
		String topLeftCornerOptionAsText = "c";
		Option topLeftCornerOption = new Option(topLeftCornerOptionAsText,
		    "top left corner offset - used for alignment");
		topLeftCornerOption.setOptionalArg(true);
		topLeftCornerOption.setArgs(1);
		options.addOption(topLeftCornerOption);

		String minimalModuleWidthOptionAsText = "m";
		Option minimalModuleWidthOption = new Option(minimalModuleWidthOptionAsText,
    "minimal module width - module is the smallest graphical element of a barcode of which all lines are defined as multiples of");
		minimalModuleWidthOption.setOptionalArg(true);
		minimalModuleWidthOption.setArgs(1);
		options.addOption(minimalModuleWidthOption);
		
		String printWeightOptionAsText = "w";
		Option printWeightOption = new Option(printWeightOptionAsText,
      " print weight - or darkness. Useful for ensuring faint or thin lines get printed." +
      " Ideally this option shouldn't be used by there are going to be some pragmatic circumstances!" +
      " Consult the specific printer manual for the value range and meaning." +
      " If sign +/- is used then this will be relative to the current prevailing darkness setting on the printer" + 
      " If no sign used then the current prevailing darkness setting on the printer would be used." );
		printWeightOption.setArgs(1);
		options.addOption(printWeightOption);
		
    String tagsInFileOptionAsText = "t";
    Option listTagsInFileOption
      = new Option(tagsInFileOptionAsText, "list tags in file" );  
    listTagsInFileOption.setArgs(1);
    options.addOption(listTagsInFileOption);
    
    String allKnownTagsOptionAsText = "a";
    Option getListOfAllKnownTagsOption 
      = new Option(allKnownTagsOptionAsText, "get list of all known tags" );
    options.addOption(getListOfAllKnownTagsOption);
		
    String listPrintersOptionAsText = "l";
    Option listPrintersOption 
      = new Option(listPrintersOptionAsText, "list printers available to this host" );
    options.addOption(listPrintersOption);
    
		// create the parser
		CommandLineParser parser = new PosixParser();
		try
		{
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			String labelSVGFileInput;
			String labelDpi;
			String populatorXmlFile;
			String printer;
			
			if (line.hasOption(inputPopulatedLabelOptionAsText)
			    && line.hasOption(inputPopulatorMLOptionAsText)
			    && line.hasOption(dotsPerInchOptionAsText))
			{
				labelSVGFileInput = line
				    .getOptionValue(inputPopulatedLabelOptionAsText);
				populatorXmlFile = line.getOptionValue(inputPopulatorMLOptionAsText);
				labelDpi = line.getOptionValue(dotsPerInchOptionAsText);

        processDebugOptions(line);
        
        // either or both of: an output file for the png - i.e. the generated png file AND/OR a printer MUST be defined
				if (!line.hasOption(labelFileOutputOptionAsText)
				    && !line.hasOption(printerNameOptionAsText))
				{
					// if neither defined then we have no output destination for the bitmap - so what's the point?!
					// therefore we raise an error
					XyDebug.advise(new XyExceptionCliOutputParamError(
					    XyExceptionCliOutputParamError.NO_OUTPUT_DEFINED), log);
				}
				else // at least one output destination is defined
				{
					// ...knowing this we therefore definitely need to populate the label xml
					
					Document labelTemplateDocToBePopulated = XyDocumentProducer.getDocument( "svg", new File(labelSVGFileInput) );	
					Document labelPopulatorDoc = XyDocumentProducer.getDocument( "label", new File(populatorXmlFile) );
					
					// we need to see if the module width in pixels has been defined
				  // -m (no args) = proportionately scale to longest within placeholder
				  // -m (arg) = render each barcode with module width having value specified by arg in pixels e.g. -m 1 means the module width is 1 printer dot wide
				  // no -m = render each barcode to fit to placeholder
					if ( line.hasOption( minimalModuleWidthOptionAsText ) )
					{						  
						String moduleWidthArg = line.getOptionValue( minimalModuleWidthOptionAsText );
						
						if ( moduleWidthArg != null )
						{
						  XyLabelPopulator.populateEntireLabelToModuleWidth( labelTemplateDocToBePopulated, labelPopulatorDoc, moduleWidthArg, labelDpi );
     				}
				    else // if no value specified then we scale each barcode in proportion to the longest that fits its placeholder
				    { 
				      XyLabelPopulator.populateEntireLabelToLongestWidth(labelTemplateDocToBePopulated, labelPopulatorDoc, labelDpi );
				    }
					}
					else // no -m option - so simply scale each barcode to the placeholder
					{
						XyLabelPopulator.populateEntireLabelFitToPlaceholder(labelTemplateDocToBePopulated, labelPopulatorDoc, labelDpi );
					}
					
				  if (line.hasOption(labelFileOutputOptionAsText) && line.getOptionValue(labelFileOutputOptionAsText).contains( ".svg"))
				  {
						String svgAsString = XyXMLDOMDocumentToStringService.outputAsString(labelTemplateDocToBePopulated);

						FileUtils.writeStringToFile(  new File (labelFileOutputOptionAsText), svgAsString, XyCharacterEncoding.ENCODING );
				  }
					
					// now, are we specifically creating bitmap - to be stored as a .png file AND/OR to be printed?
					if (    (line.hasOption(labelFileOutputOptionAsText) && line.getOptionValue(labelFileOutputOptionAsText).contains( ".png"))  
							 || line.hasOption(printerNameOptionAsText)
						 )
					{
						// if so then we only want to generate the bitmap once
						// and doing this enables us to both generate and print the bitmap 
						// without having to generate it for each of these, efficiency saving
						
						// create a stream to output the bitmap to
						ByteArrayOutputStream bos =  new ByteArrayOutputStream();
						
						// pass stream into the bitmap generator, which will store the resultant bitmap in the stream
						XyLabelBitmapProducer.generateBitmap( labelTemplateDocToBePopulated, bos, labelDpi );
						
						log.trace("Bitmap ready.");
						
						// we now have a stream containing the bitmap which we can then use to output as a png file
						// or print
								
						// consumers of the stream cannot take it in that form directly, so we have to convert it to
						// a common all garden byte array. One would hope that this byte array is actually 
						// the internal byte array of the stream, rather than a copy - which incurs cpu cycles
						// and extra memory to be allocated, but i don't know for sure. It's probably not
						// a big deal. The risks are if the bitmap is huge like 100s of megabytes, and in that
						// case performance and memory limits become a real issue. But actually we know that
						// most bitmaps are multiples of 10Kbytes.
						byte[] bitmapAsByteArray = bos.toByteArray();
			
						// are we going to write the bitmap to a file?	
						if ( line.hasOption(labelFileOutputOptionAsText))
						{
						  if (line.getOptionValue(labelFileOutputOptionAsText).contains( ".png"))
							{

								FileOutputStream fileOutputStream = new FileOutputStream(
								    new File(line.getOptionValue(labelFileOutputOptionAsText)));


								
								fileOutputStream.write(bitmapAsByteArray);
								
				        fileOutputStream.flush();
				        
				        fileOutputStream.close();
							}
						}
						 
						// are we going to print the bitmap?
						if (line.hasOption(printerNameOptionAsText))
						{
							log.trace("Printer option selected.");
							
							// get the printer as specified by the string supplied at the command line
						  printer = line.getOptionValue(printerNameOptionAsText);
						  
							log.trace("Printer:" + printer);
							
						  XyLabelPrintService printService = XyLabelPrintServiceFactory.getPrintService( printer );
								
							InputStream is = new ByteArrayInputStream(bitmapAsByteArray);
							
							// if so, then we want to check if the user has also asked to alter the darkness of the print
							if (line.hasOption(printWeightOptionAsText))
							{
								// they have, and there should be an argument value that the printer will specifically understand
							  // consult the printer manual for the value range and meaning
								String printWeight = line.getOptionValue(printWeightOptionAsText);
								printService.setDarkness( printWeight );
							}
							
							if ( line.hasOption( topLeftCornerOptionAsText ) )
							{
								String topLeftCorner = line.getOptionValue( topLeftCornerOptionAsText );
	
							  String combinedOffset 
							  	= XyPrinterOffsetManager.getCombinedValueWithPrinterOffset(
							  			topLeftCorner, printService.getPrinterName() );
							  
							  printService.setOrigin(combinedOffset);
							}
						
							log.trace("Send bitmap to the print service.");
							
						  // send the bitmap to the printer
							printService.printBitmap(is);
						} // end print option selected
					}
					else // there is output defined, but its not .png or a printer
					{
						// so the only other option is outputting to an svg file
					  if (line.getOptionValue(labelFileOutputOptionAsText).contains( ".svg" ) )
						{
					  	File outputSvgFile = new File(line.getOptionValue(labelFileOutputOptionAsText));
					  	
							FileUtils.writeStringToFile(
									outputSvgFile,
							    XyXMLDOMDocumentToStringService.outputAsString(labelTemplateDocToBePopulated),
							    XyCharacterEncoding.ENCODING);
						}
					} // end check for specifically creating bitmap - to be stored as a .png file AND/OR to be printed?
				} // end check that an output destination is defined
			} // end if populated label, populatorML and dpi present (all must be present together at once)


			if (line.hasOption(helpOptionAsText))
			{
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(commandName, options);
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
      
      if ( line.hasOption( listPrintersOptionAsText ) )
      {
      	Vector<String> printerList = XyLabelPrintServiceFactory.getPrinters();
      	
      	String listAsString = "";
      	for ( int i = 0; i < printerList.size(); i++ )
      	{
      		listAsString += printerList.get(i) + "\n";
      	}
      }
		}
		catch (Exception cause)
		{
			XyDebug.debugException(cause, log);
		}
	} // end main
}
