package org.garret.jipc.server;

import org.garret.jipc.*;
import java.io.PrintStream;

class JIPCWaitObject { 
    JIPCWaitObject      next;
    JIPCWaitObject      prev;
    JIPCPrimitiveImpl   prim;
    JIPCSessionImpl     session;
    int                 flags;
    int                 rank;
    boolean             signaled;
    Object              data;

    void sendNotification() { 
	sendNotification(null);
    }

    synchronized void sendNotification(Object data) { 
	session.waitFor = null;
	this.data = data;
	signaled = true;
	notify();
    }

    void waitNotification() throws JIPCException { 
	synchronized (this) {
	    prim.cs.leave();
	    try { 
		wait();
	    } catch (InterruptedException x) { 
		throw new JIPCInterruptedException();
	    }
	}
	prim.cs.enter();
    }
	
    void waitNotificationWithTimeout(long timeout) throws JIPCException { 
	synchronized (this) {
	    prim.cs.leave();
	    try { 
		wait(timeout);	    
	    } catch (InterruptedException x) { 
		throw new JIPCInterruptedException();
	    }
	}
	prim.cs.enter();
    }
	
    boolean detectDeadlock(JIPCSessionImpl session) { 
	return ((flags & JIPCServer.TIMED_WAIT) == 0) ? prim.detectDeadlock(this, session) : false;
    }

    JIPCWaitObject(JIPCSessionImpl session, JIPCPrimitiveImpl prim, int flags, int rank) { 
	this.session = session;
	this.flags = flags;
	this.prim = prim;
        this.rank = rank;
    }

    JIPCWaitObject() { 
        prune();
    }

    final void linkAfter(JIPCWaitObject after) { 
        prev = after;
        next = after.next;
        next.prev = this;
        after.next = this;
    }
    
    final void unlink() { 
        next.prev = prev;
        prev.next = next;
    }
    
    final void prune() { 
        next = prev = this;
    }

    final boolean isEmpty() { 
        return next == this;
    }

    public void dump(PrintStream out) { 
	out.println("    " + session + " flags=" + flags + " rank=" + rank);
    }
}




