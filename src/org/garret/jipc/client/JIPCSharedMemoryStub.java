package org.garret.jipc.client;

import org.garret.jipc.*;
import org.garret.jipc.protocol.*;
import java.io.IOException;
import java.io.Serializable;

class JIPCSharedMemoryStub extends JIPCPrimitiveStub implements JIPCSharedMemory { 
    public void set(Serializable obj) throws JIPCException, IOException {
	if (obj == null) { 
	    throw new JIPCInvalidParameterException();
	}
	JIPCRequest req = new JIPCRequest();
	req.opCode = JIPCRequest.SET_OBJECT;
	req.objectId = id;
	req.data = obj;
	JIPCResponse resp = session.sendAndVerify(req);
	sequenceNo = resp.sequenceNo;
    }
	
    public Serializable get() throws JIPCException, IOException {
	JIPCRequest req = new JIPCRequest();
	req.opCode = JIPCRequest.GET_OBJECT;
	req.objectId = id;
	JIPCResponse resp = session.sendAndVerify(req);
	sequenceNo = resp.sequenceNo;
	return resp.data;
    }

    JIPCSharedMemoryStub(JIPCSessionStub session, String name, JIPCResponse resp) { 
	super(session, name, resp);
    }
};


