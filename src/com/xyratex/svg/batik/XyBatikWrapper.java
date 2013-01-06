//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyBatikWrapper.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.svg.batik;

import org.w3c.dom.Document;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.gvt.RootGraphicsNode;


/**
 * <p>This is a wrapper for the 3rd party free open source batik library from Apache for manipulating SVG</p>
 * 
 * <p>It initialises the batik library around an SVG Document (e.g. a label drawing), so that the graphical properties
 * of elements within the document can be accessed</p>
 * 
 * <p>A generic component, useful for any SVG manipulation</p>
 * 
 * <p>See also the batik javadoc documentation: <a href="http://xmlgraphics.apache.org/batik/javadoc/">http://xmlgraphics.apache.org/batik/javadoc/</a></p>
 * 
 * @author Rob Davis
 */
public class XyBatikWrapper 
{
	/**
	 *  R = Release L = Level G = date U = time
	 *  Values calculated by SCCS when file is checked out and compiled.
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyBatikWrapper.java  %R%.%L%, %G% %U%";
	
	/**
	 * log object used for logging activity in the form of String/character messages for debug, diagnostic and analysis purposes.
	 * The output destination for the log could be console output and/or file(s) depending on the user-definable overall configuration
	 * of logs in the system.
	 */
	private Log log = LogFactory.getLog(XyBatikWrapper.class);
	
	/**
	 * The W3C SVG XML Document that will be operated on
	 */
	protected Document doc = null;
	
  /**
   * see <a href="xmlgraphics.apache.org/batik/javadoc/org/apache/batik/bridge/UserAgent.html">UserAgent</a>
   */
	protected UserAgent userAgent = null;
	
	/**
	 * see <a href="xmlgraphics.apache.org/batik/javadoc/org/apache/batik/bridge/DocumentLoader.html">DocumentLoader</a>
	 */
	protected DocumentLoader loader = null;
	
	/**
	 * see <a href="xmlgraphics.apache.org/batik/javadoc/org/apache/batik/bridge/BridgeContext.html">BridgeContext</a>
	 */
	protected BridgeContext ctx = null;
	
	/**
	 * see <a href="xmlgraphics.apache.org/batik/javadoc/org/apache/batik/bridge/GVTBuilder.html">GVTBuilder</a>
	 */
	protected GVTBuilder builder = null;
	
	/**
	 * see <a href="xmlgraphics.apache.org/batik/javadoc/org/apache/batik/gvt/RootGraphicsNode.html">RootGraphicsNode<a/>
	 */
	protected RootGraphicsNode rootGN = null;

  /** <p>Initialises the graphical properties of a given SVG file</p>
   * 
   * @param entireLabelSVGw3cDoc (input parameter) the SVG file
   */
	public XyBatikWrapper(final Document entireLabelSVGw3cDoc)
	{
		log.trace("sccsid");
		
		doc = entireLabelSVGw3cDoc; // we do this rather than use the parameter because we may want to use this member variable to retrieve the svg later for other things
		userAgent = new UserAgentAdapter();
		loader = new DocumentLoader(userAgent);
		ctx = new BridgeContext(userAgent, loader);
		ctx.setDynamicState(BridgeContext.DYNAMIC);
		builder = new GVTBuilder();
		rootGN    = (RootGraphicsNode)builder.build(ctx, doc);
	}

  /**
   *  <p>gets the BridgeContext associated with the SVG</p>
   * 
   * <p>A BridgeContext is a batik object that provides a link between the 
   * W3C DOM Elements in a SVG and their graphical properties defined in terms of various W3C SVG compliant batik objects and methods.
   * In other words it enables you to determine the DOM W3C elements involved in shape(s) or text in a drawing
   * and vice-versa. 
   * </p>
   * 
   * @return BridgeContext
	 */
  public BridgeContext getBridgeContext()
  {
	  return ctx;
  }

  /**
   *  <p>Gets the root of the SVG document - the svg tag - expresed in terms of a batik GraphicsNode.</p>
   *
   *<p>
   * This is used as a starting point for iterating through all the graphical elements of a document.
   * Being the root graphics node means that the iteration should cover all of the children -and therefore
   * the whole of the document. It therefore enables the entire SVG document to be processed for whatever
   * reason you need.
   *</p>
   *
   *@return RootGraphicsNode
   */
  public RootGraphicsNode getRootGraphicsNode()
  {
	  return rootGN;
  }
}


