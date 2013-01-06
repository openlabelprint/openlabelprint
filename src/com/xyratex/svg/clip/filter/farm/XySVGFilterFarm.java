package com.xyratex.svg.clip.filter.farm;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.batik.bridge.BridgeContext;
import org.w3c.dom.Element;

public class XySVGFilterFarm
{
  public static final int OVERLAP_STATE_UNSET = 0;
  public static final int OVERLAP_STATE_NO = 1;
  public static final int OVERLAP_STATE_YES = 2;
  public static final int OVERLAP_STATE_ELEMENT_IGNORED = 3;
  public static final int OVERLAP_STATE_UNKNOWN_ELEMENT = 4;
  
  private ConcurrentHashMap<String,XySVGFilter> filters 
  = new ConcurrentHashMap<String,XySVGFilter>();
  
  public XySVGFilterFarm( BridgeContext ctx )
  {

    
    // there is a way to do an auto instantiate and register without explicitly instantiating
    // (as with dom parsers)
    filters.put("text", new XySVGTextCollisionFilter( ctx ) );
    filters.put("rect", new XySVGRectCollisionFilter( ctx ) );
    filters.put("path", new XySVGPathCollisionFilter( ctx ) );
    filters.put("polygon", new XySVGPolygonCollisionFilter( ctx ) );
    filters.put("line", new XySVGLineCollisionFilter( ctx ) );
    filters.put("glyph", new XySVGGlyphDetectFilter() );
    filters.put("circle", new XySVGCircleCollisionFilter( ctx ) );
    filters.put("polyline", new XySVGPolylineCollisionFilter( ctx ) );
    filters.put("!DOCTYPE", new XySVGDocTypeDetectFilter() );
  }
  
  public boolean isAVisibleSVGElement( Element elementToTest )
  {
    boolean isVisible = false;
    
    String elementToTestTagName = elementToTest.getTagName();
    XySVGFilter filter = filters.get( elementToTestTagName );
    
    if ( filter != null )
    {
      isVisible = filter.isAVisibleSVGElement();
    }
    else
    {
      isVisible = false;
    }
    
    return isVisible;
  }
  
  
  public boolean isAVisibleSVGShapeElement( Element elementToTest )
  {
    boolean isVisible = false;
    
    String elementToTestTagName = elementToTest.getTagName();
    XySVGFilter filter = filters.get( elementToTestTagName );
    
    if ( filter != null )
    {
      isVisible = filter.isAVisibleSVGShapeElement();
    }
    else
    {
      isVisible = false;
    }
    
    return isVisible;
  }
  
  
  public int getOverlapState( Element elementToTest, Element fixedElement ) throws Exception
  {
    int overlapMode = OVERLAP_STATE_UNSET;
    
    String elementToTestId = elementToTest.getAttribute("id");
    String fixedElementId = fixedElement.getAttribute("id");
    
    if (!( elementToTestId.equals( fixedElementId )))
    {
      String elementToTestTagName = elementToTest.getTagName();
      
      XySVGFilter filter = filters.get( elementToTestTagName );
      
      if ( filter != null )
      {
        if ( filter.isAVisibleSVGElement() )
        {
          if ( filter.areOverlapping(elementToTest, fixedElement ) )
          {
            overlapMode = OVERLAP_STATE_YES;
          }
          else
          {
            overlapMode = OVERLAP_STATE_NO;
          }
        }
        else
        {
          overlapMode = OVERLAP_STATE_ELEMENT_IGNORED; 
        }
      }
      else
      {
        overlapMode = OVERLAP_STATE_UNKNOWN_ELEMENT;
      }
    }
    
    return overlapMode;
  }
}
