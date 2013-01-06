package org.garret.jipc;

import java.io.IOException;

/**
 * Synchronization object providing mutual exclusion.
 * Mutex object supports nested locks, it means that if 
 * some session locks mutex <code>N</code> times, it should
 * unlock it <code>N</code> time to release the mutex.
 * Server is able to detect deadlock for mutexes.<P>
 * Semantic of methods inherited from <code>JIPCPrimitive</code>:
 * <DL>
 * <DT><code>waitFor</code><DD>lock the mutex
 * <DT><code>reset</code><DD>release the mutex (remove all locks).
 * It is not required that session, invoking <code>reset</code> method be owner
 * of the mutex
 * </DL>
 * </DL>
 */
public interface JIPCMutex extends JIPCPrimitive { 
    /**
     * Lock mutex. Only one session can lock mutex each moment of time.
     */
    public void lock() throws JIPCException, IOException;
    
    /**
     * Try to lock the mutex with specified timeout. If lock can not be granted
     * within specified time, request is failed.
     * @param timeout time in milliseconds
     * @return <code>true</code> if lock is granted, <code>false</code> of timeout
     * is expired
     */
    public boolean lock(long timeout) throws JIPCException, IOException;

    /**
     * Lock mutex. Only one session can lock mutex each moment of time.
     * @param rank processes will be placed in wait queue in the order of increasing 
     * rank value and in the same order will be taken from the queue.
     */
    public void priorityLock(int rank) throws JIPCException, IOException;
    
    /**
     * Try to lock the mutex with specified timeout. If lock can not be granted
     * within specified time, request is failed.
     * @param rank processes will be placed in wait queue in the order of increasing 
     * rank value and in the same order will be taken from the queue.
     * @param timeout time in milliseconds
     * @return <code>true</code> if lock is granted, <code>false</code> of timeout
     * is expired
     */
    public boolean priorityLock(int rank, long timeout) throws JIPCException, IOException;

    /**
     * Unlock mutex. This method release the mutex if number of unlocks
     * is equal to number of locks
     */
    public void unlock() throws JIPCException, IOException;
}
