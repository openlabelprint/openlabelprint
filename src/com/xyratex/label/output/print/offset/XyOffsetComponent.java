package com.xyratex.label.output.print.offset;



public interface XyOffsetComponent
{
  public void moveUp( String up ); 
  public void moveDown( String down ); 
  public void moveLeft( String left );
  public void moveRight( String right ); 
  
  public void reset();
  
  public void setX( String x ); 
  public void setY( String y ); 
  
	public float getXAsMM();
	public float getYAsMM();
	
  public int getXAsDots();
  public int getYAsDots();
	
  public void save();
  public void load();
  
  public void setDotsPerUnitMeasurement( String dotsperunit ) throws Exception;
  
  // thought about using Observer but actually this decouples
  // too much, makes the association too lose
  public void setOverallOffsetManager( XyOffsetSum offsetSum );
  
  public void setComponentId( String id );
  
  public String getComponentId();
}
