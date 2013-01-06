package org.garret.jipc;

import java.io.IOException;

/**
 * Synchronization object for setting exlusive and shared locks.
 * Locks are not nested - if session sets the same lock several time, 
 * all lock requests are succeed but single unlock request will 
 * unlock the object. It is possible to upgrade the lock:
 * if session first lock object in shared mode and then in exclusive
 * mode, lock will be upgraded to exclusive (<B>Attention!</B> upgrading
 * locks can easily cause a deadlock if several session will try to upgrade
 * their shared locks simultaneously).<BR>
 * It is not possible to downgrade the lock - locking of object in shared
 * mode after granted exlusive lock has no effect.<BR>
 * Locks are granted in FIFO order - it means that if object is locked
 * shared mode, then request for exclusive lock comes and is blocked and
 * then if yet another request for shared lock is received, it will
 * be also blocked and placed inqueue <B>after</B> excusive lock request.
 * Server is able to detect deadlock for this primitive.<P>
 * Semantic of methods inherited from <code>JIPCPrimitive</code>:
 * <DL>
 * <DT><code>waitFor</code><DD>set exlusive lock
 * <DT><code>reset</code><DD>removes all locks (shared or exclusive). It is not 
 * required that session, invoking <code>reset</code> method be owner of this lock
 * </DL>
 */ 
public interface JIPCLock extends JIPCPrimitive { 
    /**
     * Set exlusive lock. No other session can set exclusive or shared lock.
     */       
    public void    exclusiveLock() throws JIPCException, IOException;
    
    /**
     * Set exclusive lock with timeout. If lock can not be graned
     * within specifed time, request is failed.
     * @param timeout time in milliseconds
     * @return <code>true</code> if lock is granted, <code>false</code> of timeout
     * is expired
     */
    public boolean exclusiveLock(long timeout) throws JIPCException, IOException;

    /**
     * Set shared lock. No other session can set exclusive lock but
     * other shared locks are possible.
     */
    public void    sharedLock() throws JIPCException, IOException;

    /**
     * Set shared lock with timeout. If lock can not be graned
     * within specifed time, request is failed.
     * @param timeout time in milliseconds
     * @return <code>true</code> if lock is granted, <code>false</code> of timeout
     * is expired
     */
    public boolean sharedLock(long timeout) throws JIPCException, IOException;

    /**
     * Set exlusive lock. No other session can set exclusive or shared lock.
     * @param rank processes will be placed in wait queue in the order of increasing 
     * rank value and in the same order will be taken from the queue.
     */       
    public void    priorityExclusiveLock(int rank) throws JIPCException, IOException;
    
    /**
     * Set exclusive lock with timeout. If lock can not be graned
     * within specifed time, request is failed.
     * @param rank processes will be placed in wait queue in the order of increasing 
     * rank value and in the same order will be taken from the queue.
     * @param timeout time in milliseconds
     * @return <code>true</code> if lock is granted, <code>false</code> of timeout
     * is expired
     */
    public boolean priorityExclusiveLock(int rank, long timeout) throws JIPCException, IOException;

    /**
     * Set shared lock. No other session can set exclusive lock but
     * other shared locks are possible.
     * @param rank processes will be placed in wait queue in the order of increasing 
     * rank value and in the same order will be taken from the queue.
     */
    public void    prioritySharedLock(int rank) throws JIPCException, IOException;

    /**
     * Set shared lock with timeout. If lock can not be graned
     * within specifed time, request is failed.
     * @param rank processes will be placed in wait queue in the order of increasing 
     * rank value and in the same order will be taken from the queue.
     * @param timeout time in milliseconds
     * @return <code>true</code> if lock is granted, <code>false</code> of timeout
     * is expired
     */
    public boolean prioritySharedLock(int rank, long timeout) throws JIPCException, IOException;

    /**
     * Remove lock from the object
     */
    public void    unlock() throws JIPCException, IOException;
}




