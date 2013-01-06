package org.garret.jipc.server;

import org.garret.jipc.*;
import java.io.Serializable;
import java.io.PrintStream;

class JIPCSharedMemoryImpl extends JIPCPrimitiveImpl implements JIPCSharedMemory { 
    JIPCSharedMemoryImpl(JIPCServer server, String name, Serializable obj)
    {
	super(server, name);
	this.obj = obj;
	sequenceNo = 1;
    }
 
    public void priorityWait(int rank) throws JIPCException { 
	cs.enter();
	Thread current = Thread.currentThread();
	if (!(current instanceof JIPCSessionImpl) 
	    || ((JIPCSessionImpl)current).sequenceNo == sequenceNo) 
	{
	    server.waitNotification(this, 0, rank);
	}	
	cs.leave();
    }

    public boolean priorityWait(int rank, long timeout) throws JIPCException { 
	cs.enter();
	boolean result = true;
	Thread current = Thread.currentThread();
	if (!(current instanceof JIPCSessionImpl) 
	    || ((JIPCSessionImpl)current).sequenceNo == sequenceNo) 
	{
	    JIPCWaitObject wob = server.waitNotificationWithTimeout(this, timeout, 0, rank);
	    if (!wob.signaled) { 
		result = false;
	    }
	}	
	cs.leave();
	return result;
    }

    public void set(Serializable obj) throws JIPCException {
	cs.enter();
	this.obj = obj;
	sequenceNo += 1;
        wakeupAll();
	Thread current = Thread.currentThread();
	if (current instanceof JIPCSessionImpl) { 
	    ((JIPCSessionImpl)current).sequenceNo = sequenceNo;
	}
	cs.leave();
    }

    public Serializable get() throws JIPCException {
	Thread current = Thread.currentThread();
	if (current instanceof JIPCSessionImpl) { 
	    ((JIPCSessionImpl)current).sequenceNo = sequenceNo;
	}
	return obj;
    }

    public void reset() {}
    
    protected void deletePrimitive() { 
	server.deleteSharedMemory(this);
    }

    public void dump(PrintStream out) throws JIPCException { 
	super.dump(out);
	out.println("  Object: " + obj);
	out.println("  Sequence Number: " + sequenceNo);
    }

    long         sequenceNo;
    Serializable obj;
}


