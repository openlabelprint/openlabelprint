package org.garret.jipc.client;

import org.garret.jipc.*;
import org.garret.jipc.protocol.*;
import java.io.IOException;

class JIPCEventStub extends JIPCPrimitiveStub implements JIPCEvent { 
    public void signal() throws JIPCException, IOException
    {
	JIPCRequest req = new JIPCRequest();
	req.objectId = id;
	req.opCode = JIPCRequest.SIGNAL_EVENT;
	session.sendAndVerify(req);	
    }

    public void pulse() throws JIPCException, IOException
    {
	JIPCRequest req = new JIPCRequest();
	req.objectId = id;
	req.opCode = JIPCRequest.PULSE;
	session.sendAndVerify(req);	
    }

    JIPCEventStub(JIPCSessionStub session, String name, JIPCResponse resp) { 
	super(session, name, resp);
    }
}



