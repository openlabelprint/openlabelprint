package com.xyratex.svg.clip.filter.farm;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.io.StringReader;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.parser.AWTPathProducer;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import com.xyratex.svg.batik.util.XyCombinedTransformProducer;
import com.xyratex.svg.batik.util.XyShapeProducer;

public class XySVGPathCollisionFilter extends XyAbstractSVGCollisionFilter
{
  public XySVGPathCollisionFilter( BridgeContext aCtx )
  {
    childLog = LogFactory.getLog(XySVGLineCollisionFilter.class);
    
    theCtx = aCtx;
  }
    
  public boolean areOverlapping( Element testElement, Element fixedElement ) throws IOException
  {
    boolean overlap = false;
   
    /*
     
                listOfCoordsString = element.getAttribute("d");

            shape = AWTPathProducer.createShape(new StringReader(
                listOfCoordsString), PathIterator.WIND_EVEN_ODD);
            filter = filterShape(shape, XyCombinedTransformProducer
                .getCurrentTransform(element));
      
     
     */
    
    String path = testElement.getAttribute("d");
    
    /*
    Shape fixedElementShape = XyShapeProducer.createShape( fixedElement, theCtx );

    String testElementListOfCoordsString = testElement.getAttribute("d");

    Shape testElementShape 
      = AWTPathProducer.createShape( 
          new StringReader( testElementListOfCoordsString ), PathIterator.WIND_EVEN_ODD );
    
    
    AffineTransform testElementTransform = XyCombinedTransformProducer.getCurrentTransform( testElement );
    
    Shape testElementTransformedShape = XyShapeProducer.createTransformedShape( testElementShape, testElementTransform );
    
    overlap = XyAbstractSVGCollisionFilter.detectOverlap( testElementTransformedShape, fixedElementShape );
    */
    
    Shape fixedElementShape = XyShapeProducer.createShape( fixedElement, theCtx );
    
    String testElementListOfCoordsString = testElement.getAttribute("d");

    Shape testElementShape 
      = AWTPathProducer.createShape( 
          new StringReader( testElementListOfCoordsString ), PathIterator.WIND_EVEN_ODD );
    
    AffineTransform testElementTransform = XyCombinedTransformProducer.getCurrentTransform( testElement );
    
    boolean contains = XyAbstractSVGCollisionFilter.detectOverlap( 
      testElementShape, testElementTransform, fixedElementShape );
    
    overlap = contains;
    
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
