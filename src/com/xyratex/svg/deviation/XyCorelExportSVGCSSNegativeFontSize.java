//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyCorelExportSVGCSSNegativeFontSize.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.svg.deviation;

import org.w3c.dom.Document;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.batik.bridge.BridgeContext;

import org.apache.batik.css.engine.SVGCSSEngine;
import org.apache.batik.css.engine.StyleDeclaration;
import org.apache.batik.css.engine.value.Value;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.CSSConstants;

import com.xyratex.svg.css.XyAbstractConditionalCSSElementListProducer;

/**
 * <p>A specific class to address one of the non-compliances/standards deviations of the SVG
 * files exported from CorelDRAW Graphics Suite 12 (unpatched). We don't know yet if any available
 * patches fix the issue - but this class provides the workaround.</p>
 * 
 * <p>The non-compliant/deviation is that CorelDRAW Graphics Suite 12 (without patches) exports SVG 
 * with CSS that contains negative font size.
 * </p>
 * 
 * <p>This class inherits from XyAbstractConditionalCSSElementListProducer
 * to get the generic CSS style-value affected elements selection and this derived class
 * provides the criteria of negative font-size so that those elements are selected.</p>
 * 
 * @see com.xyratex.svg.css.XyAbstractConditionalCSSElementListProducer
 * 
 * <p>See also the batik javadoc documentation: <a href="http://xmlgraphics.apache.org/batik/javadoc/">http://xmlgraphics.apache.org/batik/javadoc/</a></p>
 * 
 * */
public class XyCorelExportSVGCSSNegativeFontSize extends XyAbstractConditionalCSSElementListProducer {

	/**
	 *  <p>R = Release L = Level G = date U = time</p>
	 *  <p>Values calculated by SCCS when file is checked out and compiled.</p>
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyCorelExportSVGCSSNegativeFontSize.java  %R%.%L%, %G% %U%";
	
	
/**
 * The index value representing font size. This is used internally when checking a CSS style to see if it is font size.
 * A table of possible styles is built by batik at run time. 
 * 
 * Longer explanation: As part of parsing an SVG document,
 * batik assembles objects representing all of the CSS styles used. The style type (e.g. font-size)
 * for an CSS style object is represented by an index value corresponding to the aforementioned table.
 * In order for XyCSSCorelDeviationNegativeFontSize to identify a font-size, it therefore has to
 * workout at runtime what the value of font-size is, by looking up the value in this table using a 
 * constant CSSConstants.CSS_FONT_SIZE_PROPERTY as the query value.
 * 
 * Once we have this value for the font-size CSS style attribute, we can then check each batik CSS style object
 * to see if it is about font-size and then check to see if the value is negative.
 * 
 */
private int propertyIndex = 0;

private static Log log = LogFactory.getLog(XyCorelExportSVGCSSNegativeFontSize.class);
	
/** 
 * <p>Instantiate XyCSSCorelDeviationNegativeFontSize</p>
*
* @param doc (input parameter) the original SVG W3C Document containing the non-compliant SVG CSS
* @param ctx (input parameter) BridgeContext batik object linking W3C DOM elements with their graphical W3C SVG/batik equivalents - vital for the region filtering system to work
* @param gn (input parameter) GraphicsNode to start with - only it and its children are considered to see if 
* they have the non-compliance issue. usually the GraphicsNode supplied is the RootGraphicsNode - equivalent to the svg element so the entire document is considered. But asking for a graphics node enables restricting to parts of the document
*
*/
public XyCorelExportSVGCSSNegativeFontSize( final Document doc, final BridgeContext ctx, final GraphicsNode gn )
{
  super( doc, ctx, gn );

  log.trace(sccsid);
	
	String propertyName = CSSConstants.CSS_FONT_SIZE_PROPERTY;
	
	for ( int svgValueManagersIndex = 0; svgValueManagersIndex < (SVGCSSEngine.SVG_VALUE_MANAGERS).length; svgValueManagersIndex++ )
	{
		if ( SVGCSSEngine.SVG_VALUE_MANAGERS[svgValueManagersIndex].getPropertyName().equals(propertyName) )
	  {
		  log.trace( "found font size manager " + svgValueManagersIndex );
		  
		  propertyIndex = svgValueManagersIndex;
	  }
	}
}

/**
 * 
 * <p>Criteria for selecting CSS style-value combination</p>
 * 
 * <p>Checks to see if a style is a negative font size value. 
 * Tests whether a style is "font-size" and if the accompanying value is negative</p>
 * 
 * <p>This enables us to deal with the negative font size CSS that Corel exports in the SVG file</p>
 * 
 * @param sd (input parameter) a StyleDeclaration object representing a CSS style definition within the SVG document
 * @param sdindex (input parameter) an index integer: the style can contain 1 or more attributes and the index is used to select each one 
 * 
 * @return boolean to say if style-value combination found
 */
protected boolean condition(final StyleDeclaration sd, final int sdindex )
{
	Value val = sd.getValue(sdindex);
	
  if ( sd.getIndex(sdindex) == propertyIndex )
  {
	   log.trace( "found font size" );
	   
	   float aFloatValue = Float.valueOf(val.getCssText());
	   
	   if ( aFloatValue < 0.0F )
	   {
	  	 return true;
	   }
  }
  
  return false;
}
	
}
