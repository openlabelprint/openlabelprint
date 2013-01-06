//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyOffsettingApp.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.apps.gui;


import java.awt.LayoutManager;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xyratex.label.apps.gui.components.XyChildGui;

import com.xyratex.label.apps.gui.components.XyLabelTemplateImageLogic;

import com.xyratex.label.apps.gui.components.XyOLPRecallPrintedLabelJPanel;
import com.xyratex.label.apps.gui.components.XyOLPRecallPrintedLabelLogic;


import com.xyratex.label.apps.gui.components.XyOLPStoreLabelJPanel1;
import com.xyratex.label.apps.gui.components.XyOLPStoreLabelLogic;

import com.xyratex.label.apps.gui.components.XyLoadLabelTemplateLogic;

import com.xyratex.label.apps.gui.components.XyPrinterControlLogic;
import com.xyratex.label.apps.gui.components.XyRedrawNotifiable;
import com.xyratex.label.output.print.offset.XyOffsetByPrinterInstance;
import com.xyratex.label.output.print.offset.XyOffsetSum;

import java.util.Observer;
import java.util.Observable;

import com.xyratex.label.apps.gui.components.XyOverallJPanel;

import com.xyratex.label.apps.gui.components.XyLabelTemplateImageJPanel;

    
import com.xyratex.label.apps.gui.components.XyLoadLabelTemplateJPanel;

  
      
 import com.xyratex.label.apps.gui.components.XyOffsettingJPanel;

    
 import com.xyratex.label.apps.gui.components.XyPrinterControlJPanel;
 

 
import com.xyratex.debug.XyDebug;
import javax.swing.UIManager;

      import javax.swing.UIManager.*;

/**
 * <p>
 * Application to enable offsetting adjustments on label printing,
 * to compensate for drift due to wear-and-tear of alignment mechanics on printer
 * and also where printer models have an offset.
 * </p>
 * 
 * <p>
 * This is an ongoing effort, very rough basic capability is provided at the current time
 * to provide offsetting of labels.
 * </p>
 * 
 * @author rdavis
 *
 */
public class XyLabelPrintConfigApp implements XyRedrawNotifiable, Observer
{
	/**
	 *  R = Release L = Level G = date U = time
	 *  Values calculated by SCCS when file is checked out and compiled.
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyOffsettingApp.java  %R%.%L%, %G% %U%";
	
	/**
	 * log object used for logging activity in the form of String/character messages for debug, diagnostic and analysis purposes.
	 * The output destination for the log could be console output and/or file(s) depending on the user-definable overall configuration
	 * of logs in the system.
	 */
	private static Log log = LogFactory.getLog(XyLabelPrintConfigApp.class);
	
  /**
	 * The frame - the Window displayed which the application UI is contained within.
	 */
  protected JFrame jFrame = null;
	
	/**
	 * The overall graphical user interface container for this application.
	 */
  XyOverallJPanel overallContainerJPanel = null;
  LayoutManager overallLayout = null; 
	
  private XyOffsetSum offsetSum = null;
  
  // declare ui component variables
	private XyLabelTemplateImageLogic labelImageLogic = null;
	private XyLoadLabelTemplateLogic loadLabelTemplateLogic = null;
	
  private XyOLPRecallPrintedLabelLogic recallPrintedLabelLogic = null;
	
	//private XyOffsettingGUI offsetByPrinterInstanceGUI = null;
	private XyPrinterControlLogic printerControlLogic = null;
	
	private XyOffsetByPrinterInstance offsetByPrinterInstance = null;
        
        
          private XyLabelTemplateImageJPanel xyLabelTemplateImageJPanel = null;
          
          private XyOLPRecallPrintedLabelJPanel recallPrintedLabelJPanel = null;

    
   private XyLoadLabelTemplateJPanel xyLoadLabelTemplateJPanel = null;

  
      
  private XyOffsettingJPanel xyOffsettingJPanel = null;

    
  private XyPrinterControlJPanel xyPrinterControlJPanel = null;
  
  private  XyOLPStoreLabelJPanel1 xyStoreLabelJPanel = null;
  
  /**
   * Create the ui component resposible for displaying the label template.
   */
  protected XyLabelTemplateImageLogic labelAlignmentDisplay = null; 
  
  private XyOLPStoreLabelLogic storeLabelLogic = null;
  

  

  

  

  
  public XyLabelPrintConfigApp(JFrame f) 
  {
    jFrame = f;
    
    overallContainerJPanel = new XyOverallJPanel();
    
    initComponents();
  }
  
  public XyOverallJPanel getOverallContainerJPanel()
  {
      return overallContainerJPanel;
  }
  
  public JFrame getJFrame()
  {
  	return jFrame;
  }
  
  /**
   * The starting point of execution of the application.
   * 
   * @param args
   */
  public static void main(String[] args)
	{




try {
    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
            UIManager.setLookAndFeel(info.getClassName());
            break;
        }
    }
} catch (Exception e) {
    // If Nimbus is not available, you can set the GUI to another look and feel.



}




		// Create a new JFrame - this is the main window of the application
		JFrame f = new JFrame("Label Offsetter Tool (ISTP OLP)");
		
		// instantiate the application
		XyLabelPrintConfigApp app = new XyLabelPrintConfigApp(f);
		
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
		
		
		//f.setSize(400, 400);
		f.pack();
		
		f.setVisible(true);
	}
    
  public void redrawRequired( XyChildGui child )
  {
  	jFrame.update( jFrame.getGraphics() );
  }
  
  public void update( Observable observable, Object O )
  {
    //if ( observable instanceof )
  }
  
  
  /**
   * Create all the UI components
   * @return
   */
  public void initComponents() 
  {
  	// initialise components
  	
  	// looks after the loading of the label template, providing facility for user to load template
  	loadLabelTemplateLogic = new XyLoadLabelTemplateLogic();
        
        loadLabelTemplateLogic.setFrame( jFrame );
        
        
        recallPrintedLabelLogic = new XyOLPRecallPrintedLabelLogic();
  	
        recallPrintedLabelLogic.setFrame( jFrame );
        
        
  	// looks after the display of the label template
  	labelImageLogic = new XyLabelTemplateImageLogic( this );
  	
  	// labelImageGUI displays the template that is loaded
  	// therefore it needs to know if this changes
  	// - as a result of the user loading a new one
  	//loadLabelTemplateLogic.addObserver(labelImageLogic);
        loadLabelTemplateLogic.setLabelImageLogic(labelImageLogic);
        
        recallPrintedLabelLogic.setLabelImageLogic(labelImageLogic);
  	
  	// looks after offsets against the printer installation
  	offsetByPrinterInstance = new XyOffsetByPrinterInstance( this );
  	
  	// looks after the gui that enables the editing of the offsets for the printer installation
  	//offsetByPrinterInstanceGUI = new XyOffsettingGUI( offsetByPrinterInstance );
  	
  	// looks after the control of the printer - setup and printing
  	printerControlLogic = new XyPrinterControlLogic( this );
          printerControlLogic.setOffsetByPrinterInstance( offsetByPrinterInstance );
        printerControlLogic.addObserver( this );
        
        printerControlLogic.setTemplateImageLogic( labelImageLogic );

  	
        storeLabelLogic = new XyOLPStoreLabelLogic();
        
        storeLabelLogic.setLoadLabelLogic( loadLabelTemplateLogic );
        
  	// the labelImageGUI wants to know if the dots per unit measurement (resolution)
  	// has changed 
  	//printerControlLogic.addObserver(labelImageLogic);
  
  	

  	
  	// labelImageGUI displays the template with the offsets applied
  	// therefore it needs to know if the user changes the offsets
  	//offsetSum.addObserver(labelImageLogic);


   
        
   xyLabelTemplateImageJPanel = overallContainerJPanel.getLabelTemplateImageJPanel();

   
           labelImageLogic.setImageUI(xyLabelTemplateImageJPanel);
           
             	// provides the overall offset value that effects the position of the label image when printed - and when displayed
  	offsetSum = new XyOffsetSum( labelImageLogic );
  	offsetSum.addOffset(offsetByPrinterInstance);
    
   xyLoadLabelTemplateJPanel = overallContainerJPanel.getLoadLabelTemplateJPanel();
   xyLoadLabelTemplateJPanel.setLogic(loadLabelTemplateLogic);
  
   recallPrintedLabelJPanel = overallContainerJPanel.getRecallPrintedLabelJPanel();
   
   recallPrintedLabelJPanel.setLogic( recallPrintedLabelLogic );
   
      
  xyOffsettingJPanel = overallContainerJPanel.getOffsettingJPanel();
  xyOffsettingJPanel.setLogic( offsetByPrinterInstance );
    
  xyPrinterControlJPanel = overallContainerJPanel.getPrinterControlJPanel();
  xyPrinterControlJPanel.setLogic( printerControlLogic );
  	
  
  xyLabelTemplateImageJPanel.setOffsetSum( offsetSum );
  labelImageLogic.setOffsetSum( offsetSum );
  
  xyLabelTemplateImageJPanel.setXyLabelPrintConfigApp( this );
  
  xyStoreLabelJPanel = overallContainerJPanel.getStoreLabelJPanel();
  
  
  xyStoreLabelJPanel.setLogic(storeLabelLogic);
  
  

  

  

  
  
  }

  public void error( Exception e )
  {
      XyDebug.debugException(e, log);
  }
  
}

/**
 * Future plans for managing the various causes of offsetting
 * 
 * inputs
 *   - scanning in part number - label part offset (comes from label.info - from versioned packaged zip file available locally)
 *   - drop down list on applicaton, printer model offset (local printermodelxml file), ideally from device driver, which printer
 *   - printer instance offset (based on serial number) (local printerinstancexml file - needs to be centralised), manually entered 
 *     from serial number - comes from label on printer
 *   - svg file (derived from part number) (file name from label.info from versioned packaged zip file available locally)
 *
 * what needs to be in a database? instance offsets and model offsets
 *
 * model offsets: printer model, x offset, y offset
 *
 * <printermodeloffsetml>
 *  <printermodel model="" serialnumber="" xoffset="" yoffset=""/>
 *  <printermodel model="" serialnumber="" xoffset="" yoffset=""/>
 *  <printermodel model="" serialnumber="" xoffset="" yoffset=""/>
 * </printermodeloffsetml>
 *
 *
 * instance offsets: printer model, printer serial number, x offset, y offset
 *
 * <printerinstanceoffsetml>
 *  <printerinstance model="" xoffset="" yoffset=""/>
 *  <printerinstance model="" xoffset="" yoffset=""/>
 *  <printerinstance model="" xoffset="" yoffset=""/>
 * </printerinstanceoffsetml>
 *
 *
 */