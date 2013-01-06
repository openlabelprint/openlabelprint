package com.xyratex.svg.clip.filter.farm;

import org.w3c.dom.Element;

public class XySVGGCollisionFilter extends XyAbstractSVGCollisionFilter
{
  public boolean areOverlapping( Element element1, Element element2 ) throws Exception
  {
    return false;
  }
  
  public boolean isAVisibleSVGElement()
  {
    return false;
  }
  
  public boolean isAVisibleSVGShapeElement()
  {
    return false;
  }
}
