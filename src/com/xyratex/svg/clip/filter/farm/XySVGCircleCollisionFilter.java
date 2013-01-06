package com.xyratex.svg.clip.filter.farm;

import java.awt.Shape;
import java.io.IOException;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import com.xyratex.svg.batik.util.XyShapeProducer;

public class XySVGCircleCollisionFilter extends XyAbstractSVGCollisionFilter
{
  public XySVGCircleCollisionFilter( BridgeContext aCtx )
  {
    childLog = LogFactory.getLog(XySVGLineCollisionFilter.class);
    
    theCtx = aCtx;
  }
  
  public boolean areOverlapping( Element testElement, Element fixedElement ) throws IOException
  {
    boolean overlap = false;
    
    Shape fixedElementShape = XyShapeProducer.createShape( fixedElement, theCtx );

    GraphicsNode circleGraphicsNode = theCtx.getGraphicsNode(testElement);
    
    if ( circleGraphicsNode != null )
    {
      Shape testElementShape = circleGraphicsNode.getOutline();
     
      overlap = XyAbstractSVGCollisionFilter.detectOverlap( testElementShape, fixedElementShape );
    }
    
    return overlap;
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
