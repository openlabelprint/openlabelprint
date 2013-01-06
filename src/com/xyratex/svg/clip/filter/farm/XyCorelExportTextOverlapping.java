package com.xyratex.svg.clip.filter.farm;

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

  private static Log log = LogFactory.getLog(XyCorelExportTextOverlapping.class);
  
  public static Vector findOverlappingSVGTextElements( Document svgdoc, BridgeContext ctx )
  { 
    Vector overlappingLists = new Vector();
   
/* commented out as still work in progress
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
          
          SVGLocatable aSvgLocatable = (SVGLocatable) aDifferentElement;
          SVGLocatable anotherSvgLocatable = (SVGLocatable) anotherElement;
          
          if (    ( aSvgLocatable != null )
               || ( anotherSvgLocatable != null )
               
             )
          {
            SVGRect aBoundingBox = aSvgLocatable.getBBox();
            SVGRect anotherBoundingBox = anotherSvgLocatable.getBBox();
          
            if (    ( aBoundingBox != null )
                 || ( anotherBoundingBox != null )
               )
            {  
              byte overlap = getOverlap( aBoundingBox, anotherBoundingBox );
          
              // if there is an overlap
              if ( overlap != OVERLAP_NONE  )
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
      }
    }
   */

    // an array of arrays, where
    // each array has an SVG Element that is overlapped by others, 
    // the others being the rest of the list
    return overlappingLists;
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
