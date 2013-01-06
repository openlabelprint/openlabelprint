package org.garret.jipc.server;

import org.garret.jipc.*;

class JIPCCriticalSection {
    public synchronized void enter() throws JIPCException { 
        if (locked || nBlocked != 0) { 
	    do { 
		try { 
		    nBlocked += 1;
		    wait();
		    nBlocked -= 1;
		} catch (InterruptedException x) { 
		    nBlocked -= 1;
		    notify();
		    throw new JIPCInterruptedException();
		}
	    } while (locked);
	}
	locked = true;
    }

    public synchronized void leave() throws JIPCException { 
	locked = false;
	if (nBlocked != 0) { 
	    notify();
	}
    }

    protected boolean locked;
    protected int     nBlocked;
} 


