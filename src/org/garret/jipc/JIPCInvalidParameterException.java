package org.garret.jipc;

import org.garret.jipc.protocol.JIPCResponse;

/**
 * Exception thrown when some of the method's parameters is invalid
 */
public class JIPCInvalidParameterException extends JIPCException { 
    public int getResponseCode() { 
	return JIPCResponse.INVALID_PARAMETER;
    }
}
