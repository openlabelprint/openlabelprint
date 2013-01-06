package org.garret.jipc.server;

import org.garret.jipc.*;
import java.io.PrintStream;

class JIPCMutexImpl extends JIPCPrimitiveImpl implements JIPCMutex {
    protected boolean detectDeadlock(JIPCWaitObject wob, JIPCSessionImpl session) { 
	JIPCSessionImpl owner = this.owner;
	if (owner != null) { 
	    if (owner == session) { 
		return true;
	    } else if ((wob = owner.waitFor) != null) { 
		return wob.detectDeadlock(session);
	    }
	}
	return false;
    }

    public void lock() throws JIPCException { 
        priorityLock(DEFAULT_RANK);
    }

    public void priorityLock(int rank) throws JIPCException { 
	JIPCSessionImpl session = server.getSession();
	cs.enter();
	if (owner == null || owner == session) {
	    if (owner == null) { 
		owner = session;
		counter = 1;
		lock = session.addLock(this, 0);
	    } else { 
		counter += 1;
	    }
	} else { 
	    JIPCWaitObject wob;
	    server.cs.enter();
	    if ((wob = owner.waitFor) != null) { 
		if (wob.detectDeadlock(session)) { 
		    server.cs.leave();
		    cs.leave();
		    throw new JIPCDeadlockException();
		}
	    }
	    server.waitNotification(this, JIPCServer.LOCKED_SERVER, rank);
	}
	cs.leave();
    }
    
    public boolean lock(long timeout) throws JIPCException { 
        return priorityLock(DEFAULT_RANK, timeout);
    }

    public boolean priorityLock(int rank, long timeout) throws JIPCException { 
	JIPCSessionImpl session = server.getSession();
	boolean result = true;
	cs.enter();       
	if (owner == null || owner == session) {
	    if (owner == null) { 
		owner = session;
		counter = 1;
		lock = session.addLock(this, 0);
	    } else { 
		counter += 1;
	    }
	} else { 
	    JIPCWaitObject wob = server.waitNotificationWithTimeout(this, timeout, 0, rank);
	    if (!wob.signaled) {
		result = false;
	    }
	}
	cs.leave();
	return result;
    }
    

    public void unlock() throws JIPCException {
	JIPCSessionImpl session = server.getSession();
	if (owner != session) { 
	    throw new JIPCNotOwnerException();
	}
	cs.enter();
	if (--counter == 0) { 	
	    owner = null;
	    session.removeLock(lock);	
	    lock = null;
	    if (!wobList.isEmpty()) { 
                JIPCWaitObject wob = wobList.next;
		wob.sendNotification();
                wob.unlink();
		owner = wob.session;
		counter = 1;
		lock = owner.addLock(this, 0);
	    }
	}
	cs.leave();
    }

    protected void unlock(JIPCLockObject lob) throws JIPCException { 
	cs.enter();
	if (lock != null) { 
	    owner = null;
	    counter = 0;
	    lock.owner.removeLock(lock);	
	    lock = null;
	    if (!wobList.isEmpty()) { 
                JIPCWaitObject wob = wobList.next;
		wob.sendNotification();
		wob.unlink();
		owner = wob.session;
		counter = 1;
		lock = owner.addLock(this, 0);
	    }
	}
	cs.leave();
    }

	
    public void priorityWait(int rank) throws JIPCException { 
	priorityLock(rank);
    }

    public boolean priorityWait(int rank, long timeout) throws JIPCException { 
	return priorityLock(rank, timeout);
    }

    public void reset() throws JIPCException {
	unlock(lock);
    }

    JIPCMutexImpl(JIPCServer server, String name, boolean locked) { 
	super(server, name);
	if (locked) { 
	    owner = server.getSession();
	    counter = 1;
	    lock = owner.addLock(this, 0);
	}
    }

    protected void deletePrimitive() {
	server.deleteMutex(this);
    }

    public void dump(PrintStream out) throws JIPCException { 
	super.dump(out);
	out.println("  Owner: "   + owner);
	out.println("  Nesting: " + counter);
    }

    JIPCSessionImpl owner;
    JIPCLockObject  lock;
    int             counter;
}
		  




