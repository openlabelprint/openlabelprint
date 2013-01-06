package org.garret.jipc;

import org.garret.jipc.protocol.JIPCResponse;

/**
 * Excepion thrown when class definition is not found at local system
 * for object (or components of these object) passed through shared memory 
 * or queue
 */ 
public class JIPCClassNotFoundException extends JIPCException { 
    public int getResponseCode() { 
	return JIPCResponse.NOT_FOUND;
    }

    public JIPCClassNotFoundException(String name) {
	this.name = name;
    }

    /**
     * Get name of class which is not found
     */
    public String getName() { 
	return name;
    }

    String name;
}
