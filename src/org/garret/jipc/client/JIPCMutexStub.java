package org.garret.jipc.client;

import org.garret.jipc.*;
import org.garret.jipc.protocol.*;
import java.io.IOException;

class JIPCMutexStub extends JIPCPrimitiveStub implements JIPCMutex { 
    public void lock() throws JIPCException, IOException { 
        priorityLock(DEFAULT_RANK);
    }

    public void priorityLock(int rank) throws JIPCException, IOException { 
	JIPCRequest req = new JIPCRequest();
	req.objectId = id;
	req.opCode = JIPCRequest.LOCK_MUTEX;
        req.rank = rank;
	session.sendAndVerify(req);	
    }

    public boolean lock(long timeout) throws JIPCException, IOException { 
        return priorityLock(DEFAULT_RANK, timeout);
    }

    public boolean priorityLock(int rank, long timeout) throws JIPCException, IOException { 
	JIPCRequest req = new JIPCRequest();
	req.objectId = id;
	req.opCode = JIPCRequest.TIMED_LOCK_MUTEX;
        req.rank = rank;
	req.value = timeout;
	return session.sendAndCheckForOk(req);	
    }

    public void unlock() throws JIPCException, IOException { 
	JIPCRequest req = new JIPCRequest();
	req.objectId = id;
	req.opCode = JIPCRequest.UNLOCK_MUTEX;
	session.sendAndVerify(req);	
    }	

    JIPCMutexStub(JIPCSessionStub session, String name, JIPCResponse resp) { 
	super(session, name, resp);
    }
}




