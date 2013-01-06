package org.garret.jipc.server;

import org.garret.jipc.*;
import java.io.PrintStream;

class JIPCSemaphoreImpl extends JIPCPrimitiveImpl implements JIPCSemaphore { 
    JIPCSemaphoreImpl(JIPCServer server, String name, int initValue)
    {
	super(server, name);
	count = initValue;
    }
 

    public void priorityWait(int rank) throws JIPCException { 
	cs.enter();
	if (count > 0) { 
	    count -= 1;
	} else { 
	    server.waitNotification(this, 0, rank);
	}	
	cs.leave();
    }

    public boolean priorityWait(int rank, long timeout) throws JIPCException { 
	cs.enter();
	boolean result = true;
	if (count > 0) { 
	    count -= 1;
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
	signal(1);
    }

    public void signal(int n)  throws JIPCException {
	if (n < 0) { 
	    throw new JIPCInvalidParameterException();
	}
	cs.enter();
	while (n > 0 && !wobList.isEmpty()) { 	    
            JIPCWaitObject wob = wobList.next;
	    wob.sendNotification();
            wob.unlink();
	    n -= 1;
	}
	count += n;
	cs.leave();
    }
   
    public void reset() throws JIPCException { 
	cs.enter();
	count = 0;
	cs.leave();
    }


    protected void deletePrimitive() { 
	server.deleteSemaphore(this);
    }

    public void dump(PrintStream out) throws JIPCException { 
	super.dump(out);
	out.println("  Counter: " + count);
    }

    int count;
}


