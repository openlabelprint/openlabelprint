package org.garret.jipc.server;

import org.garret.jipc.*;
import java.io.PrintStream;

class JIPCEventImpl extends JIPCPrimitiveImpl implements JIPCEvent { 
    JIPCEventImpl(JIPCServer server, String name, boolean signaled, boolean manualReset)
    {
	super(server, name);
	this.signaled = signaled;
	this.manualReset = manualReset;
    }
 

    public void priorityWait(int rank) throws JIPCException { 
	cs.enter();
	if (signaled) { 
	    if (!manualReset) { 
		signaled = false;
	    }	    
	} else { 
	    server.waitNotification(this, 0, rank);
	}	
	cs.leave();
    }

    public boolean priorityWait(int rank, long timeout) throws JIPCException { 
	boolean result = true;
	cs.enter();
	if (signaled) { 
	    if (!manualReset) { 
		signaled = false;
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

    public void signal() throws JIPCException { 
	cs.enter();
	if (wobList.isEmpty()) { 
	    signaled = true;
	} else {
	    if (manualReset) {
                wakeupAll();
		signaled = true;
	    } else { 
		wobList.next.sendNotification();
		wobList.next.unlink();
	    }
	}
	cs.leave();
    }

    public void pulse() throws JIPCException { 
	cs.enter();
	if (manualReset) { 
            wakeupAll();
	} else if (!wobList.isEmpty()) { 
	    wobList.next.sendNotification();
	    wobList.next.unlink();
	}
	cs.leave();
    }

    public void reset() throws JIPCException { 
	cs.enter();
	signaled = false;
	cs.leave();
    }
   
    protected void deletePrimitive() throws JIPCException { 
	server.deleteEvent(this);
    }

    public void dump(PrintStream out) throws JIPCException { 
	super.dump(out);
	out.println("  Signaled: " + signaled);
	out.println("  Manual Reset: " + manualReset);
    }

    boolean signaled;
    boolean manualReset;
}


