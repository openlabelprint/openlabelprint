package org.garret.jipc;

import org.garret.jipc.protocol.JIPCResponse;

/**
 * Exception thrown in case of some internal JIPC error
 */
public class JIPCInternalException extends JIPCException { 
    public int getResponseCode() { 
	return JIPCResponse.INTERNAL_ERROR;
    }
}
