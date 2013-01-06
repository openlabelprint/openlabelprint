package org.garret.jipc;

import java.io.IOException;

/**
 * Base class for all synchronizatin primitives.
 * Semantic of <code>waitFor</code> and <code>reset</code> methods 
 * for particular primitives explained in the descripotion of these proimitives
 */
public interface JIPCPrimitive { 
    /**
     * Get primitive name. Name of the primitive is unique with primitives
     * of the same type (events, semaphores,...). It is possible
     * that, for example, event and mutex has the same name.
     * @return primitive name
     */
    String getName();
    
    /**
     * Primitive returned by <code>createXXX</code> method already exists
     * This method should be call immediatly after <code>createXXX</code>
     * to check if new primitive was created or existed one was returned.
     * @return <code>true</code> if <code>createXXX</code> method doesn't
     * create new primitive
     */
    public boolean alreadyExists();
    
    /**
     * Wait until state of primitive is switched. Semantic of this method
     * depends on particular primitive type and is explained in specification
     * of each primitive.
     */
    public void    waitFor() throws JIPCException, IOException;

    /**
     * Wait until state of primitive is switched with timeout. 
     * Semantic of this method depends on particular primitive type and is 
     * explained in specification of each primitive.
     * @param timeout operation timeout in millisoconds
     * @return <code>false</code> if timeout is expired before primitive
     * state is changed
     */
    public boolean waitFor(long timeout) throws JIPCException, IOException;

    /**
     * Rank with which requsts will be queued if rank was not explicitely specified. 
     */
    int DEFAULT_RANK = 0;

    /**
     * Priority wait until state of primitive is switched. 
     * Requests with the lowest <code>rank</code> value will be satisfied first.
     * Semantic of this method depends on particular primitive type and is explained 
     * in specification of each primitive.
     * @param rank processes will be placed in wait queue in the order of increasing 
     * rank value and in the same order will be taken from the queue.
     */
    public void    priorityWait(int rank) throws JIPCException, IOException;

    /**
     * Priority wait until state of primitive is switched with timeout. 
     * Requests with the lowest <code>rank</code> value will be satisfied first.
     * Semantic of this method depends on particular primitive type and is 
     * explained in specification of each primitive.
     * @param rank processes will be placed in wait queue in the order of increasing 
     * rank value and in the same order will be taken from the queue.
     * @param timeout operation timeout in millisoconds
     * @return <code>false</code> if timeout is expired before primitive
     * state is changed
     */
    public boolean priorityWait(int rank, long timeout) throws JIPCException, IOException;

    /**
     * Reset primitive.  Semantic of this method
     * depends on particular primitive type and is explained in specification
     * of each primitive.
     */
    public void    reset() throws JIPCException, IOException;

    /**
     * Close primitive. This method decrease access counter of the primitive
     * and once it becomes zero, primitive is destroyed.
     */
    public void    close() throws JIPCException, IOException;
}    

