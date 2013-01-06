package org.garret.jipc.client;

import org.garret.jipc.*;
import org.garret.jipc.protocol.*;
import java.io.*;
import java.net.Socket;

class JIPCSessionStub implements JIPCSession { 
    public JIPCEvent createEvent(String name, boolean signaled, boolean manualReset) throws JIPCException, IOException { 
	JIPCRequest req = new JIPCRequest();
	req.name = name;
	req.opCode = manualReset ? JIPCRequest.CREATE_EVENT : JIPCRequest.CREATE_AUTO_EVENT;
	req.value = signaled ? 1 : 0;
	return new JIPCEventStub(this, name, sendAndVerify(req));
    }

    public JIPCEvent openEvent(String name) throws JIPCException, IOException { 
	JIPCRequest req = new JIPCRequest();
	req.name = name;
	req.opCode = JIPCRequest.OPEN_EVENT;
	JIPCResponse resp = sendAndVerify(req);
	return resp.statusCode == JIPCResponse.OK ? new JIPCEventStub(this, name, resp) : null;
    }
	

    public JIPCSemaphore createSemaphore(String name, int initCount) throws JIPCException, IOException { 
	JIPCRequest req = new JIPCRequest();
	req.name = name;
	req.opCode = JIPCRequest.CREATE_SEMAPHORE;
	req.value = initCount;
	return new JIPCSemaphoreStub(this, name, sendAndVerify(req));
    }
	
    public JIPCSemaphore openSemaphore(String name) throws JIPCException, IOException {
	JIPCRequest req = new JIPCRequest();
	req.name = name;
	req.opCode = JIPCRequest.OPEN_SEMAPHORE;
	JIPCResponse resp = sendAndVerify(req);
	return resp.statusCode == JIPCResponse.OK ? new JIPCSemaphoreStub(this, name, resp) : null;
    }
   
    public JIPCMutex createMutex(String name, boolean locked) throws JIPCException, IOException { 
	JIPCRequest req = new JIPCRequest();
	req.name = name;
	req.opCode = JIPCRequest.CREATE_MUTEX;
	req.value = locked ? 1 : 0;
	return new JIPCMutexStub(this, name, sendAndVerify(req));
    }
	
	
    public JIPCMutex openMutex(String name) throws JIPCException, IOException {
	JIPCRequest req = new JIPCRequest();
	req.name = name;
	req.opCode = JIPCRequest.OPEN_MUTEX;
	JIPCResponse resp = sendAndVerify(req);
	return resp.statusCode == JIPCResponse.OK ? new JIPCMutexStub(this, name, resp) : null;
    }
   	
    
    public JIPCQueue createQueue(String name) throws JIPCException, IOException { 
	JIPCRequest req = new JIPCRequest();
	req.name = name;
	req.opCode = JIPCRequest.CREATE_QUEUE;
	return new JIPCQueueStub(this, name, sendAndVerify(req));
    }
	
    public JIPCQueue openQueue(String name) throws JIPCException, IOException { 
	JIPCRequest req = new JIPCRequest();
	req.name = name;
	req.opCode = JIPCRequest.OPEN_QUEUE;
	JIPCResponse resp = sendAndVerify(req);
	return resp.statusCode == JIPCResponse.OK ? new JIPCQueueStub(this, name, resp) : null;
    }

    public JIPCSharedMemory createSharedMemory(String name, Serializable obj) throws JIPCException, IOException { 
	if (obj == null) { 
	    throw new JIPCInvalidParameterException();
	}
	JIPCRequest req = new JIPCRequest();
	req.name = name;
	req.opCode = JIPCRequest.CREATE_SHMEM;
	req.data = obj;
	return new JIPCSharedMemoryStub(this, name, sendAndVerify(req));
    }
	
    public JIPCSharedMemory openSharedMemory(String name) throws JIPCException, IOException { 
	JIPCRequest req = new JIPCRequest();
	req.name = name;
	req.opCode = JIPCRequest.OPEN_SHMEM;
	JIPCResponse resp = sendAndVerify(req);
	return resp.statusCode == JIPCResponse.OK ? new JIPCSharedMemoryStub(this, name, resp) : null;
    }

    public JIPCLock createLock(String name) throws JIPCException, IOException { 
	JIPCRequest req = new JIPCRequest();
	req.name = name;
	req.opCode = JIPCRequest.CREATE_LOCK;
	return new JIPCLockStub(this, name, sendAndVerify(req));
    }
	
    public JIPCLock openLock(String name) throws JIPCException, IOException { 
	JIPCRequest req = new JIPCRequest();
	req.name = name;
	req.opCode = JIPCRequest.OPEN_LOCK;
	JIPCResponse resp = sendAndVerify(req);
	return resp.statusCode == JIPCResponse.OK ? new JIPCLockStub(this, name, resp) : null;
    }

    public JIPCBarrier createBarrier(String name, int nSessions) throws JIPCException, IOException { 
	if (nSessions <= 0) { 
	    throw new JIPCInvalidParameterException();
	}
	JIPCRequest req = new JIPCRequest();
	req.name = name;
	req.opCode = JIPCRequest.CREATE_BARRIER;
	req.value = nSessions;
	return new JIPCBarrierStub(this, name, sendAndVerify(req));
    }
	
    public JIPCBarrier openBarrier(String name) throws JIPCException, IOException { 
	JIPCRequest req = new JIPCRequest();
	req.name = name;
	req.opCode = JIPCRequest.OPEN_BARRIER;
	JIPCResponse resp = sendAndVerify(req);
	return resp.statusCode == JIPCResponse.OK ? new JIPCBarrierStub(this, name, resp) : null;
    }

    public synchronized void close() throws JIPCException, IOException { 
	JIPCRequest req = new JIPCRequest();
	req.opCode = JIPCRequest.CLOSE_SESSION;
	send(req);
	socket.close();
	socket = null;
	in = null;
	out = null;
    }
   	
    public void shutdownServer() throws IOException
    {
	JIPCRequest req = new JIPCRequest();
	req.opCode = JIPCRequest.SHUTDOWN;
	send(req);	    	
    }

    public void showServerInfo(PrintStream out) throws JIPCException, IOException
    {
	JIPCRequest req = new JIPCRequest();
	req.opCode = JIPCRequest.SHOW_INFO;
	JIPCResponse resp = sendAndVerify(req);
	out.print(resp.data);
    }

    public boolean isLocal() { 
	return false;
    }

    synchronized void send(JIPCRequest req) throws IOException {
	out.reset();
	// out.writeUnshared(req); // supported since JDK 1.4
	out.writeObject(req);
    }

    synchronized JIPCResponse sendAndReceive(JIPCRequest req) throws JIPCException, IOException {
	out.reset();
	// out.writeUnshared(req); // supported since JDK 1.4
	out.writeObject(req);
	try { 
	    // return (JIPCResponse)in.readUnshared(); // supported since JDK 1.4
	    return (JIPCResponse)in.readObject();
	} catch (ClassNotFoundException x) { 
	    throw new JIPCClassNotFoundException(x.getMessage());
	}	    
    }
   
    JIPCResponse sendAndVerify(JIPCRequest req) throws JIPCException, IOException {
	JIPCResponse resp = sendAndReceive(req);
	switch (resp.statusCode) { 
	  case JIPCResponse.OK:
	  case JIPCResponse.ALREADY_EXISTS:
	  case JIPCResponse.TIMEOUT_EXPIRED:
	    return resp;
 	  case JIPCResponse.NOT_FOUND:
	    throw new JIPCNotFoundException(req.name);  
 	  case JIPCResponse.NOT_OWNER:
	    throw new JIPCNotOwnerException();  
 	  case JIPCResponse.INVALID_PARAMETER:
	    throw new JIPCInvalidParameterException();
 	  case JIPCResponse.INTERRUPTED:
	    throw new JIPCInterruptedException();
 	  case JIPCResponse.DEADLOCK:
	    throw new JIPCDeadlockException();
  	  default:
	    throw new JIPCInternalException();
	}
    }
   
    boolean sendAndCheckForOk(JIPCRequest req) throws JIPCException, IOException {
	JIPCResponse resp = sendAndVerify(req);
	return resp.statusCode == JIPCResponse.OK;
    }

    JIPCSessionStub(String address, int port) throws JIPCException, IOException { 
	socket = new Socket(address, port);
	try { 
	    socket.setTcpNoDelay(true);
	} catch (NoSuchMethodError er) {}
	out = new ObjectOutputStream(socket.getOutputStream());
	JIPCRequest req = new JIPCRequest();
	req.opCode = JIPCRequest.LOGIN;
	req.name = System.getProperty("user.name");
	send(req);
	in = new ObjectInputStream(socket.getInputStream());
	try { 
	    JIPCResponse resp = (JIPCResponse)in.readObject();
	    if (resp.statusCode != JIPCResponse.OK) { 
		throw new JIPCLoginRefusedException();
	    }
	} catch (ClassNotFoundException x) {
	    throw new JIPCClassNotFoundException(x.getMessage());
	}
	    
    }

    ObjectOutputStream out;
    ObjectInputStream  in;
    Socket             socket;
}
