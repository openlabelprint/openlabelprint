package com.xyratex.label.output.print.offset;

import java.util.Observable;
import java.util.Vector;

import com.xyratex.label.apps.gui.components.XyLabelTemplateImageLogic;

// TODO need some sort of service to convert between pixels and mm depending on dotspermm

// TODO need a way to reset this and/or delete items if new printer setup for station

public class XyOffsetSum //extends Observable
{
	private Vector<XyOffsetComponent> offsets = null;
        
        private XyLabelTemplateImageLogic xyLabelTemplateImageLogic = null;

	private boolean hasChangedFlag = false;

        private com.xyratex.label.apps.gui.components.XyLabelTemplateImageLogic imageLogic;
        
        public void setImageLogic( com.xyratex.label.apps.gui.components.XyLabelTemplateImageLogic logic )
        {
            imageLogic = logic;
        }
        
	public XyOffsetSum( com.xyratex.label.apps.gui.components.XyLabelTemplateImageLogic logic )
	{
            imageLogic = logic;
		offsets = new Vector<XyOffsetComponent>();
	}
	
	public void addOffset( XyOffsetComponent offset )
	{
		offsets.add(offset);
		
		offset.setOverallOffsetManager(this);
		
		offsetHasChanged( offset );
	}
	


	
	
	public int getSumOffsetXAsPx()
	{
    int offsetXAsPx = 0;
		for ( int i = 0; i < offsets.size(); i++ )
		{
			offsetXAsPx += (offsets.get(i)).getXAsDots();
		}
		
		return offsetXAsPx;
	}
	
	public int getSumOffsetYAsPx()
	{
    int offsetYAsPx = 0;
		for ( int i = 0; i < offsets.size(); i++ )
		{
			offsetYAsPx += (offsets.get(i)).getYAsDots();
		}
		
		return offsetYAsPx;
	}
	
	public float getSumOffsetXAsMM()
	{
    float offsetXAsMM = 0.0F;
		for ( int i = 0; i < offsets.size(); i++ )
		{
			offsetXAsMM += (offsets.get(i)).getXAsMM();
		}
		
		return offsetXAsMM;
	}
	
	public float getSumOffsetYAsMM()
	{
    float offsetYAsMM = 0.0F;
		for ( int i = 0; i < offsets.size(); i++ )
		{
			offsetYAsMM += (offsets.get(i)).getYAsMM();
		}
		
		return offsetYAsMM;
	}
	
	public void offsetHasChanged( XyOffsetComponent offset )
	{
		hasChangedFlag = true;
                imageLogic.offsetHasChanged();
	}
        
        public void setLabelImageLogic( XyLabelTemplateImageLogic labelTemplateImageLogic  )
        {
            xyLabelTemplateImageLogic = labelTemplateImageLogic;
        }
}
