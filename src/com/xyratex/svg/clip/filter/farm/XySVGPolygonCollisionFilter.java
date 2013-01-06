package com.xyratex.svg.clip.filter.farm;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.io.StringReader;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.parser.AWTPolygonProducer;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import com.xyratex.svg.batik.util.XyCombinedTransformProducer;
import com.xyratex.svg.batik.util.XyShapeProducer;

public class XySVGPolygonCollisionFilter extends XyAbstractSVGCollisionFilter
{
  public XySVGPolygonCollisionFilter( BridgeContext aCtx )
  {
    childLog = LogFactory.getLog(XySVGLineCollisionFilter.class);
    
    theCtx = aCtx;
  }
  
  
  public boolean areOverlapping( Element testElement, Element fixedElement ) throws IOException
  {
    boolean overlap = false;
    
    Shape fixedElementShape = XyShapeProducer.createShape( fixedElement, theCtx );

    String listOfCoordsString = testElement.getAttribute("points");

    Shape testElementShape = AWTPolygonProducer.createShape(new StringReader(
        listOfCoordsString), PathIterator.WIND_EVEN_ODD);
    
    AffineTransform testElementTransform = XyCombinedTransformProducer.getCurrentTransform( testElement );
    Shape testElementTransformedShape = XyShapeProducer.createTransformedShape( testElementShape, testElementTransform );
    
    overlap = XyAbstractSVGCollisionFilter.detectOverlap( testElementTransformedShape, fixedElementShape );
    
    return overlap;
  }
  
  public boolean isAVisibleSVGShapeElement()
  {
    return true;
  }
  
  public boolean isAVisibleSVGElement()
  {
    return true;
  }
}
