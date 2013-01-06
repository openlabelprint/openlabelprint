//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyLabelProducerCli.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.apps.daemon;

import java.util.Vector;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.imageio.ImageIO;

import javax.print.PrintException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.w3c.dom.Document;

import com.xyratex.debug.XyDebug;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.PosixParser;

import org.apache.commons.io.FileUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jaxen.JaxenException;

import com.xyratex.xml.io.XyDocumentProducer;
import com.xyratex.xml.io.XyXMLDOMDocumentToStringService;

import com.xyratex.label.apps.cli.XyAbstractCli;
import com.xyratex.label.apps.cli.exception.XyExceptionCliOutputParamError;
import com.xyratex.label.apps.gui.components.XyOLPJFrame;
import com.xyratex.label.config.XyCharacterEncoding;
import com.xyratex.label.output.XyLabelBitmapProducer;
import com.xyratex.label.output.print.XyLabelPrintService;
import com.xyratex.label.output.print.XyLabelPrintServiceFactory;
import com.xyratex.label.output.print.offset.XyPrinterOffsetManager;
import com.xyratex.label.population.XyLabelPopulator;
import com.xyratex.label.tags.XyLabelTagRegistry;
import com.xyratex.label.template.exception.XyExceptionElementNotFoundInTemplate;




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
final public class XyOLPLauncher extends XyAbstractCli
{
	/**
	 *  R = Release L = Level G = date U = time
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyOLPLauncher.java  %R%.%L%, %G% %U%";

	/**
	 * log object used for logging activity in the form of String/character messages for debug, diagnostic and analysis purposes.
	 * The output destination for the log could be console output and/or file(s) depending on the user-definable overall configuration
	 * of logs in the system.
	 */
	private static final Log log = LogFactory.getLog(XyOLPLauncher.class);
	
  /**
   * holds the filename of the application - used as the command when running it from the command line 
   */
  public final static String commandName = "producelabel";
  
  private static TrayIcon trayIcon = null;
	
  /**
   * The main entry point of the application, called when it is run at the command line.
   *
   * @param args (input parameter)  the argument list passed from the command line
   * <p>Arguments:</p>
   * <ul>
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
  
  private JFrame jFrame = null;
  
  JPanel overallContainerJPanel = null;
  
  private static int pollingInterval = 50; // 100ms by default
  
  
  private static final String helpOptionAsText = "h";
	private static final Option helpOption = new Option(helpOptionAsText, "help");
  
	private static final String inputPopulatorMLOptionAsText = "x";
	private static final Option labelPopulatorXmlFileOption = new Option(
	    inputPopulatorMLOptionAsText, "Label populatorML xml file");
	
	private static final String labelFileOutputOptionAsText = "o";
	private static Option labelFileOutputOption = new Option(
	    labelFileOutputOptionAsText,
	    "Output populated label as a PNG bitmap file if .png file extenstion or XML if .xml file extenstion");
	
	private static final String topLeftCornerOptionAsText = "c";
	private static final Option topLeftCornerOption = new Option(topLeftCornerOptionAsText,
  "top left corner offset - used for alignment");
	
	private static final String printerNameOptionAsText = "p";
	private static final Option printerNameOption = new Option(printerNameOptionAsText,
	    "printer name");
	
	private static final String minimalModuleWidthOptionAsText = "m";
	private static final Option minimalModuleWidthOption = new Option(minimalModuleWidthOptionAsText,
  "minimal module width - module is the smallest graphical element of a barcode of which all lines are defined as multiples of");

	private static final String printWeightOptionAsText = "w";
	private static final Option printWeightOption = new Option(printWeightOptionAsText,
    " print weight - or darkness. Useful for ensuring faint or thin lines get printed." +
    " Ideally this option shouldn't be used by there are going to be some pragmatic circumstances!" +
    " Consult the specific printer manual for the value range and meaning." +
    " If sign +/- is used then this will be relative to the current prevailing darkness setting on the printer" + 
    " If no sign used then the current prevailing darkness setting on the printer would be used." );
	
	private static final String tagsInFileOptionAsText = "t";
	private static Option listTagsInFileOption
    = new Option(tagsInFileOptionAsText, "list tags in file" );  
	
	private static final String allKnownTagsOptionAsText = "a";
	private static final Option getListOfAllKnownTagsOption 
    = new Option(allKnownTagsOptionAsText, "get list of all known tags" );
	
	private static final String listPrintersOptionAsText = "l";
	private static final Option listPrintersOption 
    = new Option(listPrintersOptionAsText, "list printers available to this host" );
	
	private static final String dotsPerInchOptionAsText = "d";
	private static final Option dpiOption = new Option(dotsPerInchOptionAsText, "dots per inch");
	
	private static final String pollingIntervalAsText = "t";
	private static final Option pollingIntervalOption = new Option( pollingIntervalAsText, "time between polls" );
	
  public static void main(String args[])
  {
  	log.trace(           XyAbstractCli.sccsid
        + "\n" + sccsid );
  	

		options.addOption(helpOption);


		labelPopulatorXmlFileOption.setArgs(1);
		options.addOption(labelPopulatorXmlFileOption);


		dpiOption.setArgs(1);
		options.addOption(dpiOption);


		labelFileOutputOption.setArgs(1);
		options.addOption(labelFileOutputOption);


		printerNameOption.setArgs(1);
		options.addOption(printerNameOption);
		

		topLeftCornerOption.setOptionalArg(true);
		topLeftCornerOption.setArgs(1);
		options.addOption(topLeftCornerOption);

		minimalModuleWidthOption.setOptionalArg(true);
		minimalModuleWidthOption.setArgs(1);
		options.addOption(minimalModuleWidthOption);
		

		printWeightOption.setArgs(1);
		options.addOption(printWeightOption);
		

    listTagsInFileOption.setArgs(1);
    options.addOption(listTagsInFileOption);
    

    options.addOption(getListOfAllKnownTagsOption);
		

    options.addOption(listPrintersOption);
    
    
    pollingIntervalOption.setOptionalArg(true);
    pollingIntervalOption.setArgs(1);
    options.addOption(pollingIntervalOption);
    
		// create the parser
		CommandLineParser parser = new PosixParser();
		try
		{
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (   line.hasOption(inputPopulatorMLOptionAsText)
			    && line.hasOption(dotsPerInchOptionAsText))
			{
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
					
					pollingLoopForPopulatorChange( line );
					
				}
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
  
  public XyOLPLauncher( JFrame f )
  {
    jFrame = f;
    
    overallContainerJPanel = new JPanel();	
  }
  
  public JPanel getOverallContainerJPanel()
  {
      return overallContainerJPanel;
  }
  
  private static void setUpSystemTray( Image image ) throws AWTException
  {
    ActionListener exitListener = new ActionListener() 
    {
      public void actionPerformed(ActionEvent e) 
      {
        System.out.println("Exiting...");
        System.exit(0);
      }
    };
    
    ActionListener reprintListener = new ActionListener() 
    {
      public void actionPerformed(ActionEvent e) 
      {
        
      }
    };
    
    ActionListener settingsListener = new ActionListener() 
    {
      public void actionPerformed(ActionEvent e) 
      {
        
      }
    };
              
    PopupMenu popup = new PopupMenu();
    MenuItem exitMenuItem = new MenuItem("Exit");
    MenuItem reprintMenuItem = new MenuItem("Reprint...");
    MenuItem settingsItem = new MenuItem("Settings...");
    
    exitMenuItem.addActionListener(exitListener);
    popup.add(exitMenuItem);
    popup.add(reprintMenuItem);
    popup.add(settingsItem);

    trayIcon = new TrayIcon(image, "OLP", popup);

    ActionListener actionListener = new ActionListener() 
    {
      public void actionPerformed(ActionEvent e) 
      {
        trayIcon.displayMessage( "Action Event", 
                                 "An Action Event Has Been Performed!",
                                 TrayIcon.MessageType.INFO);
      }
    };
              
    trayIcon.setImageAutoSize(true);
    trayIcon.addActionListener(actionListener);
    //trayIcon.addMouseListener(mouseListener);

    SystemTray tray = SystemTray.getSystemTray();
    
    tray.add(trayIcon);
  }
  
  private static void pollingLoopForPopulatorChange(  CommandLine line ) 
    throws AWTException, // couldn't create system tray icon
           IllegalAccessException, ClassNotFoundException, InstantiationException, InterruptedException, Exception
  {      
  	String populatorXmlFilenameAsString = line.getOptionValue(inputPopulatorMLOptionAsText);
  	String labelDpiAsString = line.getOptionValue(dotsPerInchOptionAsText);
  	
  	if ( line.hasOption(pollingIntervalAsText))
  	{
  	  String pollingIntervalAsString = line.getOptionValue(pollingIntervalAsText);
  	  log.trace("pollingIntervalAsString:" + pollingIntervalAsString );
  	  pollingInterval = Integer.parseInt(pollingIntervalAsString);
  	}
  	
  	Document currentLabelPopulatorDoc = null;
  	Document updatedLabelPopulatorDoc = null;
  	
  	File populatorXmlFile = new File(populatorXmlFilenameAsString);

  	long lastModifiedUpdatedValue = 0;
  	long lastModifiedCurrentValue = 0;

  	String updatedTemplatePath = "";
  	String currentTemplatePath = "";
  	
  	Document labelTemplateDocToBePopulated = null;
  	
    // Create a new JFrame - this is the main window of the application
		JFrame f = new XyOLPJFrame("Open Label Print Running");
		
		// instantiate the application
		XyOLPLauncher app = new XyOLPLauncher(f);
		
		// when the 'x' close button is pressed...
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Add components to the frame.
		
		try
		{
		  f.getContentPane().add(app.getOverallContainerJPanel());
		}
		catch( Exception e )
		{
			log.trace(e.getMessage() + e.getStackTrace() );
		}
		
		// Display the frame.
		f.addWindowListener( new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		
    //Image image = Toolkit.getDefaultToolkit().getImage("olptrayicon.png");
		
		Image image = null;
		try
		{
		  URL url = app.getClass().getResource("/com/xyratex/label/apps/gui/components/olptrayicon.png");
      image = ImageIO.read( url );
		}
		catch(IOException e )
		{
		  // image will be null
		  XyDebug.debugException(e, log); 
		}
		
		if (SystemTray.isSupported() && image != null ) 
		{
		  setUpSystemTray( image );
		} 
		else //  System Tray is not supported
		{
	    f.setVisible(true); 
		}

    boolean populatorFileHasChanged = false;
  	
		while (true)
		{
      // keep for debugging
		  //String logstr =    "polling loop start\n"
		  //                 + "lastModifiedCurrentValue = " + lastModifiedCurrentValue
		  //                 + "\n";
		  
			lastModifiedUpdatedValue = populatorXmlFile.lastModified();

	    // keep for debugging
			//logstr += "lastModifiedUpdatedValue = " + lastModifiedUpdatedValue;

			// keep for debugging
	    //log.trace( logstr );  

			if (lastModifiedUpdatedValue != lastModifiedCurrentValue)
			{
			  log.trace("lastModifiedUpdatedValue != lastModifiedCurrentValue");
				// the file may have changed

			  updatedLabelPopulatorDoc = XyDocumentProducer.getDocument("label", populatorXmlFile);
			  
        // was the changed populator Document obtained successfully?
			  if ( updatedLabelPopulatorDoc != null )
			  {
			    log.trace("updatedLabelPopulatorDoc != null");
			    // changed populator file obtained successfully, now see if its predecessor existed
			    if ( currentLabelPopulatorDoc == null )
			    {
			      // its predecessor did not exist so this means that this is the first time the populator is being used on the template
            // we regard this as the populator being changed
			      populatorFileHasChanged = true;
            log.trace("first time print: currentLabelPopulatorDoc == null && updatedLabelPopulatorDoc != null");
			    }
			    else 
			    {
			      log.trace("the previous label populator and the new changed one both exist");
			      // the previous label populator and the new changed one both exist
			      // - are they the same?
            if (updatedLabelPopulatorDoc.isEqualNode(currentLabelPopulatorDoc))
            {
              // they are the same so the populator hasn't actually changed
              log.trace("populator doc hasn't changed");
              populatorFileHasChanged = false;
              
              // do this as we've already noticed the new date stamp for the doc but we know it hasn't changed 
              lastModifiedCurrentValue = lastModifiedUpdatedValue;
            }  
            else
            {
              // the new populator really has changed
              populatorFileHasChanged = true;
              log.trace("populator file has changed");
            }
			    } // if ( currentLabelPopulatorDoc == null )
			  }
			  else
			  {
			    log.trace("populator Document = null - problem with populator file?");
			    populatorFileHasChanged = false;
			  } // if (lastModifiedUpdatedValue != lastModifiedCurrentValue)
        
        currentLabelPopulatorDoc = updatedLabelPopulatorDoc;
        
				if (populatorFileHasChanged)
				{
			    log.trace("populator file has changed");
			    
		      updatedTemplatePath = XyLabelPopulator.getTemplatePath(currentLabelPopulatorDoc);
					
		      if ( !(updatedTemplatePath.equals(currentTemplatePath)) )
		      {
		        log.trace("template has changed");
		        currentTemplatePath = updatedTemplatePath;
	          labelTemplateDocToBePopulated = XyDocumentProducer.getDocument("svg", new File(currentTemplatePath));
		      }
		      else
		      {
		        log.trace("template has not changed");
		      }

	        handleChangedPopulator( currentLabelPopulatorDoc, line, labelDpiAsString, labelTemplateDocToBePopulated );
	        
	        populatorFileHasChanged = false; // reset the flag
				}
				
				lastModifiedCurrentValue = lastModifiedUpdatedValue;
        
        log.trace(   "end of loop body\n" 
                   + "lastModifiedUpdatedValue = " + lastModifiedUpdatedValue + "\n" 
                   + "lastModifiedCurrentValue = " + lastModifiedCurrentValue
                 );
			}
			
		  Thread.sleep(pollingInterval);
  	}
	}
 
  	
  public static void handleChangedPopulator( Document currentLabelPopulatorDoc, CommandLine line, String labelDpiAsString, Document labelTemplateDocToBePopulated )
    throws IOException,
           InstantiationException,
           ClassNotFoundException, 
           IllegalAccessException,
           Exception

  {
		// we need to see if the module width in pixels has been defined
		// -m (no args) = proportionately scale to longest within
		// placeholder
		// -m (arg) = render each barcode with module width having value
		// specified by arg in pixels e.g. -m 1 means the module width is 1
		// printer dot wide
		// no -m = render each barcode to fit to placeholder
		if (line.hasOption(minimalModuleWidthOptionAsText))
		{
			String moduleWidthArg = line.getOptionValue(minimalModuleWidthOptionAsText);

			if (moduleWidthArg != null)
			{
        XyLabelPopulator.populateEntireLabelToModuleWidth(
					labelTemplateDocToBePopulated, currentLabelPopulatorDoc,
					moduleWidthArg, labelDpiAsString);
      }
			else
			{
        // if no value specified then we scale each barcode in proportion
        // to
        // the longest that fits its placeholder
			  
				XyLabelPopulator.populateEntireLabelToLongestWidth(
          labelTemplateDocToBePopulated, currentLabelPopulatorDoc,
          labelDpiAsString);
			}
    }
    else
    {  
      // no -m option - so simply scale each barcode to the placeholder
			XyLabelPopulator.populateEntireLabelFitToPlaceholder(
				labelTemplateDocToBePopulated, currentLabelPopulatorDoc,
				labelDpiAsString);
		}

    if (   line.hasOption(labelFileOutputOptionAsText)
			  && line.getOptionValue(labelFileOutputOptionAsText).contains( ".svg" ))
    {
      String svgAsString = XyXMLDOMDocumentToStringService.outputAsString(labelTemplateDocToBePopulated);

      FileUtils.writeStringToFile( new File(labelFileOutputOptionAsText), svgAsString, XyCharacterEncoding.ENCODING);
		}

		// now, are we specifically creating bitmap - to be stored as a .png
    // file AND/OR to be printed?
    if (   (line.hasOption(labelFileOutputOptionAsText) 
			   && line.getOptionValue(labelFileOutputOptionAsText).contains(".png"))
			   || line.hasOption(printerNameOptionAsText))
		{
		  handleBitmap( labelTemplateDocToBePopulated, labelDpiAsString, line );
		}
		else // there is output defined, but its not .png or a printer
		{
			// so the only other option is outputting to an svg file
			if (line.getOptionValue(labelFileOutputOptionAsText).contains( ".svg"))
			{
			  File outputSvgFile = new File(line.getOptionValue(labelFileOutputOptionAsText));

				FileUtils.writeStringToFile(outputSvgFile,
					XyXMLDOMDocumentToStringService.outputAsString(labelTemplateDocToBePopulated),
					XyCharacterEncoding.ENCODING);
			}
		} // end check for specifically creating bitmap - to be stored as a .png file AND/OR to be printed?
  }


	public static void handleBitmap( Document labelTemplateDocToBePopulated, String labelDpiAsString, CommandLine line )
	  throws IOException,
	         FileNotFoundException,
	         XyExceptionElementNotFoundInTemplate,
	         JaxenException,
	         TranscoderException,
	         PrintException
	{
    // if so then we only want to generate the bitmap once
    // and doing this enables us to both generate and print the bitmap
    // without having to generate it for each of these, efficiency
    // saving

    // create a stream to output the bitmap to
    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    // pass stream into the bitmap generator, which will store the
    // resultant bitmap in the stream
    XyLabelBitmapProducer.generateBitmap( labelTemplateDocToBePopulated, bos, labelDpiAsString);

    log.trace("Bitmap ready.");

    // we now have a stream containing the bitmap which we can then use to output as a png file or print
    // consumers of the stream cannot take it in that form directly, so we have to convert it to a common
    // all garden byte array. One would hope that this byte array is actually
	  // the internal byte array of the stream, rather than a copy -
	  // which incurs cpu cycles and extra memory to be allocated, but i don't know for sure.
		// It's probably not a big deal. The risks are if the bitmap is huge like 100s of
		// megabytes, and in that case performance and memory limits become a real issue. But
	  // actually we know that most bitmaps are multiples of 10Kbytes.
		byte[] bitmapAsByteArray = bos.toByteArray();

		// are we going to write the bitmap to a file?
		if (line.hasOption(labelFileOutputOptionAsText))
		{
			if (line.getOptionValue(labelFileOutputOptionAsText).contains(".png"))
			{
			  log.trace("writing to file");
				FileOutputStream fileOutputStream = new FileOutputStream( new File(line.getOptionValue(labelFileOutputOptionAsText)));

				fileOutputStream.write(bitmapAsByteArray);
				
				fileOutputStream.flush();
				
				fileOutputStream.close();
				log.trace("writing complete");
			}
		}

		// are we going to print the bitmap?
		if (line.hasOption(printerNameOptionAsText))
		{
		  log.trace("Printer option selected.");

			// get the printer as specified by the string supplied at the
			// command line
			String printer = line.getOptionValue(printerNameOptionAsText);

			log.trace("Printer:" + printer);

			XyLabelPrintService printService = XyLabelPrintServiceFactory.getPrintService(printer);

			InputStream is = new ByteArrayInputStream(bitmapAsByteArray);

			// if so, then we want to check if the user has also asked to
			// alter the darkness of the print
			if (line.hasOption(printWeightOptionAsText))
			{
				// they have, and there should be an argument value that the
				// printer will specifically understand
				// consult the printer manual for the value range and meaning
				String printWeight = line.getOptionValue(printWeightOptionAsText);
				printService.setDarkness(printWeight);
			}

			if (line.hasOption(topLeftCornerOptionAsText))
			{
				String topLeftCorner = line.getOptionValue(topLeftCornerOptionAsText);

				String combinedOffset = XyPrinterOffsetManager.getCombinedValueWithPrinterOffset(topLeftCorner, printService.getPrinterName());

				printService.setOrigin(combinedOffset);
			}

		 	log.trace("Send bitmap to the print service.");

			// send the bitmap to the printer
			printService.printBitmap(is);
			
			log.trace("sent");
		} // end print option selected
	}
} 	

