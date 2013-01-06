//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyLabelAlignmentDisplay.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.apps.gui.components;


import java.awt.Graphics;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Observable;

import javax.swing.JOptionPane;


import org.apache.batik.transcoder.TranscoderException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jaxen.JaxenException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.xyratex.label.output.XyLabelBitmapProducer;
import com.xyratex.label.output.print.XyLabelPrintService;
import com.xyratex.label.output.print.XyLabelPrintServiceFactory;
import com.xyratex.label.output.print.offset.XyOffsetSum;

import com.xyratex.label.template.XyLabelTemplateProducer;
import com.xyratex.label.template.exception.XyExceptionElementNotFoundInTemplate;

import com.xyratex.label.apps.gui.XyLabelPrintConfigApp;
import com.xyratex.svg.rasterize.XySVGtoBitmapProducer;

/**
 * <p>Used by an application to display a label template in a Graphical User Interface.</p>
 * 
 * <p>This class knows about the label template SVG XML and handles the rendering of this
 * as a bitmap for display in the graphical user interface of the containing application that this 
 * class will be used in.</p>
 * 
 * <p>It knows about the offset values that the user has applied to the label.</p>
 * 
 * <p>It handles the printing of the label, when requested by the user, via a 
 * containing application. Displaying the label as a bitmap and printing it both
 * call upon the same library code, resulting in a bitmap than can be reused for
 * these 2 activities.</p>
 * 
 * @author rdavis
 *
 */
public class XyLabelTemplateImageLogic // extends Observable // implements Observer
{	
	/**
	 * log object used for logging activity in the form of String/character messages for debug, diagnostic and analysis purposes.
	 * The output destination for the log could be console output and/or file(s) depending on the user-definable overall configuration
	 * of logs in the system.
	 */
	private static Log log = LogFactory.getLog(XyLabelTemplateImageLogic.class);
	
	private XyOffsetSum xyOffsetSum = null;
  
  private float mmPerPixel = Float.NaN;
  
  private XyLabelPrintConfigApp parentGui = null;
  
  /**
   * Holds the label template in SVG XML form
   */
  private Document labelTemplate = null;
  
  
  

  private XyLabelTemplateImageJPanel imageUI = null;
	
          /**
   * holds the label template in bitmap form
   */
	private ByteArrayOutputStream bos = null;
  

	
	
  public XyLabelTemplateImageLogic( XyLabelPrintConfigApp parent )
  {
  	parentGui = parent;
		bos = new ByteArrayOutputStream();
		

  }
  
  public void setImageUI( XyLabelTemplateImageJPanel ui )
  {
      imageUI = ui;
  }
	
  public void setOffsetSum( XyOffsetSum offsetSum )
  {
  	xyOffsetSum = offsetSum;
  }
  
  public XyOffsetSum getOffsetSum()
  {
  	return xyOffsetSum;
  }
  

  public void offsetHasChanged()
  {
      log.trace("offsetHasChanged");
      imageUI.repaint();
  }
	
	public void resolutionChanged( String res ) 
	  throws IOException,
	         XyExceptionElementNotFoundInTemplate,
	         TranscoderException,
	         JaxenException
	{
            log.trace( "resolution changed:" + res );
            
		mmPerPixel 
		  = XySVGtoBitmapProducer.getMillimetresPerPixelFromDotsPerUnitLength( res );
		
                bos.reset();
                
		XyLabelBitmapProducer.generateBitmap(labelTemplate, bos, mmPerPixel);
		imageUI.setImage( bos );
                imageUI.repaint();
	}
	
	
	public void templateChanged( Document doc )
	  throws XyExceptionElementNotFoundInTemplate,
	         IOException,
	         TranscoderException,
	         JaxenException
	{
  	labelTemplate = doc;
  	
		Element printableRegionRectangle = doc.getElementById(XyLabelTemplateProducer.XYPRINTABLE_REGION);

		if (printableRegionRectangle != null)
		{
                     log.trace("found printable region");
                    
			printableRegionRectangle.setAttribute("style", "fill:none;stroke:black;stroke-width:0.2");
                        
                        bos.reset();
                        
                        XyLabelBitmapProducer.generateBitmap(labelTemplate, bos, mmPerPixel );
			imageUI.setImage( bos );
                 // imageUI.repaint();
		}
		else
		{
  		final String noLabelOutline = "Can't find the label outline (" + XyLabelTemplateProducer.XYPRINTABLE_REGION + ")";
  		
  	  JOptionPane.showMessageDialog( parentGui.getJFrame(), noLabelOutline);

  		log.trace( noLabelOutline );
		}
	}
	



	
	// JSVGCanvas ???
	
  /* (non-Javadoc)
   * @see com.xyratex.label.apps.gui.components.XyOffsetCapableImage#setDotsPerMM(float)
   */

  
  public void print( String printerId )
  {
      log.trace( "print - with offset: " + xyOffsetSum.getSumOffsetXAsPx() + "," + xyOffsetSum.getSumOffsetYAsPx() );
      
      try
      {
		XyLabelPrintService printService = XyLabelPrintServiceFactory.getPrintService( printerId );
	  
		printService.setOrigin( "" + xyOffsetSum.getSumOffsetXAsPx() + "," + xyOffsetSum.getSumOffsetYAsPx());
		
	  byte[] bitmapAsByteArray = bos.toByteArray();
		  
		InputStream is = new ByteArrayInputStream(bitmapAsByteArray);
		  
		printService.printBitmap(is);
      }
      catch( Exception e )
      {
          parentGui.error( e );
          //notifyObservers( e );
      }
  }
	


}
