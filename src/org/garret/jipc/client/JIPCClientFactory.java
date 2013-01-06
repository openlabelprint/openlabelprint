package org.garret.jipc.client;

import org.garret.jipc.*;
import java.io.IOException;

/** 
 * Factory for remote sessions. Remote sessions should be used to 
 * provide synchronization between different Java processes (processes
 * running in different Java Virtual Machines) at the same or diffrent
 * computers. Before client seesion is create, server process should be started.
 */
public class JIPCClientFactory implements JIPCFactory { 
    public JIPCSession create(String address, int port) throws JIPCException, IOException { 
	return new JIPCSessionStub(address, port);
    }

    public static JIPCFactory getInstance() { 
	return theFactory;
    }
    
    JIPCClientFactory() {}

    static JIPCClientFactory theFactory = new JIPCClientFactory();
}

    
