package org.garret.jipc;

import org.garret.jipc.protocol.JIPCResponse;

/**
 * Exception thrown when deadlock is detected by server
 */
public class JIPCDeadlockException extends JIPCException { 
    public int getResponseCode() { 
	return JIPCResponse.DEADLOCK;
    }
}
