package org.garret.jipc.client;

import org.garret.jipc.*;
import org.garret.jipc.protocol.*;
import java.io.IOException;

abstract class JIPCPrimitiveStub implements JIPCPrimitive { 
    public boolean alreadyExists() { 
	return exists;
    }
    
    public boolean waitFor(long timeout) throws JIPCException, IOException
    {
        return priorityWait(DEFAULT_RANK, timeout);
    }

    public void waitFor() throws JIPCException, IOException
    {
        priorityWait(DEFAULT_RANK);
    } 
 
    public void priorityWait(int rank) throws JIPCException, IOException { 
	JIPCRequest req = new JIPCRequest();
	req.objectId = id;
	req.opCode = JIPCRequest.WAIT;
        req.rank = rank;
	req.sequenceNo = sequenceNo;
	JIPCResponse resp = session.sendAndVerify(req);	
    }

    public boolean priorityWait(int rank, long timeout) throws JIPCException, IOException { 
	JIPCRequest req = new JIPCRequest();
	req.objectId = id;
	req.opCode = JIPCRequest.TIMED_WAIT;
        req.rank = rank;
	req.value = timeout;
	req.sequenceNo = sequenceNo;
	JIPCResponse resp = session.sendAndVerify(req);	
	return resp.statusCode == JIPCResponse.OK;
    }


    public void reset() throws JIPCException, IOException { 
	JIPCRequest req = new JIPCRequest();
	req.objectId = id;
	req.opCode = JIPCRequest.RESET;
	session.sendAndVerify(req);	
    }

    public void close() throws JIPCException, IOException { 
	JIPCRequest req = new JIPCRequest();
	req.objectId = id;
	req.opCode = JIPCRequest.CLOSE_PRIMITIVE;
	session.sendAndVerify(req);	
    }

    public String getName() {
	return name;
    }

    JIPCPrimitiveStub(JIPCSessionStub session, String name, JIPCResponse resp) { 
	exists = resp.statusCode == JIPCResponse.ALREADY_EXISTS;
	this.name = name;
	this.session = session;
	this.id = resp.objectId;
    }	
    
    boolean         exists;
    JIPCSessionStub session;
    String          name;
    int             id;
    long            sequenceNo;
}    
