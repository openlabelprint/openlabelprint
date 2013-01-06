package org.garret.jipc;

import org.garret.jipc.protocol.JIPCResponse;

/**
 * Exception thrown when session tries to unlock the object for which
 * it is not owner (session is not granted lock for this object)
 */
public class JIPCNotOwnerException extends JIPCException { 
    public int getResponseCode() { 
	return JIPCResponse.NOT_OWNER;
    }
}
