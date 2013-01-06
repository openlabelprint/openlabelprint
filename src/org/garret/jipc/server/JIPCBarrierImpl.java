package org.garret.jipc.server;

import org.garret.jipc.*;
import java.io.PrintStream;

class JIPCBarrierImpl extends JIPCPrimitiveImpl implements JIPCBarrier { 
    JIPCBarrierImpl(JIPCServer server, String name, int nSessions)
    {
	super(server, name);
	this.nSessions = nSessions;
    }
 

    public void priorityWait(int rank) throws JIPCException { 
	cs.enter();
	if (++nBlocked < nSessions) { 
	    server.waitNotification(this, 0, 0);
	} else { 
            wakeupAll();
	    nBlocked = 0;
	    sequenceNo += 1;
	}
	cs.leave();
    }

    public boolean priorityWait(int rank, long timeout) throws JIPCException { 
	cs.enter();
	boolean result = true;
	int sequenceNo = this.sequenceNo;
	if (++nBlocked < nSessions) { 
	    JIPCWaitObject wob = server.waitNotificationWithTimeout(this, timeout, 0, rank);
	    if (!wob.signaled) { 
		if (sequenceNo == this.sequenceNo) { 
		    nBlocked -= 1;
		}
		result = false;
	    }
	} else { 
            wakeupAll();
	    nBlocked = 0;
	    sequenceNo += 1;
	}
	cs.leave();
	return result;
    }

    public void reset() throws JIPCException { 
	cs.enter();
        wakeupAll();
	nBlocked = 0;
	sequenceNo += 1;
	cs.leave();
    }


    protected void deletePrimitive() { 
	server.deleteBarrier(this);
    }

    public void dump(PrintStream out) throws JIPCException { 
	super.dump(out);
	out.println("  Expected number of sessions: " + nSessions);
	out.println("  Blocked number of sessions: " + nBlocked);
    }

    int sequenceNo;
    int nSessions;
    int nBlocked;
}


