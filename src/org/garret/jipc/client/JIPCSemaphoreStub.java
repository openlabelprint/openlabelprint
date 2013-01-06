package org.garret.jipc.client;

import org.garret.jipc.*;
import org.garret.jipc.protocol.*;
import java.io.IOException;

class JIPCSemaphoreStub extends JIPCPrimitiveStub implements JIPCSemaphore { 
    public void signal() throws JIPCException, IOException { 
	signal(1);
    }

    public void signal(int count) throws JIPCException, IOException { 
	if (count <= 0) {
	    throw new JIPCInvalidParameterException();
	}
	JIPCRequest req = new JIPCRequest();
	req.objectId = id;
	req.opCode = JIPCRequest.SIGNAL_SEMAPHORE;
	req.value = count;
	session.sendAndVerify(req);	
    }

    JIPCSemaphoreStub(JIPCSessionStub session, String name, JIPCResponse resp) { 
	super(session, name, resp);
    }
}



