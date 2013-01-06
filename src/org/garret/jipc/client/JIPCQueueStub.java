package org.garret.jipc.client;

import org.garret.jipc.*;
import org.garret.jipc.protocol.*;
import java.io.IOException;
import java.io.Serializable;

class JIPCQueueStub extends JIPCPrimitiveStub implements JIPCQueue { 
    public Serializable get() throws JIPCException, IOException {
        return priorityGet(DEFAULT_RANK);
    }

    public Serializable priorityGet(int rank) throws JIPCException, IOException {
	JIPCRequest req = new JIPCRequest();
	req.objectId = id;
	req.opCode = JIPCRequest.DEQUEUE;
        req.rank = rank;
	JIPCResponse resp = session.sendAndVerify(req);
	return resp.data;
    }

    public Serializable get(long timeout) throws JIPCException, IOException {
        return priorityGet(DEFAULT_RANK, timeout);
    }

    public Serializable priorityGet(int rank, long timeout) throws JIPCException , IOException{
	JIPCRequest req = new JIPCRequest();
	req.objectId = id;
	req.opCode = JIPCRequest.TIMED_DEQUEUE;
        req.rank = rank;
	req.value = timeout;
	JIPCResponse resp = session.sendAndVerify(req);
	return resp.data;
    }

    public void put(Serializable obj) throws JIPCException, IOException { 
	if (obj == null) { 
	    throw new JIPCInvalidParameterException();
	}
	JIPCRequest req = new JIPCRequest();
	req.objectId = id;
	req.data = obj;
	req.opCode = JIPCRequest.ENQUEUE;
	session.sendAndVerify(req);
    }

    public void broadcast(Serializable obj) throws JIPCException, IOException { 
	if (obj == null) { 
	    throw new JIPCInvalidParameterException();
	}
	JIPCRequest req = new JIPCRequest();
	req.objectId = id;
	req.data = obj;
	req.opCode = JIPCRequest.BROADCAST;
	session.sendAndVerify(req);
    }
    
    public int size() throws JIPCException, IOException { 
	JIPCRequest req = new JIPCRequest();
	req.objectId = id;
	req.opCode = JIPCRequest.QUEUE_SIZE;
        JIPCResponse resp = session.sendAndVerify(req);
        return ((Integer)resp.data).intValue();
    }

    JIPCQueueStub(JIPCSessionStub session, String name, JIPCResponse resp) { 
	super(session, name, resp);
    }
}
    



