package org.garret.jipc;

import org.garret.jipc.protocol.JIPCResponse;

/**
 * Exception thrown when some of the sessions was interrupted
 */
public class JIPCInterruptedException extends JIPCException { 
    public int getResponseCode() { 
	return JIPCResponse.INTERRUPTED;
    }
}
