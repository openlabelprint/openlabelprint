package org.garret.jipc.server;

import org.garret.jipc.*;
import org.garret.jipc.protocol.*;
import java.io.*;
import java.util.*;
import java.net.Socket;

class JIPCSessionImpl extends Thread implements JIPCSession { 
    public JIPCEvent createEvent(String name, boolean signaled, boolean manualReset) throws JIPCException { 
	return server.createEvent(server.getSession(), name, signaled, manualReset);
    }
    public JIPCEvent openEvent(String name) throws JIPCException {
	return server.openEvent(server.getSession(), name);
    }

    public JIPCSemaphore createSemaphore(String name, int initCount) throws JIPCException {
	if (initCount < 0) { 
	    throw new JIPCInvalidParameterException();
	}
	return server.createSemaphore(server.getSession(), name, initCount);
    }	
    public JIPCSemaphore openSemaphore(String name) throws JIPCException {
	return server.openSemaphore(server.getSession(), name);
    }

    public JIPCMutex createMutex(String name, boolean locked) throws JIPCException {
	return server.createMutex(server.getSession(), name, locked);
    }	
    public JIPCMutex openMutex(String name) throws JIPCException {
	return server.openMutex(server.getSession(), name);
    }
  
    public JIPCQueue createQueue(String name) throws JIPCException {
	return server.createQueue(server.getSession(), name);
    }	
    public JIPCQueue openQueue(String name) throws JIPCException {
	return server.openQueue(server.getSession(), name);
    }	

    public JIPCSharedMemory createSharedMemory(String name, Serializable obj) throws JIPCException {
	return server.createSharedMemory(server.getSession(), name, obj);
    }	
    public JIPCSharedMemory openSharedMemory(String name) throws JIPCException {
	return server.openSharedMemory(server.getSession(), name);
    }	
    
    public JIPCLock createLock(String name) throws JIPCException {
	return server.createLock(server.getSession(), name);
    }	
    public JIPCLock openLock(String name) throws JIPCException {
	return server.openLock(server.getSession(), name);
    }	

    public JIPCBarrier createBarrier(String name, int nSessions) throws JIPCException {
	return server.createBarrier(server.getSession(), name, nSessions);
    }	
    public JIPCBarrier openBarrier(String name) throws JIPCException {
	return server.openBarrier(server.getSession(), name);
    }	

    public boolean isLocal() { 
	return true;
    }


    public void run() { 
	try { 
	    while (running) { 
		// JIPCRequest req = (JIPCRequest)in.readUnshared(); // supported only since JDK 1.4
		JIPCRequest req = (JIPCRequest)in.readObject();
		sequenceNo = req.sequenceNo;
		JIPCResponse resp = server.handleRequest(this, req);
		if (resp == null) { 
		    break;
		}
		resp.sequenceNo = sequenceNo;
		out.reset();
		// out.writeUnshared(resp); // supported only since JDK 1.4
		out.writeObject(resp);
	    }
	} catch (Exception x) {	    
	    x.printStackTrace();
	}
	try { 
	    close();
	} catch(Exception x) {}
    }

    public synchronized void close() throws JIPCException, IOException { 
	while (lockList != null) { 
	    lockList.unlock();
	    lockList = lockList.nextLock;
	}
	Iterator iter = primitives.iterator();
	while (iter.hasNext()) { 
	    ((JIPCPrimitiveImpl)iter.next()).endAccess(this);
	}	  
	running = false;
	if (socket != null) { 
	    socket.close();
	}
	server.stopSession(this);
	primitives = null;
    }

    protected void beginAccess(JIPCPrimitiveImpl prim) throws JIPCException { 
	prim.beginAccess(this);
	primitives.add(prim);
	
    }
    
    protected void endAccess(JIPCPrimitiveImpl prim) throws JIPCException, IOException { 
	prim.endAccess(this);
        primitives.remove(prim);
    }

    protected synchronized JIPCLockObject addLock(JIPCPrimitiveImpl prim, int flags) {
	JIPCLockObject lob = new JIPCLockObject(prim, this, flags);
	lob.nextLock = lockList;
	lockList = lob;
	return lob;
    }

    protected synchronized void removeLock(JIPCLockObject lock) { 
	for (JIPCLockObject lob = lockList, prev = null; 
	     lob != null; 
	     prev = lob, lob = lob.nextLock) 
	{ 
	    if (lob == lock) { 
		if (prev == null) { 
		    lockList = lob.nextLock;
		} else {
		    prev.nextLock = lob.nextLock;
		}
		break;
	    }
	}
    }

    protected JIPCSessionImpl(JIPCServer server) { 
	this.server = server;
    }

    protected JIPCSessionImpl(JIPCServer server, Socket socket) throws IOException { 
	this.server = server;
	this.socket = socket;
	in = new ObjectInputStream(socket.getInputStream());
	out = new ObjectOutputStream(socket.getOutputStream());
	running = true;
	setDaemon(true);
    }

    public String toString() { 
	if (socket != null) { // && !socket.isAnyLocalAddress()) { 
	    String address = socket.getInetAddress().toString();
	    if (address.equals("/127.0.0.1")) {
		address = "";
	    }
	    return address + "[" + hashCode() + "]";
	} else { 
	    return "[" + hashCode() + "]";
	}
    }

    public synchronized void dump(PrintStream out) throws JIPCException { 
	out.println(toString());
	
	if (waitFor != null) { 
	    out.println("  Waiting for: " + waitFor.prim);
	}
	if (lockList != null) { 
	    out.println("  Locking:");
	    for (JIPCLockObject lob = lockList; lob != null; lob = lob.nextLock) { 
		out.println("    " + lob.prim);
	    }
	}
	out.println("  Opened Primitives:");
	Iterator iter = primitives.iterator();
	while (iter.hasNext()) { 
	    out.println("    " + iter.next());
	}
    }

    public void shutdownServer() {}
   
    public void showServerInfo(PrintStream stream) throws JIPCException, IOException { 
	server.dump(stream);
    }


    JIPCServer         server;
    Socket             socket;
    ObjectInputStream  in;
    ObjectOutputStream out;    
    JIPCWaitObject     waitFor;
    JIPCLockObject     lockList;
    boolean            running;
    HashSet            primitives = new HashSet();
    long               sequenceNo;
    boolean            exists;
}


