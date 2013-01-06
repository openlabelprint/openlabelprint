//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XySVGClipFilter.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.svg.clip;

import java.io.StringReader;
import java.util.Vector;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.ext.awt.geom.Polygon2D;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.parser.AWTPolygonProducer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jaxen.JaxenException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGLocatable;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.traversal.NodeFilter;

import com.xyratex.svg.batik.util.XyShapeProducer;
import com.xyratex.svg.batik.util.XyCombinedTransformProducer;
import com.xyratex.xml.utils.XyXMLDOMSearchUtils;

/**
 * <p>The filter  used to determine if a SVG XML element is inside or outside a region in the SVG drawing.</p>
 * 
 * <p>Part of a generic Xyratex library for manipulating SVG.</p>
 * 
 * <p>An example application is in labelling
 * whereby a label drawing specification supplied from industrial designers can be taken and just the label
 * itself can be extracted and used for generating printed labels directly on the line. The industrial designer
 * can tag a region to denote the label outline and this filter can then use this to work out what is part of
 * the label (i.e. inside the label) and what is outside - i.e. ancillary notation and overall document headings,
 * designer names, document numbers etc.)</p>
 * 
 * <p>This filter is an implementation of W3C DOM NodeFilter interface so that it can be used
 * in a DOM traversal of the SVG document, which performs the selection of elements matching the criteria specified by the filter.
 * The DOM traversal of the SVG is performed by the XySVGClipper</p>
 * 
 * <p>NOTE: this XySVGClipper class and the XySVGClipFilter may be obsoleted as instantiation of the batik
 * already provides a document tree which could be traversed instead, rather than using the W3C DOM tree.
 * Since the filtering requires batik this makes sense. The savings will be CPU cycles at least and at best
 * memory footprint as fewer data structures are required</p>
 * 
 * @see XySVGClipper
 * 
 * <p>See also the batik javadoc documentation: <a href="http://xmlgraphics.apache.org/batik/javadoc/">http://xmlgraphics.apache.org/batik/javadoc/</a></p>
 * 
 * @author Rob Davis
 */
public class XySVGClipFilter implements NodeFilter
{
	/**
	 *  <p>R = Release L = Level G = date U = time</p>
	 *  <p>Values calculated by SCCS when file is checked out and compiled.</p>
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XySVGClipFilter.java  %R%.%L%, %G% %U%";
	
	private Shape clipOutline = null;
	private String elementIdOfClip = "";
	private Vector<Element> preExcludedElements = null;
	private Vector<Element> preIncludedElements = null;
	private Element clipElement = null;
	private BridgeContext ctx = null;
	private int clipperMode = XySVGClipper.CLIPPER_REMOVE_UNSET;
	private static short filterModeContains = NodeFilter.FILTER_SKIP;
	private static short filterModeDoesNotContain = NodeFilter.FILTER_SKIP;
	
	private static Log log = LogFactory.getLog(XySVGClipFilter.class);
	
  /** <p>Initialise the filter</p>
   *
   * @param  anElementIdOfClip (input parameter) as in id="elementid" String of the SVG element within the SVG (i.e. entireLabelSVGw3cDoc - see below) that defines the region
   * @param  entireLabelSVGw3cDoc (input/output parameter) the entire SVG document to traverse and apply the filter to. this is modified by this function
   * @param  mode (input parameter) the mode - i.e. are we looking for elements outside or inside the region (either CLIPPER_REMOVE_OUTSIDE or CLIPPER_REMOVE_INSIDE)
   * @param  bridgeContext (input parameter) the batik BridgeContext associated with the SVG document, to enable the mapping between the SVG as a collection of W3C DOM Elements and their graphical equivalents expressed as batik objects
   * 
	 */
	public XySVGClipFilter( 
		final String anElementIdOfClip,
		Document entireLabelSVGw3cDoc,
		final int mode,
		final BridgeContext bridgeContext )
	{
		log.trace(sccsid);
		
		// re-enabling this approach to getting the element - a specific element
		// this seems to make sense over the partial match
		// so i'm not sure why we originally disabled this
		//
		// because we needed to deal with orientation
    //
	  //clipElement = entireLabelSVGw3cDoc.getElementById(anElementIdOfClip);
		
		final String anyElement = "*"; 
		//
		// re-enabled - getting the element by partial id string match
		// as suspecting that it fails with null as it returns more than one
		// element
		// - so not really sure why it is being used in the first place
		try
		{
		  clipElement = XyXMLDOMSearchUtils.getElementByPartialAttributeValueMatch( anElementIdOfClip, "id", anyElement, entireLabelSVGw3cDoc );
	  }
		catch( JaxenException jaxenException )
		{
		  log.trace( jaxenException.getMessage() + jaxenException.getStackTrace() );
		}
		
	  clipOutline = XyShapeProducer.createShape( clipElement, bridgeContext );
	  
	  elementIdOfClip = anElementIdOfClip;
		preExcludedElements = new Vector<Element>();
		preIncludedElements = new Vector<Element>();
		
		ctx = bridgeContext;
		
		clipperMode = mode;
		
		if ( clipperMode == XySVGClipper.CLIPPER_REMOVE_INSIDE )
		{
			filterModeContains = FILTER_ACCEPT; 
			filterModeDoesNotContain = FILTER_REJECT;
		}
		else // by default (i.e. XySVGClipper.CLIPPER_REMOVE_OUTSIDE )
		{
		  // we reject a shape that is *contained* within the clip shape
			// - being rejected means that the shape is not on the list of
			// shapes to be removed. this means that shapes inside the clip shape
			// are retained while those outside are not - they appear on the removal list
			filterModeContains = FILTER_REJECT; 
			filterModeDoesNotContain = FILTER_ACCEPT;
		}
	}
	
 /**
  *  <p>Initialise the filter</p>
  *
  * <p>an alternative initialisation that does not use an elementid but a Shape instead to define a region
  *
  * @param  aClipOutline (input parameter) the shape defining the region
  * @param  mode (input parameter) are we looking for elements outside or inside the region (either CLIPPER_REMOVE_OUTSIDE or CLIPPER_REMOVE_INSIDE)
  * @param  ctx (input parameter) the batik BridgeContext associated with the SVG document, to enable the mapping between the SVG as a collection of W3C DOM Elements and their graphical equivalents expressed as batik objects
  * 
	*/
	public XySVGClipFilter( final Shape aClipOutline, final int mode, final BridgeContext ctx )
	{
		log.trace(sccsid);
		clipOutline = aClipOutline;
		preExcludedElements = new Vector<Element>();
	}
	
  /** 
   * <p>Add elements that will be definitely be filtered out before the proper region-based filtering starts.</p>
   * 
   * <p>This is useful if there are elements not required that would normally be included by the region-based filtering.
   * An example of this is a SVG document exported from a drawing package that contains elements that use non-compliant CSS.
   * CorelDRAW Graphics Suite 12 (without patches) exports SVG with CSS that contains negative font size.
   * By filtering the affected objects first before batik sees them avoids an Exception being raised.
   * Fortunately the affected objects in this case would have been filtered out by the region-based filtering 
   * anyway if batik could tolerate their non-compliance.
   * </p>
   *
   * @param  somePreExcludedElements (input parameter)
	 */
	public void addPreExcludedElements( Vector<Element> somePreExcludedElements )
	{
		Vector<Element> preExcluded = somePreExcludedElements;
		
		if ( preExcluded == null )
		{
			preExcluded = new Vector<Element>();
		}
		
		preExcludedElements.addAll( preExcluded );
	}
	
  /** 
   * <p>Add elements that will be included bypassing the region-based filtering.</p>
   * 
   * <p>This allows some elements to be overlooked by the filtering process</p>
   * 
   * <p>For example, we may want to keep the actual element that defines the filter region.
   * Sometimes, the filter removes the region defining element from the SVG as well when actually
   * we still need this. This issue is to do with the mechanisms of floating point comparison which
   * we haven't quite got to the bottom of yet. This method circumvents that issue. Although it should
   * have more legitimate uses!</p>
   *
   * @param  somePreIncludedElements (input parameter)
	 */
	public void addPreIncludedElements( final Vector<Element> somePreIncludedElements )
	{
		Vector<Element> preIncluded = somePreIncludedElements;
		
		if ( preIncluded == null )
		{
			preIncluded = new Vector<Element>();
		}
		
		preIncludedElements.addAll( preIncluded );
	}
	
  /** 
   * <p>This is the implementation of the acceptNode method defined by the W3C DOM traversal</p>
   * 
   * <p>It is called by the DOM traversal to determine if an element is to be selected or not, depending
   * on the criteria defined at initialisation - see this class's constructors</p>
   * 
   * FILTER_ACCEPT - the node is outside the label
   * FILTER_SKIP - the children of this node will be considered
   * 
   * @param n (input parameter) the Node to be considered for selection or not
   * @return an enumerate to say whether or not the element is selected i.e. FILTER_ACCEPT if yes, FILTER_SKIP if not but its children must be consider or FILTER_REJECT if definitely not
   * 
	 */
	public short acceptNode(final Node n)
	{
		short filter = FILTER_REJECT;

		String listOfCoordsString = null;
		Shape shape = null;

		for (int i = 0; i < preExcludedElements.size(); i++)
		{
			if (preExcludedElements.get(i).equals(n))
			{
				return FILTER_ACCEPT; // always accept as elements to be clipped/removed
			}
		}
		
		for (int i = 0; i < preIncludedElements.size(); i++)
		{
		  Element anElement = preIncludedElements.get(i);
		  
		  String includedElementName = anElement.getTagName();
		  
			//if (preIncludedElements.get(i).equals(n))
		  if ( anElement.equals(n) )
			{
				return FILTER_REJECT; // always reject as elements to be kept in document
			}
		  else
		  {
		    if ( XyXMLDOMSearchUtils.findAContainerNodeByTag( n, includedElementName, "svg" ) != null )  
		    {
		      return FILTER_REJECT;
		    }
		  }
		}

    // Any exceptions have to be caught with this method because
		// the acceptNode method signature in the NodeFilter interface that this method 
		// implements does not allow Exceptions
		try
		{
			if (n.getNodeType() == Node.ELEMENT_NODE)
			{
				Element element = (Element) n;

				if (element.getTagName().equals("text"))
				{
					log.trace( "element.getTextContent() = " + element.getTextContent() );

					// shape = XyTextBoundingBoxProducer.createShape( element );
					/*
					 * 
					 * float x = (new Float(element.getAttribute("x"))).floatValue();
					 * float y = (new Float(element.getAttribute("y"))).floatValue();
					 *  // if the text is not within the label then we accept the text
					 * element as being an outsider if (!clipOutline.contains(x, y))
					 * filter = FILTER_ACCEPT;
					 */

					shape = XyShapeProducer.getShapeFromBoundingBoxOfText( element );
					
					if ( shape ==  null )
					{
						// no bounding box likely means there is no text in the text element so it should be 
						// white space, BUT if there is a style sheet with a non blank background
						// then the element *could* be a block which we do not want to see
						if ( element.hasAttribute("class") ) { element.removeAttribute("class"); }
						
						// also we consider this element to not be within the region specified for the filter
						filter = filterModeDoesNotContain;
					}
					else // there is a bounding box for the text element
					{
						filter = filterShape(shape, XyCombinedTransformProducer
						    .getCurrentTransform(element));
					}
				}
				else
				{
					if (element.getTagName().equals("path"))
					{
						listOfCoordsString = element.getAttribute("d");

						shape = AWTPathProducer.createShape(new StringReader(
						    listOfCoordsString), PathIterator.WIND_EVEN_ODD);
						filter = filterShape(shape, XyCombinedTransformProducer
						    .getCurrentTransform(element));
					}
					else
					{
						if (element.getTagName().equals("polygon"))
						{
							listOfCoordsString = element.getAttribute("points");

							shape = AWTPolygonProducer.createShape(new StringReader(
							    listOfCoordsString), PathIterator.WIND_EVEN_ODD);
							filter = filterShape(shape, XyCombinedTransformProducer
							    .getCurrentTransform(element));
						}
						else
						{
							if (element.getTagName().equals("rect"))
							{
								//shape = XyRectangleProducer.createShape(element);
							  
							  String elementId = element.getAttribute("id");
							  
							  //String x = element.getAttribute("x");
	              //String y = element.getAttribute("y");
	              //String width = element.getAttribute("width");
	              //String height = element.getAttribute("height");
							  
							  shape = XyShapeProducer.createShape(element, ctx);
								
								if ( elementId.equals(elementIdOfClip))
								{
									log.trace("Found label outline");
								}
								
								// rect already takes into account its transform or that of parent if it has any
								filter = filterShape(shape, new AffineTransform() );
								

							}
							else
							{
								if (element.getTagName().equals("line"))
								{
									filter = filterLine(element);
								}
								else
								{
									if (element.getTagName().equals("glyph"))
									{
										filter = FILTER_REJECT;
									}
									else
									{
										if ( element.getTagName().equals("circle") )
										{
											GraphicsNode circle = ctx.getGraphicsNode(element);
											
											filter = filterShape(circle.getOutline(), new AffineTransform() );
										}
										else
										{
											// TODO - we already calculate a Shape but then
											// we go an apply the pathiterator on it again to determine insideness
											// this is inefficient
											//
											// I also think that XyRectangleProducer will be made obsolete
											
											// we cannot keep adding new elements to cope with
											// likely we will be able to use XyShapeProducer to cope with them all
											
											if ( element.getTagName().equals("polyline") )
											{
											  filter = filterShape( XyShapeProducer.createShape(element, ctx), new AffineTransform() );
											}
											else
											{
											  if (element.getTagName().equals("!DOCTYPE"))
											  {
											   	filter = FILTER_REJECT;
											  }
											  else
											  {
												  filter = FILTER_SKIP;
											  }				
											}
										} // circle
									} // glyph
								} // line
							} // rect
						} // polygon
					} // path
				} // text
			} // check its an element
			
		}
		catch (Exception cause)
		{
			System.out.println(cause.toString());

			System.out.println("exception");

			System.out.println(cause.getMessage());
			System.out.println("\n\n");
			System.out.println(cause.getCause());
			System.out.println("\n\n");

			StackTraceElement elements[] = cause.getStackTrace();

			for (int i = 0, num = elements.length; i < num; i++)
			{
				System.err.println(elements[i].getFileName() + ":"
				    + elements[i].getLineNumber() + ">> " + elements[i].getMethodName()
				    + "()");
			}

			filter = filterModeDoesNotContain;
		}

		return filter;
	}

/**
 * Determines if a shape is contained within the filter region.
 * 
 * @param shape (input parameter) the shape to check
 * @param at (input parameter) its associated transform that scales and positions it in the drawing
 * @return an enumerate to say whether or not the element is within the region or not
 */
private short filterShape( final Shape shape, final AffineTransform at  )
{
  short filter;
  
	boolean contains = true;

  contains = detectOverlap( shape, at, clipOutline );
	    
	if (contains)
	{
	  filter = filterModeContains;
	}
	else
	{
	  filter = filterModeDoesNotContain;
  }

  return filter;
}


public static boolean detectOverlap( Element elementToTest, Element elementToTestAgainst, BridgeContext ctx )
{
  Shape shapeToTest 
    = XyShapeProducer.createTransformedShape( 
        XyShapeProducer.createShape( elementToTest, ctx ), 
          XyCombinedTransformProducer.getCurrentTransform(elementToTest) );

  Shape shapeToTestAgainst 
    = XyShapeProducer.createTransformedShape( 
        XyShapeProducer.createShape( elementToTestAgainst, ctx ), 
          XyCombinedTransformProducer.getCurrentTransform(elementToTestAgainst) );
  
  boolean contains 
    = detectOverlap( 
       shapeToTest,
       shapeToTestAgainst );
  
  return contains;
}


public static boolean detectOverlap( 
  final Shape shapeParam,
  final AffineTransform atParam,
  final Shape clipOutlineParam )
{
  AffineTransform at = null;
  if ( atParam == null )
  {
    at = new AffineTransform();
  }
  else
  {
    at = atParam;
  }
  
  Shape shapeParamWithTransform = XyShapeProducer.createTransformedShape( shapeParam, at );
  
  boolean contains = detectOverlap( shapeParamWithTransform, clipOutlineParam );
  
  return contains;
}


public static boolean detectOverlap( 
  final Shape shapeParam,
  final Shape clipOutlineParam )
{
  final AffineTransform noTransform = null;
  
  boolean contains = true;
  if ( shapeParam != null ) 
  {
    PathIterator pathIterator = shapeParam.getPathIterator(noTransform);

      double[] coords = new double[6];
      
      while (!pathIterator.isDone() && contains) 
      {
        switch (pathIterator.currentSegment(coords)) 
        {
        case PathIterator.SEG_CLOSE: // no points
        {
          log.trace("SEG_CLOSE");
        }
        break;

        case PathIterator.SEG_LINETO: // 1 point
        {
          contains = clipOutlineParam.contains( coords[0], coords[1] );
          log.trace("SEG_LINETO");
        }
        break;

        case PathIterator.SEG_QUADTO: // 2 points
        {
          contains =    clipOutlineParam.contains( coords[0], coords[1] )
                 && clipOutlineParam.contains( coords[2], coords[3] );
          log.trace("SEG_QUADTO");
        }
        break;

        case PathIterator.SEG_MOVETO: // 1 point
        {
          contains = clipOutlineParam.contains( coords[0], coords[1] );

            log.trace("SEG_MOVETO" + "x:" + coords[0] + "y:" + coords[1]);
        }
        break;

        case PathIterator.SEG_CUBICTO: // 3 points
        {
          contains =      clipOutlineParam.contains( coords[0], coords[1] )
                 && clipOutlineParam.contains( coords[2], coords[3] )
                 && clipOutlineParam.contains( coords[4], coords[5] );
          log.trace("SEG_CUBICTO");
        }
        break;

        default: 
        {
          log.trace("default:");
        }
        } // end switch

        pathIterator.next();
      } // end while
  }
  else
  {
   contains = false;
  }

  return contains;
}



/**
 * Determines if a line is contained within the filter region.
 * 
 * @param element (input parameter) the line to check
 * @return an enumerate to say whether or not the element is within the region or not
 *
 * 
 */
private short filterLine( final Element element )
{
  if ( detectLineOverlapWithShape( element, clipOutline ) )
  {
    return filterModeContains;
  }
  else
  {
    return filterModeDoesNotContain;
  }
}

public static boolean detectLineOverlapWithShape( final Element lineElementParam, Shape shapeParam )
{
  String x1String = lineElementParam.getAttribute("x1");
  String y1String = lineElementParam.getAttribute("y1");
  String x2String = lineElementParam.getAttribute("x2");
  String y2String = lineElementParam.getAttribute("y2");

  float x1 = 0.0f, y1 = 0.0f, x2 = 0.0f, y2 = 0.0f;

  x1 = (new Float(x1String)).floatValue();
  y1 = (new Float(y1String)).floatValue();
  x2 = (new Float(x2String)).floatValue();
  y2 = (new Float(y2String)).floatValue();

  boolean overlap = false;
  if ( shapeParam.contains(x1, y1) && shapeParam.contains(x2, y2) )
  {
    overlap = true;
  }
  else
  {
    overlap = false;
  }
  
  return overlap;
}

	
/**
 * Get the element that defines the clip region
 * @return element
 */
public Element getClipElement()
{
	return clipElement;
}



/*
 * not used at the moment, but may use later if we think we dont require XyShapeProducer
 * 

	private Shape getRectAsShapeFromElement(Element element)
	{
		Shape shape = null;

		SVGElement svgElement = (SVGElement) element;

		SVGLocatable locatable = (SVGLocatable) svgElement;

		SVGRect boundingBox = locatable.getBBox();

		if (boundingBox != null)
		{

			Polygon2D boundingBoxAsPolygon = new Polygon2D();

			boundingBoxAsPolygon.addPoint(boundingBox.getX(), boundingBox.getY()); // top
																																							// left
			boundingBoxAsPolygon.addPoint(
			    boundingBox.getX() + boundingBox.getWidth(), boundingBox.getY()); // top
			// right
			boundingBoxAsPolygon.addPoint(
			    boundingBox.getX() + boundingBox.getWidth(), boundingBox.getY()
			        + boundingBox.getHeight()); // bottom right
			boundingBoxAsPolygon.addPoint(boundingBox.getX(), boundingBox.getY()
			    + boundingBox.getHeight()); // bottom
			// left

			shape = boundingBoxAsPolygon;
		}

		return shape;
	}
*/

}
