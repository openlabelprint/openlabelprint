package com.xyratex.label.output.print.offset;

import java.util.Observer;

import org.apache.commons.lang.StringUtils;

public class XyOffsetByPrinterInstance extends XyAbstractOffset
{
        public XyOffsetByPrinterInstance( Observer observer )
        {
            super( observer );
        }
    
	@Override
	public void load()
	{
		// TODO Auto-generated method stub
              //notifyObservers( "" );
            
            String offsetsAsString = XyPrinterOffsetManager.loadPrinterOffset(componentId);
            
                          	final int arraySizeIs2ForXandYCoordinateValues = 2;
              	String[] offsetsAsArray 
              	  = StringUtils.split( offsetsAsString, ",", arraySizeIs2ForXandYCoordinateValues );
              	
                setX( offsetsAsArray[0] + "px" );
                setY( offsetsAsArray[1] + "px" );
                
offsetSum.offsetHasChanged(this);
	}
	
	@Override
	public void save()
	{
		// TODO Auto-generated method stub
            
            //notifyObservers( "" );
            

            
      XyPrinterOffsetManager.saveOffsetForPrinter( "" + getXAsDots() + "," + getYAsDots(), componentId );
            

	}
        

                   
}
