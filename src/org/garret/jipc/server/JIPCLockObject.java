package org.garret.jipc.server;

import org.garret.jipc.*;

class JIPCLockObject { 
    JIPCLockObject    nextLock;
    JIPCLockObject    nextOwner;
    int               flags;
    JIPCPrimitiveImpl prim;
    JIPCSessionImpl   owner;

    void unlock() throws JIPCException { 
	prim.unlock(this);
    }

    JIPCLockObject(JIPCPrimitiveImpl prim, JIPCSessionImpl session, int flags) { 
	this.prim = prim;
	this.owner = session;
	this.flags = flags;
    }
};
