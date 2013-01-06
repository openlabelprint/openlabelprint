package com.xyratex.svg.clip.filter.farm;

import java.awt.Shape;

import org.apache.batik.bridge.BridgeContext;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import com.xyratex.svg.batik.util.XyShapeProducer;

public class XySVGLineCollisionFilter extends XyAbstractSVGCollisionFilter
{
  public XySVGLineCollisionFilter( BridgeContext aCtx )
  {
    childLog = LogFactory.getLog(XySVGLineCollisionFilter.class);
    
    theCtx = aCtx;
  }
  
  
  public boolean areOverlapping( Element testElement, Element fixedElement )
  {
    Shape fixedElementShape = XyShapeProducer.createShape( fixedElement, theCtx );

    return XyAbstractSVGCollisionFilter.detectLineOverlapWithShape( testElement, fixedElementShape ); 
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
