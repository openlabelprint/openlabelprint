package com.xyratex.label.apps.gui.components;


import java.util.Observable;
import java.util.Vector;

import com.xyratex.constants.XyConstants;
import com.xyratex.label.output.print.XyLabelPrintServiceFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xyratex.label.apps.gui.XyLabelPrintConfigApp;

import com.xyratex.label.output.print.offset.XyOffsetByPrinterInstance;

public class XyPrinterControlLogic extends Observable 
{
  private static Log log = LogFactory.getLog(XyPrinterControlLogic.class);
    
  /**
   * The currently selected printer.
   */
  private String selectedPrinter = "";
  
  public String labelPrinterFilterString = "Z";
 
  private String resolution = "";
  
  private Vector dotsPerUnitLengthList = null;
  
  private XyOffsetByPrinterInstance xyOffsetByPrinterInstance = null;
  
  private XyLabelTemplateImageLogic xyLabelTemplateImageLogic = null;
  
  private XyLabelPrintConfigApp parentApp = null;

  public XyPrinterControlLogic( XyLabelPrintConfigApp app )
  {
      parentApp = app;
      
      dotsPerUnitLengthList = new Vector<String>();
      dotsPerUnitLengthList.add( "203dpi" );
      dotsPerUnitLengthList.add( "300dpi" );
      dotsPerUnitLengthList.add( "600dpi" );
  }
  
  public Vector<String> getDotsPerUnitLengthList()
  {
      return dotsPerUnitLengthList;
  }
  
  
  public String getResolution()
  {
  	return resolution;
  }
  
  public void setPrinter( String p )
  {
      selectedPrinter = p;
      
      xyOffsetByPrinterInstance.setComponentId(p);
  }
  
  public void setOffsetByPrinterInstance( XyOffsetByPrinterInstance offsetByPrinterInstance )
  {
    xyOffsetByPrinterInstance =  offsetByPrinterInstance;
  }
  
  public Vector<String> getPrinterList()
  {	
    Vector<String> attachedPrinterList = XyLabelPrintServiceFactory.getPrinters();
 
    // only list printers capable of printing labels. i.e. exclude office desktop laser printers that
    // might be available to the print station host pc via the network
    
    Vector<String> filteredAttachedPrinterList = filterByString( labelPrinterFilterString, attachedPrinterList );
    
    
    return filteredAttachedPrinterList;
  }
  
  // ideally we'd like to use Apache Commons collections
  // to do this - but we haven't figured this out yet
  private Vector<String> filterByString( String filter, Vector<String> listOfStrings )
  {
  	Vector<String> filteredList = new Vector<String>();
  	
        String filterAsLowerCase = filter.toLowerCase();
        
    for (int i=0; i < listOfStrings.size(); i++) 
    {
    	String aString = listOfStrings.get(i);
    	
    	if ( (aString.toLowerCase()).contains(filterAsLowerCase))
    	{
    		filteredList.add( aString );
    	}
    }
    
    return filteredList;
  }
  

  public void setTemplateImageLogic( XyLabelTemplateImageLogic templateImageLogic )
  {
      xyLabelTemplateImageLogic = templateImageLogic;
  }

  public void print()
  {
      xyLabelTemplateImageLogic.print( selectedPrinter );
  }
  
  public void setResolution( String r )
  {
    if ( r.compareTo(resolution) != XyConstants.COMPARISON_EQUAL)
    {
    	resolution = r;
        
        try
        {
                	xyLabelTemplateImageLogic.resolutionChanged(resolution);
        }
        catch( Exception e )
        {
            parentApp.error( e );
        }

        
    }
  }

  

  /*
  saveOffsetsPrinterInstanceButton.addActionListener
  (
    new 
    ActionListener() 
    {
      public void actionPerformed(ActionEvent ae)
      {
      	try {

      		// open dialog box asking for scan serial number
      		
      		// get original printer offsets and take them away from the printer instance offsets
      		
      		imageToOffset.getXOffsetInPixels();
      		imageToOffset.getYOffsetInPixels();
      		
      		// for now i think we'll just have one way of saving the offsets
      		
      		Icon questionIcon = UIManager.getIcon("OptionPane.questionIcon");
      		
      		final Object noInitialSelectionValue = null;
      		final Object[] forceTextBoxEntry = null;
      		
      		 String printerSerialNumber = (String)JOptionPane.showInputDialog(
              frame, // parentComponent
              "Please scan in printer serial number barcode", // message
              "Printer serial number", // dialog box title
              JOptionPane.PLAIN_MESSAGE, // messageType
              questionIcon, 
              forceTextBoxEntry,
                    // selection values - left null so that text box entry is offered instead 
                    // - we want this so that the barcode scanner can scan the printer serial
                    // number barcode and this number is sent to stdin which will appear in the
                    // of this dialog
              noInitialSelectionValue);// initial selection value - irrelevant because we are accepting a string value0
      		     
      		  File printerOffsetFile = new File( openLabelPrintPath + "printerOffsetFile" );
      		  
      		  String labelOffset =   "printer serial number: " + printerSerialNumber
      		  	                   + " x:" + imageToOffset.getXOffsetInPixels()
      		                       + " "
      		                       + " y:" + imageToOffset.getXOffsetInPixels()
      		                       + " ";

      		                       
      		  FileUtils.writeStringToFile( printerOffsetFile, labelOffset );

      } catch (Exception ex) {
        ex.printStackTrace();
    }
      }
    }
  );
  */

/*
ActionListener offsetSaveAgainstListener = new ActionListener(  ) {
   public void actionPerformed(ActionEvent event) {
  	// offsetSaveAgainst = event.getActionCommand();
   }
};

JFrame saveAgainstOffsetDialog = new JFrame("Save Offset");
saveAgainstOffsetDialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

JPanel saveAgainstOffsetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));


JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
*/

  //return saveAgainstOffsetDialog;
//}
}
