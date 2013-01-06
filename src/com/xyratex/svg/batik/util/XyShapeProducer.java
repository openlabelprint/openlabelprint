//-------------------------------------------------------------------------
// (C) COPYRIGHT Xyratex Storage Systems Division 2011
// All Rights Reserved
//
// Filename    : XyShapeProducer.java
// Author      : Rob Davis


// By          : Rob Davis
// File Type   : Java source code for Open Label Print
//-------------------------------------------------------------------------

package com.xyratex.svg.batik.util;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.ext.awt.geom.Polygon2D;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGLocatable;
import org.w3c.dom.svg.SVGRect;


/**
 * <p>
 * This stateless singleton class produces an outline of the supplied SVG Element
 * in the form of a java.awt.Shape. 
 * </p>
 * 
 * @author rdavis
 *
 * <p>This could be used to produce a Shape object from any graphical SVG element
 * (maybe even text, if we consider it as a rectangular block), regardless
 * of whether it is closed (i.e. no break in its path) or not.</p>
 *
 */
public class XyShapeProducer
{
	private static Log log = LogFactory.getLog(XyShapeProducer.class);
	
	/**
	 *  R = Release L = Level G = date U = time
	 *  Values calculated by SCCS when file is checked out and compiled.
	 */
	public static final String sccsid = "@(#)Xyratex  ISTP  XyShapeProducer.java  %R%.%L%, %G% %U%";
	
	/**
	 * <p>Produces an outline of the supplied SVG Element in the form of a java.awt.Shape.</p>
	 * 
	 * @param element (input parameter) the SVG element
	 * @param bridgeContext (input parameter) the bridge context - this provides 
	 * the link between the XML SVG Element and its resulting graphical representation - which
	 * provides graphical values for the outline within the context of the whole SVG document and any CSS
	 * that has been applied.
	 * 
	 * @return the Shape
	 */
	public static Shape createShape(final Element element, final BridgeContext bridgeContext )
	{
		log.trace(sccsid);
		
		// get the outline (as a Shape) of the SVG Element
		// this Shape will then require any tranforms effecting the SVG Element to be applied
		GraphicsNode graphicsNode = bridgeContext.getGraphicsNode( element );
		Shape outline = graphicsNode.getOutline();

		AffineTransform at = XyCombinedTransformProducer.getCurrentTransform(element);
		
		return createTransformedShape( outline, at );
	}
	
	public static Shape createTransformedShape( Shape outline, AffineTransform at )	
	{	
		// create a polygon to hold the Shape with any effecting transforms applied
		Polygon2D polygon = new Polygon2D();
		
		// Get the Shape co-ordinates - now with effecting transforms applied
		PathIterator pathIterator = outline.getPathIterator(at);
		
		// Create the polygon from the shape co-ordinates
		//
		// iterate through each segment of the shape to build the polygon
		float[] coords = new float[6]; // to hold the current segment value - can be up to 3 sets of co-ordinate pairs
		while (!pathIterator.isDone())
		{
			// for each segment, its type determines the number of co-ordinates used in that segment
			switch (pathIterator.currentSegment(coords))
			{
				case PathIterator.SEG_CLOSE: // no points
				{
					log.trace("SEG_CLOSE");
				}
			  break;

				case PathIterator.SEG_LINETO: // 1 point
				{
					polygon.addPoint(coords[0], coords[1]);
					log.trace("SEG_LINETO");
				}
				break;

				case PathIterator.SEG_QUADTO: // 2 points
				{
					polygon.addPoint(coords[0], coords[1]);
					polygon.addPoint(coords[2], coords[3]);
					log.trace("SEG_QUADTO");
				}
				break;

				case PathIterator.SEG_MOVETO: // 1 point
				{
					polygon.addPoint(coords[0], coords[1]);
					log.trace("SEG_MOVETO");
				}
				break;

				case PathIterator.SEG_CUBICTO: // 3 points
				{
					polygon.addPoint(coords[0], coords[1]);
					polygon.addPoint(coords[2], coords[3]);
					polygon.addPoint(coords[4], coords[5]);
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

		return polygon; // this will be available to clients as a Shape
	}

	/*
	 * Footnotes for future consideration:
	 * 
	 * I thought getGlobalTransform might be the same as 
	 * 

	XySVGCombinedTransformProducer.getCurrentTransform but actually it doesnt seem to return
	the same values!
	
	    .getGlobalTransform());
  */
	

	public static Shape getShapeFromBoundingBoxOfText( Element element )
	{
    SVGElement svgElement = (SVGElement) element;

    SVGLocatable locatable = (SVGLocatable) svgElement;

    SVGRect textBoundingBox = locatable.getBBox();
    
    Shape shape = null;
    
    if ( textBoundingBox == null )
    {
      shape = null;
    }
    else // there is a bounding box for the text element
    {
      Polygon2D textBoundingBoxAsPolygon = new Polygon2D();

      textBoundingBoxAsPolygon.addPoint(textBoundingBox.getX(),
          textBoundingBox.getY()); // top left
      textBoundingBoxAsPolygon.addPoint(textBoundingBox.getX()
          + textBoundingBox.getWidth(), textBoundingBox.getY()); // top
      // right
      textBoundingBoxAsPolygon.addPoint(textBoundingBox.getX()
          + textBoundingBox.getWidth(), textBoundingBox.getY()
          + textBoundingBox.getHeight()); // bottom right
      textBoundingBoxAsPolygon.addPoint(textBoundingBox.getX(),
          textBoundingBox.getY() + textBoundingBox.getHeight()); // bottom
      // left

      shape = textBoundingBoxAsPolygon;
    }
      
    return shape;
	}
	

	
	 public static Shape getTransformedShapeFromBoundingBoxOfText( Element element )
	 {
	   Shape shape = getShapeFromBoundingBoxOfText( element );
	   
	   AffineTransform at = XyCombinedTransformProducer.getCurrentTransform(element);
	   
	   Shape transformedShape = createTransformedShape( shape, at );
	   
	   return shape;
	 }


}
