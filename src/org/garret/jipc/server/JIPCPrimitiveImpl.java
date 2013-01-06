package org.garret.jipc.server;

import org.garret.jipc.*;
import java.io.IOException;
import java.io.PrintStream;

abstract class JIPCPrimitiveImpl implements JIPCPrimitive { 
    JIPCPrimitiveImpl(JIPCServer server, String name) {
	this.server = server;
	this.name = name;
	server.assignId(this);
        wobList = new JIPCWaitObject();
	cs = new JIPCCriticalSection();
    }

    public String getName() { 
	return name;
    }
    
    public boolean alreadyExists() {
	return server.getSession().exists;
    }

    protected void beginAccess(JIPCSessionImpl session) throws JIPCException { 
	accessCount += 1;
    }

    public void close() throws JIPCException, IOException 
    {
	server.getSession().endAccess(this);
    }

    public boolean waitFor(long timeout) throws JIPCException, IOException
    {
        return priorityWait(DEFAULT_RANK, timeout);
    }

    public void waitFor() throws JIPCException, IOException
    {
        priorityWait(DEFAULT_RANK);
    } 
    

    abstract protected void deletePrimitive() throws JIPCException; 

    protected void endAccess(JIPCSessionImpl session) throws JIPCException, IOException { 
	synchronized (server) { 
	    if (--accessCount == 0) { 
		deletePrimitive();
	    }
	}
    }
    
    protected void unlock(JIPCLockObject lob) throws JIPCException {}

    protected boolean detectDeadlock(JIPCWaitObject wob, JIPCSessionImpl session) { 
	return false;
    }

    void addWaitObject(JIPCWaitObject wob) { 
        JIPCWaitObject head = wobList;        
        JIPCWaitObject last = head;
        while ((last = last.prev) != head && last.rank > wob.rank);
        wob.linkAfter(last);
    }

    public String toString() { 
	return getClass().getName() + ":" + name;
    }

    public void dump(PrintStream out) throws JIPCException { 
	out.println(toString());
	out.println("  Access Count: " + accessCount);
	out.println("  Blocked Sessions:");
	cs.enter();
	JIPCWaitObject head = wobList, wob = head; 
        while ((wob = wob.next) != head) { 
	    wob.dump(out);
	}
	cs.leave();
    }

    void wakeupAll() { 
	JIPCWaitObject head = wobList, wob = head; 
        while ((wob = wob.next) != head) { 
	    wob.sendNotification();
	}
        head.prune();
    }

    JIPCServer          server;
    String              name;
    int                 accessCount;
    int                 id;
    JIPCWaitObject      wobList;
    JIPCCriticalSection cs;
};


