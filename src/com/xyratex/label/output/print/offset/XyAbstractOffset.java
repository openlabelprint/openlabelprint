package com.xyratex.label.output.print.offset;

import com.xyratex.constants.XyConstants;

import java.util.Observable;

import java.util.Observer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class XyAbstractOffset extends Observable implements XyOffsetComponent
{
	private float xOffsetAsMM = 0.0F;
	private float yOffsetAsMM = 0.0F;
	
	private int xOffsetAsDots = 0;
	private int yOffsetAsDots = 0;
	
	private float dotsPerMM = Float.NaN; // unset by default
	
        protected XyOffsetSum offsetSum = null;
        
        protected String componentId = "unset";
        
	public float getXAsMM() { return xOffsetAsMM; }
	public float getYAsMM() { return yOffsetAsMM; }
	
  public int getXAsDots() { return xOffsetAsDots; }
  public int getYAsDots() { return yOffsetAsDots; }

  
  	private static Log log = LogFactory.getLog(XyAbstractOffset.class);

      public XyAbstractOffset( Observer observer )
  {
      addObserver( observer );
  }
  
  
	public void moveDown(String down)
	{
            log.trace( "moveDown:" + down );
            
  	    float updateYOffsetAsMM = yOffsetAsMM + asMM( down );
  	    int updateYOffsetAsDots = yOffsetAsDots + asDots( down );
        
            update( xOffsetAsMM, updateYOffsetAsMM, xOffsetAsDots, updateYOffsetAsDots );
        }

	public void moveLeft(String left)
	{
            log.trace( "moveLeft:" + left );

            float updateXOffsetAsMM = xOffsetAsMM - asMM( left );
            int updateXOffsetAsDots = xOffsetAsDots - asDots( left );
            
            update( updateXOffsetAsMM, yOffsetAsMM, updateXOffsetAsDots, yOffsetAsDots );
	}

	public void moveRight(String right)
	{
            log.trace( "moveRight:" + right );

            float updateXOffsetAsMM = xOffsetAsMM + asMM( right );
            int updateXOffsetAsDots = xOffsetAsDots + asDots( right );
            
            update( updateXOffsetAsMM, yOffsetAsMM, updateXOffsetAsDots, yOffsetAsDots );
	}

	public void moveUp(String up)
	{
            log.trace( "moveUp:" + up );

            float updateYOffsetAsMM = yOffsetAsMM - asMM( up );
  	    int updateYOffsetAsDots = yOffsetAsDots - asDots( up );
            
            update( xOffsetAsMM, updateYOffsetAsMM, xOffsetAsDots, updateYOffsetAsDots );
	}

	public void reset()
	{
            log.trace( "reset()" );
            
            float updateXOffsetAsMM = 0.0F;
            int updateXOffsetAsDots = 0;
            float updateYOffsetAsMM = 0.0F;
  	    int updateYOffsetAsDots = 0;
            
            update( updateXOffsetAsMM, updateYOffsetAsMM, updateXOffsetAsDots, updateYOffsetAsDots );
	}

	public void setDotsPerUnitMeasurement(String dotsperunit)
	{
          log.trace( "setDotsPerUnitMeasurement(" + dotsperunit + ")" );
            
	  String localValue = dotsperunit;
	  
		if ( localValue.contains("dpmm") )
		{
			localValue.replace("dpmm", "" );
			dotsPerMM = Float.valueOf(localValue);
		}
		else
		{
			if ( localValue.contains("dpi") )
			{
				final float mmInInch = 25.4F;
				localValue.replace("dpi", "" );
				dotsPerMM = Float.valueOf(localValue) * mmInInch;
			}
		}
          
          int updateXOffsetAsDots = asDots( xOffsetAsDots + "px" );
          int updateYOffsetAsDots = asDots( yOffsetAsDots + "px" );
          
          update( xOffsetAsMM, yOffsetAsMM, updateXOffsetAsDots, updateYOffsetAsDots );
	}

	public void setX(String x)
	{
          float updateXOffsetAsMM = asMM( x );
          int updateXOffsetAsDots = asDots( x );
          
          update( updateXOffsetAsMM, yOffsetAsMM, updateXOffsetAsDots, yOffsetAsDots );
	}

	public void setY(String y )
	{
          float updateYOffsetAsMM = asMM( y );
  	  int updateYOffsetAsDots = asDots( y );
          
          update( xOffsetAsMM, updateYOffsetAsMM, xOffsetAsDots, updateYOffsetAsDots );
	}
	
	private int asDots( String value ) 
	{
		String localValue = value;
                String localValueWithoutUnits = "";
		int px = 0;
		
		if ( localValue.contains("mm") )
		{
			if ( Float.compare( dotsPerMM , Float.NaN ) != XyConstants.COMPARISON_EQUAL )
			{
			  localValueWithoutUnits = localValue.replace("mm", "" );
			
			  px = ( new Float( Float.parseFloat(localValueWithoutUnits) * dotsPerMM )).intValue();
			}
			

		}
		else // assume dots
		{		

                    
			// remove if px - pixels as unit of measurement are explicitly defined
                  

		  if ( localValue.contains("px" ) )
		  {
			  localValueWithoutUnits = localValue.replace("px", "" );
                          log.trace(localValue + " - removed px ");
		  }
                  else
                  {
                      localValueWithoutUnits = localValue;
                  }
		 
                  log.trace( "\"" + localValueWithoutUnits + "\"" );
      px = Integer.parseInt(localValueWithoutUnits);
		}
		
		return px;

	}
	
	private float asMM( String value ) 
	{
		String localValue = value;
                String localValueWithoutUnits = "";
		float mm = Float.NaN;
		
		if ( localValue.contains("mm") )
		{
			localValueWithoutUnits = localValue.replace("mm", "" );
			
			mm = Float.parseFloat(localValueWithoutUnits);
		}
		else // assume dots
    {			
			// remove if px - pixels as unit of measurement are explicitly defined
			if ( localValue.contains("px" ) )
			{
			  localValueWithoutUnits = localValue.replace("px", "" );
			}
                        else
                        {
                            localValueWithoutUnits = localValue;
                        }
			
                        if ( Float.compare( dotsPerMM , Float.NaN ) != XyConstants.COMPARISON_EQUAL )
			{
			mm = dotsPerMM / ( new Integer( Integer.parseInt(localValueWithoutUnits) ) ).floatValue();
                        }
		}
		
		return mm;
	}
	
          public void setOverallOffsetManager( XyOffsetSum xyOffsetSum )
          {
              offsetSum = xyOffsetSum;
          }
  
  public void update( 
  	float updateXOffsetAsMM,
  	float updateYOffsetAsMM,
  	int updateXOffsetAsDots,
  	int updateYOffsetAsDots )
  {
		if (!(    ( updateXOffsetAsDots == xOffsetAsDots )
			     && ( updateYOffsetAsDots == yOffsetAsDots )
			     && Float.compare( updateXOffsetAsMM, xOffsetAsMM ) == XyConstants.COMPARISON_EQUAL
			     && Float.compare( updateYOffsetAsMM, yOffsetAsMM ) == XyConstants.COMPARISON_EQUAL
		   ) )
    {
			// something has changed
			xOffsetAsDots = updateXOffsetAsDots;
			yOffsetAsDots = updateYOffsetAsDots;
			xOffsetAsMM = updateXOffsetAsMM;
			yOffsetAsMM = updateYOffsetAsMM;
                        
                        offsetSum.offsetHasChanged(this);
    }
  }
  
  
            public void setComponentId( String id ){
           componentId = id;
          }
  
  public String getComponentId()
  {
      return componentId;
  }
}
