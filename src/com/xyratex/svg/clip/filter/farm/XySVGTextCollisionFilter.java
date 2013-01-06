package com.xyratex.svg.clip.filter.farm;

import java.awt.Shape;
import java.awt.geom.AffineTransform;

import org.apache.batik.bridge.BridgeContext;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import com.xyratex.svg.batik.util.XyCombinedTransformProducer;
import com.xyratex.svg.batik.util.XyShapeProducer;

public class XySVGTextCollisionFilter extends XyAbstractSVGCollisionFilter
{
  public XySVGTextCollisionFilter( BridgeContext aCtx )
  {
    childLog = LogFactory.getLog(XySVGTextCollisionFilter.class);
    
    theCtx = aCtx;
  }
    
  public boolean areOverlapping( Element testElement, Element fixedElement )
  {
    boolean overlap = false;
    
    String textString = testElement.getTextContent();
    
    if ( StringUtils.isBlank( textString ) )
    {
      overlap = false;
    }
    else
    {
    
    Shape testElementShape = XyShapeProducer.getShapeFromBoundingBoxOfText( testElement );
    
    Shape fixedElementShape = XyShapeProducer.createShape( fixedElement, theCtx );
    

    
    if ( testElementShape ==  null )
    {
      // no bounding box likely means there is no text in the text element so it should be 
      // white space, BUT if there is a style sheet with a non blank background
      // then the element *could* be a block which we do not want to see
      if ( testElement.hasAttribute("class") ) { testElement.removeAttribute("class"); }
      
      // also we consider this element to not be within the region specified for the filter
      overlap = false;
    }
    else // there is a bounding box for the text element
    {
      AffineTransform testElementTransform = XyCombinedTransformProducer.getCurrentTransform( testElement );
      Shape testElementTransformedShape = XyShapeProducer.createTransformedShape( testElementShape, testElementTransform );
      
      overlap = XyAbstractSVGCollisionFilter.detectOverlap( testElementTransformedShape, fixedElementShape );
    }
    }
    
    return overlap;
  }
  
  public boolean isAVisibleSVGElement()
  {
    return true;
  }
  
  public boolean isAVisibleSVGShapeElement()
  {
    return false; // because its visible but not a shape - it's text
  }
}
