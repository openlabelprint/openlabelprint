package org.garret.jipc.client;

import org.garret.jipc.*;
import org.garret.jipc.protocol.*;
 
class JIPCBarrierStub extends JIPCPrimitiveStub implements JIPCBarrier { 
    JIPCBarrierStub(JIPCSessionStub session, String name, JIPCResponse resp) { 
	super(session, name, resp);
    }
}



