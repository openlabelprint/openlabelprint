package com.xyratex.ui;

import javax.swing.JComponent;


public class XyAbstractOperationProgressListenerRelay 
{
	protected XyOperationProgressListener xyOperationProgressListener = null;
	
	protected JComponent triggeringComponent = null;
	
	public XyAbstractOperationProgressListenerRelay( JComponent jComponent, XyOperationProgressListener operationProgressListener )
	{
		triggeringComponent = jComponent;
		xyOperationProgressListener = operationProgressListener;
	}
	
	public JComponent getTriggeringComponent()
	{
		return triggeringComponent;
	}
}