package org.garret.jipc;

import java.io.IOException;

/**
 * Classical sempahore with standard set of operations.
 * <P>
 * Semantic of methods inherited from <code>JIPCPrimitive</code>:
 * <DL>
 * <DT><code>waitFor</code><DD>Wait until sempahore counter becomes greater than
 * zero whereupon counter is decremented by 1
 * <DT><code>reset</code><DD>Reset counter to zero
 * </DL>
 */
public interface JIPCSemaphore extends JIPCPrimitive { 
    /**
     * Increment semaphore counter by 1. If there are blocked session
     * one of them is awaken, decrements counter and proceed.
     */
    public void signal() throws JIPCException, IOException;    
    
    /**
     * Add <code>count</code> to sempahore counter. This method is equivalent
     * to <code>count</code> invocations of <code>signal()</code> method.
     * @param count positive value to be added to the semaphore counter
     */
    public void signal(int count) throws JIPCException, IOException;    
}
