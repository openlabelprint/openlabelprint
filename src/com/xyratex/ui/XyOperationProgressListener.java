package com.xyratex.ui;

public interface XyOperationProgressListener
{
	public void operationStarted( XyAbstractOperationProgressListenerRelay relay, String msg );
	  
	public void operationProgress( XyAbstractOperationProgressListenerRelay relay, String msg );
	  
	public void operationComplete( XyAbstractOperationProgressListenerRelay relay, String msg );
	  
	public void operationFail( XyAbstractOperationProgressListenerRelay relay, String msg );
}
