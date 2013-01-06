package org.garret.jipc;

import java.io.IOException;
import java.io.Serializable;

/**
 * Shared memory object. This class is used to organize
 * replication of data between all listeners of this object.
 * Listener should use wait method to detect moment when
 * object is updated by <code>set</code> method by some other session. 
 * Then it should call <code>get</code> method to get the most recent
 * version of the object. If client prefer to receive updates asynchronously,
 * it should lanunch separate thread which will preform wait in loop for
 * updates of this object, thne get the most recent version of the object
 * and somehow notify other threads that object is changed.<P>
 * Semantic of methods inherited from <code>JIPCPrimitive</code>:
 * <DL>
 * <DT><code>waitFor</code><DD>Wait until version of the object at client
 * becomes deteriorated. For local sessions, this metod always wait
 * until <code>set</code> method is invoked. For remote sessions, 
 * sequence number of the object is remembered by local stub and 
 * is sent to the server to be compared with sequence number of shared
 * memory object at the server.
 * <DT><code>reset</code><DD>Do nothing
 * </DL>
 */
public interface JIPCSharedMemory extends JIPCPrimitive { 
    /**
     * Make new objet visible to all other sessions using this shared memory
     * object
     * @param obj and non-null serializable object (implementing <code>java.io.Serializable</code> interface)
     */
    public void         set(Serializable obj) throws JIPCException, IOException;

    /**
     * Get the most recent version of shared memory object.
     * If class of the object (or of it's components) placed 
     * in the shared memory by some other client can not be located at this system,
     * then <code>JIPCClassNotFoundException</code> will be thrown.     
     * @return most recent object placed in shared memory
     */
    public Serializable get() throws JIPCException, IOException;
}
