package org.garret.jipc;

import java.io.IOException;

/**
 * JIPC session factory. This class is used to create session instances.
 * To get reference to the factory instance itself, use
 * <code>org.garret.jipc.client.JIPCClientFactory.getInstance()</code> or
 * <code>org.garret.jipc.server.JIPCServer.getInstance()</code> methods.
 */
public interface JIPCFactory { 
    /**
     * Create session instance. For client session, connection with
     * server is established.
     * @param address server host address (ignored for local sessions)
     * @param port server port (ignored for local sessions)
     * @return session object
     */
    public JIPCSession create(String address, int port) throws JIPCException, IOException;
}
