package org.garret.jipc;

import java.io.IOException;
import java.io.Serializable;

/**
 * FIFO queue primitive. This primitive allows to pass data between 
 * consumer and producer. Elements are fetched by consumer in First-In-First-Out
 * order. It is possible to pass objects of any type in the queue.
 * The only requirement is that it should be <I>serializable</i> (implements
 * <code>java.io.Serializable</code> interface).<br>
 * If class of the object (or of it's components) placed 
 * in the queue by producer can not be located by consumer, then
 * <code>JIPCClassNotFoundException</code> will be thrown at the consumer.<br>
 * Length of message queue is unlimited.<br>
 * If more than one consumer tries to get element from the queue, 
 * thie requests will be satisfied in FIFO order.<P>
 * Semantic of methods inherited from <code>JIPCPrimitive</code>:
 * <DL>
 * <DT><code>waitFor</code><DD>Wait until queue becomes not empty. If
 * queue contains some elements then method immediatly returns. This
 * method doesn't change the state of the queue.
 * <DT><code>reset</code><DD>Removes all elements from the queue
 * </DL>
 */
public interface JIPCQueue extends JIPCPrimitive { 
    /**
     * Get element from the queue. If queue is empty this method
     * waits until somebody else put element in the queue.<br>
     * If class of the object (or of it's components) placed 
     * in the queue by producer can not be located at this system, then
     * <code>JIPCClassNotFoundException</code> will be thrown.
     * @return first element in queue
     */
    public Serializable get() throws JIPCException, IOException;

    /**
     * Get element from the queue with timeout. 
     * @return <code>null</code> if timeout is expired before
     * any element was placed in the queue.
     */
    public Serializable get(long timeout) throws JIPCException, IOException;

    /**
     * Get element from the queue. If queue is empty this method
     * waits until somebody else put element in the queue.<br>
     * If class of the object (or of it's components) placed 
     * in the queue by producer can not be located at this system, then
     * <code>JIPCClassNotFoundException</code> will be thrown.
     * @param rank processes will be placed in wait queue in the order of increasing 
     * rank value and in the same order will be taken from the queue.
     * @return first element in queue
     */
    public Serializable priorityGet(int rank) throws JIPCException, IOException;

    /**
     * Get element from the queue with timeout. 
     * @param rank processes will be placed in wait queue in the order of increasing 
     * rank value and in the same order will be taken from the queue.
     * @param timeout operation timeout in millisoconds
     * @return <code>null</code> if timeout is expired before
     * any element was placed in the queue.
     */
    public Serializable priorityGet(int rank, long timeout) throws JIPCException, IOException;

    /**
     * Put element at the end of the queue.
     * @param obj any not-null serializable object
     */
    public void put(Serializable obj) throws JIPCException, IOException;

    /**
     * Broadcast message to all sessions currently connected to the queue.
     * The message will no be deleted from the queue until all session read this 
     * @param obj any not-null serializable object
     */
    public void broadcast(Serializable obj) throws JIPCException, IOException;

    /**
     * Get number of elements in queue
     * @return number of elements in queue
     */
    public int  size() throws JIPCException, IOException;
}



