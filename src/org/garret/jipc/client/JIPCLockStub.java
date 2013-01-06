package org.garret.jipc.client;

import org.garret.jipc.*;
import org.garret.jipc.protocol.*;
import java.io.IOException;

class JIPCLockStub extends JIPCPrimitiveStub implements JIPCLock 
{ 
    public void exclusiveLock() throws JIPCException, IOException { 
        priorityExclusiveLock(DEFAULT_RANK);
    }

    public void priorityExclusiveLock(int rank) throws JIPCException, IOException { 
	JIPCRequest req = new JIPCRequest();
	req.opCode = JIPCRequest.EXCLUSIVE_LOCK;
        req.rank = rank;
	req.objectId = id;
	session.sendAndVerify(req);	
    }
	
    public boolean exclusiveLock(long timeout) throws JIPCException, IOException { 
        return priorityExclusiveLock(DEFAULT_RANK, timeout);
    }

    public boolean priorityExclusiveLock(int rank, long timeout) throws JIPCException, IOException {
	JIPCRequest req = new JIPCRequest();
	req.opCode = JIPCRequest.TIMED_EXCLUSIVE_LOCK;
	req.objectId = id;
        req.rank = rank;
	req.value = timeout;
	return session.sendAndCheckForOk(req);	
    }
	
    public void sharedLock() throws JIPCException, IOException { 
        prioritySharedLock(DEFAULT_RANK);
    }

    public void prioritySharedLock(int rank) throws JIPCException, IOException { 
	JIPCRequest req = new JIPCRequest();
	req.opCode = JIPCRequest.SHARED_LOCK;
	req.objectId = id;
        req.rank = rank;
	session.sendAndVerify(req);	
    }
	
    public boolean sharedLock(long timeout) throws JIPCException, IOException { 
        return prioritySharedLock(DEFAULT_RANK, timeout);
    }

    public boolean prioritySharedLock(int rank, long timeout) throws JIPCException, IOException { 
	JIPCRequest req = new JIPCRequest();
	req.opCode = JIPCRequest.TIMED_SHARED_LOCK;
	req.objectId = id;
        req.rank = rank;
	req.value = timeout;
	return session.sendAndCheckForOk(req);	
    }
	
    public void    unlock() throws JIPCException, IOException { 
	JIPCRequest req = new JIPCRequest();
	req.opCode = JIPCRequest.UNLOCK;
	req.objectId = id;
	session.sendAndVerify(req);	
    }	

    JIPCLockStub(JIPCSessionStub session, String name, JIPCResponse resp) { 
	super(session, name, resp);
    }
}
