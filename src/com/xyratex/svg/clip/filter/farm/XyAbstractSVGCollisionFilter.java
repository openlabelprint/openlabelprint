package com.xyratex.svg.clip.filter.farm;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

import org.apache.batik.bridge.BridgeContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGRect;

import com.xyratex.svg.batik.util.XyCombinedTransformProducer;
import com.xyratex.svg.batik.util.XyShapeProducer;

public abstract class XyAbstractSVGCollisionFilter implements XySVGFilter
{
  private static Log log = LogFactory.getLog(XyAbstractSVGCollisionFilter.class);
 
  protected Log childLog = null;
  
  protected BridgeContext theCtx = null;
  
  public final static byte OVERLAP_NONE = 0;

  public final static byte OVERLAP_TOP = 1;
  
  public final static byte OVERLAP_BOTTOM = 2;

  public final static byte OVERLAP_LEFT = 4;

  public final static byte OVERLAP_RIGHT = 8;
  
  public boolean areOverlapping( Element element1, Element element2 ) throws Exception
  {
    return XyAbstractSVGCollisionFilter.detectOverlap( element1, element2, theCtx );
  }
  
  // credit: http://www.gamedev.net/reference/articles/article735.asp
    // collision detection. The basic rectangle overlap of rectangles is sufficient
    // for our bounding box overlap detection
    //
    //Object-to-object bounding-box collision detector:
    public static byte getOverlap( SVGRect textBoundingBox1, SVGRect textBoundingBox2 )
    {
      byte overlap = OVERLAP_NONE;
      if (    ( textBoundingBox1 != null )
           && ( textBoundingBox2 != null )
         )
      {
        textBoundingBox1.getX();
        
        float left1, left2;
        float right1, right2;
        float top1, top2;
        float bottom1, bottom2;
  
        left1 = textBoundingBox1.getX();
        left2 = textBoundingBox2.getX();
        right1 = textBoundingBox1.getX() + textBoundingBox1.getWidth();
        right2 = textBoundingBox2.getX() + textBoundingBox2.getWidth();
        top1 = textBoundingBox1.getY();
        top2 = textBoundingBox2.getY();
        bottom1 = textBoundingBox1.getY() + textBoundingBox1.getHeight();
        bottom2 = textBoundingBox2.getY() + textBoundingBox2.getHeight();
  
  
        // relative to element1
        //
        // co-ordinate origin (0,0) is from top left
        // http://www.w3.org/TR/SVG/coords.html#InitialCoordinateSystem
        
  
        
        // top of element 2 overlaps bottom of element1 - so element 1 bottom is overlapped
        if ( bottom1 > top2 )
        {
          overlap |= OVERLAP_BOTTOM;
        }
  
        // top of element 1 is overlapped by bottom of element2 - so element 1 top is overlapped
        if ( top1 < bottom2 )
        {
          overlap |= OVERLAP_TOP;
        }
  
        if ( right1 > left2 )
        {
          overlap |= OVERLAP_RIGHT;
        }
  
        if ( left1 < right2 ) 
        {
          overlap |= OVERLAP_LEFT;
        }
      }
      else
      {
        log.trace( "Can't process overlap as at least one element is null." );
      }
      
      return overlap;
  
  
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
    
    boolean contains = false;
    if ( shapeParam != null ) 
    {
      PathIterator pathIterator = shapeParam.getPathIterator(noTransform);
  
      
      // deal with the situation whereby some paths are residual noise and actually nothing, 
      // e.g. <path d="M0 0z"/"> - this will give no path iterator
      // TODO: it could actually be a dot, however
      if ( pathIterator != null )
      {  
      
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
    }
    else
    {
     contains = false;
    }
  
    return contains;
  }

  // concrete subclasses have to implement; there is no default
  public abstract boolean isAVisibleSVGElement();
  
  // concrete subclasses have to implement; there is no default
  public abstract boolean isAVisibleSVGShapeElement();
  
}
