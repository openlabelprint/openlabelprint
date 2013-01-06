package org.garret.jipc;

import java.io.IOException;
import java.io.Serializable;
import java.io.PrintStream;

/**
 * JIPC session. Session can be remote and local. Remote sessions
 * establish conntection with the server through TCP/IP stream socket. 
 * In this case primitives
 * at local computer servers as stubs and redirect requests to the server.
 * This mode is useful to provide sycnhronization and communication
 * between several Java processes at the same or different computers.
 * Local sessions are useful to provide synhcronization of threads within the same
 * process. The single local session can be shared by all threads.<br>
 * Session interface provides methods for creating sycnhronization objects.
 * Name of the object is unique with objects of the same type 
 * (events, semaphores,...). It is possible that, for example mutex and event
 * has the same name.
 */
public interface JIPCSession { 
    /**
     * Create or return existed event synchronization object.
     * @param name unique event name. 
     * @param signaled initial state of the event, ignored if mutex already exists
     * @param manualReset mode of the event, ignored if mutex already exists
     * @return created or existed event, use <code>alreadExists</code> method
     * to check if new object is created
     */
    public JIPCEvent createEvent(String name, boolean signaled, boolean manualReset) throws JIPCException, IOException;

    /**
     * Open existed event.
     * @param name event name
     * @return event or <code>null</code> if event with such name doesn't exists
     */
    public JIPCEvent openEvent(String name) throws JIPCException, IOException;

    /**
     * Create or return existed semaphore synchronization object.
     * @param name unique semaphore name. 
     * @param initCount non-negative initial value of semaphore counter, ignored if mutex already exists
     * @return created or existed semaphore, use <code>alreadExists</code> method
     * to check if new object is created
     */
    public JIPCSemaphore createSemaphore(String name, int initCount) throws JIPCException, IOException;

    /**
     * Open existed semaphore.
     * @param name semaphore name
     * @return semaphore or <code>null</code> if semaphore with such name doesn't exists
     */
    public JIPCSemaphore openSemaphore(String name) throws JIPCException, IOException;
   
    /**
     * Create or return existed mutex synchronization object.
     * @param name unique mutex name. 
     * @param locked initial state of the mutex, if <code>true</code>
     * then it is owned by session created this mutex, ignored if mutex already exists
     * @return created or existed mutex, use <code>alreadExists</code> method
     * to check if new object is created
     */
    public JIPCMutex createMutex(String name, boolean locked) throws JIPCException, IOException;
   /**
     * Open existed mutex.
     * @param name mutex name
     * @return mutex or <code>null</code> if mutex with such name doesn't exists
     */
    public JIPCMutex openMutex(String name) throws JIPCException, IOException;
    
    /**
     * Create or return existed queue bject.
     * @param name unique queue name. 
     * @return created or existed queue, use <code>alreadExists</code> method
     * to check if new object is created
     */
    public JIPCQueue createQueue(String name) throws JIPCException, IOException;
   /**
     * Open existed queue.
     * @param name queue name
     * @return queue or <code>null</code> if queue with such name doesn't exists
     */
    public JIPCQueue openQueue(String name) throws JIPCException, IOException;

    /**
     * Create or return existed shared memory object.
     * @param name unique shared memory name. 
     * @param obj non-null initial value of shared memory object
     * @return created or existed shared memory, use <code>alreadExists</code> method
     * to check if new object is created
     */
    public JIPCSharedMemory createSharedMemory(String name, Serializable obj) throws JIPCException, IOException;

    /**
     * Create or return existed shared memory object.
     * @param name unique shared memory name. 
     * @return created or existed shared memory, use <code>alreadExists</code> method
     * to check if new object is created
     */
     public JIPCSharedMemory openSharedMemory(String name) throws JIPCException, IOException;
    
    /**
     * Create or return existed lock synchronization object.
     * @param name unique lock name. 
     * @return created or existed lock, use <code>alreadExists</code> method
     * to check if new object is created
     */
    public JIPCLock createLock(String name) throws JIPCException, IOException;
    /**
     * Open existed lock.
     * @param name lock name
     * @return lock or <code>null</code> if lock with such name doesn't exists
     */
    public JIPCLock openLock(String name) throws JIPCException, IOException;

    /**
     * Create or return existed barrier synchronization object.
     * @param name unique barrier name. 
     * @param nSessions positive number of sessions to be synchronized on barrier
     * @return created or existed barrier, use <code>alreadExists</code> method
     * to check if new object is created
     */
    public JIPCBarrier createBarrier(String name, int nSessions) throws JIPCException, IOException;
    /**
     * Open existed barrier.
     * @param name barrier name
     * @return barrier or <code>null</code> if barrier with such name doesn't exists
     */
    public JIPCBarrier openBarrier(String name) throws JIPCException, IOException;

    /**
     * Close session. This method disconnects client from server, release
     * all locks hold by this session and close all primitives opened
     * by this session.<BR>
     * <b>Attention!</b>You should no call this method for local sessions
     * if it is shared by multiple threads
     */
    public void close() throws JIPCException, IOException;

    /**
     * Terminate server. This method is used by <code>JIPCServerMonitor</code>
     * and should not be used by normal client unless it wants to monitor
     * server itself. This method do nothing for local sessions.
     * You should not execute any other session method after this this method.
     */
    public void shutdownServer() throws JIPCException, IOException;

    /**
     * Get information about server state. This method is used by <code>JIPCServerMonitor</code>
     * and should not be used by normal client unless it wants to monitor
     * server itself.
     * @param out prnit stream to which information will be outputed
     */
    public void showServerInfo(PrintStream stream) throws JIPCException, IOException;

    /**
     * Checks whether it is local session
     * @return <code>true</code> is session was created by means of 
     * <code>org.garret.jipc.server.JIPCServer</code> factory, <code>false</code> - if
     * session eas created by  <code>org.garret.jipc.client.JIPCClientFactory</code>.
     */
    public boolean isLocal();
};
