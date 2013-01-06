package com.xyratex.svg.clip.filter.farm;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.io.StringReader;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.parser.AWTPolygonProducer;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import com.xyratex.svg.batik.util.XyCombinedTransformProducer;
import com.xyratex.svg.batik.util.XyShapeProducer;

public class XySVGRectCollisionFilter extends XyAbstractSVGCollisionFilter
{
  public XySVGRectCollisionFilter( BridgeContext aCtx )
  {
    childLog = LogFactory.getLog(XySVGLineCollisionFilter.class);
    
    theCtx = aCtx;
  }
    
  public boolean areOverlapping( Element testElement, Element fixedElement ) throws IOException
  {
    boolean overlap = false;
 
    Shape testElementShape = XyShapeProducer.createShape( testElement, theCtx );
    Shape fixedElementShape = XyShapeProducer.createShape( fixedElement, theCtx );
    overlap = XyAbstractSVGCollisionFilter.detectOverlap( testElementShape, fixedElementShape );
    return overlap;
    
    /*
    // this also works
    //
    Shape fixedElementShape = XyShapeProducer.createShape( fixedElement, theCtx );
    AffineTransform testElementTransform = XyCombinedTransformProducer.getCurrentTransform( testElement );
    Shape testElementShape = XyShapeProducer.createShape(testElement, theCtx);
    boolean contains = XyAbstractSVGCollisionFilter.detectOverlap( testElementShape, testElementTransform, fixedElementShape );
    overlap = contains;
    return overlap;
    */
    
  }
  
  public boolean isAVisibleSVGElement()
  {
    return true;
  }
  
  public boolean isAVisibleSVGShapeElement()
  {
    return true;
  }
}
