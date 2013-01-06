package org.garret.jipc.server;

import org.garret.jipc.*;
import java.io.PrintStream;

class JIPCLockImpl extends JIPCPrimitiveImpl implements JIPCLock {
    static final int EXCLUSIVE_LOCK = 1;    
    static final int SHARED_LOCK    = 2;

    protected boolean detectDeadlock(JIPCWaitObject wob, JIPCSessionImpl session) { 
	JIPCLockObject writer = this.writer;
	if (writer != null) {
	    if (writer.owner == session) { 
		return true;
	    } else if ((wob = writer.owner.waitFor) != null) { 
		return wob.detectDeadlock(session);
	    }
	} else { 
	    JIPCWaitObject rwob;
	    for (JIPCLockObject reader = readers; reader != null; reader = reader.nextOwner) { 
		if (reader.owner == session) {
		    return true;
		} else if (reader.owner != wob.session && (rwob = reader.owner.waitFor) != null) { 
		    if (rwob.detectDeadlock(session)) { 
			return true;
		    }
		} 
	    }
	}		    		    
	return false;
    }

    public void exclusiveLock() throws JIPCException { 
        priorityExclusiveLock(DEFAULT_RANK);
    }

    public void priorityExclusiveLock(int rank) throws JIPCException { 
	JIPCSessionImpl session = server.getSession();
	cs.enter();
	if (writer == null || writer.owner != session) { 
	    if (writer == null 
		&& (readers == null || readers.owner == session && readers.nextOwner == null))
	    {
		if (readers != null) { 
		    readers.flags = EXCLUSIVE_LOCK;
		    writer = readers;
		    readers = null;
		} else { 
		    writer = session.addLock(this, EXCLUSIVE_LOCK);
		}
	    } else {
		JIPCWaitObject wob;
		server.cs.enter(); 
		if (writer != null) { 
		    if ((wob = writer.owner.waitFor) != null) { 
			if (wob.detectDeadlock(session)) {
			    server.cs.leave();
			    cs.leave();
			    throw new JIPCDeadlockException();
			}
		    } 
		} else {
		    for (JIPCLockObject lob = readers; lob != null; lob = lob.nextOwner) { 
			if (lob.owner == session) { 
                            JIPCWaitObject head = wobList;
                            wob = head;
                            while ((wob = wob.next) != head) { 
				if ((wob.flags & EXCLUSIVE_LOCK) != 0) { 
				    server.cs.leave();
				    cs.leave();
				    throw new JIPCDeadlockException();
				}
			    }
			} else if ((wob = lob.owner.waitFor) != null) { 
			    if (wob.detectDeadlock(session)) {
				server.cs.leave();
				cs.leave();
				throw new JIPCDeadlockException();
			    }
			}
		    }			
		}
		server.waitNotification(this, JIPCServer.LOCKED_SERVER|EXCLUSIVE_LOCK, rank);
	    }
	}
	cs.leave();
    }
    

    public boolean exclusiveLock(long timeout) throws JIPCException { 
        return priorityExclusiveLock(DEFAULT_RANK, timeout);
    }

    public boolean priorityExclusiveLock(int rank, long timeout) throws JIPCException { 
	JIPCSessionImpl session = server.getSession();
	boolean result = true;
	cs.enter();
	if (writer == null || writer.owner != session) { 	    
	    if (writer == null 
		&& (readers == null || readers.owner == session && readers.nextOwner == null))
	    {
		if (readers != null) { 
		    readers.flags = EXCLUSIVE_LOCK;
		    writer = readers;
		    readers = null;
		} else { 
		    writer = session.addLock(this, EXCLUSIVE_LOCK);
		}
	    } else { 
		JIPCWaitObject wob = server.waitNotificationWithTimeout(this, timeout, EXCLUSIVE_LOCK, rank);
		if (!wob.signaled) {
		    result = false;
		}
	    }
	}
	cs.leave();
	return result;
    }
    
    public void sharedLock() throws JIPCException { 
        prioritySharedLock(DEFAULT_RANK);
    }

    public void prioritySharedLock(int rank) throws JIPCException { 
	JIPCSessionImpl session = server.getSession();
	cs.enter();
	if (writer == null || writer.owner != session) { 	    
	    if (writer == null) {  	
		JIPCLockObject lob;
		for (lob = readers; lob != null && lob.owner != session; lob = lob.nextOwner);
		if (lob == null) { 
		    lob = session.addLock(this, SHARED_LOCK);
		    server.cs.enter(); 
		    lob.nextOwner = readers;
		    readers = lob;
		    server.cs.leave(); 		    
		}
	    } else { 
		JIPCWaitObject wob;
		server.cs.enter(); 
		if ((wob = writer.owner.waitFor) != null) { 
		    if (wob.detectDeadlock(session)) { 
			server.cs.leave();
			cs.leave();
			throw new JIPCDeadlockException();
		    }
		} 
		server.waitNotification(this, JIPCServer.LOCKED_SERVER|SHARED_LOCK, rank);
	    }
	}
	cs.leave();
    }
    

    public boolean sharedLock(long timeout) throws JIPCException { 
        return prioritySharedLock(DEFAULT_RANK, timeout);
    }

    public boolean prioritySharedLock(int rank, long timeout) throws JIPCException { 
	JIPCSessionImpl session = server.getSession();
	boolean result = true;
	cs.enter();
	if (writer == null || writer.owner != session) { 	    
	    if (writer == null) {  	
		JIPCLockObject lob;
		for (lob = readers; lob != null && lob.owner != session; lob = lob.nextOwner);
		if (lob == null) { 
		    lob = session.addLock(this, SHARED_LOCK);
		    server.cs.enter(); 
		    lob.nextOwner = readers;
		    readers = lob;
		    server.cs.leave(); 
		}
	    } else { 
		JIPCWaitObject wob = server.waitNotificationWithTimeout(this, timeout, SHARED_LOCK, rank);
		if (!wob.signaled) {
		    result = false;
		}
	    }
	}
	cs.leave();
	return result;
    }
   

    public void unlock() throws JIPCException {
	unlock(server.getSession());
    }

    protected void unlock(JIPCLockObject lob) throws JIPCException { 
	unlock(lob.owner);
    }

    protected void retry() throws JIPCException { 
	JIPCWaitObject head = wobList;
	JIPCWaitObject wob = head;
	while ((wob = head.next) != head
               && ((wob.flags & EXCLUSIVE_LOCK) == 0 || readers == null 
                   || (readers.owner == wob.session && readers.nextOwner == null)))
	{
	    wob.sendNotification();
            wob.unlink();
	    if ((wob.flags & EXCLUSIVE_LOCK) == 0) { 
		JIPCLockObject lob = wob.session.addLock(this, SHARED_LOCK);
		server.cs.enter();
		lob.nextOwner = readers;
		readers = lob;
		server.cs.leave();
	    } else { 
		if (readers != null) {
		    readers.flags = EXCLUSIVE_LOCK;
		    writer = readers;
		    readers = null;
		} else {
		    writer = wob.session.addLock(this, EXCLUSIVE_LOCK);
		}
		break;
	    }
	}
    }
	
    void unlock(JIPCSessionImpl session) throws JIPCException {
	cs.enter();
	if (writer != null) {
	    if (writer.owner != session) { 
		cs.leave();
		throw new JIPCNotOwnerException();
	    }
	    session.removeLock(writer);
	    writer = null;
	} else { 
	    JIPCLockObject lob, prev;
	    for (lob=readers, prev=null; lob != null && lob.owner != session; prev=lob, lob=lob.nextOwner);
	    if (lob == null) { 
		cs.leave();
		throw new JIPCNotOwnerException();
	    }
	    if (prev == null) { 
		readers = lob.nextOwner;
	    } else { 
		prev.nextOwner = lob.nextOwner;
	    }
	    session.removeLock(lob);
	}
	retry();
	cs.leave();
    }

    public void reset() throws JIPCException {
	cs.enter();
	if (writer != null) {
	    writer.owner.removeLock(writer);
	    writer = null;
	} else { 
	    for (JIPCLockObject lob = readers; lob != null; lob = lob.nextOwner) { 
		lob.owner.removeLock(lob);
	    }
	    readers = null;
	}
	retry();
	cs.leave();
    }

    public void priorityWait(int rank) throws JIPCException { 
	priorityExclusiveLock(rank);
    }

    public boolean priorityWait(int rank, long timeout) throws JIPCException { 
	return priorityExclusiveLock(rank, timeout);
    }

    JIPCLockImpl(JIPCServer server, String name) { 
	super(server, name);
    }

    protected void deletePrimitive() {
	server.deleteLock(this);
    }

    public void dump(PrintStream out) throws JIPCException { 
	super.dump(out);
	cs.enter();
	if (writer != null) { 
	    out.println("  Exclusive Lock: " + writer.owner);
	} else if (readers != null) { 
	    out.println("  Shared Locks: ");
	    for (JIPCLockObject lob = readers; lob != null; lob = lob.nextOwner) { 
		out.println("    " + lob.owner);
	    }
	}
	cs.leave();
    }

    JIPCLockObject writer;
    JIPCLockObject readers;
}
		  




