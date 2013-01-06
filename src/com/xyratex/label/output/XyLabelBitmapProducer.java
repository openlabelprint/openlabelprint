//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyLabelBitmapProducer.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.label.output;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.OutputStream;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xyratex.xml.io.XyDocumentProducer;
import com.xyratex.xml.io.XyXMLDOMDocumentToStringService;
import com.xyratex.xml.utils.XyXMLDOMSearchUtils;

import com.xyratex.label.config.XyCharacterEncoding;
import com.xyratex.label.tags.XyLabelTagRegistry;
import com.xyratex.label.template.XyLabelTemplateProducer;
import com.xyratex.label.template.exception.XyExceptionElementNotFoundInTemplate;

import com.xyratex.svg.batik.XyBatikWrapper;
import com.xyratex.svg.batik.util.XyShapeProducer;
import com.xyratex.svg.rasterize.XySVGtoBitmapProducer;
import com.xyratex.svg.utils.XySVGInsert;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.jaxen.JaxenException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;



/**
 * <p>Generates the PNG bitmap from SVG.</p>
 * 
 * <p>This is a stateless singleton class with stateless methods, therefore all methods are static.
 * This provides the convenience of not needing to instantiate an instance of this class
 * to use its functionality.</p>
 *
 * @author Rob Davis
 *
 */
public class XyLabelBitmapProducer
{
	/**
	 *  %R% = Release %L% = Level %G% = date %U% = time
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyLabelBitmapProducer.java  %R%.%L%, %G% %U%";

	/**
	 * log object used for logging activity in the form of String/character messages for debug, diagnostic and analysis purposes.
	 * The output destination for the log could be console output and/or file(s) depending on the user-definable overall configuration
	 * of logs in the system.
	 */
	private static Log log = LogFactory.getLog(XyLabelBitmapProducer.class);
	
	/**
   * <p>Generates the bitmap from SVG.</p>
   * 
   * <p>
   * This is a convenience method that means that the client calling only needs to know about
	 * the SVG as a File type rather than having to know how to derive a W3C DOM Document.
   * Given that XyLabelBitmapProducer already has to know about W3C DOM Documents, the
   * knowledge can be kept here rather than imposing it on a client that wouldn't require
   * it for anything else. 
   * </p>
   * 
	 * @param svgFile (input parameter) the input File - the populated label in SVG format, a .svg file
	 * @param bitmapOutput (input parameter) the output File as a PNG, a .png file 
	 * @param resolution (input parameter) the resolution to render at, 
	 *   e.g. dots per inch (dpi) of the the PNG, e.g. 203, 300, 600 etc. as a String
	 *   or dots per millimetre (dpmm). The value is supplied as a string because all precisions can be supported flexibly.
	 */
	public static void generateBitmap(final File svgFile, final File bitmapOutput, final String resolution ) 
	  throws IllegalAccessException,
	         InstantiationException,
	         FileNotFoundException,
	         ClassNotFoundException,
	         TranscoderException,
	         IOException,
	         XyExceptionElementNotFoundInTemplate,
	         JaxenException
	{
		Document doc = XyDocumentProducer.getDocument( "svg", svgFile );
		
		generateBitmap( doc, bitmapOutput, resolution );
	}
  
	
  /**
   * <p>Generates the bitmap from SVG.</p>
   * 
   * <p>
	 * Convenience method that allows a client, which already has the SVG in W3C DOM Document form,
	 * to pass this in. This therefore enables performance efficiency as the SVG does not have to be reparsed
	 * from an input File or String, for example.
	 * </p>
   * 
	 * @param doc (input parameter) the input File - the SVG format, a .svg file
	 * @param bitmapOutput (input parameter) the File as a bitmap, a .png file 
	 * @param resolution (input parameter) the resolution to render at,
	 *   e.g. dots per inch (dpi) of the the PNG, e.g. 203, 300, 600 etc. as a String
	 *   or dots per millimetre (dpmm). The value is supplied as a string because all precisions 
	 *   can be supported flexibly.
	 */
  public static void generateBitmap(final Document doc, final File bitmapOutput, final String resolution )
    throws FileNotFoundException,
           IOException,
           TranscoderException,
           XyExceptionElementNotFoundInTemplate,
           JaxenException
  {
    OutputStream ostream = new FileOutputStream(bitmapOutput);
    
    generateBitmap( doc,  ostream,  resolution );
  }
  
  /**
   * <p>Generates the bitmap from SVG.</p>
   * 
   * <p>Convenience method that provides the generated bitmap as an OutputStream
   * which allows the client calling code to reuse the same bitmap in multiple ways without
   * the overhead of rasterizing each time, for example for printing, display and writing to a file.
   * </p>
   *
   * @param doc (input parameter) the label template document as a W3C DOM Document
   * @param ostream (input/output parameter) the empty OutputStream, created by the calling code, that will be populated with the bitmap
   * @param res (input) the resolution (e.g. dpi, dpmm)
   */
  public static void generateBitmap(final Document doc, OutputStream ostream, final String res ) 
    throws TranscoderException,
           IOException,
           XyExceptionElementNotFoundInTemplate,
           JaxenException
  {
    generateBitmap( doc, ostream, XySVGtoBitmapProducer.getMillimetresPerPixelFromDotsPerUnitLength(res) );
  }
  
  /**
   * <p>Generates the bitmap from SVG.</p>
   * 
   * <p>Generates a bitmap from a rectangular region of a label template Document where
   * the region is defined by the rect SVG element with id defined in XyLabelTemplateProducer.XYPRINTABLE_REGION.
   * This rect element is inserted by the XyLabelTemplateProducer label template generator.</p>
   * 
   * @param doc (input parameter) the label template document as a W3C DOM Document
   * @param ostream (input/output parameter) the empty OutputStream, created by the calling code, that will be populated with the bitmap
   * @param millimetresPerPixel (input) the resolution in millimetres per pixel
   * 
   * @throws XyExceptionElementNotFoundInTemplate if such <rect> is not defined.
   */
  public static void generateBitmap( 
  		final Document doc,
  		OutputStream ostream,
  		final float millimetresPerPixel ) 
    throws TranscoderException,
           IOException,
           XyExceptionElementNotFoundInTemplate,
           JaxenException
  {
  	// log the version of the class
  	log.trace(sccsid);
  
    float mmWidthAsFloat = 0.0F;
    float mmHeightAsFloat = 0.0F;
    float mmPosXAsFloat = 0.0F;
    float mmPosYAsFloat = 0.0F;
  	
    Element gContainerNode = doc.getElementById(XyLabelTagRegistry.XY_TRANSFORM);
    
    if ( gContainerNode != null)
    {
    	Element transformedPrintableRegion = XyXMLDOMSearchUtils.getElementByPartialAttributeValueMatch("XY_POSTTRANSFORMEDPRINTABLEREGION", "id", "rect", doc );
    	
    	if ( transformedPrintableRegion != null )
    	{
    		String transform = XyLabelTagRegistry.getTransformForOrientation( transformedPrintableRegion.getAttribute("id") );
    		
    		gContainerNode.setAttribute("transform", transform );

    		String x = transformedPrintableRegion.getAttribute("x");
    		String y = transformedPrintableRegion.getAttribute("y");
    		String width = transformedPrintableRegion.getAttribute("width");
    		String height = transformedPrintableRegion.getAttribute("height");
    		
    		String viewBox = x + " " + y + " " +width + " " + height;
    		
    		Element svgElement = doc.getDocumentElement();
    		
    		svgElement.setAttribute("viewBox", viewBox );
    		svgElement.setAttribute("width", width);
    		svgElement.setAttribute("height", height);
    		
        mmWidthAsFloat = Float.valueOf( width );
        mmHeightAsFloat = Float.valueOf( height );
	
        mmPosXAsFloat = Float.valueOf( x );
        mmPosYAsFloat = Float.valueOf( y );
      
        XySVGtoBitmapProducer.generateBitmap( 
          doc,
          ostream,
          millimetresPerPixel,
          mmPosXAsFloat,
          mmPosYAsFloat,
          mmWidthAsFloat,
          mmHeightAsFloat	
        );
      }
    }
    else
    {
      Element printableRegionRectangle = doc.getElementById(XyLabelTemplateProducer.XYPRINTABLE_REGION);
		
		  // get the new co-ordinates and dimensions
		
		  // plug them in here
		
		  if ( printableRegionRectangle != null )
		  {
        mmWidthAsFloat = Float.valueOf( printableRegionRectangle.getAttribute("width") );
        mmHeightAsFloat = Float.valueOf( printableRegionRectangle.getAttribute("height") );
		
        mmPosXAsFloat = Float.valueOf(printableRegionRectangle.getAttribute("x"));
        mmPosYAsFloat = Float.valueOf(printableRegionRectangle.getAttribute("y"));
      
        XySVGtoBitmapProducer.generateBitmap( 
          doc,
          ostream,
          millimetresPerPixel,
          mmPosXAsFloat,
          mmPosYAsFloat,
          mmWidthAsFloat,
          mmHeightAsFloat	
        );
		  }
		  else
		  {
			  throw new XyExceptionElementNotFoundInTemplate( "Element with " + XyLabelTemplateProducer.XYPRINTABLE_REGION + " not defined in document");
		  }
	  }
  }
}
