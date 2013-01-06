//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyLabelSinglePopulatorCli.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.apps.cli;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xyratex.debug.XyDebug;

import com.xyratex.xml.io.XyDocumentProducer;
import com.xyratex.xml.io.XyXMLDOMDocumentToStringService;
import com.xyratex.xml.utils.XyXMLDOMSearchUtils;

import com.xyratex.label.barcode.XyBarcodeGenerator;
import com.xyratex.label.barcode.XyBarcodeGeneratorFactory;
import com.xyratex.label.population.XyLabelPopulator;
import com.xyratex.label.population.exception.XyExceptionUnknownFieldTypeForPopulating;
import com.xyratex.label.tags.XyLabelTagRegistry;


/**
 * <p>The command line tool for populating a copy of a SVG label template with barcodes or human readable numbers.</p>
 *  
 *  <p>The class is for end-use command line execution of the tool 
 *  and is not intended to be called by other java code.</p>
 *  
 *  @see com.xyratex.label.output.XyLabelBitmapProducer XyLabelBitmapProducer
 * 
 * @author Rob Davis
 */
final public class XyLabelSinglePopulatorCli extends XyAbstractCli
{
	/**
	 *  R = Release L = Level G = date U = time
	 *  Values calculated by SCCS when file is checked out and compiled.
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyLabelSinglePopulatorCli.java  %R%.%L%, %G% %U%";
	
  public final static String commandName = "populatelabel";
    
	/**
	 * log object used for logging activity in the form of String/character messages for debug, diagnostic and analysis purposes.
	 * The output destination for the log could be console output and/or file(s) depending on the user-definable overall configuration
	 * of logs in the system.
	 */
	private final static Log log = LogFactory.getLog(XyLabelSinglePopulatorCli.class);
  
	/**
	 * The main entry point of the application, called when it is run at the command line.
	 *
	 * @param  args (input parameter) the argument list passed from the command line
	 * <p>For barcode population it requires 4 mandatory argument</p>
	 *
	 * <ul>
	 *  <li>-i the input file - a copy of a SVG label template - i.e. a label with the barcode fields empty</li>
	 *  <li>-o the output file - a SVG file populated with the barcode</li>
	 *  <li>-t the tag id, identifying the barcode field on the label where the barcode should be placed, e.g. BC_SN - for barcode serial number</li>
	 *  <li>-n the number used for the barcode - e.g. a serial number DHSS50A12345678</li>
	 * </ul>
	 * 
	 * <p>Optional argument: -s symbology e.g. code128 which overides the symbology specified by the industrial designer within the label.</p>
	 * 
	 * It will issue an error if:
	 * <ul>
	 *  <li>the input file is not readable - e.g. non-existent</li>
	 *  <li>the output file or its location is not writable</li>
	 *  <li>the number given does not comply with the symbology specified within the label
	 *  - this can be overided by providing a different symbology using -s as the optional parameter</li>
	 * </ul> 
	 * 
	 * <p>This class facilitates:</p>
	 * <ul>
	 *    <li>run-time integration with labelling existing systems, 
	 *    particularly those that are non-java, 3rd-party, commercial or don't build/compile
	 *    within the Java environment</li>
	 *    <li>demonstration of the SVG Barcode Label project</li>
	 *    <li>integration testing of the SVG Barcode label population</li>
	 * </ul>
	 */
  public static void main(String args[])
  {
  	log.trace(           XyAbstractCli.sccsid
        + "\n" + sccsid );
  	
		String labelFileInput;
		String labelFileOutput;
		String tagString;
		String numberString;
		String symbology;
	  
		String helpOptionAsText = "h";
		Option helpOption = new Option(helpOptionAsText, "help");
		options.addOption(helpOption);

		String inputLabelOptionAsText = "i";
		Option labelSVGFileInputOption = new Option(
		    inputLabelOptionAsText, "Populated SVG label input");
		labelSVGFileInputOption.setArgs(1);
		options.addOption(labelSVGFileInputOption);
	
		String labelFileOutputOptionAsText = "o";
		Option labelFileOutputOption = new Option(
		    labelFileOutputOptionAsText,
		    "label file output - with the number");
		labelFileOutputOption.setArgs(1);
    options.addOption(labelFileOutputOption);
		
		String tagOptionAsText = "t";
		Option tagOption = new Option(tagOptionAsText, "tag");
		tagOption.setArgs(1);
		options.addOption(tagOption);
		
		String numberOptionAsText = "n";
		Option numberOption = new Option(numberOptionAsText, "number");
		numberOption.setArgs(1);
		options.addOption(numberOption);

		String symbologyOptionAsText = "s";
		Option symbologyOption = new Option(symbologyOptionAsText, "symbology" );
		symbologyOption.setArgs(1);
		options.addOption(symbologyOption);
	
    // create the parser
    CommandLineParser parser = new PosixParser();
    try {
        // parse the command line arguments
        CommandLine line = parser.parse( options, args );
         
        if (    line.hasOption(inputLabelOptionAsText) 
        		 && line.hasOption(labelFileOutputOptionAsText)
        		 && line.hasOption(tagOptionAsText)
        		 && line.hasOption(numberOptionAsText)
        	 )
        {
      		labelFileInput = line.getOptionValue(inputLabelOptionAsText);
      		labelFileOutput = line.getOptionValue(labelFileOutputOptionAsText);
      		tagString = line.getOptionValue(tagOptionAsText);
      		numberString = line.getOptionValue(numberOptionAsText);
      		
      		if ( XyLabelTagRegistry.tagIsForBarcode( tagString ) )
      		{
          	if (line.hasOption(symbologyOptionAsText))
          	{

          		symbology = line.getOptionValue(symbologyOptionAsText);
          		
          		log.trace("(i)nput label template file: " + labelFileInput);
          		log.trace("name of (o)output file for modified label: " + labelFileOutput);
          		log.trace("(t)ag of barcode field in label: " + tagString);
          		log.trace("(n)umber: " + numberString);
          		log.trace("(s)ymbology: " + symbology);
          		
          		// TODO handle module width
          		

          		Document doc = XyDocumentProducer.getDocument( "svg", new File(labelFileInput));
          		
          		
              Element placeholderElementInTemplate = XyXMLDOMSearchUtils
              .getElementByPartialAttributeValueMatch(
                  tagString, "id", "rect",
                  doc);
          		
              
              

              XyBarcodeGenerator barcode = null;
            
              float orientation = 0.0F;
              
              if ( placeholderElementInTemplate.getAttribute("id").contains(XyLabelTagRegistry.ORIENTATIONVERTICAL) )
              {
                orientation = 90F;
              }
              
              
              XyBarcodeGeneratorFactory barcodeGeneratorFactory = new XyBarcodeGeneratorFactory();
              
              




              barcode = barcodeGeneratorFactory.createBarcodeGenerator( 
                     numberString,
                     symbology,
                     doc.getNamespaceURI(), placeholderElementInTemplate, orientation );
              
              
              
              
              
              
                     //public static void addBarcode( Document labelTemplate, final Document barcodeDOM, final double xScaleFactorValue, final double yScaleFactorValue, final Element existingElementWithTagId )
                     
             double xScaleFactorValue = 0.0; double yScaleFactorValue = 0.0;
                 
             

             

             
             barcode.setScaleFactorUsingPlaceholderWidth(Double.valueOf( placeholderElementInTemplate.getAttribute("width") ).doubleValue());
             
             xScaleFactorValue = barcode.getScaleFactor();

             yScaleFactorValue = 1.0F;
                     
              XyLabelPopulator.addBarcode( doc, barcode.getBarcodeDOM(), xScaleFactorValue , yScaleFactorValue, placeholderElementInTemplate );          
              
          		
          		
          		String svgAsString = XyXMLDOMDocumentToStringService.outputAsString(doc);
          		FileUtils.writeStringToFile( new File(labelFileOutput), svgAsString);
          	}
          	else // no symbology provided at commandline - its in the label template itself
          	{
          		Document doc = XyDocumentProducer.getDocument( "label", new File(labelFileInput));
          		
          		// TODO needs to complete
          		/*
          		Element existingElementWithTagId = XyLabelTagRegistry.getBarcodePlaceholderElement(tagString, doc);
          		
							//XyLabelPopulator.addBarcode(doc, numberString, XyLabelPopulator.getSymbologyInLabelTemplate( doc, existingElementWithTagId ),
							//    existingElementWithTagId );
          		*/
          		
          		String svgAsString = XyXMLDOMDocumentToStringService.outputAsString(doc);
          		FileUtils.writeStringToFile(  new File(labelFileOutput), svgAsString);
          	}
      		}
      		else // tag is not a barcode
      		{
      			// is it human readable?
        		if ( XyLabelTagRegistry.tagIsForHumanReadable( tagString ) )
        		{

        			Document doc = XyDocumentProducer.getDocument( "svg", labelFileInput );
        			XyLabelPopulator.addNumber( doc, numberString, tagString );
        			String svgAsString = XyXMLDOMDocumentToStringService.outputAsString(doc);
        			FileUtils.writeStringToFile(  new File (labelFileOutput), svgAsString);
        		}
        		else
        		{
        			throw new XyExceptionUnknownFieldTypeForPopulating( XyExceptionUnknownFieldTypeForPopulating.NO_OUTPUT_DEFINED );
        		} // checj if it is human readable
      		} // end check if tag is barcode/machine scannable 
        } // end check for options i,o,t,n
        
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
