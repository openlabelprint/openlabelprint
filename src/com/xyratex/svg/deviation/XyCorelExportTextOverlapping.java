package com.xyratex.svg.deviation;

import java.util.Vector;

import org.apache.batik.bridge.BridgeContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGLocatable;
import org.w3c.dom.svg.SVGRect;


import com.xyratex.svg.clip.XySVGClipFilter;


public class XyCorelExportTextOverlapping
{

  public final static byte OVERLAP_NONE = 0;
  public final static byte OVERLAP_TOP = 1;
  public final static byte OVERLAP_BOTTOM = 2;
  public final static byte OVERLAP_LEFT = 4;
  public final static byte OVERLAP_RIGHT = 8;
  
  private static Log log = LogFactory.getLog(XyCorelExportTextOverlapping.class);
  
  public static Vector findOverlappingSVGTextElements( Document svgdoc, BridgeContext ctx )
  { 
    Vector overlappingLists = new Vector();
   
    /*
    NodeList textElementList = svgdoc.getElementsByTagName( "text" );
    
    for ( int i = 1; i < textElementList.getLength(); i++ )
    {
      Element anElement = (Element) textElementList.item( i );
      
      Element anotherElement = null;
      Element aDifferentElement = null;
      for ( int j = 1; i < textElementList.getLength(); j++ )
      {
        anotherElement = (Element) textElementList.item( j );
        if ( anotherElement != anElement )
        {
          aDifferentElement = anotherElement;
          
          byte overlap = getOverlap( anElement, aDifferentElement );
          
          if ( overlap )
          {
            Vector listForAnElement = (Vector) overlappingLists.get(i);
            
            if ( listForAnElement == null )
            {
              listForAnElement = new Vector();
              
              listForAnElement.add( anElement );
            }
                        
            listForAnElement.add( aDifferentElement );
            
            // rewrite the updated list at the same position
            overlappingLists.setElementAt( listForAnElement, i );
          }
        }
      }
    }
    
    */
    
    return overlappingLists;
  }

  
  //SVGRect textBoundingBox1 = svgLocatable1.getBBox();
  //SVGRect textBoundingBox2 = svgLocatable2.getBBox();
  
  
  
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
      log.trace( "Can't process overlap as at least one element is not a rectangle." );
    }
    
    return overlap;


 }


  
  
  public static void separateOverlappingTextElements( Vector overlappingLists )
  {
    for ( int i = 1; i < overlappingLists.size(); i++ )
    {
      Vector listForAnElement = (Vector) overlappingLists.get( i );
      
      Element overlappedElement = (Element) listForAnElement.get( i );
      
      String textValue = overlappedElement.getTextContent();
      
      if ( !(textValue.contains("XY" )) )
      {
        for ( int j = 2; j < listForAnElement.size(); j++ )
        {
          Element overlappingElement = (Element) listForAnElement.get( j );
          
          String overlappingTextValue = overlappingElement.getTextContent();
          
          if ( overlappingTextValue.contains("XY") )
          {
            // we have a fixed element overlapped by a variable field
            
            // top, bottom, left, right
            //then add to these co-ordinates
          }
          else
          {
            // dont know what to do about fixed fields
          }
          
        }
      }
    }
  }
  
}
