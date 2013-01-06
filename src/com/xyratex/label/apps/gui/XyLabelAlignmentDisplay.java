//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyLabelAlignmentDisplay.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.apps.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.print.PrintException;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jaxen.JaxenException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.xyratex.label.config.XyVersion;
import com.xyratex.label.output.XyLabelBitmapProducer;
import com.xyratex.label.output.print.XyLabelPrintService;
import com.xyratex.label.output.print.XyLabelPrintServiceFactory;
import com.xyratex.label.template.XyLabelTemplateProducer;
import com.xyratex.label.template.exception.XyExceptionElementNotFoundInTemplate;

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
public class XyLabelAlignmentDisplay extends JPanel
{
	/**
	 *  R = Release L = Level G = date U = time
	 *  Values calculated by SCCS when file is checked out and compiled.
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyLabelAlignmentDisplay.java  %R%.%L%, %G% %U%";
	
	/**
	 * serialVersionUID required for this class because its necessary parent implements the Serializable interface.
	 * Its value is derived from SCCS Release and Level
	 * toLong() method won't throw an exception if non-numeric values are in the SCCS version data.
	 */
	public static final long serialVersionUID = NumberUtils.toLong("%R%") * XyVersion.sccsReleaseMultiplier 
	                                          + NumberUtils.toLong("%L%");
	
	
	/**
	 * log object used for logging activity in the form of String/character messages for debug, diagnostic and analysis purposes.
	 * The output destination for the log could be console output and/or file(s) depending on the user-definable overall configuration
	 * of logs in the system.
	 */
	private static Log log = LogFactory.getLog(XyLabelBitmapProducer.class);
	
	/**
	 * The default-x position of the label when displayed
	 */
	private int labelPositionX = 0;
	
	/**
	 * The default-y position of the label when displayed
	 */
	private int labelPositionY = 0;
	
	/**
	 * The user changeable x-offset position of the label
	 */
	private int xOffsetDots = 0;
	
	/**
	 * The user changeable y-offset position of the label
	 */
	private int yOffsetDots = 0;
  
	/**
	 * If the resolution is changed and this flag is set
	 * then the new offset values in dots per unit of measurement
	 * will be recalculated to new offset values so that
	 * the physical length of the offset is the same at the new
	 * resolution as it was before
	 */
  private boolean recalculateOn = true;
	
  /**
   * User changeable current resolution setting for the label
   */
  private float dotsPerMM = 0;
  
  /**
   * Holds the label template in SVG XML form
   */
  private Document labelTemplate = null;
  
  /**
   * holds the label template in bitmap form
   */
	private ByteArrayOutputStream bos = null;
	
	/**
	 * The background behind the label. When the label is offset, this colour
	 * has the purpose of making it easy to see how big the offset is
	 */
	private static final Color originalLabelPosition = Color.RED;
  
	/**
	 * Contructor for this class.
	 */
	public XyLabelAlignmentDisplay()
	{
		super();
		
		//this.addMouseMotionListener(arg0)
	}
	
	/**
	 * Get the user changeable x-offset position of the label
	 * 
	 * @return the x offset
	 */
	public int getXOffsetInPixels()
	{
		return xOffsetDots;
	}
	
	/**
	 * Get the user changeable y-offset position of the label
	 * 
	 * @return the y offset
	 */
	public int getYOffsetInPixels()
	{
		return yOffsetDots;
	}
	
	/**
	 * Set the user changeable x-offset position of the label
	 * 
	 * @return the x offset
	 */
	public void setXOffsetInPixels( int x )
	{
		xOffsetDots = x;
	}
	
	/**
	 * Set the user changeable y-offset position of the label
	 * 
	 * @return the y offset
	 */
	public void setYOffsetInPixels( int y )
	{
		yOffsetDots = y;
	}
	
	/**
	 * Work out the x offset in millimetres (at the given resolution)
	 * @return the x offset
	 */
  public float getXOffsetAsMM()
  {
  	// dotsPerUnitMeasurement 
  	// xOffsetDots 
  	
  	// Offset in mm = xOffsetDots / dotsPerUnitMeasurement
  	return xOffsetDots / dotsPerMM;
  }
  
	/**
	 * Work out the y offset in millimetres (at the given resolution)
	 * @return the y offset
	 */
  public float getYOffsetAsMM()
  {
  	// dotsPerUnitMeasurement 
  	// yOffsetDots 
  	
  	// Offset in mm = yOffsetDots / dotsPerUnitMeasurement
  	return yOffsetDots / dotsPerMM;
  }
  
  /**
   * Set the x and y offset
   * @param x
   * @param y
   */
  public void setOrigin( int  x, int y )
  {
  	xOffsetDots = x;
  	yOffsetDots = y;
  }
  
	/**
	 * If the resolution is changed and this flag is set
	 * then the new offset values in dots per unit of measurement
	 * will be recalculated to new offset values so that
	 * the physical length of the offset is the same at the new
	 * resolution as it was before
	 *
   * @param (nnput) recalculate flag
   */
  public void setRecalculateOrigin( boolean recalculate )
  {
  	recalculateOn = recalculate;
  }
  
  /**
   * Set the resolution in dots per millimetres
   * @param dots (input)
   */
  public void setDotsPerMM( float dots )
  {
  	float oldDotsPerMM = dotsPerMM;
		dotsPerMM = dots;

		if (recalculateOn)
		{
			float scale = dotsPerMM / oldDotsPerMM;

			xOffsetDots *= scale;
			yOffsetDots *= scale;
		}

		try
		{

			if (labelTemplate != null)
			{
				bos = new ByteArrayOutputStream();
				XyLabelBitmapProducer.generateBitmap(labelTemplate, bos, dotsPerMM);
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
  }
  
  /**
	 * Set the label template to be used.
	 * 
	 * @param doc
	 *          (input)
	 */
  public void setLabelTemplate( Document doc ) throws IOException, TranscoderException, XyExceptionElementNotFoundInTemplate, JaxenException
  {
  	labelTemplate = doc;
  	
		Element printableRegionRectangle = doc.getElementById(XyLabelTemplateProducer.XYPRINTABLE_REGION);

		if (printableRegionRectangle != null)
		{
			printableRegionRectangle.setAttribute("style", "fill:none;stroke:black;stroke-width:0.2");

			bos = new ByteArrayOutputStream();

			XyLabelBitmapProducer.generateBitmap(labelTemplate, bos, dotsPerMM);
		}
		else
		{
  		final String noLabelOutline = "Can't find the label outline (" + XyLabelTemplateProducer.XYPRINTABLE_REGION + ")";
  		
  		JOptionPane.showMessageDialog(this, noLabelOutline);

  		log.trace( noLabelOutline );
		}
  }
  
  
  /**
   * Print the label template, applying the offset
   * @param selectedPrinter (input) the printer to print to
   * @throws PrintException
   * @throws IOException
   */
  public void print( String selectedPrinter ) throws PrintException, IOException
  {
  	// if a bitmap has been generated then it can be printed
  	if ( bos != null )
  	{

			
		  XyLabelPrintService printService = XyLabelPrintServiceFactory.getPrintService( selectedPrinter );
		  
		  printService.setOrigin( "" + 	xOffsetDots + "," + yOffsetDots );
		  
			byte[] bitmapAsByteArray = bos.toByteArray();
		  
		  InputStream is = new ByteArrayInputStream(bitmapAsByteArray);
		  
		  printService.printBitmap(is);
  	}
  }
  
  /**
   * Get the list of printers attached to the host PC running this application.
   * @return list of printers
   */
  public Vector<String> getPrinters()
  {
  	return XyLabelPrintServiceFactory.getPrinters();
  }
  
  /**
   * Paint this component, for example called by the Java graphical user interface 
   * and when an update to the appearance of this component is required.
   */
  public void paintComponent(Graphics g) 
  {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D)g;
      
    if ( bos != null )
    {
      InputStream is = new ByteArrayInputStream(bos.toByteArray());

			BufferedImage pngAsBufferedImage = null;
			try
			{
				pngAsBufferedImage = ImageIO.read(is);
			}
      catch (Exception ex) 
      {
        ex.printStackTrace();
      }
			
	    g2.setColor(originalLabelPosition);
	    g2.fillRect(labelPositionX, labelPositionY, pngAsBufferedImage.getWidth(), pngAsBufferedImage.getHeight()) ;
	    
			g2.drawImage(pngAsBufferedImage, labelPositionX + xOffsetDots, labelPositionY + yOffsetDots, this);
    }
  }
}
