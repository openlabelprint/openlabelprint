package org.garret.jipc;

import org.garret.jipc.protocol.JIPCResponse;

/**
 * Exception thrown when requested primitive is not found at server
 */
public class JIPCNotFoundException extends JIPCException { 
    public int getResponseCode() { 
	return JIPCResponse.NOT_FOUND;
    }

    public JIPCNotFoundException() {}

    /**
     * Get name of the primitive which can not be found
     */
    public JIPCNotFoundException(String name) {
	this.name = name;
    }

    public String getName() { 
	return name;
    }

    String name;
}
