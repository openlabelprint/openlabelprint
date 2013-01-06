package com.xyratex.svg.clip.filter.farm;

import org.w3c.dom.Element;

public interface XySVGFilter
{
  public boolean areOverlapping( Element element1, Element element2 ) throws Exception;
  
  public boolean isAVisibleSVGElement();
  
  public boolean isAVisibleSVGShapeElement();
}
