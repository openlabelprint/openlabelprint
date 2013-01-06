package com.xyratex.svg.clip.filter.farm;

import org.w3c.dom.Element;

public class XySVGGlyphDetectFilter implements XySVGFilter
{
  public boolean isAVisibleSVGElement()
  {
    return false;
  }
  
  public boolean areOverlapping( Element element1, Element element2 )
  {
    return false;
  }
    
  public boolean isAVisibleSVGShapeElement()
  {
    return false;
  }
}
