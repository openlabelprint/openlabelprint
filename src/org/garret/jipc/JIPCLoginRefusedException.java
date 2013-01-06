package org.garret.jipc;

import org.garret.jipc.protocol.JIPCResponse;

/**
 * Excepion thrown by client session when handshake with server fails
 */ 
public class JIPCLoginRefusedException extends JIPCException { 
    public int getResponseCode() { 
	return JIPCResponse.INTERNAL_ERROR;
    }
}
